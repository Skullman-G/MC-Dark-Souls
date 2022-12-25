package com.skullmangames.darksouls.common.animation.types.attack;

import java.util.function.Function;

import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource;
import com.skullmangames.darksouls.core.util.math.MathUtils;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCSetPos;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class CriticalAttackAnimation extends AttackAnimation
{
	public CriticalAttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery,
			String index, String path, Function<Models<?>, Model> model)
	{
		super(convertTime, antic, preDelay, contact, recovery, index, path, model);
	}
	
	@Override
	protected boolean onDamageTarget(LivingCap<?> entityCap, Entity target)
	{
		LivingEntity attacker = entityCap.getOriginalEntity();
		LivingCap<?> targetCap = (LivingCap<?>)target.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
		if (entityCap == null || targetCap == null || !entityCap.canBackstab(target)) return false;
		
		double yRotAttacker = Math.toRadians(MathUtils.toNormalRot(attacker.getYRot()));
		double dist = 1.0D;
		Vec3 dir = new Vec3(Math.sin(yRotAttacker) * dist, 0, Math.cos(yRotAttacker) * dist);
		target.setPos(attacker.position().add(dir));
		target.yRot = attacker.yRot;
		target.yRotO = attacker.yRot;
		ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCSetPos(target.position(), target.getYRot(), target.getXRot(), target.getId(), true), target);
		if (target instanceof ServerPlayer)
		{
			ModNetworkManager.sendToPlayer(new STCSetPos(target.position(), target.getYRot(), target.getXRot(), target.getId()), (ServerPlayer)target);
		}
		entityCap.playAnimationSynchronized(Animations.BACKSTAB_THRUST, 0);
		if (targetCap.getOriginalEntity().getHealth() <= 0)
		{
			targetCap.playAnimationSynchronized(Animations.BIPED_DEATH_BACKSTAB_THRUST, 0);
		}
		else targetCap.playAnimationSynchronized(Animations.BIPED_HIT_BACKSTAB_THRUST, 0);
		
		return true;
	}
	
	@Override
	protected ExtendedDamageSource getDamageSourceExt(LivingCap<?> entityCap, Vec3 attackPos, Entity target,
			Phase phase, float amount)
	{
		amount *= entityCap.canBackstab(target) ? 2.0F : 0.5F;
		return super.getDamageSourceExt(entityCap, attackPos, target, phase, amount);
	}
}
