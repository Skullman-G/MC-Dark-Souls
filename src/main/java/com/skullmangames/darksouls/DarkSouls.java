package com.skullmangames.darksouls;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.skullmangames.darksouls.common.items.EstusFlask;
import com.skullmangames.darksouls.common.items.SoulsGroup;
import com.skullmangames.darksouls.core.init.BlockInit;
import com.skullmangames.darksouls.core.init.ItemInit;
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
    	
    	bus.addListener(this::setup);
    	
    	BlockInit.BLOCKS.register(bus);
    	TileEntityTypeInit.TILE_ENTITIES.register(bus);
    	ItemInit.ITEMS.register(bus);
    	
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
    	event.enqueueWork(() ->
    	{
    	    ItemModelsProperties.register(ItemInit.ESTUS_FLASK.get(), new ResourceLocation(MOD_ID, "usage"), (stack, world, living) ->
    	    {
    	        float usage = (float)EstusFlask.getUses(stack) / (float)EstusFlask.getTotalUses(stack);
    	    	return usage;
    	    });
    	});
    }
}