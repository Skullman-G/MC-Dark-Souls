package com.skullmangames.darksouls.core.util.math.vector;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;

public class PublicMatrix4f
{
	public float m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33;
	
	public PublicMatrix4f()
	{
		this.setIdentity();
	}
	
	public PublicMatrix4f(final PublicMatrix4f src)
	{
		load(src);
	}
	
	public PublicMatrix4f setIdentity()
	{
		return setIdentity(this);
	}
	
	public static PublicMatrix4f setIdentity(PublicMatrix4f m)
	{
		m.m00 = 1.0f;
		m.m01 = 0.0f;
		m.m02 = 0.0f;
		m.m03 = 0.0f;
		m.m10 = 0.0f;
		m.m11 = 1.0f;
		m.m12 = 0.0f;
		m.m13 = 0.0f;
		m.m20 = 0.0f;
		m.m21 = 0.0f;
		m.m22 = 1.0f;
		m.m23 = 0.0f;
		m.m30 = 0.0f;
		m.m31 = 0.0f;
		m.m32 = 0.0f;
		m.m33 = 1.0f;

		return m;
	}
	
	public PublicMatrix4f load(PublicMatrix4f src)
	{
		return load(src, this);
	}

	public static PublicMatrix4f load(PublicMatrix4f src, PublicMatrix4f dest)
	{
		if (dest == null) dest = new PublicMatrix4f();
		dest.m00 = src.m00;
		dest.m01 = src.m01;
		dest.m02 = src.m02;
		dest.m03 = src.m03;
		dest.m10 = src.m10;
		dest.m11 = src.m11;
		dest.m12 = src.m12;
		dest.m13 = src.m13;
		dest.m20 = src.m20;
		dest.m21 = src.m21;
		dest.m22 = src.m22;
		dest.m23 = src.m23;
		dest.m30 = src.m30;
		dest.m31 = src.m31;
		dest.m32 = src.m32;
		dest.m33 = src.m33;

		return dest;
	}
	
	public static PublicMatrix4f load(PublicMatrix4f mat, FloatBuffer buf)
	{
		if (mat == null) mat = new PublicMatrix4f();
		buf.position(0);
		mat.m00 = buf.get();
		mat.m01 = buf.get();
		mat.m02 = buf.get();
		mat.m03 = buf.get();
		mat.m10 = buf.get();
		mat.m11 = buf.get();
		mat.m12 = buf.get();
		mat.m13 = buf.get();
		mat.m20 = buf.get();
		mat.m21 = buf.get();
		mat.m22 = buf.get();
		mat.m23 = buf.get();
		mat.m30 = buf.get();
		mat.m31 = buf.get();
		mat.m32 = buf.get();
		mat.m33 = buf.get();
		return mat;
	}
	
	public PublicMatrix4f load(FloatBuffer buf)
	{
		return PublicMatrix4f.load(this, buf);
	}
	
	public PublicMatrix4f transpose()
	{
		return transpose(this);
	}
	
	public PublicMatrix4f transpose(PublicMatrix4f dest)
	{
		return transpose(this, dest);
	}
	
	public static PublicMatrix4f transpose(PublicMatrix4f src, PublicMatrix4f dest)
	{
		if (dest == null)
		   dest = new PublicMatrix4f();
		float m00 = src.m00;
		float m01 = src.m10;
		float m02 = src.m20;
		float m03 = src.m30;
		float m10 = src.m01;
		float m11 = src.m11;
		float m12 = src.m21;
		float m13 = src.m31;
		float m20 = src.m02;
		float m21 = src.m12;
		float m22 = src.m22;
		float m23 = src.m32;
		float m30 = src.m03;
		float m31 = src.m13;
		float m32 = src.m23;
		float m33 = src.m33;

		dest.m00 = m00;
		dest.m01 = m01;
		dest.m02 = m02;
		dest.m03 = m03;
		dest.m10 = m10;
		dest.m11 = m11;
		dest.m12 = m12;
		dest.m13 = m13;
		dest.m20 = m20;
		dest.m21 = m21;
		dest.m22 = m22;
		dest.m23 = m23;
		dest.m30 = m30;
		dest.m31 = m31;
		dest.m32 = m32;
		dest.m33 = m33;

		return dest;
	}
	
	public static Vector4f transform(PublicMatrix4f left, Vector4f right)
	{
		return transform(left, right, new Vector4f());
	}
	
	public static Vector4f transform(PublicMatrix4f left, Vector4f right, Vector4f dest)
	{
		if (dest == null)
			dest = new Vector4f();

		float x = left.m00 * right.x() + left.m10 * right.y() + left.m20 * right.z() + left.m30 * right.w();
		float y = left.m01 * right.x() + left.m11 * right.y() + left.m21 * right.z() + left.m31 * right.w();
		float z = left.m02 * right.x() + left.m12 * right.y() + left.m22 * right.z() + left.m32 * right.w();
		float w = left.m03 * right.x() + left.m13 * right.y() + left.m23 * right.z() + left.m33 * right.w();

		dest.setX(x);
		dest.setY(y);
		dest.setZ(z);
		dest.setW(w);

		return dest;
	}
	
	public PublicMatrix4f scale(Vector3f vec)
	{
		return scale(vec, this);
	}
	
	public PublicMatrix4f scale(Vector3f vec, PublicMatrix4f dest)
	{
		return scale(vec, this, dest);
	}
	
	public static PublicMatrix4f scale(Vector3f vec, PublicMatrix4f src, PublicMatrix4f dest)
	{
		return scale(vec.x(), vec.y(), vec.z(), src, dest);
	}
	
	public PublicMatrix4f scale(float x, float y, float z)
	{
		return scale(x, y, z, this, this);
	}
	
	public static PublicMatrix4f scale(float x, float y, float z, PublicMatrix4f src, PublicMatrix4f dest)
	{
		if (dest == null) dest = new PublicMatrix4f();
		
		dest.m00 = src.m00 * x;
		dest.m01 = src.m01 * x;
		dest.m02 = src.m02 * x;
		dest.m03 = src.m03 * x;
		dest.m10 = src.m10 * y;
		dest.m11 = src.m11 * y;
		dest.m12 = src.m12 * y;
		dest.m13 = src.m13 * y;
		dest.m20 = src.m20 * y;
		dest.m21 = src.m21 * y;
		dest.m22 = src.m22 * y;
		dest.m23 = src.m23 * y;
		return dest;
	}
	
	public PublicMatrix4f rotate(float angle, Vector3f axis)
	{
		return this.rotate(angle, axis, this);
	}
	
	public PublicMatrix4f rotate(float angle, Vector3f axis, PublicMatrix4f dest)
	{
		return rotate(angle, axis, this, dest);
	}
	
	public static PublicMatrix4f rotate(float angle, Vector3f axis, PublicMatrix4f src, PublicMatrix4f dest)
	{
		if (dest == null)
		{
			dest = new PublicMatrix4f();
		}
		
		float c = (float) Math.cos(angle);
		float s = (float) Math.sin(angle);
		float oneminusc = 1.0f - c;
		float xy = axis.x() * axis.y();
		float yz = axis.y() * axis.z();
		float xz = axis.x() * axis.z();
		float xs = axis.x() * s;
		float ys = axis.y() * s;
		float zs = axis.z() * s;

		float f00 = axis.x() * axis.x()  *oneminusc + c;
		float f01 = xy * oneminusc + zs;
		float f02 = xz * oneminusc - ys;
		
		float f10 = xy * oneminusc - zs;
		float f11 = axis.y() * axis.y() * oneminusc + c;
		float f12 = yz * oneminusc + xs;
		
		float f20 = xz * oneminusc + ys;
		float f21 = yz * oneminusc - xs;
		float f22 = axis.z() * axis.z() * oneminusc + c;

		float t00 = src.m00 * f00 + src.m10 * f01 + src.m20 * f02;
		float t01 = src.m01 * f00 + src.m11 * f01 + src.m21 * f02;
		float t02 = src.m02 * f00 + src.m12 * f01 + src.m22 * f02;
		float t03 = src.m03 * f00 + src.m13 * f01 + src.m23 * f02;
		float t10 = src.m00 * f10 + src.m10 * f11 + src.m20 * f12;
		float t11 = src.m01 * f10 + src.m11 * f11 + src.m21 * f12;
		float t12 = src.m02 * f10 + src.m12 * f11 + src.m22 * f12;
		float t13 = src.m03 * f10 + src.m13 * f11 + src.m23 * f12;
		dest.m20 = src.m00 * f20 + src.m10 * f21 + src.m20 * f22;
		dest.m21 = src.m01 * f20 + src.m11 * f21 + src.m21 * f22;
		dest.m22 = src.m02 * f20 + src.m12 * f21 + src.m22 * f22;
		dest.m23 = src.m03 * f20 + src.m13 * f21 + src.m23 * f22;
		dest.m00 = t00;
		dest.m01 = t01;
		dest.m02 = t02;
		dest.m03 = t03;
		dest.m10 = t10;
		dest.m11 = t11;
		dest.m12 = t12;
		dest.m13 = t13;
		return dest;
	}
	
	public static PublicMatrix4f mul(PublicMatrix4f left, PublicMatrix4f right, PublicMatrix4f dest)
	{
		if (dest == null)
		{
			dest = new PublicMatrix4f();
		}

		float m00 = left.m00 * right.m00 + left.m10 * right.m01 + left.m20 * right.m02 + left.m30 * right.m03;
		float m01 = left.m01 * right.m00 + left.m11 * right.m01 + left.m21 * right.m02 + left.m31 * right.m03;
		float m02 = left.m02 * right.m00 + left.m12 * right.m01 + left.m22 * right.m02 + left.m32 * right.m03;
		float m03 = left.m03 * right.m00 + left.m13 * right.m01 + left.m23 * right.m02 + left.m33 * right.m03;
		float m10 = left.m00 * right.m10 + left.m10 * right.m11 + left.m20 * right.m12 + left.m30 * right.m13;
		float m11 = left.m01 * right.m10 + left.m11 * right.m11 + left.m21 * right.m12 + left.m31 * right.m13;
		float m12 = left.m02 * right.m10 + left.m12 * right.m11 + left.m22 * right.m12 + left.m32 * right.m13;
		float m13 = left.m03 * right.m10 + left.m13 * right.m11 + left.m23 * right.m12 + left.m33 * right.m13;
		float m20 = left.m00 * right.m20 + left.m10 * right.m21 + left.m20 * right.m22 + left.m30 * right.m23;
		float m21 = left.m01 * right.m20 + left.m11 * right.m21 + left.m21 * right.m22 + left.m31 * right.m23;
		float m22 = left.m02 * right.m20 + left.m12 * right.m21 + left.m22 * right.m22 + left.m32 * right.m23;
		float m23 = left.m03 * right.m20 + left.m13 * right.m21 + left.m23 * right.m22 + left.m33 * right.m23;
		float m30 = left.m00 * right.m30 + left.m10 * right.m31 + left.m20 * right.m32 + left.m30 * right.m33;
		float m31 = left.m01 * right.m30 + left.m11 * right.m31 + left.m21 * right.m32 + left.m31 * right.m33;
		float m32 = left.m02 * right.m30 + left.m12 * right.m31 + left.m22 * right.m32 + left.m32 * right.m33;
		float m33 = left.m03 * right.m30 + left.m13 * right.m31 + left.m23 * right.m32 + left.m33 * right.m33;
		
		dest.m00 = m00;
		dest.m01 = m01;
		dest.m02 = m02;
		dest.m03 = m03;
		dest.m10 = m10;
		dest.m11 = m11;
		dest.m12 = m12;
		dest.m13 = m13;
		dest.m20 = m20;
		dest.m21 = m21;
		dest.m22 = m22;
		dest.m23 = m23;
		dest.m30 = m30;
		dest.m31 = m31;
		dest.m32 = m32;
		dest.m33 = m33;

		return dest;
	}
	
	public static PublicMatrix4f invert(PublicMatrix4f src, PublicMatrix4f dest)
	{
		float determinant = src.determinant();

		if (determinant != 0)
		{
			if (dest == null)
			{
				dest = new PublicMatrix4f();
			}
			
			float determinant_inv = 1f/determinant;

			float t00 =  determinant3x3(src.m11, src.m12, src.m13, src.m21, src.m22, src.m23, src.m31, src.m32, src.m33);
			float t01 = -determinant3x3(src.m10, src.m12, src.m13, src.m20, src.m22, src.m23, src.m30, src.m32, src.m33);
			float t02 =  determinant3x3(src.m10, src.m11, src.m13, src.m20, src.m21, src.m23, src.m30, src.m31, src.m33);
			float t03 = -determinant3x3(src.m10, src.m11, src.m12, src.m20, src.m21, src.m22, src.m30, src.m31, src.m32);
			float t10 = -determinant3x3(src.m01, src.m02, src.m03, src.m21, src.m22, src.m23, src.m31, src.m32, src.m33);
			float t11 =  determinant3x3(src.m00, src.m02, src.m03, src.m20, src.m22, src.m23, src.m30, src.m32, src.m33);
			float t12 = -determinant3x3(src.m00, src.m01, src.m03, src.m20, src.m21, src.m23, src.m30, src.m31, src.m33);
			float t13 =  determinant3x3(src.m00, src.m01, src.m02, src.m20, src.m21, src.m22, src.m30, src.m31, src.m32);
			float t20 =  determinant3x3(src.m01, src.m02, src.m03, src.m11, src.m12, src.m13, src.m31, src.m32, src.m33);
			float t21 = -determinant3x3(src.m00, src.m02, src.m03, src.m10, src.m12, src.m13, src.m30, src.m32, src.m33);
			float t22 =  determinant3x3(src.m00, src.m01, src.m03, src.m10, src.m11, src.m13, src.m30, src.m31, src.m33);
			float t23 = -determinant3x3(src.m00, src.m01, src.m02, src.m10, src.m11, src.m12, src.m30, src.m31, src.m32);
			float t30 = -determinant3x3(src.m01, src.m02, src.m03, src.m11, src.m12, src.m13, src.m21, src.m22, src.m23);
			float t31 =  determinant3x3(src.m00, src.m02, src.m03, src.m10, src.m12, src.m13, src.m20, src.m22, src.m23);
			float t32 = -determinant3x3(src.m00, src.m01, src.m03, src.m10, src.m11, src.m13, src.m20, src.m21, src.m23);
			float t33 =  determinant3x3(src.m00, src.m01, src.m02, src.m10, src.m11, src.m12, src.m20, src.m21, src.m22);

			dest.m00 = t00*determinant_inv;
			dest.m11 = t11*determinant_inv;
			dest.m22 = t22*determinant_inv;
			dest.m33 = t33*determinant_inv;
			dest.m01 = t10*determinant_inv;
			dest.m10 = t01*determinant_inv;
			dest.m20 = t02*determinant_inv;
			dest.m02 = t20*determinant_inv;
			dest.m12 = t21*determinant_inv;
			dest.m21 = t12*determinant_inv;
			dest.m03 = t30*determinant_inv;
			dest.m30 = t03*determinant_inv;
			dest.m13 = t31*determinant_inv;
			dest.m31 = t13*determinant_inv;
			dest.m32 = t23*determinant_inv;
			dest.m23 = t32*determinant_inv;
			return dest;
		}
		else
		{
			return null;
		}
	}
	
	public float determinant()
	{
		float f = m00 * ((m11 * m22 * m33 + m12 * m23 * m31 + m13 * m21 * m32) - m13 * m22 * m31 - m11 * m23 * m32 - m12 * m21 * m33);
		f -= m01 * ((m10 * m22 * m33 + m12 * m23 * m30 + m13 * m20 * m32) - m13 * m22 * m30 - m10 * m23 * m32 - m12 * m20 * m33);
		f += m02 * ((m10 * m21 * m33 + m11 * m23 * m30 + m13 * m20 * m31) - m13 * m21 * m30	- m10 * m23 * m31 - m11 * m20 * m33);
		f -= m03 * ((m10 * m21 * m32 + m11 * m22 * m30 + m12 * m20 * m31) - m12 * m21 * m30 - m10 * m22 * m31 - m11 * m20 * m32);
		
		return f;
	}
	
	private static float determinant3x3(float t00, float t01, float t02, float t10, float t11, float t12, float t20, float t21, float t22)
	{
		return t00 * (t11 * t22 - t12 * t21) + t01 * (t12 * t20 - t10 * t22) + t02 * (t10 * t21 - t11 * t20);
	}
	
	public PublicMatrix4f translate(Vector3f vec)
	{
		return this.translate(vec, this);
	}
	
	public PublicMatrix4f translate(Vector3f vec, PublicMatrix4f dest)
	{
		return translate(vec, this, dest);
	}
	
	public static PublicMatrix4f translate(Vector3f vec, PublicMatrix4f src, PublicMatrix4f dest)
	{
		if (dest == null) dest = new PublicMatrix4f();

		dest.m30 += src.m00 * vec.x() + src.m10 * vec.y() + src.m20 * vec.z();
		dest.m31 += src.m01 * vec.x() + src.m11 * vec.y() + src.m21 * vec.z();
		dest.m32 += src.m02 * vec.x() + src.m12 * vec.y() + src.m22 * vec.z();
		dest.m33 += src.m03 * vec.x() + src.m13 * vec.y() + src.m23 * vec.z();

		return dest;
	}
	
	public static PublicMatrix4f getModelMatrixIntegrated(float prevPosX, float posX, float prevPosY, float posY, float prevPosZ, float posZ, float prevPitch, float pitch, float prevYaw, float yaw, float partialTick, float scaleX, float scaleY, float scaleZ)
	{
		PublicMatrix4f modelMatrix = new PublicMatrix4f().setIdentity();
		Vector3f entityPosition = new Vector3f(-(prevPosX + (posX - prevPosX) * partialTick), ((prevPosY + (posY - prevPosY) * partialTick)), -(prevPosZ + (posZ - prevPosZ) * partialTick));
		
		PublicMatrix4f.translate(entityPosition, modelMatrix, modelMatrix);
		float pitchDegree = interpolateRotation(prevPitch, pitch, partialTick);
		float yawDegree = interpolateRotation(prevYaw, yaw, partialTick);
		PublicMatrix4f.rotate((float) -Math.toRadians(yawDegree), new Vector3f(0, 1, 0), modelMatrix, modelMatrix);
		PublicMatrix4f.rotate((float) -Math.toRadians(pitchDegree), new Vector3f(1, 0, 0), modelMatrix, modelMatrix);
		PublicMatrix4f.scale(scaleX, scaleY, scaleZ, modelMatrix, modelMatrix);
		
		return modelMatrix;
	}
	
	public static float interpolateRotation(float par1, float par2, float par3)
	{
		float f = 0;

		for (f = par2 - par1; f < -180.0F; f += 360.0F)
		{
			;
		}

		while (f >= 180.0F)
		{
			f -= 360.0F;
		}

		return par1 + par3 * f;
	}
	
	public static void rotateStack(PoseStack mStack, PublicMatrix4f mat)
	{
		mStack.mulPose(getQuaternionFromMatrix(mat));
	}
	
	public static void scaleStack(PoseStack mStack, PublicMatrix4f mat)
	{
		Vector3f vector = getScaleFromMatrix(mat);
		mStack.scale(vector.x(), vector.y(), vector.z());
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
	
	private static Vector3f getScaleFromMatrix(PublicMatrix4f mat)
	{
		Vector3f a = new Vector3f(mat.m00, mat.m10, mat.m20);
		Vector3f b = new Vector3f(mat.m01, mat.m11, mat.m21);
		Vector3f c = new Vector3f(mat.m02, mat.m12, mat.m22);
		return new Vector3f(Vector3fHelper.length(a), Vector3fHelper.length(b), Vector3fHelper.length(c));
	}
	
	public static PublicMatrix4f importMatrix(Matrix4f mat4f)
	{
		FloatBuffer buf = BufferUtils.createFloatBuffer(16);
		buf.position(0);
		mat4f.store(buf);
		return PublicMatrix4f.load(null, buf);
	}
	
	public static Matrix4f exportMatrix(PublicMatrix4f visibleMat)
	{
		float[] arr = new float[16];
		arr[0] = visibleMat.m00;
		arr[1] = visibleMat.m10;
		arr[2] = visibleMat.m20;
		arr[3] = visibleMat.m30;
		arr[4] = visibleMat.m01;
		arr[5] = visibleMat.m11;
		arr[6] = visibleMat.m21;
		arr[7] = visibleMat.m31;
		arr[8] = visibleMat.m02;
		arr[9] = visibleMat.m12;
		arr[10] = visibleMat.m22;
		arr[11] = visibleMat.m32;
		arr[12] = visibleMat.m03;
		arr[13] = visibleMat.m13;
		arr[14] = visibleMat.m23;
		arr[15] = visibleMat.m33;
		
		return new Matrix4f(arr);
	}
}
