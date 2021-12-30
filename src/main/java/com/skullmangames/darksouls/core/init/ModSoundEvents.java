package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModSoundEvents
{
	public static final SoundEvent BONFIRE_LIT = registerSound("block.bonfire.lit");
	public static final SoundEvent BONFIRE_AMBIENT = registerSound("block.bonfire.ambient");

	public static final SoundEvent DARKSIGN_USE = registerSound("item.darksign.use");
	public static final SoundEvent SOUL_CONTAINER_USE = registerSound("item.soul_container.use");
	public static final SoundEvent SOUL_CONTAINER_FINISH = registerSound("item.soul_container.finish");
	public static final SoundEvent SWORD_SWING = registerSound("item.sword.swing");
	public static final SoundEvent GREAT_HAMMER_SMASH = registerSound("item.great_hammer.smash");
	
	public static final SoundEvent HOLLOW_AMBIENT = registerSound("entity.hollow.ambient");
	public static final SoundEvent HOLLOW_DEATH = registerSound("entity.hollow.death");
	public static final SoundEvent HOLLOW_PREPARE = registerSound("entity.hollow.prepare");
	
	public static final SoundEvent ASYLUM_DEMON_AMBIENT = registerSound("entity.asylum_demon.ambient");
	public static final SoundEvent ASYLUM_DEMON_LAND = registerSound("entity.asylum_demon.land");
	public static final SoundEvent ASYLUM_DEMON_WING = registerSound("entity.asylum_demon.wing");
	public static final SoundEvent ASYLUM_DEMON_FOOT = registerSound("entity.asylum_demon.foot");
	public static final SoundEvent ASYLUM_DEMON_DEATH = registerSound("entity.asylum_demon.death");
	public static final SoundEvent ASYLUM_DEMON_SWING = registerSound("entity.asylum_demon.swing");
	public static final SoundEvent ASYLUM_DEMON_SMASH = registerSound("entity.asylum_demon.smash");
	
	public static final SoundEvent GENERIC_KILL = registerSound("entity.generic.kill");
	public static final SoundEvent GENERIC_HUMAN_FORM = registerSound("entity.generic.human_form");
	
	public static final SoundEvent PLAYER_SHIELD_DISARMED = registerSound("entity.player.shield_disarmed");

	
	private static SoundEvent registerSound(String name)
	{
		ResourceLocation res = new ResourceLocation(DarkSouls.MOD_ID, name);
		SoundEvent soundEvent = new SoundEvent(res).setRegistryName(name);
		
		return soundEvent;
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
