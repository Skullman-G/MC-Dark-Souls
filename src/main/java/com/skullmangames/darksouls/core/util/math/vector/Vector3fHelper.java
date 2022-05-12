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
	
	public static Vector3f add(Vector3f left, Vector3f right)
	{
		return new Vector3f(left.x() + right.x(), left.y() + right.y(), left.z() + right.z());
	}

	public static Vector3f sub(Vector3f left, Vector3f right)
	{
		return new Vector3f(left.x() - right.x(), left.y() - right.y(), left.z() - right.z());
	}

	public static Vector3f sub(Vector3f left, Vector3f right, Vector3f dest)
	{
		if (dest == null)
			return new Vector3f(left.x() - right.x(), left.y() - right.y(), left.z() - right.z());
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

	public static Vector3f mul(Vector3f vec, float x, float y, float z)
	{
		return new Vector3f(vec.x() * x, vec.y() * y, vec.z() * z);
	}

	public static Vector3f normalize(Vector3f vec)
	{
		float norm = (float) Math.sqrt(vec.x() * vec.x() + vec.y() * vec.y() + vec.z() * vec.z());
		if (norm != 0)
		{
			vec.set(vec.x() / norm, vec.y() / norm, vec.z() / norm);
		} else
		{
			vec.set(0, 0, 0);
		}

		return vec;
	}
}
