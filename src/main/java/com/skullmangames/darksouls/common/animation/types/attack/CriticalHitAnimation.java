package com.skullmangames.darksouls.common.animation.types.attack;

import java.util.function.Function;

import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.types.InvincibleAnimation;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.Damages;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.StunType;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;

public class CriticalHitAnimation extends InvincibleAnimation
{
	private final float hit;
	
	public CriticalHitAnimation(ResourceLocation id, float convertTime, float hit, ResourceLocation path, Function<Models<?>, Model> model)
	{
		super(id, convertTime, path, model);
		this.hit = hit;
	}
	
	@Override
	public void onUpdate(LivingCap<?> entityCap)
	{
		super.onUpdate(entityCap);
		Entity target = entityCap.criticalTarget;
		if (target == null) return;
		float time = entityCap.getAnimator().getMainPlayer().getElapsedTime();
		float prevTime = entityCap.getAnimator().getMainPlayer().getPrevElapsedTime();
		if (time >= this.hit && prevTime < this.hit)
		{
			MeleeWeaponCap weapon = entityCap.getHeldMeleeWeaponCap(InteractionHand.MAIN_HAND);
			Damages damages = entityCap.getDamageToEntity(target, InteractionHand.MAIN_HAND);
			if (weapon != null) damages.mul(weapon.getCritical());
			ExtendedDamageSource extDmgSource = entityCap.getDamageSource(entityCap.getOriginalEntity().position(), 0, StunType.INVINCIBILITY_BYPASS, 0, 0, damages);
			entityCap.hurtEntity(target, InteractionHand.MAIN_HAND, extDmgSource);
			entityCap.criticalTarget = null;
		}
	}
}
