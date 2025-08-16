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
import com.skullmangames.darksouls.core.util.collider.CubeCollider.Face;
import com.skullmangames.darksouls.core.util.math.ModMath;
import com.skullmangames.darksouls.core.util.math.vector.ModMatrix4f;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class Collider
{
	private final ColliderType<?> type;
	private Vec3 worldCenter = Vec3.ZERO;
	protected Vec3[] vertices;
	protected Vec3[] modelVertices;
	
	/**
	 * Test hitbox.
	 * Bigger than collider hitbox.
	 * Uses mass center instead of world center.
	 **/
	protected final AABB outerAABB;

	public Collider(ColliderType<?> type, AABB outerAABB)
	{
		this.type = type;
		this.outerAABB = outerAABB;
	}
	
	@Nullable
	public ResourceLocation getId()
	{
		return this.type.getId();
	}
	
	public boolean is(ColliderType<?> type)
	{
		return this.getId() == type.getId();
	}
	
	public Vec3 getWorldCenter()
	{
		return this.worldCenter;
	}
	
	public void transform(ModMatrix4f mat)
	{
		this.rotateTo(mat);
		Vec3 pos = mat.transform(Vec3.ZERO);
		this.moveTo(new Vec3(-pos.x, pos.y, -pos.z));
	}
	
	protected void rotateTo(ModMatrix4f mat)
	{
		ModMatrix4f rot = mat.removeTranslation();
		
		for (int i = 0; i < this.vertices.length; i++)
		{
			this.vertices[i] = ModMatrix4f.transform(rot, this.modelVertices[i]);
			this.vertices[i] = new Vec3(-this.vertices[i].x, this.vertices[i].y, -this.vertices[i].z);
		}
	}
	
	protected void moveTo(Vec3 pos)
	{
		this.worldCenter = pos;
		for (int i = 0; i < this.vertices.length; i++)
		{
			this.vertices[i] = this.vertices[i].add(this.worldCenter);
		}
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
					Collider shieldCollider = Colliders.SHIELD.get().create();
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
	public void draw(EntityCapability<?> entityCap, String jointName, float partialTicks)
	{
		boolean red = entityCap instanceof LivingCap<?> livingCap ? livingCap.getEntityState() == EntityState.CONTACT : false;
		this.update(entityCap, jointName, partialTicks);

		this.drawInternal(red);
	}
	
	@OnlyIn(Dist.CLIENT)
	public abstract void drawInternal(boolean red);
	
	@Override
	public String toString()
	{
		return String.format("Id: %s, Center : [%f, %f, %f]", this.getId(), this.getWorldCenter().x, this.getWorldCenter().y,
				this.getWorldCenter().z);
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
	
	public static enum ColliderShape
	{
		CUBE, CAPSULE
	}
	
	public static class CoreBuilder implements JsonBuilder<ColliderType<?>>
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
				ColliderShape shape = ColliderShape.valueOf(o.get("shape").getAsString());
				
				switch (shape)
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
		public ColliderType<?> build()
		{
			if (this.colliders.size() > 1)
			{
				return new ColliderType<>(this.getId(), (type) ->
				{
					Collider[] array = new Collider[this.colliders.size()];
					for (int i = 0; i < array.length; i++)
					{
						array[i] = this.colliders.get(i).factory(type);
					}
					return new MultiCollider(type, array);
				});
			}
			else
			{
				return this.colliders.get(0).build();
			}
		}
	}
	
	public static abstract class Builder implements JsonBuilder<ColliderType<?>>
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
		
		protected abstract ColliderShape getShape();
		
		@Override
		public JsonObject toJson()
		{
			JsonObject json = new JsonObject();
			json.addProperty("shape", this.getShape().name());
			return json;
		}
		
		@Override
		public ResourceLocation getId()
		{
			return this.id;
		}
		
		protected abstract Collider factory(ColliderType<?> type);
		
		@Override
		public ColliderType<?> build()
		{
			return new ColliderType<>(this.getId(), this::factory);
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

		// get closest points between capsule segments
		Vec3[] closest = closestSegmentPoints(a_A, a_B, b_A, b_B);
		Vec3 bestA = closest[0];
		Vec3 bestB = closest[1];

		// compute separation vector
		Vec3 penetration_normal = bestA.subtract(bestB);
		double len = penetration_normal.length();

		if (len < 1e-8)
		{
			// Capsules overlap perfectly along axes, pick arbitrary normal
			penetration_normal = new Vec3(1, 0, 0);
			len = 0.0;
		} else
		{
			penetration_normal = penetration_normal.scale(1.0 / len); // normalize
		}

		// penetration depth
		double penetration_depth = a.radius + b.radius - len;

		if (penetration_depth <= 0.0)
		{
			// no collision
			return new Vec3(0, 0, 0);
		}

		// return MTV (minimum translation vector) pointing from A to B
		return penetration_normal.scale(-penetration_depth);
	}

	private static Vec3[] closestSegmentPoints(Vec3 p1, Vec3 q1, Vec3 p2, Vec3 q2)
	{
		Vec3 d1 = q1.subtract(p1); // direction of segment S1
		Vec3 d2 = q2.subtract(p2); // direction of segment S2
		Vec3 r = p1.subtract(p2);
		double a = d1.dot(d1); // squared length of S1
		double e = d2.dot(d2); // squared length of S2
		double f = d2.dot(r);

		double s, t;

		if (a <= 1e-8 && e <= 1e-8)
		{
			// both segments degenerate into points
			return new Vec3[]
			{ p1, p2 };
		}
		if (a <= 1e-8)
		{
			// first segment is a point
			s = 0.0;
			t = ModMath.clamp(f / e, 0.0, 1.0);
		}
		else
		{
			double c = d1.dot(r);
			if (e <= 1e-8)
			{
				// second segment is a point
				t = 0.0;
				s = ModMath.clamp(-c / a, 0.0, 1.0);
			}
			else
			{
				double b = d1.dot(d2);
				double denom = a * e - b * b;

				if (denom != 0.0)
				{
					s = ModMath.clamp((b * f - c * e) / denom, 0.0, 1.0);
				}
				else
				{
					s = 0.0;
				}

				double tnom = (b * s + f);
				if (tnom < 0.0)
				{
					t = 0.0;
					s = ModMath.clamp(-c / a, 0.0, 1.0);
				}
				else if (tnom > e)
				{
					t = 1.0;
					s = ModMath.clamp((b - c) / a, 0.0, 1.0);
				}
				else
				{
					t = tnom / e;
				}
			}
		}

		Vec3 c1 = p1.add(d1.scale(s));
		Vec3 c2 = p2.add(d2.scale(t));
		return new Vec3[]
		{ c1, c2 };
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