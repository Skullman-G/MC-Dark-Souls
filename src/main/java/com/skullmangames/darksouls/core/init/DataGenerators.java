package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.data.WeaponMovesetProvider;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = DarkSouls.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators
{
	@SubscribeEvent
	public static void onGatherData(GatherDataEvent event)
	{
		if (event.includeServer())
        {
			DataGenerator generator = event.getGenerator();
			//ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
			generator.addProvider(new WeaponMovesetProvider(generator));
        }
	}
}
