package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.client.animation.AnimatorClient;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.item.WeaponCap.WeaponCategory;
import com.skullmangames.darksouls.common.entity.Faction;
import com.skullmangames.darksouls.common.entity.HollowLordranSoldier;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackInstance;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackPatternGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.ChasingGoal;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.client.CTSReqSpawnInfo;

public class HollowLordranSoldierData extends HumanoidData<HollowLordranSoldier>
{
	public HollowLordranSoldierData()
	{
		super(Faction.UNDEAD);
	}

	@Override
	public StaticAnimation getDeflectAnimation()
	{
		return Animations.HOLLOW_DEFLECTED;
	}
	
	@Override
	public void postInit()
	{
		super.postInit();
		
		if (!this.isClientSide()) this.orgEntity.setCanPickUpLoot(false);
		else ModNetworkManager.sendToServer(new CTSReqSpawnInfo(this.orgEntity.getId()));
	}
	
	@Override
	protected void initAnimator(AnimatorClient animatorClient)
	{
		super.initAnimator(animatorClient);
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.HOLLOW_LORDRAN_SOLDIER_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.WALKING, Animations.HOLLOW_LORDRAN_SOLDIER_WALK);
		animatorClient.addLivingAnimation(LivingMotion.RUNNING, Animations.HOLLOW_LORDRAN_SOLDIER_RUN);
		animatorClient.addLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		animatorClient.addLivingAnimation(LivingMotion.MOUNT, Animations.BIPED_MOUNT);
		animatorClient.addLivingAnimation(LivingMotion.DEATH, Animations.BIPED_DEATH);
		animatorClient.addLivingMixAnimation(LivingMotion.BLOCKING, Animations.HOLLOW_LORDRAN_SOLDIER_BLOCK);
		animatorClient.setCurrentLivingMotionsToDefault();
	}
	
	@Override
	public void setAIAsArmed(WeaponCategory category)
	{
		if (category == WeaponCategory.STRAIGHT_SWORD)
		{
			this.orgEntity.goalSelector.addGoal(1, new ChasingGoal(this, 1.0D));
			
			this.orgEntity.goalSelector.addGoal(0, new AttackPatternGoal(this, 0.0F, true)
					.addAttack(new AttackInstance(0, 2.0F, Animations.HOLLOW_LORDRAN_SOLDIER_SWORD_LA))
					.addAttack(new AttackInstance(1, 2.5F, 4.0F, Animations.HOLLOW_LORDRAN_SOLDIER_SWORD_DA))
					.addAttack(new AttackInstance(0, 2.0F, Animations.HOLLOW_LORDRAN_SOLDIER_SWORD_HEAVY_THRUST))
					.addAttack(new AttackInstance(0, 2.0F, Animations.HOLLOW_LORDRAN_SOLDIER_SWORD_THRUST_COMBO)));
		}
		else if (category == WeaponCategory.SPEAR)
		{
			this.orgEntity.goalSelector.addGoal(1, new ChasingGoal(this, 1.0D));
			
			this.orgEntity.goalSelector.addGoal(0, new AttackPatternGoal(this, 0.0F, true)
					.addAttack(new AttackInstance(0, 3.0F, Animations.HOLLOW_LORDRAN_SOLDIER_SPEAR_THRUSTS))
					.addAttack(new AttackInstance(0, 3.0F, Animations.HOLLOW_LORDRAN_SOLDIER_SPEAR_SWINGS)));
		}
	}

	@Override
	public void updateMotion()
	{
		super.commonCreatureUpdateMotion();
	}
}
