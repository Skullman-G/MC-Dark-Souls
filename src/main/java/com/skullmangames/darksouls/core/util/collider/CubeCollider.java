package com.skullmangames.darksouls.core.util.collider;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import com.skullmangames.darksouls.client.renderer.Gizmos;
import com.skullmangames.darksouls.core.util.math.vector.ModMatrix4f;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CubeCollider extends Collider
{
	protected final Face[] faces;
	
	public CubeCollider(ResourceLocation id, AABB aabb)
	{
		this(id, aabb, aabb.getCenter());
	}
	
	public CubeCollider(ResourceLocation id, AABB aabb, Vec3 offset)
	{
		this(id, aabb.minX - offset.x, aabb.minY - offset.y, aabb.minZ - offset.z,
				aabb.maxX - offset.x, aabb.maxY - offset.y, aabb.maxZ - offset.z);
		this.moveTo(offset);
	}
	
	/*
	 * This constructor is purely used to make a clone of another CubeCollider
	 */
	public CubeCollider(ResourceLocation id, AABB aabb, Vec3[] modelVertices, Face[] faces)
	{
		super(id, aabb);
		this.modelVertices = new Vec3[8];
		this.vertices = new Vec3[8];
		this.faces = new Face[6];
		
		for (int i = 0; i < this.modelVertices.length && i < modelVertices.length; i++)
		{
			this.modelVertices[i] = modelVertices[i];
			this.vertices[i] = modelVertices[i];
		}
		
		for (int i = 0; i < this.faces.length && i < faces.length; i++)
		{
			this.faces[i] = new Face(this, faces[i].modelNormal, faces[i].vertices);
		}
	}
	
	public CubeCollider(ResourceLocation id, double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
	{
		super(id, createOuterAABB(minX, minY, minZ, maxX, maxY, maxZ));
		this.modelVertices = new Vec3[8];
		this.vertices = new Vec3[8];
		this.faces = new Face[6];
		
		this.modelVertices[0] = new Vec3(minX, minY, minZ);
		this.modelVertices[1] = new Vec3(maxX, minY, minZ);
		this.modelVertices[2] = new Vec3(maxX, maxY, minZ);
		this.modelVertices[3] = new Vec3(minX, maxY, minZ);
		this.modelVertices[4] = new Vec3(minX, minY, maxZ);
		this.modelVertices[5] = new Vec3(maxX, minY, maxZ);
		this.modelVertices[6] = new Vec3(maxX, maxY, maxZ);
		this.modelVertices[7] = new Vec3(minX, maxY, maxZ);
		
		for (int i = 0; i < this.vertices.length; i++) this.vertices[i] = this.modelVertices[i];
		
		this.faces[0] = new Face(this, new Vec3(0, 0, -1), 0, 1, 2, 3);
		this.faces[1] = new Face(this, new Vec3(0, 0, 1), 4, 5, 6, 7);
		this.faces[2] = new Face(this, new Vec3(0, -1, 0), 0, 1, 5, 4);
		this.faces[3] = new Face(this, new Vec3(0, 1, 0), 2, 3, 7, 6);
		this.faces[4] = new Face(this, new Vec3(-1, 0, 0), 0, 3, 7, 4);
		this.faces[5] = new Face(this, new Vec3(1, 0, 0), 1, 2, 6, 5);
	}
	
	private static AABB createOuterAABB(double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
	{
		Vec3 center = new Vec3((minX + maxX) / 2, (minY + maxY) / 2, (minZ + maxZ) / 2);
		double lengthX = maxX - center.x;
		double lengthY = maxY - center.y;
		double lengthZ = maxZ - center.z;
		double length = Math.max(lengthX, Math.max(lengthY, lengthZ));
		return new AABB(-length, -length, -length, length, length, length);
	}

	@Override
	public void transform(ModMatrix4f mat)
	{
		ModMatrix4f rot = mat.removeTranslation();
		
		for (int i = 0; i < this.modelVertices.length; i++)
		{
			this.vertices[i] = ModMatrix4f.transform(rot, this.modelVertices[i]);
			this.vertices[i] = new Vec3(-this.vertices[i].x, this.vertices[i].y, -this.vertices[i].z);
		}
		
		for (Face face : this.faces)
		{
			face.normal = ModMatrix4f.transform(rot, face.modelNormal);
			face.normal = new Vec3(-face.normal.x, face.normal.y, -face.normal.z);
		}

		super.transform(mat);
	}

	@Override
	public boolean collidesWith(Collider other)
	{
		return other instanceof CubeCollider cube ? cubeCubeCollision(this, cube).lengthSqr() != 0
				: other instanceof CapsuleCollider capsule ? CapsuleCollider.capsuleCubeCollision(capsule, this).lengthSqr() != 0
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
			Vec3 pushVec = col instanceof CubeCollider cube ? cubeCubeCollision(this, cube)
						: col instanceof CapsuleCollider capsule ? CapsuleCollider.capsuleCubeCollision(capsule, this)
						: Vec3.ZERO;
			movement = movement.add(pushVec);
		}
		return movement;
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
	
	@Override
	public Vec3 getMassCenter()
	{
		Vec3 min = this.min();
		Vec3 max = this.max();
		return new Vec3((min.x + max.x) / 2, (min.y + max.y) / 2, (min.z + max.z) / 2);
	}

	@Override
	public boolean collidesWith(Entity opponent)
	{
		CubeCollider collider = new CubeCollider(null, opponent.getBoundingBox());
		return this.collidesWith(collider);
	}
	
	@Override
	protected Vec3 min()
	{
		return this.vertices[0];
	}
	
	@Override
	protected Vec3 max()
	{
		return this.vertices[6];
	}
	
	@Override
	public Vec3 top()
	{
		Vec3 from = this.vertices[1];
		Vec3 to = this.vertices[3];
		return new Vec3((from.x + to.x) / 2, (from.y + to.y) / 2, (from.z + to.z) / 2);
	}
	
	@Override
	public Vec3 bottom()
	{
		Vec3 from = this.vertices[5];
		Vec3 to = this.vertices[7];
		return new Vec3((from.x + to.x) / 2, (from.y + to.y) / 2, (from.z + to.z) / 2);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void drawInternal(boolean red)
	{
		Gizmos.drawBox(this.vertices, red ? 0xFF0000 : 0xFFFFFF);
	}
	
	public static class Builder extends Collider.Builder
	{
		protected double minX, minY, minZ, maxX, maxY, maxZ;
		
		protected Builder(ResourceLocation id, double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
		{
			super(id);
			this.minX = minX;
			this.minY = minY;
			this.minZ = minZ;
			this.maxX = maxX;
			this.maxY = maxY;
			this.maxZ = maxZ;
		}
		
		protected Builder(ResourceLocation location, JsonObject json)
		{
			super(location, json);
			
			this.minX = json.get("min_x").getAsDouble();
			this.minY = json.get("min_y").getAsDouble();
			this.minZ = json.get("min_x").getAsDouble();
			
			this.maxX = json.get("max_x").getAsDouble();
			this.maxY = json.get("max_y").getAsDouble();
			this.maxZ = json.get("max_z").getAsDouble();
		}

		@Override
		public JsonObject toJson()
		{
			JsonObject json = super.toJson();
			
			json.addProperty("min_x", this.minX);
			json.addProperty("min_y", this.minY);
			json.addProperty("min_z", this.minZ);
			
			json.addProperty("max_x", this.maxX);
			json.addProperty("max_y", this.maxY);
			json.addProperty("max_z", this.maxZ);
			
			return json;
		}

		@Override
		public Collider build()
		{
			return new CubeCollider(this.getId(), this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
		}

		@Override
		protected ColliderType getType()
		{
			return ColliderType.CUBE;
		}
	}
}
