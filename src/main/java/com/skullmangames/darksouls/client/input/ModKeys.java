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
	public static final KeyBinding SWAP_ACTION_MODE = makeKeyBinding("swap_action_mode", 294, "combat");
	
	
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