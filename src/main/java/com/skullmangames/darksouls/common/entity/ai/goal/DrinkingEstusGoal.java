package com.skullmangames.darksouls.common.entity.ai.goal;

import java.util.EnumSet;

import com.skullmangames.darksouls.common.capability.entity.MobCap;
import com.skullmangames.darksouls.common.item.EstusFlaskItem;
import com.skullmangames.darksouls.core.init.ModItems;

import net.minecraft.util.Hand;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

public class DrinkingEstusGoal extends Goal
{
	private final MobCap<?> mobCap;
	private final MobEntity mob;
	private final ItemStack estusFlask;
	private ItemStack prevItem;
	private int recovery;

	public DrinkingEstusGoal(MobCap<?> mobCap)
	{
		this.mobCap = mobCap;
		this.mob = mobCap.getOriginalEntity();
		this.estusFlask = ModItems.ESTUS_FLASK.get().getDefaultInstance();
		EstusFlaskItem.setTotalUses(this.estusFlask, 2);
		EstusFlaskItem.setUses(this.estusFlask, 2);
		EstusFlaskItem.setHeal(this.estusFlask, 2);
		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}

	public boolean canUse()
	{
		return !this.mobCap.isInaction() && EstusFlaskItem.getUses(this.estusFlask) > 0
				&& (this.mob.getHealth() / this.mob.getMaxHealth()) < 0.5F
				&& (this.mob.getTarget() == null || this.mob.distanceToSqr(this.mob.getTarget().position()) >= 50);
	}

	public boolean canContinueToUse()
	{
		return this.mob.isUsingItem() || --this.recovery > 0;
	}

	public void start()
	{
		this.prevItem = this.mob.getItemBySlot(EquipmentSlotType.MAINHAND);
		this.mob.setItemSlot(EquipmentSlotType.MAINHAND, this.estusFlask);
		this.mob.startUsingItem(Hand.MAIN_HAND);
	}
	
	@Override
	public void tick()
	{
		if (this.mob.getUseItemRemainingTicks() < 10 && this.mob.getUseItemRemainingTicks() > 0) this.recovery = 5;
	}

	public void stop()
	{
		this.mob.setItemSlot(EquipmentSlotType.MAINHAND, this.prevItem);
	}
}
