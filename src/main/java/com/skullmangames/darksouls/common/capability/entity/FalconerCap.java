package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.client.animation.ClientAnimator;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.capability.item.WeaponCap.WeaponCategory;
import com.skullmangames.darksouls.common.entity.Falconer;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackInstance;
import com.skullmangames.darksouls.common.entity.ai.goal.BowAttackGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.DrinkingEstusGoal;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.WeaponMovesets;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.client.CTSReqSpawnInfo;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.RangedAttackMob;

public class FalconerCap extends HumanoidCap<Falconer>
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
		animatorClient.putLivingAnimation(LivingMotion.IDLE, Animations.FALCONER_IDLE);
		animatorClient.putLivingAnimation(LivingMotion.WALKING, Animations.FALCONER_WALK);
		animatorClient.putLivingAnimation(LivingMotion.RUNNING, Animations.FALCONER_RUN);
		animatorClient.putLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		animatorClient.putLivingAnimation(LivingMotion.MOUNTED, Animations.BIPED_HORSEBACK_IDLE);
		animatorClient.putLivingAnimation(LivingMotion.BLOCKING, Animations.BIPED_BLOCK_HORIZONTAL);
		animatorClient.putLivingAnimation(LivingMotion.DRINKING, Animations.BIPED_DRINK);
		animatorClient.setCurrentMotionsToDefault();
	}

	@Override
	public void setAttackGoals(WeaponCategory category, ResourceLocation moveset)
	{
		this.orgEntity.goalSelector.addGoal(0, new DrinkingEstusGoal(this));
		
		if (category == WeaponCategory.BOW && this.orgEntity instanceof RangedAttackMob)
		{
			this.orgEntity.goalSelector.addGoal(0, new BowAttackGoal<Falconer, FalconerCap>(this, 40, 15.0F));
		}
		else
		{
			if (moveset.compareTo(WeaponMovesets.STRAIGHT_SWORD) != 0) return;
			this.orgEntity.goalSelector.addGoal(1, new AttackGoal(this, 0.0F, true, false, true)
					.addAttack(new AttackInstance(4, 2.0F, Animations.FALCONER_LIGHT_ATTACKS))
					.addDodge(Animations.BIPED_JUMP_BACK));
		}
	}
	
	@Override
	public ShieldHoldType getShieldHoldType()
	{
		return ShieldHoldType.VERTICAL_REVERSE;
	}

	@Override
	public void updateMotion()
	{
		super.commonMotionUpdate();
	}
}
