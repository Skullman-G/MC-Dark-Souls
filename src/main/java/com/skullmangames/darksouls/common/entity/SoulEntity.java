package com.skullmangames.darksouls.common.entity;

import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.init.ModEntities;
import com.skullmangames.darksouls.core.init.ModParticles;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class SoulEntity extends AbstractSoulEntity
{
	private int discardTime = 25;
	
	public SoulEntity(EntityType<? extends SoulEntity> type, Level level)
	{
		super(type, level);
	}
	
	public SoulEntity(Level level, double posX, double posY, double posZ, int value)
	{
		super(ModEntities.SOUL.get(), level, posX, posY, posZ);
	}
	
	@Override
	public void playerTouch(Player player)
	{
		if (!this.level.isClientSide)
		{
			if (this.value > 0)
			{
				PlayerCap<?> playerdata = (PlayerCap<?>) player.getCapability(ModCapabilities.CAPABILITY_ENTITY, null)
						.orElse(null);
				if (playerdata != null)
					playerdata.raiseSouls(this.value);
				this.value = 0;
			}
			else if (--discardTime <= 0) this.discard();
		}
	}

	@Override
	protected void makeParticles()
	{
		this.level.addParticle(ModParticles.SOUL.get(), this.getX(), this.getY() + 0.2D, this.getZ(), 0, 0, 0);
	}
}
