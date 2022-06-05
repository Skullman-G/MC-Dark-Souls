package com.skullmangames.darksouls.common.entity;

import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.core.init.ModEntities;
import com.skullmangames.darksouls.core.init.ModParticles;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class HumanityEntity extends AbstractSoulEntity
{
	public HumanityEntity(EntityType<? extends HumanityEntity> type, Level level)
	{
		super(type, level);
	}
	
	public HumanityEntity(Level level, double posX, double posY, double posZ, int value)
	{
		super(ModEntities.HUMANITY.get(), level, posX, posY, posZ, value);
	}
	
	@Override
	protected void realPlayerTouch(PlayerCap<?> playerCap)
	{
		Player player = playerCap.getOriginalEntity();
		playerCap.raiseHumanity(this.value);
		player.heal(player.getMaxHealth() - player.getHealth());
		this.value = 0;
	}

	@Override
	protected void makeParticles()
	{
		this.level.addParticle(ModParticles.HUMANITY.get(), this.getX(), this.getY() + 0.2D, this.getZ(), 0, 0, 0);
	}
}
