package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.core.data.AnimationDataProvider;
import com.skullmangames.darksouls.core.data.ArmorConfigProvider;
import com.skullmangames.darksouls.core.data.MeleeWeaponConfigProvider;
import com.skullmangames.darksouls.core.data.WeaponMovesetProvider;
import com.skullmangames.darksouls.core.data.WeaponSkillProvider;

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
			generator.addProvider(new WeaponSkillProvider(generator));
			generator.addProvider(new MeleeWeaponConfigProvider(generator));
			generator.addProvider(new ArmorConfigProvider(generator));
			generator.addProvider(new AnimationDataProvider(generator));
        }
	}
}
