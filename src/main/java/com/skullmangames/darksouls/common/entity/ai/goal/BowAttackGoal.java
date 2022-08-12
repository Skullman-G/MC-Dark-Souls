package com.skullmangames.darksouls.common.entity.ai.goal;

import com.skullmangames.darksouls.common.capability.entity.HumanoidCap;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCPlayAnimation;

import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.BowItem;

public class BowAttackGoal<T extends MobEntity & IRangedAttackMob, D extends HumanoidCap<T>> extends RangeAttackGoal<T, D>
{
	public BowAttackGoal(D entityCap, int attackCooldown, float maxAttackDist)
	{
		super(entityCap, attackCooldown, maxAttackDist);
	}
	
	protected boolean isHoldingRightWeapon()
    {
        return this.mob.getMainHandItem().getItem() instanceof BowItem;
    }

	@Override
	protected void performAttack()
	{
		int i = this.mob.getTicksUsingItem();
        if (i >= 20)
        {
            this.mob.stopUsingItem();
            ((IRangedAttackMob)this.mob).performRangedAttack(this.chasingTarget, BowItem.getPowerForTime(i));
            ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimation(Animations.BIPED_BOW_REBOUND, mob.getId(), 0.0F), mob);
            this.attackTime = this.attackCooldown;
        }
	}

	@Override
	protected void aim()
	{
		ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimation(Animations.BIPED_BOW_AIM, mob.getId(), 0.0F), mob);
        this.mob.startUsingItem(ProjectileHelper.getWeaponHoldingHand(this.mob, item -> item instanceof BowItem));
	}
}
