package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.client.animation.AnimatorClient;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.item.WeaponCap.WeaponCategory;
import com.skullmangames.darksouls.common.entity.Faction;
import com.skullmangames.darksouls.common.entity.HollowLordranWarrior;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackInstance;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackPatternGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.ChasingGoal;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.client.CTSReqSpawnInfo;

public class HollowLordranWarriorData extends HumanoidData<HollowLordranWarrior>
{
	public HollowLordranWarriorData()
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
		
		if (!this.isClientSide())
		{
			if (!this.orgEntity.canPickUpLoot()) this.orgEntity.setCanPickUpLoot(this.isArmed());
		}
		else ModNetworkManager.sendToServer(new CTSReqSpawnInfo(this.orgEntity.getId()));
	}
	
	@Override
	protected void initAnimator(AnimatorClient animatorClient)
	{
		super.initAnimator(animatorClient);
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.BIPED_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.WALKING, Animations.HOLLOW_LORDRAN_WARRIOR_WALK);
		animatorClient.addLivingAnimation(LivingMotion.RUNNING, Animations.BIPED_RUN);
		animatorClient.addLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		animatorClient.addLivingAnimation(LivingMotion.MOUNT, Animations.BIPED_MOUNT);
		animatorClient.addLivingAnimation(LivingMotion.DEATH, Animations.BIPED_DEATH);
		animatorClient.setCurrentLivingMotionsToDefault();
	}
	
	@Override
	public void setAIAsArmed(WeaponCategory category)
	{
		this.orgEntity.goalSelector.addGoal(1, new ChasingGoal(this, 1.0D));
		
		if (category == WeaponCategory.STRAIGHT_SWORD)
		{
			this.orgEntity.goalSelector.addGoal(0, new AttackPatternGoal(this, 0.0F, true)
					.addAttack(new AttackInstance(4, 2.0F, Animations.HOLLOW_LIGHT_ATTACKS))
					.addAttack(new AttackInstance(4, 2.0F, Animations.HOLLOW_BARRAGE))
					.addAttack(new AttackInstance(4, 2.0F, Animations.HOLLOW_LORDRAN_WARRIOR_TH_LA))
					.addAttack(new AttackInstance(4, 2.5F, 4.0F, Animations.HOLLOW_LORDRAN_WARRIOR_DASH_ATTACK)));
		}
		else if (category == WeaponCategory.AXE)
		{
			this.orgEntity.goalSelector.addGoal(0, new AttackPatternGoal(this, 0.0F, true)
					.addAttack(new AttackInstance(4, 2.0F, Animations.HOLLOW_LORDRAN_WARRIOR_AXE_LA))
					.addAttack(new AttackInstance(4, 2.0F, Animations.HOLLOW_LORDRAN_WARRIOR_AXE_TH_LA))
					.addAttack(new AttackInstance(4, 2.5F, 4.0F, Animations.HOLLOW_LORDRAN_WARRIOR_DASH_ATTACK)));
		}
	}

	@Override
	public void updateMotion()
	{
		super.commonCreatureUpdateMotion();
	}
}
