package com.skullmangames.darksouls.common.animation;

import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

import net.minecraft.util.math.MathHelper;

import com.skullmangames.darksouls.core.util.math.MathUtils;

public class JointTransform
{
	private Vector3f translation;
	private Vector3f scale;
	private Quaternion rotation;

	public JointTransform(Vector3f position, Quaternion rotation, Vector3f scale)
	{
		this.translation = position;
		this.rotation = rotation;
		this.scale = scale;
	}

	public Vector3f translation()
	{
		return translation;
	}

	public Quaternion rotation()
	{
		return rotation;
	}

	public Vector3f scale()
	{
		return scale;
	}

	public JointTransform copy()
	{
		return JointTransform.empty().copyFrom(this);
	}

	public JointTransform copyFrom(JointTransform jt)
	{
		Vector3f newV = jt.translation();
		Quaternion newQ = jt.rotation();
		Vector3f newS = jt.scale;
		this.translation.set(newV.x(), newV.y(), newV.z());
		this.rotation.set(newQ.i(), newQ.j(), newQ.k(), newQ.r());
		this.scale.set(newS.x(), newS.y(), newS.z());

		return this;
	}
	
	public PublicMatrix4f getParentboundMatrix(Joint joint, PublicMatrix4f parentTransform)
	{
		return this.toMatrix().mulFront(joint.getLocalTransform()).mulFront(parentTransform).mulBack(joint.getAnimatedTransform());
	}

	public PublicMatrix4f toMatrix()
	{
		PublicMatrix4f matrix = new PublicMatrix4f()
				.translate(this.translation).mulBack(PublicMatrix4f.fromQuaternion(this.rotation)).scale(this.scale);
		return matrix;
	}

	@Override
	public String toString()
	{
		return String.format("%s %s", this.translation, this.rotation);
	}

	private static JointTransform interpolateSimple(JointTransform prev, JointTransform next, float progression)
	{
		return new JointTransform(MathUtils.lerpVector(prev.translation, next.translation, progression),
				MathUtils.lerpQuaternion(prev.rotation, next.rotation, progression),
				MathUtils.lerpVector(prev.scale, next.scale, progression));
	}

	public static JointTransform interpolate(JointTransform prev, JointTransform next, float progression)
	{
		if (prev == null || next == null) return JointTransform.empty();

		progression = MathHelper.clamp(progression, 0.0F, 1.0F);
		JointTransform interpolated = interpolateSimple(prev, next, progression);

		return interpolated;
	}

	public static JointTransform fromMatrixNoScale(PublicMatrix4f matrix)
	{
		return new JointTransform(matrix.toTranslationVector(), matrix.toQuaternion(), new Vector3f(1.0F, 1.0F, 1.0F));
	}

	public static JointTransform getTranslation(Vector3f vec)
	{
		return JointTransform.translationRotation(vec, new Quaternion(0.0F, 0.0F, 0.0F, 1.0F));
	}

	public static JointTransform getRotation(Quaternion quat)
	{
		return JointTransform.translationRotation(new Vector3f(0.0F, 0.0F, 0.0F), quat);
	}

	public static JointTransform getScale(Vector3f vec)
	{
		return new JointTransform(new Vector3f(1.0F, 1.0F, 1.0F), new Quaternion(0.0F, 0.0F, 0.0F, 1.0F), vec);
	}

	public static JointTransform fromMatrix(PublicMatrix4f matrix)
	{
		return new JointTransform(matrix.toTranslationVector(), matrix.toQuaternion(), matrix.toScaleVector());
	}

	public static JointTransform translationRotation(Vector3f vec, Quaternion quat)
	{
		return new JointTransform(vec, quat, new Vector3f(1.0F, 1.0F, 1.0F));
	}

	public static JointTransform empty()
	{
		return new JointTransform(new Vector3f(0.0F, 0.0F, 0.0F), new Quaternion(0.0F, 0.0F, 0.0F, 1.0F),
				new Vector3f(1.0F, 1.0F, 1.0F));
	}
}