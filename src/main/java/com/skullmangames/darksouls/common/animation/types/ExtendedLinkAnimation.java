package com.skullmangames.darksouls.common.animation.types;

import com.skullmangames.darksouls.common.capability.entity.LivingData;

public class ExtendedLinkAnimation extends ActionAnimation
{
	protected final ActionAnimation loopAnimation;
	
	public ExtendedLinkAnimation(int id, float convertTime, boolean breakMove, boolean affectY, float seperator, String path, String armature)
	{
		super(id, convertTime, affectY, path, armature);
		this.loopAnimation = new ActionAnimation(id+1, 0.2F, false, path, armature)
		{
			@Override
			public boolean isRepeat()
			{
				return true;
			}
		};
		this.loopAnimation.startingTime = seperator;
	}
	
	@Override
	public void onFinish(LivingData<?> entitydata, boolean isEnd)
	{
		super.onFinish(entitydata, isEnd);
		entitydata.reserveAnimationSynchronize(this.loopAnimation);
	}
}
