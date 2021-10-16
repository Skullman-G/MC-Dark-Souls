package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class ConfiguredStructureInit
{
	public static StructureFeature<?, ?> CONFIGURED_CHECKPOINT_PLAINS = Structures.CHECKPOINT_PLAINS.get().configured(IFeatureConfig.NONE);
	public static StructureFeature<?, ?> CONFIGURED_UNDEAD_ASYLUM = Structures.UNDEAD_ASYLUM.get().configured(IFeatureConfig.NONE);

	public static void registerConfiguredStructures()
	{
        Registry<StructureFeature<?, ?>> registry = WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE;
        
        Registry.register(registry, new ResourceLocation(DarkSouls.MOD_ID, "configured_checkpoint_plains"), CONFIGURED_CHECKPOINT_PLAINS);
        Registry.register(registry, new ResourceLocation(DarkSouls.MOD_ID, "configured_undead_asylum"), CONFIGURED_UNDEAD_ASYLUM);
        
        FlatGenerationSettings.STRUCTURE_FEATURES.put(Structures.CHECKPOINT_PLAINS.get(), CONFIGURED_CHECKPOINT_PLAINS);
        FlatGenerationSettings.STRUCTURE_FEATURES.put(Structures.UNDEAD_ASYLUM.get(), CONFIGURED_UNDEAD_ASYLUM);
    }
}
