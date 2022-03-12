package com.skullmangames.darksouls.common.animation.types;

import com.skullmangames.darksouls.client.animation.AnimatorClient;
import com.skullmangames.darksouls.common.capability.entity.LivingData;

public class ReboundAnimation extends AimingAnimation
{
	public ReboundAnimation(float convertTime, boolean repeatPlay, String path1, String path2, String path3, String armature, boolean clientOnly)
	{
		super(convertTime, repeatPlay, path1, path2, path3, armature, clientOnly);
	}
	
	@Override
	public void onActivate(LivingData<?> entity)
	{
		if (entity.isClientSide())
		{
			AnimatorClient animator = entity.getClientAnimator();
			if(animator.mixLayerActivated())
			{
				animator.mixLayerLeft.pause = false;
				animator.mixLayerRight.pause = false;
			}
		}
	}
	
	@Override
	public void onUpdate(LivingData<?> entity) {}

	@Override
	public LivingData.EntityState getState(float time)
	{
		return LivingData.EntityState.CONTACT;
	}
}