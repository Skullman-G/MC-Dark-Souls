package com.skullmangames.darksouls.core.init;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.structures.FireKeeperRuins;
import com.skullmangames.darksouls.common.structures.FirelinkShrine;
import com.skullmangames.darksouls.common.structures.LordranCamp;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.FlatChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModStructures
{
	public static final DeferredRegister<Structure<?>> STRUCTURES = DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, DarkSouls.MOD_ID);

	public static final RegistryObject<Structure<NoFeatureConfig>> FIRE_KEEPER_RUINS = STRUCTURES.register("fire_keeper_ruins", FireKeeperRuins::new);
	public static final RegistryObject<Structure<NoFeatureConfig>> LORDRAN_CAMP = STRUCTURES.register("lordran_camp", LordranCamp::new);
	//public static final RegistryObject<Structure<NoFeatureConfig>> UNDEAD_ASYLUM = STRUCTURES.register("undead_asylum", UndeadAsylum::new);
	public static final RegistryObject<Structure<NoFeatureConfig>> FIRELINK_SHRINE = STRUCTURES.register("firelink_shrine", FirelinkShrine::new);
	
	public static void setupStructures()
    {
        setupMapSpacingAndLand(FIRE_KEEPER_RUINS.get(), new StructureSeparationSettings(1000, 900, 293760225), true);
        setupMapSpacingAndLand(LORDRAN_CAMP.get(), new StructureSeparationSettings(20, 10, 728751690), true);
        setupMapSpacingAndLand(FIRELINK_SHRINE.get(), new StructureSeparationSettings(20, 10, 383073029), true);
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
            else structureMap.put(structure, structureSeparationSettings);
        });
    }
    
    private static Method GETCODEC_METHOD;
	public static void addDimensionalSpacing(WorldEvent.Load event)
    {
		if(event.getWorld() instanceof ServerWorld)
        {
            ServerWorld serverWorld = (ServerWorld)event.getWorld();
            ServerChunkProvider scp = serverWorld.getChunkSource();

            try
            {
                if(GETCODEC_METHOD == null) GETCODEC_METHOD = ObfuscationReflectionHelper.findMethod(ChunkGenerator.class, "func_230347_a_");
				@SuppressWarnings("unchecked")
				ResourceLocation cgRL = Registry.CHUNK_GENERATOR.getKey((Codec<? extends ChunkGenerator>) GETCODEC_METHOD.invoke(scp.generator));
                if(cgRL != null && cgRL.getNamespace().equals("terraforged")) return;
            }
            catch (Exception e)
            {
                DarkSouls.LOGGER.error("Was unable to check if " + serverWorld.dimension().location() + " is using Terraforged's ChunkGenerator.");
            }

            if(serverWorld.getChunkSource().getGenerator() instanceof FlatChunkGenerator && serverWorld.dimension().equals(World.OVERWORLD))
            {
                return;
            }

			Map<Structure<?>, StructureSeparationSettings> tempMap = new HashMap<>(scp.generator.getSettings().structureConfig());
            tempMap.putIfAbsent(ModStructures.FIRE_KEEPER_RUINS.get(), DimensionStructuresSettings.DEFAULTS.get(ModStructures.FIRE_KEEPER_RUINS.get()));
            tempMap.putIfAbsent(ModStructures.LORDRAN_CAMP.get(), DimensionStructuresSettings.DEFAULTS.get(ModStructures.LORDRAN_CAMP.get()));
            tempMap.putIfAbsent(ModStructures.FIRELINK_SHRINE.get(), DimensionStructuresSettings.DEFAULTS.get(ModStructures.FIRELINK_SHRINE.get()));
            scp.generator.getSettings().structureConfig = tempMap;
        }
   }
}
