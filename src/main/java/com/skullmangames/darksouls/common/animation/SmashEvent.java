package com.skullmangames.darksouls.common.animation;

import java.util.function.Consumer;

import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation.Event.Side;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.ModParticles;
import com.skullmangames.darksouls.core.init.ModSoundEvents;
import com.skullmangames.darksouls.network.ModNetworkManager;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public interface SmashEvent extends Consumer<LivingCap<?>>
{
	public static final SmashEvent BIG_SWORD = (cap) ->
	{
		if (cap.weaponCollider != null)
		{
			Vec3 pos = cap.weaponCollider.getMassCenter();
			ModNetworkManager.connection.shakeCam(pos, 20, 1.0F);
			ModNetworkManager.connection.spawnParticlesCircle(ModParticles.DUST_CLOUD, pos, 0.1F);
			Level level = cap.getLevel();
			level.playLocalSound(pos.x, pos.y, pos.z, ModSoundEvents.ULTRA_GREATSWORD_SMASH.get(), SoundSource.AMBIENT,
					0.75F, 0.8F + level.random.nextFloat() * 0.3F, false);
		}
	};
	
	public static final SmashEvent BIG_HAMMER = (cap) ->
	{
		if (cap.weaponCollider != null)
		{
			Vec3 pos = cap.weaponCollider.getMassCenter();
			ModNetworkManager.connection.shakeCam(pos, 20, 1.0F);
			ModNetworkManager.connection.spawnParticlesCircle(ModParticles.DUST_CLOUD, pos, 0.1F);
			Level level = cap.getLevel();
			level.playLocalSound(pos.x, pos.y, pos.z, ModSoundEvents.GREAT_HAMMER_SMASH.get(), SoundSource.AMBIENT,
					0.75F, 0.8F + level.random.nextFloat() * 0.3F, false);
		}
	};
	
	public static final SmashEvent BIG_MONSTER_HAMMER = (cap) ->
	{
		if (cap.weaponCollider != null)
		{
			Vec3 pos = cap.weaponCollider.getMassCenter();
			ModNetworkManager.connection.shakeCam(pos, 25, 1.5F);
			ModNetworkManager.connection.spawnParticlesCircle(ModParticles.DUST_CLOUD, pos, 0.1F);
			Level level = cap.getLevel();
			level.playLocalSound(pos.x, pos.y, pos.z, ModSoundEvents.STRAY_DEMON_SMASH.get(), SoundSource.AMBIENT,
					0.75F, 0.8F + level.random.nextFloat() * 0.3F, false);
		}
	};
	
	public static final SmashEvent BIG_MONSTER_HAMMER_SWING = (cap) ->
	{
		if (cap.weaponCollider != null)
		{
			Vec3 pos = cap.weaponCollider.getMassCenter();
			ModNetworkManager.connection.shakeCam(pos, 20, 1.0F);
			ModNetworkManager.connection.spawnParticlesCircle(ModParticles.DUST_CLOUD, pos, 0.1F);
			Level level = cap.getLevel();
			level.playLocalSound(pos.x, pos.y, pos.z, ModSoundEvents.STRAY_DEMON_SWING.get(), SoundSource.AMBIENT,
					0.75F, 0.8F + level.random.nextFloat() * 0.3F, false);
		}
	};
	
	public static final SmashEvent BIG_MONSTER_LAND = (cap) ->
	{
		if (cap.weaponCollider != null)
		{
			Vec3 pos = cap.weaponCollider.getMassCenter();
			ModNetworkManager.connection.shakeCam(pos, 40, 3.0F);
			ModNetworkManager.connection.spawnParticlesCircle(ModParticles.DUST_CLOUD, pos, 0.25F);
			Level level = cap.getLevel();
			level.playLocalSound(pos.x, pos.y, pos.z, ModSoundEvents.STRAY_DEMON_LAND.get(), SoundSource.AMBIENT,
					0.75F, 0.8F + level.random.nextFloat() * 0.3F, false);
		}
	};
	
	public default StaticAnimation.Event create(float time)
	{
		return StaticAnimation.Event.create(time, Side.CLIENT, this);
	}
}
