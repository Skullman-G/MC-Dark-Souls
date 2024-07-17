package com.skullmangames.darksouls.core.util.collider;

import net.minecraft.world.phys.Vec3;

public class Face
{
	private final Collider collider;
	public final Vec3 modelNormal;
	public Vec3 normal;
	public final int[] vertices;
	
	/**
	 * Make sure that the vertices are in the right order
	 **/
	public Face(Collider collider, Vec3 normal, int... vertices)
	{
		this.collider = collider;
		this.modelNormal = normal;
		this.normal = normal;
		this.vertices = vertices;
	}
	
	public Vec3 vertex(int i)
	{
		return this.collider.vertices[this.vertices[i]];
	}
	
	public Vec3 center()
	{
		double x = 0;
		double y = 0;
		double z = 0;
		for (int i = 0; i < this.vertices.length; i++)
		{
			Vec3 v = this.vertex(i);
			x += v.x;
			y += v.y;
			z += v.z;
		}
		x /= this.vertices.length;
		y /= this.vertices.length;
		z /= this.vertices.length;
		return new Vec3(x, y, z);
	}
}
