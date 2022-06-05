package com.skullmangames.darksouls.core.util.math;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class MathUtils
{
	public static int lerp(int incr, int from, int to)
	{
		return from == to ? from : from < to ? from + incr : from - incr;
	}
	
	public static double round(double value, int multiply)
	{
		return (double) Math.round(value * multiply) / multiply;
	}
	
	public static void rotateStack(PoseStack mStack, PublicMatrix4f mat)
	{
		mStack.mulPose(getQuaternionFromMatrix(mat));
	}
	
	private static Quaternion getQuaternionFromMatrix(PublicMatrix4f mat)
	{
		float w, x, y, z;
		float diagonal = mat.m00 + mat.m11 + mat.m22;

		if (diagonal > 0)
		{
			float w4 = (float) (Math.sqrt(diagonal + 1.0F) * 2.0F);
			w = w4 * 0.25F;
			x = (mat.m21 - mat.m12) / w4;
			y = (mat.m02 - mat.m20) / w4;
			z = (mat.m10 - mat.m01) / w4;
		}
		else if ((mat.m00 > mat.m11) && (mat.m00 > mat.m22))
		{
			float x4 = (float) (Math.sqrt(1.0F + mat.m00 - mat.m11 - mat.m22) * 2F);
			w = (mat.m21 - mat.m12) / x4;
			x = x4 * 0.25F;
			y = (mat.m01 + mat.m10) / x4;
			z = (mat.m02 + mat.m20) / x4;
		}
		else if (mat.m11 > mat.m22)
		{
			float y4 = (float) (Math.sqrt(1.0F + mat.m11 - mat.m00 - mat.m22) * 2F);
			w = (mat.m02 - mat.m20) / y4;
			x = (mat.m01 + mat.m10) / y4;
			y = y4 * 0.25F;
			z = (mat.m12 + mat.m21) / y4;
		}
		else
		{
			float z4 = (float) (Math.sqrt(1.0F + mat.m22 - mat.m00 - mat.m11) * 2F);
			w = (mat.m10 - mat.m01) / z4;
			x = (mat.m02 + mat.m20) / z4;
			y = (mat.m12 + mat.m21) / z4;
			z = z4 * 0.25F;
		}
		
		Quaternion quat = new Quaternion(x, y, z, w);
		quat.normalize();
		return quat;
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
		double cos = (a.x() * b.x() + a.y() * b.y() + a.z() * b.z());
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
		for (f = par2 - par1; f < -180.0F; f += 360.0F)
			;
		while (f >= 180.0F)
			f -= 360.0F;
		return par1 + par3 * f;
	}

	public static float getInterpolatedRotation(float par1, float par2, float par3)
	{
		float f = 0;
		for (f = par2 - par1; f < -180.0F; f += 360.0F)
			;
		while (f >= 180.0F)
			f -= 360.0F;
		return par3 * f;
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

	public static Quaternion lerpQuaternion(Quaternion from, Quaternion to, float weight)
	{
		float fromX = from.i();
		float fromY = from.j();
		float fromZ = from.k();
		float fromW = from.r();
		float toX = to.i();
		float toY = to.j();
		float toZ = to.k();
		float toW = to.r();
		float resultX;
		float resultY;
		float resultZ;
		float resultW;
		float dot = fromW * toW + fromX * toX + fromY * toY + fromZ * toZ;
		float blendI = 1f - weight;

		if (dot < 0)
		{
			resultW = blendI * fromW + weight * -toW;
			resultX = blendI * fromX + weight * -toX;
			resultY = blendI * fromY + weight * -toY;
			resultZ = blendI * fromZ + weight * -toZ;
		} else
		{
			resultW = blendI * fromW + weight * toW;
			resultX = blendI * fromX + weight * toX;
			resultY = blendI * fromY + weight * toY;
			resultZ = blendI * fromZ + weight * toZ;
		}

		Quaternion result = new Quaternion(resultX, resultY, resultZ, resultW);
		normalizeQuaternion(result);
		return result;
	}

	private static void normalizeQuaternion(Quaternion quaternion)
	{
		float f = quaternion.i() * quaternion.i() + quaternion.j() * quaternion.j() + quaternion.k() * quaternion.k()
				+ quaternion.r() * quaternion.r();
		if (f > 1.0E-6F)
		{
			float f1 = fastInvSqrt(f);
			quaternion.set(quaternion.i() * f1, quaternion.j() * f1, quaternion.k() * f1, quaternion.r() * f1);
		} else
		{
			quaternion.set(0.0F, 0.0F, 0.0F, 0.0F);
		}
	}

	private static float fastInvSqrt(float number)
	{
		float f = 0.5F * number;
		int i = Float.floatToIntBits(number);
		i = 1597463007 - (i >> 1);
		number = Float.intBitsToFloat(i);
		return number * (1.5F - f * number * number);
	}
}
