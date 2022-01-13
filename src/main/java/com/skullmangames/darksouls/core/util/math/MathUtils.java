package com.skullmangames.darksouls.core.util.math;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class MathUtils
{
	public static double getAngleBetween(Entity e1, Entity e2)
	{
		Vec3 a = e1.getLookAngle();
		Vec3 b = new Vec3(e2.getX() - e1.getX(), e2.getY() - e1.getY(), e2.getZ() - e1.getZ())
				.normalize();
		double cosTheta = (a.x * b.x + a.y * b.y + a.z * b.z);
		return Math.acos(cosTheta);
	}
	
	public static void translateStack(PoseStack mStack, PublicMatrix4f mat)
	{
		Vector3f vector = getTranslationFromMatrix(mat);
		mStack.translate(vector.x(), vector.y(), vector.z());
	}
	
	private static Vector3f getTranslationFromMatrix(PublicMatrix4f mat)
	{
		return new Vector3f(mat.m30, mat.m31, mat.m32);
	}
	
	public static float interpolateRotation(float par1, float par2, float par3)
	{
		float f = 0;
		for (f = par2 - par1; f < -180.0F; f += 360.0F);
		while (f >= 180.0F) f -= 360.0F;
		return par1 + par3 * f;
	}

	public static float getInterpolatedRotation(float par1, float par2, float par3)
	{
		float f = 0;
		for (f = par2 - par1; f < -180.0F; f += 360.0F);
		while (f >= 180.0F) f -= 360.0F;
		return par3 * f;
	}
	
	public static int clamp(int value, int min, int max)
	{
		if (min >= max) new Exception("Min value bigger then max value.").printStackTrace();
		else if (value < min) value = min;
		else if (value > max) value = max;
		return value;
	}
	
	public static float clamp(float value, float range)
	{
		return clamp(value, -range, range);
	}
	
	public static double clamp(double value, double min, double max)
	{
		if (min >= max) new Exception("Min value bigger then max value.").printStackTrace();
		else if (value < min) value = min;
		else if (value > max) value = max;
		return value;
	}
	
	public static float clamp(float value, float min, float max)
	{
		if (min >= max) new Exception("Min value bigger then max value.").printStackTrace();
		else if (value < min) value = min;
		else if (value > max) value = max;
		return value;
	}
	
	public static int getNearestTo(int value, int... destined)
	{
		int a = 0;
		for (int i = 1; i < destined.length; i++)
		{
			int i1 = Math.abs(value - destined[i]);
			int i2 = Math.abs(value - destined[a]);
			if (i1 < i2) a = i;
		}
		
		return destined[a];
	}
}
