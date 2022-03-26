package com.skullmangames.darksouls.common.animation.types;

import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.animation.AnimatorClient;
import com.skullmangames.darksouls.client.renderer.entity.model.Armature;
import com.skullmangames.darksouls.common.animation.AnimationPlayer;
import com.skullmangames.darksouls.common.animation.JointTransform;
import com.skullmangames.darksouls.common.animation.Pose;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.ClientModels;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.math.vector.ModQuaternion;
import com.skullmangames.darksouls.core.util.parser.xml.collada.AnimationDataExtractor;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;

public class AimingAnimation extends StaticAnimation
{
	public StaticAnimation lookUp;
	public StaticAnimation lookDown;

	public AimingAnimation(float convertTime, boolean repeatPlay, String path1, String path2, String path3, String armature, boolean clientOnly)
	{
		super(true, convertTime, repeatPlay, path1, armature, clientOnly);
		lookUp = new StaticAnimation(path2);
		lookDown = new StaticAnimation(path3);
	}
	
	@Override
	public void onUpdate(LivingCap<?> entitydata)
	{
		super.onUpdate(entitydata);
		
		AnimatorClient animator = entitydata.getClientAnimator();
		if (animator.mixLayerActivated())
		{
			AnimationPlayer player = animator.getLeftMixLayerPlayer();
			if (player.getElapsedTime() >= this.totalTime - 0.06F)
			{
				animator.mixLayerLeft.pause = true;
				animator.mixLayerRight.pause = true;
			}
		}
	}
	
	@Override
	public Pose getPoseByTime(LivingCap<?> entitydata, float time)
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
			head.setRotation(ModQuaternion.rotate((float)-Math.toRadians((yawOffset - entitydata.getOriginalEntity().yRot) * ratio), new Vector3f(0,1,0), head.getRotation()));
			chest.setCustomRotation(ModQuaternion.rotate((float)-Math.toRadians((entitydata.getOriginalEntity().yRot - yawOffset) * ratio),
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
	public void bind(Dist dist)
	{
		if (this.clientOnly && dist != Dist.CLIENT) return;
		
		if (path != null)
		{
			Models<?> modeldata = dist == Dist.CLIENT ? ClientModels.CLIENT : Models.SERVER;
			Armature armature = modeldata.findArmature(this.armature);
			AnimationDataExtractor.extractAnimation(new ResourceLocation(DarkSouls.MOD_ID, path), this, armature);
			path = null;
			AnimationDataExtractor.extractAnimation(new ResourceLocation(DarkSouls.MOD_ID, lookUp.path), lookUp, armature);
			lookUp.path = null;
			AnimationDataExtractor.extractAnimation(new ResourceLocation(DarkSouls.MOD_ID, lookDown.path), lookDown, armature);
			lookDown.path = null;
		}
		return;
	}
}