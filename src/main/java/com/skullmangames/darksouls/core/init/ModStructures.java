package com.skullmangames.darksouls.core.init;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.structures.FalconerCamp;
import com.skullmangames.darksouls.common.structures.FireKeeperRuins;
import com.skullmangames.darksouls.common.structures.FirelinkShrine;
import com.skullmangames.darksouls.common.structures.LordranCamp;
import com.skullmangames.darksouls.common.structures.SunlightAltar;

import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
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

public class ModStructures
{
	private static final Map<RegistryObject<Structure<NoFeatureConfig>>, Pair<StructureSeparationSettings, Boolean>> REGISTRY_MAP = new HashMap<>();
	public static final DeferredRegister<Structure<?>> STRUCTURES = DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, DarkSouls.MOD_ID);

	public static final RegistryObject<Structure<NoFeatureConfig>> FIRE_KEEPER_RUINS = register("fire_keeper_ruins", FireKeeperRuins::new, new StructureSeparationSettings(20, 10, 293760225), true);
	public static final RegistryObject<Structure<NoFeatureConfig>> LORDRAN_CAMP = register("lordran_camp", LordranCamp::new, new StructureSeparationSettings(20, 10, 728751690), true);
	public static final RegistryObject<Structure<NoFeatureConfig>> FIRELINK_SHRINE = register("firelink_shrine", FirelinkShrine::new, new StructureSeparationSettings(1000, 900, 383073029), true);
	public static final RegistryObject<Structure<NoFeatureConfig>> FALCONER_CAMP = register("falconer_camp", FalconerCamp::new, new StructureSeparationSettings(30, 5, 1694767085), true);
	public static final RegistryObject<Structure<NoFeatureConfig>> SUNLIGHT_ALTAR = register("sunlight_altar", SunlightAltar::new, new StructureSeparationSettings(150, 100, 1694767086), true);
	
	public static RegistryObject<Structure<NoFeatureConfig>> register(String name, Supplier<Structure<NoFeatureConfig>> supplier, StructureSeparationSettings separationSettings, boolean transformSurroundingLand)
	{
		RegistryObject<Structure<NoFeatureConfig>> structure = STRUCTURES.register(name, supplier);
		REGISTRY_MAP.put(structure, new Pair<StructureSeparationSettings, Boolean>(separationSettings, transformSurroundingLand));
		return structure;
	}
	
	public static void setupStructures()
    {
        REGISTRY_MAP.forEach((structure, settings) ->
        {
        	setupMapSpacingAndLand(structure.get(), settings.getFirst(), settings.getSecond());
        });
    }
    
    public static <F extends Structure<?>> void setupMapSpacingAndLand(F structure, StructureSeparationSettings separationSettings, boolean transformSurroundingLand)
    {
        Structure.STRUCTURES_REGISTRY.put(structure.getRegistryName().toString(), structure);

        if(transformSurroundingLand)
        {
            Structure.NOISE_AFFECTING_FEATURES = ImmutableList.<Structure<?>>builder().addAll(Structure.NOISE_AFFECTING_FEATURES).add(structure).build();
        }
        
        DimensionStructuresSettings.DEFAULTS = ImmutableMap.<Structure<?>, StructureSeparationSettings>builder().putAll(DimensionStructuresSettings.DEFAULTS).put(structure, separationSettings).build();

        WorldGenRegistries.NOISE_GENERATOR_SETTINGS.entrySet().forEach(settings ->
        {
            Map<Structure<?>, StructureSeparationSettings> structureMap = settings.getValue().structureSettings().structureConfig();

            if(structureMap instanceof ImmutableMap)
            {
                Map<Structure<?>, StructureSeparationSettings> tempMap = new HashMap<>(structureMap);
                tempMap.put(structure, separationSettings);
                settings.getValue().structureSettings().structureConfig = tempMap;
            }
            else structureMap.put(structure, separationSettings);
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
			for (RegistryObject<Structure<NoFeatureConfig>> structure : REGISTRY_MAP.keySet())
			{
				tempMap.putIfAbsent(structure.get(), DimensionStructuresSettings.DEFAULTS.get(structure.get()));
			}
            scp.generator.getSettings().structureConfig = tempMap;
        }
   }
}
