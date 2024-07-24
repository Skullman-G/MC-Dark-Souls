package com.skullmangames.darksouls.core.init;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.animation.AnimationLayer.LayerPart;
import com.skullmangames.darksouls.common.animation.AnimationManager;
import com.skullmangames.darksouls.common.animation.types.AdaptableAnimation;
import com.skullmangames.darksouls.common.animation.types.DeathAnimation;
import com.skullmangames.darksouls.common.animation.types.MirrorAnimation;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.animation.types.SupplierAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.ParryAnimation;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.util.AbstractArrayGetter;
import com.skullmangames.darksouls.core.util.AbstractGetter;

import net.minecraft.resources.ResourceLocation;

public final class Animations
{
	public static final StaticAnimation DUMMY_ANIMATION = new StaticAnimation();

	public static final Getter BIPED_IDLE = new Getter(DarkSouls.rl("biped_idle"));
	public static final Getter BIPED_WALK = new Getter(DarkSouls.rl("biped_walk"));
	public static final Getter BIPED_RUN = new Getter(DarkSouls.rl("biped_run"));
	public static final Getter BIPED_SNEAK = new Getter(DarkSouls.rl("biped_sneak"));
	
	public static final Getter BIPED_IDLE_TH = new Getter(DarkSouls.rl("biped_idle_th"));
	public static final Getter BIPED_WALK_TH = new Getter(DarkSouls.rl("biped_walk_th"));
	public static final Getter BIPED_RUN_TH = new Getter(DarkSouls.rl("biped_run_th"));
	
	public static final Getter BIPED_IDLE_TH_BIG_WEAPON = new Getter(DarkSouls.rl("biped_idle_th_big_weapon"));
	
	public static final Getter BIPED_IDLE_TH_SPEAR = new Getter(DarkSouls.rl("biped_idle_th_spear"));
	
	public static final Getter BIPED_IDLE_TH_SHIELD = new Getter(DarkSouls.rl("biped_idle_th_shield"));
	
	public static final Getter BIPED_CHANGE_ITEM_RIGHT = new Getter(DarkSouls.rl("biped_change_item_right"));
	public static final Getter BIPED_CHANGE_ITEM_LEFT = new Getter(DarkSouls.rl("biped_change_item_left"));
	
	public static final Getter BIPED_SWIM = new Getter(DarkSouls.rl("biped_swim"));
	public static final Getter BIPED_FLOAT = new Getter(DarkSouls.rl("biped_float"));
	public static final Getter BIPED_KNEEL = new Getter(DarkSouls.rl("biped_kneel"));
	public static final Getter BIPED_FALL = new Getter(DarkSouls.rl("biped_fall"));
	
	public static final DeathGetter BIPED_DEATH = new DeathGetter(DarkSouls.rl("biped_death"));
	public static final DeathGetter BIPED_DEATH_SMASH = new DeathGetter(DarkSouls.rl("biped_death_smash"));
	public static final DeathGetter BIPED_DEATH_FLY_FRONT = new DeathGetter(DarkSouls.rl("biped_death_fly_front"));
	public static final DeathGetter BIPED_DEATH_FLY_BACK = new DeathGetter(DarkSouls.rl("biped_death_fly_back"));
	public static final DeathGetter BIPED_DEATH_FLY_LEFT = new DeathGetter(DarkSouls.rl("biped_death_fly_left"));
	public static final DeathGetter BIPED_DEATH_FLY_RIGHT = new DeathGetter(DarkSouls.rl("biped_death_fly_right"));
	public static final DeathGetter BIPED_DEATH_BACKSTAB = new DeathGetter(DarkSouls.rl("biped_death_backstab"));
	public static final DeathGetter BIPED_DEATH_PUNISH = new DeathGetter(DarkSouls.rl("biped_death_punish"));
	
	public static final Getter BIPED_DIG = new Getter(DarkSouls.rl("biped_dig"));
	public static final Getter BIPED_TOUCH_BONFIRE = new Getter(DarkSouls.rl("biped_touch_bonfire"));

	public static final Getter BIPED_EAT = new Getter(DarkSouls.rl("biped_eat"));
	public static final Getter BIPED_DRINK = new Getter(DarkSouls.rl("biped_drink"));
	public static final Getter BIPED_CONSUME_SOUL = new Getter(DarkSouls.rl("biped_consume_soul"));
	
	public static final Getter BIPED_THROW = new Getter(DarkSouls.rl("biped_throw"));

	public static final Getter BIPED_BLOCK_HORIZONTAL = new Getter(DarkSouls.rl("biped_block_horizontal"));
	public static final Getter BIPED_BLOCK_VERTICAL = new Getter(DarkSouls.rl("biped_block_vertical"));
	public static final Getter BIPED_BLOCK_GREATSHIELD = new Getter(DarkSouls.rl("biped_block_greatshield"));
	public static final Getter BIPED_BLOCK_TH_SWORD = new Getter(DarkSouls.rl("biped_block_th_sword"));
	public static final Getter BIPED_BLOCK_TH_VERTICAL = new Getter(DarkSouls.rl("biped_block_th_vertical"));
	public static final Getter BIPED_BLOCK_TH_HORIZONTAL = new Getter(DarkSouls.rl("biped_block_th_horizontal"));
	public static final Getter BIPED_BLOCK_TH_GREATSHIELD = new Getter(DarkSouls.rl("biped_block_th_greatshield"));
	
	public static final Getter BIPED_HIT_BLOCKED_LEFT = new Getter(DarkSouls.rl("biped_hit_blocked_left"));
	public static final Getter BIPED_HIT_BLOCKED_RIGHT = new Getter(DarkSouls.rl("biped_hit_blocked_right"));
	public static final Getter BIPED_HIT_BLOCKED_VERTICAL_LEFT = new Getter(DarkSouls.rl("biped_hit_blocked_vertical_left"));
	public static final Getter BIPED_HIT_BLOCKED_VERTICAL_RIGHT = new Getter(DarkSouls.rl("biped_hit_blocked_vertical_right"));
	public static final Getter BIPED_HIT_BLOCKED_TH_SWORD = new Getter(DarkSouls.rl("biped_hit_blocked_th_sword"));
	
	public static final Getter BIPED_HIT_BLOCKED_FLY_LEFT = new Getter(DarkSouls.rl("biped_hit_blocked_fly_left"));
	public static final Getter BIPED_HIT_BLOCKED_FLY_RIGHT = new Getter(DarkSouls.rl("biped_hit_blocked_fly_right"));
	public static final Getter BIPED_HIT_BLOCKED_VERTICAL_FLY_LEFT = new Getter(DarkSouls.rl("biped_hit_blocked_vertical_fly_left"));
	public static final Getter BIPED_HIT_BLOCKED_VERTICAL_FLY_RIGHT = new Getter(DarkSouls.rl("biped_hit_blocked_vertical_fly_right"));
	public static final Getter BIPED_HIT_BLOCKED_TH_SWORD_FLY = new Getter(DarkSouls.rl("biped_hit_blocked_th_sword_fly"));
	
	public static final Getter BIPED_DISARMED_LEFT = new Getter(DarkSouls.rl("biped_disarmed_left"));
	public static final Getter BIPED_DISARMED_RIGHT = new Getter(DarkSouls.rl("biped_disarmed_right"));
	
	public static final Getter BIPED_HORSEBACK_IDLE = new Getter(DarkSouls.rl("biped_horseback_idle"));
	
	public static final Getter BIPED_IDLE_CROSSBOW = new Getter(DarkSouls.rl("biped_idle_crossbow"));
	public static final Getter BIPED_WALK_CROSSBOW = new Getter(DarkSouls.rl("biped_walk_crossbow"));

	public static final Getter BIPED_CROSSBOW_AIM = new Getter(DarkSouls.rl("biped_crossbow_aim"));
	public static final Getter BIPED_CROSSBOW_SHOT = new Getter(DarkSouls.rl("biped_crossbow_shot"));

	public static final Getter BIPED_CROSSBOW_RELOAD = new Getter(DarkSouls.rl("biped_crossbow_reload"));

	public static final Getter BIPED_BOW_AIM = new Getter(DarkSouls.rl("biped_bow_aim"));
	public static final Getter BIPED_BOW_REBOUND = new Getter(DarkSouls.rl("biped_bow_rebound"));

	public static final Getter BIPED_SPEER_AIM = new Getter(DarkSouls.rl("biped_speer_aim"));
	public static final Getter BIPED_SPEER_REBOUND = new Getter(DarkSouls.rl("biped_speer_rebound"));

	public static final Getter BIPED_HIT_LIGHT_FRONT = new Getter(DarkSouls.rl("biped_hit_light_front"));
	public static final Getter BIPED_HIT_LIGHT_LEFT = new Getter(DarkSouls.rl("biped_hit_light_left"));
	public static final Getter BIPED_HIT_LIGHT_RIGHT = new Getter(DarkSouls.rl("biped_hit_light_right"));
	public static final Getter BIPED_HIT_LIGHT_BACK = new Getter(DarkSouls.rl("biped_hit_light_back"));
	public static final Getter BIPED_HIT_HEAVY_FRONT = new Getter(DarkSouls.rl("biped_hit_heavy_front"));
	public static final Getter BIPED_HIT_HEAVY_BACK = new Getter(DarkSouls.rl("biped_hit_heavy_back"));
	public static final Getter BIPED_HIT_HEAVY_LEFT = new Getter(DarkSouls.rl("biped_hit_heavy_left"));
	public static final Getter BIPED_HIT_HEAVY_RIGHT = new Getter(DarkSouls.rl("biped_hit_heavy_right"));
	
	public static final Getter BIPED_HORSEBACK_HIT_LIGHT_FRONT = new Getter(DarkSouls.rl("biped_horseback_hit_light_front"));
	public static final Getter BIPED_HORSEBACK_HIT_LIGHT_LEFT = new Getter(DarkSouls.rl("biped_horseback_hit_light_left"));
	public static final Getter BIPED_HORSEBACK_HIT_LIGHT_RIGHT = new Getter(DarkSouls.rl("biped_horseback_hit_light_right"));
	public static final Getter BIPED_HORSEBACK_HIT_LIGHT_BACK = new Getter(DarkSouls.rl("biped_horseback_hit_light_back"));
	public static final Getter BIPED_HORSEBACK_HIT_HEAVY_FRONT = new Getter(DarkSouls.rl("biped_horseback_hit_heavy_front"));
	public static final Getter BIPED_HORSEBACK_HIT_HEAVY_BACK = new Getter(DarkSouls.rl("biped_horseback_hit_heavy_back"));
	public static final Getter BIPED_HORSEBACK_HIT_HEAVY_LEFT = new Getter(DarkSouls.rl("biped_horseback_hit_heavy_left"));
	public static final Getter BIPED_HORSEBACK_HIT_HEAVY_RIGHT = new Getter(DarkSouls.rl("biped_horseback_hit_heavy_right"));
	
	public static final Getter BIPED_HIT_SMASH = new Getter(DarkSouls.rl("biped_hit_smash"));
	public static final Getter BIPED_HIT_FLY_FRONT = new Getter(DarkSouls.rl("biped_hit_fly_front"));
	public static final Getter BIPED_HIT_FLY_BACK = new Getter(DarkSouls.rl("biped_hit_fly_back"));
	public static final Getter BIPED_HIT_FLY_LEFT = new Getter(DarkSouls.rl("biped_hit_fly_left"));
	public static final Getter BIPED_HIT_FLY_RIGHT = new Getter(DarkSouls.rl("biped_hit_fly_right"));
	public static final Getter BIPED_HIT_LAND_HEAVY = new Getter(DarkSouls.rl("biped_hit_land_heavy"));
	
	public static final Getter BIPED_HIT_BACKSTAB = new Getter(DarkSouls.rl("biped_hit_backstab"));
	public static final Getter BIPED_HIT_PUNISH = new Getter(DarkSouls.rl("biped_hit_punish"));
	
	public static final Getter BIPED_ROLL = new Getter(DarkSouls.rl("biped_roll"));
	public static final Getter BIPED_FAT_ROLL = new Getter(DarkSouls.rl("biped_fat_roll"));
	public static final Getter BIPED_ROLL_TOO_FAT = new Getter(DarkSouls.rl("biped_roll_too_fat"));
	public static final Getter BIPED_ROLL_BACK = new Getter(DarkSouls.rl("biped_roll_back"));
	public static final Getter BIPED_ROLL_LEFT = new Getter(DarkSouls.rl("biped_roll_left"));
	public static final Getter BIPED_ROLL_RIGHT = new Getter(DarkSouls.rl("biped_roll_right"));
	public static final Getter BIPED_JUMP_BACK = new Getter(DarkSouls.rl("biped_jump_back"));
	
	// Miracle
	public static final Getter BIPED_CAST_MIRACLE_HEAL = new Getter(DarkSouls.rl("biped_cast_miracle_heal"));
	
	public static final Getter BIPED_CAST_MIRACLE_HEAL_AID = new Getter(DarkSouls.rl("biped_cast_miracle_heal_aid"));
	
	public static final Getter BIPED_CAST_MIRACLE_HOMEWARD = new Getter(DarkSouls.rl("biped_cast_miracle_homeward"));
	
	public static final Getter BIPED_CAST_MIRACLE_FORCE = new Getter(DarkSouls.rl("biped_cast_miracle_force"));

	public static final Getter BIPED_CAST_MIRACLE_LIGHTNING_SPEAR = new Getter(DarkSouls.rl("biped_cast_miracle_lightning_spear"));
	public static final Getter HORSEBACK_CAST_MIRACLE_LIGHTNING_SPEAR = new Getter(DarkSouls.rl("horseback_cast_miracle_lightning_spear"));

	public static final Getter BIPED_CAST_MIRACLE_GREAT_LIGHTNING_SPEAR = new Getter(DarkSouls.rl("biped_cast_miracle_great_lightning_spear"));
	public static final Getter HORSEBACK_CAST_MIRACLE_GREAT_LIGHTNING_SPEAR = new Getter(DarkSouls.rl("horseback_cast_miracle_great_lightning_spear"));
	
	// Big Weapon
	public static final MirrorGetter BIPED_HOLDING_BIG_WEAPON = new MirrorGetter(DarkSouls.rl("biped_holding_big_weapon"));
	
	// Horseback Attacks
	public static final AttackArrayGetter HORSEBACK_LIGHT_ATTACK = new AttackArrayGetter
		(
					DarkSouls.rl("horseback_light_attack_1"),
					DarkSouls.rl("horseback_light_attack_2")
		);
	
	// Parries
	public static final ParryGetter SHIELD_PARRY_LEFT = new ParryGetter(DarkSouls.rl("shield_parry_left"));
	public static final ParryGetter SHIELD_PARRY_RIGHT = new ParryGetter(DarkSouls.rl("shield_parry_right"));
	public static final ParryGetter BUCKLER_PARRY_LEFT = new ParryGetter(DarkSouls.rl("buckler_parry_left"));
	public static final ParryGetter BUCKLER_PARRY_RIGHT = new ParryGetter(DarkSouls.rl("buckler_parry_right"));
	
	// Backstabs
	public static final AttackGetter BACKSTAB_THRUST = new AttackGetter(DarkSouls.rl("backstab_thrust"));
	public static final AttackGetter BACKSTAB_STRIKE = new AttackGetter(DarkSouls.rl("backstab_strike"));
	
	// Punishes
	public static final AttackGetter PUNISH_THRUST = new AttackGetter(DarkSouls.rl("punish_thrust"));
	public static final AttackGetter PUNISH_STRIKE = new AttackGetter(DarkSouls.rl("punish_strike"));
	
	// Thrusting Sword
	public static final AttackGetter THRUSTING_SWORD_LIGHT_ATTACK = new AttackGetter(DarkSouls.rl("thrusting_sword_light_attack"));
	public static final AttackArrayGetter THRUSTING_SWORD_HEAVY_ATTACK = new AttackArrayGetter
			(
					DarkSouls.rl("thrusting_sword_heavy_attack_1"),
					DarkSouls.rl("thrusting_sword_heavy_attack_2")
			);
	public static final AttackGetter THRUSTING_SWORD_DASH_ATTACK = new AttackGetter(DarkSouls.rl("thrusting_sword_dash_attack"));
	public static final AttackGetter THRUSTING_SWORD_TH_LIGHT_ATTACK = new AttackGetter(DarkSouls.rl("thrusting_sword_th_light_attack"));
	public static final AttackArrayGetter THRUSTING_SWORD_TH_HEAVY_ATTACK = new AttackArrayGetter
			(
					DarkSouls.rl("thrusting_sword_th_heavy_attack_1"),
					DarkSouls.rl("thrusting_sword_th_heavy_attack_2")
			);
	public static final AttackGetter THRUSTING_SWORD_TH_DASH_ATTACK = new AttackGetter(DarkSouls.rl("thrusting_sword_th_dash_attack"));
	
	// Greatsword
	public static final AttackArrayGetter GREATSWORD_LIGHT_ATTACK = new AttackArrayGetter
			(
					DarkSouls.rl("greatsword_light_attack_1"),
					DarkSouls.rl("greatsword_light_attack_2")
			);
	public static final AttackGetter GREATSWORD_THRUST = new AttackGetter(DarkSouls.rl("greatsword_thrust"));
	public static final AttackGetter GREATSWORD_DASH_ATTACK = new AttackGetter(DarkSouls.rl("greatsword_dash_attack"));
	public static final AttackGetter GREATSWORD_UPWARD_SLASH = new AttackGetter(DarkSouls.rl("greatsword_upward_slash"));
	public static final AttackGetter GREATSWORD_STYLISH_THRUST = new AttackGetter(DarkSouls.rl("greatsword_stylish_thrust"));
	public static final AttackArrayGetter GREATSWORD_TH_LIGHT_ATTACK = new AttackArrayGetter
			(
					DarkSouls.rl("greatsword_th_la_1"),
					DarkSouls.rl("greatsword_th_la_2")
			);
	public static final AttackArrayGetter GREATSWORD_TH_THRUST_ATTACK = new AttackArrayGetter
			(
					DarkSouls.rl("greatsword_th_ha_1"),
					DarkSouls.rl("greatsword_th_ha_2")
			);
	public static final AttackGetter GREATSWORD_TH_DASH_ATTACK = new AttackGetter(DarkSouls.rl("greatsword_th_dash_attack"));

	// Ultra Greatsword
	public static final AttackArrayGetter ULTRA_GREATSWORD_LIGHT_ATTACK = new AttackArrayGetter
			(
					DarkSouls.rl("ultra_greatsword_light_attack_1"),
					DarkSouls.rl("ultra_greatsword_light_attack_2")
			);
	public static final AttackArrayGetter ULTRA_GREATSWORD_HEAVY_ATTACK = new AttackArrayGetter
			(
					DarkSouls.rl("ultra_greatsword_heavy_attack_1"),
					DarkSouls.rl("ultra_greatsword_heavy_attack_2")
			);
	public static final AttackGetter ULTRA_GREATSWORD_DASH_ATTACK = new AttackGetter(DarkSouls.rl("ultra_greatsword_dash_attack"));
	public static final AttackArrayGetter ULTRA_GREATSWORD_TH_LIGHT_ATTACK = new AttackArrayGetter
			(
					DarkSouls.rl("ultra_greatsword_th_la_1"),
					DarkSouls.rl("ultra_greatsword_th_la_2")
			);
	public static final AttackArrayGetter ULTRA_GREATSWORD_TH_HEAVY_ATTACK = new AttackArrayGetter
			(
					DarkSouls.rl("ultra_greatsword_th_ha_1"),
					DarkSouls.rl("ultra_greatsword_th_ha_2")
			);
	public static final AttackGetter ULTRA_GREATSWORD_TH_DASH_ATTACK = new AttackGetter(DarkSouls.rl("ultra_greatsword_th_dash_attack"));
	
	// Greataxe
	public static final AttackArrayGetter GREATAXE_LIGHT_ATTACK = new AttackArrayGetter
			(
					DarkSouls.rl("greataxe_la_1"),
					DarkSouls.rl("greataxe_la_2")
			);
	public static final AttackGetter GREATAXE_HEAVY_ATTACK = new AttackGetter(DarkSouls.rl("greataxe_heavy_attack"));
	public static final AttackGetter GREATAXE_DASH_ATTACK = new AttackGetter(DarkSouls.rl("greataxe_dash_attack"));
	public static final AttackArrayGetter GREATAXE_TH_LIGHT_ATTACK = new AttackArrayGetter
			(
					DarkSouls.rl("greataxe_th_la_1"),
					DarkSouls.rl("greataxe_th_la_2")
			);
	public static final AttackGetter GREATAXE_TH_HEAVY_ATTACK = new AttackGetter(DarkSouls.rl("greataxe_th_heavy_attack"));
	public static final AttackGetter GREATAXE_TH_DASH_ATTACK = new AttackGetter(DarkSouls.rl("greataxe_th_dash_attack"));

	// Spear
	public static final AttackGetter SPEAR_DASH_ATTACK = new AttackGetter(DarkSouls.rl("spear_dash_attack"));
	public static final AttackGetter SPEAR_HEAVY_ATTACK = new AttackGetter(DarkSouls.rl("spear_heavy_attack"));
	public static final AttackGetter SPEAR_LIGHT_ATTACK = new AttackGetter(DarkSouls.rl("spear_light_attack"));
	public static final AttackGetter SPEAR_LIGHT_BLOCKING_ATTACK = new AttackGetter(DarkSouls.rl("spear_light_blocking_attack"));
	public static final AttackGetter SPEAR_TH_LIGHT_ATTACK = new AttackGetter(DarkSouls.rl("spear_th_light_attack"));
	public static final AttackGetter SPEAR_TH_HEAVY_ATTACK = new AttackGetter(DarkSouls.rl("spear_th_heavy_attack"));
	public static final AttackGetter SPEAR_TH_DASH_ATTACK = new AttackGetter(DarkSouls.rl("spear_th_dash_attack"));

	// Dagger
	public static final AttackGetter DAGGER_HEAVY_ATTACK = new AttackGetter(DarkSouls.rl("dagger_heavy_attack"));
	public static final AttackArrayGetter DAGGER_LIGHT_ATTACK = new AttackArrayGetter
			(
					DarkSouls.rl("dagger_light_attack_1"),
					DarkSouls.rl("dagger_light_attack_2")
			);

	// Great Hammer
	public static final AttackGetter GREAT_HAMMER_HEAVY_ATTACK = new AttackGetter(DarkSouls.rl("great_hammer_heavy_attack"));
	public static final AttackArrayGetter GREAT_HAMMER_LIGHT_ATTACK = new AttackArrayGetter
			(
					DarkSouls.rl("great_hammer_light_attack_1"),
					DarkSouls.rl("great_hammer_light_attack_2")
			);
	public static final AttackGetter GREAT_HAMMER_DASH_ATTACK = new AttackGetter(DarkSouls.rl("great_hammer_dash_attack"));
	public static final AttackArrayGetter GREAT_HAMMER_TH_LIGHT_ATTACK = new AttackArrayGetter
			(
					DarkSouls.rl("great_hammer_th_la_1"),
					DarkSouls.rl("great_hammer_th_la_2")
			);
	public static final AttackGetter GREAT_HAMMER_TH_HEAVY_ATTACK = new AttackGetter(DarkSouls.rl("great_hammer_th_heavy_attack"));
	public static final AttackGetter GREAT_HAMMER_TH_DASH_ATTACK = new AttackGetter(DarkSouls.rl("great_hammer_th_dash_attack"));

	// Axe
	public static final AttackGetter AXE_HEAVY_ATTACK = new AttackGetter(DarkSouls.rl("axe_heavy_attack"));
	public static final AttackArrayGetter AXE_LIGHT_ATTACK = new AttackArrayGetter
			(
					DarkSouls.rl("axe_light_attack_1"),
					DarkSouls.rl("axe_light_attack_2")
			);
	public static final AttackGetter AXE_DASH_ATTACK = new AttackGetter(DarkSouls.rl("axe_dash_attack"));
	public static final AttackArrayGetter AXE_TH_LIGHT_ATTACK = new AttackArrayGetter
			(
					DarkSouls.rl("axe_th_la_1"),
					DarkSouls.rl("axe_th_la_2")
			);
	public static final AttackGetter AXE_TH_HEAVY_ATTACK = new AttackGetter(DarkSouls.rl("axe_th_heavy_attack"));
	public static final AttackGetter AXE_TH_DASH_ATTACK = new AttackGetter(DarkSouls.rl("axe_th_dash_attack"));

	// Hammer
	public static final AttackGetter HAMMER_DASH_ATTACK = new AttackGetter(DarkSouls.rl("hammer_dash_attack"));
	public static final AttackGetter HAMMER_HEAVY_ATTACK = new AttackGetter(DarkSouls.rl("hammer_heavy_attack"));
	public static final AttackGetter HAMMER_LIGHT_ATTACK = new AttackGetter(DarkSouls.rl("hammer_light_attack"));
	public static final AttackGetter HAMMER_TH_LIGHT_ATTACK = new AttackGetter(DarkSouls.rl("hammer_th_light_attack"));
	public static final AttackGetter HAMMER_TH_HEAVY_ATTACK = new AttackGetter(DarkSouls.rl("hammer_th_heavy_attack"));
	public static final AttackGetter HAMMER_TH_DASH_ATTACK = new AttackGetter(DarkSouls.rl("hammer_th_dash_attack"));

	// Fist
	public static final AttackArrayGetter FIST_LIGHT_ATTACK = new AttackArrayGetter
			(
					DarkSouls.rl("fist_light_attack_1"),
					DarkSouls.rl("fist_light_attack_2")
			);
	public static final AttackGetter FIST_DASH_ATTACK = new AttackGetter(DarkSouls.rl("fist_dash_attack"));
	public static final AttackGetter FIST_HEAVY_ATTACK = new AttackGetter(DarkSouls.rl("fist_heavy_attack"));

	// Shield
	public static final AttackGetter SHIELD_LIGHT_ATTACK = new AttackGetter(DarkSouls.rl("shield_light_attack"));
	public static final AttackArrayGetter SHIELD_HEAVY_ATTACK = new AttackArrayGetter
			(
					DarkSouls.rl("shield_ha_1"),
					DarkSouls.rl("shield_ha_2")
			);
	public static final AttackGetter SHIELD_DASH_ATTACK = new AttackGetter(DarkSouls.rl("shield_dash_attack"));
	public static final AttackGetter SHIELD_TH_LIGHT_ATTACK = new AttackGetter(DarkSouls.rl("shield_th_light_attack"));
	public static final AttackArrayGetter SHIELD_TH_HEAVY_ATTACK = new AttackArrayGetter
			(
					DarkSouls.rl("shield_th_ha_1"),
					DarkSouls.rl("shield_th_ha_2")
			);
	public static final AttackGetter SHIELD_TH_DASH_ATTACK = new AttackGetter(DarkSouls.rl("shield_th_dash_attack"));
	
	// Greatshield
	public static final AttackGetter GREATSHIELD_LIGHT_ATTACK = new AttackGetter(DarkSouls.rl("greatshield_light_attack"));
	public static final AttackGetter GREATSHIELD_HEAVY_ATTACK = new AttackGetter(DarkSouls.rl("greatshield_heavy_attack"));
	public static final AttackGetter GREATSHIELD_DASH_ATTACK = new AttackGetter(DarkSouls.rl("greatshield_dash_attack"));
	public static final AttackGetter GREATSHIELD_TH_LIGHT_ATTACK = new AttackGetter(DarkSouls.rl("greatshield_th_light_attack"));
	public static final AttackGetter GREATSHIELD_TH_HEAVY_ATTACK = new AttackGetter(DarkSouls.rl("greatshield_th_heavy_attack"));
	public static final AttackGetter GREATSHIELD_TH_DASH_ATTACK = new AttackGetter(DarkSouls.rl("greatshield_th_dash_attack"));
	public static final AttackGetter GREATSHIELD_BASH = new AttackGetter(DarkSouls.rl("greatshield_bash"));

	// Straight Sword
	public static final AttackArrayGetter STRAIGHT_SWORD_LIGHT_ATTACK = new AttackArrayGetter
			(		
					DarkSouls.rl("straight_sword_light_attack_1"),
					DarkSouls.rl("straight_sword_light_attack_2")
			);
	public static final AttackGetter STRAIGHT_SWORD_HEAVY_ATTACK = new AttackGetter(DarkSouls.rl("straight_sword_heavy_attack"));
	public static final AttackGetter STRAIGHT_SWORD_DASH_ATTACK = new AttackGetter(DarkSouls.rl("straight_sword_dash_attack"));
	public static final AttackArrayGetter STRAIGHT_SWORD_TH_LIGHT_ATTACK = new AttackArrayGetter
			(
					DarkSouls.rl("straight_sword_th_la_1"),
					DarkSouls.rl("straight_sword_th_la_2")
			);
	public static final AttackGetter STRAIGHT_SWORD_TH_HEAVY_ATTACK = new AttackGetter(DarkSouls.rl("straight_sword_th_heavy_attack"));
	public static final AttackGetter STRAIGHT_SWORD_TH_DASH_ATTACK = new AttackGetter(DarkSouls.rl("straight_sword_th_dash_attack"));

	// Hollow
	public static final Getter HOLLOW_IDLE = new Getter(DarkSouls.rl("hollow_idle"));
	public static final Getter HOLLOW_WALK = new Getter(DarkSouls.rl("hollow_walk"));
	public static final Getter HOLLOW_RUN = new Getter(DarkSouls.rl("hollow_run"));
	public static final Getter HOLLOW_DEFLECTED = new Getter(DarkSouls.rl("hollow_deflected"));
	public static final Getter HOLLOW_BREAKDOWN = new Getter(DarkSouls.rl("hollow_breakdown"));

	public static final AttackArrayGetter HOLLOW_LIGHT_ATTACKS = new AttackArrayGetter
			(
					DarkSouls.rl("hollow_light_attack_1"),
					DarkSouls.rl("hollow_light_attack_2"),
					DarkSouls.rl("hollow_light_attack_3")
			);
	public static final AttackGetter HOLLOW_BARRAGE = new AttackGetter(DarkSouls.rl("hollow_barrage"));
	public static final AttackGetter HOLLOW_OVERHEAD_SWING = new AttackGetter(DarkSouls.rl("hollow_overhead_swing"));
	public static final AttackGetter HOLLOW_JUMP_ATTACK = new AttackGetter(DarkSouls.rl("hollow_jump_attack"));

	// Hollow Lordran Warrior
	public static final Getter HOLLOW_LORDRAN_WARRIOR_WALK = new Getter(DarkSouls.rl("hollow_lordran_warrior_walk"));
	public static final Getter HOLLOW_LORDRAN_WARRIOR_RUN = new Getter(DarkSouls.rl("hollow_lordran_warrior_run"));

	public static final AttackArrayGetter HOLLOW_LORDRAN_WARRIOR_TH_LA = new AttackArrayGetter
			(
					DarkSouls.rl("hollow_lordran_warrior_th_la_1"),
					DarkSouls.rl("hollow_lordran_warrior_th_la_2")
			);
	public static final AttackGetter HOLLOW_LORDRAN_WARRIOR_DASH_ATTACK = new AttackGetter(DarkSouls.rl("hollow_lordran_warrior_dash_attack"));
	public static final AttackArrayGetter HOLLOW_LORDRAN_WARRIOR_AXE_LA = new AttackArrayGetter
			(
					DarkSouls.rl("hollow_lordran_warrior_axe_la_1"),
					DarkSouls.rl("hollow_lordran_warrior_axe_la_2")
			);
	public static final AttackArrayGetter HOLLOW_LORDRAN_WARRIOR_AXE_TH_LA = new AttackArrayGetter
			(
					DarkSouls.rl("hollow_lordran_warrior_axe_th_la_1"),
					DarkSouls.rl("hollow_lordran_warrior_axe_th_la_2")
			);

	// Hollow Lordran Soldier
	public static final Getter HOLLOW_LORDRAN_SOLDIER_WALK = new Getter(DarkSouls.rl("hollow_lordran_soldier_walk"));
	public static final Getter HOLLOW_LORDRAN_SOLDIER_RUN = new Getter(DarkSouls.rl("hollow_lordran_soldier_run"));
	public static final Getter HOLLOW_LORDRAN_SOLDIER_BLOCK = new Getter(DarkSouls.rl("hollow_lordran_soldier_block"));
	
	public static final AttackArrayGetter HOLLOW_LORDRAN_SOLDIER_SWORD_LA = new AttackArrayGetter
			(
					DarkSouls.rl("hollow_lordran_soldier_sword_la_1"),
					DarkSouls.rl("hollow_lordran_soldier_sword_la_2"),
					DarkSouls.rl("hollow_lordran_soldier_sword_la_3")
			);
	public static final AttackGetter HOLLOW_LORDRAN_SOLDIER_SWORD_DA = new AttackGetter(DarkSouls.rl("hollow_lordran_soldier_sword_da"));
	public static final AttackGetter HOLLOW_LORDRAN_SOLDIER_SWORD_HEAVY_THRUST = new AttackGetter(DarkSouls.rl("hollow_lordran_soldier_sword_heavy_thrust"));
	public static final AttackGetter HOLLOW_LORDRAN_SOLDIER_SWORD_THRUST_COMBO = new AttackGetter(DarkSouls.rl("hollow_lordran_soldier_sword_thrust_combo"));

	public static final AttackArrayGetter HOLLOW_LORDRAN_SOLDIER_SPEAR_SWINGS = new AttackArrayGetter
			(
					DarkSouls.rl("hollow_lordran_soldier_spear_swing_1"),
					DarkSouls.rl("hollow_lordran_soldier_spear_swing_2"),
					DarkSouls.rl("hollow_lordran_soldier_spear_swing_3"),
					DarkSouls.rl("hollow_lordran_soldier_spear_swing_4")
			);
	public static final AttackArrayGetter HOLLOW_LORDRAN_SOLDIER_SPEAR_THRUSTS = new AttackArrayGetter
			(
					DarkSouls.rl("hollow_lordran_soldier_spear_thrust_1"),
					DarkSouls.rl("hollow_lordran_soldier_spear_thrust_2"),
					DarkSouls.rl("hollow_lordran_soldier_spear_thrust_3")
			);
	public static final AttackGetter HOLLOW_LORDRAN_SOLDIER_SHIELD_BASH = new AttackGetter(DarkSouls.rl("hollow_lordran_soldier_shield_bash"));
	
	// Falconer
	public static final Getter FALCONER_IDLE = new Getter(DarkSouls.rl("falconer_idle"));
	public static final Getter FALCONER_WALK = new Getter(DarkSouls.rl("falconer_walk"));
	public static final Getter FALCONER_RUN = new Getter(DarkSouls.rl("falconer_run"));
	
	public static final AttackArrayGetter FALCONER_LIGHT_ATTACKS = new AttackArrayGetter
			(
					DarkSouls.rl("falconer_light_attack_1"),
					DarkSouls.rl("falconer_light_attack_2"),
					DarkSouls.rl("falconer_light_attack_3")
			);
	
	// Balder Knight
	public static final Getter BALDER_KNIGHT_IDLE = new Getter(DarkSouls.rl("balder_knight_idle"));
	public static final Getter BALDER_KNIGHT_WALK = new Getter(DarkSouls.rl("balder_knight_walk"));
	public static final Getter BALDER_KNIGHT_RUN = new Getter(DarkSouls.rl("balder_knight_run"));
	public static final Getter BALDER_KNIGHT_BLOCK = new Getter(DarkSouls.rl("balder_knight_block"));
	public static final AdaptableGetter BALDER_KNIGHT_RAPIER_BLOCK = new AdaptableGetter(DarkSouls.rl("balder_knight_rapier_block"));
	
	public static final ParryGetter BALDER_KNIGHT_RAPIER_PARRY = new ParryGetter(DarkSouls.rl("balder_knight_rapier_parry"));
	
	public static final AttackArrayGetter BALDER_KNIGHT_SIDE_SWORD_LA = new AttackArrayGetter
				(
					DarkSouls.rl("balder_knight_side_sword_la_1"),
					DarkSouls.rl("balder_knight_side_sword_la_2"),
					DarkSouls.rl("balder_knight_side_sword_la_3")
				);
	public static final AttackGetter BALDER_KNIGHT_SIDE_SWORD_HA = new AttackGetter(DarkSouls.rl("balder_knight_side_sword_ha"));
	public static final AttackGetter BALDER_KNIGHT_SIDE_SWORD_DA = new AttackGetter(DarkSouls.rl("balder_knight_side_sword_da"));
	public static final AttackGetter BALDER_KNIGHT_SIDE_SWORD_FAST_LA = new AttackGetter(DarkSouls.rl("balder_knight_side_sword_fast_la"));
	public static final AttackGetter BALDER_KNIGHT_SHIELD_HA = new AttackGetter(DarkSouls.rl("balder_knight_shield_ha"));
	
	public static final AttackArrayGetter BALDER_KNIGHT_RAPIER_LA = new AttackArrayGetter
			(
					DarkSouls.rl("balder_knight_rapier_la_1"),
					DarkSouls.rl("balder_knight_rapier_la_2"),
					DarkSouls.rl("balder_knight_rapier_la_3")
			);
	public static final AttackGetter BALDER_KNIGHT_RAPIER_HA = new AttackGetter(DarkSouls.rl("balder_knight_rapier_ha"));
	public static final AttackGetter BALDER_KNIGHT_RAPIER_DA = new AttackGetter(DarkSouls.rl("balder_knight_rapier_da"));
	
	// Berenike Knight
	public static final Getter BERENIKE_KNIGHT_IDLE = new Getter(DarkSouls.rl("berenike_knight_idle"));
	public static final Getter BERENIKE_KNIGHT_WALK = new Getter(DarkSouls.rl("berenike_knight_walk"));
	public static final Getter BERENIKE_KNIGHT_RUN = new Getter(DarkSouls.rl("berenike_knight_run"));
	public static final Getter BERENIKE_KNIGHT_BLOCK = new Getter(DarkSouls.rl("berenike_knight_block"));
	
	public static final AttackArrayGetter BERENIKE_KNIGHT_SWORD_LA = new AttackArrayGetter
				(
					DarkSouls.rl("berenike_knight_sword_la_1"),
					DarkSouls.rl("berenike_knight_sword_la_2")
				);
	public static final AttackArrayGetter BERENIKE_KNIGHT_SWORD_HA = new AttackArrayGetter
				(
					DarkSouls.rl("berenike_knight_sword_ha_1"),
					DarkSouls.rl("berenike_knight_sword_ha_2")
				);
	public static final AttackGetter BERENIKE_KNIGHT_SWORD_DA = new AttackGetter(DarkSouls.rl("berenike_knight_sword_da"));
	
	public static final AttackArrayGetter BERENIKE_KNIGHT_MACE_LA = new AttackArrayGetter
				(
					DarkSouls.rl("berenike_knight_mace_la_1"),
					DarkSouls.rl("berenike_knight_mace_la_2")
				);
	public static final AttackGetter BERENIKE_KNIGHT_MACE_HA = new AttackGetter(DarkSouls.rl("berenike_knight_mace_ha"));
	
	public static final AttackGetter BERENIKE_KNIGHT_KICK = new AttackGetter(DarkSouls.rl("berenike_knight_kick"));
	
	// Black Knight
	public static final Getter BLACK_KNIGHT_IDLE = new Getter(DarkSouls.rl("black_knight_idle"));
	public static final Getter BLACK_KNIGHT_WALK = new Getter(DarkSouls.rl("black_knight_walk"));
	public static final Getter BLACK_KNIGHT_RUN = new Getter(DarkSouls.rl("black_knight_run"));
	public static final Getter BLACK_KNIGHT_BLOCK = new Getter(DarkSouls.rl("black_knight_block"));
	public static final DeathGetter BLACK_KNIGHT_DEATH = new DeathGetter(DarkSouls.rl("black_knight_death"));
	
	public static final AttackArrayGetter BLACK_KNIGHT_SWORD_LA_LONG = new AttackArrayGetter
			(
					DarkSouls.rl("black_knight_sword_la_long_1"),
					DarkSouls.rl("black_knight_sword_la_long_2"),
					DarkSouls.rl("black_knight_sword_la_long_3"),
					DarkSouls.rl("black_knight_sword_la_long_4")
			);
	public static final AttackArrayGetter BLACK_KNIGHT_SWORD_LA_SHORT = new AttackArrayGetter
			(
					DarkSouls.rl("black_knight_sword_la_short_1"),
					DarkSouls.rl("black_knight_sword_la_short_2"),
					DarkSouls.rl("black_knight_sword_la_short_5")
			);
	public static final AttackGetter BLACK_KNIGHT_SWORD_HA = new AttackGetter(DarkSouls.rl("black_knight_sword_ha"));
	public static final AttackGetter BLACK_KNIGHT_SWORD_DA = new AttackGetter(DarkSouls.rl("black_knight_sword_da"));
	public static final AttackGetter BLACK_KNIGHT_SHIELD_ATTACK = new AttackGetter(DarkSouls.rl("black_knight_shield_attack"));

	// Stray Demon
	public static final Getter STRAY_DEMON_IDLE = new Getter(DarkSouls.rl("stray_demon_idle"));
	public static final Getter STRAY_DEMON_WALK = new Getter(DarkSouls.rl("stray_demon_walk"));
	public static final DeathGetter STRAY_DEMON_DEATH = new DeathGetter(DarkSouls.rl("stray_demon_death"));

	public static final AttackArrayGetter STRAY_DEMON_HAMMER_LIGHT_ATTACK = new AttackArrayGetter
			(
					DarkSouls.rl("stray_demon_hammer_la_1"),
					DarkSouls.rl("stray_demon_hammer_la_2")
			);
	public static final AttackArrayGetter STRAY_DEMON_HAMMER_ALT_LIGHT_ATTACK = new AttackArrayGetter
			(
					DarkSouls.rl("stray_demon_hammer_la_alt_1"),
					DarkSouls.rl("stray_demon_hammer_la_alt_2")
			);
	public static final AttackArrayGetter STRAY_DEMON_HAMMER_HEAVY_ATTACK = new AttackArrayGetter
			(
					DarkSouls.rl("stray_demon_hammer_ha_1"),
					DarkSouls.rl("stray_demon_hammer_ha_2")
			);
	public static final AttackGetter STRAY_DEMON_HAMMER_DRIVE = new AttackGetter(DarkSouls.rl("stray_demon_hammer_drive"));
	public static final AttackGetter STRAY_DEMON_HAMMER_DASH_ATTACK = new AttackGetter(DarkSouls.rl("stray_demon_hammer_dash_attack"));
	public static final AttackGetter STRAY_DEMON_GROUND_POUND = new AttackGetter(DarkSouls.rl("stray_demon_ground_pound"));
	
	public static final Getter TAURUS_DEMON_IDLE = new Getter(DarkSouls.rl("taurus_demon_idle"));
	
	// Anastacia of Astora
	public static final Getter ANASTACIA_IDLE = new Getter(DarkSouls.rl("anastacia_idle"));
	
	// Bell Gargoyle
	public static final Getter BELL_GARGOYLE_IDLE = new Getter(DarkSouls.rl("bell_gargoyle_idle"));
	
	public static StaticAnimation createSupplier(BiFunction<LivingCap<?>, LayerPart, StaticAnimation> biFunction)
	{
		return new SupplierAnimation(biFunction);
	}
	
	public static class Getter extends AbstractGetter<StaticAnimation>
	{
		public Getter(ResourceLocation id)
		{
			super(id);
		}

		@Override
		public StaticAnimation get()
		{
			return AnimationManager.getAnimation(this.getId());
		}
	}
	
	public static class DeathGetter extends AbstractGetter<DeathAnimation>
	{
		public DeathGetter(ResourceLocation id)
		{
			super(id);
		}

		@Override
		public DeathAnimation get()
		{
			return AnimationManager.getDeathAnimation(this.getId());
		}
	}
	
	public static class AttackGetter extends AbstractGetter<AttackAnimation>
	{
		public AttackGetter(ResourceLocation id)
		{
			super(id);
		}

		@Override
		public AttackAnimation get()
		{
			return AnimationManager.getAttackAnimation(this.getId());
		}
	}
	
	public static class AttackArrayGetter extends AbstractArrayGetter<AttackAnimation[]>
	{
		public AttackArrayGetter(ResourceLocation... ids)
		{
			super(ids);
		}

		@Override
		public AttackAnimation[] get()
		{
			AttackAnimation[] attacks = new AttackAnimation[this.getIds().length];
			for (int i = 0; i < attacks.length; i++)
			{
				attacks[i] = AnimationManager.getAttackAnimation(this.getIds()[i]);
			}
			return attacks;
		}
		
		public List<AttackAnimation> getAsList()
		{
			List<AttackAnimation> attacks = new ArrayList<>();
			for (int i = 0; i < attacks.size(); i++)
			{
				attacks.add(AnimationManager.getAttackAnimation(this.getIds()[i]));
			}
			return attacks;
		}
	}
	
	public static class MirrorGetter extends AbstractGetter<MirrorAnimation>
	{
		public MirrorGetter(ResourceLocation id)
		{
			super(id);
		}

		@Override
		public MirrorAnimation get()
		{
			return AnimationManager.getMirrorAnimation(this.getId());
		}
	}
	
	public static class ParryGetter extends AbstractGetter<ParryAnimation>
	{
		public ParryGetter(ResourceLocation id)
		{
			super(id);
		}

		@Override
		public ParryAnimation get()
		{
			return AnimationManager.getParryAnimation(this.getId());
		}
	}
	
	public static class AdaptableGetter extends AbstractGetter<AdaptableAnimation>
	{
		public AdaptableGetter(ResourceLocation id)
		{
			super(id);
		}

		@Override
		public AdaptableAnimation get()
		{
			return AnimationManager.getAdaptableAnimation(this.getId());
		}
	}
}