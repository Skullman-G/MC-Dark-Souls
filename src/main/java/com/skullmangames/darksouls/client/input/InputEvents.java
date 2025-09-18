package com.skullmangames.darksouls.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.ClientManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.client.event.InputEvent.MouseScrollEvent;
import net.minecraftforge.client.event.InputEvent.RawMouseEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = DarkSouls.MOD_ID, value = Dist.CLIENT)
public class InputEvents
{
	// I'm using this only to cancel vanilla attacks
	@SubscribeEvent
	public static void onClickInputCancelable(InputEvent.ClickInputEvent event)
	{
		if (event.isAttack())
		{
			if (ClientManager.INSTANCE.isCombatModeActive())
			{
				event.setSwingHand(false);
			}
			
			HitResult.Type hit = Minecraft.getInstance().hitResult.getType();
			if (hit == HitResult.Type.ENTITY
					|| (hit == HitResult.Type.BLOCK && ClientManager.INSTANCE.isCombatModeActive()))
			{
				event.setCanceled(true);
			}
		}
		else if (event.isPickBlock() && ClientManager.INSTANCE.isCombatModeActive())
		{
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public static void onRawMouseInput(RawMouseEvent event)
	{
		if (Minecraft.getInstance().player != null)
		{
			ClientManager.INSTANCE.inputManager.doActionForKey(InputConstants.Type.MOUSE, event.getButton(), event.getAction());
		}
	}
	
	@SubscribeEvent
	public static void onMouseScroll(MouseScrollEvent event)
	{
		event.setCanceled(ClientManager.INSTANCE.inputManager.shouldCancelScrolling());
	}
	
	@SubscribeEvent
	public static void onKeyboardInput(KeyInputEvent event)
	{
		if (Minecraft.getInstance().player != null)
		{
			ClientManager.INSTANCE.inputManager.doActionForKey(InputConstants.Type.KEYSYM, event.getKey(), event.getAction());
		}
	}
	
	@SubscribeEvent
	public static void onMoveInput(MovementInputUpdateEvent event)
	{
		ClientManager.INSTANCE.inputManager.handleMovement(event.getInput());
	}
	
	@SubscribeEvent
	public static void preProcessKeyBindings(TickEvent.ClientTickEvent event)
	{
		if (event.phase == TickEvent.Phase.START
				&& Minecraft.getInstance().player != null && Minecraft.getInstance().screen == null)
		{
			ClientManager.INSTANCE.inputManager.tick();
		}
	}
}
