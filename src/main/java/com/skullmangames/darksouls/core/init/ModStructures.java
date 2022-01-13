package com.skullmangames.darksouls.core.init;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.world.structures.CheckpointPlainsStructure;
import com.skullmangames.darksouls.common.world.structures.ModStructure;
import com.skullmangames.darksouls.common.world.structures.UndeadAsylumStructure;

import net.minecraft.data.BuiltinRegistries;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import net.minecraftforge.event.world.StructureSpawnListGatherEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModStructures
{
	public static final DeferredRegister<StructureFeature<?>> STRUCTURES = DeferredRegister
			.create(ForgeRegistries.STRUCTURE_FEATURES, DarkSouls.MOD_ID);

	public static final RegistryObject<ModStructure> CHECKPOINT_PLAINS = STRUCTURES
			.register("checkpoint_plains", () -> (new CheckpointPlainsStructure(JigsawConfiguration.CODEC)));
	public static final RegistryObject<ModStructure> UNDEAD_ASYLUM = STRUCTURES
			.register("undead_asylum", () -> (new UndeadAsylumStructure(JigsawConfiguration.CODEC)));

	public static void setupStructures()
	{
		setupMapSpacingAndLand(CHECKPOINT_PLAINS.get(), new StructureFeatureConfiguration(20, 10, 293760225), true);
        setupMapSpacingAndLand(UNDEAD_ASYLUM.get(), new StructureFeatureConfiguration(20, 10, 772382762), false);
	}

	public static <F extends StructureFeature<?>> void setupMapSpacingAndLand(F structure,
			StructureFeatureConfiguration structureFeatureConfiguration, boolean transformSurroundingLand)
	{
		StructureFeature.STRUCTURES_REGISTRY.put(structure.getRegistryName().toString().toLowerCase(), structure);
		if (transformSurroundingLand)
		{
			StructureFeature.NOISE_AFFECTING_FEATURES = ImmutableList.<StructureFeature<?>>builder()
					.addAll(StructureFeature.NOISE_AFFECTING_FEATURES).add(structure).build();
		}
		StructureSettings.DEFAULTS = ImmutableMap.<StructureFeature<?>, StructureFeatureConfiguration>builder()
				.putAll(StructureSettings.DEFAULTS).put(structure, structureFeatureConfiguration).build();

		BuiltinRegistries.NOISE_GENERATOR_SETTINGS.entrySet().forEach(settings ->
		{
			Map<StructureFeature<?>, StructureFeatureConfiguration> structureMap = settings.getValue()
					.structureSettings().structureConfig();

			if (structureMap instanceof ImmutableMap)
			{
				Map<StructureFeature<?>, StructureFeatureConfiguration> tempMap = new HashMap<>(structureMap);
				tempMap.put(structure, structureFeatureConfiguration);
				settings.getValue().structureSettings().structureConfig = tempMap;
			} else
			{
				structureMap.put(structure, structureFeatureConfiguration);
			}
		});
	}
	
	public static void setupStructureSpawns(StructureSpawnListGatherEvent event)
	{
		if (!(event.getStructure() instanceof ModStructure)) return;
		((ModStructure)event.getStructure()).setupSpawns(event);
	}
}
