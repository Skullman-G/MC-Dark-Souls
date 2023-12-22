package com.skullmangames.darksouls.core.util.math.vector;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.core.util.math.MathUtils;

import net.minecraft.world.phys.Vec3;

public class ModMatrix4f
{
	public float m00, m01, m02, m03,
				 m10, m11, m12, m13,
				 m20, m21, m22, m23,
				 m30, m31, m32, m33;

	public ModMatrix4f()
	{
		this.setIdentity();
	}

	public ModMatrix4f(final ModMatrix4f src)
	{
		this.load(src);
	}
	
	public ModMatrix4f(Matrix4f mat4f)
	{
		FloatBuffer buf = BufferUtils.createFloatBuffer(16);
		buf.position(0);
		mat4f.store(buf);
		this.load(buf);
	}
	
	@Override
	public String toString()
	{
		return "("+this.m00+" "+this.m01+" "+this.m02+" "+this.m03+")\n"
				+"("+this.m10+" "+this.m11+" "+this.m12+" "+this.m13+")\n"
				+"("+this.m20+" "+this.m21+" "+this.m22+" "+this.m23+")\n"
				+"("+this.m30+" "+this.m31+" "+this.m32+" "+this.m33+")\n";
	}

	public void setIdentity()
	{
		this.m00 = 1.0f;
		this.m01 = 0.0f;
		this.m02 = 0.0f;
		this.m03 = 0.0f;
		this.m10 = 0.0f;
		this.m11 = 1.0f;
		this.m12 = 0.0f;
		this.m13 = 0.0f;
		this.m20 = 0.0f;
		this.m21 = 0.0f;
		this.m22 = 1.0f;
		this.m23 = 0.0f;
		this.m30 = 0.0f;
		this.m31 = 0.0f;
		this.m32 = 0.0f;
		this.m33 = 1.0f;
	}

	public ModMatrix4f load(ModMatrix4f src)
	{
		this.m00 = src.m00;
		this.m01 = src.m01;
		this.m02 = src.m02;
		this.m03 = src.m03;
		this.m10 = src.m10;
		this.m11 = src.m11;
		this.m12 = src.m12;
		this.m13 = src.m13;
		this.m20 = src.m20;
		this.m21 = src.m21;
		this.m22 = src.m22;
		this.m23 = src.m23;
		this.m30 = src.m30;
		this.m31 = src.m31;
		this.m32 = src.m32;
		this.m33 = src.m33;
		
		return this;
	}

	public ModMatrix4f load(FloatBuffer buf)
	{
		buf.position(0);
		this.m00 = buf.get();
		this.m01 = buf.get();
		this.m02 = buf.get();
		this.m03 = buf.get();
		this.m10 = buf.get();
		this.m11 = buf.get();
		this.m12 = buf.get();
		this.m13 = buf.get();
		this.m20 = buf.get();
		this.m21 = buf.get();
		this.m22 = buf.get();
		this.m23 = buf.get();
		this.m30 = buf.get();
		this.m31 = buf.get();
		this.m32 = buf.get();
		this.m33 = buf.get();
		
		return this;
	}

	public ModMatrix4f transpose()
	{
		return transpose(this);
	}

	public ModMatrix4f transpose(ModMatrix4f src)
	{
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

		this.m00 = m00;
		this.m01 = m01;
		this.m02 = m02;
		this.m03 = m03;
		this.m10 = m10;
		this.m11 = m11;
		this.m12 = m12;
		this.m13 = m13;
		this.m20 = m20;
		this.m21 = m21;
		this.m22 = m22;
		this.m23 = m23;
		this.m30 = m30;
		this.m31 = m31;
		this.m32 = m32;
		this.m33 = m33;

		return this;
	}

	public Vec4f transform(Vec4f right)
	{
		return transform(this, right);
	}
	
	public static Vec3 transform(ModMatrix4f matrix, Vec3 src)
	{
		double x = matrix.m00 * src.x + matrix.m10 * src.y + matrix.m20 * src.z + matrix.m30 * 1.0F;
		double y = matrix.m01 * src.x + matrix.m11 * src.y + matrix.m21 * src.z + matrix.m31 * 1.0F;
		double z = matrix.m02 * src.x + matrix.m12 * src.y + matrix.m22 * src.z + matrix.m32 * 1.0F;
		
		return new Vec3(x, y ,z);
	}

	public static Vec4f transform(ModMatrix4f left, Vec4f right)
	{
		float x = left.m00 * right.x + left.m10 * right.y + left.m20 * right.z + left.m30 * right.w;
		float y = left.m01 * right.x + left.m11 * right.y + left.m21 * right.z + left.m31 * right.w;
		float z = left.m02 * right.x + left.m12 * right.y + left.m22 * right.z + left.m32 * right.w;
		float w = left.m03 * right.x + left.m13 * right.y + left.m23 * right.z + left.m33 * right.w;

		return new Vec4f(x, y, z, w);
	}

	public ModMatrix4f scale(Vector3f vec)
	{
		return this.scale(vec.x(), vec.y(), vec.z());
	}

	public ModMatrix4f scale(float x, float y, float z)
	{
		this.m00 *= x;
		this.m01 *= x;
		this.m02 *= x;
		this.m03 *= x;
		this.m10 *= y;
		this.m11 *= y;
		this.m12 *= y;
		this.m13 *= y;
		this.m20 *= y;
		this.m21 *= y;
		this.m22 *= y;
		this.m23 *= y;
		
		return this;
	}
	
	public ModMatrix4f rotateDeg(float angle, Vector3f axis)
	{
		return this.rotate((float)Math.toRadians(angle), axis);
	}

	public ModMatrix4f rotate(float angle, Vector3f axis)
	{
		float c = (float) Math.cos(angle);
		float s = (float) Math.sin(angle);
		float oneminusc = 1.0f - c;
		
		float xx = axis.x() * axis.x();
		float yy = axis.y() * axis.y();
		float zz = axis.z() * axis.z();
		float xy = axis.x() * axis.y();
		float yz = axis.y() * axis.z();
		float xz = axis.x() * axis.z();
		
		float xs = axis.x() * s;
		float ys = axis.y() * s;
		float zs = axis.z() * s;

		float f00 = xx * oneminusc + c;
		float f01 = xy * oneminusc + zs;
		float f02 = xz * oneminusc - ys;

		float f10 = xy * oneminusc - zs;
		float f11 = yy * oneminusc + c;
		float f12 = yz * oneminusc + xs;

		float f20 = xz * oneminusc + ys;
		float f21 = yz * oneminusc - xs;
		float f22 = zz * oneminusc + c;

		float t00 = this.m00 * f00 + this.m10 * f01 + this.m20 * f02;
		float t01 = this.m01 * f00 + this.m11 * f01 + this.m21 * f02;
		float t02 = this.m02 * f00 + this.m12 * f01 + this.m22 * f02;
		float t03 = this.m03 * f00 + this.m13 * f01 + this.m23 * f02;
		float t10 = this.m00 * f10 + this.m10 * f11 + this.m20 * f12;
		float t11 = this.m01 * f10 + this.m11 * f11 + this.m21 * f12;
		float t12 = this.m02 * f10 + this.m12 * f11 + this.m22 * f12;
		float t13 = this.m03 * f10 + this.m13 * f11 + this.m23 * f12;
		
		this.m20 = this.m00 * f20 + this.m10 * f21 + this.m20 * f22;
		this.m21 = this.m01 * f20 + this.m11 * f21 + this.m21 * f22;
		this.m22 = this.m02 * f20 + this.m12 * f21 + this.m22 * f22;
		this.m23 = this.m03 * f20 + this.m13 * f21 + this.m23 * f22;
		
		this.m00 = t00;
		this.m01 = t01;
		this.m02 = t02;
		this.m03 = t03;
		this.m10 = t10;
		this.m11 = t11;
		this.m12 = t12;
		this.m13 = t13;
		
		return this;
	}
	
	public ModMatrix4f mulFront(ModMatrix4f left)
	{
		return ModMatrix4f.mul(left, this, this);
	}
	
	public ModMatrix4f mulBack(ModMatrix4f right)
	{
		return mul(this, right, this);
	}
	
	public static ModMatrix4f mul(ModMatrix4f left, ModMatrix4f right)
	{
		return mul(left, right, null);
	}

	public static ModMatrix4f mul(ModMatrix4f left, ModMatrix4f right, ModMatrix4f dest)
	{
		if (dest == null)
			dest = new ModMatrix4f();

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

	public static ModMatrix4f mulAsOrigin(ModMatrix4f left, ModMatrix4f right, ModMatrix4f dest)
	{
		float x = right.m30;
		float y = right.m31;
		float z = right.m32;
		ModMatrix4f result = mul(left, right, dest);
		result.m30 = x;
		result.m31 = y;
		result.m32 = z;
		return result;
	}

	public static ModMatrix4f mulAsOriginFront(ModMatrix4f left, ModMatrix4f right, ModMatrix4f dest)
	{
		return mulAsOrigin(right, left, dest);
	}

	public static ModMatrix4f invert(ModMatrix4f src, ModMatrix4f dest)
	{
		float determinant = src.determinant();

		if (determinant != 0)
		{
			if (dest == null)
			{
				dest = new ModMatrix4f();
			}

			float determinant_inv = 1f / determinant;

			float t00 = determinant3x3(src.m11, src.m12, src.m13, src.m21, src.m22, src.m23, src.m31, src.m32, src.m33);
			float t01 = -determinant3x3(src.m10, src.m12, src.m13, src.m20, src.m22, src.m23, src.m30, src.m32,
					src.m33);
			float t02 = determinant3x3(src.m10, src.m11, src.m13, src.m20, src.m21, src.m23, src.m30, src.m31, src.m33);
			float t03 = -determinant3x3(src.m10, src.m11, src.m12, src.m20, src.m21, src.m22, src.m30, src.m31,
					src.m32);
			float t10 = -determinant3x3(src.m01, src.m02, src.m03, src.m21, src.m22, src.m23, src.m31, src.m32,
					src.m33);
			float t11 = determinant3x3(src.m00, src.m02, src.m03, src.m20, src.m22, src.m23, src.m30, src.m32, src.m33);
			float t12 = -determinant3x3(src.m00, src.m01, src.m03, src.m20, src.m21, src.m23, src.m30, src.m31,
					src.m33);
			float t13 = determinant3x3(src.m00, src.m01, src.m02, src.m20, src.m21, src.m22, src.m30, src.m31, src.m32);
			float t20 = determinant3x3(src.m01, src.m02, src.m03, src.m11, src.m12, src.m13, src.m31, src.m32, src.m33);
			float t21 = -determinant3x3(src.m00, src.m02, src.m03, src.m10, src.m12, src.m13, src.m30, src.m32,
					src.m33);
			float t22 = determinant3x3(src.m00, src.m01, src.m03, src.m10, src.m11, src.m13, src.m30, src.m31, src.m33);
			float t23 = -determinant3x3(src.m00, src.m01, src.m02, src.m10, src.m11, src.m12, src.m30, src.m31,
					src.m32);
			float t30 = -determinant3x3(src.m01, src.m02, src.m03, src.m11, src.m12, src.m13, src.m21, src.m22,
					src.m23);
			float t31 = determinant3x3(src.m00, src.m02, src.m03, src.m10, src.m12, src.m13, src.m20, src.m22, src.m23);
			float t32 = -determinant3x3(src.m00, src.m01, src.m03, src.m10, src.m11, src.m13, src.m20, src.m21,
					src.m23);
			float t33 = determinant3x3(src.m00, src.m01, src.m02, src.m10, src.m11, src.m12, src.m20, src.m21, src.m22);

			dest.m00 = t00 * determinant_inv;
			dest.m11 = t11 * determinant_inv;
			dest.m22 = t22 * determinant_inv;
			dest.m33 = t33 * determinant_inv;
			dest.m01 = t10 * determinant_inv;
			dest.m10 = t01 * determinant_inv;
			dest.m20 = t02 * determinant_inv;
			dest.m02 = t20 * determinant_inv;
			dest.m12 = t21 * determinant_inv;
			dest.m21 = t12 * determinant_inv;
			dest.m03 = t30 * determinant_inv;
			dest.m30 = t03 * determinant_inv;
			dest.m13 = t31 * determinant_inv;
			dest.m31 = t13 * determinant_inv;
			dest.m32 = t23 * determinant_inv;
			dest.m23 = t32 * determinant_inv;
			return dest;
		} else
		{
			return null;
		}
	}

	public float determinant()
	{
		float f = m00 * ((m11 * m22 * m33 + m12 * m23 * m31 + m13 * m21 * m32) - m13 * m22 * m31 - m11 * m23 * m32
				- m12 * m21 * m33);
		f -= m01 * ((m10 * m22 * m33 + m12 * m23 * m30 + m13 * m20 * m32) - m13 * m22 * m30 - m10 * m23 * m32
				- m12 * m20 * m33);
		f += m02 * ((m10 * m21 * m33 + m11 * m23 * m30 + m13 * m20 * m31) - m13 * m21 * m30 - m10 * m23 * m31
				- m11 * m20 * m33);
		f -= m03 * ((m10 * m21 * m32 + m11 * m22 * m30 + m12 * m20 * m31) - m12 * m21 * m30 - m10 * m22 * m31
				- m11 * m20 * m32);

		return f;
	}

	private static float determinant3x3(float t00, float t01, float t02, float t10, float t11, float t12, float t20,
			float t21, float t22)
	{
		return t00 * (t11 * t22 - t12 * t21) + t01 * (t12 * t20 - t10 * t22) + t02 * (t10 * t21 - t11 * t20);
	}
	
	public ModMatrix4f translate(Vec3 vec)
	{
		return this.translate((float)vec.x, (float)vec.y, (float)vec.z);
	}

	public ModMatrix4f translate(Vector3f vec)
	{
		return this.translate(vec.x(), vec.y(), vec.z());
	}
	
	public ModMatrix4f translate(float x, float y, float z)
	{
		this.m30 += this.m00 * x + this.m10 * y + this.m20 * z;
		this.m31 += this.m01 * x + this.m11 * y + this.m21 * z;
		this.m32 += this.m02 * x + this.m12 * y + this.m22 * z;
		this.m33 += this.m03 * x + this.m13 * y + this.m23 * z;

		return this;
	}

	public static ModMatrix4f createModelMatrix(float prevPosX, float posX, float prevPosY, float posY,
			float prevPosZ, float posZ, float prevPitch, float pitch, float prevYaw, float yaw, float partialTick,
			float scaleX, float scaleY, float scaleZ)
	{
		ModMatrix4f modelMatrix = new ModMatrix4f();
		Vector3f entityPosition = new Vector3f(-(prevPosX + (posX - prevPosX) * partialTick),
				((prevPosY + (posY - prevPosY) * partialTick)), -(prevPosZ + (posZ - prevPosZ) * partialTick));

		modelMatrix.translate(entityPosition);
		float pitchDegree = MathUtils.interpolateRotation(prevPitch, pitch, partialTick);
		float yawDegree = MathUtils.interpolateRotation(prevYaw, yaw, partialTick);
		modelMatrix.rotate((float) -Math.toRadians(yawDegree), Vector3f.YP);
		modelMatrix.rotate((float) -Math.toRadians(pitchDegree), Vector3f.XP);
		modelMatrix.scale(scaleX, scaleY, scaleZ);

		return modelMatrix;
	}

	public static void rotateStack(PoseStack mStack, ModMatrix4f mat)
	{
		mStack.mulPose(toQuaternion(mat).vanilla());
	}

	public static void scaleStack(PoseStack mStack, ModMatrix4f mat)
	{
		Vector3f vector = getScaleFromMatrix(mat);
		mStack.scale(vector.x(), vector.y(), vector.z());
	}

	public ModMatrix4f removeTranslation()
	{
		ModMatrix4f copy = new ModMatrix4f(this);
		copy.m30 = 0.0F;
		copy.m31 = 0.0F;
		copy.m32 = 0.0F;

		return copy;
	}

	public static ModMatrix4f createRotatorDeg(float angle, Vector3f axis)
	{
		return new ModMatrix4f().rotate((float)Math.toRadians(angle), axis);
	}

	public static Vector3f transform3v(ModMatrix4f matrix, Vector3f src)
	{
		Vec4f v = transform(matrix, new Vec4f(src.x(), src.y(), src.z(), 1.0F));
		return new Vector3f(v.x, v.y, v.z);
	}

	private static Vector3f getScaleFromMatrix(ModMatrix4f mat)
	{
		Vector3f a = new Vector3f(mat.m00, mat.m10, mat.m20);
		Vector3f b = new Vector3f(mat.m01, mat.m11, mat.m21);
		Vector3f c = new Vector3f(mat.m02, mat.m12, mat.m22);
		return new Vector3f(Vector3fHelper.length(a), Vector3fHelper.length(b), Vector3fHelper.length(c));
	}
	
	public static void translateStack(PoseStack mStack, ModMatrix4f mat)
	{
		Vector3f vector = mat.toTranslationVector();
		mStack.translate(vector.x(), vector.y(), vector.z());
	}

	public static Matrix4f exportMatrix(ModMatrix4f visibleMat)
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

	public static ModMatrix4f fromQuaternion(ModQuaternion quaternion)
	{
		ModMatrix4f matrix = new ModMatrix4f();
		float x = quaternion.x;
		float y = quaternion.y;
		float z = quaternion.z;
		float w = quaternion.w;
		float xy = x * y;
		float xz = x * z;
		float xw = x * w;
		float yz = y * z;
		float yw = y * w;
		float zw = z * w;
		float xSquared = 2F * x * x;
		float ySquared = 2F * y * y;
		float zSquared = 2F * z * z;
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

	public Vector3f toTranslationVector()
	{
		return toVector(this);
	}

	public static Vector3f toVector(ModMatrix4f matrix)
	{
		return new Vector3f(matrix.m30, matrix.m31, matrix.m32);
	}

	public ModQuaternion toQuaternion()
	{
		return ModMatrix4f.toQuaternion(this);
	}

	public static ModQuaternion toQuaternion(ModMatrix4f matrix)
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
		} else if ((matrix.m00 > matrix.m11) && (matrix.m00 > matrix.m22))
		{
			float x4 = (float) (Math.sqrt(1f + matrix.m00 - matrix.m11 - matrix.m22) * 2f);
			w = (matrix.m21 - matrix.m12) / x4;
			x = x4 / 4f;
			y = (matrix.m01 + matrix.m10) / x4;
			z = (matrix.m02 + matrix.m20) / x4;
		} else if (matrix.m11 > matrix.m22)
		{
			float y4 = (float) (Math.sqrt(1f + matrix.m11 - matrix.m00 - matrix.m22) * 2f);
			w = (matrix.m02 - matrix.m20) / y4;
			x = (matrix.m01 + matrix.m10) / y4;
			y = y4 / 4f;
			z = (matrix.m12 + matrix.m21) / y4;
		} else
		{
			float z4 = (float) (Math.sqrt(1f + matrix.m22 - matrix.m00 - matrix.m11) * 2f);
			w = (matrix.m10 - matrix.m01) / z4;
			x = (matrix.m02 + matrix.m20) / z4;
			y = (matrix.m12 + matrix.m21) / z4;
			z = z4 / 4f;
		}
		return new ModQuaternion(x, y, z, w);
	}

	public Vector3f toScaleVector()
	{
		return new Vector3f(Vector3fHelper.length(new Vector3f(this.m00, this.m01, this.m02)),
				Vector3fHelper.length(new Vector3f(this.m10, this.m11, this.m12)),
				Vector3fHelper.length(new Vector3f(this.m20, this.m21, this.m22)));
	}
}
