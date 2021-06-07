package com.skullmangames.darksouls;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.FlatChunkGenerator;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.serialization.Codec;
import com.skullmangames.darksouls.common.items.EstusFlask;
import com.skullmangames.darksouls.common.items.SoulsGroup;
import com.skullmangames.darksouls.core.init.BlockInit;
import com.skullmangames.darksouls.core.init.ConfiguredStructureInit;
import com.skullmangames.darksouls.core.init.EffectInit;
import com.skullmangames.darksouls.core.init.ItemInit;
import com.skullmangames.darksouls.core.init.SoundEventInit;
import com.skullmangames.darksouls.core.init.StructureInit;
import com.skullmangames.darksouls.core.init.TileEntityTypeInit;

@Mod(DarkSouls.MOD_ID)
public class DarkSouls
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "darksouls"; 
    public static final ItemGroup TAB_SOULS = new SoulsGroup("soulstab");

    public DarkSouls() 
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    	
    	SoundEventInit.SOUND_EVENTS.register(bus);
    	BlockInit.BLOCKS.register(bus);
    	TileEntityTypeInit.TILE_ENTITIES.register(bus);
    	ItemInit.ITEMS.register(bus);
    	StructureInit.STRUCTURES.register(bus);
    	EffectInit.EFFECTS.register(bus);
    	bus.addListener(this::setup);
    	
        MinecraftForge.EVENT_BUS.register(this);
        
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(EventPriority.NORMAL, this::addDimensionalSpacing);
        
        forgeBus.addListener(EventPriority.HIGH, this::biomeModification);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
    	event.enqueueWork(() ->
    	{
    	    ItemModelsProperties.register(ItemInit.ESTUS_FLASK.get(), new ResourceLocation(MOD_ID, "usage"), (stack, world, living) ->
    	    {
    	        float usage = (float)((EstusFlask)ItemInit.ESTUS_FLASK.get()).getUses(stack) / (float)((EstusFlask)ItemInit.ESTUS_FLASK.get()).getTotalUses(stack);
    	    	return usage;
    	    });
    	    
    	    StructureInit.setupStructures();
    	    ConfiguredStructureInit.registerConfiguredStructures();
    	});
    }
    
    public void biomeModification(final BiomeLoadingEvent event)
    {
        event.getGeneration().getStructures().add(() -> ConfiguredStructureInit.CONFIGURED_CHECKPOINT_PLAINS);
    }
    
    private static Method GETCODEC_METHOD;
    @SuppressWarnings("resource")
	public void addDimensionalSpacing(final WorldEvent.Load event)
    {
        if(event.getWorld() instanceof ServerWorld){
            ServerWorld serverWorld = (ServerWorld)event.getWorld();

            try
            {
                if(GETCODEC_METHOD == null) GETCODEC_METHOD = ObfuscationReflectionHelper.findMethod(ChunkGenerator.class, "func_230347_a_");
                @SuppressWarnings("unchecked")
				ResourceLocation cgRL = Registry.CHUNK_GENERATOR.getKey((Codec<? extends ChunkGenerator>) GETCODEC_METHOD.invoke(serverWorld.getChunkSource().generator));
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

			Map<Structure<?>, StructureSeparationSettings> tempMap = new HashMap<>(serverWorld.getChunkSource().generator.getSettings().structureConfig());
            tempMap.putIfAbsent(StructureInit.CHECKPOINT_PLAINS.get(), DimensionStructuresSettings.DEFAULTS.get(StructureInit.CHECKPOINT_PLAINS.get()));
            serverWorld.getChunkSource().generator.getSettings().structureConfig = tempMap;
        }
   }
}