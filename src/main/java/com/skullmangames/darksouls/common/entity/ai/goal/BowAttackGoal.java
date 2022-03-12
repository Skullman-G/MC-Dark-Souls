package com.skullmangames.darksouls.common.entity.ai.goal;

import com.skullmangames.darksouls.common.capability.entity.HumanoidData;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCPlayAnimation;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;

public class BowAttackGoal<T extends Mob & RangedAttackMob, D extends HumanoidData<T>> extends RangeAttackGoal<T, D>
{
	public BowAttackGoal(D entitydata, int attackCooldown, float maxAttackDist)
	{
		super(entitydata, attackCooldown, maxAttackDist);
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
            ((RangedAttackMob)this.mob).performRangedAttack(this.chasingTarget, BowItem.getPowerForTime(i));
            ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimation(Animations.BIPED_BOW_REBOUND.getId(), mob.getId(), 0.0F, true), mob);
            this.attackTime = this.attackCooldown;
        }
	}

	@Override
	protected void aim()
	{
		ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimation(Animations.BIPED_BOW_AIM.getId(), mob.getId(), 0.0F, true), mob);
        this.mob.startUsingItem(ProjectileUtil.getWeaponHoldingHand(this.mob, item -> item instanceof BowItem));
	}
}
