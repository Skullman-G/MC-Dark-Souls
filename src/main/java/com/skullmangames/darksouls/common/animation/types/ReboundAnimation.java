package com.skullmangames.darksouls.common.animation.types;

import com.skullmangames.darksouls.client.animation.AnimatorClient;
import com.skullmangames.darksouls.common.capability.entity.LivingData;

public class ReboundAnimation extends AimingAnimation
{
	public ReboundAnimation(int id, float convertTime, boolean repeatPlay, String path1, String path2, String path3)
	{
		super(id, convertTime, repeatPlay, path1, path2, path3);
	}
	
	@Override
	public void onActivate(LivingData<?> entity)
	{
		if (entity.isRemote())
		{
			AnimatorClient animator = entity.getClientAnimator();
			if(animator.mixLayerActivated)
			{
				animator.mixLayer.pause = false;
			}
		}
	}
	
	@Override
	public void onUpdate(LivingData<?> entity)
	{
		;
	}

	@Override
	public LivingData.EntityState getState(float time)
	{
		return LivingData.EntityState.POST_DELAY;
	}
}