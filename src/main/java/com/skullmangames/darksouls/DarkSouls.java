package com.skullmangames.darksouls;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.EntityClassification;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.FlatChunkGenerator;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.config.ModConfig;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.serialization.Codec;
import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.client.event.ClientEvents;
import com.skullmangames.darksouls.client.gui.screens.IngameConfigurationScreen;
import com.skullmangames.darksouls.client.gui.screens.ReinforceEstusFlaskScreen;
import com.skullmangames.darksouls.client.gui.screens.SmithingTableScreenOverride;
import com.skullmangames.darksouls.client.input.InputManager;
import com.skullmangames.darksouls.client.input.ModKeys;
import com.skullmangames.darksouls.client.renderer.RenderEngine;
import com.skullmangames.darksouls.common.item.EstusFlaskItem;
import com.skullmangames.darksouls.common.item.SoulsGroup;
import com.skullmangames.darksouls.common.world.ModGamerules;
import com.skullmangames.darksouls.config.ConfigManager;
import com.skullmangames.darksouls.config.IngameConfig;
import com.skullmangames.darksouls.core.event.CapabilityEvents;
import com.skullmangames.darksouls.core.event.EntityEvents;
import com.skullmangames.darksouls.core.event.PlayerEvents;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.init.ModBlocks;
import com.skullmangames.darksouls.core.init.ClientModels;
import com.skullmangames.darksouls.core.init.ConfiguredStructureInit;
import com.skullmangames.darksouls.core.init.ModContainers;
import com.skullmangames.darksouls.core.init.CriteriaTriggerInit;
import com.skullmangames.darksouls.core.init.ModEffects;
import com.skullmangames.darksouls.core.init.ModEntities;
import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.init.ModParticles;
import com.skullmangames.darksouls.core.init.ProviderEntity;
import com.skullmangames.darksouls.core.init.ProviderItem;
import com.skullmangames.darksouls.core.init.ProviderProjectile;
import com.skullmangames.darksouls.core.init.ModRecipes;
import com.skullmangames.darksouls.core.init.ModSoundEvents;
import com.skullmangames.darksouls.core.init.ModStructures;
import com.skullmangames.darksouls.core.init.ModTileEntities;
import com.skullmangames.darksouls.network.ModNetworkManager;

@Mod(DarkSouls.MOD_ID)
public class DarkSouls
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "darksouls"; 
    public static final String CONFIG_FILE_PATH = DarkSouls.MOD_ID + ".toml";
    public static IngameConfig CLIENT_INGAME_CONFIG;
    public static final ItemGroup TAB_SOULS = new SoulsGroup("soulstab");

    public DarkSouls() 
    {
    	ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigManager.COMMON_CONFIG, CONFIG_FILE_PATH);
    	ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigManager.CLIENT_CONFIG);
    	
    	if(isPhysicalClient())
    	{
    		ClientModels.CLIENT.buildArmatureData();
    		Models.SERVER.buildArmatureData();
    	}
    	else
    	{
    		Models.SERVER.buildArmatureData();
    	}
    	
    	Animations.registerAnimations(FMLEnvironment.dist);
    	
    	IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
    	IEventBus forgeBus = MinecraftForge.EVENT_BUS;
    	
    	modBus.addListener(this::doCommonStuff);
    	modBus.addListener(this::doClientStuff);
    	modBus.addListener(ModAttributes::modifyAttributeMap);
    	
    	ModAttributes.ATTRIBUTES.register(modBus);
    	ModSoundEvents.VANILLA_SOUND_EVENTS.register(modBus);
    	ModBlocks.BLOCKS.register(modBus);
    	ModBlocks.VANILLA_BLOCKS.register(modBus);
    	ModTileEntities.TILE_ENTITIES.register(modBus);
    	ModItems.VANILLA_ITEMS.register(modBus);
    	ModStructures.STRUCTURES.register(modBus);
    	ModEffects.EFFECTS.register(modBus);
    	ModContainers.VANILLA_CONTAINERS.register(modBus);
    	ModContainers.CONTAINERS.register(modBus);
    	ModEntities.ENTITIES.register(modBus);
    	ModItems.ITEMS.register(modBus);
    	ModRecipes.RECIPE_SERIALIZERS.register(modBus);
    	ModParticles.PARTICLES.register(modBus);
    	
    	forgeBus.register(this);
        forgeBus.register(EntityEvents.class);
        forgeBus.register(CapabilityEvents.class);
        forgeBus.register(PlayerEvents.class);
        
        ConfigManager.loadConfig(ConfigManager.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MOD_ID + "-client.toml").toString());
        ConfigManager.INGAME_CONFIG.populateDefaultValues();
        ConfigManager.loadConfig(ConfigManager.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve(CONFIG_FILE_PATH).toString());
        
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> IngameConfigurationScreen::new);
        
        forgeBus.addListener(EventPriority.NORMAL, this::addDimensionalSpacing);
        forgeBus.addListener(EventPriority.HIGH, this::biomeModification);
    }

	private void doCommonStuff(final FMLCommonSetupEvent event)
    {
		ModCapabilities.registerCapabilities();
    	ModNetworkManager.registerPackets();
    	
    	ProviderItem.initCapabilityMap();
    	ProviderItem.registerCapabilityItems();
    	
    	ProviderEntity.makeMap();
    	ProviderProjectile.makeMap();
    	ModGamerules.registerRules();
		
		event.enqueueWork(() ->
    	{
    	    ItemModelsProperties.register(ModItems.ESTUS_FLASK.get(), new ResourceLocation(MOD_ID, "usage"), (stack, level, living) ->
    	    {
    	        float usage = (float)EstusFlaskItem.getUses(stack) / (float)EstusFlaskItem.getTotalUses(stack);
    	    	return usage;
    	    });
    	    
    	    ModStructures.setupStructures();
    	    ConfiguredStructureInit.registerConfiguredStructures();
    	});
    	
    	ModEntities.registerEntitySpawnPlacement();
    	CriteriaTriggerInit.register();
    }
    
	private void doClientStuff(final FMLClientSetupEvent event)
    {
		if (FMLEnvironment.dist.isDedicatedServer()) return;
        
        new ClientManager();
        
        ClientModels.CLIENT.buildMeshData();
		ClientManager.INSTANCE.renderEngine.buildRenderer();
		
		ProviderEntity.makeMapClient();
		ModKeys.registerKeys();
		
		MinecraftForge.EVENT_BUS.register(InputManager.Events.class);
        MinecraftForge.EVENT_BUS.register(RenderEngine.Events.class);
        MinecraftForge.EVENT_BUS.register(ClientEvents.class);
        
        RenderTypeLookup.setRenderLayer(ModBlocks.BIG_ACACIA_DOOR.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.BIG_OAK_DOOR.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.BIG_JUNGLE_DOOR.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.IRON_BAR_DOOR.get(), RenderType.cutout());
        
        ScreenManager.register(ModContainers.SMITHING.get(), SmithingTableScreenOverride::new);
        ScreenManager.register(ModContainers.REINFORCE_ESTUS_FLASK.get(), ReinforceEstusFlaskScreen::new);
        
        ModItems.registerDescriptionItems();
        
        CLIENT_INGAME_CONFIG = ConfigManager.INGAME_CONFIG;
        
        com.skullmangames.darksouls.client.gui.ScreenManager.onDarkSoulsUIChanged(CLIENT_INGAME_CONFIG.darkSoulsUI.getValue());
    }
	
	public static boolean isPhysicalClient()
	{
    	return FMLEnvironment.dist == Dist.CLIENT;
    }
    
    public void biomeModification(final BiomeLoadingEvent event)
    {
    	if (event.getCategory() == Biome.Category.PLAINS || event.getCategory() == Biome.Category.FOREST || event.getCategory() == Biome.Category.JUNGLE)
        {
        	event.getGeneration().getStructures().add(() -> ConfiguredStructureInit.CONFIGURED_CHECKPOINT_PLAINS);
        }
    	
    	if (event.getCategory() == Biome.Category.EXTREME_HILLS)
    	{
    		event.getGeneration().getStructures().add(() -> ConfiguredStructureInit.CONFIGURED_UNDEAD_ASYLUM);
    	}
    	
    	event.getSpawns().addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.HOLLOW.get(), 20, 1, 1));
    }
    
    private static Method GETCODEC_METHOD;
	public void addDimensionalSpacing(final WorldEvent.Load event)
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
            tempMap.putIfAbsent(ModStructures.CHECKPOINT_PLAINS.get(), DimensionStructuresSettings.DEFAULTS.get(ModStructures.CHECKPOINT_PLAINS.get()));
            tempMap.putIfAbsent(ModStructures.UNDEAD_ASYLUM.get(), DimensionStructuresSettings.DEFAULTS.get(ModStructures.UNDEAD_ASYLUM.get()));
            scp.generator.getSettings().structureConfig = tempMap;
        }
   }
}