package com.skullmangames.darksouls.core.data;

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
import com.skullmangames.darksouls.common.animation.AnimationManager;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.SmashEvents;
import com.skullmangames.darksouls.common.animation.Property.ActionAnimationProperty;
import com.skullmangames.darksouls.common.animation.Property.AimingAnimationProperty;
import com.skullmangames.darksouls.common.animation.Property.AttackProperty;
import com.skullmangames.darksouls.common.animation.Property.DeathProperty;
import com.skullmangames.darksouls.common.animation.Property.StaticAnimationProperty;
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
import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation.Phase;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap.AttackType;
import com.skullmangames.darksouls.common.capability.item.Shield.Deflection;
import com.skullmangames.darksouls.common.entity.projectile.LightningSpear;
import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.init.ModParticles;
import com.skullmangames.darksouls.core.init.ModSoundEvents;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.MovementDamageType;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.StunType;

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
			}
			catch (IOException ioexception)
			{
				LOGGER.error("Couldn't save animation data {}", path1, ioexception);
			}
		}
		
		AnimationManager.initForDataGenerator(configs);
	}
	
	private static List<AnimBuilder> defaultConfigs()
	{
		return ImmutableList.of
		(
				new StaticAnimation.Builder(DarkSouls.rl("biped_idle"), 0.1F, true, DarkSouls.rl("biped/living/idle"),
						(models) -> models.ENTITY_BIPED),
				new MovementAnimation.Builder(DarkSouls.rl("biped_walk"), 0.08F, true,
						DarkSouls.rl("biped/living/walk"), (models) -> models.ENTITY_BIPED),
				new MovementAnimation.Builder(DarkSouls.rl("biped_run"), 0.08F, true, DarkSouls.rl("biped/living/run"),
						(models) -> models.ENTITY_BIPED),
				new MovementAnimation.Builder(DarkSouls.rl("biped_sneak"), 0.08F, true,
						DarkSouls.rl("biped/living/sneak"), (models) -> models.ENTITY_BIPED),

				new StaticAnimation.Builder(DarkSouls.rl("biped_idle_th"), 0.1F, true,
						DarkSouls.rl("biped/living/idle_th"), (models) -> models.ENTITY_BIPED),
				new StaticAnimation.Builder(DarkSouls.rl("biped_walk_th"), 0.08F, true,
						DarkSouls.rl("biped/living/walk_th"), (models) -> models.ENTITY_BIPED),
				new StaticAnimation.Builder(DarkSouls.rl("biped_run_th"), 0.08F, true,
						DarkSouls.rl("biped/living/run_th"), (models) -> models.ENTITY_BIPED),

				new StaticAnimation.Builder(DarkSouls.rl("biped_idle_th_big_weapon"), 0.1F, true,
						DarkSouls.rl("biped/living/idle_th_big_weapon"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.LAYER_PART, LayerPart.UP),

				new StaticAnimation.Builder(DarkSouls.rl("biped_idle_th_spear"), 0.1F, true,
						DarkSouls.rl("biped/living/idle_th_spear"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.LAYER_PART, LayerPart.UP),

				new StaticAnimation.Builder(DarkSouls.rl("biped_idle_th_shield"), 0.1F, true,
						DarkSouls.rl("biped/living/idle_th_shield"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.LAYER_PART, LayerPart.UP),

				new StaticAnimation.Builder(DarkSouls.rl("biped_change_item_right"), 0.1F, false,
						DarkSouls.rl("biped/living/change_item_right"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new ChangeItemEvent(0.2F) }),

				new StaticAnimation.Builder(DarkSouls.rl("biped_change_item_left"), 0.1F, false,
						DarkSouls.rl("biped/living/change_item_left"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new ChangeItemEvent(0.2F) }),

				new MovementAnimation.Builder(DarkSouls.rl("biped_swim"), 0.08F, true,
						DarkSouls.rl("biped/living/swim"), (models) -> models.ENTITY_BIPED),
				new StaticAnimation.Builder(new ResourceLocation(DarkSouls.MOD_ID, "biped_float"), 0.08F, true,
						DarkSouls.rl("biped/living/float"), (models) -> models.ENTITY_BIPED),
				new StaticAnimation.Builder(new ResourceLocation(DarkSouls.MOD_ID, "biped_kneel"), 0.08F, true,
						DarkSouls.rl("biped/living/kneel"), (models) -> models.ENTITY_BIPED),
				new StaticAnimation.Builder(new ResourceLocation(DarkSouls.MOD_ID, "biped_fall"), 0.08F, false,
						DarkSouls.rl("biped/living/fall"), (models) -> models.ENTITY_BIPED),

				new DeathAnimation.Builder(DarkSouls.rl("biped_death"), 0.05F, DarkSouls.rl("biped/death/death"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(1.52F, Side.SERVER, ModSoundEvents.GENERIC_LAND) })
				.addProperty(DeathProperty.DISAPPEAR_AT, 1.52F),

				new DeathAnimation.Builder(DarkSouls.rl("biped_death_smash"), 0.05F, DarkSouls.rl("biped/death/smash"),
						(models) -> models.ENTITY_BIPED).addProperty(DeathProperty.DISAPPEAR_AT, 1.52F),
				new DeathAnimation.Builder(DarkSouls.rl("biped_death_fly_front"), 0.05F,
						DarkSouls.rl("biped/death/fly_front"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.4F, Side.SERVER, ModSoundEvents.GENERIC_LAND),
										new PlaySoundEvent(0.8F, Side.SERVER, ModSoundEvents.GENERIC_LAND) })
								.addProperty(DeathProperty.DISAPPEAR_AT, 2.2F),
				new DeathAnimation.Builder(DarkSouls.rl("biped_death_fly_back"), 0.05F,
						DarkSouls.rl("biped/death/fly_back"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.44F, Side.SERVER, ModSoundEvents.GENERIC_LAND),
										new PlaySoundEvent(0.8F, Side.SERVER, ModSoundEvents.GENERIC_LAND) })
								.addProperty(DeathProperty.DISAPPEAR_AT, 2.2F),
				new DeathAnimation.Builder(DarkSouls.rl("biped_death_fly_left"), 0.05F,
						DarkSouls.rl("biped/death/fly_left"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.48F, Side.SERVER, ModSoundEvents.GENERIC_LAND),
										new PlaySoundEvent(0.92F, Side.SERVER, ModSoundEvents.GENERIC_LAND) })
								.addProperty(DeathProperty.DISAPPEAR_AT, 2.2F),
				new DeathAnimation.Builder(DarkSouls.rl("biped_death_fly_right"), 0.05F,
						DarkSouls.rl("biped/death/fly_right"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.48F, Side.SERVER, ModSoundEvents.GENERIC_LAND),
										new PlaySoundEvent(0.92F, Side.SERVER, ModSoundEvents.GENERIC_LAND) })
								.addProperty(DeathProperty.DISAPPEAR_AT, 2.2F),
				new DeathAnimation.Builder(DarkSouls.rl("biped_death_backstab"), 0.05F,
						DarkSouls.rl("biped/death/backstab"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new ImpactParticleEvent(0.44F, 0, 0, 0),
										new PlaySoundEvent(1.4F, Side.SERVER, ModSoundEvents.GENERIC_LAND) })
								.addProperty(DeathProperty.DISAPPEAR_AT, 3.76F),
				new DeathAnimation.Builder(DarkSouls.rl("biped_death_punish"), 0.05F,
						DarkSouls.rl("biped/death/punish"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new ImpactParticleEvent(0.44F, 0, 0, 0),
										new PlaySoundEvent(1.4F, Side.SERVER, ModSoundEvents.GENERIC_LAND) })
								.addProperty(DeathProperty.DISAPPEAR_AT, 4.4F),

				new StaticAnimation.Builder(new ResourceLocation(DarkSouls.MOD_ID, "biped_dig"), 0.2F, true,
						DarkSouls.rl("biped/living/dig"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.LAYER_PART, LayerPart.RIGHT),
				new ActionAnimation.Builder(DarkSouls.rl("biped_touch_bonfire"), 0.5F,
						DarkSouls.rl("biped/living/touching_bonfire"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new TeleportParticleEvent(0.5F), new TeleportParticleEvent(1.0F),
										new TeleportParticleEvent(1.5F), new TeleportParticleEvent(2.0F),
										new TeleportParticleEvent(2.5F), new TeleportParticleEvent(3.0F),
										new TeleportParticleEvent(3.5F), new TeleportParticleEvent(4.0F),
										new PlaySoundEvent(2.5F, Side.SERVER, ModSoundEvents.BONFIRE_TELEPORT),
										new TeleportEvent(3.2F) }),

				new MirrorAnimation.Builder(DarkSouls.rl("biped_eat"), 0.2F, true, DarkSouls.rl("biped/living/eat_r"),
						DarkSouls.rl("biped/living/eat_l"), (models) -> models.ENTITY_BIPED),
				new MirrorAnimation.Builder(DarkSouls.rl("biped_drink"), 0.2F, true,
						DarkSouls.rl("biped/living/drink_r"), DarkSouls.rl("biped/living/drink_l"),
						(models) -> models.ENTITY_BIPED),
				new MirrorAnimation.Builder(DarkSouls.rl("biped_consume_soul"), 0.2F, true,
						DarkSouls.rl("biped/living/consume_soul_r"), DarkSouls.rl("biped/living/consume_soul_l"),
						(models) -> models.ENTITY_BIPED),

				new ActionAnimation.Builder(DarkSouls.rl("biped_throw"), 0.2F, DarkSouls.rl("biped/combat/throw"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new ShootThrowableProjectileEvent(0.6F) }),

				new AdaptableAnimation.Builder(DarkSouls.rl("biped_block"), 0.1F, true, (models) -> models.ENTITY_BIPED)
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

				new AdaptableAnimation.Builder(DarkSouls.rl("biped_block_vertical"), 0.1F, true,
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

				new AdaptableAnimation.Builder(DarkSouls.rl("biped_block_th_sword"), 0.1F, true,
						(models) -> models.ENTITY_BIPED)
								.addEntry(LivingMotion.BLOCKING, DarkSouls.rl("biped/combat/block_th_sword"), false)
								.addEntry(LivingMotion.WALKING, DarkSouls.rl("biped/combat/block_th_sword"), true)
								.addEntry(LivingMotion.RUNNING, DarkSouls.rl("biped/combat/block_th_sword"), true)
								.addEntry(LivingMotion.KNEELING, DarkSouls.rl("biped/combat/block_th_sword"), true)
								.addEntry(LivingMotion.SNEAKING, DarkSouls.rl("biped/combat/block_th_sword"), true),

				new AdaptableAnimation.Builder(DarkSouls.rl("biped_block_greatshield"), 0.1F, true,
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

				new BlockedAnimation.Builder(DarkSouls.rl("biped_hit_blocked_left"), 0.05F,
						DarkSouls.rl("biped/hit/blocked_left"), (models) -> models.ENTITY_BIPED)
								.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true),
				new BlockedAnimation.Builder(DarkSouls.rl("biped_hit_blocked_right"), 0.05F,
						DarkSouls.rl("biped/hit/blocked_right"), (models) -> models.ENTITY_BIPED)
								.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true),
				new BlockedAnimation.Builder(DarkSouls.rl("biped_hit_blocked_vertical_left"), 0.05F,
						DarkSouls.rl("biped/hit/blocked_vertical_left"), (models) -> models.ENTITY_BIPED)
								.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true),
				new BlockedAnimation.Builder(DarkSouls.rl("biped_hit_blocked_vertical_right"), 0.05F,
						DarkSouls.rl("biped/hit/blocked_vertical_right"), (models) -> models.ENTITY_BIPED)
								.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true),
				new BlockedAnimation.Builder(DarkSouls.rl("biped_hit_blocked_th_sword"), 0.05F,
						DarkSouls.rl("biped/hit/blocked_th_sword"), (models) -> models.ENTITY_BIPED),

				new InvincibleAnimation.Builder(DarkSouls.rl("biped_hit_blocked_fly_left"), 0.05F,
						DarkSouls.rl("biped/hit/blocked_fly_left"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.48F, Side.SERVER, ModSoundEvents.GENERIC_ROLL) }),
				new InvincibleAnimation.Builder(DarkSouls.rl("biped_hit_blocked_fly_right"), 0.05F,
						DarkSouls.rl("biped/hit/blocked_fly_right"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.48F, Side.SERVER, ModSoundEvents.GENERIC_ROLL) }),
				new InvincibleAnimation.Builder(DarkSouls.rl("biped_hit_blocked_vertical_fly_left"), 0.05F,
						DarkSouls.rl("biped/hit/blocked_vertical_fly_left"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.48F, Side.SERVER, ModSoundEvents.GENERIC_ROLL) }),
				new InvincibleAnimation.Builder(DarkSouls.rl("biped_hit_blocked_vertical_fly_right"), 0.05F,
						DarkSouls.rl("biped/hit/blocked_vertical_fly_right"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.48F, Side.SERVER, ModSoundEvents.GENERIC_ROLL) }),
				new InvincibleAnimation.Builder(DarkSouls.rl("biped_hit_blocked_th_sword_fly"), 0.05F,
						DarkSouls.rl("biped/hit/blocked_th_sword_fly"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.48F, Side.SERVER, ModSoundEvents.GENERIC_ROLL) }),

				new ActionAnimation.Builder(DarkSouls.rl("biped_disarm_shield_left"), 0.05F,
						DarkSouls.rl("biped/combat/disarmed_left"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(AnimEvent.ON_BEGIN, Side.SERVER,
										ModSoundEvents.PLAYER_SHIELD_DISARMED) })
								.addProperty(ActionAnimationProperty.PUNISHABLE, true),
				new ActionAnimation.Builder(DarkSouls.rl("biped_disarm_shield_right"), 0.05F,
						DarkSouls.rl("biped/combat/disarmed_right"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(AnimEvent.ON_BEGIN, Side.SERVER,
										ModSoundEvents.PLAYER_SHIELD_DISARMED) })
								.addProperty(ActionAnimationProperty.PUNISHABLE, true),

				new StaticAnimation.Builder(DarkSouls.rl("biped_horseback_idle"), 0.2F, true,
						DarkSouls.rl("biped/horseback/horseback_idle"), (models) -> models.ENTITY_BIPED),

				new StaticAnimation.Builder(DarkSouls.rl("biped_idle_crossbow"), 0.2F, true,
						DarkSouls.rl("biped/living/idle_crossbow"), (models) -> models.ENTITY_BIPED),
				new MovementAnimation.Builder(DarkSouls.rl("biped_walk_crossbow"), 0.2F, true,
						DarkSouls.rl("biped/living/walk_crossbow"), (models) -> models.ENTITY_BIPED),

				new AimingAnimation.Builder(DarkSouls.rl("biped_crossbow_aim"), 0.16F, true,
						DarkSouls.rl("biped/combat/crossbow_aim_mid"), DarkSouls.rl("biped/combat/crossbow_aim_up"),
						DarkSouls.rl("biped/combat/crossbow_aim_down"), (models) -> models.ENTITY_BIPED),
				new AimingAnimation.Builder(DarkSouls.rl("biped_crossbow_shot"), 0.16F, false,
						DarkSouls.rl("biped/combat/crossbow_shot_mid"), DarkSouls.rl("biped/combat/crossbow_shot_up"),
						DarkSouls.rl("biped/combat/crossbow_shot_down"), (models) -> models.ENTITY_BIPED)
						.addProperty(AimingAnimationProperty.IS_REBOUND, true),

				new StaticAnimation.Builder(DarkSouls.rl("biped_crossbow_reload"), 0.16F, false,
						DarkSouls.rl("biped/combat/crossbow_reload"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.LAYER_PART, LayerPart.UP),

				new AimingAnimation.Builder(DarkSouls.rl("biped_bow_aim"), 0.16F, true,
						DarkSouls.rl("biped/combat/bow_aim_mid"), DarkSouls.rl("biped/combat/bow_aim_up"),
						DarkSouls.rl("biped/combat/bow_aim_down"), (models) -> models.ENTITY_BIPED),
				new AimingAnimation.Builder(DarkSouls.rl("biped_bow_rebound"), 0.04F, false,
						DarkSouls.rl("biped/combat/bow_shot_mid"), DarkSouls.rl("biped/combat/bow_shot_up"),
						DarkSouls.rl("biped/combat/bow_shot_down"), (models) -> models.ENTITY_BIPED)
						.addProperty(AimingAnimationProperty.IS_REBOUND, true),

				new AimingAnimation.Builder(DarkSouls.rl("biped_speer_aim"), 0.16F, false,
						DarkSouls.rl("biped/combat/javelin_aim_mid"), DarkSouls.rl("biped/combat/javelin_aim_up"),
						DarkSouls.rl("biped/combat/javelin_aim_down"), (models) -> models.ENTITY_BIPED),
				new AimingAnimation.Builder(DarkSouls.rl("biped_speer_rebound"), 0.08F, false,
						DarkSouls.rl("biped/combat/javelin_throw_mid"), DarkSouls.rl("biped/combat/javelin_throw_up"),
						DarkSouls.rl("biped/combat/javelin_throw_down"), (models) -> models.ENTITY_BIPED)
						.addProperty(AimingAnimationProperty.IS_REBOUND, true),

				new ActionAnimation.Builder(DarkSouls.rl("biped_hit_light_front"), 0.05F, DarkSouls.rl("biped/hit/light_front"),
						(models) -> models.ENTITY_BIPED).addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true)
						.addProperty(ActionAnimationProperty.IS_HIT, true),
				new ActionAnimation.Builder(DarkSouls.rl("biped_hit_light_left"), 0.05F, DarkSouls.rl("biped/hit/light_left"),
						(models) -> models.ENTITY_BIPED).addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true)
						.addProperty(ActionAnimationProperty.IS_HIT, true),
				new ActionAnimation.Builder(DarkSouls.rl("biped_hit_light_right"), 0.05F, DarkSouls.rl("biped/hit/light_right"),
						(models) -> models.ENTITY_BIPED).addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true)
						.addProperty(ActionAnimationProperty.IS_HIT, true),
				new ActionAnimation.Builder(DarkSouls.rl("biped_hit_light_back"), 0.05F, DarkSouls.rl("biped/hit/light_back"),
						(models) -> models.ENTITY_BIPED).addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true)
						.addProperty(ActionAnimationProperty.IS_HIT, true),
				new ActionAnimation.Builder(DarkSouls.rl("biped_hit_heavy_front"), 0.05F, DarkSouls.rl("biped/hit/heavy_front"),
						(models) -> models.ENTITY_BIPED).addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true)
						.addProperty(ActionAnimationProperty.IS_HIT, true),
				new ActionAnimation.Builder(DarkSouls.rl("biped_hit_heavy_back"), 0.05F, DarkSouls.rl("biped/hit/heavy_back"),
						(models) -> models.ENTITY_BIPED).addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true)
						.addProperty(ActionAnimationProperty.IS_HIT, true),
				new ActionAnimation.Builder(DarkSouls.rl("biped_hit_heavy_left"), 0.05F, DarkSouls.rl("biped/hit/heavy_left"),
						(models) -> models.ENTITY_BIPED).addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true)
						.addProperty(ActionAnimationProperty.IS_HIT, true),
				new ActionAnimation.Builder(DarkSouls.rl("biped_hit_heavy_right"), 0.05F, DarkSouls.rl("biped/hit/heavy_right"),
						(models) -> models.ENTITY_BIPED).addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true)
						.addProperty(ActionAnimationProperty.IS_HIT, true),

				new ActionAnimation.Builder(DarkSouls.rl("biped_horseback_hit_light_front"), 0.05F,
						DarkSouls.rl("biped/hit/horseback_light_front"), (models) -> models.ENTITY_BIPED)
								.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true)
								.addProperty(ActionAnimationProperty.IS_HIT, true),
				new ActionAnimation.Builder(DarkSouls.rl("biped_horseback_hit_light_left"), 0.05F,
						DarkSouls.rl("biped/hit/horseback_light_left"), (models) -> models.ENTITY_BIPED)
								.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true)
								.addProperty(ActionAnimationProperty.IS_HIT, true),
				new ActionAnimation.Builder(DarkSouls.rl("biped_horseback_hit_light_right"), 0.05F,
						DarkSouls.rl("biped/hit/horseback_light_right"), (models) -> models.ENTITY_BIPED)
								.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true)
								.addProperty(ActionAnimationProperty.IS_HIT, true),
				new ActionAnimation.Builder(DarkSouls.rl("biped_horseback_hit_light_back"), 0.05F,
						DarkSouls.rl("biped/hit/horseback_light_back"), (models) -> models.ENTITY_BIPED)
								.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true)
								.addProperty(ActionAnimationProperty.IS_HIT, true),
				new ActionAnimation.Builder(DarkSouls.rl("biped_horseback_hit_heavy_front"), 0.05F,
						DarkSouls.rl("biped/hit/horseback_heavy_front"), (models) -> models.ENTITY_BIPED)
								.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true)
								.addProperty(ActionAnimationProperty.IS_HIT, true),
				new ActionAnimation.Builder(DarkSouls.rl("biped_horseback_hit_heavy_back"), 0.05F,
						DarkSouls.rl("biped/hit/horseback_heavy_back"), (models) -> models.ENTITY_BIPED)
								.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true)
								.addProperty(ActionAnimationProperty.IS_HIT, true),
				new ActionAnimation.Builder(DarkSouls.rl("biped_horseback_hit_heavy_left"), 0.05F,
						DarkSouls.rl("biped/hit/horseback_heavy_left"), (models) -> models.ENTITY_BIPED)
								.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true)
								.addProperty(ActionAnimationProperty.IS_HIT, true),
				new ActionAnimation.Builder(DarkSouls.rl("biped_horseback_hit_heavy_right"), 0.05F,
						DarkSouls.rl("biped/hit/horseback_heavy_right"), (models) -> models.ENTITY_BIPED)
								.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true)
								.addProperty(ActionAnimationProperty.IS_HIT, true),

				new InvincibleAnimation.Builder(DarkSouls.rl("biped_hit_smash"), 0.05F, DarkSouls.rl("biped/hit/smash"),
						(models) -> models.ENTITY_BIPED),
				new InvincibleAnimation.Builder(DarkSouls.rl("biped_hit_fly_front"), 0.05F,
						DarkSouls.rl("biped/hit/fly"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.4F, Side.SERVER, ModSoundEvents.GENERIC_LAND),
										new PlaySoundEvent(0.8F, Side.SERVER, ModSoundEvents.GENERIC_LAND) }),
				new InvincibleAnimation.Builder(DarkSouls.rl("biped_hit_fly_back"), 0.05F,
						DarkSouls.rl("biped/hit/fly_back"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.44F, Side.SERVER, ModSoundEvents.GENERIC_LAND),
										new PlaySoundEvent(0.8F, Side.SERVER, ModSoundEvents.GENERIC_LAND) }),
				new InvincibleAnimation.Builder(DarkSouls.rl("biped_hit_fly_left"), 0.05F,
						DarkSouls.rl("biped/hit/fly_left"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.48F, Side.SERVER, ModSoundEvents.GENERIC_LAND),
										new PlaySoundEvent(0.92F, Side.SERVER, ModSoundEvents.GENERIC_LAND) }),
				new InvincibleAnimation.Builder(DarkSouls.rl("biped_hit_fly_right"), 0.05F,
						DarkSouls.rl("biped/hit/fly_right"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.48F, Side.SERVER, ModSoundEvents.GENERIC_LAND),
										new PlaySoundEvent(0.92F, Side.SERVER, ModSoundEvents.GENERIC_LAND) }),
				new ActionAnimation.Builder(DarkSouls.rl("biped_hit_land_heavy"), 0.05F, DarkSouls.rl("biped/hit/land_heavy"),
						(models) -> models.ENTITY_BIPED).addProperty(ActionAnimationProperty.IS_HIT, true),

				new InvincibleAnimation.Builder(DarkSouls.rl("biped_hit_backstab"), 0.05F,
						DarkSouls.rl("biped/hit/backstab"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.DEATH_ANIMATION,
										DarkSouls.rl("biped_death_backstab"))
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new ImpactParticleEvent(0.44F, 0, 0, 0),
										new PlaySoundEvent(1.4F, Side.SERVER, ModSoundEvents.GENERIC_LAND) }),

				new InvincibleAnimation.Builder(DarkSouls.rl("biped_hit_punish"), 0.05F,
						DarkSouls.rl("biped/hit/punish"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.DEATH_ANIMATION,
										DarkSouls.rl("biped_death_punish"))
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new ImpactParticleEvent(0.44F, 0, 0, 0),
										new PlaySoundEvent(1.4F, Side.SERVER, ModSoundEvents.GENERIC_LAND) }),

				new DodgingAnimation.Builder(DarkSouls.rl("biped_roll"), 0.05F, DarkSouls.rl("biped/combat/roll"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.28F, Side.CLIENT, ModSoundEvents.GENERIC_ROLL) }),
				new DodgingAnimation.Builder(DarkSouls.rl("biped_fat_roll"), 0.05F, DarkSouls.rl("biped/combat/fat_roll"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.48F, Side.SERVER, ModSoundEvents.GENERIC_LAND),
						new ShakeCamGlobalEvent(0.48F, 10, 0.25F) }),
				new ActionAnimation.Builder(DarkSouls.rl("biped_roll_too_fat"), 0.05F,
						DarkSouls.rl("biped/combat/roll_too_fat"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.4F, Side.SERVER, ModSoundEvents.GENERIC_LAND),
										new ShakeCamGlobalEvent(0.4F, 10, 0.25F) }),
				new DodgingAnimation.Builder(DarkSouls.rl("biped_roll_back"), 0.05F, DarkSouls.rl("biped/combat/roll_back"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.28F, Side.SERVER, ModSoundEvents.GENERIC_ROLL) }),
				new DodgingAnimation.Builder(DarkSouls.rl("biped_roll_left"), 0.05F, true,
						DarkSouls.rl("biped/combat/roll_left"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.28F, Side.SERVER, ModSoundEvents.GENERIC_ROLL) }),
				new DodgingAnimation.Builder(DarkSouls.rl("biped_roll_right"), 0.05F, true,
						DarkSouls.rl("biped/combat/roll_right"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.28F, Side.SERVER, ModSoundEvents.GENERIC_ROLL) }),
				new DodgingAnimation.Builder(DarkSouls.rl("biped_jump_back"), 0.08F, DarkSouls.rl("biped/combat/jump_back"),
						(models) -> models.ENTITY_BIPED),

				// Miracle
				new ActionAnimation.Builder(DarkSouls.rl("biped_cast_miracle_heal"), 0.5F,
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

				new ActionAnimation.Builder(DarkSouls.rl("biped_cast_miracle_heal_aid"), 0.5F,
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

				new ActionAnimation.Builder(DarkSouls.rl("biped_cast_miracle_homeward"), 0.5F,
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

				new ActionAnimation.Builder(DarkSouls.rl("biped_cast_miracle_force"), 0.3F,
						DarkSouls.rl("biped/combat/cast_miracle_force"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new SetLightSourceEvent(AnimEvent.ON_BEGIN, 15, 1.85F),
										new SimpleParticleEvent(0.56F, ModParticles.FORCE, 0, 1, 0),
										new PlaySoundEvent(0.56F, Side.SERVER, ModSoundEvents.MIRACLE_FORCE),
										new ShockWaveEvent(0.6F, 3.0D) }),

				new ActionAnimation.Builder(DarkSouls.rl("biped_cast_miracle_lightning_spear"), 0.3F,
						DarkSouls.rl("biped/combat/cast_miracle_spear"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new SetLightSourceEvent(AnimEvent.ON_BEGIN, 15, 1.2F),
										new PlaySoundEvent(AnimEvent.ON_BEGIN, Side.SERVER,
												ModSoundEvents.LIGHTNING_SPEAR_APPEAR),
										new EntityboundParticleEvent(AnimEvent.ON_BEGIN, ModParticles.LIGHTNING_SPEAR,
												0, 1, 0),
										new ShootMagicProjectileEvent(0.9F, LightningSpear::lightningSpear),
										new PlaySoundEvent(0.9F, Side.SERVER, ModSoundEvents.LIGHTNING_SPEAR_SHOT) }),
				new ActionAnimation.Builder(DarkSouls.rl("horseback_cast_miracle_lightning_spear"), 0.3F,
						DarkSouls.rl("biped/combat/horseback_cast_miracle_spear"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new SetLightSourceEvent(AnimEvent.ON_BEGIN, 15, 1.2F),
										new PlaySoundEvent(AnimEvent.ON_BEGIN, Side.SERVER,
												ModSoundEvents.LIGHTNING_SPEAR_APPEAR),
										new EntityboundParticleEvent(AnimEvent.ON_BEGIN, ModParticles.LIGHTNING_SPEAR,
												0, 1, 0),
										new ShootMagicProjectileEvent(0.9F, LightningSpear::lightningSpear),
										new PlaySoundEvent(0.9F, Side.SERVER, ModSoundEvents.LIGHTNING_SPEAR_SHOT) }),

				new ActionAnimation.Builder(DarkSouls.rl("biped_cast_miracle_great_lightning_spear"), 0.3F,
						DarkSouls.rl("biped/combat/cast_miracle_spear"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new SetLightSourceEvent(AnimEvent.ON_BEGIN, 15, 1.2F),
										new PlaySoundEvent(AnimEvent.ON_BEGIN, Side.SERVER,
												ModSoundEvents.LIGHTNING_SPEAR_APPEAR),
										new EntityboundParticleEvent(AnimEvent.ON_BEGIN,
												ModParticles.GREAT_LIGHTNING_SPEAR, 0, 1, 0),
										new ShootMagicProjectileEvent(0.9F, LightningSpear::greatLightningSpear),
										new PlaySoundEvent(0.9F, Side.SERVER, ModSoundEvents.LIGHTNING_SPEAR_SHOT) }),
				new ActionAnimation.Builder(DarkSouls.rl("horseback_cast_miracle_great_lightning_spear"), 0.3F,
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
				new MirrorAnimation.Builder(DarkSouls.rl("biped_holding_big_weapon"), 0.2F, true, true,
						DarkSouls.rl("biped/living/holding_big_weapon_r"),
						DarkSouls.rl("biped/living/holding_big_weapon_l"), (models) -> models.ENTITY_BIPED),

				// Horseback Attacks
				new AttackAnimation.Builder(DarkSouls.rl("horseback_light_attack_1"), AttackType.LIGHT, 0.5F, 0.0F,
						0.2F, 0.52F, 1.6F, "Tool_R", DarkSouls.rl("biped/combat/horseback_light_attack_1"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM),
				new AttackAnimation.Builder(DarkSouls.rl("horseback_light_attack_2"), AttackType.LIGHT, 0.5F, 0.0F,
						0.12F, 0.48F, 1.6F, "Tool_R", DarkSouls.rl("biped/combat/horseback_light_attack_2"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM),

				// Parries
				new ParryAnimation.Builder(DarkSouls.rl("shield_parry"), 0.1F, 0.32F, 0.8F, "Tool_L",
						DarkSouls.rl("biped/combat/shield_parry"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.32F, Side.SERVER, ModSoundEvents.FIST_SWING) }),
				new ParryAnimation.Builder(DarkSouls.rl("shield_parry_mirrored"), 0.1F, 0.32F, 0.8F, "Tool_R",
						DarkSouls.rl("biped/combat/shield_parry_mirrored"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.32F, Side.SERVER, ModSoundEvents.FIST_SWING) }),
				new ParryAnimation.Builder(DarkSouls.rl("buckler_parry"), 0.15F, 0.32F, 0.8F, "Tool_L",
						DarkSouls.rl("biped/combat/buckler_parry"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.32F, Side.SERVER, ModSoundEvents.FIST_SWING) }),
				new ParryAnimation.Builder(DarkSouls.rl("buckler_parry_mirrored"), 0.15F, 0.32F, 0.8F, "Tool_R",
						DarkSouls.rl("biped/combat/buckler_parry_mirrored"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.32F, Side.SERVER, ModSoundEvents.FIST_SWING) }),

				// Backstabs
				new BackstabCheckAnimation.Builder(DarkSouls.rl("backstab_thrust_check"), AttackType.BACKSTAB, 0.2F,
						0.0F, 0.36F, 0.64F, 1.44F, false, "Tool_R", DarkSouls.rl("biped/combat/backstab_thrust_check"),
						(models) -> models.ENTITY_BIPED, DarkSouls.rl("backstab_thrust")),
				new InvincibleAnimation.Builder(DarkSouls.rl("backstab_thrust"), 0.05F,
						DarkSouls.rl("biped/combat/backstab_thrust"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(AnimEvent.ON_BEGIN, Side.SERVER,
										ModSoundEvents.PLAYER_SHIELD_DISARMED),
										new ShakeCamEvent(AnimEvent.ON_BEGIN, 10, 0.5F),
										new PlaySoundEvent(1.08F, Side.SERVER, ModSoundEvents.GENERIC_KICK),
										new PlaySoundEvent(1.08F, Side.SERVER, ModSoundEvents.SWORD_PULLOUT),
										new ShakeCamEvent(1.3F, 10, 0.5F) }),
				new BackstabCheckAnimation.Builder(DarkSouls.rl("backstab_strike_check"), AttackType.BACKSTAB, 0.2F,
						0.0F, 0.4F, 0.8F, 1.44F, true, "Tool_R", DarkSouls.rl("biped/combat/backstab_strike_check"),
						(models) -> models.ENTITY_BIPED, DarkSouls.rl("backstab_strike")),
				new CriticalHitAnimation.Builder(DarkSouls.rl("backstab_strike"), 0.05F, 1.24F,
						DarkSouls.rl("biped/combat/backstab_strike"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(AnimEvent.ON_BEGIN, Side.SERVER,
										ModSoundEvents.PLAYER_SHIELD_DISARMED),
										new ShakeCamEvent(AnimEvent.ON_BEGIN, 10, 0.5F),
										new PlaySoundEvent(1.24F, Side.SERVER, ModSoundEvents.PLAYER_SHIELD_DISARMED),
										new ShakeCamEvent(1.3F, 10, 0.5F) }),

				// Punishes
				new PunishCheckAnimation.Builder(DarkSouls.rl("punish_thrust_check"), AttackType.PUNISH, 0.2F, 0.0F,
						0.36F, 0.64F, 1.44F, false, "Tool_R", DarkSouls.rl("biped/combat/backstab_thrust_check"),
						(models) -> models.ENTITY_BIPED, DarkSouls.rl("punish_thrust")),
				new InvincibleAnimation.Builder(DarkSouls.rl("punish_thrust"), 0.05F,
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
				new PunishCheckAnimation.Builder(DarkSouls.rl("punish_strike_check"), AttackType.PUNISH, 0.2F, 0.0F,
						0.4F, 0.8F, 1.44F, true, "Tool_R", DarkSouls.rl("biped/combat/backstab_strike_check"),
						(models) -> models.ENTITY_BIPED, DarkSouls.rl("punish_strike")),
				new CriticalHitAnimation.Builder(DarkSouls.rl("punish_strike"), 0.05F, 1.24F,
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
				new AttackAnimation.Builder(DarkSouls.rl("thrusting_sword_light_attack"), AttackType.LIGHT, 0.2F, 0.0F,
						0.28F, 0.4F, 1.2F, "Tool_R", DarkSouls.rl("biped/combat/thrusting_sword_la"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.28F, Side.SERVER, ModSoundEvents.SWORD_THRUST) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STUN_TYPE, StunType.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 25)
								.addProperty(AttackProperty.STAMINA_USAGE, 15)
								.addProperty(AttackProperty.POISE_DAMAGE, 15),

				new AttackAnimation.Builder(DarkSouls.rl("thrusting_sword_heavy_attack_1"), AttackType.HEAVY, 0.2F,
						0.0F, 0.72F, 0.84F, 1.6F, "Tool_R", DarkSouls.rl("biped/combat/thrusting_sword_ha_1"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.72F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 37)
								.addProperty(AttackProperty.STAMINA_USAGE, 25)
								.addProperty(AttackProperty.POISE_DAMAGE, 15),
				new AttackAnimation.Builder(DarkSouls.rl("thrusting_sword_heavy_attack_2"), AttackType.HEAVY, 0.2F,
						0.0F, 0.68F, 0.8F, 2.0F, "Tool_R", DarkSouls.rl("biped/combat/thrusting_sword_ha_2"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.68F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 37)
								.addProperty(AttackProperty.STAMINA_USAGE, 25)
								.addProperty(AttackProperty.POISE_DAMAGE, 15),

				new AttackAnimation.Builder(DarkSouls.rl("thrusting_sword_dash_attack"), AttackType.DASH, 0.2F, 0.0F,
						0.6F, 0.88F, 1.6F, "Tool_R", DarkSouls.rl("biped/combat/thrusting_sword_da"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.6F, Side.SERVER, ModSoundEvents.SWORD_THRUST) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STUN_TYPE, StunType.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 37)
								.addProperty(AttackProperty.STAMINA_USAGE, 25)
								.addProperty(AttackProperty.POISE_DAMAGE, 15),
				new AttackAnimation.Builder(DarkSouls.rl("thrusting_sword_th_light_attack"), AttackType.LIGHT, 0.2F,
						0.0F, 0.28F, 0.4F, 1.2F, "Tool_R", DarkSouls.rl("biped/combat/thrusting_sword_th_la"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.28F, Side.SERVER, ModSoundEvents.SWORD_THRUST) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STUN_TYPE, StunType.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 37)
								.addProperty(AttackProperty.STAMINA_USAGE, 15)
								.addProperty(AttackProperty.POISE_DAMAGE, 15),

				new AttackAnimation.Builder(DarkSouls.rl("thrusting_sword_th_heavy_attack_1"), AttackType.HEAVY, 0.2F,
						0.0F, 0.72F, 0.84F, 1.6F, "Tool_R", DarkSouls.rl("biped/combat/thrusting_sword_th_ha_1"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.72F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 37)
								.addProperty(AttackProperty.STAMINA_USAGE, 25)
								.addProperty(AttackProperty.POISE_DAMAGE, 15),
				new AttackAnimation.Builder(DarkSouls.rl("thrusting_sword_th_heavy_attack_2"), AttackType.HEAVY, 0.2F,
						0.0F, 0.68F, 0.8F, 2.0F, "Tool_R", DarkSouls.rl("biped/combat/thrusting_sword_th_ha_2"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.68F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 37)
								.addProperty(AttackProperty.STAMINA_USAGE, 25)
								.addProperty(AttackProperty.POISE_DAMAGE, 15),

				new AttackAnimation.Builder(DarkSouls.rl("thrusting_sword_th_dash_attack"), AttackType.DASH, 0.2F, 0.0F,
						0.2F, 0.36F, 1.2F, "Tool_R", DarkSouls.rl("biped/combat/thrusting_sword_th_da"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.2F, Side.SERVER, ModSoundEvents.SWORD_THRUST) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STUN_TYPE, StunType.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 37)
								.addProperty(AttackProperty.STAMINA_USAGE, 24)
								.addProperty(AttackProperty.POISE_DAMAGE, 15),

				// Greatsword
				new AttackAnimation.Builder(DarkSouls.rl("greatsword_light_attack_1"), AttackType.LIGHT, 0.3F, 0.0F,
						0.44F, 0.68F, 2.0F, "Tool_R", DarkSouls.rl("biped/combat/greatsword_light_attack_1"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.44F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 30)
								.addProperty(AttackProperty.STAMINA_USAGE, 25)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),
				new AttackAnimation.Builder(DarkSouls.rl("greatsword_light_attack_2"), AttackType.LIGHT, 0.3F, 0.0F,
						0.32F, 0.68F, 1.8F, "Tool_R", DarkSouls.rl("biped/combat/greatsword_light_attack_2"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.44F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 30)
								.addProperty(AttackProperty.STAMINA_USAGE, 25)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),

				new AttackAnimation.Builder(DarkSouls.rl("greatsword_thrust"), AttackType.HEAVY, 0.3F, 0.0F, 0.28F,
						0.68F, 1.6F, "Tool_R", DarkSouls.rl("biped/combat/greatsword_heavy_attack"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.28F, Side.SERVER, ModSoundEvents.SWORD_THRUST) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 36)
								.addProperty(AttackProperty.STAMINA_USAGE, 35)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),
				new AttackAnimation.Builder(DarkSouls.rl("greatsword_upward_slash"), AttackType.HEAVY, 0.3F, 0.0F,
						0.28F, 0.56F, 2.4F, "Tool_R", DarkSouls.rl("biped/combat/greatsword_upward_slash"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.28F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 36)
								.addProperty(AttackProperty.STAMINA_USAGE, 35)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),
				new AttackAnimation.Builder(DarkSouls.rl("greatsword_stylish_thrust"), AttackType.HEAVY, 0.3F, 0.0F,
						0.64F, 0.76F, 2.8F, "Tool_R", DarkSouls.rl("biped/combat/greatsword_stylish_thrust"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.64F, Side.SERVER, ModSoundEvents.SWORD_THRUST) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 36)
								.addProperty(AttackProperty.STAMINA_USAGE, 35)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),
				new AttackAnimation.Builder(DarkSouls.rl("greatsword_dash_attack"), AttackType.DASH, 0.1F, 0.0F, 0.48F,
						0.84F, 1.6F, "Tool_R", DarkSouls.rl("biped/combat/greatsword_dash_attack"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.4F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 30)
								.addProperty(AttackProperty.STAMINA_USAGE, 30)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),

				new AttackAnimation.Builder(DarkSouls.rl("greatsword_th_la_1"), AttackType.TWO_HANDED_LIGHT, 0.5F, 0.0F,
						0.2F, 0.44F, 1.6F, "Tool_R", DarkSouls.rl("biped/combat/greatsword_th_la_1"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.2F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 35)
								.addProperty(AttackProperty.STAMINA_USAGE, 25)
								.addProperty(AttackProperty.POISE_DAMAGE, 26),
				new AttackAnimation.Builder(DarkSouls.rl("greatsword_th_la_2"), AttackType.TWO_HANDED_LIGHT, 0.3F, 0.0F,
						0.28F, 0.48F, 2.0F, "Tool_R", DarkSouls.rl("biped/combat/greatsword_th_la_2"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.28F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 35)
								.addProperty(AttackProperty.STAMINA_USAGE, 25)
								.addProperty(AttackProperty.POISE_DAMAGE, 26),

				new AttackAnimation.Builder(DarkSouls.rl("greatsword_th_ha_1"), AttackType.TWO_HANDED_HEAVY, 0.5F, 0.0F,
						0.4F, 0.52F, 1.6F, "Tool_R", DarkSouls.rl("biped/combat/greatsword_th_ha_1"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.4F, Side.SERVER, ModSoundEvents.SWORD_THRUST) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 42)
								.addProperty(AttackProperty.STAMINA_USAGE, 35)
								.addProperty(AttackProperty.POISE_DAMAGE, 26),
				new AttackAnimation.Builder(DarkSouls.rl("greatsword_th_ha_2"), AttackType.TWO_HANDED_HEAVY, 0.3F, 0.0F,
						0.28F, 0.48F, 2.0F, "Tool_R", DarkSouls.rl("biped/combat/greatsword_th_la_2"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.28F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 42)
								.addProperty(AttackProperty.STAMINA_USAGE, 35)
								.addProperty(AttackProperty.POISE_DAMAGE, 26),

				new AttackAnimation.Builder(DarkSouls.rl("greatsword_th_da"), AttackType.TWO_HANDED_DASH, 0.05F, 0.0F,
						0.4F, 0.52F, 1.6F, "Tool_R", DarkSouls.rl("biped/combat/greatsword_th_ha_1"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.4F, Side.SERVER, ModSoundEvents.SWORD_THRUST) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 40)
								.addProperty(AttackProperty.STAMINA_USAGE, 30)
								.addProperty(AttackProperty.POISE_DAMAGE, 25),

				// Ultra Greatsword
				new AttackAnimation.Builder(DarkSouls.rl("ultra_greatsword_light_attack_1"), AttackType.LIGHT, 0.3F,
						0.0F, 0.48F, 0.88F, 2.8F, "Tool_R",
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
				new AttackAnimation.Builder(DarkSouls.rl("ultra_greatsword_light_attack_2"), AttackType.LIGHT, 0.2F,
						0.0F, 0.44F, 0.88F, 2.4F, "Tool_R",
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

				new AttackAnimation.Builder(DarkSouls.rl("ultra_greatsword_heavy_attack_1"), AttackType.HEAVY, 0.5F,
						0.0F, 1.35F, 1.6F, 3.45F, "Tool_R",
						DarkSouls.rl("biped/combat/ultra_greatsword_heavy_attack_1"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_SWORD.appendTo(1.3F, new AnimEvent[]
										{})).addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 70)
								.addProperty(AttackProperty.STAMINA_USAGE, 40)
								.addProperty(AttackProperty.POISE_DAMAGE, 36),
				new AttackAnimation.Builder(DarkSouls.rl("ultra_greatsword_heavy_attack_2"), AttackType.HEAVY, 0.5F,
						0.0F, 1.35F, 1.6F, 3.45F, "Tool_R",
						DarkSouls.rl("biped/combat/ultra_greatsword_heavy_attack_2"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_SWORD.appendTo(1.3F, new AnimEvent[]
										{})).addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 70)
								.addProperty(AttackProperty.STAMINA_USAGE, 40)
								.addProperty(AttackProperty.POISE_DAMAGE, 36),

				new AttackAnimation.Builder(DarkSouls.rl("ultra_greatsword_dash_attack"), AttackType.DASH, 0.1F, 0.0F, 0.68F, 0.96F,
						2.0F, "Tool_R", DarkSouls.rl("biped/combat/greataxe_da"),
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

				new AttackAnimation.Builder(DarkSouls.rl("ultra_greatsword_th_la_1"), AttackType.TWO_HANDED_LIGHT, 0.3F,
						0.0F, 0.48F, 0.88F, 2.8F, "Tool_R", DarkSouls.rl("biped/combat/ultra_greatsword_th_la_1"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_SWORD.appendTo(0.7F, new AnimEvent[]
										{}))
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 75)
								.addProperty(AttackProperty.STAMINA_USAGE, 30)
								.addProperty(AttackProperty.POISE_DAMAGE, 32),
				new AttackAnimation.Builder(DarkSouls.rl("ultra_greatsword_th_la_2"), AttackType.TWO_HANDED_LIGHT, 0.2F,
						0.0F, 0.44F, 0.88F, 2.4F, "Tool_R", DarkSouls.rl("biped/combat/ultra_greatsword_th_la_2"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_SWORD.appendTo(0.72F, new AnimEvent[]
										{}))
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 75)
								.addProperty(AttackProperty.STAMINA_USAGE, 30)
								.addProperty(AttackProperty.POISE_DAMAGE, 32),

				new AttackAnimation.Builder(DarkSouls.rl("ultra_greatsword_th_ha_1"), AttackType.TWO_HANDED_HEAVY, 0.5F,
						0.0F, 1.35F, 1.6F, 3.45F, "Tool_R", DarkSouls.rl("biped/combat/ultra_greatsword_th_ha_1"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_SWORD.appendTo(1.3F, new AnimEvent[]
										{})).addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 76)
								.addProperty(AttackProperty.STAMINA_USAGE, 40)
								.addProperty(AttackProperty.POISE_DAMAGE, 40),
				new AttackAnimation.Builder(DarkSouls.rl("ultra_greatsword_th_ha_2"), AttackType.TWO_HANDED_HEAVY, 0.5F,
						0.0F, 1.35F, 1.6F, 3.45F, "Tool_R", DarkSouls.rl("biped/combat/ultra_greatsword_th_ha_2"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_SWORD.appendTo(1.3F, new AnimEvent[]
										{})).addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 76)
								.addProperty(AttackProperty.STAMINA_USAGE, 40)
								.addProperty(AttackProperty.POISE_DAMAGE, 40),

				new AttackAnimation.Builder(DarkSouls.rl("ultra_greatsword_th_da"), AttackType.TWO_HANDED_DASH, 0.1F,
						0.0F, 1.35F, 1.6F, 3.45F, "Tool_R", DarkSouls.rl("biped/combat/ultra_greatsword_th_ha_1"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_SWORD.appendTo(1.6F, new AnimEvent[]
										{})).addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 76)
								.addProperty(AttackProperty.STAMINA_USAGE, 35)
								.addProperty(AttackProperty.POISE_DAMAGE, 40),

				// Greataxe
				new AttackAnimation.Builder(DarkSouls.rl("greataxe_la_1"), AttackType.LIGHT, 0.3F, 0.0F, 0.64F, 0.92F,
						2.0F, "Tool_R", DarkSouls.rl("biped/combat/greataxe_la_1"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_HAMMER.appendTo(0.92F, new AnimEvent[]
										{})).addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 75)
								.addProperty(AttackProperty.STAMINA_USAGE, 30)
								.addProperty(AttackProperty.POISE_DAMAGE, 30),
				new AttackAnimation.Builder(DarkSouls.rl("greataxe_la_2"), AttackType.LIGHT, 0.3F, 0.0F, 0.76F, 1.0F,
						2.0F, "Tool_R", DarkSouls.rl("biped/combat/greataxe_la_2"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_HAMMER.appendTo(1.0F, new AnimEvent[]
										{})).addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 75)
								.addProperty(AttackProperty.STAMINA_USAGE, 30)
								.addProperty(AttackProperty.POISE_DAMAGE, 30),

				new AttackAnimation.Builder(DarkSouls.rl("greataxe_ha"), AttackType.HEAVY, 0.3F, 0.0F, 1.08F, 1.28F,
						2.2F, "Tool_R", DarkSouls.rl("biped/combat/greataxe_ha"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_HAMMER.appendTo(1.28F, new AnimEvent[]
										{})).addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 75)
								.addProperty(AttackProperty.STAMINA_USAGE, 40)
								.addProperty(AttackProperty.POISE_DAMAGE, 30),
				new AttackAnimation.Builder(DarkSouls.rl("greataxe_da"), AttackType.DASH, 0.1F, 0.0F, 0.68F, 0.96F,
						2.0F, "Tool_R", DarkSouls.rl("biped/combat/greataxe_da"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_HAMMER.appendTo(0.96F, new AnimEvent[]
										{})).addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 75)
								.addProperty(AttackProperty.STAMINA_USAGE, 35)
								.addProperty(AttackProperty.POISE_DAMAGE, 30),

				new AttackAnimation.Builder(DarkSouls.rl("greataxe_th_la_1"), AttackType.TWO_HANDED_LIGHT, 0.3F, 0.0F,
						0.8F, 0.92F, 2.2F, "Tool_R", DarkSouls.rl("biped/combat/greataxe_th_la_1"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_HAMMER.appendTo(0.92F, new AnimEvent[]
										{})).addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 75)
								.addProperty(AttackProperty.STAMINA_USAGE, 30)
								.addProperty(AttackProperty.POISE_DAMAGE, 30),
				new AttackAnimation.Builder(DarkSouls.rl("greataxe_th_la_2"), AttackType.TWO_HANDED_LIGHT, 0.3F, 0.0F,
						0.56F, 0.88F, 2.2F, "Tool_R", DarkSouls.rl("biped/combat/greataxe_th_la_2"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_HAMMER.appendTo(0.88F, new AnimEvent[]
										{})).addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 75)
								.addProperty(AttackProperty.STAMINA_USAGE, 30)
								.addProperty(AttackProperty.POISE_DAMAGE, 30),

				new AttackAnimation.Builder(DarkSouls.rl("greataxe_th_ha"), AttackType.TWO_HANDED_HEAVY, 0.3F, 0.0F,
						0.92F, 1.2F, 2.4F, "Tool_R", DarkSouls.rl("biped/combat/greataxe_th_ha"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_HAMMER.appendTo(1.2F, new AnimEvent[]
										{})).addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 75)
								.addProperty(AttackProperty.STAMINA_USAGE, 40)
								.addProperty(AttackProperty.POISE_DAMAGE, 30),
				new AttackAnimation.Builder(DarkSouls.rl("greataxe_th_da"), AttackType.TWO_HANDED_DASH, 0.1F, 0.0F,
						0.68F, 0.96F, 2.0F, "Tool_R", DarkSouls.rl("biped/combat/greataxe_th_da"),
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
				new AttackAnimation.Builder(DarkSouls.rl("spear_dash_attack"), AttackType.DASH, 0.2F, 0.0F, 0.4F, 0.52F,
						1.08F, "Tool_R", DarkSouls.rl("biped/combat/spear_dash_attack"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.4F, Side.SERVER, ModSoundEvents.SPEAR_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 40)
								.addProperty(AttackProperty.STAMINA_USAGE, 20)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(DarkSouls.rl("spear_heavy_attack"), AttackType.HEAVY, 0.35F, 0.0F, 0.65F,
						0.8F, 1.75F, "Tool_R", DarkSouls.rl("biped/combat/spear_heavy_attack"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.65F, Side.SERVER, ModSoundEvents.SPEAR_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 40)
								.addProperty(AttackProperty.STAMINA_USAGE, 25)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(DarkSouls.rl("spear_light_attack"), AttackType.LIGHT, 0.15F, 0.0F, 0.32F,
						0.6F, 1.5F, "Tool_R", DarkSouls.rl("biped/combat/spear_light_attack"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.32F, Side.SERVER, ModSoundEvents.SPEAR_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 40)
								.addProperty(AttackProperty.STAMINA_USAGE, 15)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(DarkSouls.rl("spear_light_blocking_attack"), AttackType.LIGHT, 0.2F, 0.0F,
						0.35F, 0.5F, 1.25F, "Tool_R", DarkSouls.rl("biped/combat/spear_light_blocking_attack"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.35F, Side.SERVER, ModSoundEvents.SPEAR_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.BLOCKING, true)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 40)
								.addProperty(AttackProperty.STAMINA_USAGE, 15)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(DarkSouls.rl("spear_th_la"), AttackType.TWO_HANDED_LIGHT, 0.15F, 0.0F, 0.4F,
						0.64F, 1.2F, "Tool_R", DarkSouls.rl("biped/combat/spear_th_la"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.4F, Side.SERVER, ModSoundEvents.SPEAR_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 42)
								.addProperty(AttackProperty.STAMINA_USAGE, 15)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),
				new AttackAnimation.Builder(DarkSouls.rl("spear_th_ha"), AttackType.TWO_HANDED_HEAVY, 0.35F, 0.0F, 0.6F,
						0.8F, 1.6F, "Tool_R", DarkSouls.rl("biped/combat/spear_th_ha"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.6F, Side.SERVER, ModSoundEvents.SPEAR_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.STAMINA_USAGE, 25)
								.addProperty(AttackProperty.POISE_DAMAGE, 26),
				new AttackAnimation.Builder(DarkSouls.rl("spear_th_da"), AttackType.TWO_HANDED_DASH, 0.2F, 0.0F, 0.36F,
						0.55F, 1.2F, "Tool_R", DarkSouls.rl("biped/combat/spear_th_da"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.36F, Side.SERVER, ModSoundEvents.SPEAR_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.STAMINA_USAGE, 20)
								.addProperty(AttackProperty.POISE_DAMAGE, 24),

				// Dagger
				new AttackAnimation.Builder(DarkSouls.rl("dagger_heavy_attack"), AttackType.HEAVY, 0.2F, 0.0F, 0.68F,
						0.96F, 1.6F, "Tool_R", DarkSouls.rl("biped/combat/dagger_heavy_attack"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT),

				new AttackAnimation.Builder(DarkSouls.rl("dagger_light_attack_1"), AttackType.LIGHT, 0.2F, 0.0F, 0.16F,
						0.4F, 1.0F, "Tool_R", DarkSouls.rl("biped/combat/dagger_light_attack_1"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.SLASH)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT),
				new AttackAnimation.Builder(DarkSouls.rl("dagger_light_attack_2"), AttackType.LIGHT, 0.2F, 0.0F, 0.04F,
						0.32F, 1.0F, "Tool_R", DarkSouls.rl("biped/combat/dagger_light_attack_2"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.SLASH)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT),

				// Great Hammer
				new AttackAnimation.Builder(DarkSouls.rl("great_hammer_heavy_attack"), AttackType.HEAVY, 0.5F, 0.0F,
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

				new AttackAnimation.Builder(DarkSouls.rl("great_hammer_light_attack_1"), AttackType.LIGHT, 0.5F, 0.0F,
						0.84F, 1.38F, 2.76F, "Tool_R", DarkSouls.rl("biped/combat/great_hammer_light_attack_1"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_HAMMER.appendTo(1.3F, new AnimEvent[]
										{})).addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 80)
								.addProperty(AttackProperty.STAMINA_USAGE, 30)
								.addProperty(AttackProperty.POISE_DAMAGE, 28),
				new AttackAnimation.Builder(DarkSouls.rl("great_hammer_light_attack_2"), AttackType.LIGHT, 0.5F, 0.0F,
						1.15F, 1.7F, 3.45F, "Tool_R", DarkSouls.rl("biped/combat/great_hammer_light_attack_2"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_HAMMER.appendTo(1.3F, new AnimEvent[]
										{})).addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 80)
								.addProperty(AttackProperty.STAMINA_USAGE, 30)
								.addProperty(AttackProperty.POISE_DAMAGE, 28),

				new AttackAnimation.Builder(DarkSouls.rl("great_hammer_dash_attack"), AttackType.DASH, 0.1F, 0.0F, 0.68F, 0.96F,
						2.0F, "Tool_R", DarkSouls.rl("biped/combat/greataxe_da"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_HAMMER.appendTo(0.93F, new AnimEvent[]
										{})).addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 80)
								.addProperty(AttackProperty.STAMINA_USAGE, 35)
								.addProperty(AttackProperty.POISE_DAMAGE, 28),

				new AttackAnimation.Builder(DarkSouls.rl("great_hammer_th_la_1"), AttackType.LIGHT, 0.5F, 0.0F, 0.32F,
						0.56F, 1.6F, "Tool_R", DarkSouls.rl("biped/combat/great_hammer_th_la_1"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_HAMMER.appendTo(0.56F, new AnimEvent[]
										{})).addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 90)
								.addProperty(AttackProperty.STAMINA_USAGE, 30)
								.addProperty(AttackProperty.POISE_DAMAGE, 36),
				new AttackAnimation.Builder(DarkSouls.rl("great_hammer_th_la_2"), AttackType.LIGHT, 0.5F, 0.0F, 0.24F,
						0.48F, 1.2F, "Tool_R", DarkSouls.rl("biped/combat/great_hammer_th_la_2"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_HAMMER.appendTo(0.48F, new AnimEvent[]
										{})).addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 90)
								.addProperty(AttackProperty.STAMINA_USAGE, 30)
								.addProperty(AttackProperty.POISE_DAMAGE, 36),

				new AttackAnimation.Builder(DarkSouls.rl("great_hammer_th_ha"), AttackType.HEAVY, 0.5F, 0.0F, 0.32F,
						0.52F, 1.2F, "Tool_R", DarkSouls.rl("biped/combat/great_hammer_th_ha"),
						(models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_HAMMER.appendTo(0.52F, new AnimEvent[]
										{})).addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 95)
								.addProperty(AttackProperty.STAMINA_USAGE, 40)
								.addProperty(AttackProperty.POISE_DAMAGE, 39),
				new AttackAnimation.Builder(DarkSouls.rl("great_hammer_th_da"), AttackType.LIGHT, 0.5F, 0.0F, 0.32F,
						0.56F, 1.6F, "Tool_R", DarkSouls.rl("biped/combat/great_hammer_th_la_1"),
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
				new AttackAnimation.Builder(DarkSouls.rl("axe_heavy_attack"), AttackType.HEAVY, 0.3F, 0.0F, 0.55F, 0.7F,
						1.5F, "Tool_R", DarkSouls.rl("biped/combat/axe_heavy_attack"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.4F, Side.SERVER, ModSoundEvents.AXE_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
								.addProperty(AttackProperty.STAMINA_USAGE, 30)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),

				new AttackAnimation.Builder(DarkSouls.rl("axe_light_attack_1"), AttackType.LIGHT, 0.3F, 0.0F, 0.2F,
						0.35F, 1.5F, "Tool_R", DarkSouls.rl("biped/combat/axe_light_attack_1"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.16F, Side.SERVER, ModSoundEvents.AXE_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
								.addProperty(AttackProperty.STAMINA_USAGE, 20)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),
				new AttackAnimation.Builder(DarkSouls.rl("axe_light_attack_2"), AttackType.LIGHT, 0.2F, 0.0F, 0.15F,
						0.4F, 1.25F, "Tool_R", DarkSouls.rl("biped/combat/axe_light_attack_2"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.12F, Side.SERVER, ModSoundEvents.AXE_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
								.addProperty(AttackProperty.STAMINA_USAGE, 20)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),

				new AttackAnimation.Builder(DarkSouls.rl("axe_dash_attack"), AttackType.DASH, 0.2F, 0.0F, 0.4F, 0.6F,
						1.35F, "Tool_R", DarkSouls.rl("biped/combat/axe_dash_attack"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.35F, Side.SERVER, ModSoundEvents.AXE_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
								.addProperty(AttackProperty.STAMINA_USAGE, 25)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),

				new AttackAnimation.Builder(DarkSouls.rl("axe_th_la_1"), AttackType.TWO_HANDED_LIGHT, 0.4F, 0.0F, 0.08F,
						0.4F, 1.2F, "Tool_R", DarkSouls.rl("biped/combat/axe_th_la_1"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.08F, Side.SERVER, ModSoundEvents.AXE_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
								.addProperty(AttackProperty.STAMINA_USAGE, 20)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),
				new AttackAnimation.Builder(DarkSouls.rl("axe_th_la_2"), AttackType.TWO_HANDED_LIGHT, 0.4F, 0.0F, 0.08F,
						0.45F, 1.25F, "Tool_R", DarkSouls.rl("biped/combat/axe_th_la_2"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.08F, Side.SERVER, ModSoundEvents.AXE_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
								.addProperty(AttackProperty.STAMINA_USAGE, 20)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),

				new AttackAnimation.Builder(DarkSouls.rl("axe_th_ha"), AttackType.TWO_HANDED_HEAVY, 0.4F, 0.0F, 0.32F,
						0.56F, 1.6F, "Tool_R", DarkSouls.rl("biped/combat/axe_th_ha"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.32F, Side.SERVER, ModSoundEvents.AXE_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
								.addProperty(AttackProperty.STAMINA_USAGE, 30)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),
				new AttackAnimation.Builder(DarkSouls.rl("axe_th_da"), AttackType.TWO_HANDED_DASH, 0.1F, 0.0F, 0.48F,
						0.68F, 1.6F, "Tool_R", DarkSouls.rl("biped/combat/axe_th_da"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.48F, Side.SERVER, ModSoundEvents.AXE_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 80)
								.addProperty(AttackProperty.STAMINA_USAGE, 25)
								.addProperty(AttackProperty.POISE_DAMAGE, 28),

				// Hammer
				new AttackAnimation.Builder(DarkSouls.rl("hammer_dash_attack"), AttackType.DASH, 0.2F, 0.0F, 0.4F, 0.6F,
						1.35F, "Tool_R", DarkSouls.rl("biped/combat/axe_dash_attack"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.32F, Side.SERVER, ModSoundEvents.AXE_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 56)
								.addProperty(AttackProperty.STAMINA_USAGE, 25)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),
				new AttackAnimation.Builder(DarkSouls.rl("hammer_heavy_attack"), AttackType.HEAVY, 0.5F, 0.0F, 0.28F,
						0.6F, 1.4F, "Tool_R", DarkSouls.rl("biped/combat/hammer_heavy_attack"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.32F, Side.SERVER, ModSoundEvents.AXE_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 68)
								.addProperty(AttackProperty.STAMINA_USAGE, 30)
								.addProperty(AttackProperty.POISE_DAMAGE, 36),
				new AttackAnimation.Builder(DarkSouls.rl("hammer_light_attack"), AttackType.LIGHT, 0.3F, 0.0F, 0.24F,
						0.6F, 1.2F, "Tool_R", DarkSouls.rl("biped/combat/hammer_light_attack"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.28F, Side.SERVER, ModSoundEvents.AXE_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 56)
								.addProperty(AttackProperty.STAMINA_USAGE, 20)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),
				new AttackAnimation.Builder(DarkSouls.rl("hammer_th_la"), AttackType.TWO_HANDED_LIGHT, 0.3F, 0.0F,
						0.24F, 0.6F, 1.6F, "Tool_R", DarkSouls.rl("biped/combat/hammer_th_la"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.2F, Side.SERVER, ModSoundEvents.AXE_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 63)
								.addProperty(AttackProperty.STAMINA_USAGE, 20)
								.addProperty(AttackProperty.POISE_DAMAGE, 32),
				new AttackAnimation.Builder(DarkSouls.rl("hammer_th_ha"), AttackType.TWO_HANDED_HEAVY, 0.4F, 0.0F,
						0.24F, 0.44F, 1.6F, "Tool_R", DarkSouls.rl("biped/combat/hammer_th_ha"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.24F, Side.SERVER, ModSoundEvents.AXE_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 69)
								.addProperty(AttackProperty.STAMINA_USAGE, 30)
								.addProperty(AttackProperty.POISE_DAMAGE, 47),
				new AttackAnimation.Builder(DarkSouls.rl("hammer_th_da"), AttackType.TWO_HANDED_DASH, 0.2F, 0.0F, 0.24F,
						0.48F, 1.6F, "Tool_R", DarkSouls.rl("biped/combat/hammer_th_la"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.2F, Side.SERVER, ModSoundEvents.AXE_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 63)
								.addProperty(AttackProperty.STAMINA_USAGE, 25)
								.addProperty(AttackProperty.POISE_DAMAGE, 32),

				// Fist
				new AttackAnimation.Builder(DarkSouls.rl("fist_light_attack_1"), AttackType.LIGHT, 0.2F, 0.0F, 0.3F,
						0.4F, 1.25F, "Tool_R", DarkSouls.rl("biped/combat/fist_light_attack_1"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.3F, Side.SERVER, ModSoundEvents.FIST_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 28)
								.addProperty(AttackProperty.STAMINA_USAGE, 18)
								.addProperty(AttackProperty.POISE_DAMAGE, 15),
				new AttackAnimation.Builder(DarkSouls.rl("fist_light_attack_2"), AttackType.LIGHT, 0.2F, 0.0F, 0.15F,
						0.3F, 1.25F, "Tool_R", DarkSouls.rl("biped/combat/fist_light_attack_2"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.15F, Side.SERVER, ModSoundEvents.FIST_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 28)
								.addProperty(AttackProperty.STAMINA_USAGE, 18)
								.addProperty(AttackProperty.POISE_DAMAGE, 15),

				new AttackAnimation.Builder(DarkSouls.rl("fist_dash_attack"), AttackType.DASH, 0.3F, 0.0F, 0.15F, 0.3F,
						1.0F, "Tool_R", DarkSouls.rl("biped/combat/fist_dash_attack"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.15F, Side.SERVER, ModSoundEvents.FIST_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 28)
								.addProperty(AttackProperty.STAMINA_USAGE, 20)
								.addProperty(AttackProperty.POISE_DAMAGE, 15),
				new AttackAnimation.Builder(DarkSouls.rl("fist_heavy_attack"), AttackType.HEAVY, 0.5F, 0.0F, 0.35F,
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
				new AttackAnimation.Builder(DarkSouls.rl("shield_la"), AttackType.LIGHT, 0.2F, 0.0F, 0.4F, 0.56F, 1.2F,
						"Tool_R", DarkSouls.rl("biped/combat/shield_la"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.4F, Side.SERVER, ModSoundEvents.FIST_SWING) })
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.STRIKE)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 45)
								.addProperty(AttackProperty.STAMINA_USAGE, 22)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),

				new AttackAnimation.Builder(DarkSouls.rl("shield_ha_1"), AttackType.HEAVY, 0.2F, 0.0F, 0.44F, 0.56F,
						1.4F, "Tool_R", DarkSouls.rl("biped/combat/shield_ha_1"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.44F, Side.SERVER, ModSoundEvents.FIST_SWING) })
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 51)
								.addProperty(AttackProperty.STAMINA_USAGE, 28)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(DarkSouls.rl("shield_ha_2"), AttackType.HEAVY, 0.2F, 0.0F, 0.28F, 0.48F,
						1.4F, "Tool_R", DarkSouls.rl("biped/combat/shield_ha_2"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.28F, Side.SERVER, ModSoundEvents.FIST_SWING) })
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 51)
								.addProperty(AttackProperty.STAMINA_USAGE, 28)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),

				new AttackAnimation.Builder(DarkSouls.rl("shield_da"), AttackType.DASH, 0.05F, 0.0F, 0.32F, 0.52F, 1.4F,
						"Tool_R", DarkSouls.rl("biped/combat/shield_da"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.32F, Side.SERVER, ModSoundEvents.FIST_SWING) })
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 49)
								.addProperty(AttackProperty.STAMINA_USAGE, 25)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(DarkSouls.rl("shield_th_la"), AttackType.TWO_HANDED_LIGHT, 0.3F, 0.0F,
						0.12F, 0.32F, 1.2F, "Tool_R", DarkSouls.rl("biped/combat/shield_th_la"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.12F, Side.SERVER, ModSoundEvents.FIST_SWING) })
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 48)
								.addProperty(AttackProperty.STAMINA_USAGE, 26)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),

				new AttackAnimation.Builder(DarkSouls.rl("shield_th_ha_1"), AttackType.TWO_HANDED_HEAVY, 0.2F, 0.0F,
						0.28F, 0.48F, 1.2F, "Tool_R", DarkSouls.rl("biped/combat/shield_th_ha_1"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.28F, Side.SERVER, ModSoundEvents.FIST_SWING) })
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 54)
								.addProperty(AttackProperty.STAMINA_USAGE, 30)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(DarkSouls.rl("shield_th_ha_2"), AttackType.TWO_HANDED_HEAVY, 0.2F, 0.0F,
						0.12F, 0.32F, 1.2F, "Tool_R", DarkSouls.rl("biped/combat/shield_th_ha_2"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.12F, Side.SERVER, ModSoundEvents.FIST_SWING) })
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 54)
								.addProperty(AttackProperty.STAMINA_USAGE, 30)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),

				new AttackAnimation.Builder(DarkSouls.rl("shield_th_da"), AttackType.TWO_HANDED_DASH, 0.2F, 0.0F, 0.12F,
						0.32F, 1.4F, "Tool_R", DarkSouls.rl("biped/combat/shield_th_da"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.12F, Side.SERVER, ModSoundEvents.FIST_SWING) })
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 52)
								.addProperty(AttackProperty.STAMINA_USAGE, 25)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),

				// Greatshield
				new AttackAnimation.Builder(DarkSouls.rl("greatshield_light_attack"), AttackType.LIGHT, 0.2F, 0.0F,
						0.36F, 0.75F, 1.2F, "Tool_R", DarkSouls.rl("biped/combat/greatshield_la"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.36F, Side.SERVER, ModSoundEvents.FIST_SWING) })
								.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 70)
								.addProperty(AttackProperty.STAMINA_USAGE, 35)
								.addProperty(AttackProperty.POISE_DAMAGE, 30),
				new AttackAnimation.Builder(DarkSouls.rl("greatshield_heavy_attack"), AttackType.HEAVY, 0.2F, 0.0F,
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
				new AttackAnimation.Builder(DarkSouls.rl("greatshield_dash_attack"), AttackType.DASH, 0.05F, 0.0F,
						0.48F, 0.85F, 1.32F, "Tool_R", DarkSouls.rl("biped/combat/greatshield_da"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.48F, Side.SERVER, ModSoundEvents.FIST_SWING) })
								.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 70)
								.addProperty(AttackProperty.STAMINA_USAGE, 35)
								.addProperty(AttackProperty.POISE_DAMAGE, 30),
				new AttackAnimation.Builder(DarkSouls.rl("greatshield_th_light_attack"), AttackType.TWO_HANDED_LIGHT,
						0.2F, 0.0F, 0.36F, 0.75F, 1.2F, "Tool_R", DarkSouls.rl("biped/combat/greatshield_th_la"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.36F, Side.SERVER, ModSoundEvents.FIST_SWING) })
								.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 70)
								.addProperty(AttackProperty.STAMINA_USAGE, 30)
								.addProperty(AttackProperty.POISE_DAMAGE, 30),
				new AttackAnimation.Builder(DarkSouls.rl("greatshield_th_heavy_attack"), AttackType.TWO_HANDED_HEAVY,
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
				new AttackAnimation.Builder(DarkSouls.rl("greatshield_th_dash_attack"), AttackType.TWO_HANDED_DASH,
						0.05F, 0.0F, 0.48F, 0.85F, 1.32F, "Tool_R", DarkSouls.rl("biped/combat/greatshield_th_da"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.48F, Side.SERVER, ModSoundEvents.FIST_SWING) })
								.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 70)
								.addProperty(AttackProperty.STAMINA_USAGE, 35)
								.addProperty(AttackProperty.POISE_DAMAGE, 30),
				new AttackAnimation.Builder(DarkSouls.rl("greatshield_bash"), AttackType.LIGHT, 0.2F, 0.0F, 0.36F,
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
				new AttackAnimation.Builder(DarkSouls.rl("straight_sword_light_attack_1"), AttackType.LIGHT, 0.2F, 0.0F,
						0.24F, 0.4F, 0.88F, "Tool_R", DarkSouls.rl("biped/combat/straight_sword_light_attack_1"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.24F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.STAMINA_USAGE, 15)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(DarkSouls.rl("straight_sword_light_attack_2"), AttackType.LIGHT, 0.2F, 0.0F,
						0.08F, 0.3F, 0.8F, "Tool_R", DarkSouls.rl("biped/combat/straight_sword_light_attack_2"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.08F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.STAMINA_USAGE, 15)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),

				new AttackAnimation.Builder(DarkSouls.rl("straight_sword_heavy_attack"), AttackType.HEAVY, 0.2F, 0.0F,
						0.36F, 0.6F, 1.0F, "Tool_R", DarkSouls.rl("biped/combat/straight_sword_heavy_attack"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.36F, Side.SERVER, ModSoundEvents.SWORD_THRUST) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.SLASH)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.STAMINA_USAGE, 25)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(DarkSouls.rl("straight_sword_dash_attack"), AttackType.DASH, 0.2F, 0.0F,
						0.16F, 0.36F, 0.8F, "Tool_R", DarkSouls.rl("biped/combat/straight_sword_dash_attack"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.16F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.STAMINA_USAGE, 20)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),

				new AttackAnimation.Builder(DarkSouls.rl("straight_sword_th_la_1"), AttackType.TWO_HANDED_LIGHT, 0.1F,
						0.0F, 0.36F, 0.6F, 1.52F, "Tool_R", DarkSouls.rl("biped/combat/straight_sword_th_la_1"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.36F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.STAMINA_USAGE, 15)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(DarkSouls.rl("straight_sword_th_la_2"), AttackType.TWO_HANDED_LIGHT, 0.1F,
						0.0F, 0.42F, 0.48F, 1.6F, "Tool_R", DarkSouls.rl("biped/combat/straight_sword_th_la_2"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.36F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.STAMINA_USAGE, 15)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),

				new AttackAnimation.Builder(DarkSouls.rl("straight_sword_th_ha"), AttackType.TWO_HANDED_HEAVY, 0.2F,
						0.0F, 0.36F, 0.56F, 1.2F, "Tool_R", DarkSouls.rl("biped/combat/straight_sword_th_ha_1"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.36F, Side.SERVER, ModSoundEvents.SWORD_THRUST) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.STAMINA_USAGE, 25)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(DarkSouls.rl("straight_sword_th_da"), AttackType.TWO_HANDED_DASH, 0.2F,
						0.0F, 0.48F, 0.72F, 1.6F, "Tool_R", DarkSouls.rl("biped/combat/straight_sword_th_da"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.48F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.STAMINA_USAGE, 20)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),

				// Hollow
				new StaticAnimation.Builder(DarkSouls.rl("hollow_idle"), 0.2F, true, DarkSouls.rl("hollow/idle"),
						(models) -> models.ENTITY_BIPED),
				new MovementAnimation.Builder(DarkSouls.rl("hollow_walk"), 0.2F, true, DarkSouls.rl("hollow/move"),
						(models) -> models.ENTITY_BIPED),
				new MovementAnimation.Builder(DarkSouls.rl("hollow_run"), 0.2F, true, DarkSouls.rl("hollow/run"),
						(models) -> models.ENTITY_BIPED),
				new ActionAnimation.Builder(DarkSouls.rl("hollow_deflected"), 0.2F, DarkSouls.rl("hollow/deflected"),
						(models) -> models.ENTITY_BIPED).addProperty(ActionAnimationProperty.IS_HIT, true),
				new StaticAnimation.Builder(DarkSouls.rl("hollow_breakdown"), 0.2F, true,
						DarkSouls.rl("hollow/breakdown"), (models) -> models.ENTITY_BIPED),

				new AttackAnimation.Builder(DarkSouls.rl("hollow_light_attack_1"), AttackType.LIGHT, 0.2F, 0.0F, 0.56F,
						1.05F, 2.5F, "Tool_R", DarkSouls.rl("hollow/swing_1"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.56F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(DarkSouls.rl("hollow_light_attack_2"), AttackType.LIGHT, 0.2F, 0.0F, 0.48F,
						1.0F, 2.0F, "Tool_R", DarkSouls.rl("hollow/swing_2"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.48F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(DarkSouls.rl("hollow_light_attack_3"), AttackType.LIGHT, 0.2F, 0.0F, 0.16F,
						0.4F, 2.0F, "Tool_R", DarkSouls.rl("hollow/swing_3"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.16F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),

				new AttackAnimation.Builder(DarkSouls.rl("hollow_barrage"), AttackType.LIGHT, 0.2F,
						DarkSouls.rl("hollow/fury_attack"), (models) -> models.ENTITY_BIPED,
						new Phase(0.0F, 1.48F, 1.72F, 1.72F, "Tool_R", Colliders.BROKEN_SWORD),
						new Phase(1.72F, 1.8F, 1.92F, 1.92F, "Tool_R", Colliders.BROKEN_SWORD),
						new Phase(1.92F, 2.12F, 2.24F, 2.24F, "Tool_R", Colliders.BROKEN_SWORD),
						new Phase(2.24F, 2.4F, 2.56F, 2.56F, "Tool_R", Colliders.BROKEN_SWORD),
						new Phase(2.56F, 2.76F, 2.88F, 2.88F, "Tool_R", Colliders.BROKEN_SWORD),
						new Phase(2.88F, 3.08F, 3.2F, 4.2F, "Tool_R", Colliders.BROKEN_SWORD))
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
				new AttackAnimation.Builder(DarkSouls.rl("hollow_overhead_swing"), AttackType.HEAVY, 0.2F, 0.0F, 0.4F,
						0.6F, 1.2F, "Tool_R", DarkSouls.rl("hollow/overhead_swing"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.4F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(DarkSouls.rl("hollow_jump_attack"), AttackType.DASH, 0.05F, 0.0F, 0.52F,
						0.72F, 1.6F, "Tool_R", DarkSouls.rl("hollow/jump_attack"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.52F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),

				// Hollow Lordran Warrior
				new MovementAnimation.Builder(DarkSouls.rl("hollow_lordran_warrior_walk"), 0.2F, true,
						DarkSouls.rl("hollow_lordran_warrior/move"), (models) -> models.ENTITY_BIPED),
				new MovementAnimation.Builder(DarkSouls.rl("hollow_lordran_warrior_run"), 0.2F, true,
						DarkSouls.rl("hollow_lordran_warrior/run"), (models) -> models.ENTITY_BIPED),

				new AttackAnimation.Builder(DarkSouls.rl("hollow_lordran_warrior_th_la_1"), AttackType.LIGHT, 0.2F,
						0.0F, 0.68F, 1.08F, 1.6F, "Tool_R", DarkSouls.rl("hollow_lordran_warrior/sword_th_la_1"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.68F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.SLASH)
								.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(DarkSouls.rl("hollow_lordran_warrior_th_la_2"), AttackType.LIGHT, 0.2F,
						0.0F, 0.68F, 1.08F, 1.6F, "Tool_R", DarkSouls.rl("hollow_lordran_warrior/sword_th_la_2"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.68F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.SLASH)
								.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),

				new AttackAnimation.Builder(DarkSouls.rl("hollow_lordran_warrior_dash_attack"), AttackType.DASH, 0.2F,
						0.0F, 0.44F, 0.88F, 1.8F, "Tool_R", DarkSouls.rl("hollow_lordran_warrior/dash_attack"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.44F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.SLASH)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),

				new AttackAnimation.Builder(DarkSouls.rl("hollow_lordran_warrior_axe_la_1"), AttackType.LIGHT, 0.2F,
						0.0F, 0.6F, 1.0F, 2.4F, "Tool_R", DarkSouls.rl("hollow_lordran_warrior/axe_la_1"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.6F, Side.SERVER, ModSoundEvents.AXE_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),
				new AttackAnimation.Builder(DarkSouls.rl("hollow_lordran_warrior_axe_la_2"), AttackType.LIGHT, 0.2F,
						0.0F, 1.12F, 1.36F, 2.8F, "Tool_R", DarkSouls.rl("hollow_lordran_warrior/axe_la_2"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(1.12F, Side.SERVER, ModSoundEvents.AXE_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),

				new AttackAnimation.Builder(DarkSouls.rl("hollow_lordran_warrior_axe_th_la_1"), AttackType.LIGHT, 0.2F,
						0.0F, 0.56F, 1.0F, 2.8F, "Tool_R", DarkSouls.rl("hollow_lordran_warrior/axe_th_la_1"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.56F, Side.SERVER, ModSoundEvents.AXE_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.SLASH)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),
				new AttackAnimation.Builder(DarkSouls.rl("hollow_lordran_warrior_axe_th_la_2"), AttackType.LIGHT, 0.2F,
						0.0F, 0.68F, 1.0F, 2.0F, "Tool_R", DarkSouls.rl("hollow_lordran_warrior/axe_th_la_2"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.68F, Side.SERVER, ModSoundEvents.AXE_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.SLASH)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),

				// Hollow Lordran Soldier
				new MovementAnimation.Builder(DarkSouls.rl("hollow_lordran_soldier_walk"), 0.2F, true,
						DarkSouls.rl("hollow_lordran_soldier/walking"), (models) -> models.ENTITY_BIPED),
				new MovementAnimation.Builder(DarkSouls.rl("hollow_lordran_soldier_run"), 0.2F, true,
						DarkSouls.rl("hollow_lordran_soldier/run"), (models) -> models.ENTITY_BIPED),
				new AdaptableAnimation.Builder(DarkSouls.rl("hollow_lordran_soldier_block"), 0.2F, true,
						(models) -> models.ENTITY_BIPED)
								.addEntry(LivingMotion.BLOCKING, DarkSouls.rl("hollow_lordran_soldier/block"),
										DarkSouls.rl("hollow_lordran_soldier/block"), false)
								.addEntry(LivingMotion.WALKING, DarkSouls.rl("hollow_lordran_soldier/block_walking"),
										DarkSouls.rl("hollow_lordran_soldier/block_walking"), true)
								.addEntry(LivingMotion.RUNNING, DarkSouls.rl("hollow_lordran_soldier/block_run"),
										DarkSouls.rl("hollow_lordran_soldier/block_run"), true),

				new AttackAnimation.Builder(DarkSouls.rl("hollow_lordran_soldier_sword_la_1"), AttackType.LIGHT, 0.2F,
						0.0F, 0.44F, 0.76F, 1.6F, "Tool_R", DarkSouls.rl("hollow_lordran_soldier/sword_la_1"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.44F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),
				new AttackAnimation.Builder(DarkSouls.rl("hollow_lordran_soldier_sword_la_2"), AttackType.LIGHT, 0.2F,
						0.0F, 0.16F, 0.56F, 1.0F, "Tool_R", DarkSouls.rl("hollow_lordran_soldier/sword_la_2"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.16F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),
				new AttackAnimation.Builder(DarkSouls.rl("hollow_lordran_soldier_sword_la_3"), AttackType.LIGHT, 0.2F,
						0.0F, 0.44F, 0.6F, 1.6F, "Tool_R", DarkSouls.rl("hollow_lordran_soldier/sword_la_3"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.44F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),

				new AttackAnimation.Builder(DarkSouls.rl("hollow_lordran_soldier_sword_da"), AttackType.DASH, 0.2F,
						0.0F, 0.35F, 0.5F, 3.0F, "Tool_R", DarkSouls.rl("hollow_lordran_soldier/sword_da"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.35F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),

				new AttackAnimation.Builder(DarkSouls.rl("hollow_lordran_soldier_sword_heavy_thrust"), AttackType.HEAVY,
						0.2F, 0.0F, 1.0F, 1.16F, 2.0F, "Tool_R",
						DarkSouls.rl("hollow_lordran_soldier/sword_heavy_thrust"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(1.0F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),

				new AttackAnimation.Builder(DarkSouls.rl("hollow_lordran_soldier_sword_thrust_combo"), AttackType.LIGHT,
						0.2F, DarkSouls.rl("hollow_lordran_soldier/sword_thrust_combo"),
						(models) -> models.ENTITY_BIPED, new Phase(0.0F, 0.52F, 0.72F, 0.72F, "Tool_R", null),
						new Phase(0.72F, 1.2F, 1.4F, 2.0F, "Tool_R", null))
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.52F, Side.SERVER, ModSoundEvents.SWORD_SWING),
										new PlaySoundEvent(1.2F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 50)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),

				new AttackAnimation.Builder(DarkSouls.rl("hollow_lordran_soldier_spear_swing_1"), AttackType.LIGHT,
						0.2F, 0.0F, 0.48F, 0.76F, 1.6F, "Tool_R", DarkSouls.rl("hollow_lordran_soldier/spear_swing_1"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.48F, Side.SERVER, ModSoundEvents.SPEAR_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 40)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(DarkSouls.rl("hollow_lordran_soldier_spear_swing_2"), AttackType.LIGHT,
						0.2F, 0.0F, 0.16F, 0.56F, 1.0F, "Tool_R", DarkSouls.rl("hollow_lordran_soldier/spear_swing_2"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.16F, Side.SERVER, ModSoundEvents.SPEAR_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 40)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(DarkSouls.rl("hollow_lordran_soldier_spear_swing_3"), AttackType.LIGHT,
						0.2F, 0.0F, 0.6F, 0.72F, 1.6F, "Tool_R", DarkSouls.rl("hollow_lordran_soldier/spear_swing_3"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.6F, Side.SERVER, ModSoundEvents.SPEAR_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 40)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(DarkSouls.rl("hollow_lordran_soldier_spear_swing_4"), AttackType.LIGHT,
						0.2F, 0.0F, 0.44F, 0.6F, 1.6F, "Tool_R", DarkSouls.rl("hollow_lordran_soldier/spear_swing_4"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.44F, Side.SERVER, ModSoundEvents.SPEAR_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 40)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),

				new AttackAnimation.Builder(DarkSouls.rl("hollow_lordran_soldier_spear_thrust_1"), AttackType.LIGHT,
						0.2F, 0.0F, 0.64F, 0.8F, 1.6F, "Tool_R", DarkSouls.rl("hollow_lordran_soldier/spear_thrust_1"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.64F, Side.SERVER, ModSoundEvents.SPEAR_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 40)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(DarkSouls.rl("hollow_lordran_soldier_spear_thrust_2"), AttackType.LIGHT,
						0.2F, 0.0F, 0.72F, 0.88F, 1.6F, "Tool_R", DarkSouls.rl("hollow_lordran_soldier/spear_thrust_2"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.72F, Side.SERVER, ModSoundEvents.SPEAR_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 40)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(DarkSouls.rl("hollow_lordran_soldier_spear_thrust_3"), AttackType.LIGHT,
						0.2F, 0.0F, 0.88F, 1.04F, 1.6F, "Tool_R", DarkSouls.rl("hollow_lordran_soldier/spear_thrust_3"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.88F, Side.SERVER, ModSoundEvents.SPEAR_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 40)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),

				new AttackAnimation.Builder(DarkSouls.rl("hollow_lordran_soldier_shield_bash"), AttackType.HEAVY, 0.2F,
						0.0F, 0.6F, 0.8F, 1.6F, Colliders.SHIELD, "Tool_L", DarkSouls.rl("hollow_lordran_soldier/shield_bash"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.6F, Side.SERVER, ModSoundEvents.FIST_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 40)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),

				// Falconer
				new StaticAnimation.Builder(DarkSouls.rl("falconer_idle"), 1.0F, true, DarkSouls.rl("falconer/idle"),
						(models) -> models.ENTITY_BIPED),
				new MovementAnimation.Builder(DarkSouls.rl("falconer_walk"), 0.2F, true,
						DarkSouls.rl("falconer/walking"), (models) -> models.ENTITY_BIPED),
				new MovementAnimation.Builder(DarkSouls.rl("falconer_run"), 0.2F, true, DarkSouls.rl("falconer/run"),
						(models) -> models.ENTITY_BIPED),

				new AttackAnimation.Builder(DarkSouls.rl("falconer_light_attack_1"), AttackType.LIGHT, 0.2F, 0.0F,
						0.56F, 0.68F, 1.88F, "Tool_R", DarkSouls.rl("falconer/swing_1"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.56F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(DarkSouls.rl("falconer_light_attack_2"), AttackType.LIGHT, 0.1F, 0.0F,
						0.72F, 1.04F, 1.88F, "Tool_R", DarkSouls.rl("falconer/swing_2"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.72F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(DarkSouls.rl("falconer_light_attack_3"), AttackType.LIGHT, 0.1F, 0.0F,
						0.52F, 0.68F, 1.88F, "Tool_R", DarkSouls.rl("falconer/swing_3"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.52F, Side.SERVER, ModSoundEvents.SWORD_SWING) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),

				// Balder Knight
				new StaticAnimation.Builder(DarkSouls.rl("balder_knight_idle"), 0.3F, true,
						DarkSouls.rl("balder_knight/idle"), (models) -> models.ENTITY_BIPED),
				new StaticAnimation.Builder(DarkSouls.rl("balder_knight_walk"), 0.1F, true,
						DarkSouls.rl("balder_knight/walking"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.24F, Side.SERVER, ModSoundEvents.BALDER_KNIGHT_FOOT),
										new PlaySoundEvent(0.48F, Side.SERVER, ModSoundEvents.BALDER_KNIGHT_FOOT) }),
				new StaticAnimation.Builder(DarkSouls.rl("balder_knight_run"), 0.1F, true,
						DarkSouls.rl("balder_knight/run"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.12F, Side.SERVER, ModSoundEvents.BALDER_KNIGHT_FOOT),
										new PlaySoundEvent(0.5F, Side.SERVER, ModSoundEvents.BALDER_KNIGHT_FOOT) }),
				new AdaptableAnimation.Builder(DarkSouls.rl("balder_knight_block"), 0.2F, true,
						(models) -> models.ENTITY_BIPED)
								.addEntry(LivingMotion.BLOCKING, DarkSouls.rl("balder_knight/block"), false)
								.addEntry(LivingMotion.WALKING, DarkSouls.rl("balder_knight/block_walk"), true)
								.addEntry(LivingMotion.RUNNING, DarkSouls.rl("balder_knight/block_run"), true),
				new AdaptableAnimation.Builder(DarkSouls.rl("balder_knight_rapier_block"), 0.2F, true,
						(models) -> models.ENTITY_BIPED)
								.addEntry(LivingMotion.IDLE, DarkSouls.rl("balder_knight/rapier_block"), false)
								.addEntry(LivingMotion.WALKING, DarkSouls.rl("balder_knight/rapier_block_walk"), true)
								.addEntry(LivingMotion.RUNNING, DarkSouls.rl("balder_knight/rapier_block_run"), true),

				new ParryAnimation.Builder(DarkSouls.rl("balder_knight_rapier_parry"), 0.05F, 0.0F, 1.2F, "Tool_L",
						DarkSouls.rl("balder_knight/rapier_parry"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.32F, Side.SERVER, ModSoundEvents.SWORD_SWING) }),

				new AttackAnimation.Builder(DarkSouls.rl("balder_knight_side_sword_la_1"), AttackType.LIGHT, 0.2F, 0.0F,
						0.4F, 0.56F, 1.6F, "Tool_R", DarkSouls.rl("balder_knight/side_sword_la_1"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.4F, Side.CLIENT, ModSoundEvents.SWORD_SWING),
						new PlaySoundEvent(0.44F, Side.CLIENT, ModSoundEvents.BALDER_KNIGHT_FOOT),
						new PlaySoundEvent(1.6F, Side.CLIENT, ModSoundEvents.BALDER_KNIGHT_FOOT) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(DarkSouls.rl("balder_knight_side_sword_la_2"), AttackType.LIGHT, 0.2F, 0.0F,
						0.16F, 0.4F, 1.6F, "Tool_R", DarkSouls.rl("balder_knight/side_sword_la_2"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.16F, Side.CLIENT, ModSoundEvents.SWORD_SWING),
						new PlaySoundEvent(0.28F, Side.CLIENT, ModSoundEvents.BALDER_KNIGHT_FOOT),
						new PlaySoundEvent(1.6F, Side.CLIENT, ModSoundEvents.BALDER_KNIGHT_FOOT) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(DarkSouls.rl("balder_knight_side_sword_la_3"), AttackType.LIGHT, 0.2F, 0.0F,
						0.24F, 0.44F, 1.6F, "Tool_R", DarkSouls.rl("balder_knight/side_sword_la_3"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.24F, Side.CLIENT, ModSoundEvents.SWORD_SWING),
						new PlaySoundEvent(0.32F, Side.CLIENT, ModSoundEvents.BALDER_KNIGHT_FOOT),
						new PlaySoundEvent(1.6F, Side.CLIENT, ModSoundEvents.BALDER_KNIGHT_FOOT) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 44)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),

				new AttackAnimation.Builder(DarkSouls.rl("balder_knight_side_sword_ha"), AttackType.HEAVY, 0.2F, 0.0F,
						0.68F, 0.76F, 2.0F, "Tool_R", DarkSouls.rl("balder_knight/side_sword_ha"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.64F, Side.CLIENT, ModSoundEvents.SWORD_SWING),
						new PlaySoundEvent(0.76F, Side.CLIENT, ModSoundEvents.BALDER_KNIGHT_FOOT),
						new PlaySoundEvent(2.0F, Side.CLIENT, ModSoundEvents.BALDER_KNIGHT_FOOT) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 45)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),
				new AttackAnimation.Builder(DarkSouls.rl("balder_knight_side_sword_da"), AttackType.DASH, 0.2F, 0.0F,
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
				new AttackAnimation.Builder(DarkSouls.rl("balder_knight_side_sword_fast_la"), AttackType.LIGHT, 0.2F,
						DarkSouls.rl("balder_knight/rapier_la"), (models) -> models.ENTITY_BIPED,
						new Phase(0.0F, 0.1F, 0.4F, 0.4F, "Tool_R"), new Phase(0.4F, 0.72F, 0.8F, 2.0F, "Tool_R"))
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
				new AttackAnimation.Builder(DarkSouls.rl("balder_knight_shield_ha"), AttackType.HEAVY, 0.2F, 0.0F,
						0.08F, 0.24F, 1.2F, Colliders.SHIELD, "Tool_L", DarkSouls.rl("balder_knight/shield_ha"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(AnimEvent.ON_BEGIN, Side.CLIENT, ModSoundEvents.BALDER_KNIGHT_FOOT),
						new PlaySoundEvent(0.08F, Side.CLIENT, ModSoundEvents.FIST_SWING),
						new PlaySoundEvent(1.1F, Side.CLIENT, ModSoundEvents.BALDER_KNIGHT_FOOT) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 37)
								.addProperty(AttackProperty.POISE_DAMAGE, 15),

				new AttackAnimation.Builder(DarkSouls.rl("balder_knight_rapier_la_1"), AttackType.LIGHT, 0.2F, 0.0F,
						0.4F, 0.6F, 1.2F, "Tool_R", DarkSouls.rl("balder_knight/rapier_la_1"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.4F, Side.CLIENT, ModSoundEvents.SWORD_SWING),
						new PlaySoundEvent(0.52F, Side.CLIENT, ModSoundEvents.BALDER_KNIGHT_FOOT),
						new PlaySoundEvent(1.16F, Side.CLIENT, ModSoundEvents.BALDER_KNIGHT_FOOT) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 37)
								.addProperty(AttackProperty.POISE_DAMAGE, 15),
				new AttackAnimation.Builder(DarkSouls.rl("balder_knight_rapier_la_2"), AttackType.LIGHT, 0.2F, 0.0F,
						0.24F, 0.44F, 1.2F, "Tool_R", DarkSouls.rl("balder_knight/rapier_la_2"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.24F, Side.CLIENT, ModSoundEvents.SWORD_SWING),
						new PlaySoundEvent(0.32F, Side.CLIENT, ModSoundEvents.BALDER_KNIGHT_FOOT),
						new PlaySoundEvent(1.16F, Side.CLIENT, ModSoundEvents.BALDER_KNIGHT_FOOT) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 37)
								.addProperty(AttackProperty.POISE_DAMAGE, 15),
				new AttackAnimation.Builder(DarkSouls.rl("balder_knight_rapier_la_3"), AttackType.LIGHT, 0.1F, 0.0F,
						0.24F, 0.44F, 1.2F, "Tool_R", DarkSouls.rl("balder_knight/rapier_la_2"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.24F, Side.CLIENT, ModSoundEvents.SWORD_SWING),
						new PlaySoundEvent(0.32F, Side.CLIENT, ModSoundEvents.BALDER_KNIGHT_FOOT),
						new PlaySoundEvent(1.16F, Side.CLIENT, ModSoundEvents.BALDER_KNIGHT_FOOT) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 37)
								.addProperty(AttackProperty.POISE_DAMAGE, 15),

				new AttackAnimation.Builder(DarkSouls.rl("balder_knight_rapier_ha"), AttackType.HEAVY, 0.05F, 0.0F,
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
				new AttackAnimation.Builder(DarkSouls.rl("balder_knight_rapier_da"), AttackType.DASH, 0.05F, 0.0F,
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
				new StaticAnimation.Builder(DarkSouls.rl("berenike_knight_idle"), 0.3F, true,
						DarkSouls.rl("berenike_knight/idle"), (models) -> models.ENTITY_BIPED),

				new AttackAnimation.Builder(DarkSouls.rl("berenike_knight_sword_la_1"), AttackType.LIGHT, 0.2F, 0.0F,
						1.04F, 1.5F, 2.4F, "Tool_R", DarkSouls.rl("berenike_knight/sword_la_1"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(1.04F, Side.CLIENT, ModSoundEvents.SWORD_SWING),
						new PlaySoundEvent(1.08F, Side.CLIENT, ModSoundEvents.BERENIKE_KNIGHT_FOOT),
						new PlaySoundEvent(2.36F, Side.CLIENT, ModSoundEvents.BERENIKE_KNIGHT_FOOT) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 75)
								.addProperty(AttackProperty.POISE_DAMAGE, 32),
				new AttackAnimation.Builder(DarkSouls.rl("berenike_knight_sword_la_2"), AttackType.LIGHT, 0.2F, 0.0F,
						0.32F, 0.7F, 1.6F, "Tool_R", DarkSouls.rl("berenike_knight/sword_la_2"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.2F, Side.CLIENT, ModSoundEvents.BERENIKE_KNIGHT_FOOT),
						new PlaySoundEvent(0.32F, Side.CLIENT, ModSoundEvents.SWORD_SWING),
						new PlaySoundEvent(1.56F, Side.CLIENT, ModSoundEvents.BERENIKE_KNIGHT_FOOT) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 75)
								.addProperty(AttackProperty.POISE_DAMAGE, 32),

				new AttackAnimation.Builder(DarkSouls.rl("berenike_knight_sword_ha_1"), AttackType.HEAVY, 0.2F, 0.0F,
						1.48F, 1.8F, 2.8F, "Tool_R", DarkSouls.rl("berenike_knight/sword_ha_1"),
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
				new AttackAnimation.Builder(DarkSouls.rl("berenike_knight_sword_ha_2"), AttackType.HEAVY, 0.2F, 0.0F,
						1.24F, 1.7F, 2.4F, "Tool_R", DarkSouls.rl("berenike_knight/sword_ha_2"),
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

				new AttackAnimation.Builder(DarkSouls.rl("berenike_knight_sword_da"), AttackType.DASH, 0.05F, 0.0F,
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

				new AttackAnimation.Builder(DarkSouls.rl("berenike_knight_mace_la_1"), AttackType.LIGHT, 0.2F, 0.0F,
						1.24F, 1.7F, 2.4F, "Tool_R", DarkSouls.rl("berenike_knight/mace_la_1"),
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
				new AttackAnimation.Builder(DarkSouls.rl("berenike_knight_mace_la_2"), AttackType.LIGHT, 0.2F, 0.0F,
						0.6F, 0.76F, 2F, "Tool_R", DarkSouls.rl("berenike_knight/mace_la_2"),
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

				new AttackAnimation.Builder(DarkSouls.rl("berenike_knight_mace_ha"), AttackType.HEAVY, 0.2F, 0.0F,
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

				new AttackAnimation.Builder(DarkSouls.rl("berenike_knight_kick"), AttackType.HEAVY, 0.2F, 0.0F, 0.56F,
						0.9F, 1.6F, Colliders.FIST, "Leg_L", DarkSouls.rl("berenike_knight/kick"),
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
				new StaticAnimation.Builder(DarkSouls.rl("black_knight_idle"), 0.3F, true,
						DarkSouls.rl("black_knight/idle"), (models) -> models.ENTITY_BIPED),
				new StaticAnimation.Builder(DarkSouls.rl("black_knight_walking"), 0.1F, true,
						DarkSouls.rl("black_knight/walking"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.24F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT),
										new PlaySoundEvent(0.8F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT) }),
				new StaticAnimation.Builder(DarkSouls.rl("black_knight_running"), 0.1F, true,
						DarkSouls.rl("black_knight/running"), (models) -> models.ENTITY_BIPED)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.12F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT),
										new PlaySoundEvent(0.5F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT) }),
				new AdaptableAnimation.Builder(DarkSouls.rl("black_knight_block"), 0.2F, true,
						(models) -> models.ENTITY_BIPED)
								.addEntry(LivingMotion.BLOCKING, DarkSouls.rl("black_knight/block"), false)
								.addEntry(LivingMotion.WALKING, DarkSouls.rl("black_knight/block_walk"), true)
								.addEntry(LivingMotion.RUNNING, DarkSouls.rl("black_knight/block_run"), true),
				new DeathAnimation.Builder(DarkSouls.rl("black_knight_death"), 0.1F, DarkSouls.rl("black_knight/death"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(1.84F, Side.SERVER, ModSoundEvents.GENERIC_LAND), })
								.addProperty(DeathProperty.DISAPPEAR_AT, 0.5F),

				new AttackAnimation.Builder(DarkSouls.rl("black_knight_sword_la_long_1"), AttackType.LIGHT, 0.2F, 0.0F,
						0.56F, 0.8F, 1.4F, "Tool_R", DarkSouls.rl("black_knight/black_knight_sword_la_1"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.48F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT),
						new PlaySoundEvent(0.56F, Side.CLIENT, ModSoundEvents.SWORD_SWING),
						new PlaySoundEvent(1.28F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 56)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),
				new AttackAnimation.Builder(DarkSouls.rl("black_knight_sword_la_long_2"), AttackType.LIGHT, 0.1F, 0.0F,
						0.44F, 0.64F, 1.4F, "Tool_R", DarkSouls.rl("black_knight/black_knight_sword_la_2"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.44F, Side.CLIENT, ModSoundEvents.SWORD_SWING),
						new PlaySoundEvent(0.48F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT),
						new PlaySoundEvent(1.28F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 56)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),
				new AttackAnimation.Builder(DarkSouls.rl("black_knight_sword_la_long_3"), AttackType.HEAVY, 0.1F, 0.0F,
						0.28F, 0.48F, 2.2F, "Tool_R", DarkSouls.rl("black_knight/black_knight_sword_la_3"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.2F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT),
						new PlaySoundEvent(0.28F, Side.CLIENT, ModSoundEvents.SWORD_THRUST),
						new PlaySoundEvent(1.48F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.THRUST)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 56)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),
				new AttackAnimation.Builder(DarkSouls.rl("black_knight_sword_la_long_4"), AttackType.HEAVY, 0.1F, 0.0F,
						0.64F, 0.88F, 1.92F, "Tool_R", DarkSouls.rl("black_knight/black_knight_sword_la_4"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.64F, Side.CLIENT, ModSoundEvents.SWORD_SWING),
						new PlaySoundEvent(0.72F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT),
						new PlaySoundEvent(1.6F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 56)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),

				new AttackAnimation.Builder(DarkSouls.rl("black_knight_sword_la_short_1"), AttackType.LIGHT, 0.2F, 0.0F,
						0.56F, 0.8F, 1.4F, "Tool_R", DarkSouls.rl("black_knight/black_knight_sword_la_1"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.48F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT),
						new PlaySoundEvent(0.56F, Side.CLIENT, ModSoundEvents.SWORD_SWING),
						new PlaySoundEvent(1.28F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 56)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),
				new AttackAnimation.Builder(DarkSouls.rl("black_knight_sword_la_short_2"), AttackType.LIGHT, 0.1F, 0.0F,
						0.44F, 0.64F, 1.4F, "Tool_R", DarkSouls.rl("black_knight/black_knight_sword_la_2"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.44F, Side.CLIENT, ModSoundEvents.SWORD_SWING),
						new PlaySoundEvent(0.48F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT),
						new PlaySoundEvent(1.28F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 56)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),
				new AttackAnimation.Builder(DarkSouls.rl("black_knight_sword_la_short_5"), AttackType.HEAVY, 0.1F, 0.0F,
						0.92F, 1.12F, 2.36F, "Tool_R", DarkSouls.rl("black_knight/black_knight_sword_la_5"),
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

				new AttackAnimation.Builder(DarkSouls.rl("black_knight_sword_ha"), AttackType.HEAVY, 0.2F, 0.0F, 0.4F,
						0.64F, 1.68F, "Tool_R", DarkSouls.rl("black_knight/black_knight_sword_ha"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.4F, Side.CLIENT, ModSoundEvents.SWORD_SWING),
						new PlaySoundEvent(0.48F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT),
						new PlaySoundEvent(1.44F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 56)
								.addProperty(AttackProperty.POISE_DAMAGE, 23),
				new AttackAnimation.Builder(DarkSouls.rl("black_knight_shield_attack"), AttackType.HEAVY, 0.2F, 0.0F,
						0.52F, 0.8F, 1.6F, Colliders.SHIELD, "Tool_L", DarkSouls.rl("black_knight/black_knight_shield_attack"),
						(models) -> models.ENTITY_BIPED).addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
				{ new PlaySoundEvent(0.52F, Side.CLIENT, ModSoundEvents.AXE_SWING),
						new PlaySoundEvent(0.6F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT),
						new PlaySoundEvent(1.52F, Side.CLIENT, ModSoundEvents.BLACK_KNIGHT_FOOT) })
								.addProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE, MovementDamageType.REGULAR)
								.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 40)
								.addProperty(AttackProperty.POISE_DAMAGE, 20),
				new AttackAnimation.Builder(DarkSouls.rl("black_knight_sword_da"), AttackType.DASH, 0.2F, 0.0F, 0.64F,
						0.8F, 1.68F, "Tool_R", DarkSouls.rl("black_knight/black_knight_sword_da"),
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
				new StaticAnimation.Builder(DarkSouls.rl("stray_demon_idle"), 0.5F, true,
						DarkSouls.rl("stray_demon/idle"), (models) -> models.ENTITY_STRAY_DEMON),
				new StaticAnimation.Builder(DarkSouls.rl("stray_demon_walk"), 0.5F, true,
						DarkSouls.rl("stray_demon/walk"), (models) -> models.ENTITY_STRAY_DEMON)
								.addProperty(StaticAnimationProperty.EVENTS, new AnimEvent[]
								{ new PlaySoundEvent(0.7F, Side.CLIENT, ModSoundEvents.STRAY_DEMON_FOOT),
										new ShakeCamGlobalEvent(0.7F, 10, 0.5F),
										new PlaySoundEvent(1.5F, Side.CLIENT, ModSoundEvents.STRAY_DEMON_FOOT),
										new ShakeCamGlobalEvent(1.5F, 10, 0.5F) }),
				new DeathAnimation.Builder(DarkSouls.rl("stray_demon_death"), 0.5F, DarkSouls.rl("stray_demon/death"),
						(models) -> models.ENTITY_STRAY_DEMON).addProperty(DeathProperty.DISAPPEAR_AT, 1.5F),

				new AttackAnimation.Builder(DarkSouls.rl("stray_demon_hammer_la_1"), AttackType.LIGHT, 1.0F, 0.0F,
						0.12F, 0.44F, 1.6F, "Tool_R", DarkSouls.rl("stray_demon/hammer_la_1"),
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
				new AttackAnimation.Builder(DarkSouls.rl("stray_demon_hammer_la_2"), AttackType.LIGHT, 1.0F, 0.0F,
						0.44F, 0.64F, 2.0F, "Tool_R", DarkSouls.rl("stray_demon/hammer_la_2"),
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

				new AttackAnimation.Builder(DarkSouls.rl("stray_demon_hammer_la_alt_1"), AttackType.LIGHT, 1.0F, 0.0F,
						0.16F, 0.3F, 1.6F, "Tool_R", DarkSouls.rl("stray_demon/hammer_la_alt_1"),
						(models) -> models.ENTITY_STRAY_DEMON)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_MONSTER_HAMMER_SWING.appendTo(0.16F, new AnimEvent[]
										{})).addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.STUN_TYPE, StunType.FLY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 80)
								.addProperty(AttackProperty.POISE_DAMAGE, 28),
				new AttackAnimation.Builder(DarkSouls.rl("stray_demon_hammer_la_alt_2"), AttackType.LIGHT, 1.0F, 0.0F,
						0.48F, 0.8F, 2.0F, "Tool_R", DarkSouls.rl("stray_demon/hammer_la_alt_2"),
						(models) -> models.ENTITY_STRAY_DEMON)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_MONSTER_HAMMER_SWING.appendTo(0.44F, new AnimEvent[]
										{})).addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.STUN_TYPE, StunType.FLY)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 80)
								.addProperty(AttackProperty.POISE_DAMAGE, 28),

				new AttackAnimation.Builder(DarkSouls.rl("stray_demon_hammer_ha_1"), AttackType.HEAVY, 0.2F, 0.0F,
						1.08F, 1.24F, 2.6F, "Tool_R", DarkSouls.rl("stray_demon/hammer_ha_1"),
						(models) -> models.ENTITY_STRAY_DEMON)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_MONSTER_HAMMER.appendTo(1.12F, new AnimEvent[]
										{ new PlaySoundEvent(1.0F, Side.SERVER, ModSoundEvents.STRAY_DEMON_FOOT),
												new ShakeCamGlobalEvent(1.0F, 10, 0.5F) }))
								.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 80)
								.addProperty(AttackProperty.POISE_DAMAGE, 28),
				new AttackAnimation.Builder(DarkSouls.rl("stray_demon_hammer_ha_2"), AttackType.HEAVY, 0.2F, 0.0F,
						1.76F, 1.92F, 3.6F, "Tool_R", DarkSouls.rl("stray_demon/hammer_ha_2"),
						(models) -> models.ENTITY_STRAY_DEMON)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_MONSTER_HAMMER.appendTo(1.8F, new AnimEvent[]
										{ new PlaySoundEvent(1.68F, Side.SERVER, ModSoundEvents.STRAY_DEMON_FOOT),
												new ShakeCamGlobalEvent(1.68F, 10, 0.5F) }))
								.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 80)
								.addProperty(AttackProperty.POISE_DAMAGE, 28),

				new AttackAnimation.Builder(DarkSouls.rl("stray_demon_hammer_drive"), AttackType.HEAVY, 0.1F, 0.0F,
						0.72F, 0.96F, 2.8F, "Tool_R", DarkSouls.rl("stray_demon/hammer_drive"),
						(models) -> models.ENTITY_STRAY_DEMON)
								.addProperty(StaticAnimationProperty.EVENTS,
										SmashEvents.BIG_MONSTER_HAMMER.appendTo(0.88F, new AnimEvent[]
										{})).addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
								.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
								.addProperty(AttackProperty.STAMINA_DAMAGE, 80)
								.addProperty(AttackProperty.POISE_DAMAGE, 28),
				new AttackAnimation.Builder(DarkSouls.rl("stray_demon_hammer_da"), AttackType.DASH, 0.5F, 0.0F, 1.0F,
						1.2F, 2.4F, "Tool_R", DarkSouls.rl("stray_demon/hammer_da"),
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
				new AttackAnimation.Builder(DarkSouls.rl("stray_demon_ground_pound"), AttackType.HEAVY, 0.05F, 0.0F,
						1.6F, 1.88F, 3.2F, Colliders.STRAY_DEMON_BODY, "Root", DarkSouls.rl("stray_demon/ground_pound"),
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
				new StaticAnimation.Builder(DarkSouls.rl("taurus_demon_idle"), 0.2F, true,
						DarkSouls.rl("taurus_demon/idle"), (models) -> models.ENTITY_TAURUS_DEMON),

				// Anastacia of Astora
				new StaticAnimation.Builder(DarkSouls.rl("anastacia_idle"), 0.4F, true,
						DarkSouls.rl("anastacia_of_astora/idle"), (models) -> models.ENTITY_BIPED),

				// Bell Gargoyle
				new StaticAnimation.Builder(DarkSouls.rl("bell_gargoyle_idle"), 0.2F, true,
						DarkSouls.rl("bell_gargoyle/idle"), (models) -> models.ENTITY_BELL_GARGOYLE)
		);
	}
	
	private static Path createPath(Path path, ResourceLocation location)
	{
		return path.resolve("data/" + location.getNamespace() + "/animation_data/" + location.getPath() + ".json");
	}

	@Override
	public String getName()
	{
		return "AnimationData";
	}
}
