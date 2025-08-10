package com.skullmangames.darksouls.core.util.collider;

import com.google.gson.JsonObject;
import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.client.renderer.Gizmos;
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
	
	public CapsuleCollider(ColliderType<?> type, double radius, double height, Vec3 base, float xRot, float yRot)
	{
		this(type, radius, height, base);
		
		ModMatrix4f rotMat = ModMatrix4f.createRotatorDeg((float)xRot, Vector3f.XP)
				.rotateDeg((float)(180 + yRot), Vector3f.YP);
		
		for (int i = 0; i < this.vertices.length; i++)
		{
			this.modelVertices[i] = ModMatrix4f.transform(rotMat, this.modelVertices[i]);
			this.modelVertices[i] = new Vec3(-this.modelVertices[i].x, this.modelVertices[i].y, -this.modelVertices[i].z);
		}
	}
	
	public CapsuleCollider(ColliderType<?> type, double radius, double height, Vec3 base)
	{
		super(type, createOuterAABB(radius, height));
		
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
	public boolean collidesWith(Collider other)
	{
		return other instanceof MultiCollider ? other.collidesWith(this)
				: other instanceof CubeCollider cube ? capsuleCubeCollision(this, cube).lengthSqr() != 0
				: other instanceof CapsuleCollider capsule ? capsuleCapsuleCollision(this, capsule).lengthSqr() != 0
				: false;
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
	
	@Override
	public String toString()
	{
		return super.toString() + " [capsule]";
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
		protected CapsuleCollider factory(ColliderType<?> type)
		{
			return new CapsuleCollider(type, this.radius, this.height, this.base, this.xRot, this.yRot);
		}

		@Override
		protected ColliderShape getShape()
		{
			return ColliderShape.CAPSULE;
		}
	}
}
