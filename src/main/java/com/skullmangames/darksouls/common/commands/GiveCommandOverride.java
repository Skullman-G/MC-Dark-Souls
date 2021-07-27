package com.skullmangames.darksouls.common.commands;

import java.util.Collection;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.skullmangames.darksouls.core.init.ItemInit;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ItemArgument;
import net.minecraft.command.arguments.ItemInput;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TranslationTextComponent;

public class GiveCommandOverride
{
	public static void register(CommandDispatcher<CommandSource> p_198494_0_)
	{
	      p_198494_0_.register(Commands.literal("give").requires((p_198496_0_) ->
	      {
	         return p_198496_0_.hasPermission(2);
	      }).then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("item", ItemArgument.item()).executes((p_198493_0_) ->
	      {
	         return giveItem(p_198493_0_.getSource(), ItemArgument.getItem(p_198493_0_, "item"), EntityArgument.getPlayers(p_198493_0_, "targets"), 1);
	      }).then(Commands.argument("count", IntegerArgumentType.integer(1)).executes((p_198495_0_) ->
	      {
	         return giveItem(p_198495_0_.getSource(), ItemArgument.getItem(p_198495_0_, "item"), EntityArgument.getPlayers(p_198495_0_, "targets"), IntegerArgumentType.getInteger(p_198495_0_, "count"));
	      })))));
	 }

	 private static int giveItem(CommandSource p_198497_0_, ItemInput iteminput, Collection<ServerPlayerEntity> p_198497_2_, int p_198497_3_) throws CommandSyntaxException
	 {
	      if (iteminput.getItem() != ItemInit.DARKSIGN.get())
	      {
	    	  for(ServerPlayerEntity serverplayerentity : p_198497_2_)
		      {
		         int i = p_198497_3_;

		         while(i > 0)
		         {
		            @SuppressWarnings("deprecation")
					int j = Math.min(iteminput.getItem().getMaxStackSize(), i);
		            i -= j;
		            ItemStack itemstack = iteminput.createItemStack(j, false);
		            boolean flag = serverplayerentity.inventory.add(itemstack);
		            if (flag && itemstack.isEmpty())
		            {
		               itemstack.setCount(1);
		               ItemEntity itementity1 = serverplayerentity.drop(itemstack, false);
		               if (itementity1 != null)
		               {
		                  itementity1.makeFakeItem();
		               }

		               serverplayerentity.level.playSound((PlayerEntity)null, serverplayerentity.getX(), serverplayerentity.getY(), serverplayerentity.getZ(), SoundEvents.ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((serverplayerentity.getRandom().nextFloat() - serverplayerentity.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
		               serverplayerentity.inventoryMenu.broadcastChanges();
		            }
		            else
		            {
		               ItemEntity itementity = serverplayerentity.drop(itemstack, false);
		               if (itementity != null)
		               {
		                  itementity.setNoPickUpDelay();
		                  itementity.setOwner(serverplayerentity.getUUID());
		               }
		            }
		         }
		      }
	    	  
	    	  if (p_198497_2_.size() == 1)
		      {
			     p_198497_0_.sendSuccess(new TranslationTextComponent("commands.give.success.single", p_198497_3_, iteminput.createItemStack(p_198497_3_, false).getDisplayName(), p_198497_2_.iterator().next().getDisplayName()), true);
			  }
		      else
		      {
			     p_198497_0_.sendSuccess(new TranslationTextComponent("commands.give.success.single", p_198497_3_, iteminput.createItemStack(p_198497_3_, false).getDisplayName(), p_198497_2_.size()), true);
			  }
	      }
	      else
	      {
	    	  if (p_198497_2_.size() == 1)
		      {
			     p_198497_0_.sendSuccess(new TranslationTextComponent("commands.give.fail.darksign"), true);
			  }
		      else
		      {
			     p_198497_0_.sendSuccess(new TranslationTextComponent("commands.give.fail.darksign"), true);
			  }
	      }

		  return p_198497_2_.size();
	 }
}
