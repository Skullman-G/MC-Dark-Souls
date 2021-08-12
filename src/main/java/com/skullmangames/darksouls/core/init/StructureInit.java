package com.skullmangames.darksouls.core.init;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.world.structures.CheckpointPlainsStructure;
import com.skullmangames.darksouls.common.world.structures.UndeadAsylumStructure;

import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class StructureInit
{
    public static final DeferredRegister<Structure<?>> STRUCTURES = DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, DarkSouls.MOD_ID);
    
    public static final RegistryObject<Structure<NoFeatureConfig>> CHECKPOINT_PLAINS = STRUCTURES.register("checkpoint_plains", () -> (new CheckpointPlainsStructure(NoFeatureConfig.CODEC)));
    public static final RegistryObject<Structure<NoFeatureConfig>> UNDEAD_ASYLUM = STRUCTURES.register("undead_asylum", () -> (new UndeadAsylumStructure(NoFeatureConfig.CODEC)));
    
    
    public static void setupStructures()
    {
        setupMapSpacingAndLand(CHECKPOINT_PLAINS.get(), new StructureSeparationSettings(20, 10, 293760225), true);
        setupMapSpacingAndLand(UNDEAD_ASYLUM.get(), new StructureSeparationSettings(20, 10, 772382762), true);
    }
    
    public static <F extends Structure<?>> void setupMapSpacingAndLand(F structure, StructureSeparationSettings structureSeparationSettings, boolean transformSurroundingLand)
    {
        Structure.STRUCTURES_REGISTRY.put(structure.getRegistryName().toString(), structure);

        if(transformSurroundingLand)
        {
            Structure.NOISE_AFFECTING_FEATURES = ImmutableList.<Structure<?>>builder().addAll(Structure.NOISE_AFFECTING_FEATURES).add(structure).build();
        }
        
        DimensionStructuresSettings.DEFAULTS = ImmutableMap.<Structure<?>, StructureSeparationSettings>builder().putAll(DimensionStructuresSettings.DEFAULTS).put(structure, structureSeparationSettings).build();

        WorldGenRegistries.NOISE_GENERATOR_SETTINGS.entrySet().forEach(settings ->
        {
            Map<Structure<?>, StructureSeparationSettings> structureMap = settings.getValue().structureSettings().structureConfig();

            if(structureMap instanceof ImmutableMap)
            {
                Map<Structure<?>, StructureSeparationSettings> tempMap = new HashMap<>(structureMap);
                tempMap.put(structure, structureSeparationSettings);
                settings.getValue().structureSettings().structureConfig = tempMap;
            }
            else{
                structureMap.put(structure, structureSeparationSettings);
            }
        });
    }
}
