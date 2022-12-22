package com.skullmangames.darksouls.core.init;

import java.util.List;

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
import com.skullmangames.darksouls.common.block.LightSource;
import com.skullmangames.darksouls.core.util.DamageSourceExtended;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.Damage;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.DamageType;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.StunType;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.capability.item.IShield.Deflection;
import com.skullmangames.darksouls.common.entity.projectile.LightningSpear;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
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
	
	public static final DeathAnimation BIPED_DEATH = new DeathAnimation(0.05F, "biped/death/death", (models) -> models.ENTITY_BIPED)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(1.52F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get())) });
	public static final DeathAnimation BIPED_DEATH_SMASH = new DeathAnimation(0.05F, "biped/death/smash", (models) -> models.ENTITY_BIPED);
	public static final DeathAnimation BIPED_DEATH_FLY_FRONT = new DeathAnimation(0.05F, "biped/death/fly_front", (models) -> models.ENTITY_BIPED)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[]
			{
					Event.create(0.4F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get())),
					Event.create(0.8F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get()))
			});
	public static final DeathAnimation BIPED_DEATH_FLY_BACK = new DeathAnimation(0.05F, "biped/death/fly_back", (models) -> models.ENTITY_BIPED)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[]
			{
					Event.create(0.44F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get())),
					Event.create(0.8F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get()))
			});
	public static final DeathAnimation BIPED_DEATH_FLY_LEFT = new DeathAnimation(0.05F, "biped/death/fly_left", (models) -> models.ENTITY_BIPED)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[]
			{
					Event.create(0.48F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get())),
					Event.create(0.92F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get()))
			});
	public static final DeathAnimation BIPED_DEATH_FLY_RIGHT = new DeathAnimation(0.05F, "biped/death/fly_right", (models) -> models.ENTITY_BIPED)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[]
			{
					Event.create(0.48F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get())),
					Event.create(0.92F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get()))
			});
	
	public static final StaticAnimation BIPED_DIG = new StaticAnimation(0.2F, true, "biped/living/dig", (models) -> models.ENTITY_BIPED)
			.addProperty(StaticAnimationProperty.LAYER_PART, LayerPart.RIGHT);
	public static final StaticAnimation BIPED_TOUCH_BONFIRE = new ActionAnimation(0.5F, "biped/living/touching_bonfire", (models) -> models.ENTITY_BIPED)
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
			});
	
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

	public static final StaticAnimation BIPED_EAT = new MirrorAnimation(0.2F, true, "biped/living/eat_r", "biped/living/eat_l", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation BIPED_DRINK = new MirrorAnimation(0.2F, true, "biped/living/drink_r", "biped/living/drink_l", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation BIPED_CONSUME_SOUL = new MirrorAnimation(0.2F, true, "biped/living/consume_soul_r", "biped/living/consume_soul_l", (models) -> models.ENTITY_BIPED);

	public static final StaticAnimation BIPED_BLOCK = new AdaptableAnimation(0.2F, true, (models) -> models.ENTITY_BIPED,
			new AnimConfig(LivingMotion.BLOCKING, "biped/combat/block_mirror", "biped/combat/block", false),
			new AnimConfig(LivingMotion.WALKING, "biped/combat/block_walk_mirror", "biped/combat/block_walk", true),
			new AnimConfig(LivingMotion.RUNNING, "biped/combat/block_run_mirror", "biped/combat/block_run", true),
			new AnimConfig(LivingMotion.KNEELING, "biped/combat/block_mirror", "biped/combat/block", true),
			new AnimConfig(LivingMotion.SNEAKING, "biped/combat/block_mirror", "biped/combat/block", true));
	
	public static final StaticAnimation BIPED_HIT_BLOCKED_LEFT = new BlockAnimation(0.05F, "biped/hit/blocked_left", (models) -> models.ENTITY_BIPED)
			.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true);
	public static final StaticAnimation BIPED_HIT_BLOCKED_RIGHT = new BlockAnimation(0.05F, "biped/hit/blocked_right", (models) -> models.ENTITY_BIPED)
			.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true);
	
	public static final StaticAnimation BIPED_HIT_BLOCKED_FLY_LEFT = new InvincibleAnimation(0.05F, "biped/hit/blocked_fly_left", (models) -> models.ENTITY_BIPED)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.48F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.GENERIC_ROLL.get())) });
	public static final StaticAnimation BIPED_HIT_BLOCKED_FLY_RIGHT = new InvincibleAnimation(0.05F, "biped/hit/blocked_fly_right", (models) -> models.ENTITY_BIPED)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.48F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.GENERIC_ROLL.get())) });
	
	public static final StaticAnimation BIPED_DISARM_SHIELD_LEFT = new ActionAnimation(0.05F, "biped/combat/disarmed_left", (models) -> models.ENTITY_BIPED)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(Event.ON_BEGIN, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.PLAYER_SHIELD_DISARMED.get())) });
	public static final StaticAnimation BIPED_DISARM_SHIELD_RIGHT = new ActionAnimation(0.05F, "biped/combat/disarmed_right", (models) -> models.ENTITY_BIPED)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(Event.ON_BEGIN, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.PLAYER_SHIELD_DISARMED.get())) });
	
	public static final StaticAnimation BIPED_HORSEBACK_IDLE = new StaticAnimation(0.2F, true, "biped/horseback/horseback_idle", (models) -> models.ENTITY_BIPED);
	
	public static final StaticAnimation BIPED_IDLE_CROSSBOW = new StaticAnimation(0.2F, true, "biped/living/idle_crossbow", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation BIPED_WALK_CROSSBOW = new MovementAnimation(0.2F, true, "biped/living/walk_crossbow", (models) -> models.ENTITY_BIPED);

	public static final StaticAnimation BIPED_CROSSBOW_AIM = new AimingAnimation(0.16F, true, "biped/combat/crossbow_aim_mid", "biped/combat/crossbow_aim_up", "biped/combat/crossbow_aim_down", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation BIPED_CROSSBOW_SHOT = new ReboundAnimation(0.16F, false, "biped/combat/crossbow_shot_mid", "biped/combat/crossbow_shot_up", "biped/combat/crossbow_shot_down", (models) -> models.ENTITY_BIPED);

	public static final StaticAnimation BIPED_CROSSBOW_RELOAD = new StaticAnimation(0.16F, false, "biped/combat/crossbow_reload", (models) -> models.ENTITY_BIPED)
			.addProperty(StaticAnimationProperty.LAYER_PART, LayerPart.UP);

	public static final StaticAnimation BIPED_BOW_AIM = new AimingAnimation(0.16F, true, "biped/combat/bow_aim_mid", "biped/combat/bow_aim_up", "biped/combat/bow_aim_down", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation BIPED_BOW_REBOUND = new ReboundAnimation(0.04F, false, "biped/combat/bow_shot_mid", "biped/combat/bow_shot_up", "biped/combat/bow_shot_down", (models) -> models.ENTITY_BIPED);

	public static final StaticAnimation BIPED_SPEER_AIM = new AimingAnimation(0.16F, false, "biped/combat/javelin_aim_mid", "biped/combat/javelin_aim_up", "biped/combat/javelin_aim_down", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation BIPED_SPEER_REBOUND = new ReboundAnimation(0.08F, false, "biped/combat/javelin_throw_mid", "biped/combat/javelin_throw_up", "biped/combat/javelin_throw_down", (models) -> models.ENTITY_BIPED);

	public static final StaticAnimation BIPED_HIT_LIGHT_FRONT = new HitAnimation(0.05F, "biped/hit/light_front", (models) -> models.ENTITY_BIPED)
			.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true);
	public static final StaticAnimation BIPED_HIT_LIGHT_LEFT = new HitAnimation(0.05F, "biped/hit/light_left", (models) -> models.ENTITY_BIPED)
			.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true);
	public static final StaticAnimation BIPED_HIT_LIGHT_RIGHT = new HitAnimation(0.05F, "biped/hit/light_right", (models) -> models.ENTITY_BIPED)
			.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true);
	public static final StaticAnimation BIPED_HIT_LIGHT_BACK = new HitAnimation(0.05F, "biped/hit/light_back", (models) -> models.ENTITY_BIPED)
			.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true);
	public static final StaticAnimation BIPED_HIT_HEAVY_FRONT = new HitAnimation(0.05F, "biped/hit/heavy_front", (models) -> models.ENTITY_BIPED)
			.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true);
	public static final StaticAnimation BIPED_HIT_HEAVY_BACK = new HitAnimation(0.05F, "biped/hit/heavy_back", (models) -> models.ENTITY_BIPED)
			.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true);
	public static final StaticAnimation BIPED_HIT_HEAVY_LEFT = new HitAnimation(0.05F, "biped/hit/heavy_left", (models) -> models.ENTITY_BIPED)
			.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true);
	public static final StaticAnimation BIPED_HIT_HEAVY_RIGHT = new HitAnimation(0.05F, "biped/hit/heavy_right", (models) -> models.ENTITY_BIPED)
			.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true);
	
	public static final StaticAnimation BIPED_HORSEBACK_HIT_LIGHT_FRONT = new HitAnimation(0.05F, "biped/hit/horseback_light_front", (models) -> models.ENTITY_BIPED)
			.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true);
	public static final StaticAnimation BIPED_HORSEBACK_HIT_LIGHT_LEFT = new HitAnimation(0.05F, "biped/hit/horseback_light_left", (models) -> models.ENTITY_BIPED)
			.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true);
	public static final StaticAnimation BIPED_HORSEBACK_HIT_LIGHT_RIGHT = new HitAnimation(0.05F, "biped/hit/horseback_light_right", (models) -> models.ENTITY_BIPED)
			.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true);
	public static final StaticAnimation BIPED_HORSEBACK_HIT_LIGHT_BACK = new HitAnimation(0.05F, "biped/hit/horseback_light_back", (models) -> models.ENTITY_BIPED)
			.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true);
	public static final StaticAnimation BIPED_HORSEBACK_HIT_HEAVY_FRONT = new HitAnimation(0.05F, "biped/hit/horseback_heavy_front", (models) -> models.ENTITY_BIPED)
			.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true);
	public static final StaticAnimation BIPED_HORSEBACK_HIT_HEAVY_BACK = new HitAnimation(0.05F, "biped/hit/horseback_heavy_back", (models) -> models.ENTITY_BIPED)
			.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true);
	public static final StaticAnimation BIPED_HORSEBACK_HIT_HEAVY_LEFT = new HitAnimation(0.05F, "biped/hit/horseback_heavy_left", (models) -> models.ENTITY_BIPED)
			.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true);
	public static final StaticAnimation BIPED_HORSEBACK_HIT_HEAVY_RIGHT = new HitAnimation(0.05F, "biped/hit/horseback_heavy_right", (models) -> models.ENTITY_BIPED)
			.addProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS, true);
	
	public static final StaticAnimation BIPED_HIT_SMASH = new InvincibleAnimation(0.05F, "biped/hit/smash", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation BIPED_HIT_FLY_FRONT = new InvincibleAnimation(0.05F, "biped/hit/fly", (models) -> models.ENTITY_BIPED)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[]
					{
							Event.create(0.4F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get())),
							Event.create(0.8F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get()))
					});
	public static final StaticAnimation BIPED_HIT_FLY_BACK = new InvincibleAnimation(0.05F, "biped/hit/fly_back", (models) -> models.ENTITY_BIPED)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[]
					{
							Event.create(0.44F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get())),
							Event.create(0.8F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get()))
					});
	public static final StaticAnimation BIPED_HIT_FLY_LEFT = new InvincibleAnimation(0.05F, "biped/hit/fly_left", (models) -> models.ENTITY_BIPED)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[]
					{
							Event.create(0.48F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get())),
							Event.create(0.92F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get()))
					});
	public static final StaticAnimation BIPED_HIT_FLY_RIGHT = new InvincibleAnimation(0.05F, "biped/hit/fly_right", (models) -> models.ENTITY_BIPED)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[]
					{
							Event.create(0.48F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get())),
							Event.create(0.92F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get()))
					});
	public static final StaticAnimation BIPED_HIT_LAND_HEAVY = new HitAnimation(0.05F, "biped/hit/land_heavy", (models) -> models.ENTITY_BIPED);
	
	public static final StaticAnimation BIPED_ROLL = new DodgingAnimation(0.1F, "biped/combat/roll", (models) -> models.ENTITY_BIPED)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.28F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.GENERIC_ROLL.get())) });
	public static final StaticAnimation BIPED_FAT_ROLL = new DodgingAnimation(0.1F, "biped/combat/fat_roll", (models) -> models.ENTITY_BIPED)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[]
			{
					Event.create(0.48F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.GENERIC_LAND.get())),
					Event.create(0.48F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 10, 0.25F))
			});
	public static final StaticAnimation BIPED_ROLL_BACK = new DodgingAnimation(0.1F, "biped/combat/roll_back", (models) -> models.ENTITY_BIPED)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.28F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.GENERIC_ROLL.get())) });
	public static final StaticAnimation BIPED_ROLL_LEFT = new DodgingAnimation(0.1F, true, "biped/combat/roll_left", (models) -> models.ENTITY_BIPED)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.28F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.GENERIC_ROLL.get())) });
	public static final StaticAnimation BIPED_ROLL_RIGHT = new DodgingAnimation(0.1F, true, "biped/combat/roll_right", (models) -> models.ENTITY_BIPED)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.28F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.GENERIC_ROLL.get())) });
	public static final DodgingAnimation BIPED_JUMP_BACK = new DodgingAnimation(0.08F, "biped/combat/jump_back", (models) -> models.ENTITY_BIPED);
	
	// Miracle
	public static final StaticAnimation BIPED_CAST_MIRACLE_HEAL = new ActionAnimation(0.5F, "biped/combat/cast_miracle", (models) -> models.ENTITY_BIPED)
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
							Event.create(2.5F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.MIRACLE_USE.get())),
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
					});
	
	public static final StaticAnimation BIPED_CAST_MIRACLE_HEAL_AID = new ActionAnimation(0.5F, "biped/combat/cast_miracle_fast", (models) -> models.ENTITY_BIPED)
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
						Event.create(1.0F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.MIRACLE_USE.get())),
						Event.create(1.0F, Side.CLIENT, (cap) ->
						{
							cap.getLevel().addAlwaysVisibleParticle(ModParticles.TINY_MIRACLE_CIRCLE.get(), cap.getX(), cap.getY() + 0.1F, cap.getZ(), 0, 0, 0);
						}),
						Event.create(1.1F, Side.SERVER, (cap) ->
						{
							cap.getOriginalEntity().heal(2.5F);
						})
					});
	
	public static final StaticAnimation BIPED_CAST_MIRACLE_HOMEWARD = new ActionAnimation(0.5F, "biped/combat/cast_miracle", (models) -> models.ENTITY_BIPED)
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
						Event.create(2.5F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.MIRACLE_USE.get())),
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
					});
	
	public static final StaticAnimation BIPED_CAST_MIRACLE_FORCE = new ActionAnimation(0.3F, "biped/combat/cast_miracle_force", (models) -> models.ENTITY_BIPED)
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
					});
	
	private static final Event[] LIGHTNING_SPEAR_EVENTS = new Event[]
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
	
	public static final StaticAnimation BIPED_CAST_MIRACLE_LIGHTNING_SPEAR = new ActionAnimation(0.3F, "biped/combat/cast_miracle_spear", (models) -> models.ENTITY_BIPED)
			.addProperty(StaticAnimationProperty.EVENTS, LIGHTNING_SPEAR_EVENTS);
	public static final StaticAnimation HORSEBACK_CAST_MIRACLE_LIGHTNING_SPEAR = new ActionAnimation(0.3F, "biped/combat/horseback_cast_miracle_spear", (models) -> models.ENTITY_BIPED)
			.addProperty(StaticAnimationProperty.EVENTS, LIGHTNING_SPEAR_EVENTS);
	
	private static final Event[] GREAT_LIGHTNING_SPEAR_EVENTS = new Event[]
			{
					Event.create(Event.ON_BEGIN, Side.SERVER, (cap) ->
					{
						LightSource.setLightSource(cap.getLevel(), cap.getOriginalEntity().blockPosition(), 15, 1.2F);
					}),
					Event.create(Event.ON_BEGIN, Side.CLIENT, (cap) ->
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
	
	public static final StaticAnimation BIPED_CAST_MIRACLE_GREAT_LIGHTNING_SPEAR = new ActionAnimation(0.3F, "biped/combat/cast_miracle_spear", (models) -> models.ENTITY_BIPED)
			.addProperty(StaticAnimationProperty.EVENTS, GREAT_LIGHTNING_SPEAR_EVENTS);
	public static final StaticAnimation HORSEBACK_CAST_MIRACLE_GREAT_LIGHTNING_SPEAR = new ActionAnimation(0.3F, "biped/combat/horseback_cast_miracle_spear", (models) -> models.ENTITY_BIPED)
			.addProperty(StaticAnimationProperty.EVENTS, GREAT_LIGHTNING_SPEAR_EVENTS);
	
	// Big Weapon
	public static final MirrorAnimation BIPED_HOLDING_BIG_WEAPON = new MirrorAnimation(0.2F, true, true,
			"biped/living/holding_big_weapon_r", "biped/living/holding_big_weapon_l", (models) -> models.ENTITY_BIPED);
	
	// Horseback Attacks
	public static final AttackAnimation[] HORSEBACK_LIGHT_ATTACK = new AttackAnimation[]
	{
				new AttackAnimation(0.5F, 0.0F, 0.2F, 0.52F, 1.6F, "Tool_R", "biped/combat/horseback_light_attack_1", (models) -> models.ENTITY_BIPED)
						.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
						.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM),
				new AttackAnimation(0.5F, 0.0F, 0.12F, 0.48F, 1.6F, "Tool_R", "biped/combat/horseback_light_attack_2", (models) -> models.ENTITY_BIPED)
						.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
						.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
	};

	// Ultra Greatsword
	public static final AttackAnimation[] ULTRA_GREATSWORD_LIGHT_ATTACK = new AttackAnimation[]
	{
			new AttackAnimation(0.3F, 0.0F, 0.48F, 0.88F, 2.8F, "Tool_R", "biped/combat/ultra_greatsword_light_attack_1", (models) -> models.ENTITY_BIPED)
						.addProperty(StaticAnimationProperty.EVENTS, new Event[]
						{
								Event.create(0.7F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.ULTRA_GREATSWORD_SMASH.get())),
								Event.create(0.7F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 20, 1))
						})
						.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
						.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
						.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
						.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F)),
			new AttackAnimation(0.2F, 0.0F, 0.44F, 0.88F, 2.4F, "Tool_R", "biped/combat/ultra_greatsword_light_attack_2", (models) -> models.ENTITY_BIPED)
						.addProperty(StaticAnimationProperty.EVENTS, new Event[]
						{
								Event.create(0.72F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.ULTRA_GREATSWORD_SMASH.get())),
								Event.create(0.72F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 20, 1))
						})
						.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
						.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
						.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
						.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
	};
	
	public static final AttackAnimation[] ULTRA_GREATSWORD_HEAVY_ATTACK = new AttackAnimation[]
	{
			new AttackAnimation(0.5F, 0.0F, 1.35F, 1.6F, 3.45F, "Tool_R", "biped/combat/ultra_greatsword_heavy_attack_1", (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(1.5F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.ULTRA_GREATSWORD_SMASH.get())),
						Event.create(1.5F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 20, 1))
				})
				.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
				.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
				.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F)),
			new AttackAnimation(0.5F, 0.0F, 1.35F, 1.6F, 3.45F, "Tool_R", "biped/combat/ultra_greatsword_heavy_attack_2", (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(1.5F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.ULTRA_GREATSWORD_SMASH.get())),
						Event.create(1.5F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 20, 1))
				})
				.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
				.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
	};
	public static final AttackAnimation ULTRA_GREATSWORD_DASH_ATTACK = new AttackAnimation(0.1F, 0.0F, 1.35F, 1.6F, 3.45F, "Tool_R", "biped/combat/ultra_greatsword_heavy_attack_1", (models) -> models.ENTITY_BIPED)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[]
			{
					Event.create(1.6F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.ULTRA_GREATSWORD_SMASH.get())),
					Event.create(1.6F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 20, 1))
			})
			.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
			.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
			.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
			.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F));

	// Spear
	public static final AttackAnimation SPEAR_DASH_ATTACK = new AttackAnimation(0.2F, 0.0F, 0.15F, 0.3F, 1.0F, "Tool_R", "biped/combat/spear_dash_attack", (models) -> models.ENTITY_BIPED)
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.THRUST)
			.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
			.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
			.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT);
	public static final AttackAnimation SPEAR_HEAVY_ATTACK = new AttackAnimation(0.35F, 0.0F, 0.65F, 0.8F, 1.75F, "Tool_R", "biped/combat/spear_heavy_attack", (models) -> models.ENTITY_BIPED)
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.THRUST)
			.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
			.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
			.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM);
	public static final AttackAnimation SPEAR_LIGHT_ATTACK = new AttackAnimation(0.15F, 0.0F, 0.65F, 0.8F, 1.5F, "Tool_R", "biped/combat/spear_light_attack", (models) -> models.ENTITY_BIPED)
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
			.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
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
			.addProperty(StaticAnimationProperty.EVENTS, new Event[]
			{
					Event.create(1.8F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.GREAT_HAMMER_SMASH.get())),
					Event.create(1.8F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 20, 1))
			})
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE)
			.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
			.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
			.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
			.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F));
	public static final AttackAnimation[] GREAT_HAMMER_LIGHT_ATTACK = new AttackAnimation[]
	{
			new AttackAnimation(0.5F, 0.0F, 1.35F, 1.6F, 3.45F, "Tool_R", "biped/combat/great_hammer_light_attack_1", (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(1.5F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.GREAT_HAMMER_SMASH.get())),
						Event.create(1.5F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 20, 1))
				})
				.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
				.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F)),
			new AttackAnimation(0.5F, 0.0F, 1.35F, 1.6F, 3.45F, "Tool_R", "biped/combat/great_hammer_light_attack_2", (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(1.5F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.GREAT_HAMMER_SMASH.get())),
						Event.create(1.5F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 20, 1))
				})
				.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
				.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F))
	};
	public static final AttackAnimation GREAT_HAMMER_DASH_ATTACK = new AttackAnimation(0.1F, 0.0F, 1.35F, 1.6F, 3.45F, "Tool_R", "biped/combat/great_hammer_light_attack_1", (models) -> models.ENTITY_BIPED)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[]
			{
					Event.create(1.5F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.GREAT_HAMMER_SMASH.get())),
					Event.create(1.5F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 20, 1))
			})
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE)
			.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
			.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
			.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
			.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F));

	// Axe
	public static final AttackAnimation AXE_HEAVY_ATTACK = new AttackAnimation(0.3F, 0.0F, 0.55F, 0.7F, 1.5F, "Tool_R", "biped/combat/axe_heavy_attack", (models) -> models.ENTITY_BIPED)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.4F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.AXE_SWING.get())) })
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
			.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
			.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
			.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY);
	public static final AttackAnimation[] AXE_LIGHT_ATTACK = new AttackAnimation[]
	{
			new AttackAnimation(0.3F, 0.0F, 0.2F, 0.35F, 1.5F, "Tool_R", "biped/combat/axe_light_attack_1", (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.16F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.AXE_SWING.get())) })
				.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
				.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM),
			new AttackAnimation(0.2F, 0.0F, 0.15F, 0.4F, 1.25F, "Tool_R", "biped/combat/axe_light_attack_2", (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.12F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.AXE_SWING.get())) })
				.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
				.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM) };
	public static final AttackAnimation AXE_DASH_ATTACK = new AttackAnimation(0.2F, 0.0F, 0.4F, 0.5F, 1.5F, "Tool_R", "biped/combat/axe_dash_attack", (models) -> models.ENTITY_BIPED)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.35F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.AXE_SWING.get())) })
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
			.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
			.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
			.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY);

	// Hammer
	public static final AttackAnimation HAMMER_DASH_ATTACK = new AttackAnimation(0.5F, 0.0F, 0.32F, 0.6F, 1.4F, "Tool_R", "biped/combat/hammer_dash_attack", (models) -> models.ENTITY_BIPED)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.32F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.AXE_SWING.get())) })
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE)
			.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
			.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
			.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY);
	public static final AttackAnimation HAMMER_HEAVY_ATTACK = new AttackAnimation(0.5F, 0.0F, 0.32F, 0.52F, 1.4F, "Tool_R", "biped/combat/hammer_heavy_attack", (models) -> models.ENTITY_BIPED)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.32F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.AXE_SWING.get())) })
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE)
			.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
			.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
			.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY);
	public static final AttackAnimation[] HAMMER_LIGHT_ATTACK = new AttackAnimation[]
	{
			new AttackAnimation(0.5F, 0.0F, 0.28F, 0.52F, 1.2F, "Tool_R", "biped/combat/hammer_light_attack", (models) -> models.ENTITY_BIPED)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.28F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.AXE_SWING.get())) })
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
			.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
			.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT);
	public static final AttackAnimation FIST_HEAVY_ATTACK = new AttackAnimation(0.5F, 0.0F, 0.35F, 0.5F, 1.25F, "Tool_R", "biped/combat/fist_heavy_attack", (models) -> models.ENTITY_BIPED)
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.STRIKE)
			.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
			.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
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
			new AttackAnimation(0.2F, 0.0F, 0.24F, 0.45F, 1.0F, "Tool_R", "biped/combat/straight_sword_light_attack_1", (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
				.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT),
			new AttackAnimation(0.2F, 0.0F, 0.08F, 0.3F, 1.25F, "Tool_R", "biped/combat/straight_sword_light_attack_2", (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
				.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
	};
	public static final AttackAnimation STRAIGHT_SWORD_HEAVY_ATTACK = new AttackAnimation(0.2F, 0.0F, 0.56F, 0.8F, 1.5F, "Tool_R", "biped/combat/straight_sword_heavy_attack", (models) -> models.ENTITY_BIPED)
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.SLASH)
			.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
			.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
			.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM);
	public static final AttackAnimation STRAIGHT_SWORD_DASH_ATTACK = new AttackAnimation(0.2F, 0.0F, 0.12F, 0.56F, 1.0F, "Tool_R", "biped/combat/straight_sword_dash_attack", (models) -> models.ENTITY_BIPED)
			.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
			.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
			.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
			.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM);

	// Hollow
	public static final StaticAnimation HOLLOW_IDLE = new StaticAnimation(0.2F, true, "hollow/idle", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation HOLLOW_WALK = new MovementAnimation(0.2F, true, "hollow/move", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation HOLLOW_RUN = new MovementAnimation(0.2F, true, "hollow/run", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation HOLLOW_DEFLECTED = new HitAnimation(0.2F, "hollow/deflected", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation HOLLOW_BREAKDOWN = new StaticAnimation(0.2F, true, "hollow/breakdown", (models) -> models.ENTITY_BIPED);

	public static final AttackAnimation[] HOLLOW_LIGHT_ATTACKS = new AttackAnimation[]
	{
			new AttackAnimation(0.2F, 0.0F, 0.56F, 1.05F, 2.5F, Colliders.BROKEN_SWORD, "Tool_R", "hollow/swing_1", (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT),
			new AttackAnimation(0.2F, 0.0F, 0.48F, 1.0F, 2.0F, Colliders.BROKEN_SWORD, "Tool_R", "hollow/swing_2", (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT),
			new AttackAnimation(0.2F, 0.0F, 0.16F, 0.4F, 2.0F, Colliders.BROKEN_SWORD, "Tool_R", "hollow/swing_3", (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
	};
	public static final AttackAnimation HOLLOW_BARRAGE = new AttackAnimation(0.2F, "hollow/fury_attack", (models) -> models.ENTITY_BIPED,
			new Phase(0.0F, 1.48F, 1.72F, 1.72F, "Tool_R", Colliders.BROKEN_SWORD),
			new Phase(1.72F, 1.8F, 1.92F, 1.92F, "Tool_R", Colliders.BROKEN_SWORD),
			new Phase(1.92F, 2.12F, 2.24F, 2.24F, "Tool_R", Colliders.BROKEN_SWORD),
			new Phase(2.24F, 2.4F, 2.56F, 2.56F, "Tool_R", Colliders.BROKEN_SWORD),
			new Phase(2.56F, 2.76F, 2.88F, 2.88F, "Tool_R", Colliders.BROKEN_SWORD),
			new Phase(2.88F, 3.08F, 3.2F, 4.2F, "Tool_R", Colliders.BROKEN_SWORD))
					.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT)
					.addProperty(StaticAnimationProperty.EVENTS, new Event[] { Event.create(0.04F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.HOLLOW_PREPARE.get())) });
	public static final AttackAnimation HOLLOW_OVERHEAD_SWING = new AttackAnimation(0.2F, 0.0F, 0.4F, 0.6F, 1.2F, Colliders.BROKEN_SWORD, "Tool_R", "hollow/overhead_swing", (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
					.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
					.addProperty(AttackProperty.STAMINA_DMG_MUL, 2);
	public static final AttackAnimation HOLLOW_JUMP_ATTACK = new AttackAnimation(0.05F, 0.0F, 0.52F, 0.72F, 1.6F,
			Colliders.BROKEN_SWORD, "Tool_R", "hollow/jump_attack", (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
					.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
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
					.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
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
					.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
					.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY),
			new AttackAnimation(0.2F, 0.0F, 0.68F, 1.0F, 2.0F, "Tool_R", "hollow_lordran_warrior/axe_th_la_2",
					(models) -> models.ENTITY_BIPED).addProperty(AttackProperty.DAMAGE_TYPE, DamageType.SLASH)
							.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
							.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
							.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY) };

	// Hollow Lordran Soldier
	public static final StaticAnimation HOLLOW_LORDRAN_SOLDIER_WALK = new MovementAnimation(0.2F, true, "hollow_lordran_soldier/walking", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation HOLLOW_LORDRAN_SOLDIER_RUN = new MovementAnimation(0.2F, true, "hollow_lordran_soldier/run", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation HOLLOW_LORDRAN_SOLDIER_BLOCK = new AdaptableAnimation(0.2F, true, (models) -> models.ENTITY_BIPED,
			new AnimConfig(LivingMotion.BLOCKING, "hollow_lordran_soldier/block", "hollow_lordran_soldier/block", false),
			new AnimConfig(LivingMotion.WALKING, "hollow_lordran_soldier/block_walking", "hollow_lordran_soldier/block_walking", true),
			new AnimConfig(LivingMotion.RUNNING, "hollow_lordran_soldier/block_run", "hollow_lordran_soldier/block_run", true));
	
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
			.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
			.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM);

	public static final AttackAnimation HOLLOW_LORDRAN_SOLDIER_SWORD_HEAVY_THRUST = new AttackAnimation(0.2F, 0.0F,
			1.0F, 1.16F, 2.0F, "Tool_R", "hollow_lordran_soldier/sword_heavy_thrust",
			(models) -> models.ENTITY_BIPED).addProperty(AttackProperty.DAMAGE_TYPE, DamageType.THRUST)
					.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
					.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
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
							.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
							.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY),
			new AttackAnimation(0.2F, 0.0F, 0.44F, 0.6F, 1.6F, "Tool_R", "hollow_lordran_soldier/spear_swing_4",
					(models) -> models.ENTITY_BIPED).addProperty(AttackProperty.DAMAGE_TYPE, DamageType.THRUST)
							.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT) };

	public static final AttackAnimation[] HOLLOW_LORDRAN_SOLDIER_SPEAR_THRUSTS = new AttackAnimation[]
	{ 
			new AttackAnimation(0.2F, 0.0F, 0.64F, 0.8F, 1.6F, "Tool_R", "hollow_lordran_soldier/spear_thrust_1",
					(models) -> models.ENTITY_BIPED).addProperty(AttackProperty.DAMAGE_TYPE, DamageType.THRUST)
					.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT),
			new AttackAnimation(0.2F, 0.0F, 0.72F, 0.88F, 1.6F, "Tool_R",
					"hollow_lordran_soldier/spear_thrust_2", (models) -> models.ENTITY_BIPED)
							.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.THRUST)
							.addProperty(AttackProperty.DEFLECTION, Deflection.LIGHT),
			new AttackAnimation(0.2F, 0.0F, 0.88F, 1.04F, 1.6F, "Tool_R",
					"hollow_lordran_soldier/spear_thrust_3", (models) -> models.ENTITY_BIPED)
							.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.THRUST)
							.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
							.addProperty(AttackProperty.DEFLECTION, Deflection.MEDIUM)
	};

	public static final AttackAnimation HOLLOW_LORDRAN_SOLDIER_SHIELD_BASH = new AttackAnimation(0.2F, 0.0F, 0.6F, 0.8F,
			1.6F, "Tool_L", "hollow_lordran_soldier/shield_bash", (models) -> models.ENTITY_BIPED)
					.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
					.addProperty(AttackProperty.STAMINA_DMG_MUL, 2)
					.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
					.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
					.addProperty(AttackProperty.SWING_SOUND, ModSoundEvents.FIST_SWING);
	
	// Falconer
	public static final StaticAnimation FALCONER_IDLE = new StaticAnimation(1.0F, true, "falconer/idle", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation FALCONER_WALK = new MovementAnimation(0.2F, true, "falconer/walking", (models) -> models.ENTITY_BIPED);
	public static final StaticAnimation FALCONER_RUN = new MovementAnimation(0.2F, true, "falconer/run", (models) -> models.ENTITY_BIPED);
	
	public static final AttackAnimation[] FALCONER_SWINGS = new AttackAnimation[]
	{
			new AttackAnimation(0.2F, 0.0F, 0.56F, 0.68F, 1.88F, "Tool_R", "falconer/swing_1", (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY),
			new AttackAnimation(0.1F, 0.0F, 0.72F, 1.04F, 1.88F, "Tool_R", "falconer/swing_2", (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY),
			new AttackAnimation(0.1F, 0.0F, 0.52F, 0.68F, 1.88F, "Tool_R", "falconer/swing_3", (models) -> models.ENTITY_BIPED)
				.addProperty(AttackProperty.DAMAGE_TYPE, DamageType.REGULAR)
				.addProperty(AttackProperty.DEFLECTION, Deflection.HEAVY)
				.addProperty(AttackProperty.STUN_TYPE, StunType.HEAVY)
	};
	
	

	// Stray Demon
	public static final StaticAnimation STRAY_DEMON_IDLE = new StaticAnimation(1.0F, true, "asylum_demon/idle", (models) -> models.ENTITY_STRAY_DEMON);
	public static final StaticAnimation STRAY_DEMON_MOVE = new StaticAnimation(0.5F, true, "asylum_demon/move", (models) -> models.ENTITY_STRAY_DEMON)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[]
			{
					Event.create(0.4F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_FOOT.get())),
					Event.create(0.4F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 10, 0.5F)),
					Event.create(1.2F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_FOOT.get())),
					Event.create(1.2F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 10, 0.5F))
			});
	public static final DeathAnimation STRAY_DEMON_DEATH = new DeathAnimation(0.5F, "asylum_demon/death", (models) -> models.ENTITY_STRAY_DEMON);

	public static final AttackAnimation[] STRAY_DEMON_LIGHT_ATTACK = new AttackAnimation[]
	{
			new AttackAnimation(1.0F, 0.0F, 0.52F, 1.0F, 2.0F, "Tool_R", "asylum_demon/light_attack_1", (models) -> models.ENTITY_STRAY_DEMON)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(0.52F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_SWING.get())),
						Event.create(0.52F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 10, 0.5F))
				})
				.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
				.addProperty(AttackProperty.STUN_TYPE, StunType.FLY)
				.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F)),
			new AttackAnimation(1.0F, 0.0F, 0.6F, 0.92F, 2.0F, "Tool_R", "asylum_demon/light_attack_2", (models) -> models.ENTITY_STRAY_DEMON)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(0.6F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_SWING.get())),
						Event.create(0.6F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 10, 0.5F))
				})
				.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
				.addProperty(AttackProperty.STUN_TYPE, StunType.FLY)
				.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F)),
			new AttackAnimation(1.0F, 0.0F, 0.6F, 0.84F, 1.2F, "Tool_R", "asylum_demon/light_attack_3", (models) -> models.ENTITY_STRAY_DEMON)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[]
				{
						Event.create(0.72F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_SMASH.get())),
						Event.create(0.72F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 25, 1.5F))
				})
				.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
				.addProperty(AttackProperty.STUN_TYPE, StunType.FLY)
				.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F)) };
	public static final AttackAnimation STRAY_DEMON_HAMMER_DRIVE = new AttackAnimation(1.0F, 0.0F, 0.64F, 1.04F, 2.8F, "Tool_R", "asylum_demon/heavy_attack", (models) -> models.ENTITY_STRAY_DEMON)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[]
			{
					Event.create(0.92F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_SMASH.get())),
					Event.create(0.92F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 25, 1.5F))
			})
			.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
			.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
			.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F));
	public static final AttackAnimation STRAY_DEMON_JUMP_ATTACK = new AttackAnimation(1.0F, 0.0F, 0.6F, 1.2F, 2.0F,
			"Tool_R", "asylum_demon/dash_attack", (models) -> models.ENTITY_STRAY_DEMON)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[]
			{
					Event.create(0.04F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_WING.get())),
					Event.create(0.8F, Side.CLIENT, (cap) -> { cap.playSound(ModSoundEvents.STRAY_DEMON_LAND.get()); cap.playSound(ModSoundEvents.STRAY_DEMON_SMASH.get()); }),
					Event.create(0.8F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 25, 1.5F))
			})
			.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
			.addProperty(AttackProperty.STUN_TYPE, StunType.SMASH)
			.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.1F));
	public static final AttackAnimation STRAY_DEMON_GROUND_POUND = new AttackAnimation(1.0F, 0.0F, 2.48F, 2.76F, 4.0F, Colliders.STRAY_DEMON_BODY, "Root", "asylum_demon/ground_pound", (models) -> models.ENTITY_STRAY_DEMON)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[]
			{
					Event.create(0.4F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_WING.get())),
					Event.create(1.0F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_WING.get())),
					Event.create(1.76F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_WING.get())),
					Event.create(2.76F, Side.CLIENT, (cap) -> cap.playSound(ModSoundEvents.STRAY_DEMON_LAND.get())),
					Event.create(2.76F, Side.CLIENT, (cap) -> ModNetworkManager.connection.shakeCam(cap.getOriginalEntity().position(), 40, 3.0F))
			})
			.addProperty(AttackProperty.DEFLECTION, Deflection.IMPOSSIBLE)
			.addProperty(AttackProperty.STUN_TYPE, StunType.FLY)
			.addProperty(AttackProperty.PARTICLE, new CircleParticleSpawner(ModParticles.DUST_CLOUD, 3, 0.25F));
	
	// Anastacia of Astora
	public static final StaticAnimation ANASTACIA_IDLE = new StaticAnimation(0.4F, true, "anastacia_of_astora/idle", (models) -> models.ENTITY_BIPED);

	@OnlyIn(Dist.CLIENT)
	public static void buildClient() {}
}