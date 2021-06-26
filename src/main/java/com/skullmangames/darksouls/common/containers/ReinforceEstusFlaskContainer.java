package com.skullmangames.darksouls.common.containers;

import java.util.List;

import javax.annotation.Nullable;

import com.skullmangames.darksouls.common.entities.FireKeeperEntity;
import com.skullmangames.darksouls.common.items.EstusFlaskItem;
import com.skullmangames.darksouls.core.init.ContainerTypeInit;
import com.skullmangames.darksouls.core.init.ItemInit;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.SmithingRecipe;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.world.World;

public class ReinforceEstusFlaskContainer extends Container
{
	private final World level;
	
	private final FireKeeperEntity fireKeeper;
	
	@Nullable
	private SmithingRecipe selectedRecipe;
	protected final IWorldPosCallable access = IWorldPosCallable.NULL;
	
	protected final CraftResultInventory resultSlots = new CraftResultInventory();
	protected final IInventory inputSlots = new Inventory(2)
	{
	      public void setChanged()
	      {
	         super.setChanged();
	         ReinforceEstusFlaskContainer.this.slotsChanged(this);
	      }
	 };
	
	public ReinforceEstusFlaskContainer(int id, PlayerInventory inventory, FireKeeperEntity firekeeper)
	{
		this(ContainerTypeInit.REINFORCE_ESTUS_FLASK.get(), id, inventory, firekeeper);
	}
	
	public ReinforceEstusFlaskContainer(int id, PlayerInventory inventory)
	{
		this(ContainerTypeInit.REINFORCE_ESTUS_FLASK.get(), id, inventory, null);
	}
	
	public ReinforceEstusFlaskContainer(ContainerType<?> p_i50105_1_, int p_i50105_2_, PlayerInventory inventory, @Nullable FireKeeperEntity firekeeper)
	{
		super(p_i50105_1_, p_i50105_2_);
		
		this.addSlot(new Slot(this.inputSlots, 0, 27, 47));
	    this.addSlot(new Slot(this.inputSlots, 1, 76, 47));
	    this.addSlot(new Slot(this.resultSlots, 2, 134, 47)
	    {
	         public boolean mayPlace(ItemStack p_75214_1_)
	         {
	            return false;
	         }

	         public boolean mayPickup(PlayerEntity p_82869_1_)
	         {
	            return ReinforceEstusFlaskContainer.this.mayPickup(p_82869_1_, this.hasItem());
	         }

	         public ItemStack onTake(PlayerEntity p_190901_1_, ItemStack p_190901_2_)
	         {
	            return ReinforceEstusFlaskContainer.this.onTake(p_190901_1_, p_190901_2_);
	         }
	    });
	    
	    for(int i = 0; i < 3; ++i)
	    {
	       for(int j = 0; j < 9; ++j)
	       {
	          this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
	       }
	    }

	    for(int k = 0; k < 9; ++k)
	    {
	       this.addSlot(new Slot(inventory, k, 8 + k * 18, 142));
	    }
	    
	    this.level = inventory.player.level;
	    
	    if (firekeeper != null)
	    {
		    this.fireKeeper = firekeeper;
		    this.fireKeeper.talking = true;
	    }
	    else
	    {
	    	this.fireKeeper = null;
	    }
	}
	
	@Override
	public void slotsChanged(IInventory p_75130_1_)
	{
		super.slotsChanged(p_75130_1_);
	    if (p_75130_1_ == this.inputSlots)
	    {
	       this.createResult();
	    }
	}
	
	public void createResult()
	{
		List<SmithingRecipe> list = this.level.getRecipeManager().getRecipesFor(IRecipeType.SMITHING, this.inputSlots, this.level);
	    if (list.isEmpty())
	    {
	        this.resultSlots.setItem(0, ItemStack.EMPTY);
	    }
	    else
	    {
	       this.selectedRecipe = list.get(0);
	       ItemStack itemstack = this.selectedRecipe.assemble(this.inputSlots);
	       this.resultSlots.setRecipeUsed(this.selectedRecipe);
	       if (itemstack.getItem() instanceof EstusFlaskItem)
	       {
	    	   if (this.inputSlots.getItem(0).getItem() instanceof EstusFlaskItem && this.inputSlots.getItem(1).getItem() == ItemInit.ESTUS_SHARD.get() && EstusFlaskItem.getTotalUses(itemstack) < 20)
	    	   {
	    		   EstusFlaskItem.setTotalUses(itemstack, EstusFlaskItem.getTotalUses(itemstack) + 1);
	    		   EstusFlaskItem.setUses(itemstack, EstusFlaskItem.getTotalUses(itemstack));
	    	   }
	    	   else if (EstusFlaskItem.getTotalUses(itemstack) >= 20)
	    	   {
	    		   this.resultSlots.setItem(0, ItemStack.EMPTY);
	    		   return;
	    	   }
	       }
	       this.resultSlots.setItem(0, itemstack);
	    }
	}

	protected ItemStack onTake(PlayerEntity player, ItemStack stack)
	{
		stack.onCraftedBy(player.level, player, stack.getCount());
	    this.resultSlots.awardUsedRecipes(player);
	    this.shrinkStackInSlot(0);
	    this.shrinkStackInSlot(1);
	    this.access.execute((p_234653_0_, p_234653_1_) ->
	    {
	       p_234653_0_.levelEvent(1044, p_234653_1_, 0);
	    });
	    return stack;
	}
	
	private void shrinkStackInSlot(int p_234654_1_)
	{
	    ItemStack itemstack = this.inputSlots.getItem(p_234654_1_);
	    itemstack.shrink(1);
	    this.inputSlots.setItem(p_234654_1_, itemstack);
	}

	protected boolean mayPickup(PlayerEntity p_82869_1_, boolean hasItem)
	{
		return this.selectedRecipe != null && this.selectedRecipe.matches(this.inputSlots, this.level);
	}

	@Override
	public boolean stillValid(PlayerEntity p_75145_1_)
	{
		return true;
	}
	
	@Override
	public Slot getSlot(int p_75139_1_)
	{
		return super.getSlot(p_75139_1_);
	}
	
	public void stopTalking()
	{
		System.out.print("helo");
		if (this.fireKeeper != null)
		{
			System.out.print("mekaka");
			this.fireKeeper.talking = false;
		}
	}
}
