package com.skullmangames.darksouls.client.animation;

import java.util.Map;

import com.mojang.datafixers.util.Pair;
import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.common.animation.Joint;
import com.skullmangames.darksouls.common.animation.JointTransform;
import com.skullmangames.darksouls.common.animation.Pose;
import com.skullmangames.darksouls.common.animation.types.DynamicAnimation;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class JointMask
{
	@OnlyIn(Dist.CLIENT)
	@FunctionalInterface
	public static interface BindModifier
	{
		public void modify(ClientAnimator clientAnimator, Pose resultPose, AnimationLayer.Priority priority, Joint joint,
				Map<AnimationLayer.Priority, Pair<DynamicAnimation, Pose>> poses);
	}

	public static final BindModifier ROOT_COMBINE = (clientAnimator, result, priority, joint, poses) ->
	{
		Pose lowestPose = poses.get(AnimationLayer.Priority.LOWEST).getSecond();
		Pose currentPose = poses.get(priority).getSecond();
		JointTransform lowestTransform = lowestPose.getJointTransformData().getOrDefault(joint.getName(),
				JointTransform.empty());
		JointTransform currentTransform = currentPose.getJointTransformData().getOrDefault(joint.getName(),
				JointTransform.empty());
		result.getJointTransformData().get(joint.getName()).translation().setY(lowestTransform.translation().y());
		PublicMatrix4f lowestMatrix = lowestTransform.toMatrix();
		PublicMatrix4f currentMatrix = currentTransform.toMatrix();
		PublicMatrix4f currentToLowest = PublicMatrix4f.mul(PublicMatrix4f.invert(currentMatrix, null), lowestMatrix, null);

		for (Joint subJoint : joint.getSubJoints())
		{
			if (!poses.get(priority).getFirst().isJointEnabled(clientAnimator.getOwner(), subJoint.getName()))
			{
				PublicMatrix4f lowestLocalTransform = PublicMatrix4f.mul(joint.getLocalTrasnform(), lowestMatrix, null);
				PublicMatrix4f currentLocalTransform = PublicMatrix4f.mul(joint.getLocalTrasnform(), currentMatrix, null);
				PublicMatrix4f childTransform = PublicMatrix4f.mul(subJoint.getLocalTrasnform(),
						lowestPose.getTransformByName(subJoint.getName()).toMatrix(), null);
				PublicMatrix4f lowestFinal = PublicMatrix4f.mul(lowestLocalTransform, childTransform, null);
				PublicMatrix4f currentFinal = PublicMatrix4f.mul(currentLocalTransform, childTransform, null);
				Vector3f vec = new Vector3f(0, currentFinal.m31 - lowestFinal.m31, currentFinal.m32 - lowestFinal.m32);
				JointTransform jt = result.getJointTransformData().getOrDefault(subJoint.getName(),
						JointTransform.empty());
				jt.parent(JointTransform.getTranslation(vec), PublicMatrix4f::mul);
				jt.jointLocal(JointTransform.fromMatrixNoScale(currentToLowest), PublicMatrix4f::mul);
			}
		}
	};

	public static JointMask of(String jointName, BindModifier bindModifier)
	{
		return new JointMask(jointName, bindModifier);
	}

	public static JointMask of(String jointName)
	{
		return new JointMask(jointName, null);
	}

	private final String jointName;
	private final BindModifier bindModifier;

	private JointMask(String jointName, BindModifier bindModifier)
	{
		this.jointName = jointName;
		this.bindModifier = bindModifier;
	}

	@Override
	public boolean equals(Object object)
	{
		if (object instanceof JointMask)
		{
			return ((JointMask) object).jointName.equals(this.jointName);
		}

		return super.equals(object);
	}

	public BindModifier getBindModifier()
	{
		return this.bindModifier;
	}
}
