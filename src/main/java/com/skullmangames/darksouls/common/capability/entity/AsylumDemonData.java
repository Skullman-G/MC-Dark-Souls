package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.client.animation.AnimatorClient;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.entity.AsylumDemonEntity;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackPatternGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.ChasingGoal;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.Models;

public class AsylumDemonData extends MobData<AsylumDemonEntity>
{
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB)
	{
		return modelDB.ENTITY_ASYLUM_DEMON;
	}

	@Override
	protected void initAnimator(AnimatorClient animatorClient)
	{
		animatorClient.mixLayerLeft.setJointMask("Shoulder_L", "Arm_L", "Hand_L");
		animatorClient.mixLayerRight.setJointMask("Shoulder_R", "Arm_R", "Hand_R");
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.ASYLUM_DEMON_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.WALKING, Animations.ASYLUM_DEMON_MOVE);
		animatorClient.setCurrentLivingMotionsToDefault();
	}
	
	@Override
	protected void updateOnClient()
	{
		AnimatorClient animator = getClientAnimator();
		
		if(this.inaction)
		{
			this.currentMotion = LivingMotion.IDLE;
		}
		else
		{
			this.updateMotion();
			if(!animator.compareMotion(currentMotion))
			{
				animator.playLoopMotion();
			}
			if(!animator.compareMixMotion(currentMixMotion))
			{
				animator.playMixLoopMotion();
			}
		}
	}
	
	@Override
	protected void initAI()
	{
		super.initAI();
		orgEntity.goalSelector.addGoal(1, new ChasingGoal(this, this.orgEntity, 1.0D, false));
		orgEntity.goalSelector.addGoal(0, new AttackPatternGoal(this, this.orgEntity, 0.0D, 3.5D, true, Animations.ASYLUM_DEMON_ATTACKS));
	}
	
	@Override
	public void updateMotion()
	{
		if (orgEntity.animationSpeed > 0.01F) this.currentMotion = LivingMotion.WALKING;
		else this.currentMotion = LivingMotion.IDLE;
	}
}
