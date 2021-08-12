package com.skullmangames.darksouls.client.input;

import com.skullmangames.darksouls.DarkSouls;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.ClientRegistry;

@OnlyIn(Dist.CLIENT)
public class ModKeys
{
	public static final KeyBinding SPECIAL_ATTACK_TOOLTIP = new KeyBinding("key." + DarkSouls.MOD_ID + ".show_tooltip", 80, "key." + DarkSouls.MOD_ID + ".gui");
	
	public static void registerKeys()
	{
		ClientRegistry.registerKeyBinding(SPECIAL_ATTACK_TOOLTIP);
	}
}