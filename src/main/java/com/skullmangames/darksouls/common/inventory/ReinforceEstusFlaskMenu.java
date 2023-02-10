package com.skullmangames.darksouls.common.inventory;

import com.skullmangames.darksouls.common.item.EstusFlaskItem;
import com.skullmangames.darksouls.core.init.ModContainers;
import com.skullmangames.darksouls.core.init.ModItems;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.AbstractRepairContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;

public class ReinforceEstusFlaskMenu extends AbstractRepairContainer
{
	public ReinforceEstusFlaskMenu(int id, PlayerInventory inventory)
	{
		this(id, inventory, IWorldPosCallable.NULL);
	}
	
	public ReinforceEstusFlaskMenu(int id, PlayerInventory inventory, IWorldPosCallable access)
	{
		super(ModContainers.REINFORCE_ESTUS_FLASK.get(), id, inventory, access);
	}

	@Override
	protected boolean mayPickup(PlayerEntity player, boolean p_39799_)
	{
		return this.validResult();
	}
	
	private boolean validResult()
	{
		return (this.isEstusFlask() || this.isAshenEstusFlask()) && this.slots.get(1).getItem().getItem() == ModItems.ESTUS_SHARD.get();
	}
	
	private boolean isEstusFlask()
	{
		return this.slots.get(0).getItem().getItem() == ModItems.ESTUS_FLASK.get();
	}
	
	private boolean isAshenEstusFlask()
	{
		return this.slots.get(0).getItem().getItem() == ModItems.ASHEN_ESTUS_FLASK.get();
	}

	@Override
	protected ItemStack onTake(PlayerEntity player, ItemStack stack)
	{
		stack.onCraftedBy(player.level, player, stack.getCount());
		this.resultSlots.awardUsedRecipes(player);
		this.shrinkStackInSlot(0);
		this.shrinkStackInSlot(1);
		this.access.execute((p_40263_, p_40264_) ->
		{
			p_40263_.levelEvent(1044, p_40264_, 0);
		});
		return stack;
	}

	private void shrinkStackInSlot(int id)
	{
		ItemStack itemstack = this.inputSlots.getItem(id);
		itemstack.shrink(1);
		this.inputSlots.setItem(id, itemstack);
	}

	@Override
	protected boolean isValidBlock(BlockState p_39788_)
	{
		return true;
	}

	@Override
	public void createResult()
	{
		if (!this.validResult())
		{
			this.resultSlots.setItem(0, ItemStack.EMPTY);
			return;
		}
		int estuslevel = EstusFlaskItem.getTotalUses(this.inputSlots.getItem(0));
		ItemStack stack = this.isAshenEstusFlask() ? new ItemStack(ModItems.ASHEN_ESTUS_FLASK.get()) : new ItemStack(ModItems.ESTUS_FLASK.get());
		EstusFlaskItem.setTotalUses(stack, estuslevel + 1);
		this.resultSlots.setItem(0, stack);
	}
}
