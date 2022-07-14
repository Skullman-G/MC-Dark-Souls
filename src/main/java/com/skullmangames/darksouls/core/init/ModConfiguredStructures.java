package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraftforge.event.world.BiomeLoadingEvent;

public class ModConfiguredStructures
{
	public static StructureFeature<?, ?> CONFIGURED_FIRE_KEEPER_RUINS = ModStructures.FIRE_KEEPER_RUINS.get().configured(IFeatureConfig.NONE);
	public static StructureFeature<?, ?> CONFIGURED_LORDRAN_CAMP = ModStructures.LORDRAN_CAMP.get().configured(IFeatureConfig.NONE);
	public static StructureFeature<?, ?> CONFIGURED_FIRELINK_SHRINE = ModStructures.FIRELINK_SHRINE.get().configured(IFeatureConfig.NONE);

	public static void registerConfiguredStructures()
	{
        Registry<StructureFeature<?, ?>> registry = WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE;
        
        Registry.register(registry, new ResourceLocation(DarkSouls.MOD_ID, "configured_fire_keeper_ruins"), CONFIGURED_FIRE_KEEPER_RUINS);
        Registry.register(registry, new ResourceLocation(DarkSouls.MOD_ID, "configured_lordran_camp"), CONFIGURED_LORDRAN_CAMP);
        Registry.register(registry, new ResourceLocation(DarkSouls.MOD_ID, "configured_firelink_shrine"), CONFIGURED_FIRELINK_SHRINE);
        
        FlatGenerationSettings.STRUCTURE_FEATURES.put(ModStructures.FIRE_KEEPER_RUINS.get(), CONFIGURED_FIRE_KEEPER_RUINS);
        FlatGenerationSettings.STRUCTURE_FEATURES.put(ModStructures.LORDRAN_CAMP.get(), CONFIGURED_LORDRAN_CAMP);
        FlatGenerationSettings.STRUCTURE_FEATURES.put(ModStructures.FIRELINK_SHRINE.get(), CONFIGURED_FIRELINK_SHRINE);
    }
	
	public static void biomeModification(BiomeLoadingEvent event)
    {
    	if (event.getCategory() != Biome.Category.OCEAN && event.getCategory() != Biome.Category.NETHER && event.getCategory() != Biome.Category.RIVER
    			&& event.getCategory() != Biome.Category.THEEND)
        {
        	event.getGeneration().getStructures().add(() -> ModConfiguredStructures.CONFIGURED_FIRE_KEEPER_RUINS);
        }
    	
    	if (event.getCategory() == Biome.Category.PLAINS || event.getCategory() == Biome.Category.FOREST)
    	{
    		event.getGeneration().getStructures().add(() -> ModConfiguredStructures.CONFIGURED_LORDRAN_CAMP);
    	}
    	
    	if (event.getCategory() == Biome.Category.PLAINS)
    	{
    		event.getGeneration().getStructures().add(() -> ModConfiguredStructures.CONFIGURED_FIRELINK_SHRINE);
    	}
    }
}
