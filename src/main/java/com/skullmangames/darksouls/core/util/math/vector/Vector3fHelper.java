package com.skullmangames.darksouls.core.util.math.vector;

import com.mojang.math.Vector3f;

public class Vector3fHelper
{
	public static float length(Vector3f vector)
	{
		return (float) Math.sqrt(lengthSqr(vector));
	}
	
	public static float lengthSqr(Vector3f vector)
	{
		return vector.x() * vector.x() + vector.y() * vector.y() + vector.z() * vector.z();
	}
	
	public static Vector3f scale(Vector3f vector, float scale)
	{
		vector.set(vector.x() * scale, vector.y() * scale, vector.z() * scale);
		return vector;
	}
	
	public static Vector3f sub(Vector3f left, Vector3f right)
	{
		return new Vector3f(left.x() - right.x(), left.y() - right.y(), left.z() - right.z());
	}
	
	public static Vector3f sub(Vector3f left, Vector3f right, Vector3f dest)
	{
		if (dest == null) return new Vector3f(left.x() - right.x(), left.y() - right.y(), left.z() - right.z());
		else
		{
			dest.set(left.x() - right.x(), left.y() - right.y(), left.z() - right.z());
			return dest;
		}
	}
	
	public static float dot(Vector3f left, Vector3f right)
	{
		return left.x() * right.x() + left.y() * right.y() + left.z() * right.z();
	}
}
