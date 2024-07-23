package com.skullmangames.darksouls.core.init;

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

public final class Animations
{
	public static StaticAnimation DUMMY_ANIMATION;

	public static StaticAnimation BIPED_IDLE;
	public static StaticAnimation BIPED_WALK;
	public static StaticAnimation BIPED_RUN;
	public static StaticAnimation BIPED_SNEAK;
	
	public static StaticAnimation BIPED_IDLE_TH;
	public static StaticAnimation BIPED_WALK_TH;
	public static StaticAnimation BIPED_RUN_TH;
	
	public static StaticAnimation BIPED_IDLE_TH_BIG_WEAPON;
	
	public static StaticAnimation BIPED_IDLE_TH_SPEAR;
	
	public static StaticAnimation BIPED_IDLE_TH_SHIELD;
	
	public static StaticAnimation BIPED_CHANGE_ITEM_RIGHT;
	public static StaticAnimation BIPED_CHANGE_ITEM_LEFT;
	
	public static StaticAnimation BIPED_SWIM;
	public static StaticAnimation BIPED_FLOAT;
	public static StaticAnimation BIPED_KNEEL;
	public static StaticAnimation BIPED_FALL;
	
	public static DeathAnimation BIPED_DEATH;
	public static DeathAnimation BIPED_DEATH_SMASH;
	public static DeathAnimation BIPED_DEATH_FLY_FRONT;
	public static DeathAnimation BIPED_DEATH_FLY_BACK;
	public static DeathAnimation BIPED_DEATH_FLY_LEFT;
	public static DeathAnimation BIPED_DEATH_FLY_RIGHT;
	public static DeathAnimation BIPED_DEATH_BACKSTAB;
	public static DeathAnimation BIPED_DEATH_PUNISH;
	
	public static StaticAnimation BIPED_DIG;
	public static StaticAnimation BIPED_TOUCH_BONFIRE;

	public static StaticAnimation BIPED_EAT;
	public static StaticAnimation BIPED_DRINK;
	public static StaticAnimation BIPED_CONSUME_SOUL;
	
	public static StaticAnimation BIPED_THROW;

	public static StaticAnimation BIPED_BLOCK_HORIZONTAL;
	public static StaticAnimation BIPED_BLOCK_VERTICAL;
	public static StaticAnimation BIPED_BLOCK_GREATSHIELD;
	public static StaticAnimation BIPED_BLOCK_TH_SWORD;
	public static StaticAnimation BIPED_BLOCK_TH_VERTICAL;
	public static StaticAnimation BIPED_BLOCK_TH_HORIZONTAL;
	public static StaticAnimation BIPED_BLOCK_TH_GREATSHIELD;
	
	public static StaticAnimation BIPED_HIT_BLOCKED_LEFT;
	public static StaticAnimation BIPED_HIT_BLOCKED_RIGHT;
	public static StaticAnimation BIPED_HIT_BLOCKED_VERTICAL_LEFT;
	public static StaticAnimation BIPED_HIT_BLOCKED_VERTICAL_RIGHT;
	public static StaticAnimation BIPED_HIT_BLOCKED_TH_SWORD;
	
	public static StaticAnimation BIPED_HIT_BLOCKED_FLY_LEFT;
	public static StaticAnimation BIPED_HIT_BLOCKED_FLY_RIGHT;
	public static StaticAnimation BIPED_HIT_BLOCKED_VERTICAL_FLY_LEFT;
	public static StaticAnimation BIPED_HIT_BLOCKED_VERTICAL_FLY_RIGHT;
	public static StaticAnimation BIPED_HIT_BLOCKED_TH_SWORD_FLY;
	
	public static StaticAnimation BIPED_DISARMED_LEFT;
	public static StaticAnimation BIPED_DISARMED_RIGHT;
	
	public static StaticAnimation BIPED_HORSEBACK_IDLE;
	
	public static StaticAnimation BIPED_IDLE_CROSSBOW;
	public static StaticAnimation BIPED_WALK_CROSSBOW;

	public static StaticAnimation BIPED_CROSSBOW_AIM;
	public static StaticAnimation BIPED_CROSSBOW_SHOT;

	public static StaticAnimation BIPED_CROSSBOW_RELOAD;

	public static StaticAnimation BIPED_BOW_AIM;
	public static StaticAnimation BIPED_BOW_REBOUND;

	public static StaticAnimation BIPED_SPEER_AIM;
	public static StaticAnimation BIPED_SPEER_REBOUND;

	public static StaticAnimation BIPED_HIT_LIGHT_FRONT;
	public static StaticAnimation BIPED_HIT_LIGHT_LEFT;
	public static StaticAnimation BIPED_HIT_LIGHT_RIGHT;
	public static StaticAnimation BIPED_HIT_LIGHT_BACK;
	public static StaticAnimation BIPED_HIT_HEAVY_FRONT;
	public static StaticAnimation BIPED_HIT_HEAVY_BACK;
	public static StaticAnimation BIPED_HIT_HEAVY_LEFT;
	public static StaticAnimation BIPED_HIT_HEAVY_RIGHT;
	
	public static StaticAnimation BIPED_HORSEBACK_HIT_LIGHT_FRONT;
	public static StaticAnimation BIPED_HORSEBACK_HIT_LIGHT_LEFT;
	public static StaticAnimation BIPED_HORSEBACK_HIT_LIGHT_RIGHT;
	public static StaticAnimation BIPED_HORSEBACK_HIT_LIGHT_BACK;
	public static StaticAnimation BIPED_HORSEBACK_HIT_HEAVY_FRONT;
	public static StaticAnimation BIPED_HORSEBACK_HIT_HEAVY_BACK;
	public static StaticAnimation BIPED_HORSEBACK_HIT_HEAVY_LEFT;
	public static StaticAnimation BIPED_HORSEBACK_HIT_HEAVY_RIGHT;
	
	public static StaticAnimation BIPED_HIT_SMASH;
	public static StaticAnimation BIPED_HIT_FLY_FRONT;
	public static StaticAnimation BIPED_HIT_FLY_BACK;
	public static StaticAnimation BIPED_HIT_FLY_LEFT;
	public static StaticAnimation BIPED_HIT_FLY_RIGHT;
	public static StaticAnimation BIPED_HIT_LAND_HEAVY;
	
	public static StaticAnimation BIPED_HIT_BACKSTAB;
	public static StaticAnimation BIPED_HIT_PUNISH;
	
	public static StaticAnimation BIPED_ROLL;
	public static StaticAnimation BIPED_FAT_ROLL;
	public static StaticAnimation BIPED_ROLL_TOO_FAT;
	public static StaticAnimation BIPED_ROLL_BACK;
	public static StaticAnimation BIPED_ROLL_LEFT;
	public static StaticAnimation BIPED_ROLL_RIGHT;
	public static StaticAnimation BIPED_JUMP_BACK;
	
	// Miracle
	public static StaticAnimation BIPED_CAST_MIRACLE_HEAL;
	
	public static StaticAnimation BIPED_CAST_MIRACLE_HEAL_AID;
	
	public static StaticAnimation BIPED_CAST_MIRACLE_HOMEWARD;
	
	public static StaticAnimation BIPED_CAST_MIRACLE_FORCE;

	public static StaticAnimation BIPED_CAST_MIRACLE_LIGHTNING_SPEAR;
	public static StaticAnimation HORSEBACK_CAST_MIRACLE_LIGHTNING_SPEAR;

	public static StaticAnimation BIPED_CAST_MIRACLE_GREAT_LIGHTNING_SPEAR;
	public static StaticAnimation HORSEBACK_CAST_MIRACLE_GREAT_LIGHTNING_SPEAR;
	
	// Big Weapon
	public static MirrorAnimation BIPED_HOLDING_BIG_WEAPON;
	
	// Horseback Attacks
	public static AttackAnimation[] HORSEBACK_LIGHT_ATTACK;
	
	// Parries
	public static ParryAnimation SHIELD_PARRY_LEFT;
	public static ParryAnimation SHIELD_PARRY_RIGHT;
	public static ParryAnimation BUCKLER_PARRY_LEFT;
	public static ParryAnimation BUCKLER_PARRY_RIGHT;
	
	// Backstabs
	public static AttackAnimation BACKSTAB_THRUST;
	public static AttackAnimation BACKSTAB_STRIKE;
	
	// Punishes
	public static AttackAnimation PUNISH_THRUST;
	public static AttackAnimation PUNISH_STRIKE;
	
	// Thrusting Sword
	public static AttackAnimation THRUSTING_SWORD_LIGHT_ATTACK;
	public static AttackAnimation[] THRUSTING_SWORD_HEAVY_ATTACK;
	public static AttackAnimation THRUSTING_SWORD_DASH_ATTACK;
	public static AttackAnimation THRUSTING_SWORD_TH_LIGHT_ATTACK;
	public static AttackAnimation[] THRUSTING_SWORD_TH_HEAVY_ATTACK;
	public static AttackAnimation THRUSTING_SWORD_TH_DASH_ATTACK;
	
	// Greatsword
	public static AttackAnimation[] GREATSWORD_LIGHT_ATTACK;
	public static AttackAnimation GREATSWORD_THRUST;
	public static AttackAnimation GREATSWORD_DASH_ATTACK;
	public static AttackAnimation GREATSWORD_UPWARD_SLASH;
	public static AttackAnimation GREATSWORD_STYLISH_THRUST;
	public static AttackAnimation[] GREATSWORD_TH_LIGHT_ATTACK;
	public static AttackAnimation[] GREATSWORD_TH_THRUST_ATTACK;
	public static AttackAnimation GREATSWORD_TH_DASH_ATTACK;

	// Ultra Greatsword
	public static AttackAnimation[] ULTRA_GREATSWORD_LIGHT_ATTACK;
	public static AttackAnimation[] ULTRA_GREATSWORD_HEAVY_ATTACK;
	public static AttackAnimation ULTRA_GREATSWORD_DASH_ATTACK;
	public static AttackAnimation[] ULTRA_GREATSWORD_TH_LIGHT_ATTACK;
	public static AttackAnimation[] ULTRA_GREATSWORD_TH_HEAVY_ATTACK;
	public static AttackAnimation ULTRA_GREATSWORD_TH_DASH_ATTACK;
	
	// Greataxe
	public static AttackAnimation[] GREATAXE_LIGHT_ATTACK;
	public static AttackAnimation GREATAXE_HEAVY_ATTACK;
	public static AttackAnimation GREATAXE_DASH_ATTACK;
	public static AttackAnimation[] GREATAXE_TH_LIGHT_ATTACK;
	public static AttackAnimation GREATAXE_TH_HEAVY_ATTACK;
	public static AttackAnimation GREATAXE_TH_DASH_ATTACK;

	// Spear
	public static AttackAnimation SPEAR_DASH_ATTACK;
	public static AttackAnimation SPEAR_HEAVY_ATTACK;
	public static AttackAnimation SPEAR_LIGHT_ATTACK;
	public static AttackAnimation SPEAR_LIGHT_BLOCKING_ATTACK;
	public static AttackAnimation SPEAR_TH_LIGHT_ATTACK;
	public static AttackAnimation SPEAR_TH_HEAVY_ATTACK;
	public static AttackAnimation SPEAR_TH_DASH_ATTACK;

	// Dagger
	public static AttackAnimation DAGGER_HEAVY_ATTACK;
	public static AttackAnimation[] DAGGER_LIGHT_ATTACK;

	// Great Hammer
	public static AttackAnimation GREAT_HAMMER_HEAVY_ATTACK;
	public static AttackAnimation[] GREAT_HAMMER_LIGHT_ATTACK;
	public static AttackAnimation GREAT_HAMMER_DASH_ATTACK;
	public static AttackAnimation[] GREAT_HAMMER_TH_LIGHT_ATTACK;
	public static AttackAnimation GREAT_HAMMER_TH_HEAVY_ATTACK;
	public static AttackAnimation GREAT_HAMMER_TH_DASH_ATTACK;

	// Axe
	public static AttackAnimation AXE_HEAVY_ATTACK;
	public static AttackAnimation[] AXE_LIGHT_ATTACK;
	public static AttackAnimation AXE_DASH_ATTACK;
	public static AttackAnimation[] AXE_TH_LIGHT_ATTACK;
	public static AttackAnimation AXE_TH_HEAVY_ATTACK;
	public static AttackAnimation AXE_TH_DASH_ATTACK;

	// Hammer
	public static AttackAnimation HAMMER_DASH_ATTACK;
	public static AttackAnimation HAMMER_HEAVY_ATTACK;
	public static AttackAnimation HAMMER_LIGHT_ATTACK;
	public static AttackAnimation HAMMER_TH_LIGHT_ATTACK;
	public static AttackAnimation HAMMER_TH_HEAVY_ATTACK;
	public static AttackAnimation HAMMER_TH_DASH_ATTACK;

	// Fist
	public static AttackAnimation[] FIST_LIGHT_ATTACK;
	public static AttackAnimation FIST_DASH_ATTACK;
	public static AttackAnimation FIST_HEAVY_ATTACK;

	// Shield
	public static AttackAnimation SHIELD_LIGHT_ATTACK;
	public static AttackAnimation[] SHIELD_HEAVY_ATTACK;
	public static AttackAnimation SHIELD_DASH_ATTACK;
	public static AttackAnimation SHIELD_TH_LIGHT_ATTACK;
	public static AttackAnimation[] SHIELD_TH_HEAVY_ATTACK;
	public static AttackAnimation SHIELD_TH_DASH_ATTACK;
	
	// Greatshield
	public static AttackAnimation GREATSHIELD_LIGHT_ATTACK;
	public static AttackAnimation GREATSHIELD_HEAVY_ATTACK;
	public static AttackAnimation GREATSHIELD_DASH_ATTACK;
	public static AttackAnimation GREATSHIELD_TH_LIGHT_ATTACK;
	public static AttackAnimation GREATSHIELD_TH_HEAVY_ATTACK;
	public static AttackAnimation GREATSHIELD_TH_DASH_ATTACK;
	public static AttackAnimation GREATSHIELD_BASH;

	// Straight Sword
	public static AttackAnimation[] STRAIGHT_SWORD_LIGHT_ATTACK;
	public static AttackAnimation STRAIGHT_SWORD_HEAVY_ATTACK;
	public static AttackAnimation STRAIGHT_SWORD_DASH_ATTACK;
	public static AttackAnimation[] STRAIGHT_SWORD_TH_LIGHT_ATTACK;
	public static AttackAnimation STRAIGHT_SWORD_TH_HEAVY_ATTACK;
	public static AttackAnimation STRAIGHT_SWORD_TH_DASH_ATTACK;

	// Hollow
	public static StaticAnimation HOLLOW_IDLE;
	public static StaticAnimation HOLLOW_WALK;
	public static StaticAnimation HOLLOW_RUN;
	public static StaticAnimation HOLLOW_DEFLECTED;
	public static StaticAnimation HOLLOW_BREAKDOWN;

	public static AttackAnimation[] HOLLOW_LIGHT_ATTACKS;
	public static AttackAnimation HOLLOW_BARRAGE;
	public static AttackAnimation HOLLOW_OVERHEAD_SWING;
	public static AttackAnimation HOLLOW_JUMP_ATTACK;

	// Hollow Lordran Warrior
	public static StaticAnimation HOLLOW_LORDRAN_WARRIOR_WALK;
	public static StaticAnimation HOLLOW_LORDRAN_WARRIOR_RUN;

	public static AttackAnimation[] HOLLOW_LORDRAN_WARRIOR_TH_LA;
	public static AttackAnimation HOLLOW_LORDRAN_WARRIOR_DASH_ATTACK;
	public static AttackAnimation[] HOLLOW_LORDRAN_WARRIOR_AXE_LA;
	public static AttackAnimation[] HOLLOW_LORDRAN_WARRIOR_AXE_TH_LA;

	// Hollow Lordran Soldier
	public static StaticAnimation HOLLOW_LORDRAN_SOLDIER_WALK;
	public static StaticAnimation HOLLOW_LORDRAN_SOLDIER_RUN;
	public static StaticAnimation HOLLOW_LORDRAN_SOLDIER_BLOCK;
	
	public static AttackAnimation[] HOLLOW_LORDRAN_SOLDIER_SWORD_LA;
	public static AttackAnimation HOLLOW_LORDRAN_SOLDIER_SWORD_DA;
	public static AttackAnimation HOLLOW_LORDRAN_SOLDIER_SWORD_HEAVY_THRUST;
	public static AttackAnimation HOLLOW_LORDRAN_SOLDIER_SWORD_THRUST_COMBO;

	public static AttackAnimation[] HOLLOW_LORDRAN_SOLDIER_SPEAR_SWINGS;
	public static AttackAnimation[] HOLLOW_LORDRAN_SOLDIER_SPEAR_THRUSTS;
	public static AttackAnimation HOLLOW_LORDRAN_SOLDIER_SHIELD_BASH;
	
	// Falconer
	public static StaticAnimation FALCONER_IDLE;
	public static StaticAnimation FALCONER_WALK;
	public static StaticAnimation FALCONER_RUN;
	
	public static AttackAnimation[] FALCONER_LIGHT_ATTACKS;
	
	// Balder Knight
	public static StaticAnimation BALDER_KNIGHT_IDLE;
	public static StaticAnimation BALDER_KNIGHT_WALK;
	public static StaticAnimation BALDER_KNIGHT_RUN;
	public static StaticAnimation BALDER_KNIGHT_BLOCK;
	public static AdaptableAnimation BALDER_KNIGHT_RAPIER_BLOCK;
	
	public static ParryAnimation BALDER_KNIGHT_RAPIER_PARRY;
	
	public static AttackAnimation[] BALDER_KNIGHT_SIDE_SWORD_LA;
	public static AttackAnimation BALDER_KNIGHT_SIDE_SWORD_HA;
	public static AttackAnimation BALDER_KNIGHT_SIDE_SWORD_DA;
	public static AttackAnimation BALDER_KNIGHT_SIDE_SWORD_FAST_LA;
	public static AttackAnimation BALDER_KNIGHT_SHIELD_HA;
	
	public static AttackAnimation[] BALDER_KNIGHT_RAPIER_LA;
	public static AttackAnimation BALDER_KNIGHT_RAPIER_HA;
	public static AttackAnimation BALDER_KNIGHT_RAPIER_DA;
	
	// Berenike Knight
	public static StaticAnimation BERENIKE_KNIGHT_IDLE;
	public static StaticAnimation BERENIKE_KNIGHT_WALK;
	public static StaticAnimation BERENIKE_KNIGHT_RUN;
	public static StaticAnimation BERENIKE_KNIGHT_BLOCK;
	
	public static AttackAnimation[] BERENIKE_KNIGHT_SWORD_LA;
	public static AttackAnimation[] BERENIKE_KNIGHT_SWORD_HA;
	public static AttackAnimation BERENIKE_KNIGHT_SWORD_DA;
	
	public static AttackAnimation[] BERENIKE_KNIGHT_MACE_LA;
	public static AttackAnimation BERENIKE_KNIGHT_MACE_HA;
	
	public static AttackAnimation BERENIKE_KNIGHT_KICK;
	
	// Black Knight
	public static StaticAnimation BLACK_KNIGHT_IDLE;
	public static StaticAnimation BLACK_KNIGHT_WALK;
	public static StaticAnimation BLACK_KNIGHT_RUN;
	public static StaticAnimation BLACK_KNIGHT_BLOCK;
	public static DeathAnimation BLACK_KNIGHT_DEATH;
	
	public static AttackAnimation[] BLACK_KNIGHT_SWORD_LA_LONG;
	public static AttackAnimation[] BLACK_KNIGHT_SWORD_LA_SHORT;
	public static AttackAnimation BLACK_KNIGHT_SWORD_HA;
	public static AttackAnimation BLACK_KNIGHT_SWORD_DA;
	public static AttackAnimation BLACK_KNIGHT_SHIELD_ATTACK;

	// Stray Demon
	public static StaticAnimation STRAY_DEMON_IDLE;
	public static StaticAnimation STRAY_DEMON_WALK;
	public static DeathAnimation STRAY_DEMON_DEATH;

	public static AttackAnimation[] STRAY_DEMON_HAMMER_LIGHT_ATTACK;
	public static AttackAnimation[] STRAY_DEMON_HAMMER_ALT_LIGHT_ATTACK;
	public static AttackAnimation[] STRAY_DEMON_HAMMER_HEAVY_ATTACK;
	public static AttackAnimation STRAY_DEMON_HAMMER_DRIVE;
	public static AttackAnimation STRAY_DEMON_HAMMER_DASH_ATTACK;
	public static AttackAnimation STRAY_DEMON_GROUND_POUND;
	
	public static StaticAnimation TAURUS_DEMON_IDLE;
	
	// Anastacia of Astora
	public static StaticAnimation ANASTACIA_IDLE;
	
	// Bell Gargoyle
	public static StaticAnimation BELL_GARGOYLE_IDLE;
	
	public static void init()
	{
		DUMMY_ANIMATION = new StaticAnimation();

		BIPED_IDLE = AnimationManager.getAnimation(DarkSouls.rl("biped_idle"));
		BIPED_WALK = AnimationManager.getAnimation(DarkSouls.rl("biped_walk"));
		BIPED_RUN = AnimationManager.getAnimation(DarkSouls.rl("biped_run"));
		BIPED_SNEAK = AnimationManager.getAnimation(DarkSouls.rl("biped_sneak"));
		
		BIPED_IDLE_TH = AnimationManager.getAnimation(DarkSouls.rl("biped_idle_th"));
		BIPED_WALK_TH = AnimationManager.getAnimation(DarkSouls.rl("biped_walk_th"));
		BIPED_RUN_TH = AnimationManager.getAnimation(DarkSouls.rl("biped_run_th"));
		
		BIPED_IDLE_TH_BIG_WEAPON = AnimationManager.getAnimation(DarkSouls.rl("biped_idle_th_big_weapon"));
		
		BIPED_IDLE_TH_SPEAR = AnimationManager.getAnimation(DarkSouls.rl("biped_idle_th_spear"));
		
		BIPED_IDLE_TH_SHIELD = AnimationManager.getAnimation(DarkSouls.rl("biped_idle_th_shield"));
		
		BIPED_CHANGE_ITEM_RIGHT = AnimationManager.getAnimation(DarkSouls.rl("biped_change_item_right"));
		
		BIPED_CHANGE_ITEM_LEFT = AnimationManager.getAnimation(DarkSouls.rl("biped_change_item_left"));
		
		BIPED_SWIM = AnimationManager.getAnimation(DarkSouls.rl("biped_swim"));
		BIPED_FLOAT = AnimationManager.getAnimation(DarkSouls.rl("biped_float"));
		BIPED_KNEEL = AnimationManager.getAnimation(DarkSouls.rl("biped_kneel"));
		BIPED_FALL = AnimationManager.getAnimation(DarkSouls.rl("biped_fall"));
		
		BIPED_DEATH = AnimationManager.getDeathAnimation(DarkSouls.rl("biped_death"));
		
		BIPED_DEATH_SMASH = AnimationManager.getDeathAnimation(DarkSouls.rl("biped_death_smash"));
		BIPED_DEATH_FLY_FRONT = AnimationManager.getDeathAnimation(DarkSouls.rl("biped_death_fly_front"));
		BIPED_DEATH_FLY_BACK = AnimationManager.getDeathAnimation(DarkSouls.rl("biped_death_fly_back"));
		BIPED_DEATH_FLY_LEFT = AnimationManager.getDeathAnimation(DarkSouls.rl("biped_death_fly_left"));
		BIPED_DEATH_FLY_RIGHT = AnimationManager.getDeathAnimation(DarkSouls.rl("biped_death_fly_right"));
		BIPED_DEATH_BACKSTAB = AnimationManager.getDeathAnimation(DarkSouls.rl("biped_death_backstab"));
		BIPED_DEATH_PUNISH = AnimationManager.getDeathAnimation(DarkSouls.rl("biped_death_punish"));
		
		BIPED_DIG = AnimationManager.getAnimation(DarkSouls.rl("biped_dig"));
		BIPED_TOUCH_BONFIRE = AnimationManager.getAnimation(DarkSouls.rl("biped_touch_bonfire"));

		BIPED_EAT = AnimationManager.getMirrorAnimation(DarkSouls.rl("biped_eat"));
		BIPED_DRINK = AnimationManager.getMirrorAnimation(DarkSouls.rl("biped_drink"));
		BIPED_CONSUME_SOUL = AnimationManager.getMirrorAnimation(DarkSouls.rl("biped_consume_soul"));
		
		BIPED_THROW = AnimationManager.getAnimation(DarkSouls.rl("biped_throw"));

		BIPED_BLOCK_HORIZONTAL = AnimationManager.getAnimation(DarkSouls.rl("biped_block"));

		BIPED_BLOCK_VERTICAL = AnimationManager.getAnimation(DarkSouls.rl("biped_block_vertical"));
		
		BIPED_BLOCK_GREATSHIELD = AnimationManager.getAnimation(DarkSouls.rl("biped_block_greatshield"));
		
		BIPED_BLOCK_TH_SWORD = AnimationManager.getAnimation(DarkSouls.rl("biped_block_th_sword"));
		
		BIPED_BLOCK_TH_VERTICAL = AnimationManager.getAnimation(DarkSouls.rl("biped_block_th_vertical"));
		
		BIPED_BLOCK_TH_HORIZONTAL = AnimationManager.getAnimation(DarkSouls.rl("biped_block_th_horizontal"));
		
		BIPED_BLOCK_TH_GREATSHIELD = AnimationManager.getAnimation(DarkSouls.rl("biped_block_th_greatshield"));
		
		BIPED_HIT_BLOCKED_LEFT = AnimationManager.getAnimation(DarkSouls.rl("biped_hit_blocked_left"));
		BIPED_HIT_BLOCKED_RIGHT = AnimationManager.getAnimation(DarkSouls.rl("biped_hit_blocked_right"));
		BIPED_HIT_BLOCKED_VERTICAL_LEFT = AnimationManager.getAnimation(DarkSouls.rl("biped_hit_blocked_vertical_left"));
		BIPED_HIT_BLOCKED_VERTICAL_RIGHT = AnimationManager.getAnimation(DarkSouls.rl("biped_hit_blocked_vertical_right"));
		BIPED_HIT_BLOCKED_TH_SWORD = AnimationManager.getAnimation(DarkSouls.rl("biped_hit_blocked_th_sword"));
		
		BIPED_HIT_BLOCKED_FLY_LEFT = AnimationManager.getAnimation(DarkSouls.rl("biped_hit_blocked_fly_left"));
		BIPED_HIT_BLOCKED_FLY_RIGHT = AnimationManager.getAnimation(DarkSouls.rl("biped_hit_blocked_fly_right"));
		BIPED_HIT_BLOCKED_VERTICAL_FLY_LEFT = AnimationManager.getAnimation(DarkSouls.rl("biped_hit_blocked_vertical_fly_left"));
		BIPED_HIT_BLOCKED_VERTICAL_FLY_RIGHT = AnimationManager.getAnimation(DarkSouls.rl("biped_hit_blocked_vertical_fly_right"));
		BIPED_HIT_BLOCKED_TH_SWORD_FLY = AnimationManager.getAnimation(DarkSouls.rl("biped_hit_blocked_th_sword_fly"));
		
		BIPED_DISARMED_LEFT = AnimationManager.getAnimation(DarkSouls.rl("biped_disarm_shield_left"));
		BIPED_DISARMED_RIGHT = AnimationManager.getAnimation(DarkSouls.rl("biped_disarm_shield_right"));
		
		BIPED_HORSEBACK_IDLE = AnimationManager.getAnimation(DarkSouls.rl("biped_horseback_idle"));
		
		BIPED_IDLE_CROSSBOW = AnimationManager.getAnimation(DarkSouls.rl("biped_idle_crossbow"));
		BIPED_WALK_CROSSBOW = AnimationManager.getAnimation(DarkSouls.rl("biped_walk_crossbow"));

		BIPED_CROSSBOW_AIM = AnimationManager.getAnimation(DarkSouls.rl("biped_crossbow_aim"));
		BIPED_CROSSBOW_SHOT = AnimationManager.getAnimation(DarkSouls.rl("biped_crossbow_shot"));

		BIPED_CROSSBOW_RELOAD = AnimationManager.getAnimation(DarkSouls.rl("biped_crossbow_reload"));

		BIPED_BOW_AIM = AnimationManager.getAnimation(DarkSouls.rl("biped_bow_aim"));
		BIPED_BOW_REBOUND = AnimationManager.getAnimation(DarkSouls.rl("biped_bow_rebound"));

		BIPED_SPEER_AIM = AnimationManager.getAnimation(DarkSouls.rl("biped_speer_aim"));
		BIPED_SPEER_REBOUND = AnimationManager.getAnimation(DarkSouls.rl("biped_speer_rebound"));

		BIPED_HIT_LIGHT_FRONT = AnimationManager.getAnimation(DarkSouls.rl("biped_hit_light_front"));
		BIPED_HIT_LIGHT_LEFT = AnimationManager.getAnimation(DarkSouls.rl("biped_hit_light_left"));
		BIPED_HIT_LIGHT_RIGHT = AnimationManager.getAnimation(DarkSouls.rl("biped_hit_light_right"));
		BIPED_HIT_LIGHT_BACK = AnimationManager.getAnimation(DarkSouls.rl("biped_hit_light_back"));
		BIPED_HIT_HEAVY_FRONT = AnimationManager.getAnimation(DarkSouls.rl("biped_hit_heavy_front"));
		BIPED_HIT_HEAVY_BACK = AnimationManager.getAnimation(DarkSouls.rl("biped_hit_heavy_back"));
		BIPED_HIT_HEAVY_LEFT = AnimationManager.getAnimation(DarkSouls.rl("biped_hit_heavy_left"));
		BIPED_HIT_HEAVY_RIGHT = AnimationManager.getAnimation(DarkSouls.rl("biped_hit_heavy_right"));
		
		BIPED_HORSEBACK_HIT_LIGHT_FRONT = AnimationManager.getAnimation(DarkSouls.rl("biped_horseback_hit_light_front"));
		BIPED_HORSEBACK_HIT_LIGHT_LEFT = AnimationManager.getAnimation(DarkSouls.rl("biped_horseback_hit_light_left"));
		BIPED_HORSEBACK_HIT_LIGHT_RIGHT = AnimationManager.getAnimation(DarkSouls.rl("biped_horseback_hit_light_right"));
		BIPED_HORSEBACK_HIT_LIGHT_BACK = AnimationManager.getAnimation(DarkSouls.rl("biped_horseback_hit_light_back"));
		BIPED_HORSEBACK_HIT_HEAVY_FRONT = AnimationManager.getAnimation(DarkSouls.rl("biped_horseback_hit_heavy_front"));
		BIPED_HORSEBACK_HIT_HEAVY_BACK = AnimationManager.getAnimation(DarkSouls.rl("biped_horseback_hit_heavy_back"));
		BIPED_HORSEBACK_HIT_HEAVY_LEFT = AnimationManager.getAnimation(DarkSouls.rl("biped_horseback_hit_heavy_left"));
		BIPED_HORSEBACK_HIT_HEAVY_RIGHT = AnimationManager.getAnimation(DarkSouls.rl("biped_horseback_hit_heavy_right"));
		
		BIPED_HIT_SMASH = AnimationManager.getAnimation(DarkSouls.rl("biped_hit_smash"));
		BIPED_HIT_FLY_FRONT = AnimationManager.getAnimation(DarkSouls.rl("biped_hit_fly_front"));
		BIPED_HIT_FLY_BACK = AnimationManager.getAnimation(DarkSouls.rl("biped_hit_fly_back"));
		BIPED_HIT_FLY_LEFT = AnimationManager.getAnimation(DarkSouls.rl("biped_hit_fly_left"));
		BIPED_HIT_FLY_RIGHT = AnimationManager.getAnimation(DarkSouls.rl("biped_hit_fly_right"));
		BIPED_HIT_LAND_HEAVY = AnimationManager.getAnimation(DarkSouls.rl("biped_hit_land_heavy"));
		
		BIPED_HIT_BACKSTAB = AnimationManager.getAnimation(DarkSouls.rl("biped_hit_backstab"));
		
		BIPED_HIT_PUNISH = AnimationManager.getAnimation(DarkSouls.rl("biped_hit_punish"));
		
		BIPED_ROLL = AnimationManager.getAnimation(DarkSouls.rl("biped_roll"));
		BIPED_FAT_ROLL = AnimationManager.getAnimation(DarkSouls.rl("biped_fat_roll"));
		BIPED_ROLL_TOO_FAT = AnimationManager.getAnimation(DarkSouls.rl("biped_roll_too_fat"));
		BIPED_ROLL_BACK = AnimationManager.getAnimation(DarkSouls.rl("biped_roll_back"));
		BIPED_ROLL_LEFT = AnimationManager.getAnimation(DarkSouls.rl("biped_roll_left"));
		BIPED_ROLL_RIGHT = AnimationManager.getAnimation(DarkSouls.rl("biped_roll_right"));
		BIPED_JUMP_BACK = AnimationManager.getAnimation(DarkSouls.rl("biped_jump_back"));
		
		// Miracle
		BIPED_CAST_MIRACLE_HEAL = AnimationManager.getAnimation(DarkSouls.rl("biped_cast_miracle_heal"));
		
		BIPED_CAST_MIRACLE_HEAL_AID = AnimationManager.getAnimation(DarkSouls.rl("biped_cast_miracle_heal_aid"));
		
		BIPED_CAST_MIRACLE_HOMEWARD = AnimationManager.getAnimation(DarkSouls.rl("biped_cast_miracle_homeward"));
		
		BIPED_CAST_MIRACLE_FORCE = AnimationManager.getAnimation(DarkSouls.rl("biped_cast_miracle_force"));
		
		BIPED_CAST_MIRACLE_LIGHTNING_SPEAR = AnimationManager.getAnimation(DarkSouls.rl("biped_cast_miracle_lightning_spear"));
		HORSEBACK_CAST_MIRACLE_LIGHTNING_SPEAR = AnimationManager.getAnimation(DarkSouls.rl("horseback_cast_miracle_lightning_spear"));
		
		BIPED_CAST_MIRACLE_GREAT_LIGHTNING_SPEAR = AnimationManager.getAnimation(DarkSouls.rl("biped_cast_miracle_great_lightning_spear"));
		HORSEBACK_CAST_MIRACLE_GREAT_LIGHTNING_SPEAR = AnimationManager.getAnimation(DarkSouls.rl("horseback_cast_miracle_great_lightning_spear"));
		
		// Big Weapon
		BIPED_HOLDING_BIG_WEAPON = AnimationManager.getMirrorAnimation(DarkSouls.rl("biped_holding_big_weapon"));
		
		// Horseback Attacks
		HORSEBACK_LIGHT_ATTACK = new AttackAnimation[]
		{
					AnimationManager.getAttackAnimation(DarkSouls.rl("horseback_light_attack_1")),
					AnimationManager.getAttackAnimation(DarkSouls.rl("horseback_light_attack_2"))
		};
		
		// Parries
		SHIELD_PARRY_LEFT = AnimationManager.getParryAnimation(DarkSouls.rl("shield_parry"));
		SHIELD_PARRY_RIGHT = AnimationManager.getParryAnimation(DarkSouls.rl("shield_parry_mirrored"));
		BUCKLER_PARRY_LEFT = AnimationManager.getParryAnimation(DarkSouls.rl("buckler_parry"));
		BUCKLER_PARRY_RIGHT = AnimationManager.getParryAnimation(DarkSouls.rl("buckler_parry_mirrored"));
		
		// Backstabs
		BACKSTAB_THRUST = AnimationManager.getAttackAnimation(DarkSouls.rl("backstab_thrust_check"));
		BACKSTAB_STRIKE = AnimationManager.getAttackAnimation(DarkSouls.rl("backstab_strike_check"));
		
		// Punishes
		PUNISH_THRUST = AnimationManager.getAttackAnimation(DarkSouls.rl("punish_thrust_check"));
		PUNISH_STRIKE = AnimationManager.getAttackAnimation(DarkSouls.rl("punish_strike_check"));
		
		// Thrusting Sword
		THRUSTING_SWORD_LIGHT_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("thrusting_sword_light_attack"));
		THRUSTING_SWORD_HEAVY_ATTACK = new AttackAnimation[]
				{
						AnimationManager.getAttackAnimation(DarkSouls.rl("thrusting_sword_heavy_attack_1")),
						AnimationManager.getAttackAnimation(DarkSouls.rl("thrusting_sword_heavy_attack_2"))
				};
		THRUSTING_SWORD_DASH_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("thrusting_sword_dash_attack"));
		THRUSTING_SWORD_TH_LIGHT_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("thrusting_sword_th_light_attack"));
		THRUSTING_SWORD_TH_HEAVY_ATTACK = new AttackAnimation[]
				{
						AnimationManager.getAttackAnimation(DarkSouls.rl("thrusting_sword_th_heavy_attack_1")),
						AnimationManager.getAttackAnimation(DarkSouls.rl("thrusting_sword_th_heavy_attack_2"))
				};
		THRUSTING_SWORD_TH_DASH_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("thrusting_sword_th_dash_attack"));
		
		// Greatsword
		GREATSWORD_LIGHT_ATTACK = new AttackAnimation[]
		{
				AnimationManager.getAttackAnimation(DarkSouls.rl("greatsword_light_attack_1")),
				AnimationManager.getAttackAnimation(DarkSouls.rl("greatsword_light_attack_2"))
		};
		GREATSWORD_THRUST = AnimationManager.getAttackAnimation(DarkSouls.rl("greatsword_thrust"));
		GREATSWORD_UPWARD_SLASH = AnimationManager.getAttackAnimation(DarkSouls.rl("greatsword_upward_slash"));
		GREATSWORD_STYLISH_THRUST = AnimationManager.getAttackAnimation(DarkSouls.rl("greatsword_stylish_thrust"));
		GREATSWORD_DASH_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("greatsword_dash_attack"));
		GREATSWORD_TH_LIGHT_ATTACK = new AttackAnimation[]
				{
						AnimationManager.getAttackAnimation(DarkSouls.rl("greatsword_th_la_1")),
						AnimationManager.getAttackAnimation(DarkSouls.rl("greatsword_th_la_2"))
				};
		GREATSWORD_TH_THRUST_ATTACK = new AttackAnimation[]
				{
						AnimationManager.getAttackAnimation(DarkSouls.rl("greatsword_th_ha_1")),
						AnimationManager.getAttackAnimation(DarkSouls.rl("greatsword_th_ha_2"))
				};
		GREATSWORD_TH_DASH_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("greatsword_th_da"));

		// Ultra Greatsword
		ULTRA_GREATSWORD_LIGHT_ATTACK = new AttackAnimation[]
		{
				AnimationManager.getAttackAnimation(DarkSouls.rl("ultra_greatsword_light_attack_1")),
				AnimationManager.getAttackAnimation(DarkSouls.rl("ultra_greatsword_light_attack_2"))
		};
		ULTRA_GREATSWORD_HEAVY_ATTACK = new AttackAnimation[]
		{
				AnimationManager.getAttackAnimation(DarkSouls.rl("ultra_greatsword_heavy_attack_1")),
				AnimationManager.getAttackAnimation(DarkSouls.rl("ultra_greatsword_heavy_attack_2"))
		};
		ULTRA_GREATSWORD_DASH_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("ultra_greatsword_dash_attack"));
		ULTRA_GREATSWORD_TH_LIGHT_ATTACK = new AttackAnimation[]
				{
						AnimationManager.getAttackAnimation(DarkSouls.rl("ultra_greatsword_th_la_1")),
						AnimationManager.getAttackAnimation(DarkSouls.rl("ultra_greatsword_th_la_2"))
				};
		ULTRA_GREATSWORD_TH_HEAVY_ATTACK = new AttackAnimation[]
				{
						AnimationManager.getAttackAnimation(DarkSouls.rl("ultra_greatsword_th_ha_1")),
						AnimationManager.getAttackAnimation(DarkSouls.rl("ultra_greatsword_th_ha_2"))
				};
		ULTRA_GREATSWORD_TH_DASH_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("ultra_greatsword_th_da"));
		
		// Greataxe
		GREATAXE_LIGHT_ATTACK = new AttackAnimation[]
				{
						AnimationManager.getAttackAnimation(DarkSouls.rl("greataxe_la_1")),
						AnimationManager.getAttackAnimation(DarkSouls.rl("greataxe_la_2"))
				};
		GREATAXE_HEAVY_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("greataxe_ha"));
		GREATAXE_DASH_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("greataxe_da"));
		GREATAXE_TH_LIGHT_ATTACK = new AttackAnimation[]
				{
						AnimationManager.getAttackAnimation(DarkSouls.rl("greataxe_th_la_1")),
						AnimationManager.getAttackAnimation(DarkSouls.rl("greataxe_th_la_2"))
				};
		GREATAXE_TH_HEAVY_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("greataxe_th_ha"));
		GREATAXE_TH_DASH_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("greataxe_th_da"));

		// Spear
		SPEAR_DASH_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("spear_dash_attack"));
		SPEAR_HEAVY_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("spear_heavy_attack"));
		SPEAR_LIGHT_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("spear_light_attack"));
		SPEAR_LIGHT_BLOCKING_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("spear_light_blocking_attack"));
		SPEAR_TH_LIGHT_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("spear_th_la"));
		SPEAR_TH_HEAVY_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("spear_th_ha"));
		SPEAR_TH_DASH_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("spear_th_da"));

		// Dagger
		DAGGER_HEAVY_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("dagger_heavy_attack"));
		DAGGER_LIGHT_ATTACK = new AttackAnimation[]
		{
				AnimationManager.getAttackAnimation(DarkSouls.rl("dagger_light_attack_1")),
				AnimationManager.getAttackAnimation(DarkSouls.rl("dagger_light_attack_2"))
		};

		// Great Hammer
		GREAT_HAMMER_HEAVY_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("great_hammer_heavy_attack"));
		GREAT_HAMMER_LIGHT_ATTACK = new AttackAnimation[]
		{
				AnimationManager.getAttackAnimation(DarkSouls.rl("great_hammer_light_attack_1")),
				AnimationManager.getAttackAnimation(DarkSouls.rl("great_hammer_light_attack_2"))
		};
		GREAT_HAMMER_DASH_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("great_hammer_dash_attack"));
		GREAT_HAMMER_TH_LIGHT_ATTACK = new AttackAnimation[]
		{
						AnimationManager.getAttackAnimation(DarkSouls.rl("great_hammer_th_la_1")),
						AnimationManager.getAttackAnimation(DarkSouls.rl("great_hammer_th_la_2"))
		};
		GREAT_HAMMER_TH_HEAVY_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("great_hammer_th_ha"));
		GREAT_HAMMER_TH_DASH_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("great_hammer_th_da"));

		// Axe
		AXE_HEAVY_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("axe_heavy_attack"));
		AXE_LIGHT_ATTACK = new AttackAnimation[]
		{
				AnimationManager.getAttackAnimation(DarkSouls.rl("axe_light_attack_1")),
				AnimationManager.getAttackAnimation(DarkSouls.rl("axe_light_attack_2"))
		};
		AXE_DASH_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("axe_dash_attack"));
		AXE_TH_LIGHT_ATTACK = new AttackAnimation[]
		{
			AnimationManager.getAttackAnimation(DarkSouls.rl("axe_th_la_1")),
			AnimationManager.getAttackAnimation(DarkSouls.rl("axe_th_la_2"))
		};
		AXE_TH_HEAVY_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("axe_th_ha"));
		AXE_TH_DASH_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("axe_th_da"));

		// Hammer
		HAMMER_DASH_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("hammer_dash_attack"));
		HAMMER_HEAVY_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("hammer_heavy_attack"));
		HAMMER_LIGHT_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("hammer_light_attack"));
		HAMMER_TH_LIGHT_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("hammer_th_la"));
		HAMMER_TH_HEAVY_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("hammer_th_ha"));
		HAMMER_TH_DASH_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("hammer_th_da"));

		// Fist
		FIST_LIGHT_ATTACK = new AttackAnimation[]
		{
				AnimationManager.getAttackAnimation(DarkSouls.rl("fist_light_attack_1")),
				AnimationManager.getAttackAnimation(DarkSouls.rl("fist_light_attack_2"))
		};
		FIST_DASH_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("fist_dash_attack"));
		FIST_HEAVY_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("fist_heavy_attack"));

		// Shield
		SHIELD_LIGHT_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("shield_la"));
		SHIELD_HEAVY_ATTACK = new AttackAnimation[]
		{
				AnimationManager.getAttackAnimation(DarkSouls.rl("shield_ha_1")),
				AnimationManager.getAttackAnimation(DarkSouls.rl("shield_ha_2")),
		};
		SHIELD_DASH_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("shield_da"));
		SHIELD_TH_LIGHT_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("shield_th_la"));
		SHIELD_TH_HEAVY_ATTACK = new AttackAnimation[]
				{
						AnimationManager.getAttackAnimation(DarkSouls.rl("shield_th_ha_1")),
						AnimationManager.getAttackAnimation(DarkSouls.rl("shield_th_ha_2")),
				};
		SHIELD_TH_DASH_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("shield_th_da"));
		
		
		// Greatshield
		GREATSHIELD_LIGHT_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("greatshield_light_attack"));
		GREATSHIELD_HEAVY_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("greatshield_heavy_attack"));
		GREATSHIELD_DASH_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("greatshield_dash_attack"));
		GREATSHIELD_TH_LIGHT_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("greatshield_th_light_attack"));
		GREATSHIELD_TH_HEAVY_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("greatshield_th_heavy_attack"));
		GREATSHIELD_TH_DASH_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("greatshield_th_dash_attack"));
		GREATSHIELD_BASH = AnimationManager.getAttackAnimation(DarkSouls.rl("greatshield_bash"));
		
		// Straight Sword
		STRAIGHT_SWORD_LIGHT_ATTACK = new AttackAnimation[]
		{ 		
				AnimationManager.getAttackAnimation(DarkSouls.rl("straight_sword_light_attack_1")),
				AnimationManager.getAttackAnimation(DarkSouls.rl("straight_sword_light_attack_2"))
		};
		STRAIGHT_SWORD_HEAVY_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("straight_sword_heavy_attack"));
		STRAIGHT_SWORD_DASH_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("straight_sword_dash_attack"));
		STRAIGHT_SWORD_TH_LIGHT_ATTACK = new AttackAnimation[]
		{
						AnimationManager.getAttackAnimation(DarkSouls.rl("straight_sword_th_la_1")),
						AnimationManager.getAttackAnimation(DarkSouls.rl("straight_sword_th_la_2"))
		};
		STRAIGHT_SWORD_TH_HEAVY_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("straight_sword_th_ha"));
		STRAIGHT_SWORD_TH_DASH_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("straight_sword_th_da"));

		// Hollow
		HOLLOW_IDLE = AnimationManager.getAnimation(DarkSouls.rl("hollow_idle"));
		HOLLOW_WALK = AnimationManager.getAnimation(DarkSouls.rl("hollow_walk"));
		HOLLOW_RUN = AnimationManager.getAnimation(DarkSouls.rl("hollow_run"));
		HOLLOW_DEFLECTED = AnimationManager.getAnimation(DarkSouls.rl("hollow_deflected"));
		HOLLOW_BREAKDOWN = AnimationManager.getAnimation(DarkSouls.rl("hollow_breakdown"));

		HOLLOW_LIGHT_ATTACKS = new AttackAnimation[]
		{
				AnimationManager.getAttackAnimation(DarkSouls.rl("hollow_light_attack_1")),
				AnimationManager.getAttackAnimation(DarkSouls.rl("hollow_light_attack_2"))
		};
		HOLLOW_BARRAGE = AnimationManager.getAttackAnimation(DarkSouls.rl("hollow_barrage"));
		HOLLOW_OVERHEAD_SWING = AnimationManager.getAttackAnimation(DarkSouls.rl("hollow_overhead_swing"));
		HOLLOW_JUMP_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("hollow_jump_attack"));

		// Hollow Lordran Warrior
		HOLLOW_LORDRAN_WARRIOR_WALK = AnimationManager.getAnimation(DarkSouls.rl("hollow_lordran_warrior_walk"));
		HOLLOW_LORDRAN_WARRIOR_RUN = AnimationManager.getAnimation(DarkSouls.rl("hollow_lordran_warrior_run"));

		HOLLOW_LORDRAN_WARRIOR_TH_LA = new AttackAnimation[]
		{
				AnimationManager.getAttackAnimation(DarkSouls.rl("hollow_lordran_warrior_th_la_1")),
				AnimationManager.getAttackAnimation(DarkSouls.rl("hollow_lordran_warrior_th_la_2"))
		};

		HOLLOW_LORDRAN_WARRIOR_DASH_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("hollow_lordran_warrior_dash_attack"));

		HOLLOW_LORDRAN_WARRIOR_AXE_LA = new AttackAnimation[]
		{
				AnimationManager.getAttackAnimation(DarkSouls.rl("hollow_lordran_warrior_axe_la_1")),
				AnimationManager.getAttackAnimation(DarkSouls.rl("hollow_lordran_warrior_axe_la_2"))
		};

		HOLLOW_LORDRAN_WARRIOR_AXE_TH_LA = new AttackAnimation[]
		{
				AnimationManager.getAttackAnimation(DarkSouls.rl("hollow_lordran_warrior_axe_th_la_1")),
				AnimationManager.getAttackAnimation(DarkSouls.rl("hollow_lordran_warrior_axe_th_la_2"))
		};

		// Hollow Lordran Soldier
		HOLLOW_LORDRAN_SOLDIER_WALK = AnimationManager.getAnimation(DarkSouls.rl("hollow_lordran_soldier_walk"));
		HOLLOW_LORDRAN_SOLDIER_RUN = AnimationManager.getAnimation(DarkSouls.rl("hollow_lordran_soldier_run"));
		HOLLOW_LORDRAN_SOLDIER_BLOCK = AnimationManager.getAnimation(DarkSouls.rl("hollow_lordran_soldier_block"));
		
		HOLLOW_LORDRAN_SOLDIER_SWORD_LA = new AttackAnimation[]
		{
				AnimationManager.getAttackAnimation(DarkSouls.rl("hollow_lordran_soldier_sword_la_1")),
				AnimationManager.getAttackAnimation(DarkSouls.rl("hollow_lordran_soldier_sword_la_2")),
				AnimationManager.getAttackAnimation(DarkSouls.rl("hollow_lordran_soldier_sword_la_3"))
		};

		HOLLOW_LORDRAN_SOLDIER_SWORD_DA = AnimationManager.getAttackAnimation(DarkSouls.rl("hollow_lordran_soldier_sword_da"));

		HOLLOW_LORDRAN_SOLDIER_SWORD_HEAVY_THRUST = AnimationManager.getAttackAnimation(DarkSouls.rl("hollow_lordran_soldier_sword_heavy_thrust"));

		HOLLOW_LORDRAN_SOLDIER_SWORD_THRUST_COMBO = AnimationManager.getAttackAnimation(DarkSouls.rl("hollow_lordran_soldier_sword_thrust_combo"));

		HOLLOW_LORDRAN_SOLDIER_SPEAR_SWINGS = new AttackAnimation[]
		{
				AnimationManager.getAttackAnimation(DarkSouls.rl("hollow_lordran_soldier_spear_swing_1")),
				AnimationManager.getAttackAnimation(DarkSouls.rl("hollow_lordran_soldier_spear_swing_2")),
				AnimationManager.getAttackAnimation(DarkSouls.rl("hollow_lordran_soldier_spear_swing_3")),
				AnimationManager.getAttackAnimation(DarkSouls.rl("hollow_lordran_soldier_spear_swing_4"))
		};

		HOLLOW_LORDRAN_SOLDIER_SPEAR_THRUSTS = new AttackAnimation[]
		{ 
				AnimationManager.getAttackAnimation(DarkSouls.rl("hollow_lordran_soldier_spear_thrust_1")),
				AnimationManager.getAttackAnimation(DarkSouls.rl("hollow_lordran_soldier_spear_thrust_2")),
				AnimationManager.getAttackAnimation(DarkSouls.rl("hollow_lordran_soldier_spear_thrust_3"))
		};

		HOLLOW_LORDRAN_SOLDIER_SHIELD_BASH = AnimationManager.getAttackAnimation(DarkSouls.rl("hollow_lordran_soldier_shield_bash"));
		
		// Falconer
		FALCONER_IDLE = AnimationManager.getAnimation(DarkSouls.rl("falconer_idle"));
		FALCONER_WALK = AnimationManager.getAnimation(DarkSouls.rl("falconer_walk"));
		FALCONER_RUN = AnimationManager.getAnimation(DarkSouls.rl("falconer_run"));
		
		FALCONER_LIGHT_ATTACKS = new AttackAnimation[]
		{
				AnimationManager.getAttackAnimation(DarkSouls.rl("falconer_light_attack_1")),
				AnimationManager.getAttackAnimation(DarkSouls.rl("falconer_light_attack_2")),
				AnimationManager.getAttackAnimation(DarkSouls.rl("falconer_light_attack_3"))
		};
		
		
		// Balder Knight
		BALDER_KNIGHT_IDLE = AnimationManager.getAnimation(DarkSouls.rl("balder_knight_idle"));
		BALDER_KNIGHT_WALK = AnimationManager.getAnimation(DarkSouls.rl("balder_knight_walk"));
		BALDER_KNIGHT_RUN = AnimationManager.getAnimation(DarkSouls.rl("balder_knight_run"));
		BALDER_KNIGHT_BLOCK = AnimationManager.getAnimation(DarkSouls.rl("balder_knight_block"));
		BALDER_KNIGHT_RAPIER_BLOCK = AnimationManager.getAdaptableAnimation(DarkSouls.rl("balder_knight_rapier_block"));
		
		BALDER_KNIGHT_RAPIER_PARRY = AnimationManager.getParryAnimation(DarkSouls.rl("balder_knight_rapier_parry"));
		
		BALDER_KNIGHT_SIDE_SWORD_LA = new AttackAnimation[]
				{
					AnimationManager.getAttackAnimation(DarkSouls.rl("balder_knight_side_sword_la_1")),
					AnimationManager.getAttackAnimation(DarkSouls.rl("balder_knight_side_sword_la_2")),
					AnimationManager.getAttackAnimation(DarkSouls.rl("balder_knight_side_sword_la_3"))
				};
		BALDER_KNIGHT_SIDE_SWORD_HA = AnimationManager.getAttackAnimation(DarkSouls.rl("balder_knight_side_sword_ha"));
		BALDER_KNIGHT_SIDE_SWORD_DA = AnimationManager.getAttackAnimation(DarkSouls.rl("balder_knight_side_sword_da"));
		BALDER_KNIGHT_SIDE_SWORD_FAST_LA = AnimationManager.getAttackAnimation(DarkSouls.rl("balder_knight_side_sword_fast_la"));
		BALDER_KNIGHT_SHIELD_HA = AnimationManager.getAttackAnimation(DarkSouls.rl("balder_knight_shield_ha"));
		
		BALDER_KNIGHT_RAPIER_LA = new AttackAnimation[]
				{
						AnimationManager.getAttackAnimation(DarkSouls.rl("balder_knight_rapier_la_1")),
						AnimationManager.getAttackAnimation(DarkSouls.rl("balder_knight_rapier_la_2")),
						AnimationManager.getAttackAnimation(DarkSouls.rl("balder_knight_rapier_la_3"))
					};
		BALDER_KNIGHT_RAPIER_HA = AnimationManager.getAttackAnimation(DarkSouls.rl("balder_knight_rapier_ha"));
		BALDER_KNIGHT_RAPIER_DA = AnimationManager.getAttackAnimation(DarkSouls.rl("balder_knight_rapier_da"));
		
		
		// Berenike Knight
		BERENIKE_KNIGHT_IDLE = AnimationManager.getAnimation(DarkSouls.rl("berenike_knight_idle"));
		
		BERENIKE_KNIGHT_SWORD_LA = new AttackAnimation[]
				{
					AnimationManager.getAttackAnimation(DarkSouls.rl("berenike_knight_sword_la_1")),
					AnimationManager.getAttackAnimation(DarkSouls.rl("berenike_knight_sword_la_2"))
				};
		BERENIKE_KNIGHT_SWORD_HA = new AttackAnimation[]
				{
					AnimationManager.getAttackAnimation(DarkSouls.rl("berenike_knight_sword_ha_1")),
					AnimationManager.getAttackAnimation(DarkSouls.rl("berenike_knight_sword_ha_2"))
				};
		BERENIKE_KNIGHT_SWORD_DA = AnimationManager.getAttackAnimation(DarkSouls.rl("berenike_knight_sword_da"));
		
		BERENIKE_KNIGHT_MACE_LA = new AttackAnimation[]
				{
					AnimationManager.getAttackAnimation(DarkSouls.rl("berenike_knight_mace_la_1")),
					AnimationManager.getAttackAnimation(DarkSouls.rl("berenike_knight_mace_la_2"))
				};
		BERENIKE_KNIGHT_MACE_HA = AnimationManager.getAttackAnimation(DarkSouls.rl("berenike_knight_mace_ha"));
		
		BERENIKE_KNIGHT_KICK = AnimationManager.getAttackAnimation(DarkSouls.rl("berenike_knight_kick"));
		
		
		// Black Knight
		BLACK_KNIGHT_IDLE = AnimationManager.getAnimation(DarkSouls.rl("black_knight_idle"));
		BLACK_KNIGHT_WALK = AnimationManager.getAnimation(DarkSouls.rl("black_knight_walking"));
		BLACK_KNIGHT_RUN = AnimationManager.getAnimation(DarkSouls.rl("black_knight_running"));
		BLACK_KNIGHT_BLOCK = AnimationManager.getAnimation(DarkSouls.rl("black_knight_block"));
		BLACK_KNIGHT_DEATH = AnimationManager.getDeathAnimation(DarkSouls.rl("black_knight_death"));
		
		BLACK_KNIGHT_SWORD_LA_LONG = new AttackAnimation[]
				{
						AnimationManager.getAttackAnimation(DarkSouls.rl("black_knight_sword_la_long_1")),
						AnimationManager.getAttackAnimation(DarkSouls.rl("black_knight_sword_la_long_2")),
						AnimationManager.getAttackAnimation(DarkSouls.rl("black_knight_sword_la_long_3")),
						AnimationManager.getAttackAnimation(DarkSouls.rl("black_knight_sword_la_long_4"))
				};
		BLACK_KNIGHT_SWORD_LA_SHORT = new AttackAnimation[]
				{
						AnimationManager.getAttackAnimation(DarkSouls.rl("black_knight_sword_la_short_1")),
						AnimationManager.getAttackAnimation(DarkSouls.rl("black_knight_sword_la_short_2")),
						AnimationManager.getAttackAnimation(DarkSouls.rl("black_knight_sword_la_short_5"))
				};
		BLACK_KNIGHT_SWORD_HA = AnimationManager.getAttackAnimation(DarkSouls.rl("black_knight_sword_ha"));
		BLACK_KNIGHT_SHIELD_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("black_knight_shield_attack"));
		BLACK_KNIGHT_SWORD_DA = AnimationManager.getAttackAnimation(DarkSouls.rl("black_knight_sword_da"));
		
		
		// Stray Demon
		STRAY_DEMON_IDLE = AnimationManager.getAnimation(DarkSouls.rl("stray_demon_idle"));
		STRAY_DEMON_WALK = AnimationManager.getAnimation(DarkSouls.rl("stray_demon_walk"));
		STRAY_DEMON_DEATH = AnimationManager.getDeathAnimation(DarkSouls.rl("stray_demon_death"));

		STRAY_DEMON_HAMMER_LIGHT_ATTACK = new AttackAnimation[]
		{
				AnimationManager.getAttackAnimation(DarkSouls.rl("stray_demon_hammer_la_1")),
				AnimationManager.getAttackAnimation(DarkSouls.rl("stray_demon_hammer_la_2"))
		};
		STRAY_DEMON_HAMMER_ALT_LIGHT_ATTACK = new AttackAnimation[]
		{
				AnimationManager.getAttackAnimation(DarkSouls.rl("stray_demon_hammer_la_alt_1")),
				AnimationManager.getAttackAnimation(DarkSouls.rl("stray_demon_hammer_la_alt_2"))
		};
		STRAY_DEMON_HAMMER_HEAVY_ATTACK = new AttackAnimation[]
		{
				AnimationManager.getAttackAnimation(DarkSouls.rl("stray_demon_hammer_ha_1")),
				AnimationManager.getAttackAnimation(DarkSouls.rl("stray_demon_hammer_ha_2"))
		};
		STRAY_DEMON_HAMMER_DRIVE = AnimationManager.getAttackAnimation(DarkSouls.rl("stray_demon_hammer_drive"));
		STRAY_DEMON_HAMMER_DASH_ATTACK = AnimationManager.getAttackAnimation(DarkSouls.rl("stray_demon_hammer_da"));
		STRAY_DEMON_GROUND_POUND = AnimationManager.getAttackAnimation(DarkSouls.rl("stray_demon_ground_pound"));
		
		// Taurus Demon
		TAURUS_DEMON_IDLE = AnimationManager.getAnimation(DarkSouls.rl("taurus_demon_idle"));
		
		// Anastacia of Astora
		ANASTACIA_IDLE = AnimationManager.getAnimation(DarkSouls.rl("anastacia_idle"));
		
		// Bell Gargoyle
		BELL_GARGOYLE_IDLE = AnimationManager.getAnimation(DarkSouls.rl("bell_gargoyle_idle"));
	}
	
	public static StaticAnimation createSupplier(BiFunction<LivingCap<?>, LayerPart, StaticAnimation> biFunction)
	{
		return new SupplierAnimation(biFunction);
	}
}