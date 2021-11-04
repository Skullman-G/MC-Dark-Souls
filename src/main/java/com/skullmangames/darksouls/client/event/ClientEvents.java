package com.skullmangames.darksouls.client.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.gui.GameOverlayManager;
import com.skullmangames.darksouls.common.item.DarkSoulsSpawnEggItem;
import com.skullmangames.darksouls.core.init.EffectInit;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
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
	public static void onRenderGameOverlayPre(final RenderGameOverlayEvent.Pre event)
	{
		Minecraft minecraft = Minecraft.getInstance();
		MainWindow window = event.getWindow();
		MatrixStack matStack = event.getMatrixStack();
		
		switch (event.getType())
		{
			case HEALTH:
				event.setCanceled(true);
				GameOverlayManager.renderHealth(window, matStack);
				break;
				
			case FOOD:
				if (!(minecraft.getCameraEntity() instanceof LivingEntity)
						|| !((LivingEntity)minecraft.getCameraEntity()).hasEffect(EffectInit.UNDEAD_CURSE.get())) break;
				event.setCanceled(true);
				GameOverlayManager.renderStamina(window, matStack);
				break;
				
			case ALL:
				GameOverlayManager.renderAdditional(window, matStack);
				break;
				
			default:
				minecraft.getTextureManager().bind(AbstractGui.GUI_ICONS_LOCATION);
				break;
		}
	}
}
