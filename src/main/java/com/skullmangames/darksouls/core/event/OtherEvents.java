package com.skullmangames.darksouls.core.event;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.entity.SoulEntity;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.init.ModItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.event.FOVModifierEvent;
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
		if (event.getEntityLiving() instanceof Zombie)
		{
			event.setResult(Result.DENY);
		}
	}
	
	@SubscribeEvent
	public static void onLivingExperienceDrop(final LivingExperienceDropEvent event)
	{
		LivingEntity entity = event.getEntityLiving();
		LivingCap<?> cap = (LivingCap<?>)entity.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
		int soulReward = 0;
		if (cap != null) soulReward = cap.getSoulReward();
		if (soulReward == 0) soulReward = event.getDroppedExperience() * 10;
		entity.level.addFreshEntity(new SoulEntity(entity.level, entity.getX(), entity.getY(), entity.getZ(), soulReward));
	}
	
	@SubscribeEvent
	public static void onFOVUpdate(final FOVModifierEvent event)
	{
		Player player = event.getEntity();
		float f = 1.0F;
		if (player.getAbilities().flying)
		{
			f *= 1.1F;
		}

		if (player.isUsingItem() && player.getUseItem().getItem() == Items.BOW)
		{
			int i = player.getTicksUsingItem();
			float f1 = (float) i / 20.0F;
			if (f1 > 1.0F)
			{
				f1 = 1.0F;
			} else
			{
				f1 = f1 * f1;
			}

			f *= 1.0F - f1 * 0.15F;
		}

		event.setNewfov(f);
	}
}
