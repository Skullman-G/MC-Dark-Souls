package com.skullmangames.darksouls.core.util.collider;

import com.skullmangames.darksouls.client.renderer.Gizmos;
import com.skullmangames.darksouls.core.util.math.vector.ModMatrix4f;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CubeCollider extends Collider
{
	protected final Face[] faces;
	
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
	public CubeCollider(AABB aabb, Vec3[] modelVertices, Face[] faces)
	{
		super(aabb);
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
	
	public CubeCollider(double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
	{
		super(createOuterAABB(minX, minY, minZ, maxX, maxY, maxZ));
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
		return other instanceof CubeCollider cube ? satCollisionDetection(this, cube) && satCollisionDetection(cube, this)
				: other instanceof CapsuleCollider capsule ? CapsuleCollider.capsuleCubeDetection(capsule, this)
				: false;
	}
	
	protected static boolean satCollisionDetection(CubeCollider a, CubeCollider b)
	{
		for (Face face : a.faces)
		{
			Vec3 axis = face.normal;
			
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
	public boolean collidesWith(Entity opponent)
	{
		CubeCollider collider = new CubeCollider(opponent.getBoundingBox());
		return this.collidesWith(collider);
	}
	
	@Override
	public CubeCollider clone()
	{
		return new CubeCollider(this.outerAABB, this.modelVertices, this.faces);
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
}
