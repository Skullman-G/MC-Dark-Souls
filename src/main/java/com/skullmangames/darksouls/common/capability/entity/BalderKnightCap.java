package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.client.animation.ClientAnimator;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.entity.BalderKnight;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackInstance;
import com.skullmangames.darksouls.common.entity.ai.goal.CrossbowAttackGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.DrinkingEstusGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.SwitchWeaponGoal;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.core.util.WeaponCategory;

import net.minecraft.world.InteractionHand;

public class BalderKnightCap extends HumanoidCap<BalderKnight>
{
	@Override
	public void initAnimator(ClientAnimator animatorClient)
	{
		animatorClient.putLivingAnimation(LivingMotion.IDLE, Animations.BALDER_KNIGHT_IDLE.get());
		animatorClient.putLivingAnimation(LivingMotion.WALKING, Animations.BALDER_KNIGHT_WALK.get());
		animatorClient.putLivingAnimation(LivingMotion.RUNNING, Animations.BALDER_KNIGHT_RUN.get());
		animatorClient.putLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL.get());
		animatorClient.putLivingAnimation(LivingMotion.MOUNTED, Animations.BIPED_HORSEBACK_IDLE.get());
		animatorClient.putLivingAnimation(LivingMotion.BLOCKING, Animations.BALDER_KNIGHT_BLOCK.get());
		animatorClient.putLivingAnimation(LivingMotion.DRINKING, Animations.BIPED_DRINK.get());
		animatorClient.setCurrentMotionsToDefault();
	}
	
	@Override
	public void setAttackGoals(WeaponCategory category)
	{
		SwitchWeaponGoal weaponGoal = new SwitchWeaponGoal(this).addDefaultEstusCondition();
		this.orgEntity.goalSelector.addGoal(1, new DrinkingEstusGoal(this));
		
		if (category == WeaponCategory.CROSSBOW)
		{
			weaponGoal.addSwitchCondition(this.orgEntity.getMainHandItem(), () ->
			{
				return this.getTarget() != null && this.orgEntity.distanceTo(this.getTarget()) > 10D;
			});
			this.orgEntity.goalSelector.addGoal(1, new CrossbowAttackGoal<>(this));
			
			weaponGoal.addSwitchCondition(ModItems.BALDER_SIDE_SWORD.get().getDefaultInstance(), () ->
			{
				return this.getTarget() != null && this.orgEntity.distanceTo(this.getTarget()) < 5D && this.orgEntity.getRandom().nextFloat() <= 0.1D;
			});
			this.orgEntity.goalSelector.addGoal(1, new AttackGoal(this, 0.0F, true, true, true)
					.addAttack(new AttackInstance(1, 2.0F, Animations.BALDER_KNIGHT_SIDE_SWORD_LA.get()))
					.addAttack(new AttackInstance(1, 2.0F, Animations.BALDER_KNIGHT_SIDE_SWORD_DA.get()))
					.addAttack(new AttackInstance(1, 2.0F, Animations.BALDER_KNIGHT_SIDE_SWORD_FAST_LA.get()))
					.addDodge(Animations.BIPED_JUMP_BACK.get()));
		}
		else if (category == WeaponCategory.STRAIGHT_SWORD)
		{
			weaponGoal.addSwitchCondition(this.orgEntity.getMainHandItem(), () ->
			{
				return this.getTarget() != null && this.orgEntity.distanceTo(this.getTarget()) < 5D;
			});
			this.orgEntity.goalSelector.addGoal(1, new AttackGoal(this, 0.0F, true, false, true)
					.addAttack(new AttackInstance(1, 2.0F, Animations.BALDER_KNIGHT_SIDE_SWORD_LA.get()))
					.addAttack(new AttackInstance(1, 2.0F, Animations.BALDER_KNIGHT_SIDE_SWORD_HA.get()))
					.addAttack(new AttackInstance(1, 2.0F, Animations.BALDER_KNIGHT_SIDE_SWORD_DA.get()))
					.addAttack(new AttackInstance(1, 2.0F, Animations.BALDER_KNIGHT_SHIELD_HA.get()))
					.addAttack(new AttackInstance(1, 2.0F, Animations.BALDER_KNIGHT_SIDE_SWORD_FAST_LA.get()))
					.addDodge(Animations.BIPED_JUMP_BACK.get()));
		}
		else if (category == WeaponCategory.THRUSTING_SWORD)
		{
			weaponGoal.addSwitchCondition(this.orgEntity.getMainHandItem(), () ->
			{
				return this.getTarget() != null && this.orgEntity.distanceTo(this.getTarget()) < 5D;
			});
			this.orgEntity.goalSelector.addGoal(1, new AttackGoal(this, 0.0F, true, false, true)
					.addAttack(new AttackInstance(1, 2.0F, Animations.BALDER_KNIGHT_RAPIER_LA.get()))
					.addAttack(new AttackInstance(1, 2.0F, Animations.BALDER_KNIGHT_RAPIER_HA.get()))
					.addAttack(new AttackInstance(1, 2.5F, 4.0F, Animations.BALDER_KNIGHT_RAPIER_DA.get()))
					.addDodge(Animations.BIPED_JUMP_BACK.get())
					.addParry(Animations.BALDER_KNIGHT_RAPIER_BLOCK.get(), Animations.BALDER_KNIGHT_RAPIER_PARRY.get()));
		}
		
		this.orgEntity.goalSelector.addGoal(0, weaponGoal);
	}
	
	@Override
	public void onParrySuccess()
	{
		super.onParrySuccess();
		this.playAnimationSynchronized(Animations.PUNISH_THRUST.get(), 0.0F);
	}

	@Override
	public void updateMotion()
	{
		super.commonMotionUpdate();
	}
	
	@Override
	public int getSoulReward()
	{
		return 160;
	}
	
	@Override
	public ShieldHoldType getShieldHoldType(InteractionHand hand)
	{
		return this.orgEntity.getItemInHand(hand).is(ModItems.BUCKLER.get()) ? ShieldHoldType.HORIZONTAL
		: ShieldHoldType.VERTICAL_REVERSE;
	}
}
