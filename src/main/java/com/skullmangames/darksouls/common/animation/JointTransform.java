package com.skullmangames.darksouls.common.animation;

import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.core.util.math.vector.ModQuaternion;
import com.skullmangames.darksouls.core.util.math.vector.ModMatrix4f;

import net.minecraft.util.Mth;

import com.skullmangames.darksouls.core.util.math.ModMath;

public class JointTransform
{
	private Vector3f translation;
	private Vector3f scale;
	private ModQuaternion rotation;

	public JointTransform(Vector3f position, ModQuaternion rotation, Vector3f scale)
	{
		this.translation = position;
		this.rotation = rotation;
		this.scale = scale;
	}

	public Vector3f translation()
	{
		return translation;
	}

	public ModQuaternion rotation()
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
		ModQuaternion newQ = jt.rotation();
		Vector3f newS = jt.scale;
		this.translation.set(newV.x(), newV.y(), newV.z());
		this.rotation.set(newQ);
		this.scale.set(newS.x(), newS.y(), newS.z());

		return this;
	}
	
	public ModMatrix4f getParentboundMatrix(Joint joint, ModMatrix4f parentTransform)
	{
		return this.toMatrix().mulFront(joint.getLocalTransform()).mulFront(parentTransform).mulBack(joint.getAnimatedTransform());
	}

	public ModMatrix4f toMatrix()
	{
		ModMatrix4f matrix = new ModMatrix4f()
				.translate(this.translation).mulBack(ModMatrix4f.fromQuaternion(this.rotation)).scale(this.scale);
		return matrix;
	}

	@Override
	public String toString()
	{
		return String.format("%s %s", this.translation, this.rotation);
	}

	private static JointTransform interpolateSimple(JointTransform prev, JointTransform next, float progression)
	{
		return new JointTransform(ModMath.lerpVector(prev.translation, next.translation, progression),
				ModQuaternion.lerp(prev.rotation, next.rotation, progression),
				ModMath.lerpVector(prev.scale, next.scale, progression));
	}

	public static JointTransform interpolate(JointTransform prev, JointTransform next, float progression)
	{
		if (prev == null || next == null) return JointTransform.empty();

		progression = Mth.clamp(progression, 0.0F, 1.0F);
		JointTransform interpolated = interpolateSimple(prev, next, progression);

		return interpolated;
	}

	public static JointTransform fromMatrixNoScale(ModMatrix4f matrix)
	{
		return new JointTransform(matrix.toTranslationVector(), matrix.toQuaternion(), new Vector3f(1.0F, 1.0F, 1.0F));
	}

	public static JointTransform getTranslation(Vector3f vec)
	{
		return JointTransform.translationRotation(vec, new ModQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
	}

	public static JointTransform getRotation(ModQuaternion quat)
	{
		return JointTransform.translationRotation(new Vector3f(0.0F, 0.0F, 0.0F), quat);
	}

	public static JointTransform getScale(Vector3f vec)
	{
		return new JointTransform(new Vector3f(1.0F, 1.0F, 1.0F), new ModQuaternion(0.0F, 0.0F, 0.0F, 1.0F), vec);
	}

	public static JointTransform fromMatrix(ModMatrix4f matrix)
	{
		return new JointTransform(matrix.toTranslationVector(), matrix.toQuaternion(), matrix.toScaleVector());
	}

	public static JointTransform translationRotation(Vector3f vec, ModQuaternion quat)
	{
		return new JointTransform(vec, quat, new Vector3f(1.0F, 1.0F, 1.0F));
	}

	public static JointTransform empty()
	{
		return new JointTransform(new Vector3f(0.0F, 0.0F, 0.0F), new ModQuaternion(0.0F, 0.0F, 0.0F, 1.0F),
				new Vector3f(1.0F, 1.0F, 1.0F));
	}
}