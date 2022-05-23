package com.skullmangames.darksouls.client.input;

import java.util.ArrayList;
import java.util.List;

import com.skullmangames.darksouls.DarkSouls;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ClientRegistry;

@OnlyIn(Dist.CLIENT)
public class ModKeys
{
	private static final List<KeyMapping> keyBindings = new ArrayList<KeyMapping>();
	
	
	public static final KeyMapping SHOW_ITEM_INFO = makeKeyBinding("show_item_info", 71, "gui");
	public static final KeyMapping TOGGLE_COMBAT_MODE = makeKeyBinding("toggle_combat_mode", 82, "combat");
	public static final KeyMapping OPEN_STAT_SCREEN = makeKeyBinding("open_stat_screen", 77, "gui");
	
	// For temporary use
	public static final KeyMapping VISIBLE_HITBOXES = new KeyMapping("visible_hitboxes", 79, "debug");
	
	
	public static void registerKeys()
	{
		Minecraft minecraft = Minecraft.getInstance();
		
		removeKeyBinding(minecraft.options.keyTogglePerspective);
		minecraft.options.keyTogglePerspective = new FakeKeyBinding("key.togglePerspective");
		
		for (KeyMapping key : keyBindings) ClientRegistry.registerKeyBinding(key);
	}
	
	public static KeyMapping makeKeyBinding(String name, int defaultKey, String category)
	{
		name = "key."+DarkSouls.MOD_ID+"."+name;
		category = "key."+DarkSouls.MOD_ID+"."+category;
		
		KeyMapping key = new KeyMapping(name, defaultKey, category);
		keyBindings.add(key);
		return key;
	}
	
	private static void removeKeyBinding(KeyMapping binding)
	{
		Minecraft minecraft = Minecraft.getInstance();
		List<KeyMapping> keyBindings = new ArrayList<KeyMapping>();
		for (KeyMapping k : minecraft.options.keyMappings) keyBindings.add(k);
		keyBindings.remove(binding);
		
		minecraft.options.keyMappings = new KeyMapping[keyBindings.size()];
		for (int i = 0; i < keyBindings.size(); i++) minecraft.options.keyMappings[i] = keyBindings.get(i);
	}
}