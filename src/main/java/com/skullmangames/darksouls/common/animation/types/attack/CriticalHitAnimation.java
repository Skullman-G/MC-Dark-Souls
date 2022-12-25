package com.skullmangames.darksouls.common.animation.types.attack;

import java.util.function.Function;

import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.types.InvincibleAnimation;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.DamageType;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.StunType;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;

public class CriticalHitAnimation extends InvincibleAnimation
{
	private LivingEntity target;
	private final float hit;
	
	public CriticalHitAnimation(float convertTime, float hit, String path, Function<Models<?>, Model> model)
	{
		super(convertTime, path, model);
		this.hit = hit;
	}
	
	public void setTarget(LivingEntity target)
	{
		this.target = target;
	}
	
	@Override
	public void onUpdate(LivingCap<?> entityCap)
	{
		super.onUpdate(entityCap);
		float time = entityCap.getAnimator().getMainPlayer().getElapsedTime();
		float prevTime = entityCap.getAnimator().getMainPlayer().getPrevElapsedTime();
		if (time >= this.hit && prevTime < this.hit)
		{
			float amount = entityCap.getDamageToEntity(this.target, InteractionHand.MAIN_HAND);
			ExtendedDamageSource extDmgSource = entityCap.getDamageSource(entityCap.getOriginalEntity().position(), 0, StunType.NONE, amount, 0, DamageType.CRITICAL, 0);
			entityCap.hurtEntity(this.target, InteractionHand.MAIN_HAND, extDmgSource, amount);
		}
	}
}
