package com.skullmangames.darksouls.core.init;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.mojang.serialization.Codec;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.world.structures.ModStructure;

import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.BiomeCategory;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.RegistryObject;

public class ConfiguredStructureInit
{
	public static List<ConfiguredStructureFeature<JigsawConfiguration, ModStructure>> CONFIGURED_STRUCTURES = new ArrayList<>();
	
	public static ConfiguredStructureFeature<JigsawConfiguration, ModStructure> CONFIGURED_CHECKPOINT_PLAINS = ModStructures.CHECKPOINT_PLAINS.get().configured();
	public static ConfiguredStructureFeature<JigsawConfiguration, ModStructure> CONFIGURED_UNDEAD_ASYLUM = ModStructures.UNDEAD_ASYLUM.get().configured();

	public static void register(Registry<ConfiguredStructureFeature<?, ?>> registry, String name, ConfiguredStructureFeature<JigsawConfiguration, ModStructure> structure)
	{
		CONFIGURED_STRUCTURES.add(structure);
		Registry.register(registry, new ResourceLocation(DarkSouls.MOD_ID, name), structure);
	}
	
	public static void registerAll()
	{
		Registry<ConfiguredStructureFeature<?, ?>> registry = BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE;
		
		register(registry, "configured_checkpoint_plains", CONFIGURED_CHECKPOINT_PLAINS);
		register(registry, "configured_undead_asylum", CONFIGURED_UNDEAD_ASYLUM);
    }
	
	private static Method GETCODEC_METHOD;

	public static void addDimensionalSpacing(WorldEvent.Load event)
	{
		if (event.getWorld() instanceof ServerLevel serverLevel)
		{
			ChunkGenerator chunkGenerator = serverLevel.getChunkSource().getGenerator();

			if (chunkGenerator instanceof FlatLevelSource && serverLevel.dimension().equals(Level.OVERWORLD))
			{
				return;
			}

			StructureSettings worldStructureConfig = chunkGenerator.getSettings();

			HashMap<StructureFeature<?>, HashMultimap<ConfiguredStructureFeature<?, ?>, ResourceKey<Biome>>> STStructureToMultiMap = new HashMap<>();

			for (Map.Entry<ResourceKey<Biome>, Biome> biomeEntry : serverLevel.registryAccess()
					.ownedRegistryOrThrow(Registry.BIOME_REGISTRY).entrySet())
			{
				BiomeCategory biomeCategory = biomeEntry.getValue().getBiomeCategory();
				for (ConfiguredStructureFeature<JigsawConfiguration, ModStructure> structure : CONFIGURED_STRUCTURES)
				{
					if (structure.feature.canSpawnInBiome(biomeCategory))
					{
						associateBiomeToConfiguredStructure(STStructureToMultiMap,
								structure, biomeEntry.getKey());
					}
				}
			}

			ImmutableMap.Builder<StructureFeature<?>, ImmutableMultimap<ConfiguredStructureFeature<?, ?>, ResourceKey<Biome>>> tempStructureToMultiMap = ImmutableMap
					.builder();
			worldStructureConfig.configuredStructures.entrySet().stream()
					.filter(entry -> !STStructureToMultiMap.containsKey(entry.getKey()))
					.forEach(tempStructureToMultiMap::put);

			STStructureToMultiMap
					.forEach((key, value) -> tempStructureToMultiMap.put(key, ImmutableMultimap.copyOf(value)));

			worldStructureConfig.configuredStructures = tempStructureToMultiMap.build();

			try
			{
				if (GETCODEC_METHOD == null)
					GETCODEC_METHOD = ObfuscationReflectionHelper.findMethod(ChunkGenerator.class, "codec");
				@SuppressWarnings("unchecked")
				ResourceLocation cgRL = Registry.CHUNK_GENERATOR
						.getKey((Codec<? extends ChunkGenerator>) GETCODEC_METHOD.invoke(chunkGenerator));
				if (cgRL != null && cgRL.getNamespace().equals("terraforged"))
					return;
			} catch (Exception e)
			{
				DarkSouls.LOGGER.error("Was unable to check if " + serverLevel.dimension().location()
						+ " is using Terraforged's ChunkGenerator.");
			}

			Map<StructureFeature<?>, StructureFeatureConfiguration> tempMap = new HashMap<>(
					worldStructureConfig.structureConfig());
			
			for (RegistryObject<StructureFeature<?>> structure : ModStructures.STRUCTURES.getEntries())
			{
				tempMap.putIfAbsent(structure.get(),
						StructureSettings.DEFAULTS.get(structure.get()));
			}
			
			worldStructureConfig.structureConfig = tempMap;
		}
	}
	
	private static void associateBiomeToConfiguredStructure(
			Map<StructureFeature<?>, HashMultimap<ConfiguredStructureFeature<?, ?>, ResourceKey<Biome>>> STStructureToMultiMap,
			ConfiguredStructureFeature<?, ?> configuredStructureFeature, ResourceKey<Biome> biomeRegistryKey)
	{
		STStructureToMultiMap.putIfAbsent(configuredStructureFeature.feature, HashMultimap.create());
		HashMultimap<ConfiguredStructureFeature<?, ?>, ResourceKey<Biome>> configuredStructureToBiomeMultiMap = STStructureToMultiMap
				.get(configuredStructureFeature.feature);
		if (configuredStructureToBiomeMultiMap.containsValue(biomeRegistryKey))
		{
			DarkSouls.LOGGER.error(
					"""
							    Detected 2 ConfiguredStructureFeatures that share the same base StructureFeature trying to be added to same biome.
							    The two conflicting ConfiguredStructures are: {}, {}
							    The biome that is attempting to be shared: {}
							""",
					BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(configuredStructureFeature),
					BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(configuredStructureToBiomeMultiMap.entries()
							.stream().filter(e -> e.getValue() == biomeRegistryKey).findFirst().get().getKey()),
					biomeRegistryKey);
		} else
		{
			configuredStructureToBiomeMultiMap.put(configuredStructureFeature, biomeRegistryKey);
		}
	}
}
