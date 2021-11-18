package com.skullmangames.darksouls.common.command;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.skullmangames.darksouls.core.init.ModItems;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ItemPredicateArgument;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;

public class ClearCommandOverride
{
	private static final DynamicCommandExceptionType ERROR_SINGLE = new DynamicCommandExceptionType((p_208785_0_) ->
	{
	      return new TranslationTextComponent("clear.failed.single", p_208785_0_);
	});
	
	private static final DynamicCommandExceptionType ERROR_MULTIPLE = new DynamicCommandExceptionType((p_208787_0_) ->
	{
	   return new TranslationTextComponent("clear.failed.multiple", p_208787_0_);
	});
	
	public static void register(CommandDispatcher<CommandSource> p_198243_0_)
	{
	      p_198243_0_.register(Commands.literal("clear").requires((p_198247_0_) ->
	      {
	         return p_198247_0_.hasPermission(2);
	      }).executes((p_198241_0_) -> {
	         return clearInventory(p_198241_0_.getSource(), Collections.singleton(p_198241_0_.getSource().getPlayerOrException()), (p_198248_0_) ->
	         {
	            return true;
	         }, -1);
	      }).then(Commands.argument("targets", EntityArgument.players()).executes((p_198245_0_) -> {
	         return clearInventory(p_198245_0_.getSource(), EntityArgument.getPlayers(p_198245_0_, "targets"), (p_198242_0_) ->
	         {
	            return true;
	         }, -1);
	      }).then(Commands.argument("item", ItemPredicateArgument.itemPredicate()).executes((p_198240_0_) ->
	      {
	         return clearInventory(p_198240_0_.getSource(), EntityArgument.getPlayers(p_198240_0_, "targets"), ItemPredicateArgument.getItemPredicate(p_198240_0_, "item"), -1);
	      }).then(Commands.argument("maxCount", IntegerArgumentType.integer(0)).executes((p_198246_0_) ->
	      {
	         return clearInventory(p_198246_0_.getSource(), EntityArgument.getPlayers(p_198246_0_, "targets"), ItemPredicateArgument.getItemPredicate(p_198246_0_, "item"), IntegerArgumentType.getInteger(p_198246_0_, "maxCount"));
	      })))));
	 }
	
	private static int clearInventory(CommandSource p_198244_0_, Collection<ServerPlayerEntity> p_198244_1_, Predicate<ItemStack> p_198244_2_, int p_198244_3_) throws CommandSyntaxException
	{
	      int i = 0;

	      for(ServerPlayerEntity serverplayerentity : p_198244_1_) {
	         i += clearOrCountMatchingItems(serverplayerentity.inventory, p_198244_2_, p_198244_3_, serverplayerentity.inventoryMenu.getCraftSlots());
	         serverplayerentity.containerMenu.broadcastChanges();
	         serverplayerentity.inventoryMenu.slotsChanged(serverplayerentity.inventory);
	         serverplayerentity.broadcastCarriedItem();
	      }

	      if (i == 0) {
	         if (p_198244_1_.size() == 1) {
	            throw ERROR_SINGLE.create(p_198244_1_.iterator().next().getName());
	         } else {
	            throw ERROR_MULTIPLE.create(p_198244_1_.size());
	         }
	      } else {
	         if (p_198244_3_ == 0) {
	            if (p_198244_1_.size() == 1) {
	               p_198244_0_.sendSuccess(new TranslationTextComponent("commands.clear.test.single", i, p_198244_1_.iterator().next().getDisplayName()), true);
	            } else {
	               p_198244_0_.sendSuccess(new TranslationTextComponent("commands.clear.test.multiple", i, p_198244_1_.size()), true);
	            }
	         } else if (p_198244_1_.size() == 1) {
	            p_198244_0_.sendSuccess(new TranslationTextComponent("commands.clear.success.single", i, p_198244_1_.iterator().next().getDisplayName()), true);
	         } else {
	            p_198244_0_.sendSuccess(new TranslationTextComponent("commands.clear.success.multiple", i, p_198244_1_.size()), true);
	         }

	         return i;
	      }
	}
	
	public static int clearOrCountMatchingItems(PlayerInventory playerinventory, Predicate<ItemStack> predicate, int p_234564_2_, IInventory inventory)
	{
		int i = 0;
	    boolean flag = p_234564_2_ == 0;
	    i = i + clearOrCountMatchingItems(playerinventory, predicate, p_234564_2_ - i, flag);
	    i = i + clearOrCountMatchingItems(inventory, predicate, p_234564_2_ - i, flag);
	    i = i + clearOrCountMatchingItems(playerinventory.getCarried(), predicate, p_234564_2_ - i, flag);
	    if (playerinventory.getCarried().isEmpty())
	    {
	    	playerinventory.setCarried(ItemStack.EMPTY);
	    }

	    return i;
	}
	
	private static int clearOrCountMatchingItems(IInventory inventory, Predicate<ItemStack> predicate, int p_233534_2_, boolean flag)
	{
	    int i = 0;

	    for(int j = 0; j < inventory.getContainerSize(); ++j)
	    {
	    	ItemStack itemstack = inventory.getItem(j);
	    	int k = clearOrCountMatchingItems(itemstack, predicate, p_233534_2_ - i, flag);
	    	if (k > 0 && !flag && itemstack.isEmpty())
	    	{
	    		inventory.setItem(j, ItemStack.EMPTY);
	    	}

	    	i += k;
	    }

	    return i;
	 }

	 private static int clearOrCountMatchingItems(ItemStack itemstack, Predicate<ItemStack> predicate, int p_233535_2_, boolean flag)
	 {
	    if (!itemstack.isEmpty() && predicate.test(itemstack) && itemstack.getItem() != ModItems.DARKSIGN.get())
	    {
	       if (flag)
	       {
	          return itemstack.getCount();
	       }
	       else
	       {
	          int i = p_233535_2_ < 0 ? itemstack.getCount() : Math.min(p_233535_2_, itemstack.getCount());
	          itemstack.shrink(i);
	          return i;
	       }
	    }
	    else
	    {
	       return 0;
	    }
	 }
}
