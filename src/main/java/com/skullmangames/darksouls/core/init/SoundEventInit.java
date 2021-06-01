package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class SoundEventInit
{
	public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, DarkSouls.MOD_ID);

	public static final RegistryObject<SoundEvent> BONFIRE_LIT = SOUND_EVENTS.register("block.bonfire.lit", () -> new SoundEvent(new ResourceLocation(DarkSouls.MOD_ID, "block.bonfire.lit")));
	
	public static final RegistryObject<SoundEvent> BONFIRE_AMBIENT = SOUND_EVENTS.register("block.bonfire.ambient", () -> new SoundEvent(new ResourceLocation(DarkSouls.MOD_ID, "block.bonfire.ambient")));
}
