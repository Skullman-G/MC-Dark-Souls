package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModSoundEvents
{
	public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, DarkSouls.MOD_ID);
	
	public static final RegistryObject<SoundEvent> BONFIRE_LIT = registerSound("block.bonfire.lit");
	public static final RegistryObject<SoundEvent> BONFIRE_AMBIENT = registerSound("block.bonfire.ambient");

	public static final RegistryObject<SoundEvent> DARKSIGN_USE = registerSound("item.darksign.use");
	public static final RegistryObject<SoundEvent> SOUL_CONTAINER_USE = registerSound("item.soul_container.use");
	public static final RegistryObject<SoundEvent> SOUL_CONTAINER_FINISH = registerSound("item.soul_container.finish");
	
	public static final SoundEvent SWORD_SWING = makeSoundEvent("item.sword.swing");
	public static final SoundEvent GREAT_HAMMER_SMASH = makeSoundEvent("item.great_hammer.smash");
	
	public static final RegistryObject<SoundEvent> HOLLOW_AMBIENT = registerSound("entity.hollow.ambient");
	public static final RegistryObject<SoundEvent> HOLLOW_DEATH = registerSound("entity.hollow.death");
	public static final SoundEvent HOLLOW_PREPARE = makeSoundEvent("entity.hollow.prepare");
	
	public static final RegistryObject<SoundEvent> ASYLUM_DEMON_AMBIENT = registerSound("entity.asylum_demon.ambient");
	public static final RegistryObject<SoundEvent> ASYLUM_DEMON_DEATH = registerSound("entity.asylum_demon.death");
	public static final SoundEvent ASYLUM_DEMON_FOOT = makeSoundEvent("entity.asylum_demon.foot");
	public static final SoundEvent ASYLUM_DEMON_SWING = makeSoundEvent("entity.asylum_demon.swing");
	public static final SoundEvent ASYLUM_DEMON_SMASH = makeSoundEvent("entity.asylum_demon.smash");
	public static final SoundEvent ASYLUM_DEMON_LAND = makeSoundEvent("entity.asylum_demon.land");
	public static final SoundEvent ASYLUM_DEMON_WING = makeSoundEvent("entity.asylum_demon.wing");
	
	public static final RegistryObject<SoundEvent> GENERIC_KILL = registerSound("entity.generic.kill");
	public static final RegistryObject<SoundEvent> GENERIC_HUMAN_FORM = registerSound("entity.generic.human_form");
	
	public static final RegistryObject<SoundEvent> PLAYER_SHIELD_DISARMED = registerSound("entity.player.shield_disarmed");

	
	private static RegistryObject<SoundEvent> registerSound(String name)
	{
		return SOUND_EVENTS.register(name, () -> new SoundEvent(new ResourceLocation(DarkSouls.MOD_ID, name)));
	}
	
	public static SoundEvent makeSoundEvent(String name)
	{
		ResourceLocation res = new ResourceLocation(DarkSouls.MOD_ID, name);
		return new SoundEvent(res).setRegistryName(res);
	}
	
	// Override Vanilla Sound Events
	public static final DeferredRegister<SoundEvent> VANILLA_SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, "minecraft");
	
	public static final RegistryObject<SoundEvent> GENERIC_HURT = overrideVanillaSound("entity.generic.hurt");
	public static final RegistryObject<SoundEvent> PLAYER_HURT = overrideVanillaSound("entity.player.hurt");
	public static final RegistryObject<SoundEvent> HOSTILE_HURT = overrideVanillaSound("entity.hostile.hurt");
	

	private static RegistryObject<SoundEvent> overrideVanillaSound(String name)
	{
		return VANILLA_SOUND_EVENTS.register(name, () -> new SoundEvent(new ResourceLocation(DarkSouls.MOD_ID, name)));
	}
}
