package com.skullmangames.darksouls.client.event;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.gui.GameOverlayManager;
import com.skullmangames.darksouls.common.items.DarkSoulsSpawnEggItem;
import com.skullmangames.darksouls.core.init.EffectInit;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = DarkSouls.MOD_ID, bus = Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents
{
	@SubscribeEvent
    public static void onRegisterEntities(final RegistryEvent.Register<EntityType<?>> event)
	{
        DarkSoulsSpawnEggItem.initSpawnEggs();
    }
	
	@SubscribeEvent
	public static void onRenderGameOverlayPost(final RenderGameOverlayEvent.Post event)
	{
		GameOverlayManager.render(event.getType(), event.getWindow(), event.getMatrixStack());
	}
	
	@SubscribeEvent
	public static void onRenderGameOverlayPre(final RenderGameOverlayEvent.Pre event)
	{
		if (event.getType() == ElementType.HEALTH)
		{
			event.setCanceled(true);
		}
		else if (event.getType() == ElementType.EXPERIENCE)
		{
			event.setCanceled(true);
		}
		else if (event.getType() == ElementType.FOOD && Minecraft.getInstance().getCameraEntity() instanceof LivingEntity && ((LivingEntity)Minecraft.getInstance().getCameraEntity()).hasEffect(EffectInit.UNDEAD_CURSE.get()))
		{
			event.setCanceled(true);
		}
	}
}
