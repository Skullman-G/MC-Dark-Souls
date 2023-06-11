package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.client.animation.ClientAnimator;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.DeathAnimation;
import com.skullmangames.darksouls.common.capability.item.WeaponCap.WeaponCategory;
import com.skullmangames.darksouls.common.entity.BlackKnight;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackInstance;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.WeaponMovesets;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource;

import net.minecraft.resources.ResourceLocation;

public class BlackKnightCap extends HumanoidCap<BlackKnight>
{
	@Override
	public void initAnimator(ClientAnimator animatorClient)
	{
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.BLACK_KNIGHT_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.WALKING, Animations.BLACK_KNIGHT_WALKING);
		animatorClient.addLivingAnimation(LivingMotion.RUNNING, Animations.BLACK_KNIGHT_RUNNING);
		animatorClient.addLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		animatorClient.addLivingAnimation(LivingMotion.MOUNTED, Animations.BIPED_HORSEBACK_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.BLOCKING, Animations.BIPED_BLOCK);
		animatorClient.addLivingAnimation(LivingMotion.DRINKING, Animations.BIPED_DRINK);
		animatorClient.setCurrentMotionsToDefault();
	}
	
	@Override
	public void setAttackGoals(WeaponCategory category, ResourceLocation moveset)
	{
		if (moveset.compareTo(WeaponMovesets.BLACK_KNIGHT_SWORD) != 0) return;
		this.orgEntity.goalSelector.addGoal(1, new AttackGoal(this, 0.0F, true, false, true)
				.addAttack(new AttackInstance(4, 3.0F, Animations.BLACK_KNIGHT_SWORD_LA))
				.addDodge(Animations.BIPED_JUMP_BACK));
	}

	@Override
	public void updateMotion()
	{
		super.commonMotionUpdate();
	}
	
	@Override
	public DeathAnimation getDeathAnimation(ExtendedDamageSource dmgSource)
	{
		return Animations.BLACK_KNIGHT_DEATH;
	}
}
