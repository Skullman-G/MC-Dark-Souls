package com.skullmangames.darksouls.client.event;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.renderer.entity.FireKeeperRenderer;
import com.skullmangames.darksouls.client.renderer.entity.HollowRenderer;
import com.skullmangames.darksouls.client.renderer.entity.SoulRenderer;
import com.skullmangames.darksouls.common.entity.FireKeeperEntity;
import com.skullmangames.darksouls.common.entity.HollowEntity;
import com.skullmangames.darksouls.core.init.EntityTypeInit;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = DarkSouls.MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)
public final class ModClientEvents
{
	private static final Logger LOGGER = LogManager.getLogger(DarkSouls.MOD_ID + " Client Mod Event Subscriber");
	
	private ModClientEvents()
	{
		throw new IllegalAccessError("Attempted to construct utility class.");
	}
	
	@SubscribeEvent
	public static void setup(final FMLClientSetupEvent event)
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityTypeInit.FIRE_KEEPER.get(), FireKeeperRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTypeInit.HOLLOW.get(), HollowRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTypeInit.SOUL.get(), SoulRenderer::new);
		LOGGER.debug("Registered Entity Renderers");
	}
	
	@SubscribeEvent
	public static void onEntityAttributeCreation(final EntityAttributeCreationEvent event)
	{
		event.put(EntityTypeInit.FIRE_KEEPER.get(), FireKeeperEntity.createAttributes().build());
		event.put(EntityTypeInit.HOLLOW.get(), HollowEntity.createAttributes().build());
	}
}
