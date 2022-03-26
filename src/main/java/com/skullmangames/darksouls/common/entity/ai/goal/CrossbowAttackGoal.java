package com.skullmangames.darksouls.common.entity.ai.goal;

import java.util.EnumSet;

import com.skullmangames.darksouls.common.capability.entity.HumanoidCap;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCPlayAnimation;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class CrossbowAttackGoal<T extends Mob & CrossbowAttackMob, D extends HumanoidCap<T>> extends Goal
{
	private final T mob;
	private final D mobdata;
	private int attackCooldown;
	
	private Vec3 targetPos = Vec3.ZERO;
	
	private CrossbowState crossbowState = CrossbowState.UNCHARGED;

	public CrossbowAttackGoal(D mobdata)
	{
		super();
		this.mobdata = mobdata;
		this.mob = mobdata.getOriginalEntity();
		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}

	@Override
	public void stop()
	{
		if (this.mob.isUsingItem()) this.mob.stopUsingItem();

		if (this.mob.isHolding(is -> is.getItem() instanceof CrossbowItem))
		{
			this.mob.setChargingCrossbow(false);
			CrossbowItem.setCharged(this.mob.getUseItem(), false);
		}
		
		this.mob.getNavigation().stop();
	}
	
	@Override
	public void tick()
	{
		LivingEntity target = this.mob.getTarget();
		if (target == null) return;
		if (this.mob.distanceToSqr(target) > 50) this.move(target);
		else this.crossbowAttack(target);
	}
	
	private void move(LivingEntity target)
	{
		if (this.mobdata.isInaction() || (this.targetPos != Vec3.ZERO && target.distanceToSqr(this.targetPos) <= 1.0D)) return;
		this.targetPos = target.position();
		Path path = this.mob.getNavigation().createPath(target, 8);
		this.mob.getNavigation().moveTo(path, 1.0D);
	}

	private void crossbowAttack(LivingEntity target)
	{
		this.mobdata.rotateTo(target, 60, false);
		if (this.crossbowState == CrossbowState.UNCHARGED)
		{
			ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimation(Animations.BIPED_CROSSBOW_RELOAD.getId(), mob.getId(), 0.0F, true), mob);
			this.mob.startUsingItem(
					ProjectileUtil.getWeaponHoldingHand(this.mob, item -> item instanceof CrossbowItem));
			this.crossbowState = CrossbowState.CHARGING;
			this.mob.setChargingCrossbow(true);
		} else if (this.crossbowState == CrossbowState.CHARGING)
		{
			if (!this.mob.isUsingItem())
			{
				this.crossbowState = CrossbowState.UNCHARGED;
			}

			int i = this.mob.getTicksUsingItem();
			ItemStack itemstack = this.mob.getUseItem();
			if (i >= CrossbowItem.getChargeDuration(itemstack))
			{
				this.mob.releaseUsingItem();
				this.crossbowState = CrossbowState.CHARGED;
				this.attackCooldown = 10;
				this.mob.setChargingCrossbow(false);
			}
		} else if (this.crossbowState == CrossbowState.CHARGED)
		{
			ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimation(Animations.BIPED_CROSSBOW_AIM.getId(), mob.getId(), 0.0F, true), mob);
			--this.attackCooldown;
			if (this.attackCooldown == 0)
			{
				this.crossbowState = CrossbowState.READY_TO_ATTACK;
			}
		} else if (this.crossbowState == CrossbowState.READY_TO_ATTACK)
		{
			this.mob.performRangedAttack(target, 1.0F);
			ItemStack itemstack1 = this.mob
					.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this.mob, item -> item instanceof CrossbowItem));
			CrossbowItem.setCharged(itemstack1, false);
			this.crossbowState = CrossbowState.UNCHARGED;
			ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimation(Animations.BIPED_CROSSBOW_SHOT.getId(), mob.getId(), 0.0F, true), mob);
		}
	}
	
	@Override
	public boolean canUse()
	{
		LivingEntity target = this.mob.getTarget();
		if (target == null) return false;
		return this.mob.isHolding(is -> is.getItem() instanceof CrossbowItem);
	}
	
	@Override
	public boolean canContinueToUse()
	{
		LivingEntity target = this.mob.getTarget();
		if (target == null) return false;
		return this.mob.isHolding(is -> is.getItem() instanceof CrossbowItem);
	}

	private static enum CrossbowState
	{
		UNCHARGED, CHARGING, CHARGED, READY_TO_ATTACK;
	}
}
