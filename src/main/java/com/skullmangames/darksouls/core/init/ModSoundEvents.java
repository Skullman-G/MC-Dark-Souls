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
	
	public static final RegistryObject<SoundEvent> BONFIRE_LIT = register("block.bonfire.lit");
	public static final RegistryObject<SoundEvent> BONFIRE_AMBIENT = register("block.bonfire.ambient");

	public static final RegistryObject<SoundEvent> MIRACLE_USE = register("item.miracle.use");
	public static final RegistryObject<SoundEvent> MIRACLE_USE_PRE = register("item.miracle.use_pre");
	public static final RegistryObject<SoundEvent> MIRACLE_FORCE = register("item.miracle.force");
	
	public static final RegistryObject<SoundEvent> SOUL_CONTAINER_USE = register("item.soul_container.use");
	public static final RegistryObject<SoundEvent> SOUL_CONTAINER_FINISH = register("item.soul_container.finish");
	
	public static final RegistryObject<SoundEvent> SWORD_SWING = register("item.sword.swing");
	public static final RegistryObject<SoundEvent> FIST_SWING = register("item.fist.swing");
	public static final RegistryObject<SoundEvent> AXE_SWING = register("item.axe.swing");
	public static final RegistryObject<SoundEvent> SPEAR_SWING = register("item.spear.swing");
	public static final RegistryObject<SoundEvent> GREAT_HAMMER_SMASH = register("item.great_hammer.smash");
	public static final RegistryObject<SoundEvent> ULTRA_GREATSWORD_SMASH = register("item.ultra_greatsword.smash");
	
	public static final RegistryObject<SoundEvent> HOLLOW_AMBIENT = register("entity.hollow.ambient");
	public static final RegistryObject<SoundEvent> HOLLOW_DEATH = register("entity.hollow.death");
	public static final RegistryObject<SoundEvent> HOLLOW_PREPARE = register("entity.hollow.prepare");
	
	public static final RegistryObject<SoundEvent> STRAY_DEMON_AMBIENT = register("entity.stray_demon.ambient");
	public static final RegistryObject<SoundEvent> STRAY_DEMON_DEATH = register("entity.stray_demon.death");
	public static final RegistryObject<SoundEvent> STRAY_DEMON_FOOT = register("entity.stray_demon.foot");
	public static final RegistryObject<SoundEvent> STRAY_DEMON_SWING = register("entity.stray_demon.swing");
	public static final RegistryObject<SoundEvent> STRAY_DEMON_SMASH = register("entity.stray_demon.smash");
	public static final RegistryObject<SoundEvent> STRAY_DEMON_LAND = register("entity.stray_demon.land");
	public static final RegistryObject<SoundEvent> STRAY_DEMON_WING = register("entity.stray_demon.wing");
	
	public static final RegistryObject<SoundEvent> GENERIC_KILL = register("entity.generic.kill");
	public static final RegistryObject<SoundEvent> GENERIC_HUMAN_FORM = register("entity.generic.human_form");
	public static final RegistryObject<SoundEvent> GENERIC_HIT = register("entity.generic.hit");
	public static final RegistryObject<SoundEvent> GENERIC_ROLL = register("entity.generic.roll");
	
	public static final RegistryObject<SoundEvent> PLAYER_SHIELD_DISARMED = register("entity.player.shield_disarmed");
	
	public static final RegistryObject<SoundEvent> WOODEN_SHIELD_BLOCK = register("item.shield.wood.block");
	public static final RegistryObject<SoundEvent> IRON_SHIELD_BLOCK = register("item.shield.iron.block");
	public static final RegistryObject<SoundEvent> WEAPON_BLOCK = register("item.sword.block");
	
	public static final RegistryObject<SoundEvent> LIGHTNING_SPEAR_APPEAR = register("entity.lightning_spear.appear");
	public static final RegistryObject<SoundEvent> LIGHTNING_SPEAR_SHOT = register("entity.lightning_spear.shot");
	public static final RegistryObject<SoundEvent> LIGHTNING_SPEAR_IMPACT = register("entity.lightning_spear.impact");

	
	private static RegistryObject<SoundEvent> register(String name)
	{
		return SOUND_EVENTS.register(name, () -> new SoundEvent(new ResourceLocation(DarkSouls.MOD_ID, name)));
	}
}
