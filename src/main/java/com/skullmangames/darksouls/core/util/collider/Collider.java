package com.skullmangames.darksouls.core.util.collider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.client.renderer.entity.model.Armature;
import com.skullmangames.darksouls.common.animation.Animator;
import com.skullmangames.darksouls.common.capability.entity.EntityCapability;
import com.skullmangames.darksouls.common.capability.entity.EntityState;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.init.data.Colliders;
import com.skullmangames.darksouls.core.util.JsonBuilder;
import com.skullmangames.darksouls.core.util.math.ModMath;
import com.skullmangames.darksouls.core.util.math.vector.ModMatrix4f;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class Collider
{
	private final ResourceLocation id;
	private Vec3 worldCenter = Vec3.ZERO;
	protected Vec3[] vertices;
	protected Vec3[] modelVertices;
	
	/**
	 * Test hitbox.
	 * Bigger than collider hitbox.
	 * Uses mass center instead of world center.
	 **/
	protected final AABB outerAABB;

	public Collider(ResourceLocation id, AABB outerAABB)
	{
		this.id = id;
		this.outerAABB = outerAABB;
	}
	
	@Nullable
	public ResourceLocation getId()
	{
		return this.id;
	}
	
	public Vec3 getWorldCenter()
	{
		return this.worldCenter;
	}
	
	public void transform(ModMatrix4f mat)
	{
		Vec3 pos = mat.transform(Vec3.ZERO);
		this.moveTo(new Vec3(-pos.x, pos.y, -pos.z));
	}
	
	protected void moveTo(Vec3 pos)
	{
		this.worldCenter = pos;
		for (int i = 0; i < this.vertices.length; i++)
		{
			this.vertices[i] = this.vertices[i].add(this.worldCenter);
		}
	}
	
	public BlockHitResult getBlockCollisions(BlockGetter level)
	{
		AABB aabb = this.getHitboxAABB();
		BlockHitResult hitResult = level.clip(new ClipContext(new Vec3(aabb.minX, aabb.minY, aabb.minZ), new Vec3(aabb.maxX, aabb.maxY, aabb.maxZ),
				ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
		return hitResult;
	}
	
	public List<Entity> getShieldCollisions(Entity self)
	{
		return this.getShieldCollisions(self, (entity) -> false);
	}
	
	protected List<Entity> getShieldCollisions(Entity self, Predicate<Entity> additionalFilters)
	{
		List<Entity> collisions = self.level.getEntities(self, this.getHitboxAABB().inflate(5));
		collisions.removeIf(additionalFilters.or((entity) ->
		{
			if (entity instanceof LivingEntity)
			{
				LivingCap<?> cap = (LivingCap<?>)entity.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
				if (cap != null && cap.isBlocking())
				{
					ModMatrix4f modelMat = cap.getModelMatrix(1.0F).rotateDeg(90, Vector3f.YP);
					ModMatrix4f mat = modelMat.translate(0.4F, entity.getBbHeight() / 2, 0);
					Collider shieldCollider = Colliders.SHIELD.get();
					shieldCollider.transform(mat);
					return !this.collidesWith(shieldCollider);
				}
			}
			return true;
		}));
		return collisions;
	}

	public List<Entity> getEntityCollisions(Entity self)
	{
		return this.getEntityCollisions(self, new ArrayList<>());
	}
	
	public List<Entity> getEntityCollisions(Entity self, List<Entity> blacklist)
	{
		return this.getEntityCollisions(self, (entity) -> blacklist.contains(entity));
	}
	
	protected List<Entity> getEntityCollisions(Entity self, Predicate<Entity> additionalFilters)
	{
		List<Entity> collisions = self.level.getEntities(self, this.getHitboxAABB());
		collisions.removeIf(additionalFilters.or((entity) -> !this.collidesWith(entity)));
		return collisions;
	}
	
	protected abstract Vec3 min();
	
	protected abstract Vec3 max();
	
	public abstract Vec3 top();
	
	public abstract Vec3 bottom();
	
	public abstract boolean collidesWith(Collider other);
	
	public abstract Vec3 collide(Vec3 movement, List<ColliderHolder> others);

	protected abstract boolean collidesWith(Entity opponent);

	public AABB getHitboxAABB()
	{
		return this.outerAABB.move(this.getMassCenter());
	}
	
	public abstract Vec3 getMassCenter();
	
	public ModMatrix4f update(EntityCapability<?> entityCap, String jointName, float partialTicks)
	{
		ModMatrix4f transformMatrix;
		
		if (entityCap instanceof LivingCap<?> livingCap)
		{
			Armature armature = livingCap.getEntityModel(Models.SERVER).getArmature();
			int pathIndex = armature.searchPathIndex(jointName);

			if (pathIndex == -1) transformMatrix = new ModMatrix4f();
			else transformMatrix = Animator.getParentboundTransform(livingCap.getAnimator().getPose(partialTicks), armature, pathIndex);
			
			float scale = livingCap.getModelScale();
			transformMatrix.mulFront(entityCap.getModelMatrix(partialTicks).scale(scale, scale, scale));
		}
		else transformMatrix = entityCap.getModelMatrix(partialTicks);
		
		this.transform(transformMatrix);
		
		return transformMatrix;
	}

	@OnlyIn(Dist.CLIENT)
	public void draw(LivingCap<?> entityCap, String jointName, float partialTicks)
	{
		boolean red = entityCap.getEntityState() == EntityState.CONTACT;
		this.update(entityCap, jointName, partialTicks);

		this.drawInternal(red);
	}
	
	@OnlyIn(Dist.CLIENT)
	public abstract void drawInternal(boolean red);
	
	@Override
	public String toString()
	{
		return this.id.toString();
	}
	
	public static Builder capsuleBuilder(ResourceLocation id, double radius, double height, Vec3 base, float xRot, float yRot)
	{
		return new CapsuleCollider.Builder(id, radius, height, base, xRot, yRot);
	}
	
	public static Builder capsuleBuilder(ResourceLocation id, double radius, double height, Vec3 base)
	{
		return capsuleBuilder(id, radius, height, base, 0F, 0F);
	}
	
	public static Builder cubeBuilder(ResourceLocation id, double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
	{
		return cubeBuilder(id, minX, minY, minZ, maxX, maxY, maxZ, 0F, 0F);
	}
	
	public static Builder cubeBuilder(ResourceLocation id, double minX, double minY, double minZ, double maxX, double maxY, double maxZ,
			float xRot, float yRot)
	{
		return new CubeCollider.Builder(id, minX, minY, minZ, maxX, maxY, maxZ, xRot, yRot);
	}
	
	public static CoreBuilder multiBuilder(ResourceLocation id, Builder... builders)
	{
		return new CoreBuilder(id, builders);
	}
	
	public static enum ColliderType
	{
		CUBE, CAPSULE
	}
	
	public static class CoreBuilder implements JsonBuilder<Collider>
	{
		private final ResourceLocation id;
		private final List<Builder> colliders;
		
		private CoreBuilder(ResourceLocation id, Builder... builders)
		{
			this.id = id;
			this.colliders = Arrays.asList(builders);
		}
		
		private CoreBuilder(ResourceLocation location, JsonObject json)
		{
			this.id = location;
			this.colliders = new ArrayList<>();
			
			JsonElement arrayElement = json.get("multiple");
			Iterable<JsonElement> array = arrayElement == null ? Arrays.asList(json) : arrayElement.getAsJsonArray();
			
			for (JsonElement e : array)
			{
				JsonObject o = e.getAsJsonObject();
				ColliderType type = ColliderType.valueOf(o.get("type").getAsString());
				
				switch (type)
				{
					case CUBE:
						this.colliders.add(new CubeCollider.Builder(location, o));
						break;
						
					case CAPSULE:
						this.colliders.add(new CapsuleCollider.Builder(location, o));
						break;
				}
			}
		}
		
		@Override
		public ResourceLocation getId()
		{
			return this.id;
		}
		
		public static CoreBuilder fromJson(ResourceLocation location, JsonObject json)
		{
			return new CoreBuilder(location, json);
		}
		
		@Override
		public JsonObject toJson()
		{
			JsonObject json = new JsonObject();
			
			JsonArray array = new JsonArray();
			json.add("multiple", array);
			
			for (Builder collider : this.colliders)
			{
				array.add(collider.toJson());
			}
			
			return json;
		}
		
		@Override
		public Collider build()
		{
			if (this.colliders.size() > 1)
			{
				Collider[] array = new Collider[this.colliders.size()];
				for (int i = 0; i < array.length; i++)
				{
					array[i] = this.colliders.get(i).build();
				}
				
				return new MultiCollider(this.getId(), array);
			}
			else
			{
				return this.colliders.get(0).build();
			}
		}
	}
	
	public static abstract class Builder implements JsonBuilder<Collider>
	{
		private ResourceLocation id;
		
		protected Builder(ResourceLocation id)
		{
			this.id = id;
		}
		
		protected Builder(ResourceLocation location, JsonObject json)
		{
			this.id = location;
		}
		
		protected abstract ColliderType getType();
		
		@Override
		public JsonObject toJson()
		{
			JsonObject json = new JsonObject();
			json.addProperty("type", this.getType().name());
			return json;
		}
		
		@Override
		public ResourceLocation getId()
		{
			return this.id;
		}
	}
	
	protected static Vec3 capsuleCubeCollision(CapsuleCollider a, CubeCollider b)
	{
		// Compute capsule line endpoints A, B:
		Vec3 CapsuleNormal = a.max().subtract(a.min()).normalize();
		Vec3 LineEndOffset = CapsuleNormal.scale(a.radius);
		Vec3 A = a.min().add(LineEndOffset);
		Vec3 B = a.max().subtract(LineEndOffset);
		boolean insideCube = true;
		
		
		Vec3 pushOutVec = Vec3.ZERO;
		for (Face face : b.faces)
		{
			Vec3 p0 = face.vertex(0);
			Vec3 p1 = face.vertex(1);
			Vec3 p2 = face.vertex(2);
			Vec3 p3 = face.vertex(3);
			Vec3 N = face.normal; // plane normal
			
			// Then for each face, ray-plane intersection:
			double t = N.dot(p0.subtract(a.min())) / Math.abs(N.dot(CapsuleNormal));
			Vec3 linePlaneIntersection = a.min().add(CapsuleNormal.scale(t));
			
			Vec3 referencePoint = null;
			// Determine whether linePlaneIntersection is inside all face edges:
			Vec3 c0 = linePlaneIntersection.subtract(p0).cross(p1.subtract(p0));
			Vec3 c1 = linePlaneIntersection.subtract(p1).cross(p2.subtract(p1));
			Vec3 c2 = linePlaneIntersection.subtract(p2).cross(p3.subtract(p2));
			Vec3 c3 = linePlaneIntersection.subtract(p3).cross(p0.subtract(p3));
			boolean inside = c0.dot(N) <= 0 && c1.dot(N) <= 0 && c2.dot(N) <= 0 && c3.dot(N) <= 0;

			if (inside)
			{
				referencePoint = linePlaneIntersection;
			}
			else
			{
				// Edge 1:
				Vec3 point1 = ClosestPointOnLineSegment(p0, p1, linePlaneIntersection);
				double distsq = linePlaneIntersection.subtract(point1).lengthSqr();
				double best_dist = distsq;
				referencePoint = point1;

				// Edge 2:
				Vec3 point2 = ClosestPointOnLineSegment(p1, p2, linePlaneIntersection);
				distsq = linePlaneIntersection.subtract(point2).lengthSqr();
				if (distsq < best_dist)
				{
					referencePoint = point2;
					best_dist = distsq;
				}

				// Edge 3:
				Vec3 point3 = ClosestPointOnLineSegment(p2, p3, linePlaneIntersection);
				distsq = linePlaneIntersection.subtract(point3).lengthSqr();
				if (distsq < best_dist)
				{
					referencePoint = point3;
					best_dist = distsq;
				}
				
				// Edge 4:
				Vec3 point4 = ClosestPointOnLineSegment(p3, p0, linePlaneIntersection);
				distsq = linePlaneIntersection.subtract(point4).lengthSqr();
				if (distsq < best_dist)
				{
					referencePoint = point4;
					best_dist = distsq;
				}
			}

			// The center of the best sphere candidate:
			Vec3 center = ClosestPointOnLineSegment(A, B, referencePoint);
			pushOutVec = pushOutVec.add(sphereFaceCollision(a.radius, center, face));
			
			
			// Check if the capsule is completely inside the cube
			double faceCenterToBaseDist = a.min().subtract(face.center()).dot(N);
			double faceCenterToTipDist = a.max().subtract(face.center()).dot(N);
			insideCube |= faceCenterToBaseDist < 0 && faceCenterToTipDist < 0;
		}
		
		if (insideCube)
		{
			double bestDist = Double.MAX_VALUE;
			Vec3 bestNormal = Vec3.ZERO;
			for (Face face : b.faces)
			{
				double dist = a.getMassCenter().subtract(face.center()).dot(face.normal);
				if (bestDist > dist)
				{
					bestDist = dist;
					bestNormal = face.normal;
				}
			}
			pushOutVec.add(bestNormal.scale(0.1D));
		}
		
		return pushOutVec;
	}
	
	protected static Vec3 sphereFaceCollision(double radius, Vec3 center, Face face)
	{
		Vec3 p0 = face.vertex(0);
		Vec3 p1 = face.vertex(1);
		Vec3 p2 = face.vertex(2);
		Vec3 p3 = face.vertex(3);
		Vec3 N = face.normal; // plane normal
		double dist = center.subtract(p0).dot(N); // signed distance between sphere and plane
		
		if (dist > Math.abs(radius)) return Vec3.ZERO;

		Vec3 point0 = center.subtract(N.scale(dist)); // projected sphere center on face plane

		// Now determine whether point0 is inside all face edges:
		Vec3 c0 = point0.subtract(p0).cross(p1.subtract(p0));
		Vec3 c1 = point0.subtract(p1).cross(p2.subtract(p1));
		Vec3 c2 = point0.subtract(p2).cross(p3.subtract(p2));
		Vec3 c3 = point0.subtract(p3).cross(p0.subtract(p3));
		boolean inside = c0.dot(N) <= 0 && c1.dot(N) <= 0 && c2.dot(N) <= 0 && c3.dot(N) <= 0;

		double radiussq = radius * radius; // sphere radius squared

		// Edge 1:
		Vec3 point1 = ClosestPointOnLineSegment(p0, p1, center);
		double distsq1 = center.subtract(point1).lengthSqr();
		boolean intersects = distsq1 < radiussq;

		// Edge 2:
		Vec3 point2 = ClosestPointOnLineSegment(p1, p2, center);
		double distsq2 = center.subtract(point2).lengthSqr();
		intersects |= distsq2 < radiussq;

		// Edge 3:
		Vec3 point3 = ClosestPointOnLineSegment(p2, p3, center);
		double distsq3 = center.subtract(point3).lengthSqr();
		intersects |= distsq3 < radiussq;
		
		// Edge 4:
		Vec3 point4 = ClosestPointOnLineSegment(p3, p0, center);
		double distsq4 = center.subtract(point4).lengthSqr();
		intersects |= distsq4 < radiussq;
		
		if (!inside && !intersects) return Vec3.ZERO;

		Vec3 intersectionVec;

		if (inside)
		{
			intersectionVec = center.subtract(point0);
		}
		else
		{
			Vec3 d = center.subtract(point1);
			double best_distsq = d.dot(d);
			intersectionVec = d;

			d = center.subtract(point2);
			double distsq = d.dot(d);
			if (distsq < best_distsq)
			{
				distsq = best_distsq;
				intersectionVec = d;
			}

			d = center.subtract(point3);
			distsq = d.dot(d);
			if (distsq < best_distsq)
			{
				distsq = best_distsq;
				intersectionVec = d;
			}
			
			d = center.subtract(point4);
			distsq = d.dot(d);
			if (distsq < best_distsq)
			{
				distsq = best_distsq;
				intersectionVec = d;
			}
		}

		Vec3 penetrationNormal = intersectionVec.normalize(); // normalize
		double penetrationDepth = radius - intersectionVec.length(); // radius = sphere radius
		return penetrationNormal.scale(-penetrationDepth);
	}
	
	protected static Vec3 capsuleCapsuleCollision(CapsuleCollider a, CapsuleCollider b)
	{
		// capsule A:
		Vec3 a_Normal = a.max().subtract(a.min()).normalize();
		Vec3 a_LineEndOffset = a_Normal.scale(a.radius);
		Vec3 a_A = a.min().add(a_LineEndOffset);
		Vec3 a_B = a.max().subtract(a_LineEndOffset);

		// capsule B:
		Vec3 b_Normal = b.max().subtract(b.min()).normalize();
		Vec3 b_LineEndOffset = b_Normal.scale(b.radius);
		Vec3 b_A = b.min().add(b_LineEndOffset);
		Vec3 b_B = b.max().subtract(b_LineEndOffset);

		// vectors between line endpoints:
		Vec3 v0 = b_A.subtract(a_A);
		Vec3 v1 = b_B.subtract(a_A);
		Vec3 v2 = b_A.subtract(a_B);
		Vec3 v3 = b_B.subtract(a_B);

		// squared distances:
		double d0 = v0.dot(v0);
		double d1 = v1.dot(v1);
		double d2 = v2.dot(v2);
		double d3 = v3.dot(v3);

		// select best potential endpoint on capsule A:
		Vec3 bestA;
		if (d2 < d0 || d2 < d1 || d3 < d0 || d3 < d1)
		{
			bestA = a_B;
		}
		else
		{
		  bestA = a_A;
		}

		// select point on capsule B line segment nearest to best potential endpoint on A capsule:
		Vec3 bestB = ClosestPointOnLineSegment(b_A, b_B, bestA);

		// now do the same for capsule A segment:
		bestA = ClosestPointOnLineSegment(a_A, a_B, bestB);

		Vec3 penetration_normal = bestA.subtract(bestB);
		double len = penetration_normal.length();
		penetration_normal = penetration_normal.normalize();
		double penetration_depth = a.radius + b.radius - len;
		return penetration_normal.scale(-penetration_depth);
	}
	
	private static Vec3 ClosestPointOnLineSegment(Vec3 A, Vec3 B, Vec3 Point)
	{
		Vec3 AB = B.subtract(A);
		double t = Point.subtract(A).dot(AB) / AB.dot(AB);
		return A.add(AB.scale(ModMath.clamp(t, 0, 1)));
	}
	
	protected static Vec3 cubeCubeCollision(CubeCollider a, CubeCollider b)
	{
		List<Vec3> normals = new ArrayList<>();
		for (Face f : a.faces) normals.add(f.normal);
		for (Face f : b.faces) normals.add(f.normal.scale(-1));
		
		Vec3 pushOutVec = Vec3.ZERO;
		
		for (Vec3 axis : normals)
		{
			double maxA = Double.MIN_VALUE;
			double minA = Double.MAX_VALUE;
			for (Vec3 va : a.vertices)
			{
				double dot = axis.dot(va);
				maxA = Math.max(maxA, dot);
				minA = Math.min(minA, dot);
			}
			
			double maxB = Double.MIN_VALUE;
			double minB = Double.MAX_VALUE;
			for (Vec3 vb : b.vertices)
			{
				double dot = axis.dot(vb);
				maxB = Math.max(maxB, dot);
				minB = Math.min(minB, dot);
			}
			
			if (minA >= maxB || minB >= maxA) return Vec3.ZERO;
			double length = minA <= minB && maxA > minB ? minB - maxA :
							minA < maxB && maxA >= maxB ? maxB - minA :
							minA >= minB && maxA <= maxB ? minB - maxA :
							0.0D;
			if (pushOutVec == Vec3.ZERO || pushOutVec.length() > length)
			{
				pushOutVec = axis.scale(length);
			}
		}
		return pushOutVec;
	}
}