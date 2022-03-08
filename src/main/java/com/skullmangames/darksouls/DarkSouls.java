package com.skullmangames.darksouls;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigGuiHandler.ConfigGuiFactory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.config.ModConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.client.event.ClientEvents;
import com.skullmangames.darksouls.client.gui.screens.IngameConfigurationScreen;
import com.skullmangames.darksouls.client.input.InputManager;
import com.skullmangames.darksouls.client.input.ModKeys;
import com.skullmangames.darksouls.client.renderer.RenderEngine;
import com.skullmangames.darksouls.client.renderer.entity.FireKeeperRenderer;
import com.skullmangames.darksouls.client.renderer.entity.SoulRenderer;
import com.skullmangames.darksouls.client.renderer.entity.model.vanilla.AsylumDemonRenderer;
import com.skullmangames.darksouls.client.renderer.entity.model.vanilla.VanillaHumanoidRenderer;
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
import com.skullmangames.darksouls.core.init.ModEntities;
import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.core.init.ModModelLayers;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.init.ModParticles;
import com.skullmangames.darksouls.core.init.ProviderEntity;
import com.skullmangames.darksouls.core.init.ProviderItem;
import com.skullmangames.darksouls.core.init.ProviderProjectile;
import com.skullmangames.darksouls.core.init.ModRecipes;
import com.skullmangames.darksouls.core.init.ModSoundEvents;
import com.skullmangames.darksouls.core.init.ModStructures;
import com.skullmangames.darksouls.core.init.ModBlockEntities;
import com.skullmangames.darksouls.network.ModNetworkManager;

@Mod(DarkSouls.MOD_ID)
public class DarkSouls
{
	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MOD_ID = "darksouls";
	public static final String CONFIG_FILE_PATH = DarkSouls.MOD_ID + ".toml";
	public static IngameConfig CLIENT_INGAME_CONFIG;
	public static final CreativeModeTab TAB = new CreativeModeTab("darksouls")
	{
		@Override
		public ItemStack makeIcon()
		{
			return ModItems.DARKSIGN.get().getDefaultInstance();
		}
	};

	public DarkSouls()
	{
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigManager.COMMON_CONFIG, CONFIG_FILE_PATH);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigManager.CLIENT_CONFIG);

		if (isPhysicalClient())
		{
			ClientModels.CLIENT.buildArmatureData();
			Models.SERVER.buildArmatureData();
		} else
		{
			Models.SERVER.buildArmatureData();
		}

		Animations.registerAnimations(FMLEnvironment.dist);

		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		modBus.addListener(this::doCommonStuff);
		modBus.addListener(this::doClientStuff);
		modBus.addListener(ModAttributes::createAttributeMap);
		modBus.addListener(ModAttributes::modifyAttributeMap);

		IEventBus forgeBus = MinecraftForge.EVENT_BUS;
		forgeBus.addListener(EventPriority.NORMAL, ModStructures::setupStructureSpawns);
		forgeBus.addListener(EventPriority.NORMAL, ConfiguredStructureInit::addDimensionalSpacing);

		ModAttributes.ATTRIBUTES.register(modBus);
		ModSoundEvents.SOUND_EVENTS.register(modBus);
		ModBlocks.BLOCKS.register(modBus);
		ModBlockEntities.BLOCK_ENTITIES.register(modBus);
		ModStructures.STRUCTURES.register(modBus);
		ModContainers.CONTAINERS.register(modBus);
		ModEntities.ENTITIES.register(modBus);
		ModItems.ITEMS.register(modBus);
		ModRecipes.RECIPE_SERIALIZERS.register(modBus);
		ModParticles.PARTICLES.register(modBus);

		forgeBus.register(this);
		forgeBus.register(EntityEvents.class);
		forgeBus.register(CapabilityEvents.class);
		forgeBus.register(PlayerEvents.class);

		ConfigManager.loadConfig(ConfigManager.CLIENT_CONFIG,
				FMLPaths.CONFIGDIR.get().resolve(MOD_ID + "-client.toml").toString());
		ConfigManager.INGAME_CONFIG.populateDefaultValues();
		ConfigManager.loadConfig(ConfigManager.COMMON_CONFIG,
				FMLPaths.CONFIGDIR.get().resolve(CONFIG_FILE_PATH).toString());

		ModLoadingContext.get().registerExtensionPoint(ConfigGuiFactory.class,
				() -> new ConfigGuiFactory((mc, screen) -> new IngameConfigurationScreen(mc, screen)));
	}

	private void doCommonStuff(final FMLCommonSetupEvent event)
	{
		ModNetworkManager.registerPackets();

		ProviderItem.initCapabilityMap();
		ProviderItem.registerCapabilityItems();

		ProviderEntity.makeMap();
		ProviderProjectile.makeMap();

		event.enqueueWork(() ->
		{
			ModStructures.setupStructures();
			ConfiguredStructureInit.registerAll();
		});

		ModEntities.registerEntitySpawnPlacement();
		CriteriaTriggerInit.register();
	}

	private void doClientStuff(final FMLClientSetupEvent event)
	{
		if (FMLEnvironment.dist.isDedicatedServer())
			return;

		new ClientManager();

		ClientModels.CLIENT.buildMeshData();
		ClientManager.INSTANCE.renderEngine.buildRenderer();

		ProviderEntity.makeMapClient();
		ModKeys.registerKeys();

		MinecraftForge.EVENT_BUS.register(InputManager.Events.class);
		MinecraftForge.EVENT_BUS.register(RenderEngine.Events.class);
		MinecraftForge.EVENT_BUS.register(ClientEvents.class);

		ItemBlockRenderTypes.setRenderLayer(ModBlocks.BIG_ACACIA_DOOR.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.BIG_OAK_DOOR.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.BIG_JUNGLE_DOOR.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.IRON_BAR_DOOR.get(), RenderType.cutout());
		
		ModModelLayers.call();

		EntityRenderers.register(ModEntities.FIRE_KEEPER.get(), FireKeeperRenderer::new);
		EntityRenderers.register(ModEntities.HOLLOW.get(), VanillaHumanoidRenderer::new); // Should find a better solution
		EntityRenderers.register(ModEntities.HOLLOW_LORDRAN_WARRIOR.get(), VanillaHumanoidRenderer::new); // Should find a better solution
		EntityRenderers.register(ModEntities.HOLLOW_LORDRAN_SOLDIER.get(), VanillaHumanoidRenderer::new); // Should find a better solution
		EntityRenderers.register(ModEntities.CRESTFALLEN_WARRIOR.get(), VanillaHumanoidRenderer::new); // Should find a better solution
		EntityRenderers.register(ModEntities.ANASTACIA_OF_ASTORA.get(), VanillaHumanoidRenderer::new); // Should find a better solution
		EntityRenderers.register(ModEntities.ASYLUM_DEMON.get(), AsylumDemonRenderer::new); // Should find a better solution
		EntityRenderers.register(ModEntities.SOUL.get(), SoulRenderer::new);

		ModItems.registerDescriptionItems();

		CLIENT_INGAME_CONFIG = ConfigManager.INGAME_CONFIG;

		com.skullmangames.darksouls.client.gui.ScreenManager
				.onDarkSoulsUIChanged(CLIENT_INGAME_CONFIG.darkSoulsUI.getValue());
	}

	public static boolean isPhysicalClient()
	{
		return FMLEnvironment.dist == Dist.CLIENT;
	}
}