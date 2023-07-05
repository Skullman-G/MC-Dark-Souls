package com.skullmangames.darksouls.common.entity;

import com.skullmangames.darksouls.core.init.ModParticles;
import com.skullmangames.darksouls.core.init.ModSoundEvents;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class BreakableBarrel extends BreakableObject
{
	public BreakableBarrel(EntityType<? extends BreakableBarrel> type, Level level)
	{
		super(type, level);
	}

	@Override
	protected ParticleOptions getBreakParticle()
	{
		return ModParticles.WOOD_SPLINTER.get();
	}

	@Override
	protected SoundEvent getBreakSound()
	{
		return ModSoundEvents.BARREL_BREAK.get();
	}
}
