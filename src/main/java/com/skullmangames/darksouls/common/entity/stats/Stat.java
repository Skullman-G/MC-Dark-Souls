package com.skullmangames.darksouls.common.entity.stats;

import com.skullmangames.darksouls.DarkSouls;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;

public class Stat
{
	private final String name;
	
	public Stat(String name)
	{
		this.name = "stat."+DarkSouls.MOD_ID+"."+name;
	}
	
	@Override
	public String toString()
	{
		return this.name;
	}
	
	public void onChange(LivingEntity livingentity, boolean isinit, int value) {}
	
	public int getValue(LivingEntity livingentity)
	{
		if (livingentity instanceof ClientPlayerEntity) livingentity = Minecraft.getInstance().getSingleplayerServer().getPlayerList().getPlayer(livingentity.getUUID());
		return livingentity.getPersistentData().getInt(this.name) <= 0 ? 1 : livingentity.getPersistentData().getInt(this.name);
	}
	
	public void init(LivingEntity livingentity)
	{
		this.onChange(livingentity, true, this.getValue(livingentity));
	}
	
	public void setValue(LivingEntity livingentity, int value)
	{
		if (value <= 0 || value > 99 || livingentity.getPersistentData().getInt(this.name) == value) return;
		livingentity.getPersistentData().putInt(this.name, value);
		this.onChange(livingentity, false, value);
	}
}
