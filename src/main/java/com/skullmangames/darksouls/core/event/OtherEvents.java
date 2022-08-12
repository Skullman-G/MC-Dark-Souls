package com.skullmangames.darksouls.core.event;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.entity.SoulEntity;
import com.skullmangames.darksouls.core.init.ModItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = DarkSouls.MOD_ID, bus = Bus.FORGE)
public class OtherEvents
{
	@SubscribeEvent
	public static void onItemToss(final ItemTossEvent event)
    {
		if (event.getEntityItem().getItem().getItem() == ModItems.DARKSIGN.get())
		{
			event.getPlayer().addItem(event.getEntityItem().getItem());
		}
    }
	
	@SubscribeEvent
	public static void onCheckSpawn(final CheckSpawn event)
	{
		if (event.getEntityLiving() instanceof ZombieEntity)
		{
			event.setResult(Result.DENY);
		}
	}
	
	@SubscribeEvent
	public static void onLivingExperienceDrop(final LivingExperienceDropEvent event)
	{
		LivingEntity entity = event.getEntityLiving();
		entity.level.addFreshEntity(new SoulEntity(entity.level, entity.getX(), entity.getY(), entity.getZ(), event.getDroppedExperience()));
	}
	
	@SubscribeEvent
	public static void onFOVUpdate(final FOVUpdateEvent event)
	{
		 PlayerEntity player = event.getEntity();
		float f = 1.0F;
	      if (player.abilities.flying) {
	         f *= 1.1F;
	      }

	      f = (float)((double)f * ((player.getAttributeValue(Attributes.MOVEMENT_SPEED) / (double)player.abilities.getWalkingSpeed() + 1.0D) / 2.0D));
	      if (player.getAttributeValue(Attributes.MOVEMENT_SPEED) == 0.0D || player.abilities.getWalkingSpeed() == 0.0F || Float.isNaN(f) || Float.isInfinite(f)) {
	         f = 1.0F;
	      }

	      if (player.isUsingItem() && player.getUseItem().getItem() == Items.BOW) {
	         int i = player.getTicksUsingItem();
	         float f1 = (float)i / 20.0F;
	         if (f1 > 1.0F) {
	            f1 = 1.0F;
	         } else {
	            f1 = f1 * f1;
	         }

	         f *= 1.0F - f1 * 0.15F;
	      }
	      
	     event.setNewfov(f);
	}
}
