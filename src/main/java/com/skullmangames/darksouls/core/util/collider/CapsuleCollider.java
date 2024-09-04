package com.skullmangames.darksouls.core.util.collider;

import java.util.List;

import com.google.gson.JsonObject;
import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.client.renderer.Gizmos;
import com.skullmangames.darksouls.core.util.math.ModMath;
import com.skullmangames.darksouls.core.util.math.vector.ModMatrix4f;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CapsuleCollider extends Collider
{
	protected final double radius;
	protected final double height;
	
	public CapsuleCollider(ResourceLocation id, double radius, double height, Vec3 base, float xRot, float yRot)
	{
		this(id, radius, height, base);
		
		ModMatrix4f rotMat = ModMatrix4f.createRotatorDeg((float)xRot, Vector3f.XP)
				.rotateDeg((float)(180 + yRot), Vector3f.YP);
		
		for (int i = 0; i < this.vertices.length; i++)
		{
			this.modelVertices[i] = ModMatrix4f.transform(rotMat, this.modelVertices[i]);
			this.modelVertices[i] = new Vec3(-this.modelVertices[i].x, this.modelVertices[i].y, -this.modelVertices[i].z);
		}
	}
	
	public CapsuleCollider(ResourceLocation id, double radius, double height, Vec3 base)
	{
		super(id, createOuterAABB(radius, height));
		
		this.radius = radius;
		this.height = height;
		
		// Gizmo vertices
		this.modelVertices = new Vec3[146];
		this.vertices = new Vec3[146];
		
		this.modelVertices[0] = base;
		this.vertices[0] = base;
		for (int s = 1; s <= 6; s++)
		{
			double angle = (s / 12D) * Math.PI;
			double r = Math.sin(angle) * radius;
			double z = -radius + Math.cos(angle) * radius;
			for (int i = 1; i <= 13; i++)
			{
				double d = (i / 12D) * 2D * Math.PI;
				Vec3 vertex = new Vec3(-Math.cos(d) * r, Math.sin(d) * r, z).add(base);
				this.modelVertices[(s - 1) * 12 + i] = vertex;
				this.vertices[(s - 1) * 12 + i] = vertex;
			}
		}
		for (int s = 6; s <= 11; s++)
		{
			double angle = (s / 12D) * Math.PI;
			double r = Math.sin(angle) * radius;
			double z = -height + radius + Math.cos(angle) * radius;
			for (int i = 1; i <= 13; i++)
			{
				double d = (i / 12D) * 2D * Math.PI;
				Vec3 vertex = new Vec3(-Math.cos(d) * r, Math.sin(d) * r, z).add(base);
				this.modelVertices[s * 12 + i] = vertex;
				this.vertices[s * 12 + i] = vertex;
			}
		}
		this.modelVertices[145] = base.add(0, 0, -height);
		this.vertices[145] = base.add(0, 0, -height);
	}
	
	@Override
	protected Vec3 min()
	{
		return this.vertices[145];
	}
	
	@Override
	protected Vec3 max()
	{
		return this.vertices[0];
	}
	
	@Override
	public Vec3 top()
	{
		return this.vertices[145];
	}
	
	@Override
	public Vec3 bottom()
	{
		return this.vertices[0];
	}
	
	private static AABB createOuterAABB(double radius, double height)
	{
		double length = Math.max(radius * 2, height);
		return new AABB(-length, -length, -length, length, length, length);
	}
	
	@Override
	public void transform(ModMatrix4f mat)
	{
		ModMatrix4f rot = mat.removeTranslation();
		
		for (int i = 0; i < this.vertices.length; i++)
		{
			this.vertices[i] = ModMatrix4f.transform(rot, this.modelVertices[i]);
			this.vertices[i] = new Vec3(-this.vertices[i].x, this.vertices[i].y, -this.vertices[i].z);
		}

		super.transform(mat);
	}

	@Override
	public boolean collidesWith(Collider other)
	{
		return other instanceof CubeCollider cube ? capsuleCubeCollision(this, cube).lengthSqr() != 0
				: other instanceof CapsuleCollider capsule ? capsuleCapsuleCollision(this, capsule).lengthSqr() != 0
				: false;
	}
	
	@Override
	public Vec3 collide(Vec3 movement, List<ColliderHolder> others)
	{
		Vec3 currentPos = this.getWorldCenter();
		for (ColliderHolder holder : others)
		{
			if (holder.isEmpty()) continue;
			holder.correctPosition();
			Collider col = holder.getType();
			this.moveTo(currentPos.add(movement));
			Vec3 pushVec = col instanceof CubeCollider cube ? capsuleCubeCollision(this, cube)
					: col instanceof CapsuleCollider capsule ? capsuleCapsuleCollision(this, capsule)
					: Vec3.ZERO;
			movement = movement.add(pushVec);
		}
		return movement;
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
	
	@Override
	protected boolean collidesWith(Entity opponent)
	{
		CubeCollider collider = new CubeCollider(null, opponent.getBoundingBox());
		return this.collidesWith(collider);
	}

	@Override
	public Vec3 getMassCenter()
	{
		return this.min().add(this.max()).scale(0.5D);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void drawInternal(boolean red)
	{
		Gizmos.drawCapsule(this.vertices, red ? 0xFF0000 : 0xFFFFFF);
	}
	
	public static class Builder extends Collider.Builder
	{
		protected double radius, height;
		protected Vec3 base;
		protected float xRot, yRot;
		
		protected Builder(ResourceLocation id, double radius, double height, Vec3 base, float xRot, float yRot)
		{
			super(id);
			this.radius = radius;
			this.height = height;
			this.base = base;
			this.xRot = xRot;
			this.yRot = yRot;
		}
		
		protected Builder(ResourceLocation location, JsonObject json)
		{
			super(location, json);
			
			this.radius = json.get("radius").getAsDouble();
			this.height = json.get("height").getAsDouble();
			this.base = new Vec3
					(
							json.get("base_x").getAsDouble(),
							json.get("base_y").getAsDouble(),
							json.get("base_z").getAsDouble()
					);
			this.xRot = json.get("x_rotation").getAsFloat();
			this.yRot = json.get("y_rotation").getAsFloat();
		}

		@Override
		public JsonObject toJson()
		{
			JsonObject json = super.toJson();
			
			json.addProperty("radius", this.radius);
			json.addProperty("height", this.height);
			
			json.addProperty("base_x", this.base.x);
			json.addProperty("base_y", this.base.y);
			json.addProperty("base_z", this.base.z);
			
			json.addProperty("x_rotation", this.xRot);
			json.addProperty("y_rotation", this.yRot);
			
			return json;
		}

		@Override
		public Collider build()
		{
			return new CapsuleCollider(this.getId(), this.radius, this.height, this.base, this.xRot, this.yRot);
		}

		@Override
		protected ColliderType getType()
		{
			return ColliderType.CAPSULE;
		}
	}
}
