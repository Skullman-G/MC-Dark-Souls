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
import com.skullmangames.darksouls.core.init.data.Colliders;
import com.skullmangames.darksouls.core.util.WeaponCategory;
import com.skullmangames.darksouls.core.util.collider.Collider;
import net.minecraft.world.InteractionHand;

public class BerenikeKnightCap extends HumanoidCap<BerenikeKnight>
{
	public static final float WEAPON_SCALE = 1.1F;
	
	@Override
	public void initAnimator(ClientAnimator animatorClient)
	{
		animatorClient.putLivingAnimation(LivingMotion.IDLE, Animations.BERENIKE_KNIGHT_IDLE.get());
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
		
		if (category == WeaponCategory.ULTRA_GREATSWORD)
		{
			weaponGoal.addSwitchCondition(this.orgEntity.getMainHandItem(), () ->
			{
				return this.getTarget() != null && this.orgEntity.distanceTo(this.getTarget()) < 8D;
			});
			this.orgEntity.goalSelector.addGoal(1, new AttackGoal(this, 0.0F, true, false, true)
					.addAttack(new AttackInstance(1, 4.0F, Animations.BERENIKE_KNIGHT_SWORD_LA.get()))
					.addAttack(new AttackInstance(1, 4.0F, Animations.BERENIKE_KNIGHT_SWORD_HA.get()[0]))
					.addAttack(new AttackInstance(1, 4.0F, Animations.BERENIKE_KNIGHT_SWORD_HA.get()[1]))
					.addAttack(new AttackInstance(1, 4.0F, Animations.BERENIKE_KNIGHT_SWORD_DA.get()))
					.addAttack(new AttackInstance(1, 4.0F, Animations.BERENIKE_KNIGHT_KICK.get())));
		}
		else if (category == WeaponCategory.HAMMER)
		{
			weaponGoal.addSwitchCondition(this.orgEntity.getMainHandItem(), () ->
			{
				return this.getTarget() != null && this.orgEntity.distanceTo(this.getTarget()) < 8D;
			});
			this.orgEntity.goalSelector.addGoal(1, new AttackGoal(this, 0.0F, true, false, true)
					.addAttack(new AttackInstance(1, 4.0F, Animations.BERENIKE_KNIGHT_MACE_LA.get()))
					.addAttack(new AttackInstance(1, 4.0F, Animations.BERENIKE_KNIGHT_MACE_HA.get()))
					.addAttack(new AttackInstance(1, 6.0F, 8.0F, Animations.BERENIKE_KNIGHT_SWORD_DA.get())));
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
		return collider == Colliders.ULTRA_GREATSWORD.get() ? Colliders.BERENIKE_KNIGHT_ULTRA_GREATSWORD.get()
				: collider == Colliders.MACE.get() ? Colliders.BERENIKE_KNIGHT_MACE.get()
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
	public ShieldHoldType getShieldHoldType(InteractionHand hand)
	{
		return ShieldHoldType.HORIZONTAL;
	}
	
	@Override
	public float getModelScale()
	{
		return 1.5F;
	}
}
