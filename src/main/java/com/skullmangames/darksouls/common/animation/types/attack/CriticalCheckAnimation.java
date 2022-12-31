package com.skullmangames.darksouls.common.animation.types.attack;

import java.util.function.Function;

import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.Property.AttackProperty;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.DamageType;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.StunType;
import com.skullmangames.darksouls.core.util.math.MathUtils;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCSetPos;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class CriticalCheckAnimation extends AttackAnimation
{
	private final StaticAnimation followUp;
	private final boolean isWeak;
	
	public CriticalCheckAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, boolean isWeak,
			String index, String path, Function<Models<?>, Model> model, StaticAnimation followUp)
	{
		super(convertTime, antic, preDelay, contact, recovery, index, path, model);
		this.followUp = followUp;
		this.isWeak = isWeak;
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
		yRotAttacker = Math.toRadians(MathUtils.toNormalRot(attacker.getYRot()) - 90);
		dist = 0.5D;
		dir = new Vec3(Math.sin(yRotAttacker) * dist, 0, Math.cos(yRotAttacker) * dist);
		target.setPos(target.position().add(dir));
		target.yRot = attacker.yRot;
		target.yRotO = attacker.yRot;
		ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCSetPos(target.position(), target.getYRot(), target.getXRot(), target.getId(), true), target);
		if (target instanceof ServerPlayer)
		{
			ModNetworkManager.sendToPlayer(new STCSetPos(target.position(), target.getYRot(), target.getXRot(), target.getId()), (ServerPlayer)target);
		}
		if (this.followUp instanceof CriticalHitAnimation) entityCap.criticalTarget = target;
		return true;
	}
	
	@Override
	protected void onAttackFinish(LivingCap<?> entityCap, boolean critical)
	{
		if (critical) entityCap.playAnimationSynchronized(this.followUp, 0);
	}
	
	@Override
	protected ExtendedDamageSource getDamageSourceExt(LivingCap<?> entityCap, Vec3 attackPos, Entity target, Phase phase, float amount)
	{
		boolean canBackstab = entityCap.canBackstab(target);
		amount *= canBackstab && !this.isWeak ? 2.0F : 0.1F;
		StunType stunType = canBackstab ? StunType.BACKSTABBED : phase.getProperty(AttackProperty.STUN_TYPE).orElse(StunType.LIGHT);
		DamageType damageType = canBackstab ? DamageType.CRITICAL : phase.getProperty(AttackProperty.DAMAGE_TYPE).orElse(DamageType.REGULAR);
		float poiseDamage = (float) entityCap.getOriginalEntity().getAttributeValue(ModAttributes.POISE_DAMAGE.get());
		int staminaDmgMul = phase.getProperty(AttackProperty.STAMINA_DMG_MUL).orElse(1);
		ExtendedDamageSource extDmgSource = entityCap.getDamageSource(attackPos, staminaDmgMul, stunType, amount, this.getRequiredDeflectionLevel(phase), damageType, poiseDamage);
		return extDmgSource;
	}
}
