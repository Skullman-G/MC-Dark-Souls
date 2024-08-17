package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.client.animation.ClientAnimator;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.DeathAnimation;
import com.skullmangames.darksouls.common.entity.BellGargoyle;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackInstance;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource;
import com.skullmangames.darksouls.core.util.math.vector.ModMatrix4f;

public class BellGargoyleCap extends MobCap<BellGargoyle>
{
	public static final float WEAPON_SCALE = 1.5F;
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB)
	{
		return modelDB.ENTITY_BELL_GARGOYLE;
	}

	@Override
	public void initAnimator(ClientAnimator animatorClient)
	{
		animatorClient.putLivingAnimation(LivingMotion.IDLE, Animations.BELL_GARGOYLE_IDLE.get());
		animatorClient.putLivingAnimation(LivingMotion.WALKING, Animations.BELL_GARGOYLE_WALK.get());
		animatorClient.putLivingAnimation(LivingMotion.RUNNING, Animations.BELL_GARGOYLE_RUN.get());
		animatorClient.setCurrentMotionsToDefault();
	}
	
	@Override
	protected void initAI()
	{
		super.initAI();
		this.orgEntity.goalSelector.addGoal(0, new AttackGoal(this, 0.5F, 1, true, false, false)
				.addAttack(new AttackInstance(1, 0.5F, 4F, Animations.BELL_GARGOYLE_LA.get()))
				.addAttack(new AttackInstance(1, 0.5F, 4F, Animations.BELL_GARGOYLE_HA.get())));
	}
	
	@Override
	public float getWeaponScale()
	{
		return WEAPON_SCALE;
	}
	
	@Override
	public int getSoulReward()
	{
		return 10000;
	}
	
	@Override
	public void updateMotion()
	{
		if (this.orgEntity.animationSpeed > 0.01F)
		{
			if (this.orgEntity.isSprinting()) this.baseMotion = LivingMotion.RUNNING;
			else this.baseMotion = LivingMotion.WALKING;
		}
		else this.baseMotion = LivingMotion.IDLE;
	}
	
	@Override
	public ModMatrix4f getHeadMatrix(float partialTicks)
	{
		return ModMatrix4f.createModelMatrix(0, 0, 0, 0, 0, 0, 1, 1, 1, 1, partialTicks, 1, 1, 1);
	}

	@Override
	public boolean canBeParried()
	{
		return false;
	}

	@Override
	public boolean canBePunished()
	{
		return false;
	}

	@Override
	public boolean canBeBackstabbed()
	{
		return false;
	}
	
	@Override
	public DeathAnimation getDeathAnimation(ExtendedDamageSource dmgSource)
	{
		return Animations.BELL_GARGOYLE_DEATH.get();
	}
}
