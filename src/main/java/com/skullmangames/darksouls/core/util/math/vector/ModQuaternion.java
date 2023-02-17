package com.skullmangames.darksouls.core.util.math.vector;

import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;

public class ModQuaternion
{
	public float x, y, z, w;

	public ModQuaternion(float x, float y, float z, float w)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		normalize();
	}
	
	public void set(ModQuaternion q)
	{
		this.set(q.x, q.y, q.z, q.w);
	}

	public void set(float x, float y, float z, float w)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public void normalize()
	{
		float mag = (float) Math.sqrt(w * w + x * x + y * y + z * z);
		w /= mag;
		x /= mag;
		y /= mag;
		z /= mag;
	}
	
	public ModQuaternion mulRight(ModQuaternion right)
	{
		this.x = this.w * right.x + this.x * right.w + this.y * right.z - this.z * right.y;
		this.y = this.w * right.y - this.x * right.z + this.y * right.w + this.z * right.x;
		this.z = this.w * right.z + this.x * right.y - this.y * right.x + this.z * right.w;
		this.w = this.w * right.w - this.x * right.x - this.y * right.y - this.z * right.z;

		return this;
	}

	public ModQuaternion mulRight(Quaternion right)
	{
		return this.mulRight(new ModQuaternion(right.i(), right.j(), right.k(), right.r()));
	}

	public ModQuaternion mulLeft(ModQuaternion left)
	{
		this.x = left.w * this.x + left.x * this.w + left.y * this.z - left.z * this.y;
		this.y = left.w * this.y - left.x * this.z + left.y * this.w + left.z * this.x;
		this.z = left.w * this.z + left.x * this.y - left.y * this.x + left.z * this.w;
		this.w = left.w * this.w - left.x * this.x - left.y * this.y - left.z * this.z;

		return this;
	}

	public ModQuaternion mulLeft(Quaternion left)
	{
		return this.mulLeft(new ModQuaternion(left.i(), left.j(), left.k(), left.r()));
	}


	public PublicMatrix4f toRotationMatrix()
	{
		PublicMatrix4f matrix = new PublicMatrix4f();
		final float xy = x * y;
		final float xz = x * z;
		final float xw = x * w;
		final float yz = y * z;
		final float yw = y * w;
		final float zw = z * w;
		final float xSquared = 2F * x * x;
		final float ySquared = 2F * y * y;
		final float zSquared = 2F * z * z;
		matrix.m00 = 1.0F - ySquared - zSquared;
		matrix.m01 = 2.0F * (xy - zw);
		matrix.m02 = 2.0F * (xz + yw);
		matrix.m10 = 2.0F * (xy + zw);
		matrix.m11 = 1.0F - xSquared - zSquared;
		matrix.m12 = 2.0F * (yz - xw);
		matrix.m20 = 2.0F * (xz - yw);
		matrix.m21 = 2.0F * (yz + xw);
		matrix.m22 = 1.0F - xSquared - ySquared;
		return matrix;
	}

	public static ModQuaternion fromMatrix(PublicMatrix4f matrix)
	{
		float w, x, y, z;
		float diagonal = matrix.m00 + matrix.m11 + matrix.m22;
		if (diagonal > 0)
		{
			float w4 = (float) (Math.sqrt(diagonal + 1f) * 2f);
			w = w4 / 4f;
			x = (matrix.m21 - matrix.m12) / w4;
			y = (matrix.m02 - matrix.m20) / w4;
			z = (matrix.m10 - matrix.m01) / w4;
		}
		else if ((matrix.m00 > matrix.m11) && (matrix.m00 > matrix.m22))
		{
			float x4 = (float) (Math.sqrt(1f + matrix.m00 - matrix.m11 - matrix.m22) * 2f);
			w = (matrix.m21 - matrix.m12) / x4;
			x = x4 / 4f;
			y = (matrix.m01 + matrix.m10) / x4;
			z = (matrix.m02 + matrix.m20) / x4;
		}
		else if (matrix.m11 > matrix.m22)
		{
			float y4 = (float) (Math.sqrt(1f + matrix.m11 - matrix.m00 - matrix.m22) * 2f);
			w = (matrix.m02 - matrix.m20) / y4;
			x = (matrix.m01 + matrix.m10) / y4;
			y = y4 / 4f;
			z = (matrix.m12 + matrix.m21) / y4;
		}
		else 
		{
			float z4 = (float) (Math.sqrt(1f + matrix.m22 - matrix.m00 - matrix.m11) * 2f);
			w = (matrix.m10 - matrix.m01) / z4;
			x = (matrix.m02 + matrix.m20) / z4;
			y = (matrix.m12 + matrix.m21) / z4;
			z = z4 / 4f;
		}
		return new ModQuaternion(x, y, z, w);
	}

	public static ModQuaternion lerp(ModQuaternion a, ModQuaternion b, float blend)
	{
		ModQuaternion result = new ModQuaternion(0, 0, 0, 1);
		float dot = a.w * b.w + a.x * b.x + a.y * b.y + a.z * b.z;
		float blendI = 1f - blend;

		if (dot < 0)
		{
			result.w = blendI * a.w + blend * -b.w;
			result.x = blendI * a.x + blend * -b.x;
			result.y = blendI * a.y + blend * -b.y;
			result.z = blendI * a.z + blend * -b.z;
		}
		else
		{
			result.w = blendI * a.w + blend * b.w;
			result.x = blendI * a.x + blend * b.x;
			result.y = blendI * a.y + blend * b.y;
			result.z = blendI * a.z + blend * b.z;
		}
		
		result.normalize();
		return result;
	}

	public static ModQuaternion rotate(float degree, Vector3f axis, ModQuaternion src)
	{
		PublicMatrix4f quatmat;
		if (src == null)
		{
			quatmat = new PublicMatrix4f();
		}
		else
		{
			quatmat = src.toRotationMatrix();
		}
		
		PublicMatrix4f rotMat = new PublicMatrix4f();
		rotMat.rotate(degree, axis);
		PublicMatrix4f.mul(quatmat, rotMat,  quatmat);
		return ModQuaternion.fromMatrix(quatmat);
	}
	
	public Quaternion vanilla()
	{
		return new Quaternion(this.x, this.y, this.z, this.w);
	}
	
	@Override
	public String toString()
	{
		return String.format("%f %f %f %f", this.w, this.x, this.y, this.z);
	}
}
