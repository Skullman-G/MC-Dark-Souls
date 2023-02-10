package com.skullmangames.darksouls.common.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ItemUser
{
	public static ActionResult<ItemStack> startUsing(HasDarkSoulsUseAction item, World level, PlayerEntity playerentity, Hand hand)
	{
	    playerentity.startUsingItem(hand);
	    return ActionResult.consume(playerentity.getItemInHand(hand));
	}
	
	private static boolean shouldTriggerItemUseEffects(ItemStack itemstack, int durationremaining)
	{
		return itemstack.getUseDuration() - durationremaining == 1;
	}
	
	public static void triggerItemUseEffects(LivingEntity livingentity, ItemStack itemstack, HasDarkSoulsUseAction item, int durationremaining)
	{
	    if (shouldTriggerItemUseEffects(itemstack, durationremaining) && !itemstack.isEmpty() && livingentity.isUsingItem())
	    {
	    	switch (item.getDarkSoulsUseAnimation())
	    	{
		    	case MIRACLE:
		    		livingentity.playSound(item.getUseSound(), 0.5F, 1.0F);
		    		break;
		    		
		    	case SOUL_CONTAINER:
		    		livingentity.playSound(item.getUseSound(), 0.5F, 1.0F);
		    		break;
				
		    	case DARKSIGN:
		    		livingentity.playSound(item.getUseSound(), 0.5F, 1.0F);
		    		makeDarksignParticles(livingentity);
		    		
		    	default:
					break;
	    	}
	    }
	}
	
	private static void makeDarksignParticles(LivingEntity livingentity)
	{
		double z = 1.0D;
		for (double x = 0.0D; x < 1.0D;)
		{
			if (livingentity.level instanceof ServerWorld)
		    {
		        ServerWorld serverworld = (ServerWorld)livingentity.level;
		        serverworld.sendParticles(ParticleTypes.FLAME, livingentity.position().x + x, livingentity.position().y, livingentity.position().z + z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
		        serverworld.sendParticles(ParticleTypes.FLAME, livingentity.position().x - x, livingentity.position().y, livingentity.position().z - z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
		        serverworld.sendParticles(ParticleTypes.FLAME, livingentity.position().x + x, livingentity.position().y, livingentity.position().z - z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
		        serverworld.sendParticles(ParticleTypes.FLAME, livingentity.position().x - x, livingentity.position().y, livingentity.position().z + z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
		    }
		    else
		    {
		    	livingentity.level.addParticle(ParticleTypes.FLAME, livingentity.position().x + x, livingentity.position().y, livingentity.position().z + z, 0.0D, 0.0D, 0.0D);
		    	livingentity.level.addParticle(ParticleTypes.FLAME, livingentity.position().x - x, livingentity.position().y, livingentity.position().z - z, 0.0D, 0.0D, 0.0D);
		    	livingentity.level.addParticle(ParticleTypes.FLAME, livingentity.position().x + x, livingentity.position().y, livingentity.position().z - z, 0.0D, 0.0D, 0.0D);
		    	livingentity.level.addParticle(ParticleTypes.FLAME, livingentity.position().x - x, livingentity.position().y, livingentity.position().z + z, 0.0D, 0.0D, 0.0D);
		    }
			x += 0.1D;
			if (x <= 0.5D)
			{
				z -= 0.01D;
			}
			else
			{
				z -= 0.17D;
			}
		}
	}
}
