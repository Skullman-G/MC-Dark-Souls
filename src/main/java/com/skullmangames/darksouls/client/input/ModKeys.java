package com.skullmangames.darksouls.client.input;

import com.skullmangames.darksouls.DarkSouls;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.ClientRegistry;

@OnlyIn(Dist.CLIENT)
public class ModKeys
{
	public static final KeyBinding SPECIAL_ATTACK_TOOLTIP = new KeyBinding("key." + DarkSouls.MOD_ID + ".show_tooltip", 80, "key." + DarkSouls.MOD_ID + ".gui");
	public static final KeyBinding SWAP_ACTION_MODE = new KeyBinding("key." + DarkSouls.MOD_ID + ".swap_action_mode", 294, "key." + DarkSouls.MOD_ID + ".combat");
	
	public static void registerKeys()
	{
		Minecraft minecraft = Minecraft.getInstance();
		
		minecraft.options.keyTogglePerspective = new FakeKeyBinding("key.togglePerspective");
		
		ClientRegistry.registerKeyBinding(SPECIAL_ATTACK_TOOLTIP);
		ClientRegistry.registerKeyBinding(SWAP_ACTION_MODE);
	}
}