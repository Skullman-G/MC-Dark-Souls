package com.skullmangames.darksouls.common.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class CastingHelper
{
	public static ActionResult<ItemStack> startCasting(World world, PlayerEntity playerentity, Hand hand)
	{
	    playerentity.startUsingItem(hand);
	    return ActionResult.consume(playerentity.getItemInHand(hand));
	}
	
	private static boolean shouldTriggerItemUseEffects(ItemStack itemstack, int durationremaining)
	{
		return itemstack.getUseDuration() - durationremaining == 1;
	}
	
	public static void triggerItemUseEffects(LivingEntity livingentity, ItemStack itemstack, int p_226293_2_, int durationremaining)
	{
	    if (shouldTriggerItemUseEffects(itemstack, durationremaining) && !itemstack.isEmpty() && livingentity.isUsingItem())
	    {
	    	DarksignItem castitem = (DarksignItem)itemstack.getItem();
	    	livingentity.playSound(castitem.getCastingSound(), 0.5F, 1.0F);
	    	makeParticles(livingentity);
	    }
	}
	
	private static void makeParticles(LivingEntity livingentity)
	{
		double z = 1.0D;
		for (double x = 0.0D; x < 1.0D; x += 0.1D)
		{
			if (livingentity.level instanceof ServerWorld)
		    {
		        ((ServerWorld)livingentity.level).sendParticles(ParticleTypes.FLAME, livingentity.position().x + x, livingentity.position().y, livingentity.position().z + z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
		        ((ServerWorld)livingentity.level).sendParticles(ParticleTypes.FLAME, livingentity.position().x - x, livingentity.position().y, livingentity.position().z - z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
		    }
		    else
		    {
		    	livingentity.level.addParticle(ParticleTypes.FLAME, livingentity.position().x + x, livingentity.position().y, livingentity.position().z + z, 0.0D, 0.0D, 0.0D);
		    	livingentity.level.addParticle(ParticleTypes.FLAME, livingentity.position().x - x, livingentity.position().y, livingentity.position().z - z, 0.0D, 0.0D, 0.0D);
		    }
			z -= 0.1D;
		}
	}
}
