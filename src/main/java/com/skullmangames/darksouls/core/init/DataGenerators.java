package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.core.data_provider.AnimationDataProvider;
import com.skullmangames.darksouls.core.data_provider.ArmorConfigProvider;
import com.skullmangames.darksouls.core.data_provider.McAssetRootFileCreator;
import com.skullmangames.darksouls.core.data_provider.MeleeWeaponConfigProvider;
import com.skullmangames.darksouls.core.data_provider.RangedWeaponConfigProvider;
import com.skullmangames.darksouls.core.data_provider.SpellConfigProvider;
import com.skullmangames.darksouls.core.data_provider.SpellcastingWeaponConfigProvider;
import com.skullmangames.darksouls.core.data_provider.WeaponMovesetProvider;
import com.skullmangames.darksouls.core.data_provider.WeaponSkillProvider;

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
			generator.addProvider(new AnimationDataProvider(generator));
			generator.addProvider(new WeaponMovesetProvider(generator));
			generator.addProvider(new WeaponSkillProvider(generator));
			generator.addProvider(new MeleeWeaponConfigProvider(generator));
			generator.addProvider(new SpellcastingWeaponConfigProvider(generator));
			generator.addProvider(new RangedWeaponConfigProvider(generator));
			generator.addProvider(new ArmorConfigProvider(generator));
			generator.addProvider(new SpellConfigProvider(generator));
			generator.addProvider(new McAssetRootFileCreator(generator));
        }
	}
}
