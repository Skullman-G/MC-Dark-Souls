package com.skullmangames.darksouls.common.inventory.container;

import com.skullmangames.darksouls.common.item.EstusFlaskItem;
import com.skullmangames.darksouls.core.init.ModContainers;
import com.skullmangames.darksouls.core.init.ModItems;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.AbstractRepairContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;

public class ReinforceEstusFlaskContainer extends AbstractRepairContainer
{
	public ReinforceEstusFlaskContainer(int id, PlayerInventory inventory)
	{
		this(id, inventory, IWorldPosCallable.NULL);
	}
	
	public ReinforceEstusFlaskContainer(int id, PlayerInventory inventory, IWorldPosCallable access)
	{
		super(ModContainers.REINFORCE_ESTUS_FLASK.get(), id, inventory, access);
	}

	@Override
	protected boolean mayPickup(PlayerEntity p_39798_, boolean p_39799_)
	{
		return this.slots.get(0).getItem().getItem() == ModItems.ESTUS_FLASK.get()
				&& this.slots.get(1).getItem().getItem() == ModItems.ESTUS_SHARD.get();
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
	protected boolean isValidBlock(BlockState blockstate)
	{
		return true;
	}

	@Override
	public void createResult()
	{
		if (this.slots.get(0).getItem().getItem() == ModItems.ESTUS_FLASK.get()
				&& this.slots.get(1).getItem().getItem() == ModItems.ESTUS_SHARD.get())
		{
			int estuslevel = EstusFlaskItem.getTotalUses(this.inputSlots.getItem(0));
			ItemStack stack = new ItemStack(ModItems.ESTUS_FLASK.get());
			EstusFlaskItem.setTotalUses(stack, estuslevel + 1);
			this.resultSlots.setItem(0, stack);
		}
		else this.resultSlots.setItem(0, ItemStack.EMPTY);
	}
}
