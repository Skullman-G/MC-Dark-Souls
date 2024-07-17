package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.client.animation.ClientAnimator;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.capability.item.WeaponCap;
import com.skullmangames.darksouls.common.entity.BerenikeKnight;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackInstance;
import com.skullmangames.darksouls.common.entity.ai.goal.DrinkingEstusGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.SwitchWeaponGoal;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.util.WeaponCategory;
import com.skullmangames.darksouls.core.util.collider.Collider;

import net.minecraft.world.InteractionHand;

public class BerenikeKnightCap extends HumanoidCap<BerenikeKnight>
{
	public static final float WEAPON_SCALE = 1.1F;
	
	@Override
	public void initAnimator(ClientAnimator animatorClient)
	{
		animatorClient.putLivingAnimation(LivingMotion.IDLE, Animations.BERENIKE_KNIGHT_IDLE);
		animatorClient.putLivingAnimation(LivingMotion.WALKING, Animations.BALDER_KNIGHT_WALK);
		animatorClient.putLivingAnimation(LivingMotion.RUNNING, Animations.BALDER_KNIGHT_RUN);
		animatorClient.putLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		animatorClient.putLivingAnimation(LivingMotion.MOUNTED, Animations.BIPED_HORSEBACK_IDLE);
		animatorClient.putLivingAnimation(LivingMotion.BLOCKING, Animations.BALDER_KNIGHT_BLOCK);
		animatorClient.putLivingAnimation(LivingMotion.DRINKING, Animations.BIPED_DRINK);
		animatorClient.setCurrentMotionsToDefault();
	}
	
	@Override
	public void setAttackGoals(WeaponCategory category)
	{
		SwitchWeaponGoal weaponGoal = new SwitchWeaponGoal(this).addDefaultEstusCondition();
		this.orgEntity.goalSelector.addGoal(1, new DrinkingEstusGoal(this));
		
		if (category == WeaponCategory.ULTRA_GREATSWORD)
		{
			weaponGoal.addSwitchCondition(this.orgEntity.getMainHandItem(), () ->
			{
				return this.getTarget() != null && this.orgEntity.distanceTo(this.getTarget()) < 8D;
			});
			this.orgEntity.goalSelector.addGoal(1, new AttackGoal(this, 0.0F, true, false, true)
					.addAttack(new AttackInstance(1, 4.0F, Animations.BERENIKE_KNIGHT_SWORD_LA))
					.addAttack(new AttackInstance(1, 4.0F, Animations.BERENIKE_KNIGHT_SWORD_HA[0]))
					.addAttack(new AttackInstance(1, 4.0F, Animations.BERENIKE_KNIGHT_SWORD_HA[1]))
					.addAttack(new AttackInstance(1, 4.0F, Animations.BERENIKE_KNIGHT_SWORD_DA))
					.addAttack(new AttackInstance(1, 4.0F, Animations.BERENIKE_KNIGHT_KICK)));
		}
		else if (category == WeaponCategory.HAMMER)
		{
			weaponGoal.addSwitchCondition(this.orgEntity.getMainHandItem(), () ->
			{
				return this.getTarget() != null && this.orgEntity.distanceTo(this.getTarget()) < 8D;
			});
			this.orgEntity.goalSelector.addGoal(1, new AttackGoal(this, 0.0F, true, false, true)
					.addAttack(new AttackInstance(1, 4.0F, Animations.BERENIKE_KNIGHT_MACE_LA))
					.addAttack(new AttackInstance(1, 4.0F, Animations.BERENIKE_KNIGHT_MACE_HA))
					.addAttack(new AttackInstance(1, 6.0F, 8.0F, Animations.BERENIKE_KNIGHT_SWORD_DA)));
		}
		
		this.orgEntity.goalSelector.addGoal(0, weaponGoal);
	}
	
	@Override
	public float getWeaponScale()
	{
		WeaponCap weapon = this.getHeldWeaponCap(InteractionHand.MAIN_HAND);
		return weapon == null || weapon.getWeaponCategory() != WeaponCategory.ULTRA_GREATSWORD ? WEAPON_SCALE
				: 0.9F;
	}
	
	@Override
	public Collider getColliderMatching(InteractionHand hand)
	{
		Collider collider = super.getColliderMatching(hand);
		return collider == Colliders.ULTRA_GREATSWORD ? Colliders.BERENIKE_KNIGHT_ULTRA_GREATSWORD
				: collider == Colliders.MACE ? Colliders.BERENIKE_KNIGHT_MACE
				: collider;
	}

	@Override
	public void updateMotion()
	{
		super.commonMotionUpdate();
	}
	
	@Override
	public int getSoulReward()
	{
		return 800;
	}
	
	@Override
	public ShieldHoldType getShieldHoldType()
	{
		return ShieldHoldType.HORIZONTAL;
	}
}
