package com.skullmangames.darksouls.common.animation.types;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.animation.AnimatorClient;
import com.skullmangames.darksouls.client.renderer.entity.model.Armature;
import com.skullmangames.darksouls.common.animation.AnimationPlayer;
import com.skullmangames.darksouls.common.animation.JointTransform;
import com.skullmangames.darksouls.common.animation.Pose;
import com.skullmangames.darksouls.common.capability.entity.LivingData;
import com.skullmangames.darksouls.core.util.math.vector.Quaternion;
import com.skullmangames.darksouls.core.util.parser.xml.collada.AnimationDataExtractor;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class AimingAnimation extends StaticAnimation
{
	public StaticAnimation lookUp;
	public StaticAnimation lookDown;

	public AimingAnimation(int id, float convertTime, boolean repeatPlay, String path1, String path2, String path3)
	{
		super(id, convertTime, repeatPlay, path1);
		lookUp = new StaticAnimation(path2);
		lookDown = new StaticAnimation(path3);
	}
	
	@Override
	public void onUpdate(LivingData<?> entitydata)
	{
		super.onUpdate(entitydata);
		
		AnimatorClient animator = entitydata.getClientAnimator();
		if (animator.mixLayerActivated)
		{
			AnimationPlayer player = animator.getMixLayerPlayer();
			if (player.getElapsedTime() >= this.totalTime - 0.06F)
			{
				animator.mixLayer.pause = true;
			}
		}
	}
	
	@Override
	public Pose getPoseByTime(LivingData<?> entitydata, float time)
	{
		if (entitydata.isFirstPerson())
		{
			return super.getPoseByTime(entitydata, time);
		}
		else
		{
			float pitch = entitydata.getOriginalEntity().getViewXRot(Minecraft.getInstance().getFrameTime());
			StaticAnimation interpolateAnimation;
			interpolateAnimation = (pitch > 0) ? this.lookDown : this.lookUp;
			Pose pose1 = getPoseByTime(time);
			Pose pose2 = interpolateAnimation.getPoseByTime(entitydata, time);
			Pose interpolatedPose = Pose.interpolatePose(pose1, pose2, (Math.abs(pitch) / 90.0F));
			JointTransform chest = interpolatedPose.getTransformByName("Chest");
			JointTransform head = interpolatedPose.getTransformByName("Head");
			float f = 90.0F;
			float ratio = (f - Math.abs(entitydata.getOriginalEntity().xRot)) / f;
			float yawOffset = entitydata.getOriginalEntity().getVehicle() != null ? entitydata.getOriginalEntity().yRot : entitydata.getOriginalEntity().yBodyRot;
			head.setRotation(Quaternion.rotate((float)-Math.toRadians((yawOffset - entitydata.getOriginalEntity().yRot) * ratio), new Vector3f(0,1,0), head.getRotation()));
			chest.setCustomRotation(Quaternion.rotate((float)-Math.toRadians((entitydata.getOriginalEntity().yRot - yawOffset) * ratio),
					new Vector3f(0,1,0), null));
			
			return interpolatedPose;
		}
	}
	
	private Pose getPoseByTime(float time)
	{
		Pose pose = new Pose();
		for (String jointName : jointTransforms.keySet())
		{
			pose.putJointData(jointName, jointTransforms.get(jointName).getInterpolatedTransform(time));
		}
		
		return pose;
	}
	
	@Override
	public StaticAnimation bindFull(Armature armature)
	{
		if (animationDataPath != null)
		{
			AnimationDataExtractor.extractAnimation(new ResourceLocation(DarkSouls.MOD_ID, animationDataPath), this, armature);
			animationDataPath = null;
			AnimationDataExtractor.extractAnimation(new ResourceLocation(DarkSouls.MOD_ID, lookUp.animationDataPath), lookUp, armature);
			lookUp.animationDataPath = null;
			AnimationDataExtractor.extractAnimation(new ResourceLocation(DarkSouls.MOD_ID, lookDown.animationDataPath), lookDown, armature);
			lookDown.animationDataPath = null;
		}
		return this;
	}
}