package com.skullmangames.darksouls.core.init;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.skullmangames.darksouls.common.animation.types.AimingAnimation;
import com.skullmangames.darksouls.common.animation.types.ConsumeAnimation;
import com.skullmangames.darksouls.common.animation.types.DeathAnimation;
import com.skullmangames.darksouls.common.animation.types.DisarmAnimation;
import com.skullmangames.darksouls.common.animation.types.DodgingAnimation;
import com.skullmangames.darksouls.common.animation.types.HitAnimation;
import com.skullmangames.darksouls.common.animation.types.HoldingWeaponAnimation;
import com.skullmangames.darksouls.common.animation.types.MirrorAnimation;
import com.skullmangames.darksouls.common.animation.types.MovementAnimation;
import com.skullmangames.darksouls.common.animation.types.ReboundAnimation;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.animation.types.VariableHitAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation.Phase;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource.DamageType;
import com.skullmangames.darksouls.core.util.Property.AttackProperty;
import com.skullmangames.darksouls.common.animation.types.attack.MountAttackAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.TargetTraceJumpAnimation;
import com.skullmangames.darksouls.common.capability.item.IShield.Deflection;

import net.minecraft.util.Hand;
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
	public static final StaticAnimation BIPED_LAND_DAMAGE = new HitAnimation(0.08F, "biped/living/land_damage", "biped");
	public static final StaticAnimation BIPED_DODGE = new DodgingAnimation(0.09F, false, "biped/combat/dodge", 0.6F, 0.5F, "biped");
	public static final StaticAnimation BIPED_DISARM_SHIELD = new DisarmAnimation(0.05F, "biped/combat/disarmed_left", "biped");
			
	public static final StaticAnimation BIPED_MOB_THROW = new AttackAnimation(0.11F, 1.0F, 0, 0, 0, false, null, "", "biped/combat/javelin_throw_mid", "biped");
	
	public static final HoldingWeaponAnimation BIPED_HOLDING_GREAT_HAMMER = new HoldingWeaponAnimation(0.2F, true, "biped/living/holding_great_hammer_r", "biped/living/holding_great_hammer_l", "biped/living/holding_great_hammer_both", "biped", true);
	
	public static final AttackAnimation GREAT_HAMMER_WEAK_ATTACK = new AttackAnimation(0.9F, 0.0F, 1.0F, 1.72F, 3.6F, false, "111213", "biped/combat/great_hammer_weak_attack", "biped");
	
	public static final AttackAnimation GREAT_HAMMER_HEAVY_ATTACK = new AttackAnimation(0.2F, 0.0F, 1.36F, 1.72F, 3.0F, false, "111213", "biped/combat/great_hammer_heavy_attack", "biped")
			.registerSound(ModSoundEvents.GREAT_HAMMER_SMASH, 1.52F, true)
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE)
			.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY);
	
	public static final AttackAnimation[] GREAT_HAMMER_LIGHT_ATTACK = new AttackAnimation[]
			{
				new AttackAnimation(0.2F, 0.0F, 1.12F, 1.48F, 2.76F, false, "111213", "biped/combat/great_hammer_light_attack_1", "biped")
					.registerSound(ModSoundEvents.GREAT_HAMMER_SMASH, 1.24F, true)
					.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE)
					.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY),
				new AttackAnimation(0.2F, 0.0F, 1.12F, 1.48F, 2.76F, false, "111213", "biped/combat/great_hammer_light_attack_2", "biped")
					.registerSound(ModSoundEvents.GREAT_HAMMER_SMASH, 1.24F, true)
					.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE)
					.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
			};
	
	public static final List<AttackAnimation> AXE_LIGHT_ATTACK = new ArrayList<AttackAnimation>(Arrays.asList(new AttackAnimation[]
			{
					new AttackAnimation(0.16F, 0.25F, 0.25F, 0.35F, 0.7F, false, null, "111213", "biped/combat/axe_auto1", "biped"),
					new AttackAnimation(0.16F, 0.25F, 0.25F, 0.35F, 0.85F, false, null, "111213", "biped/combat/axe_auto2", "biped")
			}));
	public static final AttackAnimation AXE_DASH_ATTACK = new AttackAnimation(0.25F, 0.08F, 0.4F, 0.46F, 0.9F, false, null, "111213", "biped/combat/axe_dash", "biped");
	public static final AttackAnimation GUILLOTINE_AXE = new AttackAnimation(0.08F, 0.2F, 0.5F, 0.65F, 1.0F, true, null, "111213", "biped/skill/axe_special", "biped");
	
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
	
	public static final AttackAnimation[] SHIELD_LIGHT_ATTACK = new AttackAnimation[]
			{
					new AttackAnimation(0.2F, 0.0F, 0.12F, 0.44F, 0.6F, false, "111213", "biped/combat/shield_strike", "biped")
						.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
			};
	
	public static final AttackAnimation SPEAR_ONEHAND_AUTO = new AttackAnimation(0.16F, 0.1F, 0.1F, 0.2F, 0.45F, false, null, "111213", "biped/combat/spear_onehand_auto", "biped");
	public static final StaticAnimation SPEAR_TWOHAND_AUTO_1 = new AttackAnimation(0.25F, 0.05F, 0.05F, 0.15F, 0.45F, false, Colliders.spearSwing, "111213", "biped/combat/spear_twohand_auto1", "biped");
	public static final StaticAnimation SPEAR_TWOHAND_AUTO_2 = new AttackAnimation(0.25F, 0.05F, 0.05F, 0.15F, 0.45F, false, Colliders.spearSwing, "111213", "biped/combat/spear_twohand_auto2", "biped");
	public static final StaticAnimation SPEAR_DASH = new AttackAnimation(0.16F, 0.05F, 0.2F, 0.3F, 0.7F, false, null, "111213", "biped/combat/spear_dash", "biped");
	public static final StaticAnimation SPEAR_MOUNT_ATTACK = new MountAttackAnimation(0.16F, 0.38F, 0.38F, 0.45F, 0.8F, null, "111213", "biped/combat/spear_mount_attack", "biped");
	
	public static final AttackAnimation[] STRAIGHT_SWORD_LIGHT_ATTACK = new AttackAnimation[]
			{
					new AttackAnimation(0.2F, 0.0F, 0.32F, 0.6F, 1.2F, false, "111213", "biped/combat/straight_sword_light_attack_1", "biped")
						.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STANDARD)
						.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT),
					new AttackAnimation(0.2F, 0.0F, 0.12F, 0.52F, 1.0F, false, "111213", "biped/combat/straight_sword_light_attack_2", "biped")
						.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STANDARD)
						.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
			};
	public static final AttackAnimation STRAIGHT_SWORD_HEAVY_ATTACK = new AttackAnimation(0.5F, 0.0F, 0.52F, 0.92F, 1.6F, false, "111213", "biped/combat/straight_sword_heavy_attack", "biped")
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.THRUST)
			.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM);
	public static final AttackAnimation STRAIGHT_SWORD_DASH_ATTACK = new AttackAnimation(0.06F, 0.0F, 0.48F, 0.8F, 1.2F, false, "111213", "biped/combat/straight_sword_dash_attack", "biped")
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STANDARD)
			.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM);
	public static final AttackAnimation SWORD_MOUNT_ATTACK = new MountAttackAnimation(0.16F, 0.1F, 0.2F, 0.25F, 0.7F, null, "111213", "biped/combat/sword_mount_attack", "biped");
	
	public static final StaticAnimation DANCING_EDGE = new AttackAnimation(0.25F, true, "biped/skill/dancing_edge", "biped",
														new Phase(0.2F, 0.2F, 0.26F, 0.3F, "111213", Colliders.sword), new Phase(0.5F, 0.5F, 0.56F, 0.6F, Hand.OFF_HAND, "111313", Colliders.sword),
														new Phase(0.75F, 0.75F, 0.8F, 1.15F, "111213", Colliders.sword));
	public static final StaticAnimation SPEAR_THRUST = new AttackAnimation(0.11F, false, "biped/skill/spear_thrust", "biped",
														new Phase(0.3F, 0.3F, 0.36F, 0.51F, "111213", Colliders.spearNarrow), new Phase(0.51F, 0.51F, 0.56F, 0.73F, "111213", Colliders.spearNarrow),
														new Phase(0.73F, 0.73F, 0.78F, 1.05F, "111213", Colliders.spearNarrow));
	public static final StaticAnimation SPEAR_SLASH = new AttackAnimation(0.1F, false, "biped/skill/spear_slash", "biped",
														new Phase(0.24F, 0.24F, 0.36F, 0.5F, "111213", Colliders.spearSwing), new Phase(0.5F, 0.75F, 0.9F, 1.25F, "111213", Colliders.spearSwing));
	public static final StaticAnimation FATAL_DRAW = new AttackAnimation(0.15F, 0.0F, 0.7F, 0.8F, 1.0F, false, Colliders.fatal_draw, "", "biped/skill/fatal_draw", "biped")
														.addProperty(AttackProperty.SWING_SOUND, null);
	public static final StaticAnimation FATAL_DRAW_DASH = new AttackAnimation(0.15F, 0.43F, 0.85F, 0.91F, 1.4F, false, Colliders.fatal_draw_dash, "", "biped/skill/fatal_draw_dash", "biped")
														.addProperty(AttackProperty.SWING_SOUND, null);
	public static final StaticAnimation LETHAL_SLICING = new AttackAnimation(0.15F, 0.0F, 0.0F, 0.1F, 0.38F, false, Colliders.fist_fast, "", "biped/skill/lethal_slicing_start", "biped");
	public static final StaticAnimation LETHAL_SLICING_ONCE = new AttackAnimation(0.016F, 0.0F, 0.0F, 0.1F, 0.6F, false, Colliders.spearSwing, "111213", "biped/skill/lethal_slicing_once", "biped");
	public static final StaticAnimation LETHAL_SLICING_TWICE = new AttackAnimation(0.016F, false, "biped/skill/lethal_slicing_twice", "biped",
														new Phase(0.0F, 0.0F, 0.1F, 0.15F, "111213", Colliders.spearSwing), new Phase(0.15F, 0.15F, 0.25F, 0.6F, "111213", Colliders.spearSwing));
	public static final StaticAnimation RELENTLESS_COMBO = new AttackAnimation(0.05F, false, "biped/skill/relentless_combo", "biped",
			new Phase(0.016F, 0.016F, 0.066F, 0.133F, Hand.OFF_HAND, "", Colliders.fist_fast), new Phase(0.133F, 0.133F, 0.183F, 0.25F, "", Colliders.fist_fast),
			new Phase(0.25F, 0.25F, 0.3F, 0.366F, Hand.OFF_HAND, "", Colliders.fist_fast), new Phase(0.366F, 0.366F, 0.416F, 0.483F, "", Colliders.fist_fast),
			new Phase(0.483F, 0.483F, 0.533F, 0.6F, Hand.OFF_HAND, "", Colliders.fist_fast), new Phase(0.6F, 0.6F, 0.65F, 0.716F, "", Colliders.fist_fast),
			new Phase(0.716F, 0.716F, 0.766F, 0.833F, Hand.OFF_HAND, "", Colliders.fist_fast), new Phase(0.833F, 0.833F, 0.883F, 1.1F, "", Colliders.fist_fast));
	
	// Hollow
	public static final StaticAnimation HOLLOW_DEFLECTED = new HitAnimation(0.2F, "hollow/deflected", "biped");
	public static final StaticAnimation HOLLOW_BREAKDOWN = new StaticAnimation(true, 0.2F, true, "hollow/breakdown", "biped", true);
	
	public static final AttackAnimation[] HOLLOW_LIGHT_ATTACKS = new AttackAnimation[]
						{
								new AttackAnimation(0.05F, 0.0F, 1.4F, 1.6F, 2.4F, false, "111213", "hollow/swing_1", "biped")
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT),
								new AttackAnimation(0.05F, 0.0F, 1.0F, 1.2F, 1.6F, false, "111213", "hollow/swing_2", "biped")
									.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT),
								new AttackAnimation(0.05F, 0.0F, 1.08F, 1.24F, 2.4F, false, "111213", "hollow/swing_3", "biped")
									.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
						};
	public static final AttackAnimation HOLLOW_BARRAGE = new AttackAnimation(0.05F, false, "hollow/fury_attack", "biped",
																new Phase(0.0F, 1.76F, 2.08F, 2.08F, "111213"),
																new Phase(2.08F, 2.12F, 2.28F, 2.28F, "111213"),
																new Phase(2.28F, 2.44F, 2.6F, 2.6F, "111213"),
																new Phase(2.6F, 2.76F, 2.92F, 2.92F, "111213"),
																new Phase(2.92F, 3.08F, 3.24F, 3.24F, "111213"),
																new Phase(3.24F, 3.4F, 3.56F, 4.4F, "111213"))
																.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
																.registerSound(ModSoundEvents.HOLLOW_PREPARE, 0.04F, true);
	public static final AttackAnimation HOLLOW_OVERHEAD_SWING = new AttackAnimation(0.05F, 0.0F, 0.64F, 0.88F, 1.6F, false, Colliders.brokenSword, "111213", "hollow/overhead_swing", "biped")
																.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM);
	public static final AttackAnimation HOLLOW_JUMP_ATTACK = new TargetTraceJumpAnimation(0.05F, 0.0F, 0.52F, 0.8F, 1.6F, false, Colliders.brokenSword, "111213", 0.0F, "hollow/jump_attack", "biped")
																.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM);
	
	// Asylum Demon
	public static final StaticAnimation ASYLUM_DEMON_IDLE = new StaticAnimation(true, 0.9F, true, "asylum_demon/idle", "asylum_demon", true);
	public static final StaticAnimation ASYLUM_DEMON_MOVE = new StaticAnimation(true, 0.5F, true, "asylum_demon/move", "asylum_demon", true)
			.registerSound(ModSoundEvents.ASYLUM_DEMON_FOOT, 0.4F, true)
			.registerSound(ModSoundEvents.ASYLUM_DEMON_FOOT, 1.2F, true);
	public static final StaticAnimation ASYLUM_DEMON_DEATH = new DeathAnimation(0.5F, "asylum_demon/death", "asylum_demon");
			
	public static final AttackAnimation[] ASYLUM_DEMON_LIGHT_ATTACK = new AttackAnimation[]
			{
					new AttackAnimation(0.5F, 0.0F, 0.52F, 1.0F, 2.0F, false, "11131", "asylum_demon/light_attack_1", "asylum_demon")
						.registerSound(ModSoundEvents.ASYLUM_DEMON_SWING, 0.52F, true)
						.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE),
					new AttackAnimation(0.5F, 0.0F, 0.6F, 0.92F, 2.0F, false, "11131", "asylum_demon/light_attack_2", "asylum_demon")
						.registerSound(ModSoundEvents.ASYLUM_DEMON_SWING, 0.6F, true)
						.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE),
					new AttackAnimation(0.5F, 0.0F, 0.6F, 0.84F, 1.2F, false, "11131", "asylum_demon/light_attack_3", "asylum_demon")
						.registerSound(ModSoundEvents.ASYLUM_DEMON_SMASH, 0.72F, true)
						.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
			};
	public static final AttackAnimation ASYLUM_DEMON_HAMMER_DRIVE = new AttackAnimation(0.5F, 0.0F, 0.64F, 1.04F, 2.8F, false, "11131", "asylum_demon/heavy_attack", "asylum_demon")
			.registerSound(ModSoundEvents.ASYLUM_DEMON_SMASH, 0.92F, true)
			.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE);
	public static final AttackAnimation ASYLUM_DEMON_JUMP_ATTACK = new TargetTraceJumpAnimation(0.5F, 0.0F, 0.64F, 1.08F, 2.0F, true, null, "11131", 4.0F, "asylum_demon/dash_attack", "asylum_demon")
			.registerSound(ModSoundEvents.ASYLUM_DEMON_WING, 0.04F, true)
			.registerSound(ModSoundEvents.ASYLUM_DEMON_LAND, 0.8F, true)
			.registerSound(ModSoundEvents.ASYLUM_DEMON_SMASH, 0.8F, true)
			.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE);
	public static final AttackAnimation ASYLUM_DEMON_GROUND_POUND = new AttackAnimation(0.5F, true, "asylum_demon/ground_pound", "asylum_demon",
			new Phase(0.0F, 2.32F, 2.6F, 2.6F, "1", Colliders.body),
			new Phase(2.6F, 2.64F, 2.96F, 4.0F, "1", Colliders.bodyShockwave))
			.registerSound(ModSoundEvents.ASYLUM_DEMON_WING, 0.4F, true)
			.registerSound(ModSoundEvents.ASYLUM_DEMON_WING, 1.0F, true)
			.registerSound(ModSoundEvents.ASYLUM_DEMON_WING, 1.76F, true)
			.registerSound(ModSoundEvents.ASYLUM_DEMON_LAND, 2.76F, true)
			.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE);
	
	public static StaticAnimation getById(int id)
	{
		return ANIMATIONS.get(id);
	}
	
	public static void registerAnimations(Dist dist)
	{
		ANIMATIONS.forEach((animation) -> animation.bind(dist));
	}
}