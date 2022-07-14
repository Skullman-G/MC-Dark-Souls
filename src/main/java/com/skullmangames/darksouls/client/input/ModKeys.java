package com.skullmangames.darksouls.client.input;

import java.util.ArrayList;
import java.util.List;

import com.skullmangames.darksouls.DarkSouls;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.ClientRegistry;

@OnlyIn(Dist.CLIENT)
public class ModKeys
{
	private static final List<KeyBinding> keyBindings = new ArrayList<KeyBinding>();
	
	
	public static final KeyBinding SHOW_ITEM_INFO = makeKeyBinding("show_item_info", 71, "gui");
	public static final KeyBinding TOGGLE_COMBAT_MODE = makeKeyBinding("toggle_combat_mode", 82, "combat");
	public static final KeyBinding OPEN_STAT_SCREEN = makeKeyBinding("open_stat_screen", 77, "gui");
	
	// For temporary use
	public static final KeyBinding VISIBLE_HITBOXES = new KeyBinding("visible_hitboxes", 79, "debug");
	
	
	public static void registerKeys()
	{
		Minecraft minecraft = Minecraft.getInstance();
		
		removeKeyBinding(minecraft.options.keyTogglePerspective);
		minecraft.options.keyTogglePerspective = new FakeKeyBinding("key.togglePerspective");
		
		for (KeyBinding key : keyBindings) ClientRegistry.registerKeyBinding(key);
	}
	
	public static KeyBinding makeKeyBinding(String name, int defaultKey, String category)
	{
		name = "key."+DarkSouls.MOD_ID+"."+name;
		category = "key."+DarkSouls.MOD_ID+"."+category;
		
		KeyBinding key = new KeyBinding(name, defaultKey, category);
		keyBindings.add(key);
		return key;
	}
	
	private static void removeKeyBinding(KeyBinding binding)
	{
		Minecraft minecraft = Minecraft.getInstance();
		List<KeyBinding> keyBindings = new ArrayList<KeyBinding>();
		for (KeyBinding k : minecraft.options.keyMappings) keyBindings.add(k);
		keyBindings.remove(binding);
		
		minecraft.options.keyMappings = new KeyBinding[keyBindings.size()];
		for (int i = 0; i < keyBindings.size(); i++) minecraft.options.keyMappings[i] = keyBindings.get(i);
	}
}