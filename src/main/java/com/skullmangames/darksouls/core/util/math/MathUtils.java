package com.skullmangames.darksouls.core.util.math;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class MathUtils
{
	// Convert entity rotation into normal rotation
	public static float toNormalRot(float rot)
	{
		float normalRot = -rot;
		if (normalRot < 0) normalRot += 360F;
		return normalRot;
	}
	
	public static int dir(double value)
	{
		return value > 0 ? 1 : value < 0 ? -1 : 0;
	}
	
	public static int lerp(int incr, int from, int to)
	{
		if (from == to) return from;
		if (incr > Math.abs(to - from)) incr = Math.abs(to - from);
		return from < to ? from + incr : from - incr;
	}
	
	public static double round(double value, int decimalAmount)
	{
		int multiply = (int)Math.pow(10, decimalAmount);
		return (double) Math.round(value * multiply) / multiply;
	}
	
	public static float round(float value, int decimalAmount)
	{
		int multiply = (int)Math.pow(10, decimalAmount);
		return (float) Math.round(value * multiply) / multiply;
	}
	
	public static Vec3 projectVector(Vec3 from, Vec3 to)
	{
		double dot = to.dot(from);
		double normalScale = 1.0D / ((to.x * to.x) + (to.y * to.y) + (to.z * to.z));
		return new Vec3(dot * to.x * normalScale, dot * to.y * normalScale, dot * to.z * normalScale);
	}

	public static double getAngleBetween(Entity e1, Entity e2)
	{
		Vec3 a = e1.getLookAngle();
		Vec3 b = new Vec3(e2.getX() - e1.getX(), e2.getY() - e1.getY(), e2.getZ() - e1.getZ()).normalize();
		double cosTheta = (a.x * b.x + a.y * b.y + a.z * b.z);
		return Math.acos(cosTheta);
	}

	public static double getAngleBetween(Vector3f a, Vector3f b)
	{
		double cos = a.x() * b.x() + a.y() * b.y() + a.z() * b.z();
		return Math.acos(cos);
	}

	public static Quaternion mulQuaternion(Quaternion left, Quaternion right, Quaternion dest)
	{
		if (dest == null)
		{
			dest = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
		}

		float f = left.i();
		float f1 = left.j();
		float f2 = left.k();
		float f3 = left.r();
		float f4 = right.i();
		float f5 = right.j();
		float f6 = right.k();
		float f7 = right.r();
		float i = f3 * f4 + f * f7 + f1 * f6 - f2 * f5;
		float j = f3 * f5 - f * f6 + f1 * f7 + f2 * f4;
		float k = f3 * f6 + f * f5 - f1 * f4 + f2 * f7;
		float r = f3 * f7 - f * f4 - f1 * f5 - f2 * f6;

		dest.set(i, j, k, r);

		return dest;
	}

	public static float interpolateRotation(float startRot, float endRot, float percentage)
	{
		float dist = endRot - startRot;
		while (dist < -180.0F) dist += 360.0F;
		while (dist >= 180.0F) dist -= 360.0F;
		return startRot + dist * percentage;
	}

	public static int clamp(int value, int min, int max)
	{
		if (min >= max)
			new Exception("Min value bigger then max value.").printStackTrace();
		else if (value < min)
			value = min;
		else if (value > max)
			value = max;
		return value;
	}

	public static float clamp(float value, float range)
	{
		return clamp(value, -range, range);
	}
	
	public static double clamp(double value, double range)
	{
		return clamp(value, -range, range);
	}

	public static double clamp(double value, double min, double max)
	{
		if (min >= max)
			new Exception("Min value bigger then max value.").printStackTrace();
		else if (value < min)
			value = min;
		else if (value > max)
			value = max;
		return value;
	}

	public static float clamp(float value, float min, float max)
	{
		if (min >= max)
			new Exception("Min value bigger then max value.").printStackTrace();
		else if (value < min)
			value = min;
		else if (value > max)
			value = max;
		return value;
	}

	public static int getNearestTo(int value, int... destined)
	{
		int a = 0;
		for (int i = 1; i < destined.length; i++)
		{
			int i1 = Math.abs(value - destined[i]);
			int i2 = Math.abs(value - destined[a]);
			if (i1 < i2)
				a = i;
		}

		return destined[a];
	}

	public static Vector3f lerpVector(Vector3f start, Vector3f end, float weight)
	{
		float x = start.x() + (end.x() - start.x()) * weight;
		float y = start.y() + (end.y() - start.y()) * weight;
		float z = start.z() + (end.z() - start.z()) * weight;
		return new Vector3f(x, y, z);
	}
}
