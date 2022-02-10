package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSoundEvents
{
	public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, DarkSouls.MOD_ID);
	
	public static final RegistryObject<SoundEvent> BONFIRE_LIT = registerSound("block.bonfire.lit");
	public static final RegistryObject<SoundEvent> BONFIRE_AMBIENT = registerSound("block.bonfire.ambient");

	public static final RegistryObject<SoundEvent> DARKSIGN_USE = registerSound("item.darksign.use");
	public static final RegistryObject<SoundEvent> SOUL_CONTAINER_USE = registerSound("item.soul_container.use");
	public static final RegistryObject<SoundEvent> SOUL_CONTAINER_FINISH = registerSound("item.soul_container.finish");
	
	public static final RegistryObject<SoundEvent> SWORD_SWING = registerSound("item.sword.swing");
	public static final RegistryObject<SoundEvent> FIST_SWING = registerSound("item.fist.swing");
	public static final RegistryObject<SoundEvent> AXE_SWING = registerSound("item.axe.swing");
	public static final RegistryObject<SoundEvent> SPEAR_SWING = registerSound("item.spear.swing");
	public static final RegistryObject<SoundEvent> GREAT_HAMMER_SMASH = registerSound("item.great_hammer.smash");
	public static final RegistryObject<SoundEvent> ULTRA_GREATSWORD_SMASH = registerSound("item.ultra_greatsword.smash");
	
	public static final RegistryObject<SoundEvent> HOLLOW_AMBIENT = registerSound("entity.hollow.ambient");
	public static final RegistryObject<SoundEvent> HOLLOW_DEATH = registerSound("entity.hollow.death");
	public static final RegistryObject<SoundEvent> HOLLOW_PREPARE = registerSound("entity.hollow.prepare");
	
	public static final RegistryObject<SoundEvent> ASYLUM_DEMON_AMBIENT = registerSound("entity.asylum_demon.ambient");
	public static final RegistryObject<SoundEvent> ASYLUM_DEMON_DEATH = registerSound("entity.asylum_demon.death");
	public static final RegistryObject<SoundEvent> ASYLUM_DEMON_FOOT = registerSound("entity.asylum_demon.foot");
	public static final RegistryObject<SoundEvent> ASYLUM_DEMON_SWING = registerSound("entity.asylum_demon.swing");
	public static final RegistryObject<SoundEvent> ASYLUM_DEMON_SMASH = registerSound("entity.asylum_demon.smash");
	public static final RegistryObject<SoundEvent> ASYLUM_DEMON_LAND = registerSound("entity.asylum_demon.land");
	public static final RegistryObject<SoundEvent> ASYLUM_DEMON_WING = registerSound("entity.asylum_demon.wing");
	
	public static final RegistryObject<SoundEvent> GENERIC_KILL = registerSound("entity.generic.kill");
	public static final RegistryObject<SoundEvent> GENERIC_HUMAN_FORM = registerSound("entity.generic.human_form");
	
	public static final RegistryObject<SoundEvent> PLAYER_SHIELD_DISARMED = registerSound("entity.player.shield_disarmed");

	
	private static RegistryObject<SoundEvent> registerSound(String name)
	{
		return SOUND_EVENTS.register(name, () -> new SoundEvent(new ResourceLocation(DarkSouls.MOD_ID, name)));
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
