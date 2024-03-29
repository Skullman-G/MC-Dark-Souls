package com.skullmangames.darksouls.core.init;

import java.util.List;

import com.google.common.collect.ImmutableMap;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.animation.AnimationLayer.LayerPart;
import com.skullmangames.darksouls.client.particles.EntityboundParticleOptions;
import com.skullmangames.darksouls.client.particles.spawner.CircleParticleSpawner;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.Property.ActionAnimationProperty;
import com.skullmangames.darksouls.common.animation.Property.AttackProperty;
import com.skullmangames.darksouls.common.animation.Property.StaticAnimationProperty;
import com.skullmangames.darksouls.common.animation.types.ActionAnimation;
import com.skullmangames.darksouls.common.animation.types.AdaptableAnimation;
import com.skullmangames.darksouls.common.animation.types.AdaptableAnimation.AnimConfig;
import com.skullmangames.darksouls.common.animation.types.AimingAnimation;
import com.skullmangames.darksouls.common.animation.types.BlockAnimation;
import com.skullmangames.darksouls.common.animation.types.DeathAnimation;
import com.skullmangames.darksouls.common.animation.types.DodgingAnimation;
import com.skullmangames.darksouls.common.animation.types.HitAnimation;
import com.skullmangames.darksouls.common.animation.types.InvincibleAnimation;
import com.skullmangames.darksouls.common.animation.types.MirrorAnimation;
import com.skullmangames.darksouls.common.animation.types.MovementAnimation;
import com.skullmangames.darksouls.common.animation.types.ReboundAnimation;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation.Event;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation.Event.Side;
import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation.Phase;
import com.skullmangames.darksouls.common.animation.types.attack.CriticalCheckAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.CriticalHitAnimation;
import com.skullmangames.darksouls.common.block.LightSource;
import com.skullmangames.darksouls.core.util.DamageSourceExtended;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.Damage;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.DamageType;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.StunType;
import com.skullmangames.darksouls.core.util.math.MathUtils;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.capability.item.IShield.Deflection;
import com.skullmangames.darksouls.common.entity.projectile.LightningSpear;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
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

	public static StaticAnimation BIPED_BLOCK;
	
	public static StaticAnimation BIPED_HIT_BLOCKED_LEFT;
	public static StaticAnimation BIPED_HIT_BLOCKED_RIGHT;
	
	public static StaticAnimation BIPED_HIT_BLOCKED_FLY_LEFT;
	public static StaticAnimation BIPED_HIT_BLOCKED_FLY_RIGHT;
	
	public static StaticAnimation BIPED_DISARM_SHIELD_LEFT;
	public static StaticAnimation BIPED_DISARM_SHIELD_RIGHT;
	
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
	
	private static Event[] LIGHTNING_SPEAR_EVENTS;
	
	public static StaticAnimation BIPED_CAST_MIRACLE_LIGHTNING_SPEAR;
	public static StaticAnimation HORSEBACK_CAST_MIRACLE_LIGHTNING_SPEAR;
	
	private static Event[] GREAT_LIGHTNING_SPEAR_EVENTS;
	
	public static StaticAnimation BIPED_CAST_MIRACLE_GREAT_LIGHTNING_SPEAR;
	public static StaticAnimation HORSEBACK_CAST_MIRACLE_GREAT_LIGHTNING_SPEAR;
	
	// Big Weapon
	public static MirrorAnimation BIPED_HOLDING_BIG_WEAPON;
	
	// Horseback Attacks
	public static AttackAnimation[] HORSEBACK_LIGHT_ATTACK;
	
	// Backstabs
	public static AttackAnimation BACKSTAB_THRUST;
	public static AttackAnimation BACKSTAB_STRIKE;

	// Ultra Greatsword
	public static AttackAnimation[] ULTRA_GREATSWORD_LIGHT_ATTACK;
	
	public static AttackAnimation[] ULTRA_GREATSWORD_HEAVY_ATTACK;
	public static AttackAnimation ULTRA_GREATSWORD_DASH_ATTACK;

	// Spear
	public static AttackAnimation SPEAR_DASH_ATTACK;
	public static AttackAnimation SPEAR_HEAVY_ATTACK;
	public static AttackAnimation SPEAR_LIGHT_ATTACK;
	public static AttackAnimation SPEAR_LIGHT_BLOCKING_ATTACK;

	// Dagger
	public static AttackAnimation DAGGER_HEAVY_ATTACK;
	public static AttackAnimation[] DAGGER_LIGHT_ATTACK;

	// Great Hammer
	public static AttackAnimation GREAT_HAMMER_HEAVY_ATTACK;
	public static AttackAnimation[] GREAT_HAMMER_LIGHT_ATTACK;
	public static AttackAnimation GREAT_HAMMER_DASH_ATTACK;

	// Axe
	public static AttackAnimation AXE_HEAVY_ATTACK;
	public static AttackAnimation[] AXE_LIGHT_ATTACK;
	public static AttackAnimation AXE_DASH_ATTACK;

	// Hammer
	public static AttackAnimation HAMMER_DASH_ATTACK;
	public static AttackAnimation HAMMER_HEAVY_ATTACK;
	public static AttackAnimation[] HAMMER_LIGHT_ATTACK;

	// Fist
	public static AttackAnimation[] FIST_LIGHT_ATTACK;
	public static AttackAnimation FIST_DASH_ATTACK;
	public static AttackAnimation FIST_HEAVY_ATTACK;

	// Shield
	public static AttackAnimation[] SHIELD_LIGHT_ATTACK;

	// Straight Sword
	public static AttackAnimation[] STRAIGHT_SWORD_LIGHT_ATTACK;
	public static AttackAnimation STRAIGHT_SWORD_HEAVY_ATTACK;
	public static AttackAnimation STRAIGHT_SWORD_DASH_ATTACK;

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

	// Stray Demon
	public static StaticAnimation STRAY_DEMON_IDLE;
	public static StaticAnimation STRAY_DEMON_MOVE;
	public static DeathAnimation STRAY_DEMON_DEATH;

	public static AttackAnimation[] STRAY_DEMON_LIGHT_ATTACK;
	public static AttackAnimation STRAY_DEMON_HAMMER_DRIVE;
	public static AttackAnimation STRAY_DEMON_JUMP_ATTACK;
	public static AttackAnimation STRAY_DEMON_GROUND_POUND;
	
	// Anastacia of Astora
	public static StaticAnimation ANASTACIA_IDLE;
	
	public static ImmutableMap.Builder<ResourceLocation, StaticAnimation> init()
	{
		ImmutableMap.Builder<ResourceLocation, StaticAnimation> builder = ImmutableMap.builder();
		
		DUMMY_ANIMATION = new StaticAnimation();

		BIPED_IDLE = new StaticAnimation(new ResourceLocation(DarkSouls.MOD_ID, "biped_idle"), 0.2F, true,
				DarkSouls.rl("biped/living/idle"), (models) -> models.ENTITY_BIPED).register(builder);
		BIPED_WALK = new MovementAnimation(DarkSouls.rl("biped_walk"), 0.08F, true,
				DarkSouls.rl("biped/living/walk"), (models) -> models.ENTITY_BIPED).register(builder);
		BIPED_RUN = new MovementAnimation(DarkSouls.rl("biped_run"), 0.08F, true,
				DarkSouls.rl("biped/living/run"), (models) -> models.ENTITY_BIPED).register(builder);
		BIPED_SNEAK = new MovementAnimation(DarkSouls.rl("biped_sneak"), 0.08F, true,
				DarkSouls.rl("biped/living/sneak"), (models) -> models.ENTITY_BIPED).register(builder);
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
				DarkSouls.rl("biped/death/backstab_thrust"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(0.44F, Side.CLIENT, (cap) ->
						{
							double yRot = Math.toRadians(MathUtils.toNormalRot(cap.getYRot()));
							float y = cap.getOriginalEntity().getBbHeight() * 0.5F;
							Vec3 pos = cap.getOriginalEntity().position().add(Math.cos(yRot), y, Math.sin(yRot));
							cap.makeImpactParticles(pos, false);
						}),
						Event.create(1.0F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get()))
				}).register(builder);
		
		BIPED_DIG = new StaticAnimation(new ResourceLocation(DarkSouls.MOD_ID, "biped_dig"), 0.2F, true,
				DarkSouls.rl("biped/living/dig"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.LAYER_PART, LayerPart.RIGHT).register(builder);
		BIPED_TOUCH_BONFIRE = new ActionAnimation(DarkSouls.rl("biped_touch_bonfire"), 0.5F,
				DarkSouls.rl("biped/living/touching_bonfire"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(0.5F, Side.CLIENT, (cap) -> { teleportParticles(cap); }),
						Event.create(1.0F, Side.CLIENT, (cap) -> { teleportParticles(cap); }),
						Event.create(1.5F, Side.CLIENT, (cap) -> { teleportParticles(cap); }),
						Event.create(2.0F, Side.CLIENT, (cap) -> { teleportParticles(cap); }),
						Event.create(2.5F, Side.CLIENT, (cap) -> { teleportParticles(cap); }),
						Event.create(3.0F, Side.CLIENT, (cap) -> { teleportParticles(cap); }),
						Event.create(3.5F, Side.CLIENT, (cap) -> { teleportParticles(cap); }),
						Event.create(4.0F, Side.CLIENT, (cap) -> { teleportParticles(cap); }),
						Event.create(2.5F, Side.SERVER, (cap) ->
						{
							cap.playSound(ModSoundEvents.BONFIRE_TELEPORT.get());
						}),
						Event.create(3.2F, Side.SERVER, (cap) ->
						{
							cap.getOriginalEntity().teleportTo(cap.futureTeleport.x, cap.futureTeleport.y, cap.futureTeleport.z);
						}),
				}).register(builder);

		BIPED_EAT = new MirrorAnimation(DarkSouls.rl("biped_eat"), 0.2F, true,
				DarkSouls.rl("biped/living/eat_r"), DarkSouls.rl("biped/living/eat_l"), (models) -> models.ENTITY_BIPED).register(builder);
		BIPED_DRINK = new MirrorAnimation(DarkSouls.rl("biped_drink"), 0.2F, true,
				DarkSouls.rl("biped/living/drink_r"), DarkSouls.rl("biped/living/drink_l"), (models) -> models.ENTITY_BIPED).register(builder);
		BIPED_CONSUME_SOUL = new MirrorAnimation(DarkSouls.rl("biped_consume_soul"), 0.2F, true, DarkSouls.rl("biped/living/consume_soul_r"),
				DarkSouls.rl("biped/living/consume_soul_l"), (models) -> models.ENTITY_BIPED).register(builder);

		BIPED_BLOCK = new AdaptableAnimation(DarkSouls.rl("biped_block"), 0.2F, true, (models) -> models.ENTITY_BIPED,
				new AnimConfig(LivingMotion.BLOCKING, DarkSouls.rl("biped/combat/block_mirror"), DarkSouls.rl("biped/combat/block"), false),
				new AnimConfig(LivingMotion.WALKING, DarkSouls.rl("biped/combat/block_walk_mirror"), DarkSouls.rl("biped/combat/block_walk"), true),
				new AnimConfig(LivingMotion.RUNNING, DarkSouls.rl("biped/combat/block_run_mirror"), DarkSouls.rl("biped/combat/block_run"), true),
				new AnimConfig(LivingMotion.KNEELING, DarkSouls.rl("biped/combat/block_mirror"), DarkSouls.rl("biped/combat/block"), true),
				new AnimConfig(LivingMotion.SNEAKING, DarkSouls.rl("biped/combat/block_mirror"), DarkSouls.rl("biped/combat/block"), true))
				.register(builder);
		
		BIPED_HIT_BLOCKED_LEFT = new BlockAnimation(DarkSouls.rl("biped_hit_blocked_left"), 0.05F,
				DarkSouls.rl("biped/hit/blocked_left"), (models) -> models.ENTITY_BIPED)
				.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true).register(builder);
		BIPED_HIT_BLOCKED_RIGHT = new BlockAnimation(DarkSouls.rl("biped_hit_blocked_right"), 0.05F,
				DarkSouls.rl("biped/hit/blocked_right"), (models) -> models.ENTITY_BIPED)
				.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true).register(builder);
		
		BIPED_HIT_BLOCKED_FLY_LEFT = new InvincibleAnimation(DarkSouls.rl("biped_hit_blocked_fly_left"), 0.05F,
				DarkSouls.rl("biped/hit/blocked_fly_left"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{ Event.create(0.48F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GENERIC_ROLL.get())) }).register(builder);
		BIPED_HIT_BLOCKED_FLY_RIGHT = new InvincibleAnimation(DarkSouls.rl("biped_hit_blocked_fly_right"), 0.05F,
				DarkSouls.rl("biped/hit/blocked_fly_right"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{ Event.create(0.48F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GENERIC_ROLL.get())) }).register(builder);
		
		BIPED_DISARM_SHIELD_LEFT = new ActionAnimation(DarkSouls.rl("biped_disarm_shield_left"), 0.05F,
				DarkSouls.rl("biped/combat/disarmed_left"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{ Event.create(Event.ON_BEGIN, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.PLAYER_SHIELD_DISARMED.get())) }).register(builder);
		BIPED_DISARM_SHIELD_RIGHT = new ActionAnimation(DarkSouls.rl("biped_disarm_shield_right"), 0.05F,
				DarkSouls.rl("biped/combat/disarmed_right"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{ Event.create(Event.ON_BEGIN, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.PLAYER_SHIELD_DISARMED.get())) }).register(builder);
		
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
				DarkSouls.rl("biped/hit/backstab_thrust"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(0.44F, Side.CLIENT, (cap) ->
						{
							double yRot = Math.toRadians(MathUtils.toNormalRot(cap.getYRot()));
							float y = cap.getOriginalEntity().getBbHeight() * 0.5F;
							Vec3 pos = cap.getOriginalEntity().position().add(Math.cos(yRot), y, Math.sin(yRot));
							cap.makeImpactParticles(pos, false);
						}),
						Event.create(1.0F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get()))
				}).register(builder);
		
		BIPED_ROLL = new DodgingAnimation(DarkSouls.rl("biped_roll"), 0.1F, DarkSouls.rl("biped/combat/roll"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{ Event.create(0.28F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.GENERIC_ROLL.get())) }).register(builder);
		BIPED_FAT_ROLL = new DodgingAnimation(DarkSouls.rl("biped_fat_roll"), 0.1F, DarkSouls.rl("biped/combat/fat_roll"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(0.48F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get())),
						Event.create(0.48F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 10, 0.25F))
				}).register(builder);
		BIPED_ROLL_TOO_FAT = new ActionAnimation(DarkSouls.rl("biped_roll_too_fat"), 0.1F,
				DarkSouls.rl("biped/combat/roll_too_fat"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
						{
							Event.create(0.4F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get())),
							Event.create(0.4F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 10, 0.25F))
						}).register(builder);
		BIPED_ROLL_BACK = new DodgingAnimation(DarkSouls.rl("biped_roll_back"), 0.1F, DarkSouls.rl("biped/combat/roll_back"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{ Event.create(0.28F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GENERIC_ROLL.get())) }).register(builder);
		BIPED_ROLL_LEFT = new DodgingAnimation(DarkSouls.rl("biped_roll_left"), 0.1F, true, DarkSouls.rl("biped/combat/roll_left"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{ Event.create(0.28F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GENERIC_ROLL.get())) }).register(builder);
		BIPED_ROLL_RIGHT = new DodgingAnimation(DarkSouls.rl("biped_roll_right"), 0.1F, true, DarkSouls.rl("biped/combat/roll_right"), (models) -> models.ENTITY_BIPED)
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
								{
									LightSource.setLightSource(cap.getLevel(), cap.getOriginalEntity().blockPosition(), 15, 4.5F);
								}),
								Event.create(Event.ON_BEGIN, Side.CLIENT, (cap) ->
								{
									cap.playSound(ModSoundEvents.MIRACLE_USE_PRE.get());
									cap.getLevel().addAlwaysVisibleParticle(new EntityboundParticleOptions(ModParticles.MIRACLE_GLOW.get(), cap.getOriginalEntity().getId()), cap.getX(), cap.getY() + 1, cap.getZ(), 0, 0, 0);
								}),
								Event.create(2.5F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.MIRACLE_USE.get())),
								Event.create(2.5F, Side.CLIENT, (cap) ->
								{
									cap.getLevel().addAlwaysVisibleParticle(ModParticles.MEDIUM_MIRACLE_CIRCLE.get(), cap.getX(), cap.getY() + 0.1F, cap.getZ(), 0, 0, 0);
								}),
								Event.create(2.6F, Side.SERVER, (cap) ->
								{
									List<Entity> targets = cap.getLevel().getEntities(null, new AABB(cap.getX() - 3.0F, cap.getY() - 3.0F, cap.getZ() - 3.0F, cap.getX() + 3.0F, cap.getY() + 3.0F, cap.getZ() + 3.0F));
									for (Entity target : targets)
									{
										if (target instanceof LivingEntity)
										{
											((LivingEntity)target).heal(5);
										}
									}
								})
						}).register(builder);
		
		BIPED_CAST_MIRACLE_HEAL_AID = new ActionAnimation(DarkSouls.rl("biped_cast_miracle_heal_aid"), 0.5F,
				DarkSouls.rl("biped/combat/cast_miracle_fast"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
						{
							Event.create(Event.ON_BEGIN, Side.SERVER, (cap) ->
							{
								LightSource.setLightSource(cap.getLevel(), cap.getOriginalEntity().blockPosition(), 15, 3.0F);
							}),
							Event.create(Event.ON_BEGIN, Side.CLIENT, (cap) ->
							{
								cap.playSound(ModSoundEvents.MIRACLE_USE_PRE.get());
								cap.getLevel().addAlwaysVisibleParticle(new EntityboundParticleOptions(ModParticles.FAST_MIRACLE_GLOW.get(), cap.getOriginalEntity().getId()), cap.getX(), cap.getY() + 1, cap.getZ(), 0, 0, 0);
							}),
							Event.create(1.0F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.MIRACLE_USE.get())),
							Event.create(1.0F, Side.CLIENT, (cap) ->
							{
								cap.getLevel().addAlwaysVisibleParticle(ModParticles.TINY_MIRACLE_CIRCLE.get(), cap.getX(), cap.getY() + 0.1F, cap.getZ(), 0, 0, 0);
							}),
							Event.create(1.1F, Side.SERVER, (cap) ->
							{
								cap.getOriginalEntity().heal(2.5F);
							})
						}).register(builder);
		
		BIPED_CAST_MIRACLE_HOMEWARD = new ActionAnimation(DarkSouls.rl("biped_cast_miracle_homeward"), 0.5F,
				DarkSouls.rl("biped/combat/cast_miracle"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
						{
							Event.create(Event.ON_BEGIN, Side.SERVER, (cap) ->
							{
								LightSource.setLightSource(cap.getLevel(), cap.getOriginalEntity().blockPosition(), 15, 5.0F);
							}),
							Event.create(Event.ON_BEGIN, Side.CLIENT, (cap) ->
							{
								cap.playSound(ModSoundEvents.MIRACLE_USE_PRE.get());
								cap.getLevel().addAlwaysVisibleParticle(new EntityboundParticleOptions(ModParticles.FAST_MIRACLE_GLOW.get(), cap.getOriginalEntity().getId()), cap.getX(), cap.getY() + 1, cap.getZ(), 0, 0, 0);
							}),
							Event.create(2.5F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.MIRACLE_USE.get())),
							Event.create(2.5F, Side.CLIENT, (cap) ->
							{
								cap.getLevel().addAlwaysVisibleParticle(ModParticles.TINY_MIRACLE_CIRCLE.get(), cap.getX(), cap.getY() + 0.1F, cap.getZ(), 0, 0, 0);
							}),
							Event.create(3.5F, Side.SERVER, (cap) ->
							{
								if (cap.getOriginalEntity() instanceof ServerPlayer)
								{
									BlockPos pos = ((ServerPlayer)cap.getOriginalEntity()).getRespawnPosition();
									cap.getOriginalEntity().teleportTo(pos.getX(), pos.getY(), pos.getZ());
								}
							})
						}).register(builder);
		
		BIPED_CAST_MIRACLE_FORCE = new ActionAnimation(DarkSouls.rl("biped_cast_miracle_force"), 0.3F,
				DarkSouls.rl("biped/combat/cast_miracle_force"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
						{
							Event.create(Event.ON_BEGIN, Side.SERVER, (cap) ->
							{
								LightSource.setLightSource(cap.getLevel(), cap.getOriginalEntity().blockPosition(), 15, 1.85F);
							}),
							Event.create(0.56F, Side.CLIENT, (cap) ->
							{
								cap.getLevel().addAlwaysVisibleParticle(ModParticles.FORCE.get(), cap.getX(), cap.getY() + 1.0F, cap.getZ(), 0, 0, 0);
							}),
							Event.create(0.56F, Side.SERVER, (cap) ->
							{
								cap.playSound(ModSoundEvents.MIRACLE_FORCE.get());
							}),
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
													ExtendedDamageSource.causePlayerDamage((Player)cap.getOriginalEntity(), cap.getOriginalEntity().position(), StunType.FLY, 0, 0, 0, new Damage(DamageType.REGULAR, 0))
													: ExtendedDamageSource.causeMobDamage(cap.getOriginalEntity(), cap.getOriginalEntity().position(), StunType.FLY, 0, 0, 0, new Damage(DamageType.REGULAR, 0));
											target.hurt(dmgSource, 0);
										}
										else cap.knockBackEntity(target, 0.5F);
									}
									else cap.knockBackEntity(target, 1.0F);
								}
							})
						}).register(builder);
		
		LIGHTNING_SPEAR_EVENTS = new Event[]
				{
						Event.create(Event.ON_BEGIN, Side.SERVER, (cap) ->
						{
							LightSource.setLightSource(cap.getLevel(), cap.getOriginalEntity().blockPosition(), 15, 1.2F);
						}),
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
		
		GREAT_LIGHTNING_SPEAR_EVENTS = new Event[]
				{
						Event.create(Event.ON_BEGIN, Side.SERVER, (cap) ->
						{
							LightSource.setLightSource(cap.getLevel(), cap.getOriginalEntity().blockPosition(), 15, 1.2F);
						}),
						Event.create(Event.ON_BEGIN, Side.SERVER, (cap) ->
						{
							cap.playSound(ModSoundEvents.LIGHTNING_SPEAR_APPEAR.get());
						}),
						Event.create(Event.ON_BEGIN, Side.CLIENT, (cap) ->
						{
							cap.getLevel().addAlwaysVisibleParticle(new EntityboundParticleOptions(ModParticles.GREAT_LIGHTNING_SPEAR.get(), cap.getOriginalEntity().getId()), cap.getX(), cap.getY() + 1, cap.getZ(), 0, 0, 0);
						}),
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
				DarkSouls.rl("biped/living/holding_big_weapon_r"), DarkSouls.rl("biped/living/holding_big_weapon_l"), (models) -> models.ENTITY_BIPED).register(builder);
		
		// Horseback Attacks
		HORSEBACK_LIGHT_ATTACK = new AttackAnimation[]
		{
					new AttackAnimation(DarkSouls.rl("horseback_light_attack_1"), 0.5F, 0.0F, 0.2F, 0.52F, 1.6F, "Tool_R",
							DarkSouls.rl("biped/combat/horseback_light_attack_1"), (models) -> models.ENTITY_BIPED)
							.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
							.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM).register(builder),
					new AttackAnimation(DarkSouls.rl("horseback_light_attack_2"), 0.5F, 0.0F, 0.12F, 0.48F, 1.6F, "Tool_R",
							DarkSouls.rl("biped/combat/horseback_light_attack_2"), (models) -> models.ENTITY_BIPED)
							.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
							.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM).register(builder)
		};
		
		// Backstabs
		BACKSTAB_THRUST = new CriticalCheckAnimation(DarkSouls.rl("backstab_thrust_check"), 0.2F, 0.0F, 0.36F, 0.64F, 1.44F, false, "Tool_R",
				DarkSouls.rl("biped/combat/backstab_thrust_check"), (models) -> models.ENTITY_BIPED,
				new InvincibleAnimation(DarkSouls.rl("backstab_thrust"), 0.05F, DarkSouls.rl("biped/combat/backstab_thrust"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(Event.ON_BEGIN, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.PLAYER_SHIELD_DISARMED.get())),
						Event.create(0.68F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GENERIC_KICK.get()))
				}).register(builder)).register(builder);
		BACKSTAB_STRIKE = new CriticalCheckAnimation(DarkSouls.rl("backstab_strike_check"), 0.2F, 0.0F, 0.4F, 0.8F, 1.44F, true, "Tool_R",
				DarkSouls.rl("biped/combat/backstab_strike_check"), (models) -> models.ENTITY_BIPED,
				new CriticalHitAnimation(DarkSouls.rl("backstab_strike"), 0.05F, 0.84F, DarkSouls.rl("biped/combat/backstab_strike"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(Event.ON_BEGIN, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.PLAYER_SHIELD_DISARMED.get())),
						Event.create(0.84F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.PLAYER_SHIELD_DISARMED.get()))
				}).register(builder)).register(builder);

		// Ultra Greatsword
		ULTRA_GREATSWORD_LIGHT_ATTACK = new AttackAnimation[]
		{
				new AttackAnimation(DarkSouls.rl("ultra_greatsword_light_attack_1"), 0.3F, 0.0F, 0.48F, 0.88F, 2.8F, "Tool_R",
							DarkSouls.rl("biped/combat/ultra_greatsword_light_attack_1"), (models) -> models.ENTITY_BIPED)
							.addProperty(StaticAnimationProperty.EVENTS, new Event[]
							{
									Event.create(0.7F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.ULTRA_GREATSWORD_SMASH.get())),
									Event.create(0.7F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 20, 1))
							})
							.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
							.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
							.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
							.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
							.register(builder),
				new AttackAnimation(DarkSouls.rl("ultra_greatsword_light_attack_2"), 0.2F, 0.0F, 0.44F, 0.88F, 2.4F, "Tool_R",
							DarkSouls.rl("biped/combat/ultra_greatsword_light_attack_2"), (models) -> models.ENTITY_BIPED)
							.addProperty(StaticAnimationProperty.EVENTS, new Event[]
							{
									Event.create(0.72F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.ULTRA_GREATSWORD_SMASH.get())),
									Event.create(0.72F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 20, 1))
							})
							.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
							.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
							.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
							.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
							.register(builder)
		};
		
		ULTRA_GREATSWORD_HEAVY_ATTACK = new AttackAnimation[]
		{
				new AttackAnimation(DarkSouls.rl("ultra_greatsword_heavy_attack_1"), 0.5F, 0.0F, 1.35F, 1.6F, 3.45F, "Tool_R",
					DarkSouls.rl("biped/combat/ultra_greatsword_heavy_attack_1"), (models) -> models.ENTITY_BIPED)
					.addProperty(StaticAnimationProperty.EVENTS, new Event[]
					{
							Event.create(1.5F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.ULTRA_GREATSWORD_SMASH.get())),
							Event.create(1.5F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 20, 1))
					})
					.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
					.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
					.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
					.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
					.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
					.register(builder),
				new AttackAnimation(DarkSouls.rl("ultra_greatsword_heavy_attack_2"), 0.5F, 0.0F, 1.35F, 1.6F, 3.45F, "Tool_R",
					DarkSouls.rl("biped/combat/ultra_greatsword_heavy_attack_2"), (models) -> models.ENTITY_BIPED)
					.addProperty(StaticAnimationProperty.EVENTS, new Event[]
					{
							Event.create(1.5F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.ULTRA_GREATSWORD_SMASH.get())),
							Event.create(1.5F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 20, 1))
					})
					.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
					.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
					.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
					.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
					.register(builder)
		};
		ULTRA_GREATSWORD_DASH_ATTACK = new AttackAnimation(DarkSouls.rl("ultra_greatsword_dash_attack"), 0.1F, 0.0F, 1.35F, 1.6F, 3.45F, "Tool_R",
				DarkSouls.rl("biped/combat/ultra_greatsword_heavy_attack_1"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(1.6F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.ULTRA_GREATSWORD_SMASH.get())),
						Event.create(1.6F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 20, 1))
				})
				.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
				.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
				.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
				.register(builder);

		// Spear
		SPEAR_DASH_ATTACK = new AttackAnimation(DarkSouls.rl("spear_dash_attack"), 0.2F, 0.0F, 0.15F, 0.3F, 1.0F, "Tool_R",
				DarkSouls.rl("biped/combat/spear_dash_attack"), (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.THRUST)
				.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
				.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
				.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
				.register(builder);
		SPEAR_HEAVY_ATTACK = new AttackAnimation(DarkSouls.rl("spear_heavy_attack"), 0.35F, 0.0F, 0.65F, 0.8F, 1.75F, "Tool_R",
				DarkSouls.rl("biped/combat/spear_heavy_attack"), (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.THRUST)
				.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
				.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
				.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
				.register(builder);
		SPEAR_LIGHT_ATTACK = new AttackAnimation(DarkSouls.rl("spear_light_attack"), 0.15F, 0.0F, 0.65F, 0.8F, 1.5F, "Tool_R",
				DarkSouls.rl("biped/combat/spear_light_attack"), (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.THRUST)
				.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
				.register(builder);
		SPEAR_LIGHT_BLOCKING_ATTACK = new AttackAnimation(DarkSouls.rl("spear_light_blocking_attack"), 0.2F, 0.0F, 0.35F, 0.5F, 1.25F, "Tool_R",
				DarkSouls.rl("biped/combat/spear_light_blocking_attack"), (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.THRUST)
				.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
				.addProperty(AttackProperty.BLOCKING, true)
				.register(builder);

		// Dagger
		DAGGER_HEAVY_ATTACK = new AttackAnimation(DarkSouls.rl("dagger_heavy_attack"), 0.2F, 0.0F, 0.68F, 0.96F, 1.6F, "Tool_R",
				DarkSouls.rl("biped/combat/dagger_heavy_attack"), (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.THRUST)
				.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
				.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
				.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
				.register(builder);
		DAGGER_LIGHT_ATTACK = new AttackAnimation[]
		{
				new AttackAnimation(DarkSouls.rl("dagger_light_attack_1"), 0.2F, 0.0F, 0.16F, 0.4F, 1.0F, "Tool_R",
						DarkSouls.rl("biped/combat/dagger_light_attack_1"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.SLASH)
					.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
					.register(builder),
				new AttackAnimation(DarkSouls.rl("dagger_light_attack_2"), 0.2F, 0.0F, 0.04F, 0.32F, 1.0F, "Tool_R",
						DarkSouls.rl("biped/combat/dagger_light_attack_2"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.SLASH)
					.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
					.register(builder)
		};

		// Great Hammer
		GREAT_HAMMER_HEAVY_ATTACK = new AttackAnimation(DarkSouls.rl("great_hammer_heavy_attack"), 0.5F, 0.0F, 1.57F, 1.9F, 3.75F, "Tool_R",
				DarkSouls.rl("biped/combat/great_hammer_heavy_attack"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(1.8F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GREAT_HAMMER_SMASH.get())),
						Event.create(1.8F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 20, 1))
				})
				.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE)
				.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
				.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
				.register(builder);
		GREAT_HAMMER_LIGHT_ATTACK = new AttackAnimation[]
		{
				new AttackAnimation(DarkSouls.rl("great_hammer_light_attack_1"), 0.5F, 0.0F, 1.35F, 1.6F, 3.45F, "Tool_R",
						DarkSouls.rl("biped/combat/great_hammer_light_attack_1"), (models) -> models.ENTITY_BIPED)
					.addProperty(StaticAnimationProperty.EVENTS, new Event[]
					{
							Event.create(1.5F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GREAT_HAMMER_SMASH.get())),
							Event.create(1.5F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 20, 1))
					})
					.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE)
					.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
					.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
					.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
					.register(builder),
				new AttackAnimation(DarkSouls.rl("great_hammer_light_attack_2"), 0.5F, 0.0F, 1.35F, 1.6F, 3.45F, "Tool_R",
						DarkSouls.rl("biped/combat/great_hammer_light_attack_2"), (models) -> models.ENTITY_BIPED)
					.addProperty(StaticAnimationProperty.EVENTS, new Event[]
					{
							Event.create(1.5F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GREAT_HAMMER_SMASH.get())),
							Event.create(1.5F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 20, 1))
					})
					.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE)
					.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
					.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
					.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
					.register(builder)
		};
		GREAT_HAMMER_DASH_ATTACK = new AttackAnimation(DarkSouls.rl("great_hammer_dash_attack"), 0.1F, 0.0F, 1.35F, 1.6F, 3.45F, "Tool_R",
				DarkSouls.rl("biped/combat/great_hammer_light_attack_1"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(1.5F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.GREAT_HAMMER_SMASH.get())),
						Event.create(1.5F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 20, 1))
				})
				.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE)
				.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
				.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
				.register(builder);

		// Axe
		AXE_HEAVY_ATTACK = new AttackAnimation(DarkSouls.rl("axe_heavy_attack"), 0.3F, 0.0F, 0.55F, 0.7F, 1.5F, "Tool_R",
				DarkSouls.rl("biped/combat/axe_heavy_attack"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.4F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.AXE_SWING.get())) })
				.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
				.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
				.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.register(builder);
		AXE_LIGHT_ATTACK = new AttackAnimation[]
		{
				new AttackAnimation(DarkSouls.rl("axe_light_attack_1"), 0.3F, 0.0F, 0.2F, 0.35F, 1.5F, "Tool_R",
						DarkSouls.rl("biped/combat/axe_light_attack_1"), (models) -> models.ENTITY_BIPED)
					.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.16F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.AXE_SWING.get())) })
					.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
					.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
					.register(builder),
				new AttackAnimation(DarkSouls.rl("axe_light_attack_2"), 0.2F, 0.0F, 0.15F, 0.4F, 1.25F, "Tool_R",
						DarkSouls.rl("biped/combat/axe_light_attack_2"), (models) -> models.ENTITY_BIPED)
					.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.12F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.AXE_SWING.get())) })
					.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
					.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
					.register(builder)
		};
		AXE_DASH_ATTACK = new AttackAnimation(DarkSouls.rl("axe_dash_attack"), 0.2F, 0.0F, 0.4F, 0.5F, 1.5F, "Tool_R",
				DarkSouls.rl("biped/combat/axe_dash_attack"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.35F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.AXE_SWING.get())) })
				.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
				.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
				.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.register(builder);

		// Hammer
		HAMMER_DASH_ATTACK = new AttackAnimation(DarkSouls.rl("hammer_dash_attack"), 0.5F, 0.0F, 0.32F, 0.6F, 1.4F, "Tool_R",
				DarkSouls.rl("biped/combat/hammer_dash_attack"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.32F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.AXE_SWING.get())) })
				.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE)
				.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
				.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.register(builder);
		HAMMER_HEAVY_ATTACK = new AttackAnimation(DarkSouls.rl("hammer_heavy_attack"), 0.5F, 0.0F, 0.32F, 0.52F, 1.4F, "Tool_R",
				DarkSouls.rl("biped/combat/hammer_heavy_attack"), (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.32F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.AXE_SWING.get())) })
				.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE)
				.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
				.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.register(builder);
		HAMMER_LIGHT_ATTACK = new AttackAnimation[]
		{
				new AttackAnimation(DarkSouls.rl("hammer_light_attack"), 0.5F, 0.0F, 0.28F, 0.52F, 1.2F, "Tool_R",
						DarkSouls.rl("biped/combat/hammer_light_attack"), (models) -> models.ENTITY_BIPED)
					.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.28F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.AXE_SWING.get())) })
					.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE)
					.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
					.register(builder)
		};

		// Fist
		FIST_LIGHT_ATTACK = new AttackAnimation[]
		{
				new AttackAnimation(DarkSouls.rl("fist_light_attack_1"), 0.2F, 0.0F, 0.3F, 0.4F, 1.25F, "Tool_R",
						DarkSouls.rl("biped/combat/fist_light_attack_1"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE)
					.register(builder),
				new AttackAnimation(DarkSouls.rl("fist_light_attack_2"), 0.2F, 0.0F, 0.15F, 0.3F, 1.25F, "Tool_R",
						DarkSouls.rl("biped/combat/fist_light_attack_2"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE)
					.register(builder)
		};
		FIST_DASH_ATTACK = new AttackAnimation(DarkSouls.rl("fist_dash_attack"), 0.3F, 0.0F, 0.15F, 0.3F, 1.0F, "Tool_R",
				DarkSouls.rl("biped/combat/fist_dash_attack"), (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE)
				.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
				.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
				.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
				.register(builder);
		FIST_HEAVY_ATTACK = new AttackAnimation(DarkSouls.rl("fist_heavy_attack"), 0.5F, 0.0F, 0.35F, 0.5F, 1.25F, "Tool_R",
				DarkSouls.rl("biped/combat/fist_heavy_attack"), (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE)
				.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
				.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
				.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
				.register(builder);

		// Shield
		SHIELD_LIGHT_ATTACK = new AttackAnimation[]
		{
				new AttackAnimation(DarkSouls.rl("shield_attack"), 0.3F, 0.0F, 0.12F, 0.32F, 0.8F, "Tool_R",
						DarkSouls.rl("biped/combat/shield_attack"), (models) -> models.ENTITY_BIPED).addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
					.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE)
					.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
					.register(builder)
		};

		// Straight Sword
		STRAIGHT_SWORD_LIGHT_ATTACK = new AttackAnimation[]
		{ 		
				new AttackAnimation(DarkSouls.rl("straight_sword_light_attack_1"), 0.2F, 0.0F, 0.24F, 0.4F, 0.88F, "Tool_R",
						DarkSouls.rl("biped/combat/straight_sword_light_attack_1"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
					.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
					.register(builder),
				new AttackAnimation(DarkSouls.rl("straight_sword_light_attack_2"), 0.2F, 0.0F, 0.08F, 0.3F, 0.8F, "Tool_R",
						DarkSouls.rl("biped/combat/straight_sword_light_attack_2"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
					.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
					.register(builder)
		};
		STRAIGHT_SWORD_HEAVY_ATTACK = new AttackAnimation(DarkSouls.rl("straight_sword_heavy_attack"), 0.2F, 0.0F, 0.36F, 0.52F, 1.0F, "Tool_R",
				DarkSouls.rl("biped/combat/straight_sword_heavy_attack"), (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.SLASH)
				.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
				.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
				.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
				.register(builder);
		STRAIGHT_SWORD_DASH_ATTACK = new AttackAnimation(DarkSouls.rl("straight_sword_dash_attack"), 0.2F, 0.0F, 0.16F, 0.36F, 0.8F, "Tool_R",
				DarkSouls.rl("biped/combat/straight_sword_dash_attack"), (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
				.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
				.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
				.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
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
				new AttackAnimation(DarkSouls.rl("hollow_light_attack_1"), 0.2F, 0.0F, 0.56F, 1.05F, 2.5F, Colliders.BROKEN_SWORD, "Tool_R",
						DarkSouls.rl("hollow/swing_1"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
					.register(builder),
				new AttackAnimation(DarkSouls.rl("hollow_light_attack_2"), 0.2F, 0.0F, 0.48F, 1.0F, 2.0F, Colliders.BROKEN_SWORD, "Tool_R",
						DarkSouls.rl("hollow/swing_2"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
					.register(builder),
				new AttackAnimation(DarkSouls.rl("hollow_light_attack_3"), 0.2F, 0.0F, 0.16F, 0.4F, 2.0F, Colliders.BROKEN_SWORD, "Tool_R",
						DarkSouls.rl("hollow/swing_3"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
					.register(builder)
		};
		HOLLOW_BARRAGE = new AttackAnimation(DarkSouls.rl("hollow_barrage"), 0.2F,
				DarkSouls.rl("hollow/fury_attack"), (models) -> models.ENTITY_BIPED,
				new Phase(0.0F, 1.48F, 1.72F, 1.72F, "Tool_R", Colliders.BROKEN_SWORD),
				new Phase(1.72F, 1.8F, 1.92F, 1.92F, "Tool_R", Colliders.BROKEN_SWORD),
				new Phase(1.92F, 2.12F, 2.24F, 2.24F, "Tool_R", Colliders.BROKEN_SWORD),
				new Phase(2.24F, 2.4F, 2.56F, 2.56F, "Tool_R", Colliders.BROKEN_SWORD),
				new Phase(2.56F, 2.76F, 2.88F, 2.88F, "Tool_R", Colliders.BROKEN_SWORD),
				new Phase(2.88F, 3.08F, 3.2F, 4.2F, "Tool_R", Colliders.BROKEN_SWORD))
						.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
						.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.04F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.HOLLOW_PREPARE.get())) })
						.register(builder);
		HOLLOW_OVERHEAD_SWING = new AttackAnimation(DarkSouls.rl("hollow_overhead_swing"), 0.2F, 0.0F, 0.4F, 0.6F, 1.2F, Colliders.BROKEN_SWORD, "Tool_R",
				DarkSouls.rl("hollow/overhead_swing"), (models) -> models.ENTITY_BIPED)
						.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
						.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
						.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
						.register(builder);
		HOLLOW_JUMP_ATTACK = new AttackAnimation(DarkSouls.rl("hollow_jump_attack"), 0.05F, 0.0F, 0.52F, 0.72F, 1.6F, Colliders.BROKEN_SWORD, "Tool_R",
				DarkSouls.rl("hollow/jump_attack"), (models) -> models.ENTITY_BIPED)
						.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
						.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
						.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
						.register(builder);

		// Hollow Lordran Warrior
		HOLLOW_LORDRAN_WARRIOR_WALK = new MovementAnimation(DarkSouls.rl("hollow_lordran_warrior_walk"), 0.2F, true,
				DarkSouls.rl("hollow_lordran_warrior/move"), (models) -> models.ENTITY_BIPED).register(builder);
		HOLLOW_LORDRAN_WARRIOR_RUN = new MovementAnimation(DarkSouls.rl("hollow_lordran_warrior_run"), 0.2F, true,
				DarkSouls.rl("hollow_lordran_warrior/run"), (models) -> models.ENTITY_BIPED).register(builder);

		HOLLOW_LORDRAN_WARRIOR_TH_LA = new AttackAnimation[]
		{
				new AttackAnimation(DarkSouls.rl("hollow_lordran_warrior_th_la_1"), 0.2F, 0.0F, 0.68F, 1.08F, 1.6F, "Tool_R",
						DarkSouls.rl("hollow_lordran_warrior/sword_th_la_1"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.SLASH)
					.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
					.register(builder),
				new AttackAnimation(DarkSouls.rl("hollow_lordran_warrior_th_la_2"), 0.2F, 0.0F, 0.68F, 1.08F, 1.6F, "Tool_R",
						DarkSouls.rl("hollow_lordran_warrior/sword_th_la_2"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.SLASH)
					.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
					.register(builder)
		};

		HOLLOW_LORDRAN_WARRIOR_DASH_ATTACK = new AttackAnimation(DarkSouls.rl("hollow_lordran_warrior_dash_attack"), 0.2F, 0.0F, 0.44F, 0.88F, 1.8F, "Tool_R",
						DarkSouls.rl("hollow_lordran_warrior/dash_attack"), (models) -> models.ENTITY_BIPED)
						.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.SLASH)
						.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
						.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
						.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
						.register(builder);

		HOLLOW_LORDRAN_WARRIOR_AXE_LA = new AttackAnimation[]
		{
				new AttackAnimation(DarkSouls.rl("hollow_lordran_warrior_axe_la_1"), 0.2F, 0.0F, 0.6F, 1.0F, 2.4F, "Tool_R",
						DarkSouls.rl("hollow_lordran_warrior/axe_la_1"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
					.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
					.register(builder),
				new AttackAnimation(DarkSouls.rl("hollow_lordran_warrior_axe_la_2"), 0.2F, 0.0F, 1.12F, 1.36F, 2.8F, "Tool_R",
						DarkSouls.rl("hollow_lordran_warrior/axe_la_2"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
					.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
					.register(builder)
		};

		HOLLOW_LORDRAN_WARRIOR_AXE_TH_LA = new AttackAnimation[]
		{
				new AttackAnimation(DarkSouls.rl("hollow_lordran_warrior_axe_th_la_1"), 0.2F, 0.0F, 0.56F, 1.0F, 2.8F, "Tool_R",
						DarkSouls.rl("hollow_lordran_warrior/axe_th_la_1"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.SLASH)
					.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
					.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
					.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
					.register(builder),
				new AttackAnimation(DarkSouls.rl("hollow_lordran_warrior_axe_th_la_2"), 0.2F, 0.0F, 0.68F, 1.0F, 2.0F, "Tool_R",
						DarkSouls.rl("hollow_lordran_warrior/axe_th_la_2"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.SLASH)
					.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
					.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
					.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
					.register(builder)
		};

		// Hollow Lordran Soldier
		HOLLOW_LORDRAN_SOLDIER_WALK = new MovementAnimation(DarkSouls.rl("hollow_lordran_soldier_walk"), 0.2F, true,
				DarkSouls.rl("hollow_lordran_soldier/walking"), (models) -> models.ENTITY_BIPED).register(builder);
		HOLLOW_LORDRAN_SOLDIER_RUN = new MovementAnimation(DarkSouls.rl("hollow_lordran_soldier_run"), 0.2F, true,
				DarkSouls.rl("hollow_lordran_soldier/run"), (models) -> models.ENTITY_BIPED).register(builder);
		HOLLOW_LORDRAN_SOLDIER_BLOCK = new AdaptableAnimation(DarkSouls.rl("hollow_lordran_soldier_block"), 0.2F, true, (models) -> models.ENTITY_BIPED,
				new AnimConfig(LivingMotion.BLOCKING, DarkSouls.rl("hollow_lordran_soldier/block"), DarkSouls.rl("hollow_lordran_soldier/block"), false),
				new AnimConfig(LivingMotion.WALKING, DarkSouls.rl("hollow_lordran_soldier/block_walking"), DarkSouls.rl("hollow_lordran_soldier/block_walking"), true),
				new AnimConfig(LivingMotion.RUNNING, DarkSouls.rl("hollow_lordran_soldier/block_run"), DarkSouls.rl("hollow_lordran_soldier/block_run"), true))
				.register(builder);
		
		HOLLOW_LORDRAN_SOLDIER_SWORD_LA = new AttackAnimation[]
		{
				new AttackAnimation(DarkSouls.rl("hollow_lordran_soldier_sword_la_1"), 0.2F, 0.0F, 0.44F, 0.76F, 1.6F, "Tool_R",
					DarkSouls.rl("hollow_lordran_soldier/sword_la_1"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
					.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
					.register(builder),
				new AttackAnimation(DarkSouls.rl("hollow_lordran_soldier_sword_la_2"), 0.2F, 0.0F, 0.16F, 0.56F, 1.0F, "Tool_R",
					DarkSouls.rl("hollow_lordran_soldier/sword_la_2"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
					.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
					.register(builder),
				new AttackAnimation(DarkSouls.rl("hollow_lordran_soldier_sword_la_3"), 0.2F, 0.0F, 0.44F, 0.6F, 1.6F, "Tool_R",
					DarkSouls.rl("hollow_lordran_soldier/sword_la_3"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.THRUST)
					.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
					.register(builder)
		};

		HOLLOW_LORDRAN_SOLDIER_SWORD_DA = new AttackAnimation(DarkSouls.rl("hollow_lordran_soldier_sword_da"), 0.2F, 0.0F, 0.35F, 0.5F, 3.0F, "Tool_R",
				DarkSouls.rl("hollow_lordran_soldier/sword_da"), (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.THRUST)
				.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
				.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
				.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
				.register(builder);

		HOLLOW_LORDRAN_SOLDIER_SWORD_HEAVY_THRUST = new AttackAnimation(DarkSouls.rl("hollow_lordran_soldier_sword_heavy_thrust"), 0.2F, 0.0F,
				1.0F, 1.16F, 2.0F, "Tool_R", DarkSouls.rl("hollow_lordran_soldier/sword_heavy_thrust"),
				(models) -> models.ENTITY_BIPED).addProperty(AttackProperty.DAMAGE_TYPE, DamageType.THRUST)
						.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
						.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
						.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
						.register(builder);

		HOLLOW_LORDRAN_SOLDIER_SWORD_THRUST_COMBO = new AttackAnimation(DarkSouls.rl("hollow_lordran_soldier_sword_thrust_combo"), 0.2F,
				DarkSouls.rl("hollow_lordran_soldier/sword_thrust_combo"), (models) -> models.ENTITY_BIPED,
				new Phase(0.0F, 0.52F, 0.72F, 0.72F, "Tool_R", null), new Phase(0.72F, 1.2F, 1.4F, 2.0F, "Tool_R", null))
						.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.THRUST)
						.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
						.register(builder);

		HOLLOW_LORDRAN_SOLDIER_SPEAR_SWINGS = new AttackAnimation[]
		{
				new AttackAnimation(DarkSouls.rl("hollow_lordran_soldier_spear_swing_1"), 0.2F, 0.0F, 0.48F, 0.76F, 1.6F, "Tool_R",
						DarkSouls.rl("hollow_lordran_soldier/spear_swing_1"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
					.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
					.register(builder),
				new AttackAnimation(DarkSouls.rl("hollow_lordran_soldier_spear_swing_2"), 0.2F, 0.0F, 0.16F, 0.56F, 1.0F, "Tool_R",
						DarkSouls.rl("hollow_lordran_soldier/spear_swing_2"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
					.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
					.register(builder),
				new AttackAnimation(DarkSouls.rl("hollow_lordran_soldier_spear_swing_3"), 0.2F, 0.0F, 0.6F, 0.72F, 1.6F, "Tool_R",
						DarkSouls.rl("hollow_lordran_soldier/spear_swing_3"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.THRUST)
					.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
					.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
					.register(builder),
				new AttackAnimation(DarkSouls.rl("hollow_lordran_soldier_spear_swing_4"), 0.2F, 0.0F, 0.44F, 0.6F, 1.6F, "Tool_R",
						DarkSouls.rl("hollow_lordran_soldier/spear_swing_4"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.THRUST)
					.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
					.register(builder)
		};

		HOLLOW_LORDRAN_SOLDIER_SPEAR_THRUSTS = new AttackAnimation[]
		{ 
				new AttackAnimation(DarkSouls.rl("hollow_lordran_soldier_spear_thrust_1"), 0.2F, 0.0F, 0.64F, 0.8F, 1.6F, "Tool_R",
						DarkSouls.rl("hollow_lordran_soldier/spear_thrust_1"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.THRUST)
					.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
					.register(builder),
				new AttackAnimation(DarkSouls.rl("hollow_lordran_soldier_spear_thrust_2"), 0.2F, 0.0F, 0.72F, 0.88F, 1.6F, "Tool_R",
						DarkSouls.rl("hollow_lordran_soldier/spear_thrust_2"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.THRUST)
					.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
					.register(builder),
				new AttackAnimation(DarkSouls.rl("hollow_lordran_soldier_spear_thrust_3"), 0.2F, 0.0F, 0.88F, 1.04F, 1.6F, "Tool_R",
						DarkSouls.rl("hollow_lordran_soldier/spear_thrust_3"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.THRUST)
					.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
					.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
					.register(builder)
		};

		HOLLOW_LORDRAN_SOLDIER_SHIELD_BASH = new AttackAnimation(DarkSouls.rl("hollow_lordran_soldier_shield_bash"), 0.2F, 0.0F, 0.6F, 0.8F, 1.6F, "Tool_L",
						DarkSouls.rl("hollow_lordran_soldier/shield_bash"), (models) -> models.ENTITY_BIPED)
						.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
						.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
						.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
						.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
						.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.FIST_SWING)
						.register(builder);
		
		// Falconer
		FALCONER_IDLE = new StaticAnimation(DarkSouls.rl("falconer_idle"), 1.0F, true, DarkSouls.rl("falconer/idle"), (models) -> models.ENTITY_BIPED).register(builder);
		FALCONER_WALK = new MovementAnimation(DarkSouls.rl("falconer_walk"), 0.2F, true,
				DarkSouls.rl("falconer/walking"), (models) -> models.ENTITY_BIPED).register(builder);
		FALCONER_RUN = new MovementAnimation(DarkSouls.rl("falconer_run"), 0.2F, true,
				DarkSouls.rl("falconer/run"), (models) -> models.ENTITY_BIPED).register(builder);
		
		FALCONER_LIGHT_ATTACKS = new AttackAnimation[]
		{
				new AttackAnimation(DarkSouls.rl("falconer_light_attack_1"), 0.2F, 0.0F, 0.56F, 0.68F, 1.88F, "Tool_R",
					DarkSouls.rl("falconer/swing_1"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
					.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
					.register(builder),
				new AttackAnimation(DarkSouls.rl("falconer_light_attack_2"), 0.1F, 0.0F, 0.72F, 1.04F, 1.88F, "Tool_R",
					DarkSouls.rl("falconer/swing_2"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
					.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
					.register(builder),
				new AttackAnimation(DarkSouls.rl("falconer_light_attack_3"), 0.1F, 0.0F, 0.52F, 0.68F, 1.88F, "Tool_R",
					DarkSouls.rl("falconer/swing_3"), (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
					.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
					.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
					.register(builder)
		};
		
		

		// Stray Demon
		STRAY_DEMON_IDLE = new StaticAnimation(DarkSouls.rl("stray_demon_idle"), 1.0F, true, DarkSouls.rl("stray_demon/idle"), (models) -> models.ENTITY_STRAY_DEMON)
				.register(builder);
		STRAY_DEMON_MOVE = new StaticAnimation(DarkSouls.rl("stray_demon_move"), 0.5F, true, DarkSouls.rl("stray_demon/move"), (models) -> models.ENTITY_STRAY_DEMON)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(0.4F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_FOOT.get())),
						Event.create(0.4F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 10, 0.5F)),
						Event.create(1.2F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_FOOT.get())),
						Event.create(1.2F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 10, 0.5F))
				}).register(builder);
		STRAY_DEMON_DEATH = new DeathAnimation(DarkSouls.rl("stray_demon_death"), 0.5F, DarkSouls.rl("stray_demon/death"), (models) -> models.ENTITY_STRAY_DEMON)
				.register(builder);

		STRAY_DEMON_LIGHT_ATTACK = new AttackAnimation[]
		{
				new AttackAnimation(DarkSouls.rl("stray_demon_light_attack_1"), 1.0F, 0.0F, 0.52F, 1.0F, 2.0F, "Tool_R",
					DarkSouls.rl("stray_demon/light_attack_1"), (models) -> models.ENTITY_STRAY_DEMON)
					.addProperty(StaticAnimationProperty.EVENTS, new Event[]
					{
							Event.create(0.52F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_SWING.get())),
							Event.create(0.52F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 10, 0.5F))
					})
					.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
					.addProperty(AttackProperty.STUN_TYPE, StunType.FLY)
					.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
					.register(builder),
				new AttackAnimation(DarkSouls.rl("stray_demon_light_attack_2"), 1.0F, 0.0F, 0.6F, 0.92F, 2.0F, "Tool_R",
					DarkSouls.rl("stray_demon/light_attack_2"), (models) -> models.ENTITY_STRAY_DEMON)
					.addProperty(StaticAnimationProperty.EVENTS, new Event[]
					{
							Event.create(0.6F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_SWING.get())),
							Event.create(0.6F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 10, 0.5F))
					})
					.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
					.addProperty(AttackProperty.STUN_TYPE, StunType.FLY)
					.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
					.register(builder),
				new AttackAnimation(DarkSouls.rl("stray_demon_light_attacks_3"), 1.0F, 0.0F, 0.6F, 0.84F, 1.2F, "Tool_R",
					DarkSouls.rl("stray_demon/light_attack_3"), (models) -> models.ENTITY_STRAY_DEMON)
					.addProperty(StaticAnimationProperty.EVENTS, new Event[]
					{
							Event.create(0.72F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_SMASH.get())),
							Event.create(0.72F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 25, 1.5F))
					})
					.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
					.addProperty(AttackProperty.STUN_TYPE, StunType.FLY)
					.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
					.register(builder)
		};
		STRAY_DEMON_HAMMER_DRIVE = new AttackAnimation(DarkSouls.rl("stray_demon_heavy_attack"), 1.0F, 0.0F, 0.64F, 1.04F, 2.8F, "Tool_R",
				DarkSouls.rl("stray_demon/heavy_attack"), (models) -> models.ENTITY_STRAY_DEMON)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(0.92F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_SMASH.get())),
						Event.create(0.92F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 25, 1.5F))
				})
				.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
				.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
				.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
				.register(builder);
		STRAY_DEMON_JUMP_ATTACK = new AttackAnimation(DarkSouls.rl("stray_demon_jump_attack"), 1.0F, 0.0F, 0.6F, 1.2F, 2.0F,
				"Tool_R", DarkSouls.rl("stray_demon/dash_attack"), (models) -> models.ENTITY_STRAY_DEMON)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(0.04F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_WING.get())),
						Event.create(0.8F, Side.SERVER, (cap) -> { cap.playSound(ModSoundEvents.STRAY_DEMON_LAND.get()); cap.playSound(ModSoundEvents.STRAY_DEMON_SMASH.get()); }),
						Event.create(0.8F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 25, 1.5F))
				})
				.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
				.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
				.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
				.register(builder);
		STRAY_DEMON_GROUND_POUND = new AttackAnimation(DarkSouls.rl("stray_demon_ground_pound"), 1.0F, 0.0F, 2.48F, 2.76F, 4.0F, Colliders.STRAY_DEMON_BODY, "Root",
				DarkSouls.rl("stray_demon/ground_pound"), (models) -> models.ENTITY_STRAY_DEMON)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(0.4F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_WING.get())),
						Event.create(1.0F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_WING.get())),
						Event.create(1.76F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_WING.get())),
						Event.create(2.76F, Side.SERVER, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_LAND.get())),
						Event.create(2.76F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 40, 3.0F))
				})
				.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
				.addProperty(AttackProperty.STUN_TYPE, StunType.FLY)
				.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.25F))
				.register(builder);
		
		// Anastacia of Astora
		ANASTACIA_IDLE = new StaticAnimation(DarkSouls.rl("anastacia_idle"), 0.4F, true,
				DarkSouls.rl("anastacia_of_astora/idle"), (models) -> models.ENTITY_BIPED).register(builder);
		
		return builder;
	}
}