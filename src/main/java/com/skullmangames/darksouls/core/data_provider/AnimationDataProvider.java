package com.skullmangames.darksouls.core.data_provider;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.animation.AnimationLayer.LayerPart;
import com.skullmangames.darksouls.common.animation.AnimBuilder;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.SmashEvents;
import com.skullmangames.darksouls.common.animation.Property.ActionAnimationProperty;
import com.skullmangames.darksouls.common.animation.Property.AimingAnimationProperty;
import com.skullmangames.darksouls.common.animation.Property.AttackProperty;
import com.skullmangames.darksouls.common.animation.Property.DeathProperty;
import com.skullmangames.darksouls.common.animation.Property.StaticAnimationProperty;
import com.skullmangames.darksouls.common.animation.events.Anchor;
import com.skullmangames.darksouls.common.animation.events.AnimEvent;
import com.skullmangames.darksouls.common.animation.events.ChangeItemEvent;
import com.skullmangames.darksouls.common.animation.events.EntityboundParticleEvent;
import com.skullmangames.darksouls.common.animation.events.HealInRadiusEvent;
import com.skullmangames.darksouls.common.animation.events.HealSelfEvent;
import com.skullmangames.darksouls.common.animation.events.ImpactParticleEvent;
import com.skullmangames.darksouls.common.animation.events.PlaySoundEvent;
import com.skullmangames.darksouls.common.animation.events.SetLightSourceEvent;
import com.skullmangames.darksouls.common.animation.events.ShakeCamEvent;
import com.skullmangames.darksouls.common.animation.events.ShakeCamGlobalEvent;
import com.skullmangames.darksouls.common.animation.events.ShockWaveEvent;
import com.skullmangames.darksouls.common.animation.events.ShootMagicProjectileEvent;
import com.skullmangames.darksouls.common.animation.events.ShootThrowableProjectileEvent;
import com.skullmangames.darksouls.common.animation.events.SimpleParticleEvent;
import com.skullmangames.darksouls.common.animation.events.TeleportEvent;
import com.skullmangames.darksouls.common.animation.events.TeleportParticleEvent;
import com.skullmangames.darksouls.common.animation.events.TeleportToSpawnEvent;
import com.skullmangames.darksouls.common.animation.events.AnimEvent.Side;
import com.skullmangames.darksouls.common.animation.types.ActionAnimation;
import com.skullmangames.darksouls.common.animation.types.AdaptableAnimation;
import com.skullmangames.darksouls.common.animation.types.AimingAnimation;
import com.skullmangames.darksouls.common.animation.types.BlockedAnimation;
import com.skullmangames.darksouls.common.animation.types.DeathAnimation;
import com.skullmangames.darksouls.common.animation.types.DodgingAnimation;
import com.skullmangames.darksouls.common.animation.types.InvincibleAnimation;
import com.skullmangames.darksouls.common.animation.types.MirrorAnimation;
import com.skullmangames.darksouls.common.animation.types.MovementAnimation;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.BackstabCheckAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.CriticalHitAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.ParryAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.PunishCheckAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation.PhaseBuilder;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap.AttackType;
import com.skullmangames.darksouls.common.capability.item.Shield.Deflection;
import com.skullmangames.darksouls.common.entity.projectile.LightningSpear;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ModParticles;
import com.skullmangames.darksouls.core.init.ModSoundEvents;
import com.skullmangames.darksouls.core.init.data.Colliders;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.MovementDamageType;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.StunType;
import com.skullmangames.darksouls.core.util.data.pack_resources.DSDefaultPackResources;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;

public class AnimationDataProvider implements DataProvider
{
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
	private final DataGenerator generator;

	public AnimationDataProvider(DataGenerator generator)
	{
		this.generator = generator;
	}

	@Override
	public void run(HashCache cache) throws IOException
	{
		Path path = this.generator.getOutputFolder();
		List<AnimBuilder> configs = defaultConfigs();

		for (AnimBuilder builder : configs)
		{
			Path path1 = createPath(path, builder.getId());
			try
			{
				DataProvider.save(GSON, cache, builder.toJson(), path1);
			} catch (IOException ioexception)
			{
				LOGGER.error("Couldn't save animation data {}", path1, ioexception);
			}
		}
	}

	private static List<AnimBuilder> defaultConfigs()
	{
		return ImmutableList.of(
				new StaticAnimation.Builder(Animations.BIPED_IDLE.getId(), 0.1F, true,
						DarkSouls.rl("biped/living/idle"), (models) -> models.ENTITY_BIPED),
				new MovementAnimation.Builder(Animations.BIPED_WALK.getId(), 0.08F, true,
						DarkSouls.rl("biped/living/walk"), (models) -> models.ENTITY_BIPED),
				new MovementAnimation.Builder(Animations.BIPED_RUN.getId(), 0.08F, true,
						DarkSouls.rl("biped/living/run"), (models) -> models.ENTITY_BIPED),
				new MovementAnimation.Builder(Animations.BIPED_SNEAK.getId(), 0.08F, true,
						DarkSouls.rl("biped/living/sneak"), (models) -> models.ENTITY_BIPED),

				new StaticAnimation.Builder(Animations.BIPED_IDLE_TH.getId(), 0.1F, true,
						DarkSouls.rl("biped/living/idle_th"), (models) -> models.ENTITY_BIPED),
				new StaticAnimation.Builder(Animations.BIPED_WALK_TH.getId(), 0.08F, true,
						DarkSouls.rl("biped/living/walk_th"), (models) -> models.ENTITY_BIPED),
				new StaticAnimation.Builder(Animations.BIPED_RUN_TH.getId(), 0.08F, true,
						DarkSouls.rl("biped/living/run_th"), (models) -> models.ENTITY_BIPED),

				new StaticAnimation.Builder(Animations.BIPED_IDLE_TH_BIG_WEAPON.getId(), 0.1F, true,
						DarkSouls.rl("biped/living/idle_th_big_weapon"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.LAYER_PART, LayerPart.UP),

				new StaticAnimation.Builder(Animations.BIPED_IDLE_TH_SPEAR.getId(), 0.1F, true,
						DarkSouls.rl("biped/living/idle_th_spear"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.LAYER_PART, LayerPart.UP),

				new StaticAnimation.Builder(Animations.BIPED_IDLE_TH_SHIELD.getId(), 0.1F, true,
						DarkSouls.rl("biped/living/idle_th_shield"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.LAYER_PART, LayerPart.UP),

				new StaticAnimation.Builder(Animations.BIPED_CHANGE_ITEM_RIGHT.getId(), 0.1F, false,
						DarkSouls.rl("biped/living/change_item_right"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new ChangeItemEvent(0.2F) }),

				new StaticAnimation.Builder(Animations.BIPED_CHANGE_ITEM_LEFT.getId(), 0.1F, false,
						DarkSouls.rl("biped/living/change_item_left"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new ChangeItemEvent(0.2F) }),

				new MovementAnimation.Builder(Animations.BIPED_SWIM.getId(), 0.08F, true,
						DarkSouls.rl("biped/living/swim"), (models) -> models.ENTITY_BIPED),
				new StaticAnimation.Builder(new ResourceLocation(DarkSouls.MOD_ID, "biped_float"), 0.08F, true,
						DarkSouls.rl("biped/living/float"), (models) -> models.ENTITY_BIPED),
				new StaticAnimation.Builder(new ResourceLocation(DarkSouls.MOD_ID, "biped_kneel"), 0.08F, true,
						DarkSouls.rl("biped/living/kneel"), (models) -> models.ENTITY_BIPED),
				new StaticAnimation.Builder(new ResourceLocation(DarkSouls.MOD_ID, "biped_fall"), 0.08F, false,
						DarkSouls.rl("biped/living/fall"), (models) -> models.ENTITY_BIPED),

				new DeathAnimation.Builder(Animations.BIPED_DEATH.getId(), 0.05F, DarkSouls.rl("biped/death/death"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(1.52F, Side.SERVER, ModSoundEvents.GENERIC_LAND) })
								.addProperty(DeathProperty.DISAPPEAR_AT, 3.6F),

				new DeathAnimation.Builder(Animations.BIPED_DEATH_SMASH.getId(), 0.05F,
						DarkSouls.rl("biped/death/smash"), (models) -> models.ENTITY_BIPED)
								.addProperty(DeathProperty.DISAPPEAR_AT, 1.52F),
				new DeathAnimation.Builder(Animations.BIPED_DEATH_FLY_FRONT.getId(), 0.05F,
						DarkSouls.rl("biped/death/fly_front"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.4F, Side.SERVER, ModSoundEvents.GENERIC_LAND),
										new PlaySoundEvent(0.8F, Side.SERVER, ModSoundEvents.GENERIC_LAND) })
								.addProperty(DeathProperty.DISAPPEAR_AT, 4.4F),
				new DeathAnimation.Builder(Animations.BIPED_DEATH_FLY_BACK.getId(), 0.05F,
						DarkSouls.rl("biped/death/fly_back"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.44F, Side.SERVER, ModSoundEvents.GENERIC_LAND),
										new PlaySoundEvent(0.8F, Side.SERVER, ModSoundEvents.GENERIC_LAND) })
								.addProperty(DeathProperty.DISAPPEAR_AT, 4.4F),
				new DeathAnimation.Builder(Animations.BIPED_DEATH_FLY_LEFT.getId(), 0.05F,
						DarkSouls.rl("biped/death/fly_left"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.48F, Side.SERVER, ModSoundEvents.GENERIC_LAND),
										new PlaySoundEvent(0.92F, Side.SERVER, ModSoundEvents.GENERIC_LAND) })
								.addProperty(DeathProperty.DISAPPEAR_AT, 4.4F),
				new DeathAnimation.Builder(Animations.BIPED_DEATH_FLY_RIGHT.getId(), 0.05F,
						DarkSouls.rl("biped/death/fly_right"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.48F, Side.SERVER, ModSoundEvents.GENERIC_LAND),
										new PlaySoundEvent(0.92F, Side.SERVER, ModSoundEvents.GENERIC_LAND) })
								.addProperty(DeathProperty.DISAPPEAR_AT, 4.4F),
				new DeathAnimation.Builder(Animations.BIPED_DEATH_BACKSTAB.getId(), 0.05F,
						DarkSouls.rl("biped/death/backstab"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new ImpactParticleEvent(0.44F, 0, 0, 0),
										new PlaySoundEvent(1.4F, Side.SERVER, ModSoundEvents.GENERIC_LAND) })
								.addProperty(DeathProperty.DISAPPEAR_AT, 6.0F),
				new DeathAnimation.Builder(Animations.BIPED_DEATH_PUNISH.getId(), 0.05F,
						DarkSouls.rl("biped/death/punish"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new ImpactParticleEvent(0.44F, 0, 0, 0),
										new PlaySoundEvent(1.4F, Side.SERVER, ModSoundEvents.GENERIC_LAND) })
								.addProperty(DeathProperty.DISAPPEAR_AT, 4.4F),

				new StaticAnimation.Builder(Animations.BIPED_DIG.getId(), 0.2F, true, DarkSouls.rl("biped/living/dig"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.LAYER_PART,
								LayerPart.RIGHT),
				new ActionAnimation.Builder(Animations.BIPED_TOUCH_BONFIRE.getId(), 0.5F,
						DarkSouls.rl("biped/living/touching_bonfire"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new TeleportParticleEvent(0.5F), new TeleportParticleEvent(1.0F),
										new TeleportParticleEvent(1.5F), new TeleportParticleEvent(2.0F),
										new TeleportParticleEvent(2.5F), new TeleportParticleEvent(3.0F),
										new TeleportParticleEvent(3.5F), new TeleportParticleEvent(4.0F),
										new PlaySoundEvent(2.5F, Side.SERVER, ModSoundEvents.BONFIRE_TELEPORT),
										new TeleportEvent(3.2F) }),

				new MirrorAnimation.Builder(Animations.BIPED_EAT.getId(), 0.2F, true,
						DarkSouls.rl("biped/living/eat_r"), DarkSouls.rl("biped/living/eat_l"),
						(models) -> models.ENTITY_BIPED),
				new MirrorAnimation.Builder(Animations.BIPED_DRINK.getId(), 0.2F, true,
						DarkSouls.rl("biped/living/drink_r"), DarkSouls.rl("biped/living/drink_l"),
						(models) -> models.ENTITY_BIPED),
				new MirrorAnimation.Builder(Animations.BIPED_CONSUME_SOUL.getId(), 0.2F, true,
						DarkSouls.rl("biped/living/consume_soul_r"), DarkSouls.rl("biped/living/consume_soul_l"),
						(models) -> models.ENTITY_BIPED),

				new ActionAnimation.Builder(Animations.BIPED_THROW.getId(), 0.2F, DarkSouls.rl("biped/combat/throw"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new ShootThrowableProjectileEvent(0.6F) }),

				// Blocks
				new AdaptableAnimation.Builder(Animations.BIPED_BLOCK_HORIZONTAL.getId(), 0.1F, true,
						(models) -> models.ENTITY_BIPED)
								.addEntry(LivingMotion.BLOCKING, DarkSouls.rl("biped/combat/block_mirror"),
										DarkSouls.rl("biped/combat/block"), false)
								.addEntry(LivingMotion.WALKING, DarkSouls.rl("biped/combat/block_walk_mirror"),
										DarkSouls.rl("biped/combat/block_walk"), true)
								.addEntry(LivingMotion.RUNNING, DarkSouls.rl("biped/combat/block_run_mirror"),
										DarkSouls.rl("biped/combat/block_run"), true)
								.addEntry(LivingMotion.KNEELING, DarkSouls.rl("biped/combat/block_mirror"),
										DarkSouls.rl("biped/combat/block"), true)
								.addEntry(LivingMotion.SNEAKING, DarkSouls.rl("biped/combat/block_mirror"),
										DarkSouls.rl("biped/combat/block"), true),

				new AdaptableAnimation.Builder(Animations.BIPED_BLOCK_VERTICAL.getId(), 0.1F, true,
						(models) -> models.ENTITY_BIPED)
								.addEntry(LivingMotion.BLOCKING, DarkSouls.rl("biped/combat/block_vertical_mirror"),
										DarkSouls.rl("biped/combat/block_vertical"), false)
								.addEntry(LivingMotion.WALKING, DarkSouls.rl("biped/combat/block_vertical_walk_mirror"),
										DarkSouls.rl("biped/combat/block_vertical_walk"), true)
								.addEntry(LivingMotion.RUNNING, DarkSouls.rl("biped/combat/block_vertical_run_mirror"),
										DarkSouls.rl("biped/combat/block_vertical_run"), true)
								.addEntry(LivingMotion.KNEELING, DarkSouls.rl("biped/combat/block_vertical_mirror"),
										DarkSouls.rl("biped/combat/block_vertical"), true)
								.addEntry(LivingMotion.SNEAKING, DarkSouls.rl("biped/combat/block_vertical_mirror"),
										DarkSouls.rl("biped/combat/block_vertical"), true),

				new AdaptableAnimation.Builder(Animations.BIPED_BLOCK_GREATSHIELD.getId(), 0.1F, true,
						(models) -> models.ENTITY_BIPED)
								.addEntry(LivingMotion.BLOCKING, DarkSouls.rl("biped/combat/block_greatshield_right"),
										DarkSouls.rl("biped/combat/block_greatshield_left"), false)
								.addEntry(LivingMotion.WALKING,
										DarkSouls.rl("biped/combat/block_greatshield_walk_right"),
										DarkSouls.rl("biped/combat/block_greatshield_walk_left"), true)
								.addEntry(LivingMotion.RUNNING,
										DarkSouls.rl("biped/combat/block_greatshield_run_right"),
										DarkSouls.rl("biped/combat/block_greatshield_run_left"), true)
								.addEntry(LivingMotion.KNEELING, DarkSouls.rl("biped/combat/block_greatshield_right"),
										DarkSouls.rl("biped/combat/block_greatshield_left"), true)
								.addEntry(LivingMotion.SNEAKING, DarkSouls.rl("biped/combat/block_greatshield_right"),
										DarkSouls.rl("biped/combat/block_greatshield_left"), true),

				new AdaptableAnimation.Builder(Animations.BIPED_BLOCK_TH_SWORD.getId(), 0.1F, true,
						(models) -> models.ENTITY_BIPED)
								.addEntry(LivingMotion.BLOCKING, DarkSouls.rl("biped/combat/block_th_sword"), false)
								.addEntry(LivingMotion.WALKING, DarkSouls.rl("biped/combat/block_th_sword"), true)
								.addEntry(LivingMotion.RUNNING, DarkSouls.rl("biped/combat/block_th_sword"), true)
								.addEntry(LivingMotion.KNEELING, DarkSouls.rl("biped/combat/block_th_sword"), true)
								.addEntry(LivingMotion.SNEAKING, DarkSouls.rl("biped/combat/block_th_sword"), true),

				new AdaptableAnimation.Builder(Animations.BIPED_BLOCK_TH_VERTICAL.getId(), 0.1F, true,
						(models) -> models.ENTITY_BIPED)
								.addEntry(LivingMotion.BLOCKING, DarkSouls.rl("biped/combat/block_th_vertical"), false)
								.addEntry(LivingMotion.WALKING, DarkSouls.rl("biped/combat/block_th_vertical"), true)
								.addEntry(LivingMotion.RUNNING, DarkSouls.rl("biped/combat/block_th_vertical"), true)
								.addEntry(LivingMotion.KNEELING, DarkSouls.rl("biped/combat/block_th_vertical"), true)
								.addEntry(LivingMotion.SNEAKING, DarkSouls.rl("biped/combat/block_th_vertical"), true),

				new AdaptableAnimation.Builder(Animations.BIPED_BLOCK_TH_HORIZONTAL.getId(), 0.1F, true,
						(models) -> models.ENTITY_BIPED)
								.addEntry(LivingMotion.BLOCKING, DarkSouls.rl("biped/combat/block_th_horizontal"),
										false)
								.addEntry(LivingMotion.WALKING, DarkSouls.rl("biped/combat/block_th_horizontal"), true)
								.addEntry(LivingMotion.RUNNING, DarkSouls.rl("biped/combat/block_th_horizontal"), true)
								.addEntry(LivingMotion.KNEELING, DarkSouls.rl("biped/combat/block_th_horizontal"), true)
								.addEntry(LivingMotion.SNEAKING, DarkSouls.rl("biped/combat/block_th_horizontal"),
										true),

				new AdaptableAnimation.Builder(Animations.BIPED_BLOCK_TH_GREATSHIELD.getId(), 0.1F, true,
						(models) -> models.ENTITY_BIPED)
								.addEntry(LivingMotion.BLOCKING, DarkSouls.rl("biped/combat/block_th_greatshield"),
										false)
								.addEntry(LivingMotion.WALKING, DarkSouls.rl("biped/combat/block_th_greatshield"), true)
								.addEntry(LivingMotion.RUNNING, DarkSouls.rl("biped/combat/block_th_greatshield"), true)
								.addEntry(LivingMotion.KNEELING, DarkSouls.rl("biped/combat/block_th_greatshield"),
										true)
								.addEntry(LivingMotion.SNEAKING, DarkSouls.rl("biped/combat/block_th_greatshield"),
										true),

				new BlockedAnimation.Builder(Animations.BIPED_HIT_BLOCKED_LEFT.getId(), 0.05F,
						DarkSouls.rl("biped/hit/blocked_left"), (models) -> models.ENTITY_BIPED)
								.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true),
				new BlockedAnimation.Builder(Animations.BIPED_HIT_BLOCKED_RIGHT.getId(), 0.05F,
						DarkSouls.rl("biped/hit/blocked_right"), (models) -> models.ENTITY_BIPED)
								.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true),
				new BlockedAnimation.Builder(Animations.BIPED_HIT_BLOCKED_VERTICAL_LEFT.getId(), 0.05F,
						DarkSouls.rl("biped/hit/blocked_vertical_left"), (models) -> models.ENTITY_BIPED)
								.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true),
				new BlockedAnimation.Builder(Animations.BIPED_HIT_BLOCKED_VERTICAL_RIGHT.getId(), 0.05F,
						DarkSouls.rl("biped/hit/blocked_vertical_right"), (models) -> models.ENTITY_BIPED)
								.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true),
				new BlockedAnimation.Builder(Animations.BIPED_HIT_BLOCKED_TH_SWORD.getId(), 0.05F,
						DarkSouls.rl("biped/hit/blocked_th_sword"), (models) -> models.ENTITY_BIPED),

				new InvincibleAnimation.Builder(Animations.BIPED_HIT_BLOCKED_FLY_LEFT.getId(), 0.05F,
						DarkSouls.rl("biped/hit/blocked_fly_left"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.48F, Side.SERVER, ModSoundEvents.GENERIC_ROLL) }),
				new InvincibleAnimation.Builder(Animations.BIPED_HIT_BLOCKED_FLY_RIGHT.getId(), 0.05F,
						DarkSouls.rl("biped/hit/blocked_fly_right"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.48F, Side.SERVER, ModSoundEvents.GENERIC_ROLL) }),
				new InvincibleAnimation.Builder(Animations.BIPED_HIT_BLOCKED_VERTICAL_FLY_LEFT.getId(), 0.05F,
						DarkSouls.rl("biped/hit/blocked_vertical_fly_left"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.48F, Side.SERVER, ModSoundEvents.GENERIC_ROLL) }),
				new InvincibleAnimation.Builder(Animations.BIPED_HIT_BLOCKED_VERTICAL_FLY_RIGHT.getId(), 0.05F,
						DarkSouls.rl("biped/hit/blocked_vertical_fly_right"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.48F, Side.SERVER, ModSoundEvents.GENERIC_ROLL) }),
				new InvincibleAnimation.Builder(Animations.BIPED_HIT_BLOCKED_TH_SWORD_FLY.getId(), 0.05F,
						DarkSouls.rl("biped/hit/blocked_th_sword_fly"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.48F, Side.SERVER, ModSoundEvents.GENERIC_ROLL) }),

				new ActionAnimation.Builder(Animations.BIPED_DISARMED_LEFT.getId(), 0.05F,
						DarkSouls.rl("biped/combat/disarmed_left"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(AnimEvent.ON_BEGIN, Side.SERVER,
										ModSoundEvents.PLAYER_SHIELD_DISARMED) })
								.addProperty(ActionAnimationProperty.PUNISHABLE, true),
				new ActionAnimation.Builder(Animations.BIPED_DISARMED_RIGHT.getId(), 0.05F,
						DarkSouls.rl("biped/combat/disarmed_right"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(AnimEvent.ON_BEGIN, Side.SERVER,
										ModSoundEvents.PLAYER_SHIELD_DISARMED) })
								.addProperty(ActionAnimationProperty.PUNISHABLE, true),

				new StaticAnimation.Builder(Animations.BIPED_HORSEBACK_IDLE.getId(), 0.2F, true,
						DarkSouls.rl("biped/horseback/horseback_idle"), (models) -> models.ENTITY_BIPED),

				new StaticAnimation.Builder(Animations.BIPED_IDLE_CROSSBOW.getId(), 0.2F, true,
						DarkSouls.rl("biped/living/idle_crossbow"), (models) -> models.ENTITY_BIPED),
				new MovementAnimation.Builder(Animations.BIPED_WALK_CROSSBOW.getId(), 0.2F, true,
						DarkSouls.rl("biped/living/walk_crossbow"), (models) -> models.ENTITY_BIPED),

				new AimingAnimation.Builder(Animations.BIPED_CROSSBOW_AIM.getId(), 0.16F, true,
						DarkSouls.rl("biped/combat/crossbow_aim_mid"), DarkSouls.rl("biped/combat/crossbow_aim_up"),
						DarkSouls.rl("biped/combat/crossbow_aim_down"), (models) -> models.ENTITY_BIPED),
				new AimingAnimation.Builder(Animations.BIPED_CROSSBOW_SHOT.getId(), 0.16F, false,
						DarkSouls.rl("biped/combat/crossbow_shot_mid"), DarkSouls.rl("biped/combat/crossbow_shot_up"),
						DarkSouls.rl("biped/combat/crossbow_shot_down"), (models) -> models.ENTITY_BIPED)
								.addProperty(AimingAnimationProperty.IS_REBOUND, true),

				new StaticAnimation.Builder(Animations.BIPED_CROSSBOW_RELOAD.getId(), 0.16F, false,
						DarkSouls.rl("biped/combat/crossbow_reload"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.LAYER_PART, LayerPart.UP),

				new AimingAnimation.Builder(Animations.BIPED_BOW_AIM.getId(), 0.16F, true,
						DarkSouls.rl("biped/combat/bow_aim_mid"), DarkSouls.rl("biped/combat/bow_aim_up"),
						DarkSouls.rl("biped/combat/bow_aim_down"), (models) -> models.ENTITY_BIPED),
				new AimingAnimation.Builder(Animations.BIPED_BOW_REBOUND.getId(), 0.04F, false,
						DarkSouls.rl("biped/combat/bow_shot_mid"), DarkSouls.rl("biped/combat/bow_shot_up"),
						DarkSouls.rl("biped/combat/bow_shot_down"), (models) -> models.ENTITY_BIPED)
								.addProperty(AimingAnimationProperty.IS_REBOUND, true),

				new AimingAnimation.Builder(Animations.BIPED_SPEER_AIM.getId(), 0.16F, false,
						DarkSouls.rl("biped/combat/javelin_aim_mid"), DarkSouls.rl("biped/combat/javelin_aim_up"),
						DarkSouls.rl("biped/combat/javelin_aim_down"), (models) -> models.ENTITY_BIPED),
				new AimingAnimation.Builder(Animations.BIPED_SPEER_REBOUND.getId(), 0.08F, false,
						DarkSouls.rl("biped/combat/javelin_throw_mid"), DarkSouls.rl("biped/combat/javelin_throw_up"),
						DarkSouls.rl("biped/combat/javelin_throw_down"), (models) -> models.ENTITY_BIPED)
								.addProperty(AimingAnimationProperty.IS_REBOUND, true),

				new ActionAnimation.Builder(Animations.BIPED_HIT_LIGHT_FRONT.getId(), 0.05F,
						DarkSouls.rl("biped/hit/light_front"), (models) -> models.ENTITY_BIPED)
								.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true)
								.addProperty(ActionAnimationProperty.IS_HIT, true),
				new ActionAnimation.Builder(Animations.BIPED_HIT_LIGHT_LEFT.getId(), 0.05F,
						DarkSouls.rl("biped/hit/light_left"), (models) -> models.ENTITY_BIPED)
								.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true)
								.addProperty(ActionAnimationProperty.IS_HIT, true),
				new ActionAnimation.Builder(Animations.BIPED_HIT_LIGHT_RIGHT.getId(), 0.05F,
						DarkSouls.rl("biped/hit/light_right"), (models) -> models.ENTITY_BIPED)
								.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true)
								.addProperty(ActionAnimationProperty.IS_HIT, true),
				new ActionAnimation.Builder(Animations.BIPED_HIT_LIGHT_BACK.getId(), 0.05F,
						DarkSouls.rl("biped/hit/light_back"), (models) -> models.ENTITY_BIPED)
								.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true)
								.addProperty(ActionAnimationProperty.IS_HIT, true),
				new ActionAnimation.Builder(Animations.BIPED_HIT_HEAVY_FRONT.getId(), 0.05F,
						DarkSouls.rl("biped/hit/heavy_front"), (models) -> models.ENTITY_BIPED)
								.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true)
								.addProperty(ActionAnimationProperty.IS_HIT, true),
				new ActionAnimation.Builder(Animations.BIPED_HIT_HEAVY_BACK.getId(), 0.05F,
						DarkSouls.rl("biped/hit/heavy_back"), (models) -> models.ENTITY_BIPED)
								.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true)
								.addProperty(ActionAnimationProperty.IS_HIT, true),
				new ActionAnimation.Builder(Animations.BIPED_HIT_HEAVY_LEFT.getId(), 0.05F,
						DarkSouls.rl("biped/hit/heavy_left"), (models) -> models.ENTITY_BIPED)
								.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true)
								.addProperty(ActionAnimationProperty.IS_HIT, true),
				new ActionAnimation.Builder(Animations.BIPED_HIT_HEAVY_RIGHT.getId(), 0.05F,
						DarkSouls.rl("biped/hit/heavy_right"), (models) -> models.ENTITY_BIPED)
								.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true)
								.addProperty(ActionAnimationProperty.IS_HIT, true),

				new ActionAnimation.Builder(Animations.BIPED_HORSEBACK_HIT_LIGHT_FRONT.getId(), 0.05F,
						DarkSouls.rl("biped/hit/horseback_light_front"), (models) -> models.ENTITY_BIPED)
								.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true)
								.addProperty(ActionAnimationProperty.IS_HIT, true),
				new ActionAnimation.Builder(Animations.BIPED_HORSEBACK_HIT_LIGHT_LEFT.getId(), 0.05F,
						DarkSouls.rl("biped/hit/horseback_light_left"), (models) -> models.ENTITY_BIPED)
								.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true)
								.addProperty(ActionAnimationProperty.IS_HIT, true),
				new ActionAnimation.Builder(Animations.BIPED_HORSEBACK_HIT_LIGHT_RIGHT.getId(), 0.05F,
						DarkSouls.rl("biped/hit/horseback_light_right"), (models) -> models.ENTITY_BIPED)
								.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true)
								.addProperty(ActionAnimationProperty.IS_HIT, true),
				new ActionAnimation.Builder(Animations.BIPED_HORSEBACK_HIT_LIGHT_BACK.getId(), 0.05F,
						DarkSouls.rl("biped/hit/horseback_light_back"), (models) -> models.ENTITY_BIPED)
								.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true)
								.addProperty(ActionAnimationProperty.IS_HIT, true),
				new ActionAnimation.Builder(Animations.BIPED_HORSEBACK_HIT_HEAVY_FRONT.getId(), 0.05F,
						DarkSouls.rl("biped/hit/horseback_heavy_front"), (models) -> models.ENTITY_BIPED)
								.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true)
								.addProperty(ActionAnimationProperty.IS_HIT, true),
				new ActionAnimation.Builder(Animations.BIPED_HORSEBACK_HIT_HEAVY_BACK.getId(), 0.05F,
						DarkSouls.rl("biped/hit/horseback_heavy_back"), (models) -> models.ENTITY_BIPED)
								.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true)
								.addProperty(ActionAnimationProperty.IS_HIT, true),
				new ActionAnimation.Builder(Animations.BIPED_HORSEBACK_HIT_HEAVY_LEFT.getId(), 0.05F,
						DarkSouls.rl("biped/hit/horseback_heavy_left"), (models) -> models.ENTITY_BIPED)
								.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true)
								.addProperty(ActionAnimationProperty.IS_HIT, true),
				new ActionAnimation.Builder(Animations.BIPED_HORSEBACK_HIT_HEAVY_RIGHT.getId(), 0.05F,
						DarkSouls.rl("biped/hit/horseback_heavy_right"), (models) -> models.ENTITY_BIPED)
								.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true)
								.addProperty(ActionAnimationProperty.IS_HIT, true),

				new InvincibleAnimation.Builder(Animations.BIPED_HIT_SMASH.getId(), 0.05F,
						DarkSouls.rl("biped/hit/smash"), (models) -> models.ENTITY_BIPED),
				new InvincibleAnimation.Builder(Animations.BIPED_HIT_FLY_FRONT.getId(), 0.05F,
						DarkSouls.rl("biped/hit/fly"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.4F, Side.SERVER, ModSoundEvents.GENERIC_LAND),
										new PlaySoundEvent(0.8F, Side.SERVER, ModSoundEvents.GENERIC_LAND) }),
				new InvincibleAnimation.Builder(Animations.BIPED_HIT_FLY_BACK.getId(), 0.05F,
						DarkSouls.rl("biped/hit/fly_back"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.44F, Side.SERVER, ModSoundEvents.GENERIC_LAND),
										new PlaySoundEvent(0.8F, Side.SERVER, ModSoundEvents.GENERIC_LAND) }),
				new InvincibleAnimation.Builder(Animations.BIPED_HIT_FLY_LEFT.getId(), 0.05F,
						DarkSouls.rl("biped/hit/fly_left"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.48F, Side.SERVER, ModSoundEvents.GENERIC_LAND),
										new PlaySoundEvent(0.92F, Side.SERVER, ModSoundEvents.GENERIC_LAND) }),
				new InvincibleAnimation.Builder(Animations.BIPED_HIT_FLY_RIGHT.getId(), 0.05F,
						DarkSouls.rl("biped/hit/fly_right"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.48F, Side.SERVER, ModSoundEvents.GENERIC_LAND),
										new PlaySoundEvent(0.92F, Side.SERVER, ModSoundEvents.GENERIC_LAND) }),
				new ActionAnimation.Builder(Animations.BIPED_HIT_LAND_HEAVY.getId(), 0.05F,
						DarkSouls.rl("biped/hit/land_heavy"), (models) -> models.ENTITY_BIPED)
								.addProperty(ActionAnimationProperty.IS_HIT, true),

				new InvincibleAnimation.Builder(Animations.BIPED_HIT_BACKSTAB.getId(), 0.05F,
						DarkSouls.rl("biped/hit/backstab"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.DEATH_ANIMATION,
										DarkSouls.rl("biped_death_backstab"))
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new ImpactParticleEvent(0.44F, 0, 0, 0),
										new PlaySoundEvent(1.4F, Side.SERVER, ModSoundEvents.GENERIC_LAND) }),

				new InvincibleAnimation.Builder(Animations.BIPED_HIT_PUNISH.getId(), 0.05F,
						DarkSouls.rl("biped/hit/punish"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.DEATH_ANIMATION,
										DarkSouls.rl("biped_death_punish"))
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new ImpactParticleEvent(0.44F, 0, 0, 0),
										new PlaySoundEvent(1.4F, Side.SERVER, ModSoundEvents.GENERIC_LAND) }),

				new DodgingAnimation.Builder(Animations.BIPED_ROLL.getId(), 0.05F, DarkSouls.rl("biped/combat/roll"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.28F, Side.CLIENT, ModSoundEvents.GENERIC_ROLL) }),
				new DodgingAnimation.Builder(DarkSouls.rl("biped_fat_roll"), 0.05F,
						DarkSouls.rl("biped/combat/fat_roll"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.48F, Side.SERVER, ModSoundEvents.GENERIC_LAND),
										new ShakeCamGlobalEvent(0.48F, 10, 0.25F) }),
				new ActionAnimation.Builder(Animations.BIPED_ROLL_TOO_FAT.getId(), 0.05F,
						DarkSouls.rl("biped/combat/roll_too_fat"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.4F, Side.SERVER, ModSoundEvents.GENERIC_LAND),
										new ShakeCamGlobalEvent(0.4F, 10, 0.25F) }),
				new DodgingAnimation.Builder(Animations.BIPED_ROLL_BACK.getId(), 0.05F,
						DarkSouls.rl("biped/combat/roll_back"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.28F, Side.SERVER, ModSoundEvents.GENERIC_ROLL) }),
				new DodgingAnimation.Builder(Animations.BIPED_ROLL_LEFT.getId(), 0.05F, true,
						DarkSouls.rl("biped/combat/roll_left"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.28F, Side.SERVER, ModSoundEvents.GENERIC_ROLL) }),
				new DodgingAnimation.Builder(Animations.BIPED_ROLL_RIGHT.getId(), 0.05F, true,
						DarkSouls.rl("biped/combat/roll_right"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.28F, Side.SERVER, ModSoundEvents.GENERIC_ROLL) }),
				new DodgingAnimation.Builder(Animations.BIPED_JUMP_BACK.getId(), 0.08F,
						DarkSouls.rl("biped/combat/jump_back"), (models) -> models.ENTITY_BIPED),

				// Miracle
				new ActionAnimation.Builder(Animations.BIPED_CAST_MIRACLE_HEAL.getId(), 0.5F,
						DarkSouls.rl("biped/combat/cast_miracle"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new SetLightSourceEvent(AnimEvent.ON_BEGIN, 15, 4.5F),
										new PlaySoundEvent(AnimEvent.ON_BEGIN, Side.SERVER,
												ModSoundEvents.MIRACLE_USE_PRE),
										new EntityboundParticleEvent(AnimEvent.ON_BEGIN, ModParticles.MIRACLE_GLOW, 0,
												1, 0),
										new PlaySoundEvent(2.5F, Side.SERVER, ModSoundEvents.MIRACLE_USE),
										new SimpleParticleEvent(2.5F, ModParticles.MEDIUM_MIRACLE_CIRCLE, 0, 0.1F, 0),
										new HealInRadiusEvent(2.6F, 300F, 3.0F) }),

				new ActionAnimation.Builder(Animations.BIPED_CAST_MIRACLE_HEAL_AID.getId(), 0.5F,
						DarkSouls.rl("biped/combat/cast_miracle_fast"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new SetLightSourceEvent(AnimEvent.ON_BEGIN, 15, 3.0F),
										new PlaySoundEvent(AnimEvent.ON_BEGIN, Side.SERVER,
												ModSoundEvents.MIRACLE_USE_PRE),
										new EntityboundParticleEvent(AnimEvent.ON_BEGIN, ModParticles.FAST_MIRACLE_GLOW,
												0, 1, 0),
										new PlaySoundEvent(1.0F, Side.SERVER, ModSoundEvents.MIRACLE_USE),
										new SimpleParticleEvent(1.0F, ModParticles.TINY_MIRACLE_CIRCLE, 0, 0.1F, 0),
										new HealSelfEvent(1.1F, 150F) }),

				new ActionAnimation.Builder(Animations.BIPED_CAST_MIRACLE_HOMEWARD.getId(), 0.5F,
						DarkSouls.rl("biped/combat/cast_miracle"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new SetLightSourceEvent(AnimEvent.ON_BEGIN, 15, 5.0F),
										new PlaySoundEvent(AnimEvent.ON_BEGIN, Side.SERVER,
												ModSoundEvents.MIRACLE_USE_PRE),
										new EntityboundParticleEvent(AnimEvent.ON_BEGIN, ModParticles.FAST_MIRACLE_GLOW,
												0, 1, 0),
										new PlaySoundEvent(2.5F, Side.SERVER, ModSoundEvents.MIRACLE_USE),
										new SimpleParticleEvent(2.5F, ModParticles.TINY_MIRACLE_CIRCLE, 0, 0.1F, 0),
										new TeleportToSpawnEvent(3.5F) }),

				new ActionAnimation.Builder(Animations.BIPED_CAST_MIRACLE_FORCE.getId(), 0.3F,
						DarkSouls.rl("biped/combat/cast_miracle_force"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new SetLightSourceEvent(0.56F, 15, 1.85F),
										new SimpleParticleEvent(0.56F, ModParticles.FORCE, 0, 1, 0),
										new PlaySoundEvent(0.56F, Side.SERVER, ModSoundEvents.MIRACLE_FORCE),
										new ShockWaveEvent(0.6F, 3.0D, Anchor.ENTITY) }),

				new ActionAnimation.Builder(Animations.BIPED_CAST_MIRACLE_LIGHTNING_SPEAR.getId(), 0.3F,
						DarkSouls.rl("biped/combat/cast_miracle_spear"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new SetLightSourceEvent(AnimEvent.ON_BEGIN, 15, 1.2F),
										new PlaySoundEvent(AnimEvent.ON_BEGIN, Side.SERVER,
												ModSoundEvents.LIGHTNING_SPEAR_APPEAR),
										new EntityboundParticleEvent(AnimEvent.ON_BEGIN, ModParticles.LIGHTNING_SPEAR,
												0, 1, 0),
										new ShootMagicProjectileEvent(0.9F, LightningSpear::lightningSpear),
										new PlaySoundEvent(0.9F, Side.SERVER, ModSoundEvents.LIGHTNING_SPEAR_SHOT) }),
				new ActionAnimation.Builder(Animations.HORSEBACK_CAST_MIRACLE_LIGHTNING_SPEAR.getId(), 0.3F,
						DarkSouls.rl("biped/combat/horseback_cast_miracle_spear"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new SetLightSourceEvent(AnimEvent.ON_BEGIN, 15, 1.2F),
										new PlaySoundEvent(AnimEvent.ON_BEGIN, Side.SERVER,
												ModSoundEvents.LIGHTNING_SPEAR_APPEAR),
										new EntityboundParticleEvent(AnimEvent.ON_BEGIN, ModParticles.LIGHTNING_SPEAR,
												0, 1, 0),
										new ShootMagicProjectileEvent(0.9F, LightningSpear::lightningSpear),
										new PlaySoundEvent(0.9F, Side.SERVER, ModSoundEvents.LIGHTNING_SPEAR_SHOT) }),

				new ActionAnimation.Builder(Animations.BIPED_CAST_MIRACLE_GREAT_LIGHTNING_SPEAR.getId(), 0.3F,
						DarkSouls.rl("biped/combat/cast_miracle_spear"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new SetLightSourceEvent(AnimEvent.ON_BEGIN, 15, 1.2F),
										new PlaySoundEvent(AnimEvent.ON_BEGIN, Side.SERVER,
												ModSoundEvents.LIGHTNING_SPEAR_APPEAR),
										new EntityboundParticleEvent(AnimEvent.ON_BEGIN,
												ModParticles.GREAT_LIGHTNING_SPEAR, 0, 1, 0),
										new ShootMagicProjectileEvent(0.9F, LightningSpear::greatLightningSpear),
										new PlaySoundEvent(0.9F, Side.SERVER, ModSoundEvents.LIGHTNING_SPEAR_SHOT) }),
				new ActionAnimation.Builder(Animations.HORSEBACK_CAST_MIRACLE_GREAT_LIGHTNING_SPEAR.getId(), 0.3F,
						DarkSouls.rl("biped/combat/horseback_cast_miracle_spear"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new SetLightSourceEvent(AnimEvent.ON_BEGIN, 15, 1.2F),
										new PlaySoundEvent(AnimEvent.ON_BEGIN, Side.SERVER,
												ModSoundEvents.LIGHTNING_SPEAR_APPEAR),
										new EntityboundParticleEvent(AnimEvent.ON_BEGIN,
												ModParticles.GREAT_LIGHTNING_SPEAR, 0, 1, 0),
										new ShootMagicProjectileEvent(0.9F, LightningSpear::greatLightningSpear),
										new PlaySoundEvent(0.9F, Side.SERVER, ModSoundEvents.LIGHTNING_SPEAR_SHOT) }),

				// Big Weapon
				new MirrorAnimation.Builder(Animations.BIPED_HOLDING_BIG_WEAPON.getId(), 0.2F, true, true,
						DarkSouls.rl("biped/living/holding_big_weapon_r"),
						DarkSouls.rl("biped/living/holding_big_weapon_l"), (models) -> models.ENTITY_BIPED),

				// Horseback Attacks
				new AttackAnimation.Builder(Animations.HORSEBACK_LIGHT_ATTACK.getIds()[0], AttackType.LIGHT, 0.5F, 0.0F,
						0.2F, 0.52F, 1.6F, "Tool_R", DarkSouls.rl("biped/combat/horseback_light_attack_1"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM),
				new AttackAnimation.Builder(Animations.HORSEBACK_LIGHT_ATTACK.getIds()[1], AttackType.LIGHT, 0.5F, 0.0F,
						0.12F, 0.48F, 1.6F, "Tool_R", DarkSouls.rl("biped/combat/horseback_light_attack_2"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM),

				// Parries
				new ParryAnimation.Builder(Animations.SHIELD_PARRY_LEFT.getId(), 0.1F, 0.32F, 0.8F, "Tool_L",
						DarkSouls.rl("biped/combat/shield_parry"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.32F, Side.SERVER, ModSoundEvents.FIST_SWING) }),
				new ParryAnimation.Builder(Animations.SHIELD_PARRY_RIGHT.getId(), 0.1F, 0.32F, 0.8F, "Tool_R",
						DarkSouls.rl("biped/combat/shield_parry_mirrored"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.32F, Side.SERVER, ModSoundEvents.FIST_SWING) }),
				new ParryAnimation.Builder(Animations.BUCKLER_PARRY_LEFT.getId(), 0.15F, 0.32F, 0.8F, "Tool_L",
						DarkSouls.rl("biped/combat/buckler_parry"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.32F, Side.SERVER, ModSoundEvents.FIST_SWING) }),
				new ParryAnimation.Builder(Animations.BUCKLER_PARRY_RIGHT.getId(), 0.15F, 0.32F, 0.8F, "Tool_R",
						DarkSouls.rl("biped/combat/buckler_parry_mirrored"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.32F, Side.SERVER, ModSoundEvents.FIST_SWING) }),

				// Backstabs
				new BackstabCheckAnimation.Builder(Animations.BACKSTAB_THRUST.getId(), AttackType.BACKSTAB, 0.2F, 0.0F,
						0.36F, 0.64F, 1.44F, false, "Tool_R", DarkSouls.rl("biped/combat/backstab_thrust_check"),
						(models) -> models.ENTITY_BIPED, DarkSouls.rl("backstab_thrust_followup")),
				new InvincibleAnimation.Builder(DarkSouls.rl("backstab_thrust_followup"), 0.05F,
						DarkSouls.rl("biped/combat/backstab_thrust"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(AnimEvent.ON_BEGIN, Side.SERVER,
										ModSoundEvents.PLAYER_SHIELD_DISARMED),
										new ShakeCamEvent(AnimEvent.ON_BEGIN, 10, 0.5F),
										new PlaySoundEvent(1.08F, Side.SERVER, ModSoundEvents.GENERIC_KICK),
										new PlaySoundEvent(1.08F, Side.SERVER, ModSoundEvents.SWORD_PULLOUT),
										new ShakeCamEvent(1.3F, 10, 0.5F) }),
				new BackstabCheckAnimation.Builder(Animations.BACKSTAB_STRIKE.getId(), AttackType.BACKSTAB, 0.2F, 0.0F,
						0.4F, 0.8F, 1.44F, true, "Tool_R", DarkSouls.rl("biped/combat/backstab_strike_check"),
						(models) -> models.ENTITY_BIPED, DarkSouls.rl("backstab_strike_followup")),
				new CriticalHitAnimation.Builder(DarkSouls.rl("backstab_strike_followup"), 0.05F, 1.24F,
						DarkSouls.rl("biped/combat/backstab_strike"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(AnimEvent.ON_BEGIN, Side.SERVER,
										ModSoundEvents.PLAYER_SHIELD_DISARMED),
										new ShakeCamEvent(AnimEvent.ON_BEGIN, 10, 0.5F),
										new PlaySoundEvent(1.24F, Side.SERVER, ModSoundEvents.PLAYER_SHIELD_DISARMED),
										new ShakeCamEvent(1.3F, 10, 0.5F) }),

				// Punishes
				new PunishCheckAnimation.Builder(Animations.PUNISH_THRUST.getId(), AttackType.PUNISH, 0.2F, 0.0F, 0.36F,
						0.64F, 1.44F, false, "Tool_R", DarkSouls.rl("biped/combat/backstab_thrust_check"),
						(models) -> models.ENTITY_BIPED, DarkSouls.rl("punish_thrust_followup")),
				new InvincibleAnimation.Builder(DarkSouls.rl("punish_thrust_followup"), 0.05F,
						DarkSouls.rl("biped/combat/backstab_thrust"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(AnimEvent.ON_BEGIN, Side.SERVER,
										ModSoundEvents.PLAYER_SHIELD_DISARMED),
										new PlaySoundEvent(AnimEvent.ON_BEGIN, Side.SERVER,
												ModSoundEvents.GENERIC_KILL_CHANCE),
										new ShakeCamEvent(AnimEvent.ON_BEGIN, 10, 0.5F),
										new PlaySoundEvent(1.08F, Side.SERVER, ModSoundEvents.GENERIC_KICK),
										new PlaySoundEvent(1.08F, Side.SERVER, ModSoundEvents.SWORD_PULLOUT),
										new ShakeCamEvent(1.3F, 10, 0.5F) }),
				new PunishCheckAnimation.Builder(Animations.PUNISH_STRIKE.getId(), AttackType.PUNISH, 0.2F, 0.0F, 0.4F,
						0.8F, 1.44F, true, "Tool_R", DarkSouls.rl("biped/combat/backstab_strike_check"),
						(models) -> models.ENTITY_BIPED, DarkSouls.rl("punish_strike_followup")),
				new CriticalHitAnimation.Builder(DarkSouls.rl("punish_strike_followup"), 0.05F, 1.24F,
						DarkSouls.rl("biped/combat/backstab_strike"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(AnimEvent.ON_BEGIN, Side.SERVER,
										ModSoundEvents.PLAYER_SHIELD_DISARMED),
										new PlaySoundEvent(AnimEvent.ON_BEGIN, Side.SERVER,
												ModSoundEvents.GENERIC_KILL_CHANCE),
										new ShakeCamEvent(AnimEvent.ON_BEGIN, 10, 0.5F),
										new PlaySoundEvent(1.24F, Side.SERVER, ModSoundEvents.PLAYER_SHIELD_DISARMED),
										new ShakeCamEvent(1.3F, 10, 0.5F) }),

				// Thrusting Sword
				new AttackAnimation.Builder(Animations.THRUSTING_SWORD_LIGHT_ATTACK.getId(), AttackType.LIGHT, 0.2F,
						0.0F, 0.28F, 0.4F, 1.2F, "Tool_R", DarkSouls.rl("biped/combat/thrusting_sword_la"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.28F, Side.SERVER, ModSoundEvents.SWORD_THRUST) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STUN_TYPE, StunType.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 25)
								.addProperty(AttackProperty.STAMINA_USAGE, 15)
								.addProperty(AttackProperty.POISE_DAMAGE, 15),

				new AttackAnimation.Builder(Animations.THRUSTING_SWORD_HEAVY_ATTACK.getIds()[0], AttackType.HEAVY, 0.2F,
						0.0F, 0.72F, 0.84F, 1.6F, "Tool_R", DarkSouls.rl("biped/combat/thrusting_sword_ha_1"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.72F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 37)
								.addProperty(AttackProperty.STAMINA_USAGE, 25)
								.addProperty(AttackProperty.POISE_DAMAGE, 15),
				new AttackAnimation.Builder(Animations.THRUSTING_SWORD_HEAVY_ATTACK.getIds()[1], AttackType.HEAVY, 0.2F,
						0.0F, 0.68F, 0.8F, 2.0F, "Tool_R", DarkSouls.rl("biped/combat/thrusting_sword_ha_2"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.68F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 37)
								.addProperty(AttackProperty.STAMINA_USAGE, 25)
								.addProperty(AttackProperty.POISE_DAMAGE, 15),

				new AttackAnimation.Builder(Animations.THRUSTING_SWORD_DASH_ATTACK.getId(), AttackType.DASH, 0.2F, 0.0F,
						0.6F, 0.88F, 1.6F, "Tool_R", DarkSouls.rl("biped/combat/thrusting_sword_da"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.6F, Side.SERVER, ModSoundEvents.SWORD_THRUST) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STUN_TYPE, StunType.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 37)
								.addProperty(AttackProperty.STAMINA_USAGE, 25)
								.addProperty(AttackProperty.POISE_DAMAGE, 15),
				new AttackAnimation.Builder(Animations.THRUSTING_SWORD_TH_LIGHT_ATTACK.getId(), AttackType.LIGHT, 0.2F,
						0.0F, 0.28F, 0.4F, 1.2F, "Tool_R", DarkSouls.rl("biped/combat/thrusting_sword_th_la"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.28F, Side.SERVER, ModSoundEvents.SWORD_THRUST) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STUN_TYPE, StunType.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 37)
								.addProperty(AttackProperty.STAMINA_USAGE, 15)
								.addProperty(AttackProperty.POISE_DAMAGE, 15),

				new AttackAnimation.Builder(Animations.THRUSTING_SWORD_TH_HEAVY_ATTACK.getIds()[0], AttackType.HEAVY,
						0.2F, 0.0F, 0.72F, 0.84F, 1.6F, "Tool_R", DarkSouls.rl("biped/combat/thrusting_sword_th_ha_1"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.72F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 37)
								.addProperty(AttackProperty.STAMINA_USAGE, 25)
								.addProperty(AttackProperty.POISE_DAMAGE, 15),
				new AttackAnimation.Builder(Animations.THRUSTING_SWORD_TH_HEAVY_ATTACK.getIds()[1], AttackType.HEAVY,
						0.2F, 0.0F, 0.68F, 0.8F, 2.0F, "Tool_R", DarkSouls.rl("biped/combat/thrusting_sword_th_ha_2"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.68F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 37)
								.addProperty(AttackProperty.STAMINA_USAGE, 25)
								.addProperty(AttackProperty.POISE_DAMAGE, 15),

				new AttackAnimation.Builder(Animations.THRUSTING_SWORD_TH_DASH_ATTACK.getId(), AttackType.DASH, 0.2F,
						0.0F, 0.2F, 0.36F, 1.2F, "Tool_R", DarkSouls.rl("biped/combat/thrusting_sword_th_da"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.2F, Side.SERVER, ModSoundEvents.SWORD_THRUST) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STUN_TYPE, StunType.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 37)
								.addProperty(AttackProperty.STAMINA_USAGE, 24)
								.addProperty(AttackProperty.POISE_DAMAGE, 15),

				// Greatsword
				new AttackAnimation.Builder(Animations.GREATSWORD_LIGHT_ATTACK.getIds()[0], AttackType.LIGHT, 0.3F,
						0.0F, 0.44F, 0.68F, 2.0F, "Tool_R", DarkSouls.rl("biped/combat/greatsword_light_attack_1"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.44F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 30)
								.addProperty(AttackProperty.STAMINA_USAGE, 25)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),
				new AttackAnimation.Builder(Animations.GREATSWORD_LIGHT_ATTACK.getIds()[1], AttackType.LIGHT, 0.3F,
						0.0F, 0.32F, 0.68F, 1.8F, "Tool_R", DarkSouls.rl("biped/combat/greatsword_light_attack_2"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.44F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 30)
								.addProperty(AttackProperty.STAMINA_USAGE, 25)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),

				new AttackAnimation.Builder(Animations.GREATSWORD_THRUST.getId(), AttackType.HEAVY, 0.3F, 0.0F, 0.28F,
						0.68F, 1.6F, "Tool_R", DarkSouls.rl("biped/combat/greatsword_heavy_attack"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.28F, Side.SERVER, ModSoundEvents.SWORD_THRUST) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 36)
								.addProperty(AttackProperty.STAMINA_USAGE, 35)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),
				new AttackAnimation.Builder(Animations.GREATSWORD_UPWARD_SLASH.getId(), AttackType.HEAVY, 0.3F, 0.0F,
						0.28F, 0.56F, 2.4F, "Tool_R", DarkSouls.rl("biped/combat/greatsword_upward_slash"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.28F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 36)
								.addProperty(AttackProperty.STAMINA_USAGE, 35)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),
				new AttackAnimation.Builder(Animations.GREATSWORD_STYLISH_THRUST.getId(), AttackType.HEAVY, 0.3F, 0.0F,
						0.64F, 0.76F, 2.8F, "Tool_R", DarkSouls.rl("biped/combat/greatsword_stylish_thrust"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.64F, Side.SERVER, ModSoundEvents.SWORD_THRUST) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 36)
								.addProperty(AttackProperty.STAMINA_USAGE, 35)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),
				new AttackAnimation.Builder(Animations.GREATSWORD_DASH_ATTACK.getId(), AttackType.DASH, 0.1F, 0.0F,
						0.48F, 0.84F, 1.6F, "Tool_R", DarkSouls.rl("biped/combat/greatsword_dash_attack"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.4F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 30)
								.addProperty(AttackProperty.STAMINA_USAGE, 30)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),

				new AttackAnimation.Builder(Animations.GREATSWORD_TH_LIGHT_ATTACK.getIds()[0],
						AttackType.TWO_HANDED_LIGHT, 0.5F, 0.0F, 0.2F, 0.44F, 1.6F, "Tool_R",
						DarkSouls.rl("biped/combat/greatsword_th_la_1"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.2F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 35)
								.addProperty(AttackProperty.STAMINA_USAGE, 25)
								.addProperty(AttackProperty.POISE_DAMAGE, 26),
				new AttackAnimation.Builder(Animations.GREATSWORD_TH_LIGHT_ATTACK.getIds()[1],
						AttackType.TWO_HANDED_LIGHT, 0.3F, 0.0F, 0.28F, 0.48F, 2.0F, "Tool_R",
						DarkSouls.rl("biped/combat/greatsword_th_la_2"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.28F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 35)
								.addProperty(AttackProperty.STAMINA_USAGE, 25)
								.addProperty(AttackProperty.POISE_DAMAGE, 26),

				new AttackAnimation.Builder(Animations.GREATSWORD_TH_THRUST_ATTACK.getIds()[0],
						AttackType.TWO_HANDED_HEAVY, 0.5F, 0.0F, 0.4F, 0.52F, 1.6F, "Tool_R",
						DarkSouls.rl("biped/combat/greatsword_th_ha_1"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.4F, Side.SERVER, ModSoundEvents.SWORD_THRUST) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 42)
								.addProperty(AttackProperty.STAMINA_USAGE, 35)
								.addProperty(AttackProperty.POISE_DAMAGE, 26),
				new AttackAnimation.Builder(Animations.GREATSWORD_TH_THRUST_ATTACK.getIds()[1],
						AttackType.TWO_HANDED_HEAVY, 0.3F, 0.0F, 0.28F, 0.48F, 2.0F, "Tool_R",
						DarkSouls.rl("biped/combat/greatsword_th_la_2"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.28F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 42)
								.addProperty(AttackProperty.STAMINA_USAGE, 35)
								.addProperty(AttackProperty.POISE_DAMAGE, 26),

				new AttackAnimation.Builder(Animations.GREATSWORD_TH_DASH_ATTACK.getId(), AttackType.TWO_HANDED_DASH,
						0.05F, 0.0F, 0.4F, 0.52F, 1.6F, "Tool_R", DarkSouls.rl("biped/combat/greatsword_th_ha_1"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.4F, Side.SERVER, ModSoundEvents.SWORD_THRUST) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 40)
								.addProperty(AttackProperty.STAMINA_USAGE, 30)
								.addProperty(AttackProperty.POISE_DAMAGE, 25),

				// Ultra Greatsword
				new AttackAnimation.Builder(Animations.ULTRA_GREATSWORD_LIGHT_ATTACK.getIds()[0], AttackType.LIGHT,
						0.3F, 0.0F, 0.48F, 0.88F, 2.8F, "Tool_R",
						DarkSouls.rl("biped/combat/ultra_greatsword_light_attack_1"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_SWORD.appendTo(0.7F, new AnimEvent[]
										{}))
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 70)
								.addProperty(AttackProperty.STAMINA_USAGE, 30)
								.addProperty(AttackProperty.POISE_DAMAGE, 28),
				new AttackAnimation.Builder(Animations.ULTRA_GREATSWORD_LIGHT_ATTACK.getIds()[1], AttackType.LIGHT,
						0.2F, 0.0F, 0.44F, 0.88F, 2.4F, "Tool_R",
						DarkSouls.rl("biped/combat/ultra_greatsword_light_attack_2"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_SWORD.appendTo(0.72F, new AnimEvent[]
										{}))
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 70)
								.addProperty(AttackProperty.STAMINA_USAGE, 30)
								.addProperty(AttackProperty.POISE_DAMAGE, 28),

				new AttackAnimation.Builder(Animations.ULTRA_GREATSWORD_HEAVY_ATTACK.getIds()[0], AttackType.HEAVY,
						0.5F, 0.0F, 1.35F, 1.6F, 3.45F, "Tool_R",
						DarkSouls.rl("biped/combat/ultra_greatsword_heavy_attack_1"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_SWORD.appendTo(1.3F, new AnimEvent[]
										{})).addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 70)
								.addProperty(AttackProperty.STAMINA_USAGE, 40)
								.addProperty(AttackProperty.POISE_DAMAGE, 36),
				new AttackAnimation.Builder(Animations.ULTRA_GREATSWORD_HEAVY_ATTACK.getIds()[1], AttackType.HEAVY,
						0.5F, 0.0F, 1.35F, 1.6F, 3.45F, "Tool_R",
						DarkSouls.rl("biped/combat/ultra_greatsword_heavy_attack_2"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_SWORD.appendTo(1.3F, new AnimEvent[]
										{})).addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 70)
								.addProperty(AttackProperty.STAMINA_USAGE, 40)
								.addProperty(AttackProperty.POISE_DAMAGE, 36),

				new AttackAnimation.Builder(Animations.ULTRA_GREATSWORD_DASH_ATTACK.getId(), AttackType.DASH, 0.1F,
						0.0F, 0.68F, 0.96F, 2.0F, "Tool_R", DarkSouls.rl("biped/combat/greataxe_da"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_HAMMER.appendTo(0.93F, new AnimEvent[]
										{}))
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 70)
								.addProperty(AttackProperty.STAMINA_USAGE, 35)
								.addProperty(AttackProperty.POISE_DAMAGE, 28),

				new AttackAnimation.Builder(Animations.ULTRA_GREATSWORD_TH_LIGHT_ATTACK.getIds()[0],
						AttackType.TWO_HANDED_LIGHT, 0.3F, 0.0F, 0.48F, 0.88F, 2.8F, "Tool_R",
						DarkSouls.rl("biped/combat/ultra_greatsword_th_la_1"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_SWORD.appendTo(0.7F, new AnimEvent[]
										{}))
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 75)
								.addProperty(AttackProperty.STAMINA_USAGE, 30)
								.addProperty(AttackProperty.POISE_DAMAGE, 32),
				new AttackAnimation.Builder(Animations.ULTRA_GREATSWORD_TH_LIGHT_ATTACK.getIds()[1],
						AttackType.TWO_HANDED_LIGHT, 0.2F, 0.0F, 0.44F, 0.88F, 2.4F, "Tool_R",
						DarkSouls.rl("biped/combat/ultra_greatsword_th_la_2"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_SWORD.appendTo(0.72F, new AnimEvent[]
										{}))
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 75)
								.addProperty(AttackProperty.STAMINA_USAGE, 30)
								.addProperty(AttackProperty.POISE_DAMAGE, 32),

				new AttackAnimation.Builder(Animations.ULTRA_GREATSWORD_TH_HEAVY_ATTACK.getIds()[0],
						AttackType.TWO_HANDED_HEAVY, 0.5F, 0.0F, 1.35F, 1.6F, 3.45F, "Tool_R",
						DarkSouls.rl("biped/combat/ultra_greatsword_th_ha_1"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_SWORD.appendTo(1.3F, new AnimEvent[]
										{})).addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 76)
								.addProperty(AttackProperty.STAMINA_USAGE, 40)
								.addProperty(AttackProperty.POISE_DAMAGE, 40),
				new AttackAnimation.Builder(Animations.ULTRA_GREATSWORD_TH_HEAVY_ATTACK.getIds()[1],
						AttackType.TWO_HANDED_HEAVY, 0.5F, 0.0F, 1.35F, 1.6F, 3.45F, "Tool_R",
						DarkSouls.rl("biped/combat/ultra_greatsword_th_ha_2"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_SWORD.appendTo(1.3F, new AnimEvent[]
										{})).addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 76)
								.addProperty(AttackProperty.STAMINA_USAGE, 40)
								.addProperty(AttackProperty.POISE_DAMAGE, 40),

				new AttackAnimation.Builder(Animations.ULTRA_GREATSWORD_TH_DASH_ATTACK.getId(),
						AttackType.TWO_HANDED_DASH, 0.1F, 0.0F, 1.35F, 1.6F, 3.45F, "Tool_R",
						DarkSouls.rl("biped/combat/ultra_greatsword_th_ha_1"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_SWORD.appendTo(1.6F, new AnimEvent[]
										{})).addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 76)
								.addProperty(AttackProperty.STAMINA_USAGE, 35)
								.addProperty(AttackProperty.POISE_DAMAGE, 40),

				// Greataxe
				new AttackAnimation.Builder(Animations.GREATAXE_LIGHT_ATTACK.getIds()[0], AttackType.LIGHT, 0.3F, 0.0F,
						0.64F, 0.92F, 2.0F, "Tool_R", DarkSouls.rl("biped/combat/greataxe_la_1"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_HAMMER.appendTo(0.92F, new AnimEvent[]
										{})).addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 75)
								.addProperty(AttackProperty.STAMINA_USAGE, 30)
								.addProperty(AttackProperty.POISE_DAMAGE, 30),
				new AttackAnimation.Builder(Animations.GREATAXE_LIGHT_ATTACK.getIds()[1], AttackType.LIGHT, 0.3F, 0.0F,
						0.76F, 1.0F, 2.0F, "Tool_R", DarkSouls.rl("biped/combat/greataxe_la_2"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_HAMMER.appendTo(1.0F, new AnimEvent[]
										{})).addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 75)
								.addProperty(AttackProperty.STAMINA_USAGE, 30)
								.addProperty(AttackProperty.POISE_DAMAGE, 30),

				new AttackAnimation.Builder(Animations.GREATAXE_HEAVY_ATTACK.getId(), AttackType.HEAVY, 0.3F, 0.0F,
						1.08F, 1.28F, 2.2F, "Tool_R", DarkSouls.rl("biped/combat/greataxe_ha"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_HAMMER.appendTo(1.28F, new AnimEvent[]
										{})).addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 75)
								.addProperty(AttackProperty.STAMINA_USAGE, 40)
								.addProperty(AttackProperty.POISE_DAMAGE, 30),
				new AttackAnimation.Builder(Animations.GREATAXE_DASH_ATTACK.getId(), AttackType.DASH, 0.1F, 0.0F, 0.68F,
						0.96F, 2.0F, "Tool_R", DarkSouls.rl("biped/combat/greataxe_da"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_HAMMER.appendTo(0.96F, new AnimEvent[]
										{})).addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 75)
								.addProperty(AttackProperty.STAMINA_USAGE, 35)
								.addProperty(AttackProperty.POISE_DAMAGE, 30),

				new AttackAnimation.Builder(Animations.GREATAXE_TH_LIGHT_ATTACK.getIds()[0],
						AttackType.TWO_HANDED_LIGHT, 0.3F, 0.0F, 0.8F, 0.92F, 2.2F, "Tool_R",
						DarkSouls.rl("biped/combat/greataxe_th_la_1"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_HAMMER.appendTo(0.92F, new AnimEvent[]
										{})).addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 75)
								.addProperty(AttackProperty.STAMINA_USAGE, 30)
								.addProperty(AttackProperty.POISE_DAMAGE, 30),
				new AttackAnimation.Builder(Animations.GREATAXE_TH_LIGHT_ATTACK.getIds()[1],
						AttackType.TWO_HANDED_LIGHT, 0.3F, 0.0F, 0.56F, 0.88F, 2.2F, "Tool_R",
						DarkSouls.rl("biped/combat/greataxe_th_la_2"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_HAMMER.appendTo(0.88F, new AnimEvent[]
										{})).addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 75)
								.addProperty(AttackProperty.STAMINA_USAGE, 30)
								.addProperty(AttackProperty.POISE_DAMAGE, 30),

				new AttackAnimation.Builder(Animations.GREATAXE_TH_HEAVY_ATTACK.getId(), AttackType.TWO_HANDED_HEAVY,
						0.3F, 0.0F, 0.92F, 1.2F, 2.4F, "Tool_R", DarkSouls.rl("biped/combat/greataxe_th_ha"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_HAMMER.appendTo(1.2F, new AnimEvent[]
										{})).addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 75)
								.addProperty(AttackProperty.STAMINA_USAGE, 40)
								.addProperty(AttackProperty.POISE_DAMAGE, 30),
				new AttackAnimation.Builder(Animations.GREATAXE_TH_DASH_ATTACK.getId(), AttackType.TWO_HANDED_DASH,
						0.1F, 0.0F, 0.68F, 0.96F, 2.0F, "Tool_R", DarkSouls.rl("biped/combat/greataxe_th_da"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_HAMMER.appendTo(0.96F, new AnimEvent[]
										{})).addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 75)
								.addProperty(AttackProperty.STAMINA_USAGE, 35)
								.addProperty(AttackProperty.POISE_DAMAGE, 30),

				// Spear
				new AttackAnimation.Builder(Animations.SPEAR_DASH_ATTACK.getId(), AttackType.DASH, 0.2F, 0.0F, 0.4F,
						0.52F, 1.08F, "Tool_R", DarkSouls.rl("biped/combat/spear_dash_attack"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.4F, Side.SERVER, ModSoundEvents.SPEAR_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 40)
								.addProperty(AttackProperty.STAMINA_USAGE, 20)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(Animations.SPEAR_HEAVY_ATTACK.getId(), AttackType.HEAVY, 0.35F, 0.0F, 0.65F,
						0.8F, 1.75F, "Tool_R", DarkSouls.rl("biped/combat/spear_heavy_attack"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.65F, Side.SERVER, ModSoundEvents.SPEAR_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 40)
								.addProperty(AttackProperty.STAMINA_USAGE, 25)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(Animations.SPEAR_LIGHT_ATTACK.getId(), AttackType.LIGHT, 0.15F, 0.0F, 0.32F,
						0.6F, 1.5F, "Tool_R", DarkSouls.rl("biped/combat/spear_light_attack"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.32F, Side.SERVER, ModSoundEvents.SPEAR_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 40)
								.addProperty(AttackProperty.STAMINA_USAGE, 15)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(Animations.SPEAR_LIGHT_BLOCKING_ATTACK.getId(), AttackType.LIGHT, 0.2F,
						0.0F, 0.35F, 0.5F, 1.25F, "Tool_R", DarkSouls.rl("biped/combat/spear_light_blocking_attack"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.35F, Side.SERVER, ModSoundEvents.SPEAR_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.BLOCKING, true)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 40)
								.addProperty(AttackProperty.STAMINA_USAGE, 15)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(Animations.SPEAR_TH_LIGHT_ATTACK.getId(), AttackType.TWO_HANDED_LIGHT,
						0.15F, 0.0F, 0.4F, 0.64F, 1.2F, "Tool_R", DarkSouls.rl("biped/combat/spear_th_la"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.4F, Side.SERVER, ModSoundEvents.SPEAR_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 42)
								.addProperty(AttackProperty.STAMINA_USAGE, 15)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),
				new AttackAnimation.Builder(Animations.SPEAR_TH_HEAVY_ATTACK.getId(), AttackType.TWO_HANDED_HEAVY,
						0.35F, 0.0F, 0.6F, 0.8F, 1.6F, "Tool_R", DarkSouls.rl("biped/combat/spear_th_ha"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.6F, Side.SERVER, ModSoundEvents.SPEAR_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.STAMINA_USAGE, 25)
								.addProperty(AttackProperty.POISE_DAMAGE, 26),
				new AttackAnimation.Builder(Animations.SPEAR_TH_DASH_ATTACK.getId(), AttackType.TWO_HANDED_DASH, 0.2F,
						0.0F, 0.36F, 0.55F, 1.2F, "Tool_R", DarkSouls.rl("biped/combat/spear_th_da"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.36F, Side.SERVER, ModSoundEvents.SPEAR_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.STAMINA_USAGE, 20)
								.addProperty(AttackProperty.POISE_DAMAGE, 24),

				// Dagger
				new AttackAnimation.Builder(Animations.DAGGER_HEAVY_ATTACK.getId(), AttackType.HEAVY, 0.2F, 0.0F, 0.68F,
						0.96F, 1.6F, "Tool_R", DarkSouls.rl("biped/combat/dagger_heavy_attack"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT),

				new AttackAnimation.Builder(Animations.DAGGER_LIGHT_ATTACK.getIds()[0], AttackType.LIGHT, 0.2F, 0.0F,
						0.16F, 0.4F, 1.0F, "Tool_R", DarkSouls.rl("biped/combat/dagger_light_attack_1"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.SLASH)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT),
				new AttackAnimation.Builder(Animations.DAGGER_LIGHT_ATTACK.getIds()[1], AttackType.LIGHT, 0.2F, 0.0F,
						0.04F, 0.32F, 1.0F, "Tool_R", DarkSouls.rl("biped/combat/dagger_light_attack_2"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.SLASH)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT),

				// Great Hammer
				new AttackAnimation.Builder(Animations.GREAT_HAMMER_HEAVY_ATTACK.getId(), AttackType.HEAVY, 0.5F, 0.0F,
						1.45F, 1.9F, 3.75F, "Tool_R", DarkSouls.rl("biped/combat/great_hammer_heavy_attack"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_HAMMER.appendTo(1.6F, new AnimEvent[]
										{})).addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 80)
								.addProperty(AttackProperty.STAMINA_USAGE, 40)
								.addProperty(AttackProperty.POISE_DAMAGE, 32),

				new AttackAnimation.Builder(Animations.GREAT_HAMMER_LIGHT_ATTACK.getIds()[0], AttackType.LIGHT, 0.5F,
						0.0F, 0.84F, 1.38F, 2.76F, "Tool_R", DarkSouls.rl("biped/combat/great_hammer_light_attack_1"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_HAMMER.appendTo(1.3F, new AnimEvent[]
										{})).addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 80)
								.addProperty(AttackProperty.STAMINA_USAGE, 30)
								.addProperty(AttackProperty.POISE_DAMAGE, 28),
				new AttackAnimation.Builder(Animations.GREAT_HAMMER_LIGHT_ATTACK.getIds()[1], AttackType.LIGHT, 0.5F,
						0.0F, 1.15F, 1.7F, 3.45F, "Tool_R", DarkSouls.rl("biped/combat/great_hammer_light_attack_2"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_HAMMER.appendTo(1.3F, new AnimEvent[]
										{})).addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 80)
								.addProperty(AttackProperty.STAMINA_USAGE, 30)
								.addProperty(AttackProperty.POISE_DAMAGE, 28),

				new AttackAnimation.Builder(Animations.GREAT_HAMMER_DASH_ATTACK.getId(), AttackType.DASH, 0.1F, 0.0F,
						0.68F, 0.96F, 2.0F, "Tool_R", DarkSouls.rl("biped/combat/greataxe_da"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_HAMMER.appendTo(0.93F, new AnimEvent[]
										{})).addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 80)
								.addProperty(AttackProperty.STAMINA_USAGE, 35)
								.addProperty(AttackProperty.POISE_DAMAGE, 28),

				new AttackAnimation.Builder(Animations.GREAT_HAMMER_TH_LIGHT_ATTACK.getIds()[0], AttackType.LIGHT, 0.5F,
						0.0F, 0.32F, 0.56F, 1.6F, "Tool_R", DarkSouls.rl("biped/combat/great_hammer_th_la_1"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_HAMMER.appendTo(0.56F, new AnimEvent[]
										{})).addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 90)
								.addProperty(AttackProperty.STAMINA_USAGE, 30)
								.addProperty(AttackProperty.POISE_DAMAGE, 36),
				new AttackAnimation.Builder(Animations.GREAT_HAMMER_TH_LIGHT_ATTACK.getIds()[1], AttackType.LIGHT, 0.5F,
						0.0F, 0.24F, 0.48F, 1.2F, "Tool_R", DarkSouls.rl("biped/combat/great_hammer_th_la_2"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_HAMMER.appendTo(0.48F, new AnimEvent[]
										{})).addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 90)
								.addProperty(AttackProperty.STAMINA_USAGE, 30)
								.addProperty(AttackProperty.POISE_DAMAGE, 36),

				new AttackAnimation.Builder(Animations.GREAT_HAMMER_TH_HEAVY_ATTACK.getId(), AttackType.HEAVY, 0.5F,
						0.0F, 0.32F, 0.52F, 1.2F, "Tool_R", DarkSouls.rl("biped/combat/great_hammer_th_ha"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_HAMMER.appendTo(0.52F, new AnimEvent[]
										{})).addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 95)
								.addProperty(AttackProperty.STAMINA_USAGE, 40)
								.addProperty(AttackProperty.POISE_DAMAGE, 39),
				new AttackAnimation.Builder(Animations.GREAT_HAMMER_TH_DASH_ATTACK.getId(), AttackType.LIGHT, 0.5F,
						0.0F, 0.32F, 0.56F, 1.6F, "Tool_R", DarkSouls.rl("biped/combat/great_hammer_th_la_1"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_HAMMER.appendTo(0.56F, new AnimEvent[]
										{})).addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 90)
								.addProperty(AttackProperty.STAMINA_USAGE, 35)
								.addProperty(AttackProperty.POISE_DAMAGE, 36),

				// Axe
				new AttackAnimation.Builder(Animations.AXE_HEAVY_ATTACK.getId(), AttackType.HEAVY, 0.3F, 0.0F, 0.55F,
						0.7F, 1.5F, "Tool_R", DarkSouls.rl("biped/combat/axe_heavy_attack"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.4F, Side.SERVER, ModSoundEvents.AXE_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
								.addProperty(AttackProperty.STAMINA_USAGE, 30)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),

				new AttackAnimation.Builder(Animations.AXE_LIGHT_ATTACK.getIds()[0], AttackType.LIGHT, 0.3F, 0.0F, 0.2F,
						0.35F, 1.5F, "Tool_R", DarkSouls.rl("biped/combat/axe_light_attack_1"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.16F, Side.SERVER, ModSoundEvents.AXE_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
								.addProperty(AttackProperty.STAMINA_USAGE, 20)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),
				new AttackAnimation.Builder(Animations.AXE_LIGHT_ATTACK.getIds()[1], AttackType.LIGHT, 0.2F, 0.0F,
						0.15F, 0.4F, 1.25F, "Tool_R", DarkSouls.rl("biped/combat/axe_light_attack_2"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.12F, Side.SERVER, ModSoundEvents.AXE_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
								.addProperty(AttackProperty.STAMINA_USAGE, 20)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),

				new AttackAnimation.Builder(Animations.AXE_DASH_ATTACK.getId(), AttackType.DASH, 0.2F, 0.0F, 0.4F, 0.6F,
						1.35F, "Tool_R", DarkSouls.rl("biped/combat/axe_dash_attack"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.35F, Side.SERVER, ModSoundEvents.AXE_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
								.addProperty(AttackProperty.STAMINA_USAGE, 25)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),

				new AttackAnimation.Builder(Animations.AXE_TH_LIGHT_ATTACK.getIds()[0], AttackType.TWO_HANDED_LIGHT,
						0.4F, 0.0F, 0.08F, 0.4F, 1.2F, "Tool_R", DarkSouls.rl("biped/combat/axe_th_la_1"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.08F, Side.SERVER, ModSoundEvents.AXE_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
								.addProperty(AttackProperty.STAMINA_USAGE, 20)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),
				new AttackAnimation.Builder(Animations.AXE_TH_LIGHT_ATTACK.getIds()[1], AttackType.TWO_HANDED_LIGHT,
						0.4F, 0.0F, 0.08F, 0.45F, 1.25F, "Tool_R", DarkSouls.rl("biped/combat/axe_th_la_2"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.08F, Side.SERVER, ModSoundEvents.AXE_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
								.addProperty(AttackProperty.STAMINA_USAGE, 20)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),

				new AttackAnimation.Builder(Animations.AXE_TH_HEAVY_ATTACK.getId(), AttackType.TWO_HANDED_HEAVY, 0.4F,
						0.0F, 0.32F, 0.56F, 1.6F, "Tool_R", DarkSouls.rl("biped/combat/axe_th_ha"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.32F, Side.SERVER, ModSoundEvents.AXE_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
								.addProperty(AttackProperty.STAMINA_USAGE, 30)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),
				new AttackAnimation.Builder(Animations.AXE_TH_DASH_ATTACK.getId(), AttackType.TWO_HANDED_DASH, 0.1F,
						0.0F, 0.48F, 0.68F, 1.6F, "Tool_R", DarkSouls.rl("biped/combat/axe_th_da"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.48F, Side.SERVER, ModSoundEvents.AXE_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 80)
								.addProperty(AttackProperty.STAMINA_USAGE, 25)
								.addProperty(AttackProperty.POISE_DAMAGE, 28),

				// Hammer
				new AttackAnimation.Builder(Animations.HAMMER_DASH_ATTACK.getId(), AttackType.DASH, 0.2F, 0.0F, 0.4F,
						0.6F, 1.35F, "Tool_R", DarkSouls.rl("biped/combat/axe_dash_attack"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.32F, Side.SERVER, ModSoundEvents.AXE_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 56)
								.addProperty(AttackProperty.STAMINA_USAGE, 25)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),
				new AttackAnimation.Builder(Animations.HAMMER_HEAVY_ATTACK.getId(), AttackType.HEAVY, 0.5F, 0.0F, 0.28F,
						0.6F, 1.4F, "Tool_R", DarkSouls.rl("biped/combat/hammer_heavy_attack"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.32F, Side.SERVER, ModSoundEvents.AXE_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 68)
								.addProperty(AttackProperty.STAMINA_USAGE, 30)
								.addProperty(AttackProperty.POISE_DAMAGE, 36),
				new AttackAnimation.Builder(Animations.HAMMER_LIGHT_ATTACK.getId(), AttackType.LIGHT, 0.3F, 0.0F, 0.24F,
						0.6F, 1.2F, "Tool_R", DarkSouls.rl("biped/combat/hammer_light_attack"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.28F, Side.SERVER, ModSoundEvents.AXE_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 56)
								.addProperty(AttackProperty.STAMINA_USAGE, 20)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),
				new AttackAnimation.Builder(Animations.HAMMER_TH_LIGHT_ATTACK.getId(), AttackType.TWO_HANDED_LIGHT,
						0.3F, 0.0F, 0.24F, 0.6F, 1.6F, "Tool_R", DarkSouls.rl("biped/combat/hammer_th_la"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.2F, Side.SERVER, ModSoundEvents.AXE_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 63)
								.addProperty(AttackProperty.STAMINA_USAGE, 20)
								.addProperty(AttackProperty.POISE_DAMAGE, 32),
				new AttackAnimation.Builder(Animations.HAMMER_TH_HEAVY_ATTACK.getId(), AttackType.TWO_HANDED_HEAVY,
						0.4F, 0.0F, 0.24F, 0.44F, 1.6F, "Tool_R", DarkSouls.rl("biped/combat/hammer_th_ha"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.24F, Side.SERVER, ModSoundEvents.AXE_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 69)
								.addProperty(AttackProperty.STAMINA_USAGE, 30)
								.addProperty(AttackProperty.POISE_DAMAGE, 47),
				new AttackAnimation.Builder(Animations.HAMMER_TH_DASH_ATTACK.getId(), AttackType.TWO_HANDED_DASH, 0.2F,
						0.0F, 0.24F, 0.48F, 1.6F, "Tool_R", DarkSouls.rl("biped/combat/hammer_th_la"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.2F, Side.SERVER, ModSoundEvents.AXE_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 63)
								.addProperty(AttackProperty.STAMINA_USAGE, 25)
								.addProperty(AttackProperty.POISE_DAMAGE, 32),

				// Fist
				new AttackAnimation.Builder(Animations.FIST_LIGHT_ATTACK.getIds()[0], AttackType.LIGHT, 0.2F, 0.0F,
						0.3F, 0.4F, 1.25F, "Tool_R", DarkSouls.rl("biped/combat/fist_light_attack_1"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.3F, Side.SERVER, ModSoundEvents.FIST_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 28)
								.addProperty(AttackProperty.STAMINA_USAGE, 18)
								.addProperty(AttackProperty.POISE_DAMAGE, 15),
				new AttackAnimation.Builder(Animations.FIST_LIGHT_ATTACK.getIds()[1], AttackType.LIGHT, 0.2F, 0.0F,
						0.15F, 0.3F, 1.25F, "Tool_R", DarkSouls.rl("biped/combat/fist_light_attack_2"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.15F, Side.SERVER, ModSoundEvents.FIST_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 28)
								.addProperty(AttackProperty.STAMINA_USAGE, 18)
								.addProperty(AttackProperty.POISE_DAMAGE, 15),

				new AttackAnimation.Builder(Animations.FIST_DASH_ATTACK.getId(), AttackType.DASH, 0.3F, 0.0F, 0.15F,
						0.3F, 1.0F, "Tool_R", DarkSouls.rl("biped/combat/fist_dash_attack"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.15F, Side.SERVER, ModSoundEvents.FIST_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 28)
								.addProperty(AttackProperty.STAMINA_USAGE, 20)
								.addProperty(AttackProperty.POISE_DAMAGE, 15),
				new AttackAnimation.Builder(Animations.FIST_HEAVY_ATTACK.getId(), AttackType.HEAVY, 0.5F, 0.0F, 0.35F,
						0.5F, 1.25F, "Tool_R", DarkSouls.rl("biped/combat/fist_heavy_attack"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.35F, Side.SERVER, ModSoundEvents.FIST_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 28)
								.addProperty(AttackProperty.STAMINA_USAGE, 25)
								.addProperty(AttackProperty.POISE_DAMAGE, 15),

				// Shield
				new AttackAnimation.Builder(Animations.SHIELD_LIGHT_ATTACK.getId(), AttackType.LIGHT, 0.2F, 0.0F, 0.4F,
						0.56F, 1.2F, "Tool_R", DarkSouls.rl("biped/combat/shield_la"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.4F, Side.SERVER, ModSoundEvents.FIST_SWING) })
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 45)
								.addProperty(AttackProperty.STAMINA_USAGE, 22)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),

				new AttackAnimation.Builder(Animations.SHIELD_HEAVY_ATTACK.getIds()[0], AttackType.HEAVY, 0.2F, 0.0F,
						0.44F, 0.56F, 1.4F, "Tool_R", DarkSouls.rl("biped/combat/shield_ha_1"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.44F, Side.SERVER, ModSoundEvents.FIST_SWING) })
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 51)
								.addProperty(AttackProperty.STAMINA_USAGE, 28)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(Animations.SHIELD_HEAVY_ATTACK.getIds()[1], AttackType.HEAVY, 0.2F, 0.0F,
						0.28F, 0.48F, 1.4F, "Tool_R", DarkSouls.rl("biped/combat/shield_ha_2"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.28F, Side.SERVER, ModSoundEvents.FIST_SWING) })
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 51)
								.addProperty(AttackProperty.STAMINA_USAGE, 28)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),

				new AttackAnimation.Builder(Animations.SHIELD_DASH_ATTACK.getId(), AttackType.DASH, 0.05F, 0.0F, 0.32F,
						0.52F, 1.4F, "Tool_R", DarkSouls.rl("biped/combat/shield_da"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.32F, Side.SERVER, ModSoundEvents.FIST_SWING) })
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 49)
								.addProperty(AttackProperty.STAMINA_USAGE, 25)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(Animations.SHIELD_TH_LIGHT_ATTACK.getId(), AttackType.TWO_HANDED_LIGHT,
						0.3F, 0.0F, 0.12F, 0.32F, 1.2F, "Tool_R", DarkSouls.rl("biped/combat/shield_th_la"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.12F, Side.SERVER, ModSoundEvents.FIST_SWING) })
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 48)
								.addProperty(AttackProperty.STAMINA_USAGE, 26)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),

				new AttackAnimation.Builder(Animations.SHIELD_TH_HEAVY_ATTACK.getIds()[0], AttackType.TWO_HANDED_HEAVY,
						0.2F, 0.0F, 0.28F, 0.48F, 1.2F, "Tool_R", DarkSouls.rl("biped/combat/shield_th_ha_1"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.28F, Side.SERVER, ModSoundEvents.FIST_SWING) })
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 54)
								.addProperty(AttackProperty.STAMINA_USAGE, 30)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(Animations.SHIELD_TH_HEAVY_ATTACK.getIds()[1], AttackType.TWO_HANDED_HEAVY,
						0.2F, 0.0F, 0.12F, 0.32F, 1.2F, "Tool_R", DarkSouls.rl("biped/combat/shield_th_ha_2"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.12F, Side.SERVER, ModSoundEvents.FIST_SWING) })
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 54)
								.addProperty(AttackProperty.STAMINA_USAGE, 30)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),

				new AttackAnimation.Builder(Animations.SHIELD_TH_DASH_ATTACK.getId(), AttackType.TWO_HANDED_DASH, 0.2F,
						0.0F, 0.12F, 0.32F, 1.4F, "Tool_R", DarkSouls.rl("biped/combat/shield_th_da"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.12F, Side.SERVER, ModSoundEvents.FIST_SWING) })
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 52)
								.addProperty(AttackProperty.STAMINA_USAGE, 25)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),

				// Greatshield
				new AttackAnimation.Builder(Animations.GREATSHIELD_LIGHT_ATTACK.getId(), AttackType.LIGHT, 0.2F, 0.0F,
						0.36F, 0.75F, 1.2F, "Tool_R", DarkSouls.rl("biped/combat/greatshield_la"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.36F, Side.SERVER, ModSoundEvents.FIST_SWING) })
								.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 70)
								.addProperty(AttackProperty.STAMINA_USAGE, 35)
								.addProperty(AttackProperty.POISE_DAMAGE, 30),
				new AttackAnimation.Builder(Animations.GREATSHIELD_HEAVY_ATTACK.getId(), AttackType.HEAVY, 0.2F, 0.0F,
						0.42F, 0.7F, 1.5F, "Tool_R", DarkSouls.rl("biped/combat/greatshield_ha"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_SWORD.appendTo(0.7F, new AnimEvent[]
										{ new PlaySoundEvent(0.42F, Side.SERVER, ModSoundEvents.FIST_SWING) }))
								.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 70)
								.addProperty(AttackProperty.STAMINA_USAGE, 40)
								.addProperty(AttackProperty.POISE_DAMAGE, 30),
				new AttackAnimation.Builder(Animations.GREATSHIELD_DASH_ATTACK.getId(), AttackType.DASH, 0.05F, 0.0F,
						0.48F, 0.85F, 1.32F, "Tool_R", DarkSouls.rl("biped/combat/greatshield_da"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.48F, Side.SERVER, ModSoundEvents.FIST_SWING) })
								.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 70)
								.addProperty(AttackProperty.STAMINA_USAGE, 35)
								.addProperty(AttackProperty.POISE_DAMAGE, 30),
				new AttackAnimation.Builder(Animations.GREATSHIELD_TH_LIGHT_ATTACK.getId(), AttackType.TWO_HANDED_LIGHT,
						0.2F, 0.0F, 0.36F, 0.75F, 1.2F, "Tool_R", DarkSouls.rl("biped/combat/greatshield_th_la"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.36F, Side.SERVER, ModSoundEvents.FIST_SWING) })
								.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 70)
								.addProperty(AttackProperty.STAMINA_USAGE, 30)
								.addProperty(AttackProperty.POISE_DAMAGE, 30),
				new AttackAnimation.Builder(Animations.GREATSHIELD_TH_HEAVY_ATTACK.getId(), AttackType.TWO_HANDED_HEAVY,
						0.2F, 0.0F, 0.42F, 0.7F, 1.5F, "Tool_R", DarkSouls.rl("biped/combat/greatshield_th_ha"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_SWORD.appendTo(0.7F, new AnimEvent[]
										{ new PlaySoundEvent(0.42F, Side.SERVER, ModSoundEvents.FIST_SWING) }))
								.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 70)
								.addProperty(AttackProperty.STAMINA_USAGE, 40)
								.addProperty(AttackProperty.POISE_DAMAGE, 30),
				new AttackAnimation.Builder(Animations.GREATSHIELD_TH_DASH_ATTACK.getId(), AttackType.TWO_HANDED_DASH,
						0.05F, 0.0F, 0.48F, 0.85F, 1.32F, "Tool_R", DarkSouls.rl("biped/combat/greatshield_th_da"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.48F, Side.SERVER, ModSoundEvents.FIST_SWING) })
								.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 70)
								.addProperty(AttackProperty.STAMINA_USAGE, 35)
								.addProperty(AttackProperty.POISE_DAMAGE, 30),
				new AttackAnimation.Builder(Animations.GREATSHIELD_BASH.getId(), AttackType.LIGHT, 0.2F, 0.0F, 0.36F,
						0.64F, 1.2F, "Tool_L", DarkSouls.rl("biped/combat/greatshield_bash"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.36F, Side.SERVER, ModSoundEvents.FIST_SWING) })
								.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 70)
								.addProperty(AttackProperty.STAMINA_USAGE, 35)
								.addProperty(AttackProperty.POISE_DAMAGE, 30),

				// Straight Sword
				new AttackAnimation.Builder(Animations.STRAIGHT_SWORD_LIGHT_ATTACK.getIds()[0], AttackType.LIGHT, 0.2F,
						0.0F, 0.24F, 0.4F, 0.88F, "Tool_R", DarkSouls.rl("biped/combat/straight_sword_light_attack_1"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.24F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.STAMINA_USAGE, 15)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(Animations.STRAIGHT_SWORD_LIGHT_ATTACK.getIds()[1], AttackType.LIGHT, 0.2F,
						0.0F, 0.08F, 0.3F, 0.8F, "Tool_R", DarkSouls.rl("biped/combat/straight_sword_light_attack_2"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.08F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.STAMINA_USAGE, 15)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),

				new AttackAnimation.Builder(Animations.STRAIGHT_SWORD_HEAVY_ATTACK.getId(), AttackType.HEAVY, 0.2F,
						0.0F, 0.36F, 0.6F, 1.0F, "Tool_R", DarkSouls.rl("biped/combat/straight_sword_heavy_attack"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.36F, Side.SERVER, ModSoundEvents.SWORD_THRUST) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.SLASH)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.STAMINA_USAGE, 25)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(Animations.STRAIGHT_SWORD_DASH_ATTACK.getId(), AttackType.DASH, 0.2F, 0.0F,
						0.16F, 0.36F, 0.8F, "Tool_R", DarkSouls.rl("biped/combat/straight_sword_dash_attack"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.16F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.STAMINA_USAGE, 20)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),

				new AttackAnimation.Builder(Animations.STRAIGHT_SWORD_TH_LIGHT_ATTACK.getIds()[0],
						AttackType.TWO_HANDED_LIGHT, 0.1F, 0.0F, 0.36F, 0.6F, 1.52F, "Tool_R",
						DarkSouls.rl("biped/combat/straight_sword_th_la_1"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.36F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.STAMINA_USAGE, 15)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(Animations.STRAIGHT_SWORD_TH_LIGHT_ATTACK.getIds()[1],
						AttackType.TWO_HANDED_LIGHT, 0.1F, 0.0F, 0.42F, 0.48F, 1.6F, "Tool_R",
						DarkSouls.rl("biped/combat/straight_sword_th_la_2"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.36F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.STAMINA_USAGE, 15)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),

				new AttackAnimation.Builder(Animations.STRAIGHT_SWORD_TH_HEAVY_ATTACK.getId(),
						AttackType.TWO_HANDED_HEAVY, 0.2F, 0.0F, 0.36F, 0.56F, 1.2F, "Tool_R",
						DarkSouls.rl("biped/combat/straight_sword_th_ha_1"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.36F, Side.SERVER, ModSoundEvents.SWORD_THRUST) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.STAMINA_USAGE, 25)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(Animations.STRAIGHT_SWORD_TH_DASH_ATTACK.getId(),
						AttackType.TWO_HANDED_DASH, 0.2F, 0.0F, 0.48F, 0.72F, 1.6F, "Tool_R",
						DarkSouls.rl("biped/combat/straight_sword_th_da"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.48F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.STAMINA_USAGE, 20)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),

				// Hollow
				new StaticAnimation.Builder(Animations.HOLLOW_IDLE.getId(), 0.2F, true, DarkSouls.rl("hollow/idle"),
						(models) -> models.ENTITY_BIPED),
				new MovementAnimation.Builder(Animations.HOLLOW_WALK.getId(), 0.2F, true, DarkSouls.rl("hollow/move"),
						(models) -> models.ENTITY_BIPED),
				new MovementAnimation.Builder(Animations.HOLLOW_RUN.getId(), 0.2F, true, DarkSouls.rl("hollow/run"),
						(models) -> models.ENTITY_BIPED),
				new ActionAnimation.Builder(Animations.HOLLOW_DEFLECTED.getId(), 0.2F, DarkSouls.rl("hollow/deflected"),
						(models) -> models.ENTITY_BIPED).addProperty(ActionAnimationProperty.IS_HIT, true),
				new StaticAnimation.Builder(Animations.HOLLOW_BREAKDOWN.getId(), 0.2F, true,
						DarkSouls.rl("hollow/breakdown"), (models) -> models.ENTITY_BIPED),

				new AttackAnimation.Builder(Animations.HOLLOW_LIGHT_ATTACKS.getIds()[0], AttackType.LIGHT, 0.2F, 0.0F,
						0.56F, 1.05F, 2.5F, "Tool_R", DarkSouls.rl("hollow/swing_1"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.56F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(Animations.HOLLOW_LIGHT_ATTACKS.getIds()[1], AttackType.LIGHT, 0.2F, 0.0F,
						0.48F, 1.0F, 2.0F, "Tool_R", DarkSouls.rl("hollow/swing_2"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.48F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(Animations.HOLLOW_LIGHT_ATTACKS.getIds()[2], AttackType.LIGHT, 0.2F, 0.0F,
						0.16F, 0.4F, 2.0F, "Tool_R", DarkSouls.rl("hollow/swing_3"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.16F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),

				new AttackAnimation.Builder(Animations.HOLLOW_BARRAGE.getId(), AttackType.LIGHT, 0.2F,
						DarkSouls.rl("hollow/fury_attack"), (models) -> models.ENTITY_BIPED,
						new PhaseBuilder(Animations.HOLLOW_BARRAGE.getId(), 0.0F, 1.48F, 1.72F, 1.72F, "Tool_R", Colliders.BROKEN_SWORD.getId()),
						new PhaseBuilder(Animations.HOLLOW_BARRAGE.getId(), 1.72F, 1.8F, 1.92F, 1.92F, "Tool_R", Colliders.BROKEN_SWORD.getId()),
						new PhaseBuilder(Animations.HOLLOW_BARRAGE.getId(), 1.92F, 2.12F, 2.24F, 2.24F, "Tool_R", Colliders.BROKEN_SWORD.getId()),
						new PhaseBuilder(Animations.HOLLOW_BARRAGE.getId(), 2.24F, 2.4F, 2.56F, 2.56F, "Tool_R", Colliders.BROKEN_SWORD.getId()),
						new PhaseBuilder(Animations.HOLLOW_BARRAGE.getId(), 2.56F, 2.76F, 2.88F, 2.88F, "Tool_R", Colliders.BROKEN_SWORD.getId()),
						new PhaseBuilder(Animations.HOLLOW_BARRAGE.getId(), 2.88F, 3.08F, 3.2F, 4.2F, "Tool_R", Colliders.BROKEN_SWORD.getId()))
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.04F, Side.SERVER, ModSoundEvents.HOLLOW_PREPARE),
										new PlaySoundEvent(1.48F, Side.SERVER, ModSoundEvents.SWORD_SWING),
										new PlaySoundEvent(2.12F, Side.SERVER, ModSoundEvents.SWORD_SWING),
										new PlaySoundEvent(2.4F, Side.SERVER, ModSoundEvents.SWORD_SWING),
										new PlaySoundEvent(2.76F, Side.SERVER, ModSoundEvents.SWORD_SWING),
										new PlaySoundEvent(3.08F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(Animations.HOLLOW_OVERHEAD_SWING.getId(), AttackType.HEAVY, 0.2F, 0.0F,
						0.4F, 0.6F, 1.2F, "Tool_R", DarkSouls.rl("hollow/overhead_swing"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.4F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(Animations.HOLLOW_JUMP_ATTACK.getId(), AttackType.DASH, 0.05F, 0.0F, 0.52F,
						0.72F, 1.6F, "Tool_R", DarkSouls.rl("hollow/jump_attack"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.52F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),

				// Hollow Lordran Warrior
				new MovementAnimation.Builder(Animations.HOLLOW_LORDRAN_WARRIOR_WALK.getId(), 0.2F, true,
						DarkSouls.rl("hollow_lordran_warrior/move"), (models) -> models.ENTITY_BIPED),
				new MovementAnimation.Builder(Animations.HOLLOW_LORDRAN_WARRIOR_RUN.getId(), 0.2F, true,
						DarkSouls.rl("hollow_lordran_warrior/run"), (models) -> models.ENTITY_BIPED),

				new AttackAnimation.Builder(Animations.HOLLOW_LORDRAN_WARRIOR_TH_LA.getIds()[0], AttackType.LIGHT, 0.2F,
						0.0F, 0.68F, 1.08F, 1.6F, "Tool_R", DarkSouls.rl("hollow_lordran_warrior/sword_th_la_1"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.68F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.SLASH)
								.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(Animations.HOLLOW_LORDRAN_WARRIOR_TH_LA.getIds()[1], AttackType.LIGHT, 0.2F,
						0.0F, 0.68F, 1.08F, 1.6F, "Tool_R", DarkSouls.rl("hollow_lordran_warrior/sword_th_la_2"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.68F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.SLASH)
								.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),

				new AttackAnimation.Builder(Animations.HOLLOW_LORDRAN_WARRIOR_DASH_ATTACK.getId(), AttackType.DASH,
						0.2F, 0.0F, 0.44F, 0.88F, 1.8F, "Tool_R", DarkSouls.rl("hollow_lordran_warrior/dash_attack"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.44F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.SLASH)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),

				new AttackAnimation.Builder(Animations.HOLLOW_LORDRAN_WARRIOR_AXE_LA.getIds()[0], AttackType.LIGHT,
						0.2F, 0.0F, 0.6F, 1.0F, 2.4F, "Tool_R", DarkSouls.rl("hollow_lordran_warrior/axe_la_1"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.6F, Side.SERVER, ModSoundEvents.AXE_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),
				new AttackAnimation.Builder(Animations.HOLLOW_LORDRAN_WARRIOR_AXE_LA.getIds()[1], AttackType.LIGHT,
						0.2F, 0.0F, 1.12F, 1.36F, 2.8F, "Tool_R", DarkSouls.rl("hollow_lordran_warrior/axe_la_2"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(1.12F, Side.SERVER, ModSoundEvents.AXE_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),

				new AttackAnimation.Builder(Animations.HOLLOW_LORDRAN_WARRIOR_AXE_TH_LA.getIds()[0], AttackType.LIGHT,
						0.2F, 0.0F, 0.56F, 1.0F, 2.8F, "Tool_R", DarkSouls.rl("hollow_lordran_warrior/axe_th_la_1"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.56F, Side.SERVER, ModSoundEvents.AXE_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.SLASH)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),
				new AttackAnimation.Builder(Animations.HOLLOW_LORDRAN_WARRIOR_AXE_TH_LA.getIds()[1], AttackType.LIGHT,
						0.2F, 0.0F, 0.68F, 1.0F, 2.0F, "Tool_R", DarkSouls.rl("hollow_lordran_warrior/axe_th_la_2"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.68F, Side.SERVER, ModSoundEvents.AXE_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.SLASH)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),

				// Hollow Lordran Soldier
				new MovementAnimation.Builder(Animations.HOLLOW_LORDRAN_SOLDIER_WALK.getId(), 0.2F, true,
						DarkSouls.rl("hollow_lordran_soldier/walking"), (models) -> models.ENTITY_BIPED),
				new MovementAnimation.Builder(Animations.HOLLOW_LORDRAN_SOLDIER_RUN.getId(), 0.2F, true,
						DarkSouls.rl("hollow_lordran_soldier/run"), (models) -> models.ENTITY_BIPED),
				new AdaptableAnimation.Builder(Animations.HOLLOW_LORDRAN_SOLDIER_BLOCK.getId(), 0.2F, true,
						(models) -> models.ENTITY_BIPED)
								.addEntry(LivingMotion.BLOCKING, DarkSouls.rl("hollow_lordran_soldier/block"),
										DarkSouls.rl("hollow_lordran_soldier/block"), false)
								.addEntry(LivingMotion.WALKING, DarkSouls.rl("hollow_lordran_soldier/block_walking"),
										DarkSouls.rl("hollow_lordran_soldier/block_walking"), true)
								.addEntry(LivingMotion.RUNNING, DarkSouls.rl("hollow_lordran_soldier/block_run"),
										DarkSouls.rl("hollow_lordran_soldier/block_run"), true),

				new AttackAnimation.Builder(Animations.HOLLOW_LORDRAN_SOLDIER_SWORD_LA.getIds()[0], AttackType.LIGHT,
						0.2F, 0.0F, 0.44F, 0.76F, 1.6F, "Tool_R", DarkSouls.rl("hollow_lordran_soldier/sword_la_1"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.44F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),
				new AttackAnimation.Builder(Animations.HOLLOW_LORDRAN_SOLDIER_SWORD_LA.getIds()[1], AttackType.LIGHT,
						0.2F, 0.0F, 0.16F, 0.56F, 1.0F, "Tool_R", DarkSouls.rl("hollow_lordran_soldier/sword_la_2"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.16F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),
				new AttackAnimation.Builder(Animations.HOLLOW_LORDRAN_SOLDIER_SWORD_LA.getIds()[2], AttackType.LIGHT,
						0.2F, 0.0F, 0.44F, 0.6F, 1.6F, "Tool_R", DarkSouls.rl("hollow_lordran_soldier/sword_la_3"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.44F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),

				new AttackAnimation.Builder(Animations.HOLLOW_LORDRAN_SOLDIER_SWORD_DA.getId(), AttackType.DASH, 0.2F,
						0.0F, 0.35F, 0.5F, 3.0F, "Tool_R", DarkSouls.rl("hollow_lordran_soldier/sword_da"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.35F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),

				new AttackAnimation.Builder(Animations.HOLLOW_LORDRAN_SOLDIER_SWORD_HEAVY_THRUST.getId(),
						AttackType.HEAVY, 0.2F, 0.0F, 1.0F, 1.16F, 2.0F, "Tool_R",
						DarkSouls.rl("hollow_lordran_soldier/sword_heavy_thrust"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(1.0F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),

				new AttackAnimation.Builder(Animations.HOLLOW_LORDRAN_SOLDIER_SWORD_THRUST_COMBO.getId(),
						AttackType.LIGHT, 0.2F, DarkSouls.rl("hollow_lordran_soldier/sword_thrust_combo"),
						(models) -> models.ENTITY_BIPED,
						new PhaseBuilder(Animations.HOLLOW_LORDRAN_SOLDIER_SWORD_THRUST_COMBO.getId(), 0.0F, 0.52F, 0.72F, 0.72F, "Tool_R"),
						new PhaseBuilder(Animations.HOLLOW_LORDRAN_SOLDIER_SWORD_THRUST_COMBO.getId(), 0.72F, 1.2F, 1.4F, 2.0F, "Tool_R"))
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.52F, Side.SERVER, ModSoundEvents.SWORD_SWING),
										new PlaySoundEvent(1.2F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),

				new AttackAnimation.Builder(Animations.HOLLOW_LORDRAN_SOLDIER_SPEAR_SWINGS.getIds()[0],
						AttackType.LIGHT, 0.2F, 0.0F, 0.48F, 0.76F, 1.6F, "Tool_R",
						DarkSouls.rl("hollow_lordran_soldier/spear_swing_1"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.48F, Side.SERVER, ModSoundEvents.SPEAR_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 40)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(Animations.HOLLOW_LORDRAN_SOLDIER_SPEAR_SWINGS.getIds()[1],
						AttackType.LIGHT, 0.2F, 0.0F, 0.16F, 0.56F, 1.0F, "Tool_R",
						DarkSouls.rl("hollow_lordran_soldier/spear_swing_2"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.16F, Side.SERVER, ModSoundEvents.SPEAR_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 40)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(Animations.HOLLOW_LORDRAN_SOLDIER_SPEAR_SWINGS.getIds()[2],
						AttackType.LIGHT, 0.2F, 0.0F, 0.6F, 0.72F, 1.6F, "Tool_R",
						DarkSouls.rl("hollow_lordran_soldier/spear_swing_3"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.6F, Side.SERVER, ModSoundEvents.SPEAR_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 40)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(Animations.HOLLOW_LORDRAN_SOLDIER_SPEAR_SWINGS.getIds()[3],
						AttackType.LIGHT, 0.2F, 0.0F, 0.44F, 0.6F, 1.6F, "Tool_R",
						DarkSouls.rl("hollow_lordran_soldier/spear_swing_4"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.44F, Side.SERVER, ModSoundEvents.SPEAR_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 40)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),

				new AttackAnimation.Builder(Animations.HOLLOW_LORDRAN_SOLDIER_SPEAR_THRUSTS.getIds()[0],
						AttackType.LIGHT, 0.2F, 0.0F, 0.64F, 0.8F, 1.6F, "Tool_R",
						DarkSouls.rl("hollow_lordran_soldier/spear_thrust_1"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.64F, Side.SERVER, ModSoundEvents.SPEAR_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 40)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(Animations.HOLLOW_LORDRAN_SOLDIER_SPEAR_THRUSTS.getIds()[1],
						AttackType.LIGHT, 0.2F, 0.0F, 0.72F, 0.88F, 1.6F, "Tool_R",
						DarkSouls.rl("hollow_lordran_soldier/spear_thrust_2"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.72F, Side.SERVER, ModSoundEvents.SPEAR_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 40)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(Animations.HOLLOW_LORDRAN_SOLDIER_SPEAR_THRUSTS.getIds()[2],
						AttackType.LIGHT, 0.2F, 0.0F, 0.88F, 1.04F, 1.6F, "Tool_R",
						DarkSouls.rl("hollow_lordran_soldier/spear_thrust_3"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.88F, Side.SERVER, ModSoundEvents.SPEAR_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 40)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),

				new AttackAnimation.Builder(Animations.HOLLOW_LORDRAN_SOLDIER_SHIELD_BASH.getId(), AttackType.HEAVY,
						0.2F, 0.0F, 0.6F, 0.8F, 1.6F, Colliders.SHIELD.getId(), "Tool_L",
						DarkSouls.rl("hollow_lordran_soldier/shield_bash"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.6F, Side.SERVER, ModSoundEvents.FIST_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 40)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),

				// Falconer
				new StaticAnimation.Builder(Animations.FALCONER_IDLE.getId(), 1.0F, true, DarkSouls.rl("falconer/idle"),
						(models) -> models.ENTITY_BIPED),
				new MovementAnimation.Builder(Animations.FALCONER_WALK.getId(), 0.2F, true,
						DarkSouls.rl("falconer/walking"), (models) -> models.ENTITY_BIPED),
				new MovementAnimation.Builder(Animations.FALCONER_RUN.getId(), 0.2F, true, DarkSouls.rl("falconer/run"),
						(models) -> models.ENTITY_BIPED),

				new AttackAnimation.Builder(Animations.FALCONER_LIGHT_ATTACKS.getIds()[0], AttackType.LIGHT, 0.2F, 0.0F,
						0.56F, 0.68F, 1.88F, "Tool_R", DarkSouls.rl("falconer/swing_1"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.56F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(Animations.FALCONER_LIGHT_ATTACKS.getIds()[1], AttackType.LIGHT, 0.1F, 0.0F,
						0.72F, 1.04F, 1.88F, "Tool_R", DarkSouls.rl("falconer/swing_2"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.72F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(Animations.FALCONER_LIGHT_ATTACKS.getIds()[2], AttackType.LIGHT, 0.1F, 0.0F,
						0.52F, 0.68F, 1.88F, "Tool_R", DarkSouls.rl("falconer/swing_3"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.52F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),

				// Balder Knight
				new StaticAnimation.Builder(Animations.BALDER_KNIGHT_IDLE.getId(), 0.3F, true,
						DarkSouls.rl("balder_knight/idle"), (models) -> models.ENTITY_BIPED),
				new StaticAnimation.Builder(Animations.BALDER_KNIGHT_WALK.getId(), 0.1F, true,
						DarkSouls.rl("balder_knight/walking"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.24F, Side.SERVER, ModSoundEvents.BALDER_KNIGHT_FOOT),
										new PlaySoundEvent(0.48F, Side.SERVER, ModSoundEvents.BALDER_KNIGHT_FOOT) }),
				new StaticAnimation.Builder(Animations.BALDER_KNIGHT_RUN.getId(), 0.1F, true,
						DarkSouls.rl("balder_knight/run"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.12F, Side.SERVER, ModSoundEvents.BALDER_KNIGHT_FOOT),
										new PlaySoundEvent(0.5F, Side.SERVER, ModSoundEvents.BALDER_KNIGHT_FOOT) }),
				new AdaptableAnimation.Builder(Animations.BALDER_KNIGHT_BLOCK.getId(), 0.2F, true,
						(models) -> models.ENTITY_BIPED)
								.addEntry(LivingMotion.BLOCKING, DarkSouls.rl("balder_knight/block"), false)
								.addEntry(LivingMotion.WALKING, DarkSouls.rl("balder_knight/block_walk"), true)
								.addEntry(LivingMotion.RUNNING, DarkSouls.rl("balder_knight/block_run"), true),
				new AdaptableAnimation.Builder(Animations.BALDER_KNIGHT_RAPIER_BLOCK.getId(), 0.2F, true,
						(models) -> models.ENTITY_BIPED)
								.addEntry(LivingMotion.IDLE, DarkSouls.rl("balder_knight/rapier_block"), false)
								.addEntry(LivingMotion.WALKING, DarkSouls.rl("balder_knight/rapier_block_walk"), true)
								.addEntry(LivingMotion.RUNNING, DarkSouls.rl("balder_knight/rapier_block_run"), true),

				new ParryAnimation.Builder(Animations.BALDER_KNIGHT_RAPIER_PARRY.getId(), 0.05F, 0.0F, 1.2F, "Tool_L",
						DarkSouls.rl("balder_knight/rapier_parry"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.32F, Side.SERVER, ModSoundEvents.SWORD_SWING) }),

				new AttackAnimation.Builder(Animations.BALDER_KNIGHT_SIDE_SWORD_LA.getIds()[0], AttackType.LIGHT, 0.2F,
						0.0F, 0.4F, 0.56F, 1.6F, "Tool_R", DarkSouls.rl("balder_knight/side_sword_la_1"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.4F, Side.CLIENT, ModSoundEvents.SWORD_SWING),
						new PlaySoundEvent(0.44F, Side.CLIENT, ModSoundEvents.BALDER_KNIGHT_FOOT),
						new PlaySoundEvent(1.6F, Side.CLIENT, ModSoundEvents.BALDER_KNIGHT_FOOT) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(Animations.BALDER_KNIGHT_SIDE_SWORD_LA.getIds()[1], AttackType.LIGHT, 0.2F,
						0.0F, 0.16F, 0.4F, 1.6F, "Tool_R", DarkSouls.rl("balder_knight/side_sword_la_2"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.16F, Side.CLIENT, ModSoundEvents.SWORD_SWING),
						new PlaySoundEvent(0.28F, Side.CLIENT, ModSoundEvents.BALDER_KNIGHT_FOOT),
						new PlaySoundEvent(1.6F, Side.CLIENT, ModSoundEvents.BALDER_KNIGHT_FOOT) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(Animations.BALDER_KNIGHT_SIDE_SWORD_LA.getIds()[2], AttackType.LIGHT, 0.2F,
						0.0F, 0.24F, 0.44F, 1.6F, "Tool_R", DarkSouls.rl("balder_knight/side_sword_la_3"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.24F, Side.CLIENT, ModSoundEvents.SWORD_SWING),
						new PlaySoundEvent(0.32F, Side.CLIENT, ModSoundEvents.BALDER_KNIGHT_FOOT),
						new PlaySoundEvent(1.6F, Side.CLIENT, ModSoundEvents.BALDER_KNIGHT_FOOT) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),

				new AttackAnimation.Builder(Animations.BALDER_KNIGHT_SIDE_SWORD_HA.getId(), AttackType.HEAVY, 0.2F,
						0.0F, 0.68F, 0.76F, 2.0F, "Tool_R", DarkSouls.rl("balder_knight/side_sword_ha"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.64F, Side.CLIENT, ModSoundEvents.SWORD_SWING),
						new PlaySoundEvent(0.76F, Side.CLIENT, ModSoundEvents.BALDER_KNIGHT_FOOT),
						new PlaySoundEvent(2.0F, Side.CLIENT, ModSoundEvents.BALDER_KNIGHT_FOOT) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 45)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),
				new AttackAnimation.Builder(Animations.BALDER_KNIGHT_SIDE_SWORD_DA.getId(), AttackType.DASH, 0.2F, 0.0F,
						0.64F, 0.8F, 1.68F, "Tool_R", DarkSouls.rl("balder_knight/side_sword_da"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.12F, Side.CLIENT, ModSoundEvents.BALDER_KNIGHT_FOOT),
						new PlaySoundEvent(0.24F, Side.CLIENT, ModSoundEvents.BALDER_KNIGHT_FOOT),
						new PlaySoundEvent(0.6F, Side.CLIENT, ModSoundEvents.BALDER_KNIGHT_FOOT),
						new PlaySoundEvent(0.64F, Side.CLIENT, ModSoundEvents.SWORD_THRUST),
						new PlaySoundEvent(0.76F, Side.CLIENT, ModSoundEvents.BALDER_KNIGHT_FOOT),
						new PlaySoundEvent(1.6F, Side.CLIENT, ModSoundEvents.BALDER_KNIGHT_FOOT) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 45)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),
				new AttackAnimation.Builder(Animations.BALDER_KNIGHT_SIDE_SWORD_FAST_LA.getId(), AttackType.LIGHT, 0.2F,
						DarkSouls.rl("balder_knight/rapier_la"), (models) -> models.ENTITY_BIPED,
						new PhaseBuilder(Animations.BALDER_KNIGHT_SIDE_SWORD_FAST_LA.getId(), 0.0F, 0.1F, 0.4F, 0.4F, "Tool_R"),
						new PhaseBuilder(Animations.BALDER_KNIGHT_SIDE_SWORD_FAST_LA.getId(), 0.4F, 0.72F, 0.8F, 2.0F, "Tool_R"))
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.1F, Side.CLIENT, ModSoundEvents.SWORD_SWING),
										new PlaySoundEvent(0.25F, Side.CLIENT, ModSoundEvents.BALDER_KNIGHT_FOOT),
										new PlaySoundEvent(0.64F, Side.CLIENT, ModSoundEvents.BALDER_KNIGHT_FOOT),
										new PlaySoundEvent(0.72F, Side.CLIENT, ModSoundEvents.SWORD_SWING),
										new PlaySoundEvent(2.0F, Side.CLIENT, ModSoundEvents.BALDER_KNIGHT_FOOT) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STUN_TYPE, StunType.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 37)
								.addProperty(AttackProperty.POISE_DAMAGE, 15),
				new AttackAnimation.Builder(Animations.BALDER_KNIGHT_SHIELD_HA.getId(), AttackType.HEAVY, 0.2F, 0.0F,
						0.08F, 0.24F, 1.2F, Colliders.SHIELD.getId(), "Tool_L", DarkSouls.rl("balder_knight/shield_ha"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(AnimEvent.ON_BEGIN, Side.CLIENT, ModSoundEvents.BALDER_KNIGHT_FOOT),
						new PlaySoundEvent(0.08F, Side.CLIENT, ModSoundEvents.FIST_SWING),
						new PlaySoundEvent(1.1F, Side.CLIENT, ModSoundEvents.BALDER_KNIGHT_FOOT) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 37)
								.addProperty(AttackProperty.POISE_DAMAGE, 15),

				new AttackAnimation.Builder(Animations.BALDER_KNIGHT_RAPIER_LA.getIds()[0], AttackType.LIGHT, 0.2F,
						0.0F, 0.4F, 0.6F, 1.2F, "Tool_R", DarkSouls.rl("balder_knight/rapier_la_1"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.4F, Side.CLIENT, ModSoundEvents.SWORD_SWING),
						new PlaySoundEvent(0.52F, Side.CLIENT, ModSoundEvents.BALDER_KNIGHT_FOOT),
						new PlaySoundEvent(1.16F, Side.CLIENT, ModSoundEvents.BALDER_KNIGHT_FOOT) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 37)
								.addProperty(AttackProperty.POISE_DAMAGE, 15),
				new AttackAnimation.Builder(Animations.BALDER_KNIGHT_RAPIER_LA.getIds()[1], AttackType.LIGHT, 0.2F,
						0.0F, 0.24F, 0.44F, 1.2F, "Tool_R", DarkSouls.rl("balder_knight/rapier_la_2"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.24F, Side.CLIENT, ModSoundEvents.SWORD_SWING),
						new PlaySoundEvent(0.32F, Side.CLIENT, ModSoundEvents.BALDER_KNIGHT_FOOT),
						new PlaySoundEvent(1.16F, Side.CLIENT, ModSoundEvents.BALDER_KNIGHT_FOOT) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 37)
								.addProperty(AttackProperty.POISE_DAMAGE, 15),
				new AttackAnimation.Builder(Animations.BALDER_KNIGHT_RAPIER_LA.getIds()[2], AttackType.LIGHT, 0.1F,
						0.0F, 0.24F, 0.44F, 1.2F, "Tool_R", DarkSouls.rl("balder_knight/rapier_la_2"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.24F, Side.CLIENT, ModSoundEvents.SWORD_SWING),
						new PlaySoundEvent(0.32F, Side.CLIENT, ModSoundEvents.BALDER_KNIGHT_FOOT),
						new PlaySoundEvent(1.16F, Side.CLIENT, ModSoundEvents.BALDER_KNIGHT_FOOT) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 37)
								.addProperty(AttackProperty.POISE_DAMAGE, 15),

				new AttackAnimation.Builder(Animations.BALDER_KNIGHT_RAPIER_HA.getId(), AttackType.HEAVY, 0.05F, 0.0F,
						0.56F, 0.76F, 1.2F, "Tool_R", DarkSouls.rl("balder_knight/rapier_ha"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.56F, Side.CLIENT, ModSoundEvents.SWORD_THRUST),
						new PlaySoundEvent(0.56F, Side.CLIENT, ModSoundEvents.BALDER_KNIGHT_FOOT),
						new PlaySoundEvent(1.16F, Side.CLIENT, ModSoundEvents.BALDER_KNIGHT_FOOT), })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 37)
								.addProperty(AttackProperty.POISE_DAMAGE, 15),
				new AttackAnimation.Builder(Animations.BALDER_KNIGHT_RAPIER_DA.getId(), AttackType.DASH, 0.05F, 0.0F,
						0.64F, 0.84F, 1.6F, "Tool_R", DarkSouls.rl("balder_knight/rapier_da"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.28F, Side.CLIENT, ModSoundEvents.BALDER_KNIGHT_FOOT),
						new PlaySoundEvent(0.64F, Side.CLIENT, ModSoundEvents.SWORD_SWING),
						new PlaySoundEvent(0.76F, Side.CLIENT, ModSoundEvents.BALDER_KNIGHT_FOOT),
						new PlaySoundEvent(1.56F, Side.CLIENT, ModSoundEvents.BALDER_KNIGHT_FOOT) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 37)
								.addProperty(AttackProperty.POISE_DAMAGE, 15),

				// Berenike Knight
				new StaticAnimation.Builder(Animations.BERENIKE_KNIGHT_IDLE.getId(), 0.3F, true,
						DarkSouls.rl("berenike_knight/idle"), (models) -> models.ENTITY_BIPED),

				new AttackAnimation.Builder(Animations.BERENIKE_KNIGHT_SWORD_LA.getIds()[0], AttackType.LIGHT, 0.2F,
						0.0F, 1.04F, 1.5F, 2.4F, "Tool_R", DarkSouls.rl("berenike_knight/sword_la_1"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(1.04F, Side.CLIENT, ModSoundEvents.SWORD_SWING),
						new PlaySoundEvent(1.08F, Side.CLIENT, ModSoundEvents.BERENIKE_KNIGHT_FOOT),
						new PlaySoundEvent(2.36F, Side.CLIENT, ModSoundEvents.BERENIKE_KNIGHT_FOOT) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 75)
								.addProperty(AttackProperty.POISE_DAMAGE, 32),
				new AttackAnimation.Builder(Animations.BERENIKE_KNIGHT_SWORD_LA.getIds()[1], AttackType.LIGHT, 0.2F,
						0.0F, 0.32F, 0.7F, 1.6F, "Tool_R", DarkSouls.rl("berenike_knight/sword_la_2"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.2F, Side.CLIENT, ModSoundEvents.BERENIKE_KNIGHT_FOOT),
						new PlaySoundEvent(0.32F, Side.CLIENT, ModSoundEvents.SWORD_SWING),
						new PlaySoundEvent(1.56F, Side.CLIENT, ModSoundEvents.BERENIKE_KNIGHT_FOOT) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 75)
								.addProperty(AttackProperty.POISE_DAMAGE, 32),

				new AttackAnimation.Builder(Animations.BERENIKE_KNIGHT_SWORD_HA.getIds()[0], AttackType.HEAVY, 0.2F,
						0.0F, 1.48F, 1.8F, 2.8F, "Tool_R", DarkSouls.rl("berenike_knight/sword_ha_1"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_MONSTER_HAMMER_SWING.appendTo(1.8F, new AnimEvent[]
										{ new PlaySoundEvent(1.4F, Side.CLIENT, ModSoundEvents.SWORD_THRUST),
												new PlaySoundEvent(1.56F, Side.CLIENT,
														ModSoundEvents.BERENIKE_KNIGHT_FOOT),
												new PlaySoundEvent(2.64F, Side.CLIENT,
														ModSoundEvents.BERENIKE_KNIGHT_FOOT) }))
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 85)
								.addProperty(AttackProperty.POISE_DAMAGE, 38),
				new AttackAnimation.Builder(Animations.BERENIKE_KNIGHT_SWORD_HA.getIds()[1], AttackType.HEAVY, 0.2F,
						0.0F, 1.24F, 1.7F, 2.4F, "Tool_R", DarkSouls.rl("berenike_knight/sword_ha_2"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_MONSTER_HAMMER_SWING.appendTo(1.7F, new AnimEvent[]
										{ new PlaySoundEvent(1.24F, Side.CLIENT, ModSoundEvents.SWORD_SWING),
												new PlaySoundEvent(1.28F, Side.CLIENT,
														ModSoundEvents.BERENIKE_KNIGHT_FOOT),
												new PlaySoundEvent(2.36F, Side.CLIENT,
														ModSoundEvents.BERENIKE_KNIGHT_FOOT) }))
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 85)
								.addProperty(AttackProperty.POISE_DAMAGE, 38),

				new AttackAnimation.Builder(Animations.BERENIKE_KNIGHT_SWORD_DA.getId(), AttackType.DASH, 0.05F, 0.0F,
						0.88F, 1.3F, 2.8F, "Tool_R", DarkSouls.rl("berenike_knight/sword_da"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_MONSTER_HAMMER.appendTo(1.3F, new AnimEvent[]
										{ new PlaySoundEvent(0.16F, Side.CLIENT, ModSoundEvents.BERENIKE_KNIGHT_FOOT),
												new PlaySoundEvent(0.56F, Side.CLIENT,
														ModSoundEvents.BERENIKE_KNIGHT_FOOT),
												new PlaySoundEvent(0.64F, Side.CLIENT, ModSoundEvents.SWORD_SWING),
												new PlaySoundEvent(0.86F, Side.CLIENT,
														ModSoundEvents.BERENIKE_KNIGHT_FOOT),
												new PlaySoundEvent(2.76F, Side.CLIENT,
														ModSoundEvents.BERENIKE_KNIGHT_FOOT) }))
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 79)
								.addProperty(AttackProperty.POISE_DAMAGE, 35),

				new AttackAnimation.Builder(Animations.BERENIKE_KNIGHT_MACE_LA.getIds()[0], AttackType.LIGHT, 0.2F,
						0.0F, 1.24F, 1.7F, 2.4F, "Tool_R", DarkSouls.rl("berenike_knight/mace_la_1"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_MONSTER_HAMMER.appendTo(1.5F, new AnimEvent[]
										{ new PlaySoundEvent(0.52F, Side.CLIENT, ModSoundEvents.BERENIKE_KNIGHT_FOOT),
												new PlaySoundEvent(1.24F, Side.CLIENT, ModSoundEvents.FIST_SWING),
												new PlaySoundEvent(1.28F, Side.CLIENT,
														ModSoundEvents.BERENIKE_KNIGHT_FOOT),
												new PlaySoundEvent(2.36F, Side.CLIENT,
														ModSoundEvents.BERENIKE_KNIGHT_FOOT) }))
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 75)
								.addProperty(AttackProperty.POISE_DAMAGE, 32),
				new AttackAnimation.Builder(Animations.BERENIKE_KNIGHT_MACE_LA.getIds()[1], AttackType.LIGHT, 0.2F,
						0.0F, 0.6F, 0.76F, 2F, "Tool_R", DarkSouls.rl("berenike_knight/mace_la_2"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_MONSTER_HAMMER.appendTo(0.72F, new AnimEvent[]
										{ new PlaySoundEvent(0.6F, Side.CLIENT, ModSoundEvents.FIST_SWING),
												new PlaySoundEvent(0.62F, Side.CLIENT,
														ModSoundEvents.BERENIKE_KNIGHT_FOOT),
												new PlaySoundEvent(1.96F, Side.CLIENT,
														ModSoundEvents.BERENIKE_KNIGHT_FOOT) }))
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 75)
								.addProperty(AttackProperty.POISE_DAMAGE, 32),

				new AttackAnimation.Builder(Animations.BERENIKE_KNIGHT_MACE_HA.getId(), AttackType.HEAVY, 0.2F, 0.0F,
						1.24F, 1.7F, 2.8F, "Tool_R", DarkSouls.rl("berenike_knight/mace_ha"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_MONSTER_HAMMER.appendTo(1.7F, new AnimEvent[]
										{ new PlaySoundEvent(1.24F, Side.CLIENT, ModSoundEvents.FIST_SWING),
												new PlaySoundEvent(1.28F, Side.CLIENT,
														ModSoundEvents.BERENIKE_KNIGHT_FOOT),
												new PlaySoundEvent(2.76F, Side.CLIENT,
														ModSoundEvents.BERENIKE_KNIGHT_FOOT) }))
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 85)
								.addProperty(AttackProperty.POISE_DAMAGE, 38),

				new AttackAnimation.Builder(Animations.BERENIKE_KNIGHT_KICK.getId(), AttackType.HEAVY, 0.2F, 0.0F,
						0.56F, 0.9F, 1.6F, Colliders.FIST.getId(), "Leg_L", DarkSouls.rl("berenike_knight/kick"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.56F, Side.CLIENT, ModSoundEvents.FIST_SWING),
						new PlaySoundEvent(1.56F, Side.CLIENT, ModSoundEvents.BERENIKE_KNIGHT_FOOT) })
								.addProperty(AttackProperty.DEPENDS_ON_WEAPON, false)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.STUN_TYPE, StunType.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, Integer.MAX_VALUE)
								.addProperty(AttackProperty.POISE_DAMAGE, 38),

				// Black Knight
				new StaticAnimation.Builder(Animations.BLACK_KNIGHT_IDLE.getId(), 0.3F, true,
						DarkSouls.rl("black_knight/idle"), (models) -> models.ENTITY_BIPED),
				new StaticAnimation.Builder(Animations.BLACK_KNIGHT_WALK.getId(), 0.1F, true,
						DarkSouls.rl("black_knight/walking"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.24F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT),
										new PlaySoundEvent(0.8F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT) }),
				new StaticAnimation.Builder(Animations.BLACK_KNIGHT_RUN.getId(), 0.1F, true,
						DarkSouls.rl("black_knight/running"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.12F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT),
										new PlaySoundEvent(0.5F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT) }),
				new AdaptableAnimation.Builder(Animations.BLACK_KNIGHT_BLOCK.getId(), 0.2F, true,
						(models) -> models.ENTITY_BIPED)
								.addEntry(LivingMotion.BLOCKING, DarkSouls.rl("black_knight/block"), false)
								.addEntry(LivingMotion.WALKING, DarkSouls.rl("black_knight/block_walk"), true)
								.addEntry(LivingMotion.RUNNING, DarkSouls.rl("black_knight/block_run"), true),
				new DeathAnimation.Builder(Animations.BLACK_KNIGHT_DEATH.getId(), 0.1F,
						DarkSouls.rl("black_knight/death"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(1.84F, Side.SERVER, ModSoundEvents.GENERIC_LAND), })
								.addProperty(DeathProperty.DISAPPEAR_AT, 0.5F),

				new AttackAnimation.Builder(Animations.BLACK_KNIGHT_SWORD_LA_LONG.getIds()[0], AttackType.LIGHT, 0.2F,
						0.0F, 0.56F, 0.8F, 1.4F, "Tool_R", DarkSouls.rl("black_knight/black_knight_sword_la_1"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.48F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT),
						new PlaySoundEvent(0.56F, Side.CLIENT, ModSoundEvents.SWORD_SWING),
						new PlaySoundEvent(1.28F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 56)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),
				new AttackAnimation.Builder(Animations.BLACK_KNIGHT_SWORD_LA_LONG.getIds()[1], AttackType.LIGHT, 0.1F,
						0.0F, 0.44F, 0.64F, 1.4F, "Tool_R", DarkSouls.rl("black_knight/black_knight_sword_la_2"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.44F, Side.CLIENT, ModSoundEvents.SWORD_SWING),
						new PlaySoundEvent(0.48F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT),
						new PlaySoundEvent(1.28F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 56)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),
				new AttackAnimation.Builder(Animations.BLACK_KNIGHT_SWORD_LA_LONG.getIds()[2], AttackType.HEAVY, 0.1F,
						0.0F, 0.28F, 0.48F, 2.2F, "Tool_R", DarkSouls.rl("black_knight/black_knight_sword_la_3"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.2F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT),
						new PlaySoundEvent(0.28F, Side.CLIENT, ModSoundEvents.SWORD_THRUST),
						new PlaySoundEvent(1.48F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 56)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),
				new AttackAnimation.Builder(Animations.BLACK_KNIGHT_SWORD_LA_LONG.getIds()[3], AttackType.HEAVY, 0.1F,
						0.0F, 0.64F, 0.88F, 1.92F, "Tool_R", DarkSouls.rl("black_knight/black_knight_sword_la_4"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.64F, Side.CLIENT, ModSoundEvents.SWORD_SWING),
						new PlaySoundEvent(0.72F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT),
						new PlaySoundEvent(1.6F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 56)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),

				new AttackAnimation.Builder(Animations.BLACK_KNIGHT_SWORD_LA_SHORT.getIds()[0], AttackType.LIGHT, 0.2F,
						0.0F, 0.56F, 0.8F, 1.4F, "Tool_R", DarkSouls.rl("black_knight/black_knight_sword_la_1"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.48F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT),
						new PlaySoundEvent(0.56F, Side.CLIENT, ModSoundEvents.SWORD_SWING),
						new PlaySoundEvent(1.28F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 56)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),
				new AttackAnimation.Builder(Animations.BLACK_KNIGHT_SWORD_LA_SHORT.getIds()[1], AttackType.LIGHT, 0.1F,
						0.0F, 0.44F, 0.64F, 1.4F, "Tool_R", DarkSouls.rl("black_knight/black_knight_sword_la_2"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.44F, Side.CLIENT, ModSoundEvents.SWORD_SWING),
						new PlaySoundEvent(0.48F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT),
						new PlaySoundEvent(1.28F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 56)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),
				new AttackAnimation.Builder(Animations.BLACK_KNIGHT_SWORD_LA_SHORT.getIds()[2], AttackType.HEAVY, 0.1F,
						0.0F, 0.92F, 1.12F, 2.36F, "Tool_R", DarkSouls.rl("black_knight/black_knight_sword_la_5"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.16F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT),
						new PlaySoundEvent(0.92F, Side.CLIENT, ModSoundEvents.SWORD_SWING),
						new PlaySoundEvent(0.96F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT),
						new PlaySoundEvent(2.0F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 56)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),

				new AttackAnimation.Builder(Animations.BLACK_KNIGHT_SWORD_HA.getId(), AttackType.HEAVY, 0.2F, 0.0F,
						0.4F, 0.64F, 1.68F, "Tool_R", DarkSouls.rl("black_knight/black_knight_sword_ha"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.4F, Side.CLIENT, ModSoundEvents.SWORD_SWING),
						new PlaySoundEvent(0.48F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT),
						new PlaySoundEvent(1.44F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 56)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),
				new AttackAnimation.Builder(Animations.BLACK_KNIGHT_SHIELD_ATTACK.getId(), AttackType.HEAVY, 0.2F, 0.0F,
						0.52F, 0.8F, 1.6F, Colliders.SHIELD.getId(), "Tool_L",
						DarkSouls.rl("black_knight/black_knight_shield_attack"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.52F, Side.CLIENT, ModSoundEvents.AXE_SWING),
										new PlaySoundEvent(0.6F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT),
										new PlaySoundEvent(1.52F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 40)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(Animations.BLACK_KNIGHT_SWORD_DA.getId(), AttackType.DASH, 0.2F, 0.0F,
						0.64F, 0.8F, 1.68F, "Tool_R", DarkSouls.rl("black_knight/black_knight_sword_da"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.12F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT),
						new PlaySoundEvent(0.24F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT),
						new PlaySoundEvent(0.6F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT),
						new PlaySoundEvent(0.64F, Side.CLIENT, ModSoundEvents.SWORD_THRUST),
						new PlaySoundEvent(0.76F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT),
						new PlaySoundEvent(1.6F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 56)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),

				// Stray Demon
				new StaticAnimation.Builder(Animations.STRAY_DEMON_IDLE.getId(), 0.5F, true,
						DarkSouls.rl("stray_demon/idle"), (models) -> models.ENTITY_STRAY_DEMON),
				new StaticAnimation.Builder(Animations.STRAY_DEMON_WALK.getId(), 0.5F, true,
						DarkSouls.rl("stray_demon/walk"), (models) -> models.ENTITY_STRAY_DEMON)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.7F, Side.CLIENT, ModSoundEvents.STRAY_DEMON_FOOT),
										new ShakeCamGlobalEvent(0.7F, 10, 0.5F),
										new PlaySoundEvent(1.5F, Side.CLIENT, ModSoundEvents.STRAY_DEMON_FOOT),
										new ShakeCamGlobalEvent(1.5F, 10, 0.5F) }),
				new DeathAnimation.Builder(Animations.STRAY_DEMON_DEATH.getId(), 0.5F,
						DarkSouls.rl("stray_demon/death"), (models) -> models.ENTITY_STRAY_DEMON)
								.addProperty(DeathProperty.DISAPPEAR_AT, 1.5F),

				new AttackAnimation.Builder(Animations.STRAY_DEMON_HAMMER_LIGHT_ATTACK.getIds()[0], AttackType.LIGHT,
						1.0F, 0.0F, 0.12F, 0.54F, 1.6F, "Tool_R", DarkSouls.rl("stray_demon/hammer_la_1"),
						(models) -> models.ENTITY_STRAY_DEMON)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_MONSTER_HAMMER_SWING.appendTo(0.12F, new AnimEvent[]
										{ new PlaySoundEvent(0.36F, Side.SERVER, ModSoundEvents.STRAY_DEMON_FOOT),
												new ShakeCamGlobalEvent(0.36F, 10, 0.5F),
												new PlaySoundEvent(1.52F, Side.SERVER, ModSoundEvents.STRAY_DEMON_FOOT),
												new ShakeCamGlobalEvent(1.52F, 10, 0.5F) }))
								.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.STUN_TYPE, StunType.FLY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 80)
								.addProperty(AttackProperty.POISE_DAMAGE, 28),
				new AttackAnimation.Builder(Animations.STRAY_DEMON_HAMMER_LIGHT_ATTACK.getIds()[1], AttackType.LIGHT,
						1.0F, 0.0F, 0.44F, 0.74F, 2.0F, "Tool_R", DarkSouls.rl("stray_demon/hammer_la_2"),
						(models) -> models.ENTITY_STRAY_DEMON)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_MONSTER_HAMMER_SWING.appendTo(0.44F, new AnimEvent[]
										{ new PlaySoundEvent(0.48F, Side.SERVER, ModSoundEvents.STRAY_DEMON_FOOT),
												new ShakeCamGlobalEvent(0.48F, 10, 0.5F),
												new PlaySoundEvent(1.8F, Side.SERVER, ModSoundEvents.STRAY_DEMON_FOOT),
												new ShakeCamGlobalEvent(1.8F, 10, 0.5F) }))
								.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.STUN_TYPE, StunType.FLY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 80)
								.addProperty(AttackProperty.POISE_DAMAGE, 28),

				new AttackAnimation.Builder(Animations.STRAY_DEMON_HAMMER_ALT_LIGHT_ATTACK.getIds()[0],
						AttackType.LIGHT, 1.0F, 0.0F, 0.16F, 0.4F, 1.6F, "Tool_R",
						DarkSouls.rl("stray_demon/hammer_la_alt_1"), (models) -> models.ENTITY_STRAY_DEMON)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_MONSTER_HAMMER_SWING.appendTo(0.16F, new AnimEvent[]
										{})).addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.STUN_TYPE, StunType.FLY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 80)
								.addProperty(AttackProperty.POISE_DAMAGE, 28),
				new AttackAnimation.Builder(Animations.STRAY_DEMON_HAMMER_ALT_LIGHT_ATTACK.getIds()[1],
						AttackType.LIGHT, 1.0F, 0.0F, 0.48F, 0.9F, 2.0F, "Tool_R",
						DarkSouls.rl("stray_demon/hammer_la_alt_2"), (models) -> models.ENTITY_STRAY_DEMON)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_MONSTER_HAMMER_SWING.appendTo(0.44F, new AnimEvent[]
										{})).addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.STUN_TYPE, StunType.FLY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 80)
								.addProperty(AttackProperty.POISE_DAMAGE, 28),

				new AttackAnimation.Builder(Animations.STRAY_DEMON_HAMMER_HEAVY_ATTACK.getIds()[0], AttackType.HEAVY,
						0.2F, 0.0F, 1.08F, 1.34F, 2.6F, "Tool_R", DarkSouls.rl("stray_demon/hammer_ha_1"),
						(models) -> models.ENTITY_STRAY_DEMON)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_MONSTER_HAMMER.appendTo(1.12F, new AnimEvent[]
										{ new PlaySoundEvent(1.0F, Side.SERVER, ModSoundEvents.STRAY_DEMON_FOOT),
												new ShakeCamGlobalEvent(1.0F, 10, 0.5F) }))
								.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 80)
								.addProperty(AttackProperty.POISE_DAMAGE, 28),
				new AttackAnimation.Builder(Animations.STRAY_DEMON_HAMMER_HEAVY_ATTACK.getIds()[1], AttackType.HEAVY,
						0.2F, 0.0F, 1.76F, 2.02F, 3.6F, "Tool_R", DarkSouls.rl("stray_demon/hammer_ha_2"),
						(models) -> models.ENTITY_STRAY_DEMON)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_MONSTER_HAMMER.appendTo(1.8F, new AnimEvent[]
										{ new PlaySoundEvent(1.68F, Side.SERVER, ModSoundEvents.STRAY_DEMON_FOOT),
												new ShakeCamGlobalEvent(1.68F, 10, 0.5F) }))
								.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 80)
								.addProperty(AttackProperty.POISE_DAMAGE, 28),

				new AttackAnimation.Builder(Animations.STRAY_DEMON_HAMMER_DRIVE.getId(), AttackType.HEAVY, 0.1F, 0.0F,
						0.72F, 1.06F, 2.8F, "Tool_R", DarkSouls.rl("stray_demon/hammer_drive"),
						(models) -> models.ENTITY_STRAY_DEMON)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_MONSTER_HAMMER.appendTo(0.88F, new AnimEvent[]
										{})).addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 80)
								.addProperty(AttackProperty.POISE_DAMAGE, 28),
				new AttackAnimation.Builder(Animations.STRAY_DEMON_HAMMER_DASH_ATTACK.getId(), AttackType.DASH, 0.5F,
						0.0F, 1.0F, 1.3F, 2.4F, "Tool_R", DarkSouls.rl("stray_demon/hammer_da"),
						(models) -> models.ENTITY_STRAY_DEMON)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_MONSTER_HAMMER.appendTo(1.06F, new AnimEvent[]
										{ new PlaySoundEvent(0.24F, Side.SERVER, ModSoundEvents.STRAY_DEMON_WING),
												new PlaySoundEvent(0.56F, Side.SERVER, ModSoundEvents.STRAY_DEMON_WING),
												new PlaySoundEvent(0.76F, Side.SERVER, ModSoundEvents.STRAY_DEMON_LAND),
												new ShakeCamGlobalEvent(0.76F, 25, 1.5F) }))
								.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 80)
								.addProperty(AttackProperty.POISE_DAMAGE, 28),
				new AttackAnimation.Builder(Animations.STRAY_DEMON_GROUND_POUND.getId(), AttackType.HEAVY, 0.05F, 0.0F,
						1.6F, 1.88F, 3.2F, Colliders.STRAY_DEMON_BODY.getId(), "Root", DarkSouls.rl("stray_demon/ground_pound"),
						(models) -> models.ENTITY_STRAY_DEMON)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_MONSTER_LAND.appendTo(1.76F, new AnimEvent[]
										{ new PlaySoundEvent(0.52F, Side.SERVER, ModSoundEvents.STRAY_DEMON_WING),
												new PlaySoundEvent(1.0F, Side.SERVER,
														ModSoundEvents.STRAY_DEMON_WING) }))
								.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.STUN_TYPE, StunType.FLY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 80)
								.addProperty(AttackProperty.POISE_DAMAGE, 28),

				// Taurus Demon
				new StaticAnimation.Builder(Animations.TAURUS_DEMON_IDLE.getId(), 0.2F, true,
						DarkSouls.rl("taurus_demon/idle"), (models) -> models.ENTITY_TAURUS_DEMON),

				// Anastacia of Astora
				new StaticAnimation.Builder(Animations.ANASTACIA_IDLE.getId(), 0.4F, true,
						DarkSouls.rl("anastacia_of_astora/idle"), (models) -> models.ENTITY_BIPED),

				// Bell Gargoyle
				new StaticAnimation.Builder(Animations.BELL_GARGOYLE_IDLE.getId(), 0.75F, true,
						DarkSouls.rl("bell_gargoyle/idle"), (models) -> models.ENTITY_BELL_GARGOYLE),
				new StaticAnimation.Builder(Animations.BELL_GARGOYLE_WALK.getId(), 0.5F, true,
						DarkSouls.rl("bell_gargoyle/walk"), (models) -> models.ENTITY_BELL_GARGOYLE)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.24F, Side.SERVER, ModSoundEvents.BELL_GARGOYLE_FOOT),
										new PlaySoundEvent(0.84F, Side.SERVER, ModSoundEvents.BELL_GARGOYLE_FOOT) }),
				new StaticAnimation.Builder(Animations.BELL_GARGOYLE_RUN.getId(), 0.5F, true,
						DarkSouls.rl("bell_gargoyle/run"), (models) -> models.ENTITY_BELL_GARGOYLE)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.36F, Side.SERVER, ModSoundEvents.BELL_GARGOYLE_FOOT),
										new PlaySoundEvent(0.52F, Side.SERVER, ModSoundEvents.BELL_GARGOYLE_FOOT) }),
				new DeathAnimation.Builder(Animations.BELL_GARGOYLE_DEATH.getId(), 0.2F,
						DarkSouls.rl("bell_gargoyle/death"), (models) -> models.ENTITY_BELL_GARGOYLE)
								.addProperty(DeathProperty.DISAPPEAR_AT, 1.76F)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(2.08F, Side.SERVER, ModSoundEvents.BELL_GARGOYLE_LAND) }),

				new AttackAnimation.Builder(Animations.BELL_GARGOYLE_LA.getIds()[0], AttackType.LIGHT, 0.5F, 0.0F, 0.4F,
						0.64F, 1.6F, Colliders.BELL_GARGOYLE_HALBERD.getId(), "Tool_R", DarkSouls.rl("bell_gargoyle/la_1"),
						(models) -> models.ENTITY_BELL_GARGOYLE)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.4F, Side.SERVER, ModSoundEvents.STRAY_DEMON_SWING) })
								.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 17)
								.addProperty(AttackProperty.POISE_DAMAGE, 13),
				new AttackAnimation.Builder(Animations.BELL_GARGOYLE_LA.getIds()[1], AttackType.LIGHT, 0.5F, 0.0F, 0.56F,
						0.8F, 1.68F, Colliders.BELL_GARGOYLE_HALBERD.getId(), "Tool_R", DarkSouls.rl("bell_gargoyle/la_2"),
						(models) -> models.ENTITY_BELL_GARGOYLE)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.56F, Side.SERVER, ModSoundEvents.STRAY_DEMON_SWING) })
								.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 17)
								.addProperty(AttackProperty.POISE_DAMAGE, 13),
				new AttackAnimation.Builder(Animations.BELL_GARGOYLE_LA.getIds()[2], AttackType.LIGHT, 0.5F, 0.0F,
						0.7F, 0.96F, 3.2F, Colliders.BELL_GARGOYLE_HALBERD.getId(), "Tool_R",
						DarkSouls.rl("bell_gargoyle/la_3"), (models) -> models.ENTITY_BELL_GARGOYLE)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{
										new PlaySoundEvent(0.7F, Side.SERVER, ModSoundEvents.STRAY_DEMON_SWING),
										new PlaySoundEvent(1.44F, Side.SERVER, ModSoundEvents.BELL_GARGOYLE_SCREAM)
								})
								.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 17)
								.addProperty(AttackProperty.POISE_DAMAGE, 13),

				new AttackAnimation.Builder(Animations.BELL_GARGOYLE_HA.getIds()[0], AttackType.HEAVY, 0.5F, 0.0F,
						0.64F, 0.88F, 1.6F, Colliders.BELL_GARGOYLE_HALBERD.getId(), "Tool_R",
						DarkSouls.rl("bell_gargoyle/ha_1"), (models) -> models.ENTITY_BELL_GARGOYLE)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.68F, Side.SERVER, ModSoundEvents.STRAY_DEMON_SWING) })
								.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 20)
								.addProperty(AttackProperty.POISE_DAMAGE, 15),
				new AttackAnimation.Builder(Animations.BELL_GARGOYLE_HA.getIds()[1], AttackType.HEAVY, 0.5F, 0.0F,
						0.52F, 0.68F, 2.2F, Colliders.BELL_GARGOYLE_HALBERD.getId(), "Tool_R",
						DarkSouls.rl("bell_gargoyle/ha_2"), (models) -> models.ENTITY_BELL_GARGOYLE)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.52F, Side.SERVER, ModSoundEvents.STRAY_DEMON_SWING) })
								.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 20)
								.addProperty(AttackProperty.POISE_DAMAGE, 15),
				new AttackAnimation.Builder(Animations.BELL_GARGOYLE_HA.getIds()[2], AttackType.HEAVY, 0.5F, 0.0F,
						0.76F, 1.04F, 1.8F, Colliders.BELL_GARGOYLE_HALBERD.getId(), "Tool_R",
						DarkSouls.rl("bell_gargoyle/ha_3"), (models) -> models.ENTITY_BELL_GARGOYLE)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.52F, Side.SERVER, ModSoundEvents.STRAY_DEMON_SWING) })
								.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 20)
								.addProperty(AttackProperty.POISE_DAMAGE, 15)
		);
	}

	private static Path createPath(Path path, ResourceLocation location)
	{
		return path.resolve(DSDefaultPackResources.ROOT_DIR_NAME + "/" + location.getNamespace() + "/animation_data/"
				+ location.getPath() + ".json");
	}

	@Override
	public String getName()
	{
		return "AnimationData";
	}
}
