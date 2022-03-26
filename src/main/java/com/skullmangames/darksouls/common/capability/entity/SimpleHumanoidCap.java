package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.client.animation.AnimatorClient;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.capability.item.ItemCapability;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap.AttackType;
import com.skullmangames.darksouls.common.entity.Hollow;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackInstance;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackPatternGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.ChasingGoal;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

public class SimpleHumanoidCap<T extends Mob> extends HumanoidCap<T>
{
	@Override
	protected void initAnimator(AnimatorClient animatorClient)
	{
		super.initAnimator(animatorClient);
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.BIPED_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.WALKING, Animations.BIPED_WALK);
		animatorClient.addLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		animatorClient.addLivingAnimation(LivingMotion.MOUNT, Animations.BIPED_MOUNT);
		animatorClient.addLivingAnimation(LivingMotion.DEATH, Animations.BIPED_DEATH);
		animatorClient.setCurrentLivingMotionsToDefault();
	}
	
	@Override
	public void postInit()
	{
		if (this.isClientSide() || this.orgEntity.isNoAi()) return;
		super.resetCombatAI();
		ItemCapability cap = ModCapabilities.getItemCapability(this.orgEntity.getMainHandItem());
		if (cap == null || !(cap instanceof MeleeWeaponCap)) return;
		MeleeWeaponCap weapon = (MeleeWeaponCap)cap;
		
		this.orgEntity.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this.orgEntity, Hollow.class, true));
		this.orgEntity.goalSelector.addGoal(1, new ChasingGoal(this, false));
		this.orgEntity.goalSelector.addGoal(0, new AttackPatternGoal(this, 0.0F, true)
				.addAttack(new AttackInstance(1, 1.0F, weapon.getAttacks(AttackType.LIGHT)))
				.addAttack(new AttackInstance(1, 1.0F, weapon.getAttacks(AttackType.HEAVY)))
				.addAttack(new AttackInstance(1, 2.0F, weapon.getAttacks(AttackType.DASH))));
	}

	@Override
	public void updateMotion()
	{
		this.commonCreatureUpdateMotion();
	}
}
