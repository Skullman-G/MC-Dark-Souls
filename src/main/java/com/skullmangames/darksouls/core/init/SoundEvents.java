package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class SoundEvents
{
	public static final SoundEvent BONFIRE_LIT = registerSound("block.bonfire.lit");
	public static final SoundEvent BONFIRE_AMBIENT = registerSound("block.bonfire.ambient");

	public static final SoundEvent DARKSIGN_USE = registerSound("item.darksign.use");
	public static final SoundEvent SOUL_CONTAINER_USE = registerSound("item.soul_container.use");
	public static final SoundEvent SOUL_CONTAINER_FINISH = registerSound("item.soul_container.finish");
	public static final SoundEvent SWORD_SWING = registerSound("item.sword.swing");
	
	public static final SoundEvent HOLLOW_AMBIENT = registerSound("entity.hollow.ambient");
	public static final SoundEvent HOLLOW_DEATH = registerSound("entity.hollow.death");
	public static final SoundEvent HOLLOW_PREPARE = registerSound("entity.hollow.prepare");
	
	public static final SoundEvent GENERIC_KILL = registerSound("entity.generic.kill");

	
	private static SoundEvent registerSound(String name)
	{
		ResourceLocation res = new ResourceLocation(DarkSouls.MOD_ID, name);
		SoundEvent soundEvent = new SoundEvent(res).setRegistryName(name);
		
		return soundEvent;
	}
	
	// Override Vanilla Sound Events
	public static final DeferredRegister<SoundEvent> VANILLA_SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, "minecraft");
	
	public static final RegistryObject<SoundEvent> GENERIC_HURT = registerVanillaSound("entity.generic.hurt");
	public static final RegistryObject<SoundEvent> PLAYER_HURT = registerVanillaSound("entity.player.hurt");
	public static final RegistryObject<SoundEvent> HOSTILE_HURT = registerVanillaSound("entity.hostile.hurt");
	

	private static RegistryObject<SoundEvent> registerVanillaSound(String name)
	{
		return VANILLA_SOUND_EVENTS.register(name, () -> new SoundEvent(new ResourceLocation(DarkSouls.MOD_ID, name)));
	}
}
