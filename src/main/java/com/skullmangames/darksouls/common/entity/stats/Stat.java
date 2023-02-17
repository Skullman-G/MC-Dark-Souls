package com.skullmangames.darksouls.common.entity.stats;

import com.skullmangames.darksouls.DarkSouls;

import net.minecraft.entity.player.PlayerEntity;

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
	
	public void onChange(PlayerEntity player, int value) {}
	
	public void init(PlayerEntity player, int value) {}
}
