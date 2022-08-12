package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.client.animation.ClientAnimator;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.capability.item.ItemCapability;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap.AttackType;
import com.skullmangames.darksouls.common.entity.Hollow;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackInstance;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackGoal;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.CreatureEntity;

public class SimpleHumanoidCap<T extends MobEntity> extends HumanoidCap<T>
{
	@Override
	public void initAnimator(ClientAnimator animatorClient)
	{
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.BIPED_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.WALKING, Animations.BIPED_WALK);
		animatorClient.addLivingAnimation(LivingMotion.RUNNING, Animations.BIPED_RUN);
		animatorClient.addLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		animatorClient.addLivingAnimation(LivingMotion.MOUNT, Animations.BIPED_MOUNT);
		animatorClient.addLivingAnimation(LivingMotion.DEATH, Animations.BIPED_DEATH);
		animatorClient.addLivingAnimation(LivingMotion.BLOCKING, Animations.BIPED_BLOCK);
		animatorClient.setCurrentMotionsToDefault();
	}
	
	@Override
	public void postInit()
	{
		if (this.isClientSide() || this.orgEntity.isNoAi()) return;
		super.resetCombatAI();
		ItemCapability cap = ModCapabilities.getItemCapability(this.orgEntity.getMainHandItem());
		if (cap == null || !(cap instanceof MeleeWeaponCap)) return;
		MeleeWeaponCap weapon = (MeleeWeaponCap)cap;
		
		this.orgEntity.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this.orgEntity, Hollow.class, true));
		if (this.orgEntity instanceof CreatureEntity) this.orgEntity.targetSelector.addGoal(1, new HurtByTargetGoal((CreatureEntity)this.orgEntity));
		this.orgEntity.goalSelector.addGoal(0, new AttackGoal(this, 0.0F, true, false, true)
				.addAttack(new AttackInstance(1, 2.0F, weapon.getAttacks(AttackType.LIGHT)))
				.addAttack(new AttackInstance(1, 2.0F, weapon.getAttacks(AttackType.HEAVY)))
				.addAttack(new AttackInstance(1, 2.0F, weapon.getAttacks(AttackType.DASH))));
	}

	@Override
	public void updateMotion()
	{
		this.commonCreatureUpdateMotion();
	}
}
