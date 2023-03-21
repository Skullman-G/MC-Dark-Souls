package com.skullmangames.darksouls.common.animation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.renderer.entity.model.Armature;
import com.skullmangames.darksouls.common.animation.types.DynamicAnimation;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.capability.entity.EntityState;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

import net.minecraft.resources.ResourceLocation;

public abstract class Animator
{
	protected Pose prevPose = new Pose();
	protected Pose currentPose = new Pose();
	protected final Map<LivingMotion, StaticAnimation> livingAnimations = new HashMap<>();

	protected LivingCap<?> entityCap;

	public abstract void playAnimation(StaticAnimation nextAnimation, float convertTimeModifier);

	public abstract void playAnimationInstantly(StaticAnimation nextAnimation);

	public abstract void update();

	public abstract EntityState getEntityState();

	public abstract AnimationPlayer getPlayerFor(DynamicAnimation playingAnimation);
	
	public abstract AnimationPlayer getMainPlayer();

	public abstract void init();

	public abstract void updatePose();

	public final void playAnimation(ResourceLocation id, float convertTimeModifier)
	{
		this.playAnimation(DarkSouls.getInstance().animationManager.getAnimation(id),
				convertTimeModifier);
	}

	public final void playAnimationInstantly(ResourceLocation id)
	{
		this.playAnimationInstantly(DarkSouls.getInstance().animationManager.getAnimation(id));
	}

	public Pose getPose(float partialTicks)
	{
		return Pose.interpolatePose(this.prevPose, this.currentPose, partialTicks);
	}

	public boolean isReverse()
	{
		return false;
	}

	public void addLivingAnimation(LivingMotion livingMotion, StaticAnimation animation)
	{
		this.livingAnimations.put(livingMotion, animation);
	}

	public Set<Map.Entry<LivingMotion, StaticAnimation>> getLivingAnimationEntrySet()
	{
		return this.livingAnimations.entrySet();
	}

	public void resetMotions()
	{
		this.livingAnimations.clear();
	}

	public static PublicMatrix4f getParentboundTransform(Pose pose, Armature armature, int pathIndex)
	{
		armature.initializeTransform();
		return getParentboundTransformInternal(pose, armature.getJointHierarcy(), new PublicMatrix4f(), pathIndex);
	}

	private static PublicMatrix4f getParentboundTransformInternal(Pose pose, Joint joint, PublicMatrix4f parentTransform, int pathIndex)
	{
		JointTransform jt = pose.getTransformByName(joint.getName());
		PublicMatrix4f result = jt.getParentboundMatrix(joint, parentTransform);
		int nextIndex = pathIndex % 10;
		return nextIndex > 0 ? getParentboundTransformInternal(pose, joint.getSubJoints().get(nextIndex - 1), result, pathIndex / 10) : result;
	}
}