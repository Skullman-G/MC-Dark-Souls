package com.skullmangames.darksouls.core.util.math.vector;

public class Vec4f
{
	public float x;
	public float y;
	public float z;
	public float w;
	
	public Vec4f(float x, float y, float z, float w)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	public Vec4f add(Vec4f other)
	{
		return this.add(other.x, other.y, other.z, other.w);
	}
	
	public Vec4f add(float x, float y, float z, float w)
	{
		return new Vec4f(this.x + x, this.y + y, this.z + z, this.w + w);
	}
	
	public Vec4f scale(float scale)
	{
		return new Vec4f(this.x * scale, this.y * scale, this.z * scale, this.w * scale);
	}
}
