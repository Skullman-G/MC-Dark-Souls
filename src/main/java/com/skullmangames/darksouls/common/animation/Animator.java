package com.skullmangames.darksouls.common.animation;

import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.Animations;

public abstract class Animator
{
	protected LivingCap<?> entitydata;
	
	public abstract void playAnimation(int id, float modifyTime);
	public abstract void playAnimation(StaticAnimation nextAnimation, float modifyTime);
	public abstract void vacateCurrentPlay();
	public abstract void update();
	public abstract void onEntityDeath();
	public abstract AnimationPlayer getPlayer();
	public abstract void reserveAnimation(StaticAnimation nextAnimation);

	public abstract AnimationPlayer getPlayerFor(StaticAnimation animation);
	
	public boolean isReverse()
	{
		return false;
	}
	
	public void playDeathAnimation()
	{
		this.playAnimation(Animations.BIPED_DEATH, 0);
	}
}