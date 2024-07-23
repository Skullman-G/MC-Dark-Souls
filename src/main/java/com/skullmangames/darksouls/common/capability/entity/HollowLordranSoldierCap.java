package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.client.animation.ClientAnimator;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.entity.HollowLordranSoldier;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackInstance;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.CrossbowAttackGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.DrinkingEstusGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.SwitchWeaponGoal;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.core.util.WeaponCategory;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.client.CTSReqSpawnInfo;

import net.minecraft.world.InteractionHand;

public class HollowLordranSoldierCap extends HumanoidCap<HollowLordranSoldier>
{
	@Override
	public void postInit()
	{
		super.postInit();
		
		if (!this.isClientSide()) this.orgEntity.setCanPickUpLoot(false);
		else ModNetworkManager.sendToServer(new CTSReqSpawnInfo(this.orgEntity.getId()));
	}
	
	@Override
	public void initAnimator(ClientAnimator animatorClient)
	{
		animatorClient.putLivingAnimation(LivingMotion.IDLE, Animations.HOLLOW_IDLE);
		animatorClient.putLivingAnimation(LivingMotion.WALKING, Animations.HOLLOW_LORDRAN_SOLDIER_WALK);
		animatorClient.putLivingAnimation(LivingMotion.RUNNING, Animations.HOLLOW_LORDRAN_SOLDIER_RUN);
		animatorClient.putLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		animatorClient.putLivingAnimation(LivingMotion.MOUNTED, Animations.BIPED_HORSEBACK_IDLE);
		animatorClient.putLivingAnimation(LivingMotion.BLOCKING, Animations.HOLLOW_LORDRAN_SOLDIER_BLOCK);
		animatorClient.putLivingAnimation(LivingMotion.AIMING, Animations.BIPED_CROSSBOW_AIM);
		animatorClient.putLivingAnimation(LivingMotion.DRINKING, Animations.BIPED_DRINK);
		animatorClient.setCurrentMotionsToDefault();
	}
	
	@Override
	public int getSoulReward()
	{
		return 60;
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
			
			weaponGoal.addSwitchCondition(ModItems.LONGSWORD.get().getDefaultInstance(), () ->
			{
				return this.getTarget() != null && this.orgEntity.distanceTo(this.getTarget()) < 5D && this.orgEntity.getRandom().nextFloat() <= 0.1D;
			});
			this.orgEntity.goalSelector.addGoal(1, new AttackGoal(this, 0.0F, true, true, true)
					.addAttack(new AttackInstance(1, 2.0F, Animations.BALDER_KNIGHT_SIDE_SWORD_LA))
					.addAttack(new AttackInstance(1, 2.0F, Animations.BALDER_KNIGHT_SIDE_SWORD_DA))
					.addAttack(new AttackInstance(1, 2.0F, Animations.BALDER_KNIGHT_SIDE_SWORD_FAST_LA))
					.addDodge(Animations.BIPED_JUMP_BACK));
		}
		else if (category == WeaponCategory.STRAIGHT_SWORD)
		{
			weaponGoal.addSwitchCondition(this.orgEntity.getMainHandItem(), () ->
			{
				return this.getTarget() != null && this.orgEntity.distanceTo(this.getTarget()) < 5D;
			});
			this.orgEntity.goalSelector.addGoal(1, new AttackGoal(this, 0.0F, true, false, true)
					.addAttack(new AttackInstance(0, 2.0F, Animations.HOLLOW_LORDRAN_SOLDIER_SWORD_LA))
					.addAttack(new AttackInstance(1, 2.5F, 4.0F, Animations.HOLLOW_LORDRAN_SOLDIER_SWORD_DA))
					.addAttack(new AttackInstance(0, 2.0F, Animations.HOLLOW_LORDRAN_SOLDIER_SWORD_HEAVY_THRUST))
					.addAttack(new AttackInstance(0, 2.0F, Animations.HOLLOW_LORDRAN_SOLDIER_SWORD_THRUST_COMBO))
					.addAttack(new AttackInstance(2, 2.0F, Animations.HOLLOW_LORDRAN_SOLDIER_SHIELD_BASH))
					.addDodge(Animations.BIPED_JUMP_BACK));
		}
		else if (category == WeaponCategory.SPEAR)
		{
			weaponGoal.addSwitchCondition(this.orgEntity.getMainHandItem(), () ->
			{
				return this.getTarget() != null && this.orgEntity.distanceTo(this.getTarget()) < 5D;
			});
			this.orgEntity.goalSelector.addGoal(1, new AttackGoal(this, 1.0F, true, true, true)
					.addAttack(new AttackInstance(0, 3.0F, Animations.HOLLOW_LORDRAN_SOLDIER_SPEAR_THRUSTS))
					.addAttack(new AttackInstance(0, 3.0F, Animations.HOLLOW_LORDRAN_SOLDIER_SPEAR_SWINGS))
					.addAttack(new AttackInstance(2, 2.0F, Animations.HOLLOW_LORDRAN_SOLDIER_SHIELD_BASH))
					.addDodge(Animations.BIPED_JUMP_BACK));
		}
		
		this.orgEntity.goalSelector.addGoal(0, weaponGoal);
	}
	
	@Override
	public ShieldHoldType getShieldHoldType(InteractionHand hand)
	{
		return ShieldHoldType.HORIZONTAL;
	}

	@Override
	public void updateMotion()
	{
		super.commonMotionUpdate();
	}
}
