package com.skullmangames.darksouls.common.entity;

import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.core.init.ModEntities;
import com.skullmangames.darksouls.core.init.ModParticles;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public class SoulEntity extends AbstractSoulEntity
{
	public SoulEntity(EntityType<? extends SoulEntity> type, World level)
	{
		super(type, level);
	}
	
	public SoulEntity(World level, double posX, double posY, double posZ, int value)
	{
		super(ModEntities.SOUL.get(), level, posX, posY, posZ, value);
	}
	
	@Override
	protected void realPlayerTouch(PlayerCap<?> playerCap)
	{
		playerCap.raiseSouls(this.value);
		this.value = 0;
	}

	@Override
	protected void makeParticles()
	{
		this.level.addParticle(ModParticles.SOUL.get(), this.getX(), this.getY() + 0.2D, this.getZ(), 0, 0, 0);
	}
}
