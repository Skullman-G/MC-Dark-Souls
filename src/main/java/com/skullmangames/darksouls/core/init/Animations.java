package com.skullmangames.darksouls.core.init;

import java.util.ArrayList;
import java.util.List;

import com.skullmangames.darksouls.common.animation.types.AimingAnimation;
import com.skullmangames.darksouls.common.animation.types.ConsumeAnimation;
import com.skullmangames.darksouls.common.animation.types.DeathAnimation;
import com.skullmangames.darksouls.common.animation.types.DisarmAnimation;
import com.skullmangames.darksouls.common.animation.types.DodgingAnimation;
import com.skullmangames.darksouls.common.animation.types.HitAnimation;
import com.skullmangames.darksouls.common.animation.types.HoldingWeaponAnimation;
import com.skullmangames.darksouls.common.animation.types.InvincibleAnimation;
import com.skullmangames.darksouls.common.animation.types.MirrorAnimation;
import com.skullmangames.darksouls.common.animation.types.MovementAnimation;
import com.skullmangames.darksouls.common.animation.types.ReboundAnimation;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.animation.types.VariableHitAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation.Phase;
import com.skullmangames.darksouls.common.animation.types.attack.CircleParticleSpawner;
import com.skullmangames.darksouls.common.animation.types.attack.Property.AttackProperty;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource.DamageType;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource.StunType;
import com.skullmangames.darksouls.common.animation.types.attack.MountAttackAnimation;
import com.skullmangames.darksouls.common.capability.item.IShield.Deflection;

import net.minecraftforge.api.distmarker.Dist;

public final class Animations
{
	public static final List<StaticAnimation> ANIMATIONS = new ArrayList<>();
	
	
	public static final StaticAnimation DUMMY_ANIMATION = new StaticAnimation();
	
	public static final StaticAnimation BIPED_IDLE = new StaticAnimation(true, 0.2F, true, "biped/living/idle", "biped", true);
	public static final StaticAnimation BIPED_WALK = new MovementAnimation(0.2F, true, "biped/living/walk", "biped");
	public static final StaticAnimation BIPED_RUN = new MovementAnimation(true, "biped/living/run", "biped");
	public static final StaticAnimation BIPED_SNEAK = new MovementAnimation(true, "biped/living/sneak", "biped");
	public static final StaticAnimation BIPED_SWIM = new MovementAnimation(true, "biped/living/swim", "biped");
	public static final StaticAnimation BIPED_FLOAT = new StaticAnimation(true, true, "biped/living/float", "biped", true);
	public static final StaticAnimation BIPED_KNEEL = new StaticAnimation(true, true, "biped/living/kneel", "biped", true);
	public static final StaticAnimation BIPED_FALL = new StaticAnimation(true, false, "biped/living/fall", "biped", true);
	public static final StaticAnimation BIPED_MOUNT = new StaticAnimation(true, true, "biped/living/mount", "biped", true);
	public static final StaticAnimation BIPED_DEATH = new DeathAnimation(0.16F, "biped/living/death", "biped");
	public static final StaticAnimation BIPED_DIG = new StaticAnimation(true, 0.2F, true, "biped/living/dig", "biped", true);
	
	public static final StaticAnimation BIPED_EAT = new ConsumeAnimation(0.2F, true, "biped/living/eat_r", "biped/living/eat_l", "biped", true);
	public static final StaticAnimation BIPED_DRINK = new ConsumeAnimation(0.2F, true, "biped/living/drink_r", "biped/living/drink_l", "biped", true);
	public static final StaticAnimation BIPED_CONSUME_SOUL = new ConsumeAnimation(0.2F, true, "biped/living/consume_soul_r", "biped/living/consume_soul_l", "biped", true);
	public static final StaticAnimation BIPED_BLOCK = new MirrorAnimation(0.2F, true, "biped/combat/block", "biped/combat/block_mirror", "biped", true);
	
	public static final StaticAnimation BIPED_IDLE_CROSSBOW = new StaticAnimation(true, 0.2F, true, "biped/living/idle_crossbow", "biped", true);
	public static final StaticAnimation BIPED_WALK_CROSSBOW = new MovementAnimation(0.2F, true, "biped/living/walk_crossbow", "biped");
	public static final StaticAnimation BIPED_CROSSBOW_AIM = new AimingAnimation(0.16F, false, "biped/combat/crossbow_aim_mid", "biped/combat/crossbow_aim_up", "biped/combat/crossbow_aim_down", "biped", true);
	public static final StaticAnimation BIPED_CROSSBOW_SHOT = new ReboundAnimation(0.16F, false, "biped/combat/crossbow_shot_mid", "biped/combat/crossbow_shot_up", "biped/combat/crossbow_shot_down", "biped", true);
	public static final StaticAnimation BIPED_CROSSBOW_RELOAD = new StaticAnimation(true, 0.16F, false, "biped/combat/crossbow_reload", "biped", true);
	
	public static final StaticAnimation BIPED_BOW_AIM = new AimingAnimation(0.16F, false, "biped/combat/bow_aim_mid", "biped/combat/bow_aim_up", "biped/combat/bow_aim_down", "biped", true);
	public static final StaticAnimation BIPED_BOW_REBOUND = new ReboundAnimation(0.04F, false, "biped/combat/bow_shot_mid", "biped/combat/bow_shot_up", "biped/combat/bow_shot_down", "biped", true);
	
	public static final StaticAnimation BIPED_SPEER_AIM = new AimingAnimation(0.16F, false, "biped/combat/javelin_aim_mid", "biped/combat/javelin_aim_up", "biped/combat/javelin_aim_down", "biped", true);
	public static final StaticAnimation BIPED_SPEER_REBOUND = new ReboundAnimation(0.08F, false, "biped/combat/javelin_throw_mid", "biped/combat/javelin_throw_up", "biped/combat/javelin_throw_down", "biped", true);
	
	public static final StaticAnimation BIPED_HIT_SHORT = new VariableHitAnimation(0.05F, "biped/combat/hit_short", "biped");
	public static final StaticAnimation BIPED_HIT_LONG = new HitAnimation(0.08F, "biped/combat/hit_long", "biped");
	public static final StaticAnimation BIPED_HIT_ON_MOUNT = new HitAnimation(0.08F, "biped/combat/hit_on_mount", "biped");
	public static final StaticAnimation BIPED_HIT_DOWN_BACK = new InvincibleAnimation(0.08F, "biped/combat/hit_down_back", "biped");
	public static final StaticAnimation BIPED_HIT_DOWN_FRONT = new InvincibleAnimation(0.08F, "biped/combat/hit_down_front", "biped");
	public static final StaticAnimation BIPED_LAND_DAMAGE = new HitAnimation(0.08F, "biped/living/land_damage", "biped");
	public static final StaticAnimation BIPED_DODGE = new DodgingAnimation(0.09F, false, "biped/combat/dodge", 0.6F, 0.5F, "biped");
	public static final StaticAnimation BIPED_DISARM_SHIELD = new DisarmAnimation(0.05F, "biped/combat/disarmed_left", "biped");
			
	public static final StaticAnimation BIPED_MOB_THROW = new AttackAnimation(0.11F, 1.0F, 0, 0, 0, false, null, "", "biped/combat/javelin_throw_mid", "biped");
	
	// Big Weapon
	public static final HoldingWeaponAnimation BIPED_HOLDING_BIG_WEAPON = new HoldingWeaponAnimation(0.2F, true, "biped/living/holding_big_weapon_r", "biped/living/holding_big_weapon_l", "biped/living/holding_big_weapon_both", "biped", true);
	public static final AttackAnimation BIG_WEAPON_WEAK_ATTACK = new AttackAnimation(0.9F, 0.0F, 1.0F, 1.72F, 3.6F, false, "111213", "biped/combat/big_weapon_weak_attack", "biped");
	
	// Ultra Greatsword
	public static final AttackAnimation[] ULTRA_GREATSWORD_LIGHT_ATTACK = new AttackAnimation[]
			{
					new AttackAnimation(0.5F, 0.0F, 0.6F, 1.2F, 2.8F, false, "111213", "biped/combat/ultra_greatsword_light_attack_1", "biped")
						.registerSound(ModSoundEvents.ULTRA_GREATSWORD_SMASH, 0.8F, true)
						.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
						.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
						.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH_FRONT)
						.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F)),
					new AttackAnimation(0.5F, 0.0F, 0.4F, 1.0F, 2.8F, false, "111213", "biped/combat/ultra_greatsword_light_attack_2", "biped")
						.registerSound(ModSoundEvents.ULTRA_GREATSWORD_SMASH, 0.84F, true)
						.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
						.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
						.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH_FRONT)
						.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
			};
	public static final AttackAnimation[] ULTRA_GREATSWORD_HEAVY_ATTACK = new AttackAnimation[]
			{
					new AttackAnimation(0.5F, 0.0F, 1.08F, 1.48F, 2.76F, false, "111213", "biped/combat/ultra_greatsword_heavy_attack_1", "biped")
						.registerSound(ModSoundEvents.ULTRA_GREATSWORD_SMASH, 1.28F, true)
						.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
						.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
						.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH_FRONT)
						.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F)),
					new AttackAnimation(0.5F, 0.0F, 1.12F, 1.48F, 2.76F, false, "111213", "biped/combat/ultra_greatsword_heavy_attack_2", "biped")
						.registerSound(ModSoundEvents.ULTRA_GREATSWORD_SMASH, 1.28F, true)
						.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
						.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
						.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH_FRONT)
						.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
			};
	public static final AttackAnimation ULTRA_GREATSWORD_DASH_ATTACK = new AttackAnimation(0.1F, 0.0F, 1.08F, 1.48F, 2.76F, false, "111213", "biped/combat/ultra_greatsword_heavy_attack_1", "biped")
			.registerSound(ModSoundEvents.ULTRA_GREATSWORD_SMASH, 1.28F, true)
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
			.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
			.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH_FRONT)
			.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F));
	
	// Spear
	public static final AttackAnimation SPEAR_DASH_ATTACK = new AttackAnimation(0.2F, 0.0F, 0.12F, 0.36F, 0.8F, false, "111213", "biped/combat/spear_dash_attack", "biped")
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.THRUST)
			.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT);
	public static final AttackAnimation SPEAR_HEAVY_ATTACK = new AttackAnimation(0.35F, 0.0F, 0.52F, 0.8F, 1.4F, false, "111213", "biped/combat/spear_heavy_attack", "biped")
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.THRUST)
			.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM);
	public static final AttackAnimation SPEAR_LIGHT_ATTACK = new AttackAnimation(0.35F, 0.0F, 0.52F, 0.8F, 1.2F, false, "111213", "biped/combat/spear_light_attack", "biped")
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.THRUST)
			.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT);
	
	// Dagger
	public static final AttackAnimation DAGGER_HEAVY_ATTACK = new AttackAnimation(0.2F, 0.0F, 0.68F, 0.96F, 1.6F, false, "111213", "biped/combat/dagger_heavy_attack", "biped")
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.THRUST)
			.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT);
	public static final AttackAnimation[] DAGGER_LIGHT_ATTACK = new AttackAnimation[]
			{
					new AttackAnimation(0.2F, 0.0F, 0.16F, 0.4F, 1.0F, false, "111213", "biped/combat/dagger_light_attack_1", "biped")
						.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.SLASH)
						.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT),
					new AttackAnimation(0.2F, 0.0F, 0.04F, 0.32F, 1.0F, false, "111213", "biped/combat/dagger_light_attack_2", "biped")
						.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.SLASH)
						.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
			};
	
	// Great Hammer
	public static final AttackAnimation GREAT_HAMMER_HEAVY_ATTACK = new AttackAnimation(0.5F, 0.0F, 1.36F, 1.72F, 3.0F, false, "111213", "biped/combat/great_hammer_heavy_attack", "biped")
			.registerSound(ModSoundEvents.GREAT_HAMMER_SMASH, 1.52F, true)
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE)
			.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
			.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH_FRONT)
			.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F));
	public static final AttackAnimation[] GREAT_HAMMER_LIGHT_ATTACK = new AttackAnimation[]
			{
				new AttackAnimation(0.5F, 0.0F, 1.12F, 1.48F, 2.76F, false, "111213", "biped/combat/great_hammer_light_attack_1", "biped")
					.registerSound(ModSoundEvents.GREAT_HAMMER_SMASH, 1.24F, true)
					.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE)
					.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
					.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH_FRONT)
					.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F)),
				new AttackAnimation(0.5F, 0.0F, 1.12F, 1.48F, 2.76F, false, "111213", "biped/combat/great_hammer_light_attack_2", "biped")
					.registerSound(ModSoundEvents.GREAT_HAMMER_SMASH, 1.24F, true)
					.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE)
					.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
					.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH_FRONT)
					.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
			};
	public static final AttackAnimation GREAT_HAMMER_DASH_ATTACK = new AttackAnimation(0.1F, 0.0F, 1.12F, 1.48F, 2.76F, false, "111213", "biped/combat/great_hammer_light_attack_1", "biped")
			.registerSound(ModSoundEvents.GREAT_HAMMER_SMASH, 1.24F, true)
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE)
			.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
			.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH_FRONT)
			.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F));
	
	// Axe
	public static final AttackAnimation AXE_HEAVY_ATTACK = new AttackAnimation(0.3F, 0.0F, 0.4F, 0.72F, 1.2F, false, "111213", "biped/combat/axe_heavy_attack", "biped")
			.registerSound(ModSoundEvents.AXE_SWING, 0.4F, true)
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
			.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY);
	public static final AttackAnimation[] AXE_LIGHT_ATTACK = new AttackAnimation[]
			{
					new AttackAnimation(0.3F, 0.0F, 0.16F, 0.4F, 1.2F, false, "111213", "biped/combat/axe_light_attack_1", "biped")
						.registerSound(ModSoundEvents.AXE_SWING, 0.16F, true)
						.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
						.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM),
					new AttackAnimation(0.2F, 0.0F, 0.12F, 0.4F, 1.0F, false, "111213", "biped/combat/axe_light_attack_2", "biped")
						.registerSound(ModSoundEvents.AXE_SWING, 0.12F, true)
						.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
						.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
			};
	public static final AttackAnimation AXE_DASH_ATTACK = new AttackAnimation(0.2F, 0.0F, 0.28F, 0.6F, 1.2F, false, "111213", "biped/combat/axe_dash_attack", "biped")
			.registerSound(ModSoundEvents.AXE_SWING, 0.3F, true)
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
			.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY);
	
	// Hammer
	public static final AttackAnimation HAMMER_DASH_ATTACK = new AttackAnimation(0.5F, 0.0F, 0.32F, 0.6F, 1.4F, false, "111213", "biped/combat/hammer_dash_attack", "biped")
			.registerSound(ModSoundEvents.AXE_SWING, 0.32F, true)
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE)
			.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY);
	public static final AttackAnimation HAMMER_HEAVY_ATTACK = new AttackAnimation(0.5F, 0.0F, 0.32F, 0.52F, 1.4F, false, "111213", "biped/combat/hammer_heavy_attack", "biped")
			.registerSound(ModSoundEvents.AXE_SWING, 0.32F, true)
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE)
			.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY);
	public static final AttackAnimation[] HAMMER_LIGHT_ATTACK = new AttackAnimation[]
			{
					new AttackAnimation(0.5F, 0.0F, 0.28F, 0.52F, 1.2F, false, "111213", "biped/combat/hammer_light_attack", "biped")
						.registerSound(ModSoundEvents.AXE_SWING, 0.28F, true)
						.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE)
						.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
			};
	
	// Fist
	public static final AttackAnimation[] FIST_LIGHT_ATTACK = new AttackAnimation[]
			{
					new AttackAnimation(0.2F, 0.0F, 0.28F, 0.4F, 1.0F, false, "111213", "biped/combat/fist_light_attack_1", "biped")
						.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE),
					new AttackAnimation(0.2F, 0.0F, 0.08F, 0.24F, 0.8F, false, "111213", "biped/combat/fist_light_attack_2", "biped")
						.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE)
			};
	public static final AttackAnimation FIST_DASH_ATTACK = new AttackAnimation(0.06F, 0.0F, 0.48F, 0.8F, 1.2F, false, "111213", "biped/combat/fist_dash_attack", "biped")
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE)
			.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT);
	public static final AttackAnimation FIST_HEAVY_ATTACK = new AttackAnimation(0.5F, 0.0F, 0.32F, 0.6F, 1.0F, false, "111213", "biped/combat/fist_heavy_attack", "biped")
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE)
			.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT);
	
	// Shield
	public static final AttackAnimation[] SHIELD_LIGHT_ATTACK = new AttackAnimation[]
			{
					new AttackAnimation(0.2F, 0.0F, 0.12F, 0.44F, 0.6F, false, "111213", "biped/combat/shield_strike", "biped")
						.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
						.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE)
			};
	
	// Straight Sword
	public static final AttackAnimation[] STRAIGHT_SWORD_LIGHT_ATTACK = new AttackAnimation[]
			{
					new AttackAnimation(0.2F, 0.0F, 0.32F, 0.6F, 1.2F, false, "111213", "biped/combat/straight_sword_light_attack_1", "biped")
						.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
						.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT),
					new AttackAnimation(0.2F, 0.0F, 0.12F, 0.52F, 1.0F, false, "111213", "biped/combat/straight_sword_light_attack_2", "biped")
						.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
						.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
			};
	public static final AttackAnimation STRAIGHT_SWORD_HEAVY_ATTACK = new AttackAnimation(0.5F, 0.0F, 0.52F, 0.92F, 1.6F, false, "111213", "biped/combat/straight_sword_heavy_attack", "biped")
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.SLASH)
			.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM);
	public static final AttackAnimation STRAIGHT_SWORD_DASH_ATTACK = new AttackAnimation(0.06F, 0.0F, 0.48F, 0.8F, 1.2F, false, "111213", "biped/combat/straight_sword_dash_attack", "biped")
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
			.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM);
	public static final AttackAnimation SWORD_MOUNT_ATTACK = new MountAttackAnimation(0.16F, 0.1F, 0.2F, 0.25F, 0.7F, null, "111213", "biped/combat/sword_mount_attack", "biped");
	
	// Hollow
	public static final StaticAnimation HOLLOW_IDLE = new StaticAnimation(true, 0.2F, true, "hollow/idle", "biped", true);
	public static final StaticAnimation HOLLOW_WALK = new StaticAnimation(true, 0.2F, true, "hollow/move", "biped", true);
	public static final StaticAnimation HOLLOW_DEFLECTED = new HitAnimation(0.2F, "hollow/deflected", "biped");
	public static final StaticAnimation HOLLOW_BREAKDOWN = new StaticAnimation(true, 0.2F, true, "hollow/breakdown", "biped", true);
	
	public static final AttackAnimation[] HOLLOW_LIGHT_ATTACKS = new AttackAnimation[]
						{
								new AttackAnimation(0.05F, 0.0F, 1.4F, 1.6F, 2.4F, false, Colliders.BROKEN_SWORD, "111213", "hollow/swing_1", "biped")
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT),
								new AttackAnimation(0.05F, 0.0F, 1.0F, 1.2F, 1.6F, false, Colliders.BROKEN_SWORD, "111213", "hollow/swing_2", "biped")
									.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT),
								new AttackAnimation(0.05F, 0.0F, 1.08F, 1.24F, 2.4F, false, Colliders.BROKEN_SWORD, "111213", "hollow/swing_3", "biped")
									.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
						};
	public static final AttackAnimation HOLLOW_BARRAGE = new AttackAnimation(0.05F, false, "hollow/fury_attack", "biped",
																new Phase(0.0F, 1.76F, 2.08F, 2.08F, "111213", Colliders.BROKEN_SWORD),
																new Phase(2.08F, 2.12F, 2.28F, 2.28F, "111213", Colliders.BROKEN_SWORD),
																new Phase(2.28F, 2.44F, 2.6F, 2.6F, "111213", Colliders.BROKEN_SWORD),
																new Phase(2.6F, 2.76F, 2.92F, 2.92F, "111213", Colliders.BROKEN_SWORD),
																new Phase(2.92F, 3.08F, 3.24F, 3.24F, "111213", Colliders.BROKEN_SWORD),
																new Phase(3.24F, 3.4F, 3.56F, 4.4F, "111213", Colliders.BROKEN_SWORD))
																.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
																.registerSound(ModSoundEvents.HOLLOW_PREPARE, 0.04F, true);
	public static final AttackAnimation HOLLOW_OVERHEAD_SWING = new AttackAnimation(0.05F, 0.0F, 0.64F, 0.88F, 1.6F, false, Colliders.BROKEN_SWORD, "111213", "hollow/overhead_swing", "biped")
																.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM);
	public static final AttackAnimation HOLLOW_JUMP_ATTACK = new AttackAnimation(0.05F, 0.0F, 0.72F, 1.2F, 1.8F, true, Colliders.BROKEN_SWORD, "111213", "hollow/jump_attack", "biped")
																.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM);
	
	// Hollow Lordran Warrior
	public static final StaticAnimation HOLLOW_LORDRAN_WARRIOR_WALK = new StaticAnimation(true, 0.2F, true, "hollow_lordran_warrior/move", "biped", true);
	
	public static final AttackAnimation[] HOLLOW_LORDRAN_WARRIOR_TH_LA = new AttackAnimation[]
			{
				new AttackAnimation(0.2F, 0.0F, 0.68F, 1.08F, 1.6F, false, "111213", "hollow_lordran_warrior/sword_th_la_1", "biped")
					.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.SLASH)
					.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM),
				new AttackAnimation(0.2F, 0.0F, 0.68F, 1.08F, 1.6F, false, "111213", "hollow_lordran_warrior/sword_th_la_2", "biped")
					.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.SLASH)
					.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
			};
	
	public static final AttackAnimation HOLLOW_LORDRAN_WARRIOR_DASH_ATTACK = new AttackAnimation(0.2F, 0.0F, 0.44F, 0.88F, 1.8F, true, "111213", "hollow_lordran_warrior/dash_attack", "biped")
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.SLASH)
			.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY);
	
	public static final AttackAnimation HOLLOW_LORDRAN_WARRIOR_AXE_LA = new AttackAnimation(0.2F, 0.0F, 0.6F, 1.0F, 2.4F, false, "111213", "hollow_lordran_warrior/axe_la", "biped")
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
			.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM);
	
	public static final AttackAnimation HOLLOW_LORDRAN_WARRIOR_AXE_TH_LA = new AttackAnimation(0.2F, 0.0F, 0.56F, 1.0F, 2.8F, false, "111213", "hollow_lordran_warrior/axe_th_la", "biped")
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.SLASH)
			.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY);
	
	// Asylum Demon
	public static final StaticAnimation ASYLUM_DEMON_IDLE = new StaticAnimation(true, 1.0F, true, "asylum_demon/idle", "asylum_demon", true);
	public static final StaticAnimation ASYLUM_DEMON_MOVE = new StaticAnimation(true, 0.5F, true, "asylum_demon/move", "asylum_demon", true)
			.registerSound(ModSoundEvents.ASYLUM_DEMON_FOOT, 0.4F, true)
			.registerSound(ModSoundEvents.ASYLUM_DEMON_FOOT, 1.2F, true);
	public static final StaticAnimation ASYLUM_DEMON_DEATH = new DeathAnimation(0.5F, "asylum_demon/death", "asylum_demon");
			
	public static final AttackAnimation[] ASYLUM_DEMON_LIGHT_ATTACK = new AttackAnimation[]
			{
					new AttackAnimation(1.0F, 0.0F, 0.52F, 1.0F, 2.0F, false, "11131", "asylum_demon/light_attack_1", "asylum_demon")
						.registerSound(ModSoundEvents.ASYLUM_DEMON_SWING, 0.52F, true)
						.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
						.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH_FRONT)
						.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F)),
					new AttackAnimation(1.0F, 0.0F, 0.6F, 0.92F, 2.0F, false, "11131", "asylum_demon/light_attack_2", "asylum_demon")
						.registerSound(ModSoundEvents.ASYLUM_DEMON_SWING, 0.6F, true)
						.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
						.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH_FRONT)
						.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F)),
					new AttackAnimation(1.0F, 0.0F, 0.6F, 0.84F, 1.2F, false, "11131", "asylum_demon/light_attack_3", "asylum_demon")
						.registerSound(ModSoundEvents.ASYLUM_DEMON_SMASH, 0.72F, true)
						.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
						.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH_FRONT)
						.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
			};
	public static final AttackAnimation ASYLUM_DEMON_HAMMER_DRIVE = new AttackAnimation(1.0F, 0.0F, 0.64F, 1.04F, 2.8F, false, "11131", "asylum_demon/heavy_attack", "asylum_demon")
			.registerSound(ModSoundEvents.ASYLUM_DEMON_SMASH, 0.92F, true)
			.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
			.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH_FRONT)
			.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F));
	public static final AttackAnimation ASYLUM_DEMON_JUMP_ATTACK = new AttackAnimation(1.0F, 0.0F, 0.6F, 1.2F, 2.0F, true, "11131", "asylum_demon/dash_attack", "asylum_demon")
			.registerSound(ModSoundEvents.ASYLUM_DEMON_WING, 0.04F, true)
			.registerSound(ModSoundEvents.ASYLUM_DEMON_LAND, 0.8F, true)
			.registerSound(ModSoundEvents.ASYLUM_DEMON_SMASH, 0.8F, true)
			.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
			.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH_FRONT)
			.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F));
	public static final AttackAnimation ASYLUM_DEMON_GROUND_POUND = new AttackAnimation(1.0F, true, "asylum_demon/ground_pound", "asylum_demon",
			new Phase(0.0F, 2.32F, 2.6F, 2.6F, "", Colliders.ASYLUM_DEMON_BODY),
			new Phase(0.0F, 2.6F, 2.96F, 4.0F, "", Colliders.ASYLUM_DEMON_BODY))
			.registerSound(ModSoundEvents.ASYLUM_DEMON_WING, 0.4F, true)
			.registerSound(ModSoundEvents.ASYLUM_DEMON_WING, 1.0F, true)
			.registerSound(ModSoundEvents.ASYLUM_DEMON_WING, 1.76F, true)
			.registerSound(ModSoundEvents.ASYLUM_DEMON_LAND, 2.76F, true)
			.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
			.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH_FRONT)
			.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.25F));
	
	public static StaticAnimation getById(int id)
	{
		return ANIMATIONS.get(id);
	}
	
	public static void registerAnimations(Dist dist)
	{
		ANIMATIONS.forEach((animation) -> animation.bind(dist));
	}
}