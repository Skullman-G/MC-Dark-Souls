package com.skullmangames.darksouls.common.inventory.container;

import java.util.List;

import javax.annotation.Nullable;

import com.skullmangames.darksouls.common.item.crafting.ReinforceEstusFlaskRecipe;
import com.skullmangames.darksouls.core.init.ContainerTypeInit;
import com.skullmangames.darksouls.core.init.RecipeTypeInit;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.world.World;

public class ReinforceEstusFlaskContainer extends Container
{
	private final World level;
	
	@Nullable
	private ReinforceEstusFlaskRecipe selectedRecipe;
	private final IWorldPosCallable access;
	
	private final CraftResultInventory resultSlots = new CraftResultInventory();
	private final IInventory inputSlots = new Inventory(2)
	{
	      public void setChanged()
	      {
	         super.setChanged();
	         ReinforceEstusFlaskContainer.this.slotsChanged(this);
	      }
	};
	
	public ReinforceEstusFlaskContainer(int id, PlayerInventory inventory, IWorldPosCallable access)
	{
		this(ContainerTypeInit.REINFORCE_ESTUS_FLASK.get(), id, inventory, access);
	}
	
	public ReinforceEstusFlaskContainer(int id, PlayerInventory inventory)
	{
		this(ContainerTypeInit.REINFORCE_ESTUS_FLASK.get(), id, inventory, IWorldPosCallable.NULL);
	}
	
	public ReinforceEstusFlaskContainer(ContainerType<?> p_i50105_1_, int p_i50105_2_, PlayerInventory inventory, IWorldPosCallable access)
	{
		super(p_i50105_1_, p_i50105_2_);
		
		this.access = access;
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

	         public ItemStack onTake(PlayerEntity player, ItemStack itemstack)
	         {
	        	 itemstack.onCraftedBy(player.level, player, itemstack.getCount());
		   	     ReinforceEstusFlaskContainer.this.resultSlots.awardUsedRecipes(player);
		   	     ReinforceEstusFlaskContainer.this.shrinkStackInSlot(0);
		   	     ReinforceEstusFlaskContainer.this.shrinkStackInSlot(1);
		   	     ReinforceEstusFlaskContainer.this.access.execute((p_234653_0_, p_234653_1_) ->
		   	     {
		   	    	 p_234653_0_.levelEvent(1044, p_234653_1_, 0);
		   	     });
		   	     return itemstack;
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
		List<ReinforceEstusFlaskRecipe> list = this.level.getRecipeManager().getRecipesFor(RecipeTypeInit.REINFORCE_ESTUS_FLASK, this.inputSlots, this.level);
	    if (list.isEmpty())
	    {
	        this.resultSlots.setItem(0, ItemStack.EMPTY);
	    }
	    else
	    {
	       this.selectedRecipe = list.get(0);
	       ItemStack itemstack = this.selectedRecipe.assemble(this.inputSlots);
	       this.resultSlots.setRecipeUsed(this.selectedRecipe);
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
	
	@Override
	public ItemStack quickMoveStack(PlayerEntity p_82846_1_, int p_82846_2_)
	{
	      ItemStack itemstack = ItemStack.EMPTY;
	      Slot slot = this.slots.get(p_82846_2_);
	      if (slot != null && slot.hasItem()) {
	         ItemStack itemstack1 = slot.getItem();
	         itemstack = itemstack1.copy();
	         if (p_82846_2_ == 2) {
	            if (!this.moveItemStackTo(itemstack1, 3, 39, true)) {
	               return ItemStack.EMPTY;
	            }

	            slot.onQuickCraft(itemstack1, itemstack);
	         } else if (p_82846_2_ != 0 && p_82846_2_ != 1) {
	            if (p_82846_2_ >= 3 && p_82846_2_ < 39) {
	               int i = this.shouldQuickMoveToAdditionalSlot(itemstack) ? 1 : 0;
	               if (!this.moveItemStackTo(itemstack1, i, 2, false)) {
	                  return ItemStack.EMPTY;
	               }
	            }
	         } else if (!this.moveItemStackTo(itemstack1, 3, 39, false)) {
	            return ItemStack.EMPTY;
	         }

	         if (itemstack1.isEmpty()) {
	            slot.set(ItemStack.EMPTY);
	         } else {
	            slot.setChanged();
	         }

	         if (itemstack1.getCount() == itemstack.getCount()) {
	            return ItemStack.EMPTY;
	         }

	         slot.onTake(p_82846_1_, itemstack1);
	      }

	      return itemstack;
	}
	
	private boolean shouldQuickMoveToAdditionalSlot(ItemStack p_241210_1_)
	{
		List<ReinforceEstusFlaskRecipe> list = this.level.getRecipeManager().getRecipesFor(RecipeTypeInit.REINFORCE_ESTUS_FLASK, this.inputSlots, this.level);  
		return list.stream().anyMatch((p_241444_1_) ->
		{
	         return p_241444_1_.isAdditionIngredient(p_241210_1_);
	    });
	}
	
	@Override
	public void removed(PlayerEntity p_75134_1_)
	{
		super.removed(p_75134_1_);
	    this.access.execute((p_217068_2_, p_217068_3_) ->
	    {
	    	this.clearContainer(p_75134_1_, p_217068_2_, this.inputSlots);
	    });
	}
	
	@Override
	protected void clearContainer(PlayerEntity p_193327_1_, World p_193327_2_, IInventory p_193327_3_)
	{
		super.clearContainer(p_193327_1_, p_193327_2_, p_193327_3_);
	}
}
