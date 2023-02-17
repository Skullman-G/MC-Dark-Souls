package com.skullmangames.darksouls.client.gui;

import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.client.gui.OverlayRegistry;

public class ModOverlayRegistry
{
	public static void enablePlayerHealth(boolean value)
	{
		OverlayRegistry.enableOverlay(ForgeIngameGui.PLAYER_HEALTH_ELEMENT, value);
	}
	
	public static void enableArmorLevel(boolean value)
	{
		OverlayRegistry.enableOverlay(ForgeIngameGui.ARMOR_LEVEL_ELEMENT, value);
	}
	
	public static void enableFoodLevel(boolean value)
	{
		OverlayRegistry.enableOverlay(ForgeIngameGui.FOOD_LEVEL_ELEMENT, value);
	}
	
	public static void enableBossHealth(boolean value)
	{
		OverlayRegistry.enableOverlay(ForgeIngameGui.BOSS_HEALTH_ELEMENT, value);
	}
	
	public static void enableHotbar(boolean value)
	{
		OverlayRegistry.enableOverlay(ForgeIngameGui.HOTBAR_ELEMENT, value);
	}
	
	public static void enableExpBar(boolean value)
	{
		OverlayRegistry.enableOverlay(ForgeIngameGui.EXPERIENCE_BAR_ELEMENT, value);
	}
	
	public static void enableJumpMeter(boolean value)
	{
		OverlayRegistry.enableOverlay(ForgeIngameGui.JUMP_BAR_ELEMENT, value);
	}
	
	public static void registerOverlayTop(String name, ModIngameOverlay overlay)
	{
		OverlayRegistry.registerOverlayTop(name, overlay);
	}
	
	public static void registerOverlayBottom(String name, ModIngameOverlay overlay)
	{
		OverlayRegistry.registerOverlayBottom(name, overlay);
	}
	
	public interface ModIngameOverlay extends IIngameOverlay {}
}
