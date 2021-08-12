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
	//private static final Random random = new Random();
	
	public static ActionResult<ItemStack> startUsing(IHaveDarkSoulsUseAction item, World level, PlayerEntity playerentity, Hand hand)
	{
	    playerentity.startUsingItem(hand);
	    return ActionResult.consume(playerentity.getItemInHand(hand));
	}
	
	private static boolean shouldTriggerItemUseEffects(ItemStack itemstack, int durationremaining)
	{
		return itemstack.getUseDuration() - durationremaining == 1;
	}
	
	public static void triggerItemUseEffects(LivingEntity livingentity, ItemStack itemstack, IHaveDarkSoulsUseAction item, int durationremaining)
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
	
	/*private static void makeSoulContainerParticles(LivingEntity livingentity, ItemStack itemstack, int particleNumber)
	{
		for(int i = 0; i < particleNumber; ++i)
		{
			Vector3d vector3d = new Vector3d(((double)random.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
	        vector3d = vector3d.xRot(-livingentity.xRot * ((float)Math.PI / 180F));
	        vector3d = vector3d.yRot(-livingentity.yRot * ((float)Math.PI / 180F));
	        double d0 = (double)(-random.nextFloat()) * 0.6D - 0.3D;
	        Vector3d vector3d1 = new Vector3d(((double)random.nextFloat() - 0.5D) * 0.3D, d0, 0.6D);
	        vector3d1 = vector3d1.xRot(-livingentity.xRot * ((float)Math.PI / 180F));
	        vector3d1 = vector3d1.yRot(-livingentity.yRot * ((float)Math.PI / 180F));
	        vector3d1 = vector3d1.add(livingentity.getX(), livingentity.getEyeY(), livingentity.getZ());
	        
	        if (livingentity.level instanceof ServerWorld)
	        {
	             ((ServerWorld)livingentity.level).sendParticles(ParticleTypeInit.SOUL_CONTAINER.get(), vector3d1.x, vector3d1.y, vector3d1.z, 1, vector3d.x, vector3d.y + 0.05D, vector3d.z, 0.0D);
	        }
	        else
	        {
	        	livingentity.level.addParticle(ParticleTypeInit.SOUL_CONTAINER.get(), vector3d1.x, vector3d1.y, vector3d1.z, vector3d.x, vector3d.y + 0.05D, vector3d.z);
	        }
	    }
	}*/
	
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
