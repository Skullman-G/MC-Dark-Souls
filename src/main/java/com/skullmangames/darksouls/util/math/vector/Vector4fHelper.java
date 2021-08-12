package com.skullmangames.darksouls.util.math.vector;

import net.minecraft.util.math.vector.Vector4f;

public class Vector4fHelper
{
	public static Vector4f scale(Vector4f vector, float scale)
	{
		vector.set(vector.x() * scale, vector.y() * scale, vector.z() * scale, vector.w() * scale);
		return vector;
	}
	
	public static Vector4f add(Vector4f left, Vector4f right, Vector4f dest)
	{
		if (dest == null)
			dest = new Vector4f();
		
		dest.set(left.x() + right.x(), left.y() + right.y(), left.z() + right.z(), left.w() + right.w());
		return dest;
	}
}
