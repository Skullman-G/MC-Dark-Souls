package com.skullmangames.darksouls.common.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class CastingHelper
{
	public static ActionResult<ItemStack> startCasting(World world, PlayerEntity playerentity, Hand hand)
	{
	    playerentity.startUsingItem(hand);
	    return ActionResult.consume(playerentity.getItemInHand(hand));
	}
}
