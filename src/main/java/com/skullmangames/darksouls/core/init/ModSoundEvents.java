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
	public static final RegistryObject<SoundEvent> BONFIRE_TELEPORT = register("block.bonfire.teleport");

	public static final RegistryObject<SoundEvent> MIRACLE_USE = register("item.miracle.use");
	public static final RegistryObject<SoundEvent> MIRACLE_USE_PRE = register("item.miracle.use_pre");
	public static final RegistryObject<SoundEvent> MIRACLE_FORCE = register("item.miracle.force");
	
	public static final RegistryObject<SoundEvent> SOUL_CONTAINER_USE = register("item.soul_container.use");
	public static final RegistryObject<SoundEvent> SOUL_CONTAINER_FINISH = register("item.soul_container.finish");
	
	public static final RegistryObject<SoundEvent> SWORD_SWING = register("item.sword.swing");
	public static final RegistryObject<SoundEvent> SWORD_PULLOUT = register("item.sword.pullout");
	public static final RegistryObject<SoundEvent> FIST_SWING = register("item.fist.swing");
	public static final RegistryObject<SoundEvent> AXE_SWING = register("item.axe.swing");
	public static final RegistryObject<SoundEvent> SPEAR_SWING = register("item.spear.swing");
	public static final RegistryObject<SoundEvent> SWORD_THRUST = register("item.sword.thrust");
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
	public static final RegistryObject<SoundEvent> GENERIC_ROLL = register("entity.generic.roll");
	public static final RegistryObject<SoundEvent> GENERIC_LAND = register("entity.generic.land");
	public static final RegistryObject<SoundEvent> GENERIC_KICK = register("entity.generic.kick");
	public static final RegistryObject<SoundEvent> GENERIC_PARRY_SUCCESS = register("entity.generic.parry_success");
	public static final RegistryObject<SoundEvent> GENERIC_KILL_CHANCE = register("entity.generic.kill_chance");
	
	public static final RegistryObject<SoundEvent> PLAYER_SHIELD_DISARMED = register("entity.player.shield_disarmed");
	
	public static final RegistryObject<SoundEvent> WOODEN_SHIELD_BLOCK = register("item.shield.wood.block");
	public static final RegistryObject<SoundEvent> IRON_SHIELD_BLOCK = register("item.shield.iron.block");
	public static final RegistryObject<SoundEvent> WEAPON_BLOCK = register("item.sword.block");
	
	public static final RegistryObject<SoundEvent> LIGHTNING_SPEAR_APPEAR = register("entity.lightning_spear.appear");
	public static final RegistryObject<SoundEvent> LIGHTNING_SPEAR_SHOT = register("entity.lightning_spear.shot");
	public static final RegistryObject<SoundEvent> LIGHTNING_SPEAR_IMPACT = register("entity.lightning_spear.impact");
	
	public static final RegistryObject<SoundEvent> VASE_BREAK = register("block.vase.break");
	public static final RegistryObject<SoundEvent> BARREL_BREAK = register("block.barrel.break");
	
	public static final RegistryObject<SoundEvent> BLACK_KNIGHT_FOOT = register("entity.black_knight.foot");
	public static final RegistryObject<SoundEvent> BLACK_KNIGHT_DAMAGE = register("entity.black_knight.damage");
	public static final RegistryObject<SoundEvent> BLACK_KNIGHT_DEATH = register("entity.black_knight.death");
	
	public static final RegistryObject<SoundEvent> TAURUS_DEMON_AMBIENT = register("entity.taurus_demon.ambient");
	public static final RegistryObject<SoundEvent> TAURUS_DEMON_DEATH = register("entity.taurus_demon.death");
	
	public static final RegistryObject<SoundEvent> BALDER_KNIGHT_DAMAGE = register("entity.balder_knight.damage");
	public static final RegistryObject<SoundEvent> BALDER_KNIGHT_DEATH = register("entity.balder_knight.death");
	public static final RegistryObject<SoundEvent> BALDER_KNIGHT_FOOT = register("entity.balder_knight.foot");
	public static final RegistryObject<SoundEvent> BALDER_KNIGHT_AMBIENT = register("entity.balder_knight.ambient");
	
	public static final RegistryObject<SoundEvent> BERENIKE_KNIGHT_FOOT = register("entity.berenike_knight.foot");
	public static final RegistryObject<SoundEvent> BERENIKE_KNIGHT_DAMAGE = register("entity.berenike_knight.damage");
	public static final RegistryObject<SoundEvent> BERENIKE_KNIGHT_FALL_BIG = register("entity.berenike_knight.fall_big");
	public static final RegistryObject<SoundEvent> BERENIKE_KNIGHT_FALL_SMALL = register("entity.berenike_knight.fall_small");
	public static final RegistryObject<SoundEvent> BERENIKE_KNIGHT_DEATH = register("entity.berenike_knight.death");
	public static final RegistryObject<SoundEvent> BERENIKE_KNIGHT_AMBIENT = register("entity.berenike_knight.ambient");
	
	public static final RegistryObject<SoundEvent> BOMB_EXPLOSION = register("entity.generic.bomb");

	
	private static RegistryObject<SoundEvent> register(String name)
	{
		return SOUND_EVENTS.register(name, () -> new SoundEvent(new ResourceLocation(DarkSouls.MOD_ID, name)));
	}
}
