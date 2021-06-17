package com.skullmangames.darksouls.common.items;

import com.skullmangames.darksouls.core.init.SoundEventInit;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class DarksignItem extends DescriptionItem implements IHaveDarkSoulsUseAction
{
	public DarksignItem(Properties properties)
	{
		super(properties.stacksTo(1));
	}
	
	@Override
	public boolean onDroppedByPlayer(ItemStack item, PlayerEntity player)
	{
		player.addItem(item);
		return false;
	}
	
	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand)
	{
		return ItemUser.startUsing(this, world, player, hand);
	}
	
	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, World world, LivingEntity livingentity)
	{
		PlayerEntity playerentity = livingentity instanceof PlayerEntity ? (PlayerEntity)livingentity : null;
	      
	    // SERVER SIDE
	    if (!world.isClientSide && livingentity instanceof ServerPlayerEntity)
	    {
	    	ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)livingentity;
	    	CriteriaTriggers.CONSUME_ITEM.trigger(serverplayerentity, itemstack);
	    	
	    	if (serverplayerentity.getRespawnPosition() != null)
	    	{
	    		serverplayerentity.teleportTo(serverplayerentity.getRespawnPosition().getX(), serverplayerentity.getRespawnPosition().getY(), serverplayerentity.getRespawnPosition().getZ());
	    	}
	    	else
	    	{
	    		serverplayerentity.sendMessage(new TranslationTextComponent("gui.darksouls.darksign_didnt_work"), Util.NIL_UUID);
	    	}
		}

	    if (playerentity != null)
	    {
	        playerentity.awardStat(Stats.ITEM_USED.get(this));
	    }

	      return itemstack;
	}
	
	@Override
	public int getUseDuration(ItemStack itemstack)
	{
		return 32;
	}
	
	@Override
	public void onUseTick(World world, LivingEntity livingentity, ItemStack itemstack, int durationremaining)
	{
		ItemUser.triggerItemUseEffects(livingentity, itemstack, this, durationremaining);
	}

	@Override
	public DarkSoulsUseAction getDarkSoulsUseAnimation()
	{
		return DarkSoulsUseAction.MIRACLE;
	}

	@Override
	public SoundEvent getUseSound()
	{
		return SoundEventInit.DARKSIGN_USE.get();
	}
}
