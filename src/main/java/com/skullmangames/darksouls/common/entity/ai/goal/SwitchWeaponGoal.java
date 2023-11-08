package com.skullmangames.darksouls.common.entity.ai.goal;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.MobCap;
import com.skullmangames.darksouls.common.item.EstusFlaskItem;
import com.skullmangames.darksouls.core.init.ModItems;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;

public class SwitchWeaponGoal extends Goal
{
	private final MobCap<?> mobCap;
	private final Mob mob;
	private final Map<ItemStack, Supplier<Boolean>> switchConditions = new HashMap<>();

	public SwitchWeaponGoal(MobCap<?> mobCap)
	{
		this.mobCap = mobCap;
		this.mob = mobCap.getOriginalEntity();
		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}
	
	public SwitchWeaponGoal addDefaultEstusCondition()
	{
		ItemStack estusFlask = ModItems.ESTUS_FLASK.get().getDefaultInstance();
		EstusFlaskItem.setTotalUses(estusFlask, 2);
		EstusFlaskItem.setUses(estusFlask, 2);
		EstusFlaskItem.setHeal(estusFlask, 2);
		
		return this.addSwitchCondition(estusFlask, () ->
				(this.mob.getHealth() / this.mob.getMaxHealth()) < 0.5F
				&& (this.mob.getTarget() == null || this.mob.distanceToSqr(this.mob.getTarget().position()) >= 50));
	}
	
	public SwitchWeaponGoal addSwitchCondition(ItemStack itemStack, Supplier<Boolean> condition)
	{
		this.switchConditions.put(itemStack, condition);
		return this;
	}

	public boolean canUse()
	{
		if (this.mobCap.isInaction()) return false;
		for (Map.Entry<ItemStack, Supplier<Boolean>> entry : this.switchConditions.entrySet())
		{
			if (!entry.getKey().sameItem(this.mob.getMainHandItem()) && entry.getValue().get())
			{
				return true;
			}
		}
		return false;
	}

	public boolean canContinueToUse()
	{
		return this.mobCap.isChangingItem();
	}

	@Override
	public void start()
	{
		if (this.mobCap.isChangingItem()) return;
		for (Map.Entry<ItemStack, Supplier<Boolean>> entry : this.switchConditions.entrySet())
		{
			if (!entry.getKey().sameItem(this.mob.getMainHandItem()) && entry.getValue().get())
			{
				this.mob.getMoveControl().setWantedPosition(this.mob.getX(), this.mob.getY(), this.mob.getZ(), 1.0D);
				this.mob.setDeltaMovement(0, 0, 0);
				this.mobCap.changeItemAnimated(InteractionHand.MAIN_HAND, entry.getKey());
				break;
			}
		}
	}
}
