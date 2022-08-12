package com.skullmangames.darksouls;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DataSerializerEntry;
import net.minecraftforge.fml.config.ModConfig;

import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.client.animation.ClientAnimator;
import com.skullmangames.darksouls.client.event.ClientEvents;
import com.skullmangames.darksouls.client.input.InputManager;
import com.skullmangames.darksouls.client.input.ModKeys;
import com.skullmangames.darksouls.client.renderer.RenderEngine;
import com.skullmangames.darksouls.client.renderer.entity.HumanityRenderer;
import com.skullmangames.darksouls.client.renderer.entity.SoulRenderer;
import com.skullmangames.darksouls.client.renderer.entity.model.vanilla.VanillaHumanoidRenderer;
import com.skullmangames.darksouls.common.animation.AnimationManager;
import com.skullmangames.darksouls.common.animation.Animator;
import com.skullmangames.darksouls.common.animation.ServerAnimator;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.config.ConfigManager;
import com.skullmangames.darksouls.config.IngameConfig;
import com.skullmangames.darksouls.core.event.CapabilityEvents;
import com.skullmangames.darksouls.core.event.EntityEvents;
import com.skullmangames.darksouls.core.event.PlayerEvents;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.init.ModBlocks;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.init.ModConfiguredStructures;
import com.skullmangames.darksouls.core.init.ClientModels;
import com.skullmangames.darksouls.core.init.ModContainers;
import com.skullmangames.darksouls.core.init.ModCriteriaTriggers;
import com.skullmangames.darksouls.core.init.ModEntities;
import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.init.ModParticles;
import com.skullmangames.darksouls.core.init.ModRecipeTypes;
import com.skullmangames.darksouls.core.init.ProviderEntity;
import com.skullmangames.darksouls.core.init.ProviderItem;
import com.skullmangames.darksouls.core.init.ProviderProjectile;
import com.skullmangames.darksouls.core.util.QuestFlags;
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
	public static final ItemGroup TAB = new ItemGroup("darksouls")
	{
		@Override
		public ItemStack makeIcon()
		{
			return ModItems.DARKSIGN.get().getDefaultInstance();
		}
	};

	private static DarkSouls instance;
	public final AnimationManager animationManager;
	private Function<LivingCap<?>, Animator> animatorProvider;

	public static DarkSouls getInstance()
	{
		return instance;
	}

	public DarkSouls()
	{
		this.animationManager = new AnimationManager();
		instance = this;

		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigManager.COMMON_CONFIG, CONFIG_FILE_PATH);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigManager.CLIENT_CONFIG);

		if (isPhysicalClient())
		{
			ClientModels.CLIENT.buildArmatureData();
			Models.SERVER.buildArmatureData();
		}
		else Models.SERVER.buildArmatureData();

		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		
		modBus.addListener(this::doCommonStuff);
		modBus.addListener(this::doClientStuff);
		modBus.addListener(this::doServerStuff);
		modBus.addListener(ModAttributes::createAttributeMap);
		modBus.addListener(ModAttributes::modifyAttributeMap);

		IEventBus forgeBus = MinecraftForge.EVENT_BUS;
		
		modBus.addGenericListener(DataSerializerEntry.class, this::registerDataSerializers);
		
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
		forgeBus.addListener(ModEntities::addEntitySpawns);
		
		forgeBus.addListener(EventPriority.NORMAL, ModStructures::addDimensionalSpacing);
        forgeBus.addListener(EventPriority.HIGH, ModConfiguredStructures::biomeModification);

		ConfigManager.loadConfig(ConfigManager.CLIENT_CONFIG,
				FMLPaths.CONFIGDIR.get().resolve(MOD_ID + "-client.toml").toString());
		ConfigManager.INGAME_CONFIG.populateDefaultValues();
		ConfigManager.loadConfig(ConfigManager.COMMON_CONFIG,
				FMLPaths.CONFIGDIR.get().resolve(CONFIG_FILE_PATH).toString());
	}
	
	private void registerDataSerializers(RegistryEvent.Register<DataSerializerEntry> event)
	{
		event.getRegistry().register(new DataSerializerEntry(QuestFlags.SERIALIZER).setRegistryName(new ResourceLocation(MOD_ID, "quest_flags")));
	}

	private void doServerStuff(final FMLDedicatedServerSetupEvent event)
	{
		this.animationManager.loadAnimationsInit(null);
		this.animatorProvider = ServerAnimator::getAnimator;
	}

	private void doCommonStuff(final FMLCommonSetupEvent event)
	{
		ModCapabilities.registerCapabilities();
		ModRecipeTypes.call();

		ModNetworkManager.registerPackets();

		ProviderItem.initCapabilityMap();
		ProviderItem.registerCapabilityItems();

		ProviderEntity.makeMap();
		ProviderProjectile.makeMap();

		ModEntities.registerEntitySpawnPlacement();
		ModCriteriaTriggers.register();
	    
	    event.enqueueWork(() ->
    	{
    		  ModStructures.setupStructures();
    	    ModConfiguredStructures.registerConfiguredStructures();
    	});
	}

	private void doClientStuff(final FMLClientSetupEvent event)
	{
		if (FMLEnvironment.dist.isDedicatedServer())
			return;

		new ClientManager();

		IResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
		ClientModels.CLIENT.buildMeshData();
		this.animationManager.loadAnimationsInit(resourceManager);
		Animations.buildClient();
		ClientManager.INSTANCE.renderEngine.buildRenderer();

		ProviderEntity.makeMapClient();
		ModKeys.registerKeys();

		MinecraftForge.EVENT_BUS.register(InputManager.Events.class);
		MinecraftForge.EVENT_BUS.register(RenderEngine.Events.class);
		MinecraftForge.EVENT_BUS.register(ClientEvents.class);

		// Register ReloadListeners
		((IReloadableResourceManager) resourceManager).registerReloadListener(this.animationManager);

		RenderTypeLookup.setRenderLayer(ModBlocks.BIG_ACACIA_DOOR.get(), RenderType.cutout());
		RenderTypeLookup.setRenderLayer(ModBlocks.BIG_OAK_DOOR.get(), RenderType.cutout());
		RenderTypeLookup.setRenderLayer(ModBlocks.BIG_JUNGLE_DOOR.get(), RenderType.cutout());
		RenderTypeLookup.setRenderLayer(ModBlocks.IRON_BAR_DOOR.get(), RenderType.cutout());

		RenderingRegistry.registerEntityRenderingHandler(ModEntities.FIRE_KEEPER.get(), VanillaHumanoidRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.HOLLOW.get(), VanillaHumanoidRenderer::new); // Should find a better
																							// solution
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.HOLLOW_LORDRAN_WARRIOR.get(), VanillaHumanoidRenderer::new); // Should find
																											// a better
																											// solution
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.HOLLOW_LORDRAN_SOLDIER.get(), VanillaHumanoidRenderer::new); // Should find
																											// a better
																											// solution
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.CRESTFALLEN_WARRIOR.get(), VanillaHumanoidRenderer::new); // Should find a
																										// better
																										// solution
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.ANASTACIA_OF_ASTORA.get(), VanillaHumanoidRenderer::new); // Should find a
																										// better
																										// solution
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.STRAY_DEMON.get(), VanillaHumanoidRenderer::new); // Should find a better
																							// solution
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.SOUL.get(), SoulRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.HUMANITY.get(), HumanityRenderer::new);

		ModItems.registerDescriptionItems();

		CLIENT_INGAME_CONFIG = ConfigManager.INGAME_CONFIG;

		com.skullmangames.darksouls.client.gui.ScreenManager.onDarkSoulsUIChanged(CLIENT_INGAME_CONFIG.darkSoulsUI.getValue());
		
		this.animatorProvider = ClientAnimator::getAnimator;
	}
	
	public static Animator getAnimator(LivingCap<?> entityCap)
	{
		return DarkSouls.getInstance().animatorProvider.apply(entityCap);
	}

	public static boolean isPhysicalClient()
	{
		return FMLEnvironment.dist == Dist.CLIENT;
	}
}