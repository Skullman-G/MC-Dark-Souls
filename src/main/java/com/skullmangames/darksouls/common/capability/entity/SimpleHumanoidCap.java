package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.client.animation.ClientAnimator;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.capability.item.ItemCapability;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap.AttackType;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackInstance;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackGoal;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;

public class SimpleHumanoidCap<T extends Mob> extends HumanoidCap<T>
{
	@Override
	public void initAnimator(ClientAnimator animatorClient)
	{
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.BIPED_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.WALKING, Animations.BIPED_WALK);
		animatorClient.addLivingAnimation(LivingMotion.RUNNING, Animations.BIPED_RUN);
		animatorClient.addLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		animatorClient.addLivingAnimation(LivingMotion.MOUNTED, Animations.BIPED_HORSEBACK_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.SHIELD_BLOCKING, Animations.BIPED_BLOCK_VERTICAL);
		animatorClient.addLivingAnimation(LivingMotion.WEAPON_BLOCKING, Animations.BIPED_BLOCK_HORIZONTAL);
		animatorClient.setCurrentMotionsToDefault();
	}
	
	@Override
	public void postInit()
	{
		if (this.isClientSide() || this.orgEntity.isNoAi()) return;
		super.resetCombatAI();
		
		if (this.orgEntity instanceof PathfinderMob) this.orgEntity.targetSelector.addGoal(1, new HurtByTargetGoal((PathfinderMob)this.orgEntity));
		
		ItemCapability cap = ModCapabilities.getItemCapability(this.orgEntity.getMainHandItem());
		if (cap == null || !(cap instanceof MeleeWeaponCap))
		{
			this.orgEntity.goalSelector.addGoal(0, new AttackGoal(this, 0.0F, true, false, true)
					.addAttack(new AttackInstance(1, 1.0F, Animations.FIST_LIGHT_ATTACK))
					.addAttack(new AttackInstance(1, 1.0F, Animations.FIST_HEAVY_ATTACK))
					.addAttack(new AttackInstance(1, 1.0F, Animations.FIST_DASH_ATTACK)));
		}
		else
		{
			MeleeWeaponCap weapon = (MeleeWeaponCap)cap;
			
			this.orgEntity.goalSelector.addGoal(0, new AttackGoal(this, 0.0F, true, false, true)
					.addAttack(new AttackInstance(1, 2.0F, weapon.getAttacks(AttackType.LIGHT)))
					.addAttack(new AttackInstance(1, 2.0F, weapon.getAttacks(AttackType.HEAVY)))
					.addAttack(new AttackInstance(1, 2.0F, weapon.getAttacks(AttackType.DASH))));
		}
	}

	@Override
	public void updateMotion()
	{
		this.commonMotionUpdate();
	}
}
