package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.client.animation.AnimationLayer.LayerPart;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.Property.ActionAnimationProperty;
import com.skullmangames.darksouls.common.animation.Property.AttackProperty;
import com.skullmangames.darksouls.common.animation.Property.StaticAnimationProperty;
import com.skullmangames.darksouls.common.animation.types.AdaptableAnimation;
import com.skullmangames.darksouls.common.animation.types.AdaptableAnimation.AnimConfig;
import com.skullmangames.darksouls.common.animation.types.AimingAnimation;
import com.skullmangames.darksouls.common.animation.types.ConsumeAnimation;
import com.skullmangames.darksouls.common.animation.types.DisarmAnimation;
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
import com.skullmangames.darksouls.common.animation.types.attack.CircleParticleSpawner;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource.DamageType;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource.StunType;
import com.skullmangames.darksouls.common.capability.item.IShield.Deflection;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class Animations
{
	public static final StaticAnimation DUMMY_ANIMATION = new StaticAnimation();

	public static final StaticAnimation BIPED_IDLE = new StaticAnimation(0.2F, true, "biped/living/idle", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation BIPED_WALK = new MovementAnimation(0.08F, true, "biped/living/walk", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation BIPED_RUN = new MovementAnimation(0.08F, true, "biped/living/run", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation BIPED_SNEAK = new MovementAnimation(0.08F, true, "biped/living/sneak", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation BIPED_SWIM = new MovementAnimation(0.08F, true, "biped/living/swim", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation BIPED_FLOAT = new StaticAnimation(0.08F, true, "biped/living/float", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation BIPED_KNEEL = new StaticAnimation(0.08F, true, "biped/living/kneel", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation BIPED_FALL = new StaticAnimation(0.08F, false, "biped/living/fall", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation BIPED_MOUNT = new StaticAnimation(0.08F, true, "biped/living/mount", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation BIPED_DEATH = new StaticAnimation(0.16F, false, "biped/living/death", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation BIPED_DIG = new StaticAnimation(0.2F, true, "biped/living/dig", (models) -> models.ENTITY_BIPED)
			.addProperty(StaticAnimationProperty.LAYER_PART, LayerPart.RIGHT);

	public static final StaticAnimation BIPED_EAT = new ConsumeAnimation(0.2F, true, "biped/living/eat_r", "biped/living/eat_l", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation BIPED_DRINK = new ConsumeAnimation(0.2F, true, "biped/living/drink_r", "biped/living/drink_l", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation BIPED_CONSUME_SOUL = new ConsumeAnimation(0.2F, true, "biped/living/consume_soul_r", "biped/living/consume_soul_l", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation BIPED_BLOCK = new AdaptableAnimation(0.2F, true, (models) -> models.ENTITY_BIPED,
			new AnimConfig(LivingMotion.BLOCKING, "biped/combat/block_mirror", "biped/combat/block", false),
			new AnimConfig(LivingMotion.WALKING, "biped/combat/block_walk_mirror", "biped/combat/block_walk", true),
			new AnimConfig(LivingMotion.RUNNING, "biped/combat/block_run_mirror", "biped/combat/block_run", true),
			new AnimConfig(LivingMotion.KNEELING, "biped/combat/block_mirror", "biped/combat/block", true),
			new AnimConfig(LivingMotion.SNEAKING, "biped/combat/block_mirror", "biped/combat/block", true));

	public static final StaticAnimation BIPED_IDLE_CROSSBOW = new StaticAnimation(0.2F, true, "biped/living/idle_crossbow", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation BIPED_WALK_CROSSBOW = new MovementAnimation(0.2F, true, "biped/living/walk_crossbow", (models) -> models.ENTITY_BIPED);

	public static final StaticAnimation BIPED_CROSSBOW_AIM = new AimingAnimation(0.16F, false, "biped/combat/crossbow_aim_mid", "biped/combat/crossbow_aim_up", "biped/combat/crossbow_aim_down", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation BIPED_CROSSBOW_SHOT = new ReboundAnimation(0.16F, false, "biped/combat/crossbow_shot_mid", "biped/combat/crossbow_shot_up", "biped/combat/crossbow_shot_down", (models) -> models.ENTITY_BIPED);

	public static final StaticAnimation BIPED_CROSSBOW_RELOAD = new StaticAnimation(0.16F, false, "biped/combat/crossbow_reload", (models) -> models.ENTITY_BIPED);

	public static final StaticAnimation BIPED_BOW_AIM = new AimingAnimation(0.16F, false, "biped/combat/bow_aim_mid", "biped/combat/bow_aim_up", "biped/combat/bow_aim_down", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation BIPED_BOW_REBOUND = new ReboundAnimation(0.04F, false, "biped/combat/bow_shot_mid", "biped/combat/bow_shot_up", "biped/combat/bow_shot_down", (models) -> models.ENTITY_BIPED);

	public static final StaticAnimation BIPED_SPEER_AIM = new AimingAnimation(0.16F, false, "biped/combat/javelin_aim_mid", "biped/combat/javelin_aim_up", "biped/combat/javelin_aim_down", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation BIPED_SPEER_REBOUND = new ReboundAnimation(0.08F, false, "biped/combat/javelin_throw_mid", "biped/combat/javelin_throw_up", "biped/combat/javelin_throw_down", (models) -> models.ENTITY_BIPED);

	public static final StaticAnimation BIPED_HIT_SHORT = new HitAnimation(0.05F, "biped/combat/hit_short", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation BIPED_HIT_LONG = new HitAnimation(0.08F, "biped/combat/hit_long", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation BIPED_HIT_ON_MOUNT = new HitAnimation(0.08F, "biped/combat/hit_on_mount", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation BIPED_HIT_DOWN_BACK = new InvincibleAnimation(0.08F, "biped/combat/hit_down_back", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation BIPED_HIT_DOWN_FRONT = new InvincibleAnimation(0.08F, "biped/combat/hit_down_front", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation BIPED_LAND_DAMAGE = new HitAnimation(0.08F, "biped/living/land_damage", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation BIPED_ROLL = new DodgingAnimation(0.2F, "biped/combat/roll", (models) -> models.ENTITY_BIPED)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.28F, Side.BOTH, (cap) -> cap.playSound(ModSoundEvents.GENERIC_ROLL.get())) })
			.addProperty(ActionAnimationProperty.MOVE_ON_LINK, true);
	public static final StaticAnimation BIPED_FAT_ROLL = new DodgingAnimation(0.2F, "biped/combat/fat_roll", (models) -> models.ENTITY_BIPED)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.28F, Side.BOTH, (cap) -> cap.playSound(ModSoundEvents.GENERIC_ROLL.get())) })
			.addProperty(ActionAnimationProperty.MOVE_ON_LINK, true);
	public static final DodgingAnimation BIPED_JUMP_BACK = new DodgingAnimation(0.08F, "biped/combat/jump_back", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation BIPED_DISARM_SHIELD = new DisarmAnimation(0.05F, "biped/combat/disarmed_left", (models) -> models.ENTITY_BIPED);
	
	// Big Weapon
	public static final MirrorAnimation BIPED_HOLDING_BIG_WEAPON = new MirrorAnimation(0.2F, true, true,
			"biped/living/holding_big_weapon_r", "biped/living/holding_big_weapon_l", (models) -> models.ENTITY_BIPED);

	// Ultra Greatsword
	public static final AttackAnimation[] ULTRA_GREATSWORD_LIGHT_ATTACK = new AttackAnimation[]
	{	
			new AttackAnimation(0.5F, 0.0F, 0.7F, 1.0F, 3.5F, "Tool_R", "biped/combat/ultra_greatsword_light_attack_1", (models) -> models.ENTITY_BIPED)
						.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.9F, Side.BOTH, (cap) -> cap.playSound(ModSoundEvents.ULTRA_GREATSWORD_SMASH.get())) })
						.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
						.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
						.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH_FRONT)
						.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F)),
			new AttackAnimation(0.5F, 0.0F, 0.45F, 1.05F, 3.5F, "Tool_R", "biped/combat/ultra_greatsword_light_attack_2", (models) -> models.ENTITY_BIPED)
						.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.9F, Side.BOTH, (cap) -> cap.playSound(ModSoundEvents.ULTRA_GREATSWORD_SMASH.get())) })
						.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
						.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
						.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH_FRONT)
						.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
	};
	
	public static final AttackAnimation[] ULTRA_GREATSWORD_HEAVY_ATTACK = new AttackAnimation[]
	{
			new AttackAnimation(0.5F, 0.0F, 1.35F, 1.6F, 3.45F, "Tool_R", "biped/combat/ultra_greatsword_heavy_attack_1", (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(1.5F, Side.BOTH, (cap) -> cap.playSound(ModSoundEvents.ULTRA_GREATSWORD_SMASH.get())) })
				.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
				.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH_FRONT)
				.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F)),
			new AttackAnimation(0.5F, 0.0F, 1.35F, 1.6F, 3.45F, "Tool_R", "biped/combat/ultra_greatsword_heavy_attack_2", (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(1.5F, Side.BOTH, (cap) -> cap.playSound(ModSoundEvents.ULTRA_GREATSWORD_SMASH.get())) })
				.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH_FRONT)
				.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
	};
	public static final AttackAnimation ULTRA_GREATSWORD_DASH_ATTACK = new AttackAnimation(0.1F, 0.0F, 1.35F, 1.6F, 3.45F, "Tool_R", "biped/combat/ultra_greatsword_heavy_attack_1", (models) -> models.ENTITY_BIPED)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(1.6F, Side.BOTH, (cap) -> cap.playSound(ModSoundEvents.ULTRA_GREATSWORD_SMASH.get())) })
			.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
			.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
			.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH_FRONT)
			.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F));

	// Spear
	public static final AttackAnimation SPEAR_DASH_ATTACK = new AttackAnimation(0.2F, 0.0F, 0.15F, 0.3F, 1.0F, "Tool_R", "biped/combat/spear_dash_attack", (models) -> models.ENTITY_BIPED)
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.THRUST)
			.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
			.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT);
	public static final AttackAnimation SPEAR_HEAVY_ATTACK = new AttackAnimation(0.35F, 0.0F, 0.65F, 0.8F, 1.75F, "Tool_R", "biped/combat/spear_heavy_attack", (models) -> models.ENTITY_BIPED)
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.THRUST)
			.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
			.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM);
	public static final AttackAnimation SPEAR_LIGHT_ATTACK = new AttackAnimation(0.35F, 0.0F, 0.65F, 0.8F, 1.5F, "Tool_R", "biped/combat/spear_light_attack", (models) -> models.ENTITY_BIPED)
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.THRUST)
			.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT);
	public static final AttackAnimation SPEAR_LIGHT_BLOCKING_ATTACK = new AttackAnimation(0.2F, 0.0F, 0.35F, 0.5F, 1.25F, "Tool_R", "biped/combat/spear_light_blocking_attack", (models) -> models.ENTITY_BIPED)
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.THRUST)
			.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
			.addProperty(AttackProperty.BLOCKING, true);

	// Dagger
	public static final AttackAnimation DAGGER_HEAVY_ATTACK = new AttackAnimation(0.2F, 0.0F, 0.68F, 0.96F, 1.6F, "Tool_R", "biped/combat/dagger_heavy_attack", (models) -> models.ENTITY_BIPED)
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.THRUST)
			.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
			.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT);
	public static final AttackAnimation[] DAGGER_LIGHT_ATTACK = new AttackAnimation[]
	{
			new AttackAnimation(0.2F, 0.0F, 0.16F, 0.4F, 1.0F, "Tool_R", "biped/combat/dagger_light_attack_1", (models) -> models.ENTITY_BIPED).addProperty(AttackProperty.DAMAGE_TYPE, DamageType.SLASH)
				.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT),
			new AttackAnimation(0.2F, 0.0F, 0.04F, 0.32F, 1.0F, "Tool_R", "biped/combat/dagger_light_attack_2", (models) -> models.ENTITY_BIPED).addProperty(AttackProperty.DAMAGE_TYPE, DamageType.SLASH)
				.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
	};

	// Great Hammer
	public static final AttackAnimation GREAT_HAMMER_HEAVY_ATTACK = new AttackAnimation(0.5F, 0.0F, 1.57F, 1.9F, 3.75F, "Tool_R", "biped/combat/great_hammer_heavy_attack", (models) -> models.ENTITY_BIPED)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(1.8F, Side.BOTH, (cap) -> cap.playSound(ModSoundEvents.GREAT_HAMMER_SMASH.get())) })
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE)
			.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
			.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
			.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH_FRONT)
			.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F));
	public static final AttackAnimation[] GREAT_HAMMER_LIGHT_ATTACK = new AttackAnimation[]
	{
			new AttackAnimation(0.5F, 0.0F, 1.35F, 1.6F, 3.45F, "Tool_R", "biped/combat/great_hammer_light_attack_1", (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(1.5F, Side.BOTH, (cap) -> cap.playSound(ModSoundEvents.GREAT_HAMMER_SMASH.get())) })
				.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH_FRONT)
				.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F)),
			new AttackAnimation(0.5F, 0.0F, 1.35F, 1.6F, 3.45F, "Tool_R", "biped/combat/great_hammer_light_attack_2", (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(1.5F, Side.BOTH, (cap) -> cap.playSound(ModSoundEvents.GREAT_HAMMER_SMASH.get())) })
				.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH_FRONT)
				.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
	};
	public static final AttackAnimation GREAT_HAMMER_DASH_ATTACK = new AttackAnimation(0.1F, 0.0F, 1.35F, 1.6F, 3.45F, "Tool_R", "biped/combat/great_hammer_light_attack_1", (models) -> models.ENTITY_BIPED)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(1.5F, Side.BOTH, (cap) -> cap.playSound(ModSoundEvents.GREAT_HAMMER_SMASH.get())) })
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE)
			.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
			.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
			.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH_FRONT)
			.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F));

	// Axe
	public static final AttackAnimation AXE_HEAVY_ATTACK = new AttackAnimation(0.3F, 0.0F, 0.55F, 0.7F, 1.5F, "Tool_R", "biped/combat/axe_heavy_attack", (models) -> models.ENTITY_BIPED)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.4F, Side.BOTH, (cap) -> cap.playSound(ModSoundEvents.AXE_SWING.get())) })
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
			.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
			.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY);
	public static final AttackAnimation[] AXE_LIGHT_ATTACK = new AttackAnimation[]
	{
			new AttackAnimation(0.3F, 0.0F, 0.2F, 0.35F, 1.5F, "Tool_R", "biped/combat/axe_light_attack_1", (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.16F, Side.BOTH, (cap) -> cap.playSound(ModSoundEvents.AXE_SWING.get())) })
				.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
				.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM),
			new AttackAnimation(0.2F, 0.0F, 0.15F, 0.4F, 1.25F, "Tool_R", "biped/combat/axe_light_attack_2", (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.12F, Side.BOTH, (cap) -> cap.playSound(ModSoundEvents.AXE_SWING.get())) })
				.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
				.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM) };
	public static final AttackAnimation AXE_DASH_ATTACK = new AttackAnimation(0.2F, 0.0F, 0.4F, 0.5F, 1.5F, "Tool_R", "biped/combat/axe_dash_attack", (models) -> models.ENTITY_BIPED)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.35F, Side.BOTH, (cap) -> cap.playSound(ModSoundEvents.AXE_SWING.get())) })
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
			.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
			.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY);

	// Hammer
	public static final AttackAnimation HAMMER_DASH_ATTACK = new AttackAnimation(0.5F, 0.0F, 0.32F, 0.6F, 1.4F, "Tool_R", "biped/combat/hammer_dash_attack", (models) -> models.ENTITY_BIPED)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.32F, Side.BOTH, (cap) -> cap.playSound(ModSoundEvents.AXE_SWING.get())) })
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE)
			.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
			.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY);
	public static final AttackAnimation HAMMER_HEAVY_ATTACK = new AttackAnimation(0.5F, 0.0F, 0.32F, 0.52F, 1.4F, "Tool_R", "biped/combat/hammer_heavy_attack", (models) -> models.ENTITY_BIPED)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.32F, Side.BOTH, (cap) -> cap.playSound(ModSoundEvents.AXE_SWING.get())) })
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE)
			.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
			.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY);
	public static final AttackAnimation[] HAMMER_LIGHT_ATTACK = new AttackAnimation[]
	{
			new AttackAnimation(0.5F, 0.0F, 0.28F, 0.52F, 1.2F, "Tool_R", "biped/combat/hammer_light_attack", (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.28F, Side.BOTH, (cap) -> cap.playSound(ModSoundEvents.AXE_SWING.get())) })
				.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
	};

	// Fist
	public static final AttackAnimation[] FIST_LIGHT_ATTACK = new AttackAnimation[]
	{
			new AttackAnimation(0.2F, 0.0F, 0.3F, 0.4F, 1.25F, "Tool_R", "biped/combat/fist_light_attack_1", (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE),
			new AttackAnimation(0.2F, 0.0F, 0.15F, 0.3F, 1.25F, "Tool_R", "biped/combat/fist_light_attack_2", (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE)
	};
	public static final AttackAnimation FIST_DASH_ATTACK = new AttackAnimation(0.3F, 0.0F, 0.15F, 0.3F, 1.0F, "Tool_R", "biped/combat/fist_dash_attack", (models) -> models.ENTITY_BIPED)
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE)
			.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
			.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
			.addProperty(ActionAnimationProperty.MOVE_ON_LINK, true);
	public static final AttackAnimation FIST_HEAVY_ATTACK = new AttackAnimation(0.5F, 0.0F, 0.35F, 0.5F, 1.25F, "Tool_R", "biped/combat/fist_heavy_attack", (models) -> models.ENTITY_BIPED)
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE)
			.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
			.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT);

	// Shield
	public static final AttackAnimation[] SHIELD_LIGHT_ATTACK = new AttackAnimation[]
	{
			new AttackAnimation(0.3F, 0.0F, 0.12F, 0.32F, 0.8F, "Tool_R", "biped/combat/shield_attack", (models) -> models.ENTITY_BIPED).addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
				.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE)
				.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
	};

	// Straight Sword
	public static final AttackAnimation[] STRAIGHT_SWORD_LIGHT_ATTACK = new AttackAnimation[]
	{ 		
			new AttackAnimation(0.2F, 0.0F, 0.35F, 0.45F, 1.25F, "Tool_R", "biped/combat/straight_sword_light_attack_1", (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
				.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT),
			new AttackAnimation(0.2F, 0.0F, 0.15F, 0.3F, 1.25F, "Tool_R", "biped/combat/straight_sword_light_attack_2", (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
				.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
	};
	public static final AttackAnimation STRAIGHT_SWORD_HEAVY_ATTACK = new AttackAnimation(0.5F, 0.0F, 0.65F, 0.8F, 1.5F, "Tool_R", "biped/combat/straight_sword_heavy_attack", (models) -> models.ENTITY_BIPED)
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.SLASH)
			.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
			.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM);
	public static final AttackAnimation STRAIGHT_SWORD_DASH_ATTACK = new AttackAnimation(0.3F, 0.0F, 0.05F, 0.25F, 1.0F, "Tool_R", "biped/combat/straight_sword_dash_attack", (models) -> models.ENTITY_BIPED)
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
			.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
			.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM);

	// Hollow
	public static final StaticAnimation HOLLOW_IDLE = new StaticAnimation(0.2F, true, "hollow/idle", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation HOLLOW_WALK = new MovementAnimation(0.2F, true, "hollow/move", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation HOLLOW_RUN = new MovementAnimation(0.2F, true, "hollow/run", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation HOLLOW_DEFLECTED = new HitAnimation(0.2F, "hollow/deflected", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation HOLLOW_BREAKDOWN = new StaticAnimation(0.2F, true, "hollow/breakdown", (models) -> models.ENTITY_BIPED);

	public static final AttackAnimation[] HOLLOW_LIGHT_ATTACKS = new AttackAnimation[]
	{
			new AttackAnimation(0.05F, 0.0F, 1.4F, 1.6F, 2.4F, Colliders.BROKEN_SWORD, "Tool_R", "hollow/swing_1", (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT),
			new AttackAnimation(0.05F, 0.0F, 1.0F, 1.2F, 1.6F, Colliders.BROKEN_SWORD, "Tool_R", "hollow/swing_2", (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT),
			new AttackAnimation(0.05F, 0.0F, 1.08F, 1.24F, 2.4F, Colliders.BROKEN_SWORD, "Tool_R", "hollow/swing_3", (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
	};
	public static final AttackAnimation HOLLOW_BARRAGE = new AttackAnimation(0.05F, "hollow/fury_attack",
			(models) -> models.ENTITY_BIPED, new Phase(0.0F, 1.76F, 2.08F, 2.08F, "Tool_R", Colliders.BROKEN_SWORD),
			new Phase(2.08F, 2.12F, 2.28F, 2.28F, "Tool_R", Colliders.BROKEN_SWORD),
			new Phase(2.28F, 2.44F, 2.6F, 2.6F, "Tool_R", Colliders.BROKEN_SWORD),
			new Phase(2.6F, 2.76F, 2.92F, 2.92F, "Tool_R", Colliders.BROKEN_SWORD),
			new Phase(2.92F, 3.08F, 3.24F, 3.24F, "Tool_R", Colliders.BROKEN_SWORD),
			new Phase(3.24F, 3.4F, 3.56F, 4.4F, "Tool_R", Colliders.BROKEN_SWORD))
					.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
					.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.04F, Side.BOTH, (cap) -> cap.playSound(ModSoundEvents.HOLLOW_PREPARE.get())) });
	public static final AttackAnimation HOLLOW_OVERHEAD_SWING = new AttackAnimation(0.05F, 0.0F, 0.4F, 0.6F, 1.2F, Colliders.BROKEN_SWORD, "Tool_R", "hollow/overhead_swing", (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
					.addProperty(AttackProperty.STAMINA_DMG_MUL, 2);
	public static final AttackAnimation HOLLOW_JUMP_ATTACK = new AttackAnimation(0.05F, 0.0F, 0.52F, 0.72F, 1.6F,
			Colliders.BROKEN_SWORD, "Tool_R", "hollow/jump_attack", (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
					.addProperty(AttackProperty.STAMINA_DMG_MUL, 2);

	// Hollow Lordran Warrior
	public static final StaticAnimation HOLLOW_LORDRAN_WARRIOR_WALK = new MovementAnimation(0.2F, true,
			"hollow_lordran_warrior/move", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation HOLLOW_LORDRAN_WARRIOR_RUN = new MovementAnimation(0.2F, true,
			"hollow_lordran_warrior/run", (models) -> models.ENTITY_BIPED);

	public static final AttackAnimation[] HOLLOW_LORDRAN_WARRIOR_TH_LA = new AttackAnimation[]
	{ new AttackAnimation(0.2F, 0.0F, 0.68F, 1.08F, 1.6F, "Tool_R", "hollow_lordran_warrior/sword_th_la_1",
			(models) -> models.ENTITY_BIPED).addProperty(AttackProperty.DAMAGE_TYPE, DamageType.SLASH)
					.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM),
			new AttackAnimation(0.2F, 0.0F, 0.68F, 1.08F, 1.6F, "Tool_R", "hollow_lordran_warrior/sword_th_la_2",
					(models) -> models.ENTITY_BIPED).addProperty(AttackProperty.DAMAGE_TYPE, DamageType.SLASH)
							.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM) };

	public static final AttackAnimation HOLLOW_LORDRAN_WARRIOR_DASH_ATTACK = new AttackAnimation(0.2F, 0.0F, 0.44F,
			0.88F, 1.8F, "Tool_R", "hollow_lordran_warrior/dash_attack", (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.SLASH)
					.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
					.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY);

	public static final AttackAnimation[] HOLLOW_LORDRAN_WARRIOR_AXE_LA = new AttackAnimation[]
	{ new AttackAnimation(0.2F, 0.0F, 0.6F, 1.0F, 2.4F, "Tool_R", "hollow_lordran_warrior/axe_la_1",
			(models) -> models.ENTITY_BIPED).addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
					.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM),
			new AttackAnimation(0.2F, 0.0F, 1.12F, 1.36F, 2.8F, "Tool_R", "hollow_lordran_warrior/axe_la_2",
					(models) -> models.ENTITY_BIPED).addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
							.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM) };

	public static final AttackAnimation[] HOLLOW_LORDRAN_WARRIOR_AXE_TH_LA = new AttackAnimation[]
	{ new AttackAnimation(0.2F, 0.0F, 0.56F, 1.0F, 2.8F, "Tool_R", "hollow_lordran_warrior/axe_th_la_1",
			(models) -> models.ENTITY_BIPED).addProperty(AttackProperty.DAMAGE_TYPE, DamageType.SLASH)
					.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
					.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY),
			new AttackAnimation(0.2F, 0.0F, 0.68F, 1.0F, 2.0F, "Tool_R", "hollow_lordran_warrior/axe_th_la_2",
					(models) -> models.ENTITY_BIPED).addProperty(AttackProperty.DAMAGE_TYPE, DamageType.SLASH)
							.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
							.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY) };

	// Hollow Lordran Soldier
	public static final StaticAnimation HOLLOW_LORDRAN_SOLDIER_WALK = new MovementAnimation(0.2F, true, "hollow_lordran_soldier/walking", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation HOLLOW_LORDRAN_SOLDIER_RUN = new MovementAnimation(0.2F, true, "hollow_lordran_soldier/run", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation HOLLOW_LORDRAN_SOLDIER_BLOCK = new StaticAnimation(0.2F, true, "hollow_lordran_soldier/block", (models) -> models.ENTITY_BIPED);

	public static final AttackAnimation[] HOLLOW_LORDRAN_SOLDIER_SWORD_LA = new AttackAnimation[]
	{ new AttackAnimation(0.2F, 0.0F, 0.44F, 0.76F, 1.6F, "Tool_R", "hollow_lordran_soldier/sword_la_1",
			(models) -> models.ENTITY_BIPED).addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
					.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT),
			new AttackAnimation(0.2F, 0.0F, 0.16F, 0.56F, 1.0F, "Tool_R", "hollow_lordran_soldier/sword_la_2",
					(models) -> models.ENTITY_BIPED).addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
							.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT),
			new AttackAnimation(0.2F, 0.0F, 0.44F, 0.6F, 1.6F, "Tool_R", "hollow_lordran_soldier/sword_la_3",
					(models) -> models.ENTITY_BIPED).addProperty(AttackProperty.DAMAGE_TYPE, DamageType.THRUST)
							.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM) };

	public static final AttackAnimation HOLLOW_LORDRAN_SOLDIER_SWORD_DA = new AttackAnimation(0.2F, 0.0F, 0.35F, 0.5F, 3.0F, "Tool_R", "hollow_lordran_soldier/sword_da", (models) -> models.ENTITY_BIPED)
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.THRUST)
			.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
			.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
			.addProperty(ActionAnimationProperty.MOVE_ON_LINK, true);

	public static final AttackAnimation HOLLOW_LORDRAN_SOLDIER_SWORD_HEAVY_THRUST = new AttackAnimation(0.2F, 0.0F,
			1.0F, 1.16F, 2.0F, "Tool_R", "hollow_lordran_soldier/sword_heavy_thrust",
			(models) -> models.ENTITY_BIPED).addProperty(AttackProperty.DAMAGE_TYPE, DamageType.THRUST)
					.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
					.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM);

	public static final AttackAnimation HOLLOW_LORDRAN_SOLDIER_SWORD_THRUST_COMBO = new AttackAnimation(0.2F,
			"hollow_lordran_soldier/sword_thrust_combo", (models) -> models.ENTITY_BIPED,
			new Phase(0.0F, 0.52F, 0.72F, 0.72F, "Tool_R", null), new Phase(0.72F, 1.2F, 1.4F, 2.0F, "Tool_R", null))
					.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.THRUST)
					.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT);

	public static final AttackAnimation[] HOLLOW_LORDRAN_SOLDIER_SPEAR_SWINGS = new AttackAnimation[]
	{ new AttackAnimation(0.2F, 0.0F, 0.48F, 0.76F, 1.6F, "Tool_R", "hollow_lordran_soldier/spear_swing_1",
			(models) -> models.ENTITY_BIPED).addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
					.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT),
			new AttackAnimation(0.2F, 0.0F, 0.16F, 0.56F, 1.0F, "Tool_R", "hollow_lordran_soldier/spear_swing_2",
					(models) -> models.ENTITY_BIPED).addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
							.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT),
			new AttackAnimation(0.2F, 0.0F, 0.6F, 0.72F, 1.6F, "Tool_R", "hollow_lordran_soldier/spear_swing_3",
					(models) -> models.ENTITY_BIPED).addProperty(AttackProperty.DAMAGE_TYPE, DamageType.THRUST)
							.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM),
			new AttackAnimation(0.2F, 0.0F, 0.44F, 0.6F, 1.6F, "Tool_R", "hollow_lordran_soldier/spear_swing_4",
					(models) -> models.ENTITY_BIPED).addProperty(AttackProperty.DAMAGE_TYPE, DamageType.THRUST)
							.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT) };

	public static final AttackAnimation[] HOLLOW_LORDRAN_SOLDIER_SPEAR_THRUSTS = new AttackAnimation[]
	{ new AttackAnimation(0.2F, 0.0F, 0.64F, 0.8F, 1.6F, "Tool_R", "hollow_lordran_soldier/spear_thrust_1",
			(models) -> models.ENTITY_BIPED).addProperty(AttackProperty.DAMAGE_TYPE, DamageType.THRUST)
					.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT),
			new AttackAnimation(0.2F, 0.0F, 0.72F, 0.88F, 1.6F, "Tool_R",
					"hollow_lordran_soldier/spear_thrust_2", (models) -> models.ENTITY_BIPED)
							.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.THRUST)
							.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT),
			new AttackAnimation(0.2F, 0.0F, 0.88F, 1.04F, 1.6F, "Tool_R",
					"hollow_lordran_soldier/spear_thrust_3", (models) -> models.ENTITY_BIPED)
							.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.THRUST)
							.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM) };

	public static final AttackAnimation HOLLOW_LORDRAN_SOLDIER_SHIELD_BASH = new AttackAnimation(0.2F, 0.0F, 0.6F, 0.8F,
			1.6F, "Tool_L", "hollow_lordran_soldier/shield_bash", (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
					.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
					.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE);

	// Asylum Demon
	public static final StaticAnimation STRAY_DEMON_IDLE = new StaticAnimation(1.0F, true, "asylum_demon/idle",
			(models) -> models.ENTITY_ASYLUM_DEMON);
	public static final StaticAnimation STRAY_DEMON_MOVE = new StaticAnimation(0.5F, true, "asylum_demon/move",
			(models) -> models.ENTITY_ASYLUM_DEMON).addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.4F, Side.BOTH, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_FOOT.get())) })
			.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(1.2F, Side.BOTH, (cap) -> cap.playSound(ModSoundEvents.HOLLOW_PREPARE.get())) });
	public static final StaticAnimation STRAY_DEMON_DEATH = new StaticAnimation(0.5F, false, "asylum_demon/death",
			(models) -> models.ENTITY_ASYLUM_DEMON);

	public static final AttackAnimation[] STRAY_DEMON_LIGHT_ATTACK = new AttackAnimation[]
	{ new AttackAnimation(1.0F, 0.0F, 0.52F, 1.0F, 2.0F, "Tool_R", "asylum_demon/light_attack_1", (models) -> models.ENTITY_ASYLUM_DEMON)
		.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.52F, Side.BOTH, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_WING.get())) })
			.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
			.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH_FRONT)
			.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F)),
			new AttackAnimation(1.0F, 0.0F, 0.6F, 0.92F, 2.0F, "Tool_R", "asylum_demon/light_attack_2",
					(models) -> models.ENTITY_ASYLUM_DEMON).addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.6F, Side.BOTH, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_SWING.get())) })
							.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
							.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH_FRONT)
							.addProperty(AttackProperty.PARTICLE,
									new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F)),
			new AttackAnimation(1.0F, 0.0F, 0.6F, 0.84F, 1.2F, "Tool_R", "asylum_demon/light_attack_3",
					(models) -> models.ENTITY_ASYLUM_DEMON).addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.72F, Side.BOTH, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_SMASH.get())) })
							.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
							.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH_FRONT)
							.addProperty(AttackProperty.PARTICLE,
									new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F)) };
	public static final AttackAnimation STRAY_DEMON_HAMMER_DRIVE = new AttackAnimation(1.0F, 0.0F, 0.64F, 1.04F, 2.8F, "Tool_R", "asylum_demon/heavy_attack", (models) -> models.ENTITY_ASYLUM_DEMON)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.92F, Side.BOTH, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_SMASH.get())) })
					.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
					.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH_FRONT)
					.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F));
	public static final AttackAnimation STRAY_DEMON_JUMP_ATTACK = new AttackAnimation(1.0F, 0.0F, 0.6F, 1.2F, 2.0F,
			"Tool_R", "asylum_demon/dash_attack", (models) -> models.ENTITY_ASYLUM_DEMON)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.04F, Side.BOTH, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_WING.get())) })
			.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.8F, Side.BOTH, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_LAND.get())) })
			.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.8F, Side.BOTH, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_SMASH.get())) })
					.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
					.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH_FRONT)
					.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F));
	public static final AttackAnimation STRAY_DEMON_GROUND_POUND = new AttackAnimation(1.0F, 0.0F, 2.48F, 2.76F, 4.0F, Colliders.STRAY_DEMON_BODY, "Root", "asylum_demon/ground_pound", (models) -> models.ENTITY_ASYLUM_DEMON)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.4F, Side.BOTH, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_WING.get())) })
			.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(1.0F, Side.BOTH, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_WING.get())) })
			.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(1.76F, Side.BOTH, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_WING.get())) })
			.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(2.76F, Side.BOTH, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_LAND.get())) })
					.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
					.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH_FRONT)
					.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.25F));

	@OnlyIn(Dist.CLIENT)
	public static void buildClient() {}
}