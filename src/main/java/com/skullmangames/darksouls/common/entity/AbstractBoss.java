package com.skullmangames.darksouls.common.entity;

import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public abstract class AbstractBoss extends PathfinderMob implements Demon
{
	private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS);
	
	public AbstractBoss(EntityType<? extends PathfinderMob> type, Level level)
	{
		super(type, level);
	}
	
	@Override
	protected void customServerAiStep()
	{
		super.customServerAiStep();
		this.bossInfo.setProgress((this.getHealth() / this.getMaxHealth()));
	}

	@Override
	public void stopSeenByPlayer(ServerPlayer player)
	{
	    super.stopSeenByPlayer(player);
	    this.bossInfo.removePlayer(player);
	}
	
	@Override
	public void setTarget(LivingEntity target)
	{
		super.setTarget(target);
		if (target instanceof ServerPlayer) this.bossInfo.addPlayer((ServerPlayer) target);
	}
	
	@Override
	public boolean removeWhenFarAway(double distance)
	{
		return false;
	}
}
