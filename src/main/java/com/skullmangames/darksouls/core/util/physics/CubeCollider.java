package com.skullmangames.darksouls.core.util.physics;

import com.skullmangames.darksouls.client.renderer.Gizmos;
import com.skullmangames.darksouls.core.util.math.vector.ModMatrix4f;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CubeCollider extends Collider
{
	protected final Vec3[] modelVertices;
	protected final Vec3[] modelNormals;
	
	public CubeCollider(AABB aabb)
	{
		this(aabb, aabb.getCenter());
	}
	
	public CubeCollider(AABB aabb, Vec3 offset)
	{
		this(aabb.minX - offset.x, aabb.minY - offset.y, aabb.minZ - offset.z,
				aabb.maxX - offset.x, aabb.maxY - offset.y, aabb.maxZ - offset.z);
		this.moveTo(offset);
	}
	
	/*
	 * This constructor is purely used to make a clone of another CubeCollider
	 */
	public CubeCollider(AABB aabb, Vec3[] modelVertices, Vec3[] modelNormals)
	{
		super(aabb);
		this.modelVertices = new Vec3[8];
		this.vertices = new Vec3[8];
		this.modelNormals = new Vec3[3];
		this.normals = new Vec3[3];
		
		this.modelVertices[0] = modelVertices[0];
		this.modelVertices[1] = modelVertices[1];
		this.modelVertices[2] = modelVertices[2];
		this.modelVertices[3] = modelVertices[3];
		this.modelVertices[4] = modelVertices[4];
		this.modelVertices[5] = modelVertices[5];
		this.modelVertices[6] = modelVertices[6];
		this.modelVertices[7] = modelVertices[7];
		
		this.vertices[0] = this.modelVertices[0];
		this.vertices[1] = this.modelVertices[1];
		this.vertices[2] = this.modelVertices[2];
		this.vertices[3] = this.modelVertices[3];
		this.vertices[4] = this.modelVertices[4];
		this.vertices[5] = this.modelVertices[5];
		this.vertices[6] = this.modelVertices[6];
		this.vertices[7] = this.modelVertices[7];
		
		this.modelNormals[0] = modelNormals[0];
		this.modelNormals[1] = modelNormals[1];
		this.modelNormals[2] = modelNormals[2];
		
		this.normals[0] = this.modelNormals[0];
		this.normals[1] = this.modelNormals[1];
		this.normals[2] = this.modelNormals[2];
	}
	
	public CubeCollider(double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
	{
		super(createOuterAABB(minX, minY, minZ, maxX, maxY, maxZ));
		this.modelVertices = new Vec3[8];
		this.vertices = new Vec3[8];
		this.modelNormals = new Vec3[3];
		this.normals = new Vec3[3];
		
		this.modelVertices[0] = new Vec3(minX, minY, minZ);
		this.modelVertices[1] = new Vec3(maxX, minY, minZ);
		this.modelVertices[2] = new Vec3(maxX, maxY, minZ);
		this.modelVertices[3] = new Vec3(minX, maxY, minZ);
		this.modelVertices[4] = new Vec3(minX, minY, maxZ);
		this.modelVertices[5] = new Vec3(maxX, minY, maxZ);
		this.modelVertices[6] = new Vec3(maxX, maxY, maxZ);
		this.modelVertices[7] = new Vec3(minX, maxY, maxZ);
		
		this.vertices[0] = this.modelVertices[0];
		this.vertices[1] = this.modelVertices[1];
		this.vertices[2] = this.modelVertices[2];
		this.vertices[3] = this.modelVertices[3];
		this.vertices[4] = this.modelVertices[4];
		this.vertices[5] = this.modelVertices[5];
		this.vertices[6] = this.modelVertices[6];
		this.vertices[7] = this.modelVertices[7];
		
		this.modelNormals[0] = new Vec3(1, 0, 0);
		this.modelNormals[1] = new Vec3(0, 1, 0);
		this.modelNormals[2] = new Vec3(0, 0, -1);
		
		this.normals[0] = this.modelNormals[0];
		this.normals[1] = this.modelNormals[1];
		this.normals[2] = this.modelNormals[2];
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
		
		for (int i = 0; i < this.modelNormals.length; i++)
		{
			this.normals[i] = ModMatrix4f.transform(rot, this.modelNormals[i]);
			this.normals[i] = new Vec3(-this.normals[i].x, this.normals[i].y, -this.normals[i].z);
		}

		super.transform(mat);
	}

	@Override
	public boolean collidesWith(Collider other)
	{
		return satCollisionDetection(this, other) && satCollisionDetection(other, this);
	}
	
	private static boolean satCollisionDetection(Collider a, Collider b)
	{
		for (Vec3 axis : a.normals)
		{
			double maxDistA = -Double.MAX_VALUE;
			double minDistA = Double.MAX_VALUE;
			for (Vec3 va : a.vertices)
			{
				double dot = axis.dot(va);
				maxDistA = Math.max(maxDistA, dot);
				minDistA = Math.min(minDistA, dot);
			}
			
			double maxDistB = -Double.MAX_VALUE;
			double minDistB = Double.MAX_VALUE;
			for (Vec3 vb : b.vertices)
			{
				double dot = axis.dot(vb);
				maxDistB = Math.max(maxDistB, dot);
				minDistB = Math.min(minDistB, dot);
			}
			
			if (minDistA >= maxDistB || minDistB >= maxDistA) return false;
		}
		return true;
	}
	
	@Override
	public Vec3 getMassCenter()
	{
		Vec3 min = this.min();
		Vec3 max = this.max();
		return new Vec3((min.x + max.x) / 2, (min.y + max.y) / 2, (min.z + max.z) / 2);
	}
	
	@Override
	public Collider getScaledCollider(float scale)
	{
		Vec3 min = this.modelVertices[0];
		Vec3 max = this.modelVertices[6];
		return new CubeCollider(scale * min.x, scale * min.y, scale * min.z,
				scale * max.x, scale * max.y, scale * max.z);
	}

	@Override
	public boolean collidesWith(Entity entity)
	{
		CubeCollider collider = new CubeCollider(entity.getBoundingBox());
		return this.collidesWith(collider);
	}
	
	@Override
	public CubeCollider clone()
	{
		return new CubeCollider(this.outerAABB, this.modelVertices, this.modelNormals);
	}

	@Override
	public String toString()
	{
		return String.format("Center : [%f, %f, %f]", this.getWorldCenter().x, this.getWorldCenter().y,
				this.getWorldCenter().z);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void drawInternal(boolean red)
	{
		Gizmos.drawBox(this.vertices, red ? 0xFF0000 : 0xFFFFFF);
	}
}
