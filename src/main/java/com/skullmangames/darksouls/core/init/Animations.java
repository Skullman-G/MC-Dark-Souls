package com.skullmangames.darksouls.core.init;

import java.util.List;
import java.util.function.BiFunction;

import com.google.common.collect.ImmutableMap;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.animation.AnimationLayer.LayerPart;
import com.skullmangames.darksouls.client.particles.EntityboundParticleOptions;
import com.skullmangames.darksouls.client.particles.spawner.CircleParticleSpawner;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.Property.ActionAnimationProperty;
import com.skullmangames.darksouls.common.animation.Property.AttackProperty;
import com.skullmangames.darksouls.common.animation.Property.DeathProperty;
import com.skullmangames.darksouls.common.animation.Property.StaticAnimationProperty;
import com.skullmangames.darksouls.common.animation.types.ActionAnimation;
import com.skullmangames.darksouls.common.animation.types.AdaptableAnimation;
import com.skullmangames.darksouls.common.animation.types.AimingAnimation;
import com.skullmangames.darksouls.common.animation.types.BlockedAnimation;
import com.skullmangames.darksouls.common.animation.types.DeathAnimation;
import com.skullmangames.darksouls.common.animation.types.DodgingAnimation;
import com.skullmangames.darksouls.common.animation.types.HitAnimation;
import com.skullmangames.darksouls.common.animation.types.InvincibleAnimation;
import com.skullmangames.darksouls.common.animation.types.MirrorAnimation;
import com.skullmangames.darksouls.common.animation.types.MovementAnimation;
import com.skullmangames.darksouls.common.animation.types.PunishableAnimation;
import com.skullmangames.darksouls.common.animation.types.ReboundAnimation;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation.Event;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation.Event.Side;
import com.skullmangames.darksouls.common.animation.types.SupplierAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation.Phase;
import com.skullmangames.darksouls.common.animation.types.attack.BackstabCheckAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.CriticalHitAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.ParryAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.PunishCheckAnimation;
import com.skullmangames.darksouls.common.block.LightSource;
import com.skullmangames.darksouls.core.util.DamageSourceExtended;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.CoreDamageType;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.Damages;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.MovementDamageType;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.StunType;
import com.skullmangames.darksouls.core.util.math.MathUtils;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.capability.entity.MobCap;
import com.skullmangames.darksouls.common.capability.item.Shield.Deflection;
import com.skullmangames.darksouls.common.capability.item.ThrowableCap;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap.AttackType;
import com.skullmangames.darksouls.common.entity.projectile.LightningSpear;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

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
	
	public static void teleportParticles(LivingCap<?> cap)
	{
		double r = 0.1F;
		for (int i = 0; i < 360; i++)
		{
			if (i % 40 == 0)
			{
				double ir = Math.toRadians(i);
				cap.getLevel().addParticle(ModParticles.DUST_CLOUD.get(), cap.getX(), cap.getY(), cap.getZ(), Math.sin(ir) * r, 0, Math.cos(ir) * r);
			}
		}
	}

	public static StaticAnimation BIPED_EAT;
	public static StaticAnimation BIPED_DRINK;
	public static StaticAnimation BIPED_CONSUME_SOUL;
	
	public static StaticAnimation BIPED_THROW;

	public static StaticAnimation BIPED_BLOCK_HORIZONTAL;
	public static StaticAnimation BIPED_BLOCK_VERTICAL;
	public static StaticAnimation BIPED_BLOCK_TH_SWORD;
	
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
	public static DodgingAnimation BIPED_JUMP_BACK;
	
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
	
	public static ImmutableMap.Builder<ResourceLocation, StaticAnimation> init()
	{
		ImmutableMap.Builder<ResourceLocation, StaticAnimation> builder = ImmutableMap.builder();
		
		DUMMY_ANIMATION = new StaticAnimation();

		BIPED_IDLE = new StaticAnimation(DarkSouls.rl("biped_idle"), 0.1F, true,
				DarkSouls.rl("biped/living/idle"), (models) -> models.ENTITY_BIPED).register(builder);
		BIPED_WALK = new MovementAnimation(DarkSouls.rl("biped_walk"), 0.08F, true,
				DarkSouls.rl("biped/living/walk"), (models) -> models.ENTITY_BIPED).register(builder);
		BIPED_RUN = new MovementAnimation(DarkSouls.rl("biped_run"), 0.08F, true,
				DarkSouls.rl("biped/living/run"), (models) -> models.ENTITY_BIPED).register(builder);
		BIPED_SNEAK = new MovementAnimation(DarkSouls.rl("biped_sneak"), 0.08F, true,
				DarkSouls.rl("biped/living/sneak"), (models) -> models.ENTITY_BIPED).register(builder);
		
		BIPED_IDLE_TH = new StaticAnimation(DarkSouls.rl("biped_idle_th"), 0.1F, true,
				DarkSouls.rl("biped/living/idle_th"), (models) -> models.ENTITY_BIPED).register(builder);
		BIPED_WALK_TH = new StaticAnimation(DarkSouls.rl("biped_walk_th"), 0.08F, true,
				DarkSouls.rl("biped/living/walk_th"), (models) -> models.ENTITY_BIPED).register(builder);
		BIPED_RUN_TH = new StaticAnimation(DarkSouls.rl("biped_run_th"), 0.08F, true,
				DarkSouls.rl("biped/living/run_th"), (models) -> models.ENTITY_BIPED).register(builder);
		
		BIPED_IDLE_TH_BIG_WEAPON = new StaticAnimation(DarkSouls.rl("biped_idle_th_big_weapon"), 0.1F, true,
				DarkSouls.rl("biped/living/idle_th_big_weapon"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.LAYER_PART, LayerPart.UP)
				.register(builder);
		
		BIPED_IDLE_TH_SPEAR = new StaticAnimation(DarkSouls.rl("biped_idle_th_spear"), 0.1F, true,
				DarkSouls.rl("biped/living/idle_th_spear"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.LAYER_PART, LayerPart.UP)
				.register(builder);
		
		BIPED_IDLE_TH_SHIELD = new StaticAnimation(DarkSouls.rl("biped_idle_th_shield"), 0.1F, true,
				DarkSouls.rl("biped/living/idle_th_shield"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.LAYER_PART, LayerPart.UP)
				.register(builder);
		
		BIPED_CHANGE_ITEM_RIGHT = new StaticAnimation(DarkSouls.rl("biped_change_item_right"), 0.1F, false,
				DarkSouls.rl("biped/living/change_item_right"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(0.2F, Side.SERVER, (cap) ->
						{
							if (cap instanceof MobCap<?> mob)
							{
								mob.changeItem();
							}
						})
				})
				.register(builder);
		
		BIPED_CHANGE_ITEM_LEFT = new StaticAnimation(DarkSouls.rl("biped_change_item_left"), 0.1F, false,
				DarkSouls.rl("biped/living/change_item_left"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(0.2F, Side.SERVER, (cap) ->
						{
							if (cap instanceof MobCap<?> mob)
							{
								mob.changeItem();
							}
						})
				})
				.register(builder);
		
		BIPED_SWIM = new MovementAnimation(DarkSouls.rl("biped_swim"), 0.08F, true,
				DarkSouls.rl("biped/living/swim"), (models) -> models.ENTITY_BIPED).register(builder);
		BIPED_FLOAT = new StaticAnimation(new ResourceLocation(DarkSouls.MOD_ID, "biped_float"), 0.08F, true,
				DarkSouls.rl("biped/living/float"), (models) -> models.ENTITY_BIPED).register(builder);
		BIPED_KNEEL = new StaticAnimation(new ResourceLocation(DarkSouls.MOD_ID, "biped_kneel"), 0.08F, true,
				DarkSouls.rl("biped/living/kneel"), (models) -> models.ENTITY_BIPED).register(builder);
		BIPED_FALL = new StaticAnimation(new ResourceLocation(DarkSouls.MOD_ID, "biped_fall"), 0.08F, false,
				DarkSouls.rl("biped/living/fall"), (models) -> models.ENTITY_BIPED).register(builder);
		
		BIPED_DEATH = new DeathAnimation(DarkSouls.rl("biped_death"), 0.05F, DarkSouls.rl("biped/death/death"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(1.52F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get()))
				}).register(builder);
		
		BIPED_DEATH_SMASH = new DeathAnimation(DarkSouls.rl("biped_death_smash"), 0.05F, DarkSouls.rl("biped/death/smash"), (models) -> models.ENTITY_BIPED)
				.register(builder);
		BIPED_DEATH_FLY_FRONT = new DeathAnimation(DarkSouls.rl("biped_death_fly_front"), 0.05F, DarkSouls.rl("biped/death/fly_front"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(0.4F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get())),
						Event.create(0.8F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get()))
				}).register(builder);
		BIPED_DEATH_FLY_BACK = new DeathAnimation(DarkSouls.rl("biped_death_fly_back"), 0.05F, DarkSouls.rl("biped/death/fly_back"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(0.44F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get())),
						Event.create(0.8F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get()))
				}).register(builder);
		BIPED_DEATH_FLY_LEFT = new DeathAnimation(DarkSouls.rl("biped_death_fly_left"), 0.05F, DarkSouls.rl("biped/death/fly_left"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(0.48F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get())),
						Event.create(0.92F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get()))
				}).register(builder);
		BIPED_DEATH_FLY_RIGHT = new DeathAnimation(DarkSouls.rl("biped_death_fly_right"), 0.05F, DarkSouls.rl("biped/death/fly_right"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(0.48F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get())),
						Event.create(0.92F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get()))
				}).register(builder);
		BIPED_DEATH_BACKSTAB = new DeathAnimation(DarkSouls.rl("biped_death_backstab"), 0.05F,
				DarkSouls.rl("biped/death/backstab"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(0.44F, Side.CLIENT, (cap) ->
						{
							double yRot = Math.toRadians(MathUtils.toNormalRot(cap.getYRot()));
							float y = cap.getOriginalEntity().getBbHeight() * 0.5F;
							Vec3 pos = cap.getOriginalEntity().position().add(Math.cos(yRot), y, Math.sin(yRot));
							cap.makeImpactParticles(pos, false);
						}),
						Event.create(1.4F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get()))
				}).register(builder);
		BIPED_DEATH_PUNISH = new DeathAnimation(DarkSouls.rl("biped_death_punish"), 0.05F,
				DarkSouls.rl("biped/death/punish"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(0.44F, Side.CLIENT, (cap) ->
						{
							double yRot = Math.toRadians(MathUtils.toNormalRot(cap.getYRot()));
							float y = cap.getOriginalEntity().getBbHeight() * 0.5F;
							Vec3 pos = cap.getOriginalEntity().position().add(Math.cos(yRot), y, Math.sin(yRot));
							cap.makeImpactParticles(pos, false);
						}),
						Event.create(1.4F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get()))
				}).register(builder);
		
		BIPED_DIG = new StaticAnimation(new ResourceLocation(DarkSouls.MOD_ID, "biped_dig"), 0.2F, true,
				DarkSouls.rl("biped/living/dig"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.LAYER_PART, LayerPart.RIGHT).register(builder);
		BIPED_TOUCH_BONFIRE = new ActionAnimation(DarkSouls.rl("biped_touch_bonfire"), 0.5F,
				DarkSouls.rl("biped/living/touching_bonfire"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(0.5F, Side.CLIENT, Animations::teleportParticles),
						Event.create(1.0F, Side.CLIENT, Animations::teleportParticles),
						Event.create(1.5F, Side.CLIENT, Animations::teleportParticles),
						Event.create(2.0F, Side.CLIENT, Animations::teleportParticles),
						Event.create(2.5F, Side.CLIENT, Animations::teleportParticles),
						Event.create(3.0F, Side.CLIENT, Animations::teleportParticles),
						Event.create(3.5F, Side.CLIENT, Animations::teleportParticles),
						Event.create(4.0F, Side.CLIENT, Animations::teleportParticles),
						Event.create(2.5F, Side.SERVER, (cap) ->
								cap.playSound(ModSoundEvents.BONFIRE_TELEPORT.get())),
						Event.create(3.2F, Side.SERVER, (cap) ->
								cap.getOriginalEntity().teleportTo(cap.futureTeleport.x, cap.futureTeleport.y, cap.futureTeleport.z)),
				}).register(builder);

		BIPED_EAT = new MirrorAnimation(DarkSouls.rl("biped_eat"), 0.2F, true,
				DarkSouls.rl("biped/living/eat_r"), DarkSouls.rl("biped/living/eat_l"), (models) -> models.ENTITY_BIPED).register(builder);
		BIPED_DRINK = new MirrorAnimation(DarkSouls.rl("biped_drink"), 0.2F, true,
				DarkSouls.rl("biped/living/drink_r"), DarkSouls.rl("biped/living/drink_l"), (models) -> models.ENTITY_BIPED).register(builder);
		BIPED_CONSUME_SOUL = new MirrorAnimation(DarkSouls.rl("biped_consume_soul"), 0.2F, true, DarkSouls.rl("biped/living/consume_soul_r"),
				DarkSouls.rl("biped/living/consume_soul_l"), (models) -> models.ENTITY_BIPED).register(builder);
		
		BIPED_THROW = new ActionAnimation(DarkSouls.rl("biped_throw"), 0.2F, DarkSouls.rl("biped/combat/throw"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(0.6F, Side.SERVER, (cap) ->
						{
							if (cap.getHeldItemCapability(InteractionHand.MAIN_HAND) instanceof ThrowableCap throwable)
							{
								throwable.spawnProjectile(cap);
							}
						})
				})
				.register(builder);

		BIPED_BLOCK_HORIZONTAL = new AdaptableAnimation.Builder(DarkSouls.rl("biped_block"), 0.1F, true, (models) -> models.ENTITY_BIPED)
				.addEntry(LivingMotion.BLOCKING, DarkSouls.rl("biped/combat/block_mirror"), DarkSouls.rl("biped/combat/block"), false)
				.addEntry(LivingMotion.WALKING, DarkSouls.rl("biped/combat/block_walk_mirror"), DarkSouls.rl("biped/combat/block_walk"), true)
				.addEntry(LivingMotion.RUNNING, DarkSouls.rl("biped/combat/block_run_mirror"), DarkSouls.rl("biped/combat/block_run"), true)
				.addEntry(LivingMotion.KNEELING, DarkSouls.rl("biped/combat/block_mirror"), DarkSouls.rl("biped/combat/block"), true)
				.addEntry(LivingMotion.SNEAKING, DarkSouls.rl("biped/combat/block_mirror"), DarkSouls.rl("biped/combat/block"), true)
				.build().register(builder);

		BIPED_BLOCK_VERTICAL = new AdaptableAnimation.Builder(DarkSouls.rl("biped_block_vertical"), 0.1F, true, (models) -> models.ENTITY_BIPED)
				.addEntry(LivingMotion.BLOCKING, DarkSouls.rl("biped/combat/block_vertical_mirror"), DarkSouls.rl("biped/combat/block_vertical"), false)
				.addEntry(LivingMotion.WALKING, DarkSouls.rl("biped/combat/block_vertical_walk_mirror"), DarkSouls.rl("biped/combat/block_vertical_walk"), true)
				.addEntry(LivingMotion.RUNNING, DarkSouls.rl("biped/combat/block_vertical_run_mirror"), DarkSouls.rl("biped/combat/block_vertical_run"), true)
				.addEntry(LivingMotion.KNEELING, DarkSouls.rl("biped/combat/block_vertical_mirror"), DarkSouls.rl("biped/combat/block_vertical"), true)
				.addEntry(LivingMotion.SNEAKING, DarkSouls.rl("biped/combat/block_vertical_mirror"), DarkSouls.rl("biped/combat/block_vertical"), true)
				.build().register(builder);
		
		BIPED_BLOCK_TH_SWORD = new AdaptableAnimation.Builder(DarkSouls.rl("biped_block_th_sword"), 0.1F, true, (models) -> models.ENTITY_BIPED)
				.addEntry(LivingMotion.BLOCKING, DarkSouls.rl("biped/combat/block_th_sword"), false)
				.addEntry(LivingMotion.WALKING, DarkSouls.rl("biped/combat/block_th_sword"), true)
				.addEntry(LivingMotion.RUNNING, DarkSouls.rl("biped/combat/block_th_sword"), true)
				.addEntry(LivingMotion.KNEELING, DarkSouls.rl("biped/combat/block_th_sword"), true)
				.addEntry(LivingMotion.SNEAKING, DarkSouls.rl("biped/combat/block_th_sword"), true)
				.build().register(builder);
		
		BIPED_HIT_BLOCKED_LEFT = new BlockedAnimation(DarkSouls.rl("biped_hit_blocked_left"), 0.05F,
				DarkSouls.rl("biped/hit/blocked_left"), (models) -> models.ENTITY_BIPED)
				.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true).register(builder);
		BIPED_HIT_BLOCKED_RIGHT = new BlockedAnimation(DarkSouls.rl("biped_hit_blocked_right"), 0.05F,
				DarkSouls.rl("biped/hit/blocked_right"), (models) -> models.ENTITY_BIPED)
				.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true).register(builder);
		BIPED_HIT_BLOCKED_VERTICAL_LEFT = new BlockedAnimation(DarkSouls.rl("biped_hit_blocked_vertical_left"), 0.05F,
				DarkSouls.rl("biped/hit/blocked_vertical_left"), (models) -> models.ENTITY_BIPED)
				.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true).register(builder);
		BIPED_HIT_BLOCKED_VERTICAL_RIGHT = new BlockedAnimation(DarkSouls.rl("biped_hit_blocked_vertical_right"), 0.05F,
				DarkSouls.rl("biped/hit/blocked_vertical_right"), (models) -> models.ENTITY_BIPED)
				.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true).register(builder);
		BIPED_HIT_BLOCKED_TH_SWORD = new BlockedAnimation(DarkSouls.rl("biped_hit_blocked_th_sword"), 0.05F,
				DarkSouls.rl("biped/hit/blocked_th_sword"), (models) -> models.ENTITY_BIPED).register(builder);
		
		BIPED_HIT_BLOCKED_FLY_LEFT = new InvincibleAnimation(DarkSouls.rl("biped_hit_blocked_fly_left"), 0.05F,
				DarkSouls.rl("biped/hit/blocked_fly_left"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
					Event.create(0.48F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GENERIC_ROLL.get()))
				})
				.register(builder);
		BIPED_HIT_BLOCKED_FLY_RIGHT = new InvincibleAnimation(DarkSouls.rl("biped_hit_blocked_fly_right"), 0.05F,
				DarkSouls.rl("biped/hit/blocked_fly_right"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
					Event.create(0.48F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GENERIC_ROLL.get()))
				})
				.register(builder);
		BIPED_HIT_BLOCKED_VERTICAL_FLY_LEFT = new InvincibleAnimation(DarkSouls.rl("biped_hit_blocked_vertical_fly_left"), 0.05F,
				DarkSouls.rl("biped/hit/blocked_vertical_fly_left"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
					Event.create(0.48F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GENERIC_ROLL.get()))
				})
				.register(builder);
		BIPED_HIT_BLOCKED_VERTICAL_FLY_RIGHT = new InvincibleAnimation(DarkSouls.rl("biped_hit_blocked_vertical_fly_right"), 0.05F,
				DarkSouls.rl("biped/hit/blocked_vertical_fly_right"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
					Event.create(0.48F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GENERIC_ROLL.get()))
				})
				.register(builder);
		BIPED_HIT_BLOCKED_TH_SWORD_FLY = new InvincibleAnimation(DarkSouls.rl("biped_hit_blocked_th_sword_fly"), 0.05F,
				DarkSouls.rl("biped/hit/blocked_th_sword_fly"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
					Event.create(0.48F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GENERIC_ROLL.get()))
				})
				.register(builder);
		
		BIPED_DISARMED_LEFT = new PunishableAnimation(DarkSouls.rl("biped_disarm_shield_left"), 0.05F,
				DarkSouls.rl("biped/combat/disarmed_left"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
					Event.create(Event.ON_BEGIN, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.PLAYER_SHIELD_DISARMED.get()))
				})
				.register(builder);
		BIPED_DISARMED_RIGHT = new PunishableAnimation(DarkSouls.rl("biped_disarm_shield_right"), 0.05F,
				DarkSouls.rl("biped/combat/disarmed_right"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
					Event.create(Event.ON_BEGIN, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.PLAYER_SHIELD_DISARMED.get()))
				})
				.register(builder);
		
		BIPED_HORSEBACK_IDLE = new StaticAnimation(DarkSouls.rl("biped_horseback_idle"), 0.2F, true,
				DarkSouls.rl("biped/horseback/horseback_idle"), (models) -> models.ENTITY_BIPED)
				.register(builder);
		
		BIPED_IDLE_CROSSBOW = new StaticAnimation(DarkSouls.rl("biped_idle_crossbow"), 0.2F, true,
				DarkSouls.rl("biped/living/idle_crossbow"), (models) -> models.ENTITY_BIPED)
				.register(builder);
		BIPED_WALK_CROSSBOW = new MovementAnimation(DarkSouls.rl("biped_walk_crossbow"), 0.2F, true,
				DarkSouls.rl("biped/living/walk_crossbow"), (models) -> models.ENTITY_BIPED)
				.register(builder);

		BIPED_CROSSBOW_AIM = new AimingAnimation(DarkSouls.rl("biped_crossbow_aim"), 0.16F, true,
				DarkSouls.rl("biped/combat/crossbow_aim_mid"), DarkSouls.rl("biped/combat/crossbow_aim_up"),
				DarkSouls.rl("biped/combat/crossbow_aim_down"), (models) -> models.ENTITY_BIPED).register(builder);
		BIPED_CROSSBOW_SHOT = new ReboundAnimation(DarkSouls.rl("biped_crossbow_shot"), 0.16F, false,
				DarkSouls.rl("biped/combat/crossbow_shot_mid"), DarkSouls.rl("biped/combat/crossbow_shot_up"),
				DarkSouls.rl("biped/combat/crossbow_shot_down"), (models) -> models.ENTITY_BIPED).register(builder);

		BIPED_CROSSBOW_RELOAD = new StaticAnimation(DarkSouls.rl("biped_crossbow_reload"), 0.16F, false,
				DarkSouls.rl("biped/combat/crossbow_reload"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.LAYER_PART, LayerPart.UP).register(builder);

		BIPED_BOW_AIM = new AimingAnimation(DarkSouls.rl("biped_bow_aim"), 0.16F, true,
				DarkSouls.rl("biped/combat/bow_aim_mid"), DarkSouls.rl("biped/combat/bow_aim_up"),
				DarkSouls.rl("biped/combat/bow_aim_down"), (models) -> models.ENTITY_BIPED).register(builder);
		BIPED_BOW_REBOUND = new ReboundAnimation(DarkSouls.rl("biped_bow_rebound"), 0.04F, false,
				DarkSouls.rl("biped/combat/bow_shot_mid"), DarkSouls.rl("biped/combat/bow_shot_up"),
				DarkSouls.rl("biped/combat/bow_shot_down"), (models) -> models.ENTITY_BIPED).register(builder);

		BIPED_SPEER_AIM = new AimingAnimation(DarkSouls.rl("biped_speer_aim"), 0.16F, false,
				DarkSouls.rl("biped/combat/javelin_aim_mid"), DarkSouls.rl("biped/combat/javelin_aim_up"),
				DarkSouls.rl("biped/combat/javelin_aim_down"), (models) -> models.ENTITY_BIPED).register(builder);
		BIPED_SPEER_REBOUND = new ReboundAnimation(DarkSouls.rl("biped_speer_rebound"), 0.08F, false,
				DarkSouls.rl("biped/combat/javelin_throw_mid"), DarkSouls.rl("biped/combat/javelin_throw_up"),
				DarkSouls.rl("biped/combat/javelin_throw_down"), (models) -> models.ENTITY_BIPED).register(builder);

		BIPED_HIT_LIGHT_FRONT = new HitAnimation(DarkSouls.rl("biped_hit_light_front"), 0.05F,
				DarkSouls.rl("biped/hit/light_front"), (models) -> models.ENTITY_BIPED)
				.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true).register(builder);
		BIPED_HIT_LIGHT_LEFT = new HitAnimation(DarkSouls.rl("biped_hit_light_left"), 0.05F,
				DarkSouls.rl("biped/hit/light_left"), (models) -> models.ENTITY_BIPED)
				.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true).register(builder);
		BIPED_HIT_LIGHT_RIGHT = new HitAnimation(DarkSouls.rl("biped_hit_light_right"), 0.05F,
				DarkSouls.rl("biped/hit/light_right"), (models) -> models.ENTITY_BIPED)
				.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true).register(builder);
		BIPED_HIT_LIGHT_BACK = new HitAnimation(DarkSouls.rl("biped_hit_light_back"), 0.05F,
				DarkSouls.rl("biped/hit/light_back"), (models) -> models.ENTITY_BIPED)
				.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true).register(builder);
		BIPED_HIT_HEAVY_FRONT = new HitAnimation(DarkSouls.rl("biped_hit_heavy_front"), 0.05F,
				DarkSouls.rl("biped/hit/heavy_front"), (models) -> models.ENTITY_BIPED)
				.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true).register(builder);
		BIPED_HIT_HEAVY_BACK = new HitAnimation(DarkSouls.rl("biped_hit_heavy_back"), 0.05F,
				DarkSouls.rl("biped/hit/heavy_back"), (models) -> models.ENTITY_BIPED)
				.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true).register(builder);
		BIPED_HIT_HEAVY_LEFT = new HitAnimation(DarkSouls.rl("biped_hit_heavy_left"), 0.05F,
				DarkSouls.rl("biped/hit/heavy_left"), (models) -> models.ENTITY_BIPED)
				.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true).register(builder);
		BIPED_HIT_HEAVY_RIGHT = new HitAnimation(DarkSouls.rl("biped_hit_heavy_right"), 0.05F,
				DarkSouls.rl("biped/hit/heavy_right"), (models) -> models.ENTITY_BIPED)
				.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true).register(builder);
		
		BIPED_HORSEBACK_HIT_LIGHT_FRONT = new HitAnimation(DarkSouls.rl("biped_horseback_hit_light_front"), 0.05F,
				DarkSouls.rl("biped/hit/horseback_light_front"), (models) -> models.ENTITY_BIPED)
				.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true).register(builder);
		BIPED_HORSEBACK_HIT_LIGHT_LEFT = new HitAnimation(DarkSouls.rl("biped_horseback_hit_light_left"), 0.05F,
				DarkSouls.rl("biped/hit/horseback_light_left"), (models) -> models.ENTITY_BIPED)
				.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true).register(builder);
		BIPED_HORSEBACK_HIT_LIGHT_RIGHT = new HitAnimation(DarkSouls.rl("biped_horseback_hit_light_right"), 0.05F,
				DarkSouls.rl("biped/hit/horseback_light_right"), (models) -> models.ENTITY_BIPED)
				.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true).register(builder);
		BIPED_HORSEBACK_HIT_LIGHT_BACK = new HitAnimation(DarkSouls.rl("biped_horseback_hit_light_back"), 0.05F,
				DarkSouls.rl("biped/hit/horseback_light_back"), (models) -> models.ENTITY_BIPED)
				.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true).register(builder);
		BIPED_HORSEBACK_HIT_HEAVY_FRONT = new HitAnimation(DarkSouls.rl("biped_horseback_hit_heavy_front"), 0.05F,
				DarkSouls.rl("biped/hit/horseback_heavy_front"), (models) -> models.ENTITY_BIPED)
				.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true).register(builder);
		BIPED_HORSEBACK_HIT_HEAVY_BACK = new HitAnimation(DarkSouls.rl("biped_horseback_hit_heavy_back"), 0.05F,
				DarkSouls.rl("biped/hit/horseback_heavy_back"), (models) -> models.ENTITY_BIPED)
				.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true).register(builder);
		BIPED_HORSEBACK_HIT_HEAVY_LEFT = new HitAnimation(DarkSouls.rl("biped_horseback_hit_heavy_left"), 0.05F,
				DarkSouls.rl("biped/hit/horseback_heavy_left"), (models) -> models.ENTITY_BIPED)
				.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true).register(builder);
		BIPED_HORSEBACK_HIT_HEAVY_RIGHT = new HitAnimation(DarkSouls.rl("biped_horseback_hit_heavy_right"), 0.05F,
				DarkSouls.rl("biped/hit/horseback_heavy_right"), (models) -> models.ENTITY_BIPED)
				.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true).register(builder);
		
		BIPED_HIT_SMASH = new InvincibleAnimation(DarkSouls.rl("biped_hit_smash"), 0.05F,
				DarkSouls.rl("biped/hit/smash"), (models) -> models.ENTITY_BIPED).register(builder);
		BIPED_HIT_FLY_FRONT = new InvincibleAnimation(DarkSouls.rl("biped_hit_fly_front"), 0.05F,
				DarkSouls.rl("biped/hit/fly"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
						{
								Event.create(0.4F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get())),
								Event.create(0.8F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get()))
						}).register(builder);
		BIPED_HIT_FLY_BACK = new InvincibleAnimation(DarkSouls.rl("biped_hit_fly_back"), 0.05F,
				DarkSouls.rl("biped/hit/fly_back"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
						{
								Event.create(0.44F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get())),
								Event.create(0.8F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get()))
						}).register(builder);
		BIPED_HIT_FLY_LEFT = new InvincibleAnimation(DarkSouls.rl("biped_hit_fly_left"), 0.05F,
				DarkSouls.rl("biped/hit/fly_left"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
						{
								Event.create(0.48F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get())),
								Event.create(0.92F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get()))
						}).register(builder);
		BIPED_HIT_FLY_RIGHT = new InvincibleAnimation(DarkSouls.rl("biped_hit_fly_right"), 0.05F,
				DarkSouls.rl("biped/hit/fly_right"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
						{
								Event.create(0.48F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get())),
								Event.create(0.92F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get()))
						}).register(builder);
		BIPED_HIT_LAND_HEAVY = new HitAnimation(DarkSouls.rl("biped_hit_land_heavy"), 0.05F, DarkSouls.rl("biped/hit/land_heavy"), (models) -> models.ENTITY_BIPED)
				.register(builder);
		
		BIPED_HIT_BACKSTAB = new InvincibleAnimation(DarkSouls.rl("biped_hit_backstab"), 0.05F,
				DarkSouls.rl("biped/hit/backstab"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.DEATH_ANIMATION, DarkSouls.rl("biped_death_backstab"))
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(0.44F, Side.CLIENT, (cap) ->
						{
							double yRot = Math.toRadians(MathUtils.toNormalRot(cap.getYRot()));
							float y = cap.getOriginalEntity().getBbHeight() * 0.5F;
							Vec3 pos = cap.getOriginalEntity().position().add(Math.cos(yRot), y, Math.sin(yRot));
							cap.makeImpactParticles(pos, false);
						}),
						Event.create(1.4F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get()))
				}).register(builder);
		
		BIPED_HIT_PUNISH = new InvincibleAnimation(DarkSouls.rl("biped_hit_punish"), 0.05F,
				DarkSouls.rl("biped/hit/punish"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.DEATH_ANIMATION, DarkSouls.rl("biped_death_punish"))
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
								Event.create(0.44F, Side.CLIENT, (cap) ->
								{
									double yRot = Math.toRadians(MathUtils.toNormalRot(cap.getYRot()));
									float y = cap.getOriginalEntity().getBbHeight() * 0.5F;
									Vec3 pos = cap.getOriginalEntity().position().add(Math.cos(yRot), y, Math.sin(yRot));
									cap.makeImpactParticles(pos, false);
								}),
								Event.create(1.4F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get()))
				}).register(builder);
		
		BIPED_ROLL = new DodgingAnimation(DarkSouls.rl("biped_roll"), 0.05F, DarkSouls.rl("biped/combat/roll"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{ Event.create(0.28F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.GENERIC_ROLL.get())) }).register(builder);
		BIPED_FAT_ROLL = new DodgingAnimation(DarkSouls.rl("biped_fat_roll"), 0.05F, DarkSouls.rl("biped/combat/fat_roll"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(0.48F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get())),
						Event.create(0.48F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 10, 0.25F))
				}).register(builder);
		BIPED_ROLL_TOO_FAT = new ActionAnimation(DarkSouls.rl("biped_roll_too_fat"), 0.05F,
				DarkSouls.rl("biped/combat/roll_too_fat"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
						{
							Event.create(0.4F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get())),
							Event.create(0.4F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 10, 0.25F))
						}).register(builder);
		BIPED_ROLL_BACK = new DodgingAnimation(DarkSouls.rl("biped_roll_back"), 0.05F, DarkSouls.rl("biped/combat/roll_back"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{ Event.create(0.28F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GENERIC_ROLL.get())) }).register(builder);
		BIPED_ROLL_LEFT = new DodgingAnimation(DarkSouls.rl("biped_roll_left"), 0.05F, true, DarkSouls.rl("biped/combat/roll_left"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{ Event.create(0.28F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GENERIC_ROLL.get())) }).register(builder);
		BIPED_ROLL_RIGHT = new DodgingAnimation(DarkSouls.rl("biped_roll_right"), 0.05F, true, DarkSouls.rl("biped/combat/roll_right"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{ Event.create(0.28F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GENERIC_ROLL.get())) }).register(builder);
		BIPED_JUMP_BACK = new DodgingAnimation(DarkSouls.rl("biped_jump_back"), 0.08F, DarkSouls.rl("biped/combat/jump_back"), (models) -> models.ENTITY_BIPED)
				.register(builder);
		
		// Miracle
		BIPED_CAST_MIRACLE_HEAL = new ActionAnimation(DarkSouls.rl("biped_cast_miracle_heal"), 0.5F,
				DarkSouls.rl("biped/combat/cast_miracle"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
						{
								Event.create(Event.ON_BEGIN, Side.SERVER, (cap) ->
										LightSource.setLightSource(cap.getLevel(), cap.getOriginalEntity().blockPosition(), 15, 4.5F)),
								Event.create(Event.ON_BEGIN, Side.CLIENT, (cap) ->
								{
									cap.playSound(ModSoundEvents.MIRACLE_USE_PRE.get());
									cap.getLevel().addAlwaysVisibleParticle(new EntityboundParticleOptions(ModParticles.MIRACLE_GLOW.get(), cap.getOriginalEntity().getId()), cap.getX(), cap.getY() + 1, cap.getZ(), 0, 0, 0);
								}),
								Event.create(2.5F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.MIRACLE_USE.get())),
								Event.create(2.5F, Side.CLIENT, (cap) ->
										cap.getLevel().addAlwaysVisibleParticle(ModParticles.MEDIUM_MIRACLE_CIRCLE.get(), cap.getX(), cap.getY() + 0.1F, cap.getZ(), 0, 0, 0)),
								Event.create(2.6F, Side.SERVER, (cap) ->
								{
									List<Entity> targets = cap.getLevel().getEntities(null, new AABB(cap.getX() - 3.0F, cap.getY() - 3.0F, cap.getZ() - 3.0F, cap.getX() + 3.0F, cap.getY() + 3.0F, cap.getZ() + 3.0F));
									for (Entity target : targets)
									{
										if (target instanceof LivingEntity livingTarget)
										{
											livingTarget.heal(300 * cap.getSpellBuff());
										}
									}
								})
						}).register(builder);
		
		BIPED_CAST_MIRACLE_HEAL_AID = new ActionAnimation(DarkSouls.rl("biped_cast_miracle_heal_aid"), 0.5F,
				DarkSouls.rl("biped/combat/cast_miracle_fast"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
						{
							Event.create(Event.ON_BEGIN, Side.SERVER, (cap) ->
									LightSource.setLightSource(cap.getLevel(), cap.getOriginalEntity().blockPosition(), 15, 3.0F)),
							Event.create(Event.ON_BEGIN, Side.CLIENT, (cap) ->
							{
								cap.playSound(ModSoundEvents.MIRACLE_USE_PRE.get());
								cap.getLevel().addAlwaysVisibleParticle(new EntityboundParticleOptions(ModParticles.FAST_MIRACLE_GLOW.get(), cap.getOriginalEntity().getId()), cap.getX(), cap.getY() + 1, cap.getZ(), 0, 0, 0);
							}),
							Event.create(1.0F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.MIRACLE_USE.get())),
							Event.create(1.0F, Side.CLIENT, (cap) ->
									cap.getLevel().addAlwaysVisibleParticle(ModParticles.TINY_MIRACLE_CIRCLE.get(), cap.getX(), cap.getY() + 0.1F, cap.getZ(), 0, 0, 0)),
							Event.create(1.1F, Side.SERVER, (cap) ->
									cap.getOriginalEntity().heal(150 * cap.getSpellBuff()))
						}).register(builder);
		
		BIPED_CAST_MIRACLE_HOMEWARD = new ActionAnimation(DarkSouls.rl("biped_cast_miracle_homeward"), 0.5F,
				DarkSouls.rl("biped/combat/cast_miracle"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
						{
							Event.create(Event.ON_BEGIN, Side.SERVER, (cap) ->
									LightSource.setLightSource(cap.getLevel(), cap.getOriginalEntity().blockPosition(), 15, 5.0F)),
							Event.create(Event.ON_BEGIN, Side.CLIENT, (cap) ->
							{
								cap.playSound(ModSoundEvents.MIRACLE_USE_PRE.get());
								cap.getLevel().addAlwaysVisibleParticle(new EntityboundParticleOptions(ModParticles.FAST_MIRACLE_GLOW.get(), cap.getOriginalEntity().getId()), cap.getX(), cap.getY() + 1, cap.getZ(), 0, 0, 0);
							}),
							Event.create(2.5F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.MIRACLE_USE.get())),
							Event.create(2.5F, Side.CLIENT, (cap) ->
									cap.getLevel().addAlwaysVisibleParticle(ModParticles.TINY_MIRACLE_CIRCLE.get(), cap.getX(), cap.getY() + 0.1F, cap.getZ(), 0, 0, 0)),
							Event.create(3.5F, Side.SERVER, (cap) ->
							{
								if (cap.getOriginalEntity() instanceof ServerPlayer)
								{
									BlockPos pos = ((ServerPlayer)cap.getOriginalEntity()).getRespawnPosition();
									assert pos != null;
									cap.getOriginalEntity().teleportTo(pos.getX(), pos.getY(), pos.getZ());
								}
							})
						}).register(builder);
		
		BIPED_CAST_MIRACLE_FORCE = new ActionAnimation(DarkSouls.rl("biped_cast_miracle_force"), 0.3F,
				DarkSouls.rl("biped/combat/cast_miracle_force"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
						{
							Event.create(Event.ON_BEGIN, Side.SERVER, (cap) ->
									LightSource.setLightSource(cap.getLevel(), cap.getOriginalEntity().blockPosition(), 15, 1.85F)),
							Event.create(0.56F, Side.CLIENT, (cap) ->
									cap.getLevel().addAlwaysVisibleParticle(ModParticles.FORCE.get(), cap.getX(), cap.getY() + 1.0F, cap.getZ(), 0, 0, 0)),
							Event.create(0.56F, Side.SERVER, (cap) ->
									cap.playSound(ModSoundEvents.MIRACLE_FORCE.get())),
							Event.create(0.6F, Side.SERVER, (cap) ->
							{
								List<Entity> targets = cap.getLevel().getEntities(cap.getOriginalEntity(), new AABB(cap.getX() - 3.0F, cap.getY() - 3.0F, cap.getZ() - 3.0F, cap.getX() + 3.0F, cap.getY() + 3.0F, cap.getZ() + 3.0F));
								for (Entity target : targets)
								{
									if (target instanceof LivingEntity)
									{
										LivingCap<?> targetCap = (LivingCap<?>)target.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
										if (targetCap != null)
										{
											DamageSourceExtended dmgSource = cap.getOriginalEntity() instanceof Player ?
													ExtendedDamageSource.causePlayerDamage((Player)cap.getOriginalEntity(), cap.getOriginalEntity().position(),
															StunType.FLY, Deflection.NONE, 0, 0, Damages.create().put(CoreDamageType.PHYSICAL, 0))
													: ExtendedDamageSource.causeMobDamage(cap.getOriginalEntity(), cap.getOriginalEntity().position(),
															StunType.FLY, Deflection.NONE, 0, 0, Damages.create().put(CoreDamageType.PHYSICAL, 0));
											target.hurt(dmgSource, 0);
										}
										else cap.knockBackEntity(target, 0.5F);
									}
									else cap.knockBackEntity(target, 1.0F);
								}
							})
						}).register(builder);

		Event[] LIGHTNING_SPEAR_EVENTS = new Event[]
				{
						Event.create(Event.ON_BEGIN, Side.SERVER, (cap) ->
								LightSource.setLightSource(cap.getLevel(), cap.getOriginalEntity().blockPosition(), 15, 1.2F)),
						Event.create(Event.ON_BEGIN, Side.CLIENT, (cap) ->
						{
							cap.playSound(ModSoundEvents.LIGHTNING_SPEAR_APPEAR.get());
							cap.getLevel().addAlwaysVisibleParticle(new EntityboundParticleOptions(ModParticles.LIGHTNING_SPEAR.get(), cap.getOriginalEntity().getId()), cap.getX(), cap.getY() + 1, cap.getZ(), 0, 0, 0);
						}),
						Event.create(0.9F, Side.SERVER, (cap) ->
						{
							LightningSpear spear = LightningSpear.lightningSpear(cap);
							spear.shootFromRotation(cap.getOriginalEntity(), cap.getXRot(), cap.getYRot(), 0.0F, 2.0F, 0.0F);
							cap.getLevel().addFreshEntity(spear);
							cap.playSound(ModSoundEvents.LIGHTNING_SPEAR_SHOT.get());
						})
				};
		
		BIPED_CAST_MIRACLE_LIGHTNING_SPEAR = new ActionAnimation(DarkSouls.rl("biped_cast_miracle_lightning_spear"), 0.3F,
				DarkSouls.rl("biped/combat/cast_miracle_spear"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, LIGHTNING_SPEAR_EVENTS).register(builder);
		HORSEBACK_CAST_MIRACLE_LIGHTNING_SPEAR = new ActionAnimation(DarkSouls.rl("horseback_cast_miracle_lightning_spear"), 0.3F,
				DarkSouls.rl("biped/combat/horseback_cast_miracle_spear"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, LIGHTNING_SPEAR_EVENTS).register(builder);

		Event[] GREAT_LIGHTNING_SPEAR_EVENTS = new Event[]
				{
						Event.create(Event.ON_BEGIN, Side.SERVER, (cap) ->
								LightSource.setLightSource(cap.getLevel(), cap.getOriginalEntity().blockPosition(), 15, 1.2F)),
						Event.create(Event.ON_BEGIN, Side.SERVER, (cap) ->
								cap.playSound(ModSoundEvents.LIGHTNING_SPEAR_APPEAR.get())),
						Event.create(Event.ON_BEGIN, Side.CLIENT, (cap) ->
								cap.getLevel().addAlwaysVisibleParticle(new EntityboundParticleOptions(ModParticles.GREAT_LIGHTNING_SPEAR.get(), cap.getOriginalEntity().getId()), cap.getX(), cap.getY() + 1, cap.getZ(), 0, 0, 0)),
						Event.create(0.9F, Side.SERVER, (cap) ->
						{
							LightningSpear spear = LightningSpear.greatLightningSpear(cap);
							spear.shootFromRotation(cap.getOriginalEntity(), cap.getXRot(), cap.getYRot(), 0.0F, 2.0F, 0.0F);
							cap.getLevel().addFreshEntity(spear);
							cap.playSound(ModSoundEvents.LIGHTNING_SPEAR_SHOT.get());
						})
				};
		
		BIPED_CAST_MIRACLE_GREAT_LIGHTNING_SPEAR = new ActionAnimation(DarkSouls.rl("biped_cast_miracle_great_lightning_spear"), 0.3F,
				DarkSouls.rl("biped/combat/cast_miracle_spear"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, GREAT_LIGHTNING_SPEAR_EVENTS).register(builder);
		HORSEBACK_CAST_MIRACLE_GREAT_LIGHTNING_SPEAR = new ActionAnimation(DarkSouls.rl("horseback_cast_miracle_great_lightning_spear"), 0.3F,
				DarkSouls.rl("biped/combat/horseback_cast_miracle_spear"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, GREAT_LIGHTNING_SPEAR_EVENTS).register(builder);
		
		// Big Weapon
		BIPED_HOLDING_BIG_WEAPON = new MirrorAnimation(DarkSouls.rl("biped_holding_big_weapon"), 0.2F, true, true,
				DarkSouls.rl("biped/living/holding_big_weapon_r"),
				DarkSouls.rl("biped/living/holding_big_weapon_l"), (models) -> models.ENTITY_BIPED)
				.register(builder);
		
		// Horseback Attacks
		HORSEBACK_LIGHT_ATTACK = new AttackAnimation[]
		{
					new AttackAnimation(DarkSouls.rl("horseback_light_attack_1"), AttackType.LIGHT, 0.5F, 0.0F, 0.2F, 0.52F, 1.6F, "Tool_R",
							DarkSouls.rl("biped/combat/horseback_light_attack_1"), (models) -> models.ENTITY_BIPED)
							.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
							.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM).register(builder),
					new AttackAnimation(DarkSouls.rl("horseback_light_attack_2"), AttackType.LIGHT, 0.5F, 0.0F, 0.12F, 0.48F, 1.6F, "Tool_R",
							DarkSouls.rl("biped/combat/horseback_light_attack_2"), (models) -> models.ENTITY_BIPED)
							.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
							.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM).register(builder)
		};
		
		// Parries
		SHIELD_PARRY_LEFT = new ParryAnimation(DarkSouls.rl("shield_parry"), 0.1F, 0.32F, 0.8F, "Tool_L",
				DarkSouls.rl("biped/combat/shield_parry"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(0.32F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.FIST_SWING.get()))
				})
				.register(builder);
		SHIELD_PARRY_RIGHT = new ParryAnimation(DarkSouls.rl("shield_parry_mirrored"), 0.1F, 0.32F, 0.8F, "Tool_R",
				DarkSouls.rl("biped/combat/shield_parry_mirrored"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(0.32F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.FIST_SWING.get()))
				})
				.register(builder);
		BUCKLER_PARRY_LEFT = new ParryAnimation(DarkSouls.rl("buckler_parry"), 0.15F, 0.32F, 0.8F, "Tool_L",
				DarkSouls.rl("biped/combat/buckler_parry"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(0.32F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.FIST_SWING.get()))
				})
				.register(builder);
		BUCKLER_PARRY_RIGHT = new ParryAnimation(DarkSouls.rl("buckler_parry_mirrored"), 0.15F, 0.32F, 0.8F, "Tool_R",
				DarkSouls.rl("biped/combat/buckler_parry_mirrored"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(0.32F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.FIST_SWING.get()))
				})
				.register(builder);
		
		// Backstabs
		BACKSTAB_THRUST = new BackstabCheckAnimation(DarkSouls.rl("backstab_thrust_check"), AttackType.BACKSTAB, 0.2F, 0.0F, 0.36F, 0.64F, 1.44F, false, "Tool_R",
				DarkSouls.rl("biped/combat/backstab_thrust_check"), (models) -> models.ENTITY_BIPED,
				new InvincibleAnimation(DarkSouls.rl("backstab_thrust"), 0.05F, DarkSouls.rl("biped/combat/backstab_thrust"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(Event.ON_BEGIN, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.PLAYER_SHIELD_DISARMED.get())),
						Event.create(Event.ON_BEGIN, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCamForEntity(cap.getOriginalEntity(), 10, 0.5F)),
						Event.create(1.08F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GENERIC_KICK.get())),
						Event.create(1.08F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.SWORD_PULLOUT.get())),
						Event.create(1.3F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCamForEntity(cap.getOriginalEntity(), 10, 0.5F)),
				}).register(builder)).register(builder);
		BACKSTAB_STRIKE = new BackstabCheckAnimation(DarkSouls.rl("backstab_strike_check"), AttackType.BACKSTAB, 0.2F, 0.0F, 0.4F, 0.8F, 1.44F, true, "Tool_R",
				DarkSouls.rl("biped/combat/backstab_strike_check"), (models) -> models.ENTITY_BIPED,
				new CriticalHitAnimation(DarkSouls.rl("backstab_strike"), 0.05F, 1.24F, DarkSouls.rl("biped/combat/backstab_strike"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(Event.ON_BEGIN, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.PLAYER_SHIELD_DISARMED.get())),
						Event.create(Event.ON_BEGIN, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCamForEntity(cap.getOriginalEntity(), 10, 0.5F)),
						Event.create(1.24F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.PLAYER_SHIELD_DISARMED.get())),
						Event.create(1.3F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCamForEntity(cap.getOriginalEntity(), 10, 0.5F)),
				}).register(builder)).register(builder);
		
		// Punishes
		PUNISH_THRUST = new PunishCheckAnimation(DarkSouls.rl("punish_thrust_check"), AttackType.PUNISH, 0.2F, 0.0F, 0.36F, 0.64F, 1.44F, false, "Tool_R",
				DarkSouls.rl("biped/combat/backstab_thrust_check"), (models) -> models.ENTITY_BIPED,
				new InvincibleAnimation(DarkSouls.rl("punish_thrust"), 0.05F, DarkSouls.rl("biped/combat/backstab_thrust"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(Event.ON_BEGIN, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.PLAYER_SHIELD_DISARMED.get())),
						Event.create(Event.ON_BEGIN, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GENERIC_KILL_CHANCE.get())),
						Event.create(Event.ON_BEGIN, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCamForEntity(cap.getOriginalEntity(), 10, 0.5F)),
						Event.create(1.08F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GENERIC_KICK.get())),
						Event.create(1.08F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.SWORD_PULLOUT.get())),
						Event.create(1.3F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCamForEntity(cap.getOriginalEntity(), 10, 0.5F)),
				}).register(builder)).register(builder);
		PUNISH_STRIKE = new PunishCheckAnimation(DarkSouls.rl("punish_strike_check"), AttackType.PUNISH, 0.2F, 0.0F, 0.4F, 0.8F, 1.44F, true, "Tool_R",
				DarkSouls.rl("biped/combat/backstab_strike_check"), (models) -> models.ENTITY_BIPED,
				new CriticalHitAnimation(DarkSouls.rl("punish_strike"), 0.05F, 1.24F, DarkSouls.rl("biped/combat/backstab_strike"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(Event.ON_BEGIN, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.PLAYER_SHIELD_DISARMED.get())),
						Event.create(Event.ON_BEGIN, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GENERIC_KILL_CHANCE.get())),
						Event.create(Event.ON_BEGIN, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCamForEntity(cap.getOriginalEntity(), 10, 0.5F)),
						Event.create(1.24F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.PLAYER_SHIELD_DISARMED.get())),
						Event.create(1.3F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCamForEntity(cap.getOriginalEntity(), 10, 0.5F)),
				}).register(builder)).register(builder);
		
		// Thrusting Sword
		THRUSTING_SWORD_LIGHT_ATTACK = new AttackAnimation(DarkSouls.rl("thrusting_sword_light_attack"), AttackType.LIGHT, 0.2F, 0.0F, 0.28F, 0.4F, 1.2F, "Tool_R",
				DarkSouls.rl("biped/combat/thrusting_sword_la"), (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
				.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
				.addProperty(AttackProperty.STUN_TYPE, StunType.LIGHT)
				.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_THRUST)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 37)
				.addProperty(AttackProperty.STAMINA_USAGE, 20)
				.addProperty(AttackProperty.POISE_DAMAGE, 15)
				.register(builder);
		THRUSTING_SWORD_HEAVY_ATTACK = new AttackAnimation[]
				{
						new AttackAnimation(DarkSouls.rl("thrusting_sword_heavy_attack_1"), AttackType.HEAVY, 0.2F, 0.0F, 0.72F, 0.84F, 1.6F, "Tool_R",
								DarkSouls.rl("biped/combat/thrusting_sword_ha_1"), (models) -> models.ENTITY_BIPED)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 37)
								.addProperty(AttackProperty.STAMINA_USAGE, 45)
								.addProperty(AttackProperty.POISE_DAMAGE, 15)
								.register(builder),
						new AttackAnimation(DarkSouls.rl("thrusting_sword_heavy_attack_2"), AttackType.HEAVY, 0.2F, 0.0F, 0.68F, 0.8F, 2.0F, "Tool_R",
								DarkSouls.rl("biped/combat/thrusting_sword_ha_2"), (models) -> models.ENTITY_BIPED)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 37)
								.addProperty(AttackProperty.STAMINA_USAGE, 45)
								.addProperty(AttackProperty.POISE_DAMAGE, 15)
								.register(builder)
				};
		THRUSTING_SWORD_DASH_ATTACK = new AttackAnimation(DarkSouls.rl("thrusting_sword_dash_attack"), AttackType.DASH, 0.2F, 0.0F, 0.2F, 0.36F, 1.2F, "Tool_R",
				DarkSouls.rl("biped/combat/thrusting_sword_da"), (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
				.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
				.addProperty(AttackProperty.STUN_TYPE, StunType.LIGHT)
				.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_THRUST)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 37)
				.addProperty(AttackProperty.STAMINA_USAGE, 50)
				.addProperty(AttackProperty.POISE_DAMAGE, 15)
				.register(builder);
		THRUSTING_SWORD_TH_LIGHT_ATTACK = new AttackAnimation(DarkSouls.rl("thrusting_sword_th_light_attack"), AttackType.LIGHT, 0.2F, 0.0F, 0.28F, 0.4F, 1.2F, "Tool_R",
				DarkSouls.rl("biped/combat/thrusting_sword_th_la"), (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
				.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
				.addProperty(AttackProperty.STUN_TYPE, StunType.LIGHT)
				.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_THRUST)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 37)
				.addProperty(AttackProperty.STAMINA_USAGE, 28)
				.addProperty(AttackProperty.POISE_DAMAGE, 15)
				.register(builder);
		THRUSTING_SWORD_TH_HEAVY_ATTACK = new AttackAnimation[]
				{
						new AttackAnimation(DarkSouls.rl("thrusting_sword_th_heavy_attack_1"), AttackType.HEAVY, 0.2F, 0.0F, 0.72F, 0.84F, 1.6F, "Tool_R",
								DarkSouls.rl("biped/combat/thrusting_sword_th_ha_1"), (models) -> models.ENTITY_BIPED)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 37)
								.addProperty(AttackProperty.STAMINA_USAGE, 60)
								.addProperty(AttackProperty.POISE_DAMAGE, 15)
								.register(builder),
						new AttackAnimation(DarkSouls.rl("thrusting_sword_th_heavy_attack_2"), AttackType.HEAVY, 0.2F, 0.0F, 0.68F, 0.8F, 2.0F, "Tool_R",
								DarkSouls.rl("biped/combat/thrusting_sword_th_ha_2"), (models) -> models.ENTITY_BIPED)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 37)
								.addProperty(AttackProperty.STAMINA_USAGE, 76)
								.addProperty(AttackProperty.POISE_DAMAGE, 15)
								.register(builder)
				};
		THRUSTING_SWORD_TH_DASH_ATTACK = new AttackAnimation(DarkSouls.rl("thrusting_sword_th_dash_attack"), AttackType.DASH, 0.2F, 0.0F, 0.2F, 0.36F, 1.2F, "Tool_R",
				DarkSouls.rl("biped/combat/thrusting_sword_th_da"), (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
				.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
				.addProperty(AttackProperty.STUN_TYPE, StunType.LIGHT)
				.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_THRUST)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 37)
				.addProperty(AttackProperty.STAMINA_USAGE, 70)
				.addProperty(AttackProperty.POISE_DAMAGE, 15)
				.register(builder);
		
		// Greatsword
		GREATSWORD_LIGHT_ATTACK = new AttackAnimation[]
		{
				new AttackAnimation(DarkSouls.rl("greatsword_light_attack_1"), AttackType.LIGHT, 0.3F, 0.0F, 0.44F, 0.68F, 2.4F, "Tool_R",
						DarkSouls.rl("biped/combat/greatsword_light_attack_1"), (models) -> models.ENTITY_BIPED)
						.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
						.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
						.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
						.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
						.addProperty(AttackProperty.STAMINA_DAMAGE, 30)
						.addProperty(AttackProperty.STAMINA_USAGE, 56)
						.addProperty(AttackProperty.POISE_DAMAGE, 23)
						.register(builder),
				new AttackAnimation(DarkSouls.rl("greatsword_light_attack_2"), AttackType.LIGHT, 0.3F, 0.0F, 0.32F, 0.68F, 2.24F, "Tool_R",
						DarkSouls.rl("biped/combat/greatsword_light_attack_2"), (models) -> models.ENTITY_BIPED)
						.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
						.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
						.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
						.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
						.addProperty(AttackProperty.STAMINA_DAMAGE, 30)
						.addProperty(AttackProperty.STAMINA_USAGE, 56)
						.addProperty(AttackProperty.POISE_DAMAGE, 23)
						.register(builder)
		};
		GREATSWORD_THRUST = new AttackAnimation(DarkSouls.rl("greatsword_thrust"), AttackType.HEAVY, 0.3F, 0.0F, 0.28F, 0.68F, 1.6F, "Tool_R",
				DarkSouls.rl("biped/combat/greatsword_heavy_attack"), (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
				.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_THRUST)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 36)
				.addProperty(AttackProperty.STAMINA_USAGE, 70)
				.addProperty(AttackProperty.POISE_DAMAGE, 23)
				.register(builder);
		GREATSWORD_UPWARD_SLASH = new AttackAnimation(DarkSouls.rl("greatsword_upward_slash"), AttackType.HEAVY, 0.3F, 0.0F, 0.28F, 0.56F, 2.4F, "Tool_R",
				DarkSouls.rl("biped/combat/greatsword_upward_slash"), (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
				.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 36)
				.addProperty(AttackProperty.STAMINA_USAGE, 50)
				.addProperty(AttackProperty.POISE_DAMAGE, 23)
				.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
				.register(builder);
		GREATSWORD_STYLISH_THRUST = new AttackAnimation(DarkSouls.rl("greatsword_stylish_thrust"), AttackType.HEAVY, 0.3F, 0.0F, 0.64F, 0.76F, 2.8F, "Tool_R",
				DarkSouls.rl("biped/combat/greatsword_stylish_thrust"), (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
				.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_THRUST)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 36)
				.addProperty(AttackProperty.STAMINA_USAGE, 70)
				.addProperty(AttackProperty.POISE_DAMAGE, 23)
				.register(builder);
		GREATSWORD_DASH_ATTACK = new AttackAnimation(DarkSouls.rl("greatsword_dash_attack"), AttackType.DASH, 0.1F, 0.0F, 0.4F, 0.72F, 1.6F, "Tool_R",
				DarkSouls.rl("biped/combat/greatsword_dash_attack"), (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
				.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 30)
				.addProperty(AttackProperty.STAMINA_USAGE, 40)
				.addProperty(AttackProperty.POISE_DAMAGE, 23)
				.register(builder);
		GREATSWORD_TH_LIGHT_ATTACK = new AttackAnimation[]
				{
						new AttackAnimation(DarkSouls.rl("greatsword_th_la_1"), AttackType.TWO_HANDED_LIGHT, 0.5F, 0.0F, 0.2F, 0.44F, 1.6F, "Tool_R",
								DarkSouls.rl("biped/combat/greatsword_th_la_1"), (models) -> models.ENTITY_BIPED)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 35)
								.addProperty(AttackProperty.STAMINA_USAGE, 62)
								.addProperty(AttackProperty.POISE_DAMAGE, 26)
								.register(builder),
						new AttackAnimation(DarkSouls.rl("greatsword_th_la_2"), AttackType.TWO_HANDED_LIGHT, 0.3F, 0.0F, 0.28F, 0.48F, 2.0F, "Tool_R",
								DarkSouls.rl("biped/combat/greatsword_th_la_2"), (models) -> models.ENTITY_BIPED)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 35)
								.addProperty(AttackProperty.STAMINA_USAGE, 62)
								.addProperty(AttackProperty.POISE_DAMAGE, 26)
								.register(builder)
				};
		GREATSWORD_TH_THRUST_ATTACK = new AttackAnimation[]
				{
						new AttackAnimation(DarkSouls.rl("greatsword_th_ha_1"), AttackType.TWO_HANDED_HEAVY, 0.5F, 0.0F, 0.4F, 0.52F, 1.6F, "Tool_R",
								DarkSouls.rl("biped/combat/greatsword_th_ha_1"), (models) -> models.ENTITY_BIPED)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_THRUST)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 42)
								.addProperty(AttackProperty.STAMINA_USAGE, 74)
								.addProperty(AttackProperty.POISE_DAMAGE, 26)
								.register(builder),
						new AttackAnimation(DarkSouls.rl("greatsword_th_ha_2"), AttackType.TWO_HANDED_HEAVY, 0.3F, 0.0F, 0.28F, 0.48F, 2.0F, "Tool_R",
								DarkSouls.rl("biped/combat/greatsword_th_la_2"), (models) -> models.ENTITY_BIPED)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 42)
								.addProperty(AttackProperty.STAMINA_USAGE, 74)
								.addProperty(AttackProperty.POISE_DAMAGE, 26)
								.register(builder)
				};
		GREATSWORD_TH_DASH_ATTACK = new AttackAnimation(DarkSouls.rl("greatsword_th_da"), AttackType.TWO_HANDED_DASH, 0.05F, 0.0F, 0.4F, 0.52F, 1.6F, "Tool_R",
				DarkSouls.rl("biped/combat/greatsword_th_ha_1"), (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
				.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_THRUST)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 40)
				.addProperty(AttackProperty.STAMINA_USAGE, 72)
				.addProperty(AttackProperty.POISE_DAMAGE, 25)
				.register(builder);

		// Ultra Greatsword
		ULTRA_GREATSWORD_LIGHT_ATTACK = new AttackAnimation[]
		{
				new AttackAnimation(DarkSouls.rl("ultra_greatsword_light_attack_1"), AttackType.LIGHT, 0.3F, 0.0F, 0.48F, 0.88F, 2.8F, "Tool_R",
							DarkSouls.rl("biped/combat/ultra_greatsword_light_attack_1"), (models) -> models.ENTITY_BIPED)
							.addProperty(StaticAnimationProperty.EVENTS, new Event[]
							{
									Event.create(0.7F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.ULTRA_GREATSWORD_SMASH.get())),
									Event.create(0.7F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 20, 1))
							})
							.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
							.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
							.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
							.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
							.addProperty(AttackProperty.STAMINA_DAMAGE, 70)
							.addProperty(AttackProperty.STAMINA_USAGE, 45)
							.addProperty(AttackProperty.POISE_DAMAGE, 28)
							.register(builder),
				new AttackAnimation(DarkSouls.rl("ultra_greatsword_light_attack_2"), AttackType.LIGHT, 0.2F, 0.0F, 0.44F, 0.88F, 2.4F, "Tool_R",
							DarkSouls.rl("biped/combat/ultra_greatsword_light_attack_2"), (models) -> models.ENTITY_BIPED)
							.addProperty(StaticAnimationProperty.EVENTS, new Event[]
							{
									Event.create(0.72F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.ULTRA_GREATSWORD_SMASH.get())),
									Event.create(0.72F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 20, 1))
							})
							.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
							.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
							.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
							.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
							.addProperty(AttackProperty.STAMINA_DAMAGE, 70)
							.addProperty(AttackProperty.STAMINA_USAGE, 45)
							.addProperty(AttackProperty.POISE_DAMAGE, 28)
							.register(builder)
		};
		ULTRA_GREATSWORD_HEAVY_ATTACK = new AttackAnimation[]
		{
				new AttackAnimation(DarkSouls.rl("ultra_greatsword_heavy_attack_1"), AttackType.HEAVY, 0.5F, 0.0F, 1.35F, 1.6F, 3.45F, "Tool_R",
					DarkSouls.rl("biped/combat/ultra_greatsword_heavy_attack_1"), (models) -> models.ENTITY_BIPED)
					.addProperty(StaticAnimationProperty.EVENTS, new Event[]
					{
							Event.create(1.3F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.ULTRA_GREATSWORD_SMASH.get())),
							Event.create(1.3F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 20, 1))
					})
					.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
					.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
					.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
					.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
					.addProperty(AttackProperty.STAMINA_DAMAGE, 70)
					.addProperty(AttackProperty.STAMINA_USAGE, 80)
					.addProperty(AttackProperty.POISE_DAMAGE, 36)
					.register(builder),
				new AttackAnimation(DarkSouls.rl("ultra_greatsword_heavy_attack_2"), AttackType.HEAVY, 0.5F, 0.0F, 1.35F, 1.6F, 3.45F, "Tool_R",
					DarkSouls.rl("biped/combat/ultra_greatsword_heavy_attack_2"), (models) -> models.ENTITY_BIPED)
					.addProperty(StaticAnimationProperty.EVENTS, new Event[]
					{
							Event.create(1.3F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.ULTRA_GREATSWORD_SMASH.get())),
							Event.create(1.3F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 20, 1))
					})
					.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
					.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
					.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
					.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
					.addProperty(AttackProperty.STAMINA_DAMAGE, 70)
					.addProperty(AttackProperty.STAMINA_USAGE, 80)
					.addProperty(AttackProperty.POISE_DAMAGE, 36)
					.register(builder)
		};
		ULTRA_GREATSWORD_DASH_ATTACK = new AttackAnimation(DarkSouls.rl("ultra_greatsword_dash_attack"), AttackType.DASH, 0.1F, 0.0F, 1.35F, 1.6F, 3.45F, "Tool_R",
				DarkSouls.rl("biped/combat/ultra_greatsword_heavy_attack_1"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(1.6F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.ULTRA_GREATSWORD_SMASH.get())),
						Event.create(1.6F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 20, 1))
				})
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
				.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
				.addProperty(AttackProperty.STAMINA_DAMAGE, 70)
				.addProperty(AttackProperty.STAMINA_USAGE, 50)
				.addProperty(AttackProperty.POISE_DAMAGE, 28)
				.register(builder);
		ULTRA_GREATSWORD_TH_LIGHT_ATTACK = new AttackAnimation[]
				{
						new AttackAnimation(DarkSouls.rl("ultra_greatsword_th_la_1"), AttackType.TWO_HANDED_LIGHT, 0.3F, 0.0F, 0.48F, 0.88F, 2.8F, "Tool_R",
									DarkSouls.rl("biped/combat/ultra_greatsword_th_la_1"), (models) -> models.ENTITY_BIPED)
									.addProperty(StaticAnimationProperty.EVENTS, new Event[]
									{
											Event.create(0.7F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.ULTRA_GREATSWORD_SMASH.get())),
											Event.create(0.7F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 20, 1))
									})
									.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
									.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
									.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
									.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
									.addProperty(AttackProperty.STAMINA_DAMAGE, 75)
									.addProperty(AttackProperty.STAMINA_USAGE, 51)
									.addProperty(AttackProperty.POISE_DAMAGE, 32)
									.register(builder),
						new AttackAnimation(DarkSouls.rl("ultra_greatsword_th_la_2"), AttackType.TWO_HANDED_LIGHT, 0.2F, 0.0F, 0.44F, 0.88F, 2.4F, "Tool_R",
									DarkSouls.rl("biped/combat/ultra_greatsword_th_la_2"), (models) -> models.ENTITY_BIPED)
									.addProperty(StaticAnimationProperty.EVENTS, new Event[]
									{
											Event.create(0.72F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.ULTRA_GREATSWORD_SMASH.get())),
											Event.create(0.72F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 20, 1))
									})
									.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
									.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
									.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
									.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
									.addProperty(AttackProperty.STAMINA_DAMAGE, 75)
									.addProperty(AttackProperty.STAMINA_USAGE, 51)
									.addProperty(AttackProperty.POISE_DAMAGE, 32)
									.register(builder)
				};
		ULTRA_GREATSWORD_TH_HEAVY_ATTACK = new AttackAnimation[]
				{
						new AttackAnimation(DarkSouls.rl("ultra_greatsword_th_ha_1"), AttackType.TWO_HANDED_HEAVY, 0.5F, 0.0F, 1.35F, 1.6F, 3.45F, "Tool_R",
							DarkSouls.rl("biped/combat/ultra_greatsword_th_ha_1"), (models) -> models.ENTITY_BIPED)
							.addProperty(StaticAnimationProperty.EVENTS, new Event[]
							{
									Event.create(1.3F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.ULTRA_GREATSWORD_SMASH.get())),
									Event.create(1.3F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 20, 1))
							})
							.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
							.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
							.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
							.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
							.addProperty(AttackProperty.STAMINA_DAMAGE, 76)
							.addProperty(AttackProperty.STAMINA_USAGE, 84)
							.addProperty(AttackProperty.POISE_DAMAGE, 40)
							.register(builder),
						new AttackAnimation(DarkSouls.rl("ultra_greatsword_th_ha_2"), AttackType.TWO_HANDED_HEAVY, 0.5F, 0.0F, 1.35F, 1.6F, 3.45F, "Tool_R",
							DarkSouls.rl("biped/combat/ultra_greatsword_th_ha_2"), (models) -> models.ENTITY_BIPED)
							.addProperty(StaticAnimationProperty.EVENTS, new Event[]
							{
									Event.create(1.3F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.ULTRA_GREATSWORD_SMASH.get())),
									Event.create(1.3F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 20, 1))
							})
							.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
							.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
							.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
							.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
							.addProperty(AttackProperty.STAMINA_DAMAGE, 76)
							.addProperty(AttackProperty.STAMINA_USAGE, 84)
							.addProperty(AttackProperty.POISE_DAMAGE, 40)
							.register(builder)
				};
		ULTRA_GREATSWORD_TH_DASH_ATTACK = new AttackAnimation(DarkSouls.rl("ultra_greatsword_th_da"), AttackType.TWO_HANDED_DASH, 0.1F, 0.0F, 1.35F, 1.6F, 3.45F, "Tool_R",
				DarkSouls.rl("biped/combat/ultra_greatsword_th_ha_1"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(1.6F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.ULTRA_GREATSWORD_SMASH.get())),
						Event.create(1.6F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 20, 1))
				})
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
				.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
				.addProperty(AttackProperty.STAMINA_DAMAGE, 76)
				.addProperty(AttackProperty.STAMINA_USAGE, 84)
				.addProperty(AttackProperty.POISE_DAMAGE, 40)
				.register(builder);
		
		// Greataxe
		GREATAXE_LIGHT_ATTACK = new AttackAnimation[]
				{
						new AttackAnimation(DarkSouls.rl("greataxe_la_1"), AttackType.LIGHT, 0.3F, 0.0F, 0.64F, 0.88F, 2.0F, "Tool_R",
									DarkSouls.rl("biped/combat/greataxe_la_1"), (models) -> models.ENTITY_BIPED)
									.addProperty(StaticAnimationProperty.EVENTS, new Event[]
									{
											Event.create(0.7F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.ULTRA_GREATSWORD_SMASH.get())),
											Event.create(0.7F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 20, 1))
									})
									.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
									.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
									.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
									.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
									.addProperty(AttackProperty.STAMINA_DAMAGE, 75)
									.addProperty(AttackProperty.STAMINA_USAGE, 35)
									.addProperty(AttackProperty.POISE_DAMAGE, 30)
									.register(builder),
						new AttackAnimation(DarkSouls.rl("greataxe_la_2"), AttackType.LIGHT, 0.3F, 0.0F, 0.76F, 0.96F, 2.0F, "Tool_R",
									DarkSouls.rl("biped/combat/greataxe_la_2"), (models) -> models.ENTITY_BIPED)
									.addProperty(StaticAnimationProperty.EVENTS, new Event[]
									{
											Event.create(0.88F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.ULTRA_GREATSWORD_SMASH.get())),
											Event.create(0.88F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 20, 1))
									})
									.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
									.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
									.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
									.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
									.addProperty(AttackProperty.STAMINA_DAMAGE, 75)
									.addProperty(AttackProperty.STAMINA_USAGE, 35)
									.addProperty(AttackProperty.POISE_DAMAGE, 30)
									.register(builder)
				};
		GREATAXE_HEAVY_ATTACK = new AttackAnimation(DarkSouls.rl("greataxe_ha"), AttackType.HEAVY, 0.3F, 0.0F, 1.08F, 1.28F, 2.2F, "Tool_R",
				DarkSouls.rl("biped/combat/greataxe_ha"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(1.16F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.ULTRA_GREATSWORD_SMASH.get())),
						Event.create(1.16F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 20, 1))
				})
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
				.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
				.addProperty(AttackProperty.STAMINA_DAMAGE, 75)
				.addProperty(AttackProperty.STAMINA_USAGE, 74)
				.addProperty(AttackProperty.POISE_DAMAGE, 30)
				.register(builder);
		GREATAXE_DASH_ATTACK = new AttackAnimation(DarkSouls.rl("greataxe_da"), AttackType.DASH, 0.1F, 0.0F, 0.68F, 0.96F, 2.0F, "Tool_R",
				DarkSouls.rl("biped/combat/greataxe_da"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(0.72F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.ULTRA_GREATSWORD_SMASH.get())),
						Event.create(0.72F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 20, 1))
				})
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
				.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
				.addProperty(AttackProperty.STAMINA_DAMAGE, 75)
				.addProperty(AttackProperty.STAMINA_USAGE, 45)
				.addProperty(AttackProperty.POISE_DAMAGE, 30)
				.register(builder);
		GREATAXE_TH_LIGHT_ATTACK = new AttackAnimation[]
				{
						new AttackAnimation(DarkSouls.rl("greataxe_th_la_1"), AttackType.TWO_HANDED_LIGHT, 0.3F, 0.0F, 0.8F, 0.92F, 2.2F, "Tool_R",
									DarkSouls.rl("biped/combat/greataxe_th_la_1"), (models) -> models.ENTITY_BIPED)
									.addProperty(StaticAnimationProperty.EVENTS, new Event[]
									{
											Event.create(0.72F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.ULTRA_GREATSWORD_SMASH.get())),
											Event.create(0.72F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 20, 1))
									})
									.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
									.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
									.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
									.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
									.addProperty(AttackProperty.STAMINA_DAMAGE, 75)
									.addProperty(AttackProperty.STAMINA_USAGE, 48)
									.addProperty(AttackProperty.POISE_DAMAGE, 30)
									.register(builder),
						new AttackAnimation(DarkSouls.rl("greataxe_th_la_2"), AttackType.TWO_HANDED_LIGHT, 0.3F, 0.0F, 0.56F, 0.88F, 2.2F, "Tool_R",
									DarkSouls.rl("biped/combat/greataxe_th_la_2"), (models) -> models.ENTITY_BIPED)
									.addProperty(StaticAnimationProperty.EVENTS, new Event[]
									{
											Event.create(0.68F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.ULTRA_GREATSWORD_SMASH.get())),
											Event.create(0.68F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 20, 1))
									})
									.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
									.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
									.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
									.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
									.addProperty(AttackProperty.STAMINA_DAMAGE, 75)
									.addProperty(AttackProperty.STAMINA_USAGE, 48)
									.addProperty(AttackProperty.POISE_DAMAGE, 30)
									.register(builder)
				};
		GREATAXE_TH_HEAVY_ATTACK = new AttackAnimation(DarkSouls.rl("greataxe_th_ha"), AttackType.TWO_HANDED_HEAVY, 0.3F, 0.0F, 0.92F, 1.2F, 2.4F, "Tool_R",
				DarkSouls.rl("biped/combat/greataxe_th_ha"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(1.0F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.ULTRA_GREATSWORD_SMASH.get())),
						Event.create(1.0F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 20, 1))
				})
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
				.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
				.addProperty(AttackProperty.STAMINA_DAMAGE, 75)
				.addProperty(AttackProperty.STAMINA_USAGE, 85)
				.addProperty(AttackProperty.POISE_DAMAGE, 30)
				.register(builder);
		GREATAXE_TH_DASH_ATTACK = new AttackAnimation(DarkSouls.rl("greataxe_th_da"), AttackType.TWO_HANDED_DASH, 0.1F, 0.0F, 0.68F, 0.96F, 2.0F, "Tool_R",
				DarkSouls.rl("biped/combat/greataxe_th_da"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(0.72F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.ULTRA_GREATSWORD_SMASH.get())),
						Event.create(0.72F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 20, 1))
				})
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
				.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
				.addProperty(AttackProperty.STAMINA_DAMAGE, 75)
				.addProperty(AttackProperty.STAMINA_USAGE, 55)
				.addProperty(AttackProperty.POISE_DAMAGE, 30)
				.register(builder);

		// Spear
		SPEAR_DASH_ATTACK = new AttackAnimation(DarkSouls.rl("spear_dash_attack"), AttackType.DASH, 0.2F, 0.0F, 0.15F, 0.3F, 1.0F, "Tool_R",
				DarkSouls.rl("biped/combat/spear_dash_attack"), (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
				.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
				.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
				.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SPEAR_SWING)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 40)
				.addProperty(AttackProperty.STAMINA_USAGE, 30)
				.addProperty(AttackProperty.POISE_DAMAGE, 20)
				.register(builder);
		SPEAR_HEAVY_ATTACK = new AttackAnimation(DarkSouls.rl("spear_heavy_attack"), AttackType.HEAVY, 0.35F, 0.0F, 0.65F, 0.8F, 1.75F, "Tool_R",
				DarkSouls.rl("biped/combat/spear_heavy_attack"), (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
				.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
				.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
				.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SPEAR_SWING)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 40)
				.addProperty(AttackProperty.STAMINA_USAGE, 55)
				.addProperty(AttackProperty.POISE_DAMAGE, 20)
				.register(builder);
		SPEAR_LIGHT_ATTACK = new AttackAnimation(DarkSouls.rl("spear_light_attack"), AttackType.LIGHT, 0.15F, 0.0F, 0.32F, 0.6F, 1.5F, "Tool_R",
				DarkSouls.rl("biped/combat/spear_light_attack"), (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
				.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
				.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SPEAR_SWING)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 40)
				.addProperty(AttackProperty.STAMINA_USAGE, 25)
				.addProperty(AttackProperty.POISE_DAMAGE, 20)
				.register(builder);
		SPEAR_LIGHT_BLOCKING_ATTACK = new AttackAnimation(DarkSouls.rl("spear_light_blocking_attack"), AttackType.LIGHT, 0.2F, 0.0F, 0.35F, 0.5F, 1.25F, "Tool_R",
				DarkSouls.rl("biped/combat/spear_light_blocking_attack"), (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
				.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
				.addProperty(AttackProperty.BLOCKING, true)
				.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SPEAR_SWING)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 40)
				.addProperty(AttackProperty.STAMINA_USAGE, 25)
				.addProperty(AttackProperty.POISE_DAMAGE, 20)
				.register(builder);
		SPEAR_TH_LIGHT_ATTACK = new AttackAnimation(DarkSouls.rl("spear_th_la"), AttackType.TWO_HANDED_LIGHT, 0.15F, 0.0F, 0.4F, 0.64F, 1.2F, "Tool_R",
				DarkSouls.rl("biped/combat/spear_th_la"), (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
				.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
				.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SPEAR_SWING)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 42)
				.addProperty(AttackProperty.STAMINA_USAGE, 27)
				.addProperty(AttackProperty.POISE_DAMAGE, 23)
				.register(builder);
		SPEAR_TH_HEAVY_ATTACK = new AttackAnimation(DarkSouls.rl("spear_th_ha"), AttackType.TWO_HANDED_HEAVY, 0.35F, 0.0F, 0.6F, 0.8F, 1.6F, "Tool_R",
				DarkSouls.rl("biped/combat/spear_th_ha"), (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
				.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
				.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
				.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SPEAR_SWING)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
				.addProperty(AttackProperty.STAMINA_USAGE, 61)
				.addProperty(AttackProperty.POISE_DAMAGE, 26)
				.register(builder);
		SPEAR_TH_DASH_ATTACK = new AttackAnimation(DarkSouls.rl("spear_th_da"), AttackType.TWO_HANDED_DASH, 0.2F, 0.0F, 0.36F, 0.55F, 1.2F, "Tool_R",
				DarkSouls.rl("biped/combat/spear_th_da"), (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
				.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
				.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
				.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SPEAR_SWING)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
				.addProperty(AttackProperty.STAMINA_USAGE, 35)
				.addProperty(AttackProperty.POISE_DAMAGE, 24)
				.register(builder);

		// Dagger
		DAGGER_HEAVY_ATTACK = new AttackAnimation(DarkSouls.rl("dagger_heavy_attack"), AttackType.HEAVY, 0.2F, 0.0F, 0.68F, 0.96F, 1.6F, "Tool_R",
				DarkSouls.rl("biped/combat/dagger_heavy_attack"), (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
				.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
				.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
				.register(builder);
		DAGGER_LIGHT_ATTACK = new AttackAnimation[]
		{
				new AttackAnimation(DarkSouls.rl("dagger_light_attack_1"), AttackType.LIGHT, 0.2F, 0.0F, 0.16F, 0.4F, 1.0F, "Tool_R",
						DarkSouls.rl("biped/combat/dagger_light_attack_1"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.SLASH)
					.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
					.register(builder),
				new AttackAnimation(DarkSouls.rl("dagger_light_attack_2"), AttackType.LIGHT, 0.2F, 0.0F, 0.04F, 0.32F, 1.0F, "Tool_R",
						DarkSouls.rl("biped/combat/dagger_light_attack_2"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.SLASH)
					.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
					.register(builder)
		};

		// Great Hammer
		GREAT_HAMMER_HEAVY_ATTACK = new AttackAnimation(DarkSouls.rl("great_hammer_heavy_attack"), AttackType.HEAVY, 0.5F, 0.0F, 1.45F, 1.9F, 3.75F, "Tool_R",
				DarkSouls.rl("biped/combat/great_hammer_heavy_attack"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(1.6F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GREAT_HAMMER_SMASH.get())),
						Event.create(1.6F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 20, 1))
				})
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
				.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
				.addProperty(AttackProperty.STAMINA_DAMAGE, 80)
				.addProperty(AttackProperty.STAMINA_USAGE, 74)
				.addProperty(AttackProperty.POISE_DAMAGE, 32)
				.register(builder);
		GREAT_HAMMER_LIGHT_ATTACK = new AttackAnimation[]
		{
				new AttackAnimation(DarkSouls.rl("great_hammer_light_attack_1"), AttackType.LIGHT, 0.5F, 0.0F, 0.84F, 1.38F, 2.76F, "Tool_R",
						DarkSouls.rl("biped/combat/great_hammer_light_attack_1"), (models) -> models.ENTITY_BIPED)
					.addProperty(StaticAnimationProperty.EVENTS, new Event[]
					{
							Event.create(1.3F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GREAT_HAMMER_SMASH.get())),
							Event.create(1.3F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 20, 1))
					})
					.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
					.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
					.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
					.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
					.addProperty(AttackProperty.STAMINA_DAMAGE, 80)
					.addProperty(AttackProperty.STAMINA_USAGE, 35)
					.addProperty(AttackProperty.POISE_DAMAGE, 28)
					.register(builder),
				new AttackAnimation(DarkSouls.rl("great_hammer_light_attack_2"), AttackType.LIGHT, 0.5F, 0.0F, 1.15F, 1.7F, 3.45F, "Tool_R",
						DarkSouls.rl("biped/combat/great_hammer_light_attack_2"), (models) -> models.ENTITY_BIPED)
					.addProperty(StaticAnimationProperty.EVENTS, new Event[]
					{
							Event.create(1.3F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GREAT_HAMMER_SMASH.get())),
							Event.create(1.3F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 20, 1))
					})
					.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
					.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
					.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
					.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
					.addProperty(AttackProperty.STAMINA_DAMAGE, 80)
					.addProperty(AttackProperty.STAMINA_USAGE, 35)
					.addProperty(AttackProperty.POISE_DAMAGE, 28)
					.register(builder)
		};
		GREAT_HAMMER_DASH_ATTACK = new AttackAnimation(DarkSouls.rl("great_hammer_dash_attack"), AttackType.DASH, 0.1F, 0.0F, 1.15F, 1.6F, 3.45F, "Tool_R",
				DarkSouls.rl("biped/combat/great_hammer_light_attack_1"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(1.3F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GREAT_HAMMER_SMASH.get())),
						Event.create(1.3F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 20, 1))
				})
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
				.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
				.addProperty(AttackProperty.STAMINA_DAMAGE, 80)
				.addProperty(AttackProperty.STAMINA_USAGE, 45)
				.addProperty(AttackProperty.POISE_DAMAGE, 28)
				.register(builder);
		GREAT_HAMMER_TH_LIGHT_ATTACK = new AttackAnimation[]
		{
						new AttackAnimation(DarkSouls.rl("great_hammer_th_la_1"), AttackType.LIGHT, 0.5F, 0.0F, 0.32F, 0.56F, 1.6F, "Tool_R",
								DarkSouls.rl("biped/combat/great_hammer_th_la_1"), (models) -> models.ENTITY_BIPED)
							.addProperty(StaticAnimationProperty.EVENTS, new Event[]
							{
									Event.create(0.56F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GREAT_HAMMER_SMASH.get())),
									Event.create(0.56F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 20, 1))
							})
							.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
							.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
							.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
							.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
							.addProperty(AttackProperty.STAMINA_DAMAGE, 90)
							.addProperty(AttackProperty.STAMINA_USAGE, 40)
							.addProperty(AttackProperty.POISE_DAMAGE, 36)
							.register(builder),
						new AttackAnimation(DarkSouls.rl("great_hammer_th_la_2"), AttackType.LIGHT, 0.5F, 0.0F, 0.24F, 0.48F, 1.2F, "Tool_R",
								DarkSouls.rl("biped/combat/great_hammer_th_la_2"), (models) -> models.ENTITY_BIPED)
							.addProperty(StaticAnimationProperty.EVENTS, new Event[]
							{
									Event.create(0.48F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GREAT_HAMMER_SMASH.get())),
									Event.create(0.48F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 20, 1))
							})
							.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
							.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
							.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
							.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
							.addProperty(AttackProperty.STAMINA_DAMAGE, 90)
							.addProperty(AttackProperty.STAMINA_USAGE, 40)
							.addProperty(AttackProperty.POISE_DAMAGE, 36)
							.register(builder)
		};
		GREAT_HAMMER_TH_HEAVY_ATTACK = new AttackAnimation(DarkSouls.rl("great_hammer_th_ha"), AttackType.HEAVY, 0.5F, 0.0F, 0.32F, 0.52F, 1.2F, "Tool_R",
				DarkSouls.rl("biped/combat/great_hammer_th_ha"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(0.52F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GREAT_HAMMER_SMASH.get())),
						Event.create(0.52F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 20, 1))
				})
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
				.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
				.addProperty(AttackProperty.STAMINA_DAMAGE, 95)
				.addProperty(AttackProperty.STAMINA_USAGE, 82)
				.addProperty(AttackProperty.POISE_DAMAGE, 39)
				.register(builder);
		GREAT_HAMMER_TH_DASH_ATTACK = new AttackAnimation(DarkSouls.rl("great_hammer_th_da"), AttackType.LIGHT, 0.5F, 0.0F, 0.32F, 0.56F, 1.6F, "Tool_R",
				DarkSouls.rl("biped/combat/great_hammer_th_la_1"), (models) -> models.ENTITY_BIPED)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[]
			{
					Event.create(0.56F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GREAT_HAMMER_SMASH.get())),
					Event.create(0.56F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 20, 1))
			})
			.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
			.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
			.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
			.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
			.addProperty(AttackProperty.STAMINA_DAMAGE, 90)
			.addProperty(AttackProperty.STAMINA_USAGE, 40)
			.addProperty(AttackProperty.POISE_DAMAGE, 36)
			.register(builder);

		// Axe
		AXE_HEAVY_ATTACK = new AttackAnimation(DarkSouls.rl("axe_heavy_attack"), AttackType.HEAVY, 0.3F, 0.0F, 0.55F, 0.7F, 1.5F, "Tool_R",
				DarkSouls.rl("biped/combat/axe_heavy_attack"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.4F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.AXE_SWING.get())) })
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
				.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
				.addProperty(AttackProperty.STAMINA_USAGE, 50)
				.addProperty(AttackProperty.POISE_DAMAGE, 23)
				.register(builder);
		AXE_LIGHT_ATTACK = new AttackAnimation[]
		{
				new AttackAnimation(DarkSouls.rl("axe_light_attack_1"), AttackType.LIGHT, 0.3F, 0.0F, 0.2F, 0.35F, 1.5F, "Tool_R",
						DarkSouls.rl("biped/combat/axe_light_attack_1"), (models) -> models.ENTITY_BIPED)
					.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.16F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.AXE_SWING.get())) })
					.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
					.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
					.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
					.addProperty(AttackProperty.STAMINA_USAGE, 25)
					.addProperty(AttackProperty.POISE_DAMAGE, 23)
					.register(builder),
				new AttackAnimation(DarkSouls.rl("axe_light_attack_2"), AttackType.LIGHT, 0.2F, 0.0F, 0.15F, 0.4F, 1.25F, "Tool_R",
						DarkSouls.rl("biped/combat/axe_light_attack_2"), (models) -> models.ENTITY_BIPED)
					.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.12F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.AXE_SWING.get())) })
					.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
					.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
					.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
					.addProperty(AttackProperty.STAMINA_USAGE, 25)
					.addProperty(AttackProperty.POISE_DAMAGE, 23)
					.register(builder)
		};
		AXE_DASH_ATTACK = new AttackAnimation(DarkSouls.rl("axe_dash_attack"), AttackType.DASH, 0.2F, 0.0F, 0.4F, 0.5F, 1.5F, "Tool_R",
				DarkSouls.rl("biped/combat/axe_dash_attack"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.35F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.AXE_SWING.get())) })
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
				.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
				.addProperty(AttackProperty.STAMINA_USAGE, 30)
				.addProperty(AttackProperty.POISE_DAMAGE, 23)
				.register(builder);
		AXE_TH_LIGHT_ATTACK = new AttackAnimation[]
		{
			new AttackAnimation(DarkSouls.rl("axe_th_la_1"), AttackType.TWO_HANDED_LIGHT, 0.4F, 0.0F, 0.08F, 0.4F, 1.2F, "Tool_R",
					DarkSouls.rl("biped/combat/axe_th_la_1"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.08F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.AXE_SWING.get())) })
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
				.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
				.addProperty(AttackProperty.STAMINA_USAGE, 25)
				.addProperty(AttackProperty.POISE_DAMAGE, 23)
				.register(builder),
			new AttackAnimation(DarkSouls.rl("axe_th_la_2"), AttackType.TWO_HANDED_LIGHT, 0.4F, 0.0F, 0.08F, 0.45F, 1.25F, "Tool_R",
					DarkSouls.rl("biped/combat/axe_th_la_2"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.08F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.AXE_SWING.get())) })
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
				.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
				.addProperty(AttackProperty.STAMINA_USAGE, 25)
				.addProperty(AttackProperty.POISE_DAMAGE, 23)
				.register(builder)
		};
		AXE_TH_HEAVY_ATTACK = new AttackAnimation(DarkSouls.rl("axe_th_ha"), AttackType.TWO_HANDED_HEAVY, 0.4F, 0.0F, 0.32F, 0.56F, 1.6F, "Tool_R",
				DarkSouls.rl("biped/combat/axe_th_ha"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(0.32F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.AXE_SWING.get())),
						Event.create(0.4F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.ULTRA_GREATSWORD_SMASH.get()))
				})
				.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
				.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
				.addProperty(AttackProperty.STAMINA_USAGE, 50)
				.addProperty(AttackProperty.POISE_DAMAGE, 23)
				.register(builder);
		AXE_TH_DASH_ATTACK = new AttackAnimation(DarkSouls.rl("axe_th_da"), AttackType.TWO_HANDED_DASH, 0.1F, 0.0F, 0.48F, 0.68F, 1.6F, "Tool_R",
				DarkSouls.rl("biped/combat/axe_th_da"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(0.48F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.AXE_SWING.get()))
				})
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
				.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 80)
				.addProperty(AttackProperty.STAMINA_USAGE, 45)
				.addProperty(AttackProperty.POISE_DAMAGE, 28)
				.register(builder);

		// Hammer
		HAMMER_DASH_ATTACK = new AttackAnimation(DarkSouls.rl("hammer_dash_attack"), AttackType.DASH, 0.1F, 0.0F, 0.32F, 0.6F, 1.4F, "Tool_R",
				DarkSouls.rl("biped/combat/hammer_light_attack"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.32F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.AXE_SWING.get())) })
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
				.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 56)
				.addProperty(AttackProperty.STAMINA_USAGE, 30)
				.addProperty(AttackProperty.POISE_DAMAGE, 23)
				.register(builder);
		HAMMER_HEAVY_ATTACK = new AttackAnimation(DarkSouls.rl("hammer_heavy_attack"), AttackType.HEAVY, 0.5F, 0.0F, 0.28F, 0.48F, 1.4F, "Tool_R",
				DarkSouls.rl("biped/combat/hammer_heavy_attack"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.32F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.AXE_SWING.get())) })
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
				.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 68)
				.addProperty(AttackProperty.STAMINA_USAGE, 50)
				.addProperty(AttackProperty.POISE_DAMAGE, 36)
				.register(builder);
		HAMMER_LIGHT_ATTACK = new AttackAnimation(DarkSouls.rl("hammer_light_attack"), AttackType.LIGHT, 0.3F, 0.0F, 0.24F, 0.48F, 1.2F, "Tool_R",
				DarkSouls.rl("biped/combat/hammer_light_attack"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.28F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.AXE_SWING.get())) })
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 56)
				.addProperty(AttackProperty.STAMINA_USAGE, 25)
				.addProperty(AttackProperty.POISE_DAMAGE, 23)
				.register(builder);
		HAMMER_TH_LIGHT_ATTACK = new AttackAnimation(DarkSouls.rl("hammer_th_la"), AttackType.TWO_HANDED_LIGHT, 0.3F, 0.0F, 0.24F, 0.48F, 1.6F, "Tool_R",
				DarkSouls.rl("biped/combat/hammer_th_la"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.2F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.AXE_SWING.get())) })
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 63)
				.addProperty(AttackProperty.STAMINA_USAGE, 25)
				.addProperty(AttackProperty.POISE_DAMAGE, 32)
				.register(builder);
		HAMMER_TH_HEAVY_ATTACK = new AttackAnimation(DarkSouls.rl("hammer_th_ha"), AttackType.TWO_HANDED_HEAVY, 0.4F, 0.0F, 0.24F, 0.44F, 1.6F, "Tool_R",
				DarkSouls.rl("biped/combat/hammer_th_ha"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.24F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.AXE_SWING.get())) })
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 69)
				.addProperty(AttackProperty.STAMINA_USAGE, 50)
				.addProperty(AttackProperty.POISE_DAMAGE, 47)
				.register(builder);
		HAMMER_TH_DASH_ATTACK = new AttackAnimation(DarkSouls.rl("hammer_th_da"), AttackType.TWO_HANDED_DASH, 0.2F, 0.0F, 0.24F, 0.48F, 1.6F, "Tool_R",
				DarkSouls.rl("biped/combat/hammer_th_la"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.2F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.AXE_SWING.get())) })
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 63)
				.addProperty(AttackProperty.STAMINA_USAGE, 25)
				.addProperty(AttackProperty.POISE_DAMAGE, 32)
				.register(builder);

		// Fist
		FIST_LIGHT_ATTACK = new AttackAnimation[]
		{
				new AttackAnimation(DarkSouls.rl("fist_light_attack_1"), AttackType.LIGHT, 0.2F, 0.0F, 0.3F, 0.4F, 1.25F, "Tool_R",
						DarkSouls.rl("biped/combat/fist_light_attack_1"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
					.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.FIST_SWING)
					.addProperty(AttackProperty.STAMINA_DAMAGE, 28)
					.addProperty(AttackProperty.STAMINA_USAGE, 18)
					.addProperty(AttackProperty.POISE_DAMAGE, 15)
					.register(builder),
				new AttackAnimation(DarkSouls.rl("fist_light_attack_2"), AttackType.LIGHT, 0.2F, 0.0F, 0.15F, 0.3F, 1.25F, "Tool_R",
						DarkSouls.rl("biped/combat/fist_light_attack_2"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
					.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.FIST_SWING)
					.addProperty(AttackProperty.STAMINA_DAMAGE, 28)
					.addProperty(AttackProperty.STAMINA_USAGE, 18)
					.addProperty(AttackProperty.POISE_DAMAGE, 15)
					.register(builder)
		};
		FIST_DASH_ATTACK = new AttackAnimation(DarkSouls.rl("fist_dash_attack"), AttackType.DASH, 0.3F, 0.0F, 0.15F, 0.3F, 1.0F, "Tool_R",
				DarkSouls.rl("biped/combat/fist_dash_attack"), (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
				.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
				.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
				.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.FIST_SWING)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 28)
				.addProperty(AttackProperty.STAMINA_USAGE, 27)
				.addProperty(AttackProperty.POISE_DAMAGE, 15)
				.register(builder);
		FIST_HEAVY_ATTACK = new AttackAnimation(DarkSouls.rl("fist_heavy_attack"), AttackType.HEAVY, 0.5F, 0.0F, 0.35F, 0.5F, 1.25F, "Tool_R",
				DarkSouls.rl("biped/combat/fist_heavy_attack"), (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
				.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
				.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
				.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.FIST_SWING)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 28)
				.addProperty(AttackProperty.STAMINA_USAGE, 36)
				.addProperty(AttackProperty.POISE_DAMAGE, 15)
				.register(builder);

		// Shield
		SHIELD_LIGHT_ATTACK = new AttackAnimation(DarkSouls.rl("shield_la"), AttackType.LIGHT, 0.2F, 0.0F, 0.4F, 0.56F, 1.2F, "Tool_R",
			DarkSouls.rl("biped/combat/shield_la"), (models) -> models.ENTITY_BIPED)
			.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
			.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
			.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.FIST_SWING)
			.addProperty(AttackProperty.STAMINA_DAMAGE, 45)
			.addProperty(AttackProperty.STAMINA_USAGE, 22)
			.addProperty(AttackProperty.POISE_DAMAGE, 25)
			.register(builder);
		SHIELD_HEAVY_ATTACK = new AttackAnimation[]
		{
				new AttackAnimation(DarkSouls.rl("shield_ha_1"), AttackType.HEAVY, 0.2F, 0.0F, 0.44F, 0.56F, 1.4F, "Tool_R",
						DarkSouls.rl("biped/combat/shield_ha_1"), (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
				.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.FIST_SWING)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 51)
				.addProperty(AttackProperty.STAMINA_USAGE, 28)
				.addProperty(AttackProperty.POISE_DAMAGE, 30)
				.register(builder),
				new AttackAnimation(DarkSouls.rl("shield_ha_2"), AttackType.HEAVY, 0.2F, 0.0F, 0.28F, 0.48F, 1.4F, "Tool_R",
						DarkSouls.rl("biped/combat/shield_ha_2"), (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
				.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.FIST_SWING)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 51)
				.addProperty(AttackProperty.STAMINA_USAGE, 28)
				.addProperty(AttackProperty.POISE_DAMAGE, 30)
				.register(builder),
		};
		SHIELD_DASH_ATTACK = new AttackAnimation(DarkSouls.rl("shield_da"), AttackType.DASH, 0.05F, 0.0F, 0.32F, 0.52F, 1.4F, "Tool_R",
				DarkSouls.rl("biped/combat/shield_da"), (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
				.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.FIST_SWING)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 49)
				.addProperty(AttackProperty.STAMINA_USAGE, 25)
				.addProperty(AttackProperty.POISE_DAMAGE, 27)
				.register(builder);
		SHIELD_TH_LIGHT_ATTACK = new AttackAnimation(DarkSouls.rl("shield_th_la"), AttackType.TWO_HANDED_LIGHT, 0.3F, 0.0F, 0.12F, 0.32F, 1.2F, "Tool_R",
				DarkSouls.rl("biped/combat/shield_th_la"), (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
				.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.FIST_SWING)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 48)
				.addProperty(AttackProperty.STAMINA_USAGE, 26)
				.addProperty(AttackProperty.POISE_DAMAGE, 28)
				.register(builder);
		SHIELD_TH_HEAVY_ATTACK = new AttackAnimation[]
				{
						new AttackAnimation(DarkSouls.rl("shield_th_ha_1"), AttackType.TWO_HANDED_HEAVY, 0.2F, 0.0F, 0.28F, 0.48F, 1.2F, "Tool_R",
								DarkSouls.rl("biped/combat/shield_th_ha_1"), (models) -> models.ENTITY_BIPED)
						.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
						.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
						.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.FIST_SWING)
						.addProperty(AttackProperty.STAMINA_DAMAGE, 54)
						.addProperty(AttackProperty.STAMINA_USAGE, 33)
						.addProperty(AttackProperty.POISE_DAMAGE, 35)
						.register(builder),
						new AttackAnimation(DarkSouls.rl("shield_th_ha_2"), AttackType.TWO_HANDED_HEAVY, 0.2F, 0.0F, 0.12F, 0.32F, 1.2F, "Tool_R",
								DarkSouls.rl("biped/combat/shield_th_ha_2"), (models) -> models.ENTITY_BIPED)
						.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
						.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
						.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.FIST_SWING)
						.addProperty(AttackProperty.STAMINA_DAMAGE, 54)
						.addProperty(AttackProperty.STAMINA_USAGE, 33)
						.addProperty(AttackProperty.POISE_DAMAGE, 35)
						.register(builder),
				};
		SHIELD_TH_DASH_ATTACK = new AttackAnimation(DarkSouls.rl("shield_th_da"), AttackType.TWO_HANDED_DASH, 0.2F, 0.0F, 0.12F, 0.32F, 1.4F, "Tool_R",
				DarkSouls.rl("biped/combat/shield_th_da"), (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
				.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.FIST_SWING)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 52)
				.addProperty(AttackProperty.STAMINA_USAGE, 29)
				.addProperty(AttackProperty.POISE_DAMAGE, 33)
				.register(builder);
		
		// Straight Sword
		STRAIGHT_SWORD_LIGHT_ATTACK = new AttackAnimation[]
		{ 		
				new AttackAnimation(DarkSouls.rl("straight_sword_light_attack_1"), AttackType.LIGHT, 0.2F, 0.0F, 0.24F, 0.4F, 0.88F, "Tool_R",
						DarkSouls.rl("biped/combat/straight_sword_light_attack_1"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
					.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
					.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
					.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
					.addProperty(AttackProperty.STAMINA_USAGE, 20)
					.addProperty(AttackProperty.POISE_DAMAGE, 20)
					.register(builder),
				new AttackAnimation(DarkSouls.rl("straight_sword_light_attack_2"), AttackType.LIGHT, 0.2F, 0.0F, 0.08F, 0.3F, 0.8F, "Tool_R",
						DarkSouls.rl("biped/combat/straight_sword_light_attack_2"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
					.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
					.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
					.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
					.addProperty(AttackProperty.STAMINA_USAGE, 20)
					.addProperty(AttackProperty.POISE_DAMAGE, 20)
					.register(builder)
		};
		STRAIGHT_SWORD_HEAVY_ATTACK = new AttackAnimation(DarkSouls.rl("straight_sword_heavy_attack"), AttackType.HEAVY, 0.2F, 0.0F, 0.36F, 0.6F, 1.0F, "Tool_R",
				DarkSouls.rl("biped/combat/straight_sword_heavy_attack"), (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.SLASH)
				.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
				.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
				.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
				.addProperty(AttackProperty.STAMINA_USAGE, 45)
				.addProperty(AttackProperty.POISE_DAMAGE, 20)
				.register(builder);
		STRAIGHT_SWORD_DASH_ATTACK = new AttackAnimation(DarkSouls.rl("straight_sword_dash_attack"), AttackType.DASH, 0.2F, 0.0F, 0.16F, 0.36F, 0.8F, "Tool_R",
				DarkSouls.rl("biped/combat/straight_sword_dash_attack"), (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
				.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
				.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
				.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
				.addProperty(AttackProperty.STAMINA_USAGE, 25)
				.addProperty(AttackProperty.POISE_DAMAGE, 20)
				.register(builder);
		STRAIGHT_SWORD_TH_LIGHT_ATTACK = new AttackAnimation[]
		{
						new AttackAnimation(DarkSouls.rl("straight_sword_th_la_1"), AttackType.TWO_HANDED_LIGHT, 0.1F, 0.0F, 0.36F, 0.6F, 1.52F, "Tool_R",
								DarkSouls.rl("biped/combat/straight_sword_th_la_1"), (models) -> models.ENTITY_BIPED)
							.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
							.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
							.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
							.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
							.addProperty(AttackProperty.STAMINA_USAGE, 23)
							.addProperty(AttackProperty.POISE_DAMAGE, 20)
							.register(builder),
						new AttackAnimation(DarkSouls.rl("straight_sword_th_la_2"), AttackType.TWO_HANDED_LIGHT, 0.1F, 0.0F, 0.42F, 0.48F, 1.6F, "Tool_R",
								DarkSouls.rl("biped/combat/straight_sword_th_la_2"), (models) -> models.ENTITY_BIPED)
							.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
							.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
							.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
							.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
							.addProperty(AttackProperty.STAMINA_USAGE, 23)
							.addProperty(AttackProperty.POISE_DAMAGE, 20)
							.register(builder)
		};
		STRAIGHT_SWORD_TH_HEAVY_ATTACK = new AttackAnimation(DarkSouls.rl("straight_sword_th_ha"), AttackType.TWO_HANDED_HEAVY, 0.2F, 0.0F, 0.36F, 0.56F, 1.2F, "Tool_R",
				DarkSouls.rl("biped/combat/straight_sword_th_ha_1"), (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_THRUST)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
				.addProperty(AttackProperty.STAMINA_USAGE, 20)
				.addProperty(AttackProperty.POISE_DAMAGE, 20)
				.register(builder);
		STRAIGHT_SWORD_TH_DASH_ATTACK = new AttackAnimation(DarkSouls.rl("straight_sword_th_da"), AttackType.TWO_HANDED_DASH, 0.2F, 0.0F, 0.48F, 0.72F, 1.6F, "Tool_R",
				DarkSouls.rl("biped/combat/straight_sword_th_da"), (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
				.addProperty(AttackProperty.STAMINA_USAGE, 20)
				.addProperty(AttackProperty.POISE_DAMAGE, 20)
				.register(builder);

		// Hollow
		HOLLOW_IDLE = new StaticAnimation(DarkSouls.rl("hollow_idle"), 0.2F, true,
				DarkSouls.rl("hollow/idle"), (models) -> models.ENTITY_BIPED).register(builder);
		HOLLOW_WALK = new MovementAnimation(DarkSouls.rl("hollow_walk"), 0.2F, true,
				DarkSouls.rl("hollow/move"), (models) -> models.ENTITY_BIPED).register(builder);
		HOLLOW_RUN = new MovementAnimation(DarkSouls.rl("hollow_run"), 0.2F, true,
				DarkSouls.rl("hollow/run"), (models) -> models.ENTITY_BIPED).register(builder);
		HOLLOW_DEFLECTED = new HitAnimation(DarkSouls.rl("hollow_deflected"), 0.2F, DarkSouls.rl("hollow/deflected"), (models) -> models.ENTITY_BIPED)
				.register(builder);
		HOLLOW_BREAKDOWN = new StaticAnimation(DarkSouls.rl("hollow_breakdown"), 0.2F, true,
				DarkSouls.rl("hollow/breakdown"), (models) -> models.ENTITY_BIPED).register(builder);

		HOLLOW_LIGHT_ATTACKS = new AttackAnimation[]
		{
				new AttackAnimation(DarkSouls.rl("hollow_light_attack_1"), AttackType.LIGHT, 0.2F, 0.0F, 0.56F, 1.05F, 2.5F, "Tool_R",
						DarkSouls.rl("hollow/swing_1"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
					.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
					.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
					.addProperty(AttackProperty.POISE_DAMAGE, 20)
					.register(builder),
				new AttackAnimation(DarkSouls.rl("hollow_light_attack_2"), AttackType.LIGHT, 0.2F, 0.0F, 0.48F, 1.0F, 2.0F, "Tool_R",
						DarkSouls.rl("hollow/swing_2"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
					.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
					.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
					.addProperty(AttackProperty.POISE_DAMAGE, 20)
					.register(builder),
				new AttackAnimation(DarkSouls.rl("hollow_light_attack_3"), AttackType.LIGHT, 0.2F, 0.0F, 0.16F, 0.4F, 2.0F, "Tool_R",
						DarkSouls.rl("hollow/swing_3"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
					.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
					.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
					.addProperty(AttackProperty.POISE_DAMAGE, 20)
					.register(builder)
		};
		HOLLOW_BARRAGE = new AttackAnimation(DarkSouls.rl("hollow_barrage"), AttackType.LIGHT, 0.2F,
				DarkSouls.rl("hollow/fury_attack"), (models) -> models.ENTITY_BIPED,
				new Phase(0.0F, 1.48F, 1.72F, 1.72F, "Tool_R", Colliders.BROKEN_SWORD),
				new Phase(1.72F, 1.8F, 1.92F, 1.92F, "Tool_R", Colliders.BROKEN_SWORD),
				new Phase(1.92F, 2.12F, 2.24F, 2.24F, "Tool_R", Colliders.BROKEN_SWORD),
				new Phase(2.24F, 2.4F, 2.56F, 2.56F, "Tool_R", Colliders.BROKEN_SWORD),
				new Phase(2.56F, 2.76F, 2.88F, 2.88F, "Tool_R", Colliders.BROKEN_SWORD),
				new Phase(2.88F, 3.08F, 3.2F, 4.2F, "Tool_R", Colliders.BROKEN_SWORD))
						.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
						.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.04F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.HOLLOW_PREPARE.get())) })
						.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
						.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
						.addProperty(AttackProperty.POISE_DAMAGE, 20)
						.register(builder);
		HOLLOW_OVERHEAD_SWING = new AttackAnimation(DarkSouls.rl("hollow_overhead_swing"), AttackType.HEAVY, 0.2F, 0.0F, 0.4F, 0.6F, 1.2F, "Tool_R",
				DarkSouls.rl("hollow/overhead_swing"), (models) -> models.ENTITY_BIPED)
						.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
						.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
						.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
						.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
						.addProperty(AttackProperty.POISE_DAMAGE, 20)
						.register(builder);
		HOLLOW_JUMP_ATTACK = new AttackAnimation(DarkSouls.rl("hollow_jump_attack"), AttackType.DASH, 0.05F, 0.0F, 0.52F, 0.72F, 1.6F, "Tool_R",
				DarkSouls.rl("hollow/jump_attack"), (models) -> models.ENTITY_BIPED)
						.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
						.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
						.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
						.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
						.addProperty(AttackProperty.POISE_DAMAGE, 20)
						.register(builder);

		// Hollow Lordran Warrior
		HOLLOW_LORDRAN_WARRIOR_WALK = new MovementAnimation(DarkSouls.rl("hollow_lordran_warrior_walk"), 0.2F, true,
				DarkSouls.rl("hollow_lordran_warrior/move"), (models) -> models.ENTITY_BIPED).register(builder);
		HOLLOW_LORDRAN_WARRIOR_RUN = new MovementAnimation(DarkSouls.rl("hollow_lordran_warrior_run"), 0.2F, true,
				DarkSouls.rl("hollow_lordran_warrior/run"), (models) -> models.ENTITY_BIPED).register(builder);

		HOLLOW_LORDRAN_WARRIOR_TH_LA = new AttackAnimation[]
		{
				new AttackAnimation(DarkSouls.rl("hollow_lordran_warrior_th_la_1"), AttackType.LIGHT, 0.2F, 0.0F, 0.68F, 1.08F, 1.6F, "Tool_R",
						DarkSouls.rl("hollow_lordran_warrior/sword_th_la_1"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.SLASH)
					.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
					.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
					.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
					.addProperty(AttackProperty.POISE_DAMAGE, 20)
					.register(builder),
				new AttackAnimation(DarkSouls.rl("hollow_lordran_warrior_th_la_2"), AttackType.LIGHT, 0.2F, 0.0F, 0.68F, 1.08F, 1.6F, "Tool_R",
						DarkSouls.rl("hollow_lordran_warrior/sword_th_la_2"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.SLASH)
					.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
					.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
					.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
					.addProperty(AttackProperty.POISE_DAMAGE, 20)
					.register(builder)
		};

		HOLLOW_LORDRAN_WARRIOR_DASH_ATTACK = new AttackAnimation(DarkSouls.rl("hollow_lordran_warrior_dash_attack"), AttackType.DASH,
						0.2F, 0.0F, 0.44F, 0.88F, 1.8F, "Tool_R",
						DarkSouls.rl("hollow_lordran_warrior/dash_attack"), (models) -> models.ENTITY_BIPED)
						.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.SLASH)
						.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
						.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
						.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
						.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
						.addProperty(AttackProperty.POISE_DAMAGE, 20)
						.register(builder);

		HOLLOW_LORDRAN_WARRIOR_AXE_LA = new AttackAnimation[]
		{
				new AttackAnimation(DarkSouls.rl("hollow_lordran_warrior_axe_la_1"), AttackType.LIGHT, 0.2F, 0.0F, 0.6F, 1.0F, 2.4F, "Tool_R",
						DarkSouls.rl("hollow_lordran_warrior/axe_la_1"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
					.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
					.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.AXE_SWING)
					.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
					.addProperty(AttackProperty.POISE_DAMAGE, 23)
					.register(builder),
				new AttackAnimation(DarkSouls.rl("hollow_lordran_warrior_axe_la_2"), AttackType.LIGHT, 0.2F, 0.0F, 1.12F, 1.36F, 2.8F, "Tool_R",
						DarkSouls.rl("hollow_lordran_warrior/axe_la_2"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
					.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
					.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.AXE_SWING)
					.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
					.addProperty(AttackProperty.POISE_DAMAGE, 23)
					.register(builder)
		};

		HOLLOW_LORDRAN_WARRIOR_AXE_TH_LA = new AttackAnimation[]
		{
				new AttackAnimation(DarkSouls.rl("hollow_lordran_warrior_axe_th_la_1"), AttackType.LIGHT, 0.2F, 0.0F, 0.56F, 1.0F, 2.8F, "Tool_R",
						DarkSouls.rl("hollow_lordran_warrior/axe_th_la_1"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.SLASH)
					.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
					.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
					.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.AXE_SWING)
					.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
					.addProperty(AttackProperty.POISE_DAMAGE, 23)
					.register(builder),
				new AttackAnimation(DarkSouls.rl("hollow_lordran_warrior_axe_th_la_2"), AttackType.LIGHT, 0.2F, 0.0F, 0.68F, 1.0F, 2.0F, "Tool_R",
						DarkSouls.rl("hollow_lordran_warrior/axe_th_la_2"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.SLASH)
					.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
					.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
					.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.AXE_SWING)
					.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
					.addProperty(AttackProperty.POISE_DAMAGE, 23)
					.register(builder)
		};

		// Hollow Lordran Soldier
		HOLLOW_LORDRAN_SOLDIER_WALK = new MovementAnimation(DarkSouls.rl("hollow_lordran_soldier_walk"), 0.2F, true,
				DarkSouls.rl("hollow_lordran_soldier/walking"), (models) -> models.ENTITY_BIPED).register(builder);
		HOLLOW_LORDRAN_SOLDIER_RUN = new MovementAnimation(DarkSouls.rl("hollow_lordran_soldier_run"), 0.2F, true,
				DarkSouls.rl("hollow_lordran_soldier/run"), (models) -> models.ENTITY_BIPED).register(builder);
		HOLLOW_LORDRAN_SOLDIER_BLOCK = new AdaptableAnimation.Builder(DarkSouls.rl("hollow_lordran_soldier_block"), 0.2F, true, (models) -> models.ENTITY_BIPED)
				.addEntry(LivingMotion.BLOCKING, DarkSouls.rl("hollow_lordran_soldier/block"), DarkSouls.rl("hollow_lordran_soldier/block"), false)
				.addEntry(LivingMotion.WALKING, DarkSouls.rl("hollow_lordran_soldier/block_walking"), DarkSouls.rl("hollow_lordran_soldier/block_walking"), true)
				.addEntry(LivingMotion.RUNNING, DarkSouls.rl("hollow_lordran_soldier/block_run"), DarkSouls.rl("hollow_lordran_soldier/block_run"), true)
				.build()
				.register(builder);
		
		HOLLOW_LORDRAN_SOLDIER_SWORD_LA = new AttackAnimation[]
		{
				new AttackAnimation(DarkSouls.rl("hollow_lordran_soldier_sword_la_1"), AttackType.LIGHT, 0.2F, 0.0F, 0.44F, 0.76F, 1.6F, "Tool_R",
					DarkSouls.rl("hollow_lordran_soldier/sword_la_1"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
					.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
					.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
					.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
					.addProperty(AttackProperty.POISE_DAMAGE, 23)
					.register(builder),
				new AttackAnimation(DarkSouls.rl("hollow_lordran_soldier_sword_la_2"), AttackType.LIGHT, 0.2F, 0.0F, 0.16F, 0.56F, 1.0F, "Tool_R",
					DarkSouls.rl("hollow_lordran_soldier/sword_la_2"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
					.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
					.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
					.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
					.addProperty(AttackProperty.POISE_DAMAGE, 23)
					.register(builder),
				new AttackAnimation(DarkSouls.rl("hollow_lordran_soldier_sword_la_3"), AttackType.LIGHT, 0.2F, 0.0F, 0.44F, 0.6F, 1.6F, "Tool_R",
					DarkSouls.rl("hollow_lordran_soldier/sword_la_3"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
					.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
					.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
					.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
					.addProperty(AttackProperty.POISE_DAMAGE, 23)
					.register(builder)
		};

		HOLLOW_LORDRAN_SOLDIER_SWORD_DA = new AttackAnimation(DarkSouls.rl("hollow_lordran_soldier_sword_da"), AttackType.DASH, 0.2F, 0.0F, 0.35F, 0.5F, 3.0F, "Tool_R",
				DarkSouls.rl("hollow_lordran_soldier/sword_da"), (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
				.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
				.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
				.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
				.addProperty(AttackProperty.POISE_DAMAGE, 23)
				.register(builder);

		HOLLOW_LORDRAN_SOLDIER_SWORD_HEAVY_THRUST = new AttackAnimation(DarkSouls.rl("hollow_lordran_soldier_sword_heavy_thrust"), AttackType.HEAVY, 0.2F, 0.0F,
				1.0F, 1.16F, 2.0F, "Tool_R", DarkSouls.rl("hollow_lordran_soldier/sword_heavy_thrust"),
				(models) -> models.ENTITY_BIPED).addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
						.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
						.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
						.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
						.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
						.addProperty(AttackProperty.POISE_DAMAGE, 23)
						.register(builder);

		HOLLOW_LORDRAN_SOLDIER_SWORD_THRUST_COMBO = new AttackAnimation(DarkSouls.rl("hollow_lordran_soldier_sword_thrust_combo"), AttackType.LIGHT, 0.2F,
				DarkSouls.rl("hollow_lordran_soldier/sword_thrust_combo"), (models) -> models.ENTITY_BIPED,
				new Phase(0.0F, 0.52F, 0.72F, 0.72F, "Tool_R", null),
				new Phase(0.72F, 1.2F, 1.4F, 2.0F, "Tool_R", null))
						.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
						.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
						.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
						.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
						.addProperty(AttackProperty.POISE_DAMAGE, 23)
						.register(builder);

		HOLLOW_LORDRAN_SOLDIER_SPEAR_SWINGS = new AttackAnimation[]
		{
				new AttackAnimation(DarkSouls.rl("hollow_lordran_soldier_spear_swing_1"), AttackType.LIGHT, 0.2F, 0.0F, 0.48F, 0.76F, 1.6F, "Tool_R",
						DarkSouls.rl("hollow_lordran_soldier/spear_swing_1"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
					.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
					.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SPEAR_SWING)
					.addProperty(AttackProperty.STAMINA_DAMAGE, 40)
					.addProperty(AttackProperty.POISE_DAMAGE, 20)
					.register(builder),
				new AttackAnimation(DarkSouls.rl("hollow_lordran_soldier_spear_swing_2"), AttackType.LIGHT, 0.2F, 0.0F, 0.16F, 0.56F, 1.0F, "Tool_R",
						DarkSouls.rl("hollow_lordran_soldier/spear_swing_2"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
					.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
					.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SPEAR_SWING)
					.addProperty(AttackProperty.STAMINA_DAMAGE, 40)
					.addProperty(AttackProperty.POISE_DAMAGE, 20)
					.register(builder),
				new AttackAnimation(DarkSouls.rl("hollow_lordran_soldier_spear_swing_3"), AttackType.LIGHT, 0.2F, 0.0F, 0.6F, 0.72F, 1.6F, "Tool_R",
						DarkSouls.rl("hollow_lordran_soldier/spear_swing_3"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
					.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
					.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
					.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SPEAR_SWING)
					.addProperty(AttackProperty.STAMINA_DAMAGE, 40)
					.addProperty(AttackProperty.POISE_DAMAGE, 20)
					.register(builder),
				new AttackAnimation(DarkSouls.rl("hollow_lordran_soldier_spear_swing_4"), AttackType.LIGHT, 0.2F, 0.0F, 0.44F, 0.6F, 1.6F, "Tool_R",
						DarkSouls.rl("hollow_lordran_soldier/spear_swing_4"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
					.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
					.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SPEAR_SWING)
					.addProperty(AttackProperty.STAMINA_DAMAGE, 40)
					.addProperty(AttackProperty.POISE_DAMAGE, 20)
					.register(builder)
		};

		HOLLOW_LORDRAN_SOLDIER_SPEAR_THRUSTS = new AttackAnimation[]
		{ 
				new AttackAnimation(DarkSouls.rl("hollow_lordran_soldier_spear_thrust_1"), AttackType.LIGHT, 0.2F, 0.0F, 0.64F, 0.8F, 1.6F, "Tool_R",
						DarkSouls.rl("hollow_lordran_soldier/spear_thrust_1"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
					.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
					.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SPEAR_SWING)
					.addProperty(AttackProperty.STAMINA_DAMAGE, 40)
					.addProperty(AttackProperty.POISE_DAMAGE, 20)
					.register(builder),
				new AttackAnimation(DarkSouls.rl("hollow_lordran_soldier_spear_thrust_2"), AttackType.LIGHT, 0.2F, 0.0F, 0.72F, 0.88F, 1.6F, "Tool_R",
						DarkSouls.rl("hollow_lordran_soldier/spear_thrust_2"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
					.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
					.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SPEAR_SWING)
					.addProperty(AttackProperty.STAMINA_DAMAGE, 40)
					.addProperty(AttackProperty.POISE_DAMAGE, 20)
					.register(builder),
				new AttackAnimation(DarkSouls.rl("hollow_lordran_soldier_spear_thrust_3"), AttackType.LIGHT, 0.2F, 0.0F, 0.88F, 1.04F, 1.6F, "Tool_R",
						DarkSouls.rl("hollow_lordran_soldier/spear_thrust_3"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
					.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
					.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
					.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SPEAR_SWING)
					.addProperty(AttackProperty.STAMINA_DAMAGE, 40)
					.addProperty(AttackProperty.POISE_DAMAGE, 20)
					.register(builder)
		};

		HOLLOW_LORDRAN_SOLDIER_SHIELD_BASH = new AttackAnimation(DarkSouls.rl("hollow_lordran_soldier_shield_bash"), AttackType.HEAVY,
					0.2F, 0.0F, 0.6F, 0.8F, 1.6F, "Tool_L",
					DarkSouls.rl("hollow_lordran_soldier/shield_bash"), (models) -> models.ENTITY_BIPED)
						.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
						.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
						.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
						.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.FIST_SWING)
						.addProperty(AttackProperty.STAMINA_DAMAGE, 40)
						.addProperty(AttackProperty.POISE_DAMAGE, 20)
						.register(builder);
		
		// Falconer
		FALCONER_IDLE = new StaticAnimation(DarkSouls.rl("falconer_idle"), 1.0F, true,
				DarkSouls.rl("falconer/idle"), (models) -> models.ENTITY_BIPED).register(builder);
		FALCONER_WALK = new MovementAnimation(DarkSouls.rl("falconer_walk"), 0.2F, true,
				DarkSouls.rl("falconer/walking"), (models) -> models.ENTITY_BIPED).register(builder);
		FALCONER_RUN = new MovementAnimation(DarkSouls.rl("falconer_run"), 0.2F, true,
				DarkSouls.rl("falconer/run"), (models) -> models.ENTITY_BIPED).register(builder);
		
		FALCONER_LIGHT_ATTACKS = new AttackAnimation[]
		{
				new AttackAnimation(DarkSouls.rl("falconer_light_attack_1"), AttackType.LIGHT, 0.2F, 0.0F, 0.56F, 0.68F, 1.88F, "Tool_R",
					DarkSouls.rl("falconer/swing_1"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
					.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
					.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
					.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
					.addProperty(AttackProperty.POISE_DAMAGE, 20)
					.register(builder),
				new AttackAnimation(DarkSouls.rl("falconer_light_attack_2"), AttackType.LIGHT, 0.1F, 0.0F, 0.72F, 1.04F, 1.88F, "Tool_R",
					DarkSouls.rl("falconer/swing_2"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
					.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
					.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
					.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
					.addProperty(AttackProperty.POISE_DAMAGE, 20)
					.register(builder),
				new AttackAnimation(DarkSouls.rl("falconer_light_attack_3"), AttackType.LIGHT, 0.1F, 0.0F, 0.52F, 0.68F, 1.88F, "Tool_R",
					DarkSouls.rl("falconer/swing_3"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
					.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
					.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
					.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
					.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
					.addProperty(AttackProperty.POISE_DAMAGE, 20)
					.register(builder)
		};
		
		
		// Balder Knight
		BALDER_KNIGHT_IDLE = new StaticAnimation(DarkSouls.rl("balder_knight_idle"), 0.3F, true,
				DarkSouls.rl("balder_knight/idle"), (models) -> models.ENTITY_BIPED).register(builder);
		BALDER_KNIGHT_WALK = new StaticAnimation(DarkSouls.rl("balder_knight_walk"), 0.1F, true,
				DarkSouls.rl("balder_knight/walking"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
						{
								Event.create(0.24F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BALDER_KNIGHT_FOOT.get())),
								Event.create(0.8F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BALDER_KNIGHT_FOOT.get()))
						})
				.register(builder);
		BALDER_KNIGHT_RUN = new StaticAnimation(DarkSouls.rl("balder_knight_run"), 0.1F, true,
				DarkSouls.rl("balder_knight/run"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
						{
								Event.create(0.12F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BALDER_KNIGHT_FOOT.get())),
								Event.create(0.5F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BALDER_KNIGHT_FOOT.get()))
						})
				.register(builder);
		BALDER_KNIGHT_BLOCK = new AdaptableAnimation.Builder(DarkSouls.rl("balder_knight_block"), 0.2F, true, (models) -> models.ENTITY_BIPED)
				.addEntry(LivingMotion.BLOCKING, DarkSouls.rl("balder_knight/block"), false)
				.addEntry(LivingMotion.WALKING, DarkSouls.rl("balder_knight/block_walk"), true)
				.addEntry(LivingMotion.RUNNING, DarkSouls.rl("balder_knight/block_run"), true)
				.build()
				.register(builder);
		BALDER_KNIGHT_RAPIER_BLOCK = new AdaptableAnimation.Builder(DarkSouls.rl("balder_knight_rapier_block"), 0.2F, true, (models) -> models.ENTITY_BIPED)
				.addEntry(LivingMotion.IDLE, DarkSouls.rl("balder_knight/rapier_block"), false)
				.addEntry(LivingMotion.WALKING, DarkSouls.rl("balder_knight/rapier_block_walk"), true)
				.addEntry(LivingMotion.RUNNING, DarkSouls.rl("balder_knight/rapier_block_run"), true)
				.build()
				.register(builder);
		
		BALDER_KNIGHT_RAPIER_PARRY = new ParryAnimation(DarkSouls.rl("balder_knight_rapier_parry"), 0.05F, 0.0F, 1.2F, "Tool_L",
				DarkSouls.rl("balder_knight/rapier_parry"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(0.32F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.SWORD_SWING.get()))
				})
				.register(builder);
		
		BALDER_KNIGHT_SIDE_SWORD_LA = new AttackAnimation[]
				{
					new AttackAnimation(DarkSouls.rl("balder_knight_side_sword_la_1"), AttackType.LIGHT, 0.2F, 0.0F, 0.4F, 0.56F, 1.6F, "Tool_R",
							DarkSouls.rl("balder_knight/side_sword_la_1"), (models) -> models.ENTITY_BIPED)
							.addProperty(StaticAnimationProperty.EVENTS, new Event[]
								{
										Event.create(0.44F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BALDER_KNIGHT_FOOT.get())),
										Event.create(1.6F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BALDER_KNIGHT_FOOT.get()))
								})
							.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
							.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
							.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
							.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
							.addProperty(AttackProperty.POISE_DAMAGE, 20)
							.register(builder),
					new AttackAnimation(DarkSouls.rl("balder_knight_side_sword_la_2"), AttackType.LIGHT, 0.2F, 0.0F, 0.16F, 0.4F, 1.6F, "Tool_R",
							DarkSouls.rl("balder_knight/side_sword_la_2"), (models) -> models.ENTITY_BIPED)
							.addProperty(StaticAnimationProperty.EVENTS, new Event[]
								{
										Event.create(0.28F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BALDER_KNIGHT_FOOT.get())),
										Event.create(1.6F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BALDER_KNIGHT_FOOT.get()))
								})
							.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
							.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
							.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
							.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
							.addProperty(AttackProperty.POISE_DAMAGE, 20)
							.register(builder),
					new AttackAnimation(DarkSouls.rl("balder_knight_side_sword_la_3"), AttackType.LIGHT, 0.2F, 0.0F, 0.24F, 0.44F, 1.6F, "Tool_R",
							DarkSouls.rl("balder_knight/side_sword_la_3"), (models) -> models.ENTITY_BIPED)
							.addProperty(StaticAnimationProperty.EVENTS, new Event[]
								{
										Event.create(0.32F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BALDER_KNIGHT_FOOT.get())),
										Event.create(1.6F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BALDER_KNIGHT_FOOT.get()))
								})
							.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
							.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
							.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
							.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
							.addProperty(AttackProperty.POISE_DAMAGE, 20)
							.register(builder)
				};
		BALDER_KNIGHT_SIDE_SWORD_HA = new AttackAnimation(DarkSouls.rl("balder_knight_side_sword_ha"), AttackType.HEAVY, 0.2F, 0.0F, 0.68F, 0.76F, 2.0F, "Tool_R",
				DarkSouls.rl("balder_knight/side_sword_ha"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
						{
								Event.create(0.76F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BALDER_KNIGHT_FOOT.get())),
								Event.create(2.0F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BALDER_KNIGHT_FOOT.get()))
						})
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
				.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 45)
				.addProperty(AttackProperty.POISE_DAMAGE, 23)
				.register(builder);
		BALDER_KNIGHT_SIDE_SWORD_DA = new AttackAnimation(DarkSouls.rl("balder_knight_side_sword_da"), AttackType.DASH, 0.2F, 0.0F, 0.64F, 0.8F, 1.68F, "Tool_R",
				DarkSouls.rl("balder_knight/side_sword_da"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(0.12F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BALDER_KNIGHT_FOOT.get())),
						Event.create(0.24F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BALDER_KNIGHT_FOOT.get())),
						Event.create(0.6F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BALDER_KNIGHT_FOOT.get())),
						Event.create(0.76F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BALDER_KNIGHT_FOOT.get())),
						Event.create(1.6F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BALDER_KNIGHT_FOOT.get()))
				})
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
				.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_THRUST)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 45)
				.addProperty(AttackProperty.POISE_DAMAGE, 23)
				.register(builder);
		BALDER_KNIGHT_SIDE_SWORD_FAST_LA = new AttackAnimation(DarkSouls.rl("balder_knight_side_sword_fast_la"), AttackType.LIGHT, 0.2F,
				DarkSouls.rl("balder_knight/rapier_la"), (models) -> models.ENTITY_BIPED,
				new Phase(0.0F, 0.1F, 0.4F, 0.4F, "Tool_R"),
				new Phase(0.4F, 0.72F, 0.8F, 2.0F, "Tool_R"))
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(0.25F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BALDER_KNIGHT_FOOT.get())),
						Event.create(0.64F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BALDER_KNIGHT_FOOT.get())),
						Event.create(2.0F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BALDER_KNIGHT_FOOT.get()))
				})
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
				.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
				.addProperty(AttackProperty.STUN_TYPE, StunType.LIGHT)
				.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 37)
				.addProperty(AttackProperty.POISE_DAMAGE, 15)
				.register(builder);
		BALDER_KNIGHT_SHIELD_HA = new AttackAnimation(DarkSouls.rl("balder_knight_shield_ha"), AttackType.HEAVY, 0.2F, 0.0F, 0.08F, 0.24F, 1.2F,
				Colliders.SHIELD, "Tool_L", DarkSouls.rl("balder_knight/shield_ha"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(Event.ON_BEGIN, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BALDER_KNIGHT_FOOT.get())),
						Event.create(1.1F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BALDER_KNIGHT_FOOT.get()))
				})
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
				.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
				.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
				.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.FIST_SWING)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 37)
				.addProperty(AttackProperty.POISE_DAMAGE, 15)
				.register(builder);
		
		BALDER_KNIGHT_RAPIER_LA = new AttackAnimation[]
				{
						new AttackAnimation(DarkSouls.rl("balder_knight_rapier_la_1"), AttackType.LIGHT, 0.2F, 0.0F, 0.4F, 0.6F, 1.2F, "Tool_R",
								DarkSouls.rl("balder_knight/rapier_la_1"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new Event[]
									{
											Event.create(0.52F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BALDER_KNIGHT_FOOT.get())),
											Event.create(1.16F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BALDER_KNIGHT_FOOT.get()))
									})
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 37)
								.addProperty(AttackProperty.POISE_DAMAGE, 15)
								.register(builder),
						new AttackAnimation(DarkSouls.rl("balder_knight_rapier_la_2"), AttackType.LIGHT, 0.2F, 0.0F, 0.24F, 0.44F, 1.2F, "Tool_R",
								DarkSouls.rl("balder_knight/rapier_la_2"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new Event[]
									{
											Event.create(0.32F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BALDER_KNIGHT_FOOT.get())),
											Event.create(1.16F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BALDER_KNIGHT_FOOT.get()))
									})
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 37)
								.addProperty(AttackProperty.POISE_DAMAGE, 15)
								.register(builder),
						new AttackAnimation(DarkSouls.rl("balder_knight_rapier_la_3"), AttackType.LIGHT, 0.1F, 0.0F, 0.24F, 0.44F, 1.2F, "Tool_R",
								DarkSouls.rl("balder_knight/rapier_la_2"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new Event[]
									{
											Event.create(0.32F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BALDER_KNIGHT_FOOT.get())),
											Event.create(1.16F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BALDER_KNIGHT_FOOT.get()))
									})
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 37)
								.addProperty(AttackProperty.POISE_DAMAGE, 15)
								.register(builder)
					};
		BALDER_KNIGHT_RAPIER_HA = new AttackAnimation(DarkSouls.rl("balder_knight_rapier_ha"), AttackType.HEAVY, 0.05F, 0.0F, 0.56F, 0.76F, 1.2F, "Tool_R",
				DarkSouls.rl("balder_knight/rapier_ha"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(0.56F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BALDER_KNIGHT_FOOT.get())),
						Event.create(1.16F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BALDER_KNIGHT_FOOT.get()))
				})
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
				.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_THRUST)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 37)
				.addProperty(AttackProperty.POISE_DAMAGE, 15)
				.register(builder);
		BALDER_KNIGHT_RAPIER_DA = new AttackAnimation(DarkSouls.rl("balder_knight_rapier_da"), AttackType.DASH, 0.05F, 0.0F, 0.64F, 0.84F, 1.6F, "Tool_R",
				DarkSouls.rl("balder_knight/rapier_da"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(0.28F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BALDER_KNIGHT_FOOT.get())),
						Event.create(0.76F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BALDER_KNIGHT_FOOT.get())),
						Event.create(1.56F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BALDER_KNIGHT_FOOT.get()))
				})
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
				.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 37)
				.addProperty(AttackProperty.POISE_DAMAGE, 15)
				.register(builder);
		
		
		// Black Knight
		BLACK_KNIGHT_IDLE = new StaticAnimation(DarkSouls.rl("black_knight_idle"), 0.3F, true, DarkSouls.rl("black_knight/idle"), (models) -> models.ENTITY_BIPED)
				.register(builder);
		
		BLACK_KNIGHT_WALK = new StaticAnimation(DarkSouls.rl("black_knight_walking"), 0.1F, true, DarkSouls.rl("black_knight/walking"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
						{
								Event.create(0.24F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BLACK_KNIGHT_FOOT.get())),
								Event.create(0.8F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BLACK_KNIGHT_FOOT.get()))
						})
				.register(builder);
		
		BLACK_KNIGHT_RUN = new StaticAnimation(DarkSouls.rl("black_knight_running"), 0.1F, true, DarkSouls.rl("black_knight/running"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
						{
								Event.create(0.12F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BLACK_KNIGHT_FOOT.get())),
								Event.create(0.5F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BLACK_KNIGHT_FOOT.get()))
						})
				.register(builder);
		
		BLACK_KNIGHT_BLOCK = new AdaptableAnimation.Builder(DarkSouls.rl("black_knight_block"), 0.2F, true, (models) -> models.ENTITY_BIPED)
				.addEntry(LivingMotion.BLOCKING, DarkSouls.rl("black_knight/block"), false)
				.addEntry(LivingMotion.WALKING, DarkSouls.rl("black_knight/block_walk"), true)
				.addEntry(LivingMotion.RUNNING, DarkSouls.rl("black_knight/block_run"), true)
				.build()
				.register(builder);
		
		BLACK_KNIGHT_DEATH = new DeathAnimation(DarkSouls.rl("black_knight_death"), 0.1F, DarkSouls.rl("black_knight/death"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
						{
								Event.create(1.84F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get()))
						})
				.addProperty(DeathProperty.DISAPPEAR_AT, 0.5F)
				.register(builder);
		
		BLACK_KNIGHT_SWORD_LA_LONG = new AttackAnimation[]
				{
						new AttackAnimation(DarkSouls.rl("black_knight_sword_la_long_1"), AttackType.LIGHT, 0.2F, 0.0F, 0.56F, 0.8F, 1.4F, "Tool_R",
							DarkSouls.rl("black_knight/black_knight_sword_la_1"), (models) -> models.ENTITY_BIPED)
							.addProperty(StaticAnimationProperty.EVENTS, new Event[]
								{
										Event.create(0.48F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BLACK_KNIGHT_FOOT.get())),
										Event.create(1.28F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BLACK_KNIGHT_FOOT.get()))
								})
							.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
							.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
							.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
							.addProperty(AttackProperty.STAMINA_DAMAGE, 56)
							.addProperty(AttackProperty.POISE_DAMAGE, 23)
							.register(builder),
						new AttackAnimation(DarkSouls.rl("black_knight_sword_la_long_2"), AttackType.LIGHT, 0.1F, 0.0F, 0.44F, 0.64F, 1.4F, "Tool_R",
							DarkSouls.rl("black_knight/black_knight_sword_la_2"), (models) -> models.ENTITY_BIPED)
							.addProperty(StaticAnimationProperty.EVENTS, new Event[]
								{
										Event.create(0.48F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BLACK_KNIGHT_FOOT.get())),
										Event.create(1.28F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BLACK_KNIGHT_FOOT.get()))
								})
							.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
							.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
							.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
							.addProperty(AttackProperty.STAMINA_DAMAGE, 56)
							.addProperty(AttackProperty.POISE_DAMAGE, 23)
							.register(builder),
						new AttackAnimation(DarkSouls.rl("black_knight_sword_la_long_3"), AttackType.HEAVY, 0.1F, 0.0F, 0.28F, 0.48F, 2.2F, "Tool_R",
							DarkSouls.rl("black_knight/black_knight_sword_la_3"), (models) -> models.ENTITY_BIPED)
							.addProperty(StaticAnimationProperty.EVENTS, new Event[]
								{
										Event.create(0.2F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BLACK_KNIGHT_FOOT.get())),
										Event.create(1.48F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BLACK_KNIGHT_FOOT.get()))
								})
							.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
							.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
							.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
							.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_THRUST)
							.addProperty(AttackProperty.STAMINA_DAMAGE, 56)
							.addProperty(AttackProperty.POISE_DAMAGE, 23)
							.register(builder),
						new AttackAnimation(DarkSouls.rl("black_knight_sword_la_long_4"), AttackType.HEAVY, 0.1F, 0.0F, 0.64F, 0.88F, 1.92F, "Tool_R",
							DarkSouls.rl("black_knight/black_knight_sword_la_4"), (models) -> models.ENTITY_BIPED)
							.addProperty(StaticAnimationProperty.EVENTS, new Event[]
								{
										Event.create(0.72F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BLACK_KNIGHT_FOOT.get())),
										Event.create(1.6F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BLACK_KNIGHT_FOOT.get()))
								})
							.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
							.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
							.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
							.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
							.addProperty(AttackProperty.STAMINA_DAMAGE, 56)
							.addProperty(AttackProperty.POISE_DAMAGE, 23)
							.register(builder)
				};
		BLACK_KNIGHT_SWORD_LA_SHORT = new AttackAnimation[]
				{
						new AttackAnimation(DarkSouls.rl("black_knight_sword_la_short_1"), AttackType.LIGHT, 0.2F, 0.0F, 0.56F, 0.8F, 1.4F, "Tool_R",
							DarkSouls.rl("black_knight/black_knight_sword_la_1"), (models) -> models.ENTITY_BIPED)
							.addProperty(StaticAnimationProperty.EVENTS, new Event[]
								{
										Event.create(0.48F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BLACK_KNIGHT_FOOT.get())),
										Event.create(1.28F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BLACK_KNIGHT_FOOT.get()))
								})
							.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
							.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
							.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
							.addProperty(AttackProperty.STAMINA_DAMAGE, 56)
							.addProperty(AttackProperty.POISE_DAMAGE, 23)
							.register(builder),
						new AttackAnimation(DarkSouls.rl("black_knight_sword_la_short_2"), AttackType.LIGHT, 0.1F, 0.0F, 0.44F, 0.64F, 1.4F, "Tool_R",
							DarkSouls.rl("black_knight/black_knight_sword_la_2"), (models) -> models.ENTITY_BIPED)
							.addProperty(StaticAnimationProperty.EVENTS, new Event[]
								{
										Event.create(0.48F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BLACK_KNIGHT_FOOT.get())),
										Event.create(1.28F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BLACK_KNIGHT_FOOT.get()))
								})
							.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
							.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
							.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
							.addProperty(AttackProperty.STAMINA_DAMAGE, 56)
							.addProperty(AttackProperty.POISE_DAMAGE, 23)
							.register(builder),
						new AttackAnimation(DarkSouls.rl("black_knight_sword_la_short_5"), AttackType.HEAVY, 0.1F, 0.0F, 0.92F, 1.12F, 2.36F, "Tool_R",
							DarkSouls.rl("black_knight/black_knight_sword_la_5"), (models) -> models.ENTITY_BIPED)
							.addProperty(StaticAnimationProperty.EVENTS, new Event[]
								{
										Event.create(0.16F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BLACK_KNIGHT_FOOT.get())),
										Event.create(0.96F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BLACK_KNIGHT_FOOT.get())),
										Event.create(2.0F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BLACK_KNIGHT_FOOT.get()))
								})
							.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
							.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
							.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
							.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
							.addProperty(AttackProperty.STAMINA_DAMAGE, 56)
							.addProperty(AttackProperty.POISE_DAMAGE, 23)
							.register(builder)
				};
		BLACK_KNIGHT_SWORD_HA = new AttackAnimation(DarkSouls.rl("black_knight_sword_ha"), AttackType.HEAVY, 0.2F, 0.0F, 0.4F, 0.64F, 1.68F, "Tool_R",
				DarkSouls.rl("black_knight/black_knight_sword_ha"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
						{
								Event.create(0.48F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BLACK_KNIGHT_FOOT.get())),
								Event.create(1.44F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BLACK_KNIGHT_FOOT.get()))
						})
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
				.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_SWING)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 56)
				.addProperty(AttackProperty.POISE_DAMAGE, 23)
				.register(builder);
		BLACK_KNIGHT_SHIELD_ATTACK = new AttackAnimation(DarkSouls.rl("black_knight_shield_attack"), AttackType.HEAVY, 0.2F, 0.0F, 0.52F, 0.8F, 1.6F, "Tool_L",
				DarkSouls.rl("black_knight/black_knight_shield_attack"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
						{
								Event.create(0.6F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BLACK_KNIGHT_FOOT.get())),
								Event.create(1.52F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BLACK_KNIGHT_FOOT.get()))
						})
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
				.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
				.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
				.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.AXE_SWING)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 40)
				.addProperty(AttackProperty.POISE_DAMAGE, 20)
				.register(builder);
		BLACK_KNIGHT_SWORD_DA = new AttackAnimation(DarkSouls.rl("black_knight_sword_da"), AttackType.DASH, 0.2F, 0.0F, 0.64F, 0.8F, 1.68F, "Tool_R",
				DarkSouls.rl("black_knight/black_knight_sword_da"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(0.12F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BLACK_KNIGHT_FOOT.get())),
						Event.create(0.24F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BLACK_KNIGHT_FOOT.get())),
						Event.create(0.6F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BLACK_KNIGHT_FOOT.get())),
						Event.create(0.76F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BLACK_KNIGHT_FOOT.get())),
						Event.create(1.6F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.BLACK_KNIGHT_FOOT.get()))
				})
				.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
				.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.SWORD_THRUST)
				.addProperty(AttackProperty.STAMINA_DAMAGE, 56)
				.addProperty(AttackProperty.POISE_DAMAGE, 23)
				.register(builder);
		
		
		// Stray Demon
		STRAY_DEMON_IDLE = new StaticAnimation(DarkSouls.rl("stray_demon_idle"), 0.5F, true, DarkSouls.rl("stray_demon/idle"), (models) -> models.ENTITY_STRAY_DEMON)
				.register(builder);
		STRAY_DEMON_WALK = new StaticAnimation(DarkSouls.rl("stray_demon_walk"), 0.5F, true, DarkSouls.rl("stray_demon/walk"), (models) -> models.ENTITY_STRAY_DEMON)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(0.7F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_FOOT.get())),
						Event.create(0.7F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 10, 0.5F)),
						Event.create(1.5F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_FOOT.get())),
						Event.create(1.5F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 10, 0.5F))
				}).register(builder);
		STRAY_DEMON_DEATH = new DeathAnimation(DarkSouls.rl("stray_demon_death"), 0.5F, DarkSouls.rl("stray_demon/death"), (models) -> models.ENTITY_STRAY_DEMON)
				.addProperty(DeathProperty.DISAPPEAR_AT, 1.5F)
				.register(builder);

		STRAY_DEMON_HAMMER_LIGHT_ATTACK = new AttackAnimation[]
		{
				new AttackAnimation(DarkSouls.rl("stray_demon_hammer_la_1"), AttackType.LIGHT, 1.0F, 0.0F, 0.12F, 0.44F, 1.6F, "Tool_R",
					DarkSouls.rl("stray_demon/hammer_la_1"), (models) -> models.ENTITY_STRAY_DEMON)
					.addProperty(StaticAnimationProperty.EVENTS, new Event[]
					{
							Event.create(0.12F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_SWING.get())),
							Event.create(0.12F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 10, 0.5F)),
							Event.create(0.36F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_FOOT.get())),
							Event.create(0.36F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 10, 0.5F)),
							Event.create(1.52F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_FOOT.get())),
							Event.create(1.52F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 10, 0.5F))
					})
					.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
					.addProperty(AttackProperty.STUN_TYPE, StunType.FLY)
					.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
					.addProperty(AttackProperty.STAMINA_DAMAGE, 80)
					.addProperty(AttackProperty.POISE_DAMAGE, 28)
					.register(builder),
				new AttackAnimation(DarkSouls.rl("stray_demon_hammer_la_2"), AttackType.LIGHT, 1.0F, 0.0F, 0.44F, 0.64F, 2.0F, "Tool_R",
					DarkSouls.rl("stray_demon/hammer_la_2"), (models) -> models.ENTITY_STRAY_DEMON)
					.addProperty(StaticAnimationProperty.EVENTS, new Event[]
					{
							Event.create(0.44F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_SWING.get())),
							Event.create(0.44F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 10, 0.5F)),
							Event.create(0.48F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_FOOT.get())),
							Event.create(0.48F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 10, 0.5F)),
							Event.create(1.8F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_FOOT.get())),
							Event.create(1.8F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 10, 0.5F))
					})
					.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
					.addProperty(AttackProperty.STUN_TYPE, StunType.FLY)
					.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
					.addProperty(AttackProperty.STAMINA_DAMAGE, 80)
					.addProperty(AttackProperty.POISE_DAMAGE, 28)
					.register(builder)
		};
		STRAY_DEMON_HAMMER_ALT_LIGHT_ATTACK = new AttackAnimation[]
		{
				new AttackAnimation(DarkSouls.rl("stray_demon_hammer_la_alt_1"), AttackType.LIGHT, 1.0F, 0.0F, 0.16F, 0.3F, 1.6F, "Tool_R",
					DarkSouls.rl("stray_demon/hammer_la_alt_1"), (models) -> models.ENTITY_STRAY_DEMON)
					.addProperty(StaticAnimationProperty.EVENTS, new Event[]
					{
							Event.create(0.16F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_SWING.get())),
							Event.create(0.16F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 10, 0.5F))
					})
					.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
					.addProperty(AttackProperty.STUN_TYPE, StunType.FLY)
					.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
					.addProperty(AttackProperty.STAMINA_DAMAGE, 80)
					.addProperty(AttackProperty.POISE_DAMAGE, 28)
					.register(builder),
				new AttackAnimation(DarkSouls.rl("stray_demon_hammer_la_alt_2"), AttackType.LIGHT, 1.0F, 0.0F, 0.48F, 0.8F, 2.0F, "Tool_R",
					DarkSouls.rl("stray_demon/hammer_la_alt_2"), (models) -> models.ENTITY_STRAY_DEMON)
					.addProperty(StaticAnimationProperty.EVENTS, new Event[]
					{
							Event.create(0.44F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_SWING.get())),
							Event.create(0.44F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 10, 0.5F))
					})
					.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
					.addProperty(AttackProperty.STUN_TYPE, StunType.FLY)
					.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
					.addProperty(AttackProperty.STAMINA_DAMAGE, 80)
					.addProperty(AttackProperty.POISE_DAMAGE, 28)
					.register(builder)
		};
		STRAY_DEMON_HAMMER_HEAVY_ATTACK = new AttackAnimation[]
		{
				new AttackAnimation(DarkSouls.rl("stray_demon_hammer_ha_1"), AttackType.HEAVY, 0.2F, 0.0F, 1.08F, 1.24F, 2.6F, "Tool_R",
					DarkSouls.rl("stray_demon/hammer_ha_1"), (models) -> models.ENTITY_STRAY_DEMON)
					.addProperty(StaticAnimationProperty.EVENTS, new Event[]
					{
						Event.create(1.0F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_FOOT.get())),
						Event.create(1.0F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 10, 0.5F)),
						Event.create(1.12F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_SMASH.get())),
						Event.create(1.12F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 25, 1.5F))
					})
					.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
					.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
					.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
					.addProperty(AttackProperty.STAMINA_DAMAGE, 80)
					.addProperty(AttackProperty.POISE_DAMAGE, 28)
					.register(builder),
				new AttackAnimation(DarkSouls.rl("stray_demon_hammer_ha_2"), AttackType.HEAVY, 0.2F, 0.0F, 1.76F, 1.92F, 3.6F, "Tool_R",
					DarkSouls.rl("stray_demon/hammer_ha_2"), (models) -> models.ENTITY_STRAY_DEMON)
					.addProperty(StaticAnimationProperty.EVENTS, new Event[]
					{
						Event.create(1.68F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_FOOT.get())),
						Event.create(1.68F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 10, 0.5F)),
						Event.create(1.8F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_SMASH.get())),
						Event.create(1.8F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 25, 1.5F))
					})
					.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
					.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
					.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
					.addProperty(AttackProperty.STAMINA_DAMAGE, 80)
					.addProperty(AttackProperty.POISE_DAMAGE, 28)
					.register(builder)
		};
		STRAY_DEMON_HAMMER_DRIVE = new AttackAnimation(DarkSouls.rl("stray_demon_hammer_drive"), AttackType.HEAVY, 0.1F, 0.0F, 0.72F, 0.96F, 2.8F, "Tool_R",
				DarkSouls.rl("stray_demon/hammer_drive"), (models) -> models.ENTITY_STRAY_DEMON)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(0.88F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_SMASH.get())),
						Event.create(0.88F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 25, 1.5F))
				})
				.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
				.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
				.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
				.addProperty(AttackProperty.STAMINA_DAMAGE, 80)
				.addProperty(AttackProperty.POISE_DAMAGE, 28)
				.register(builder);
		STRAY_DEMON_HAMMER_DASH_ATTACK = new AttackAnimation(DarkSouls.rl("stray_demon_hammer_da"), AttackType.DASH, 0.5F, 0.0F, 1.0F, 1.2F, 2.4F,
				"Tool_R", DarkSouls.rl("stray_demon/hammer_da"), (models) -> models.ENTITY_STRAY_DEMON)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(0.24F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_WING.get())),
						Event.create(0.56F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_WING.get())),
						Event.create(0.76F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_LAND.get())),
						Event.create(0.76F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 25, 1.5F)),
						Event.create(1.06F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_SMASH.get())),
						Event.create(1.06F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 25, 1.5F))
				})
				.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
				.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
				.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
				.addProperty(AttackProperty.STAMINA_DAMAGE, 80)
				.addProperty(AttackProperty.POISE_DAMAGE, 28)
				.register(builder);
		STRAY_DEMON_GROUND_POUND = new AttackAnimation(DarkSouls.rl("stray_demon_ground_pound"), AttackType.HEAVY, 0.05F, 0.0F, 1.6F, 1.88F, 3.2F, Colliders.STRAY_DEMON_BODY, "Root",
				DarkSouls.rl("stray_demon/ground_pound"), (models) -> models.ENTITY_STRAY_DEMON)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(0.52F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_WING.get())),
						Event.create(1.0F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_WING.get())),
						Event.create(1.76F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_LAND.get())),
						Event.create(1.76F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 40, 3.0F))
				})
				.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
				.addProperty(AttackProperty.STUN_TYPE, StunType.FLY)
				.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.25F))
				.addProperty(AttackProperty.STAMINA_DAMAGE, 80)
				.addProperty(AttackProperty.POISE_DAMAGE, 28)
				.register(builder);
		
		// Taurus Demon
		TAURUS_DEMON_IDLE = new StaticAnimation(DarkSouls.rl("taurus_demon_idle"), 0.2F, true, DarkSouls.rl("taurus_demon/idle"), (models) -> models.ENTITY_TAURUS_DEMON)
				.register(builder);
		
		// Anastacia of Astora
		ANASTACIA_IDLE = new StaticAnimation(DarkSouls.rl("anastacia_idle"), 0.4F, true,
				DarkSouls.rl("anastacia_of_astora/idle"), (models) -> models.ENTITY_BIPED).register(builder);
		
		return builder;
	}
	
	public static StaticAnimation createSupplier(BiFunction<LivingCap<?>, LayerPart, StaticAnimation> biFunction)
	{
		return new SupplierAnimation(biFunction);
	}
}