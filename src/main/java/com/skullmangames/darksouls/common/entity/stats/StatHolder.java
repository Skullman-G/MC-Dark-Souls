package com.skullmangames.darksouls.common.entity.stats;

import java.util.HashMap;
import java.util.Map;

import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.core.util.math.MathUtils;

import net.minecraft.nbt.CompoundTag;

public class StatHolder
{
	private final Map<String, Integer> statValues = new HashMap<>();
	private final PlayerCap<?> playerCap;
	
	public StatHolder(PlayerCap<?> playerCap)
	{
		this.playerCap = playerCap;
		for (Stat stat : Stats.STATS.values()) this.statValues.putIfAbsent(stat.getName(), Stats.STANDARD_LEVEL);
	}
	
	public int getStatValue(Stat stat)
	{
		return this.getStatValue(stat.getName());
	}
	
	public int getStatValue(String name)
	{
		return this.statValues.get(name);
	}
	
	private void setStatValue(String name, int value)
	{
		this.statValues.put(name, value);
	}
	
	public void loadStats(CompoundTag nbt)
	{
		ChangeRequest request = this.requestChange();
		for (String name : Stats.STATS.keySet())
		{
			int value = MathUtils.clamp(nbt.getInt(name), Stats.STANDARD_LEVEL, Stats.MAX_LEVEL);
			request.set(name, value);
		}
		request.finish();
	}
	
	public void saveStats(CompoundTag nbt)
	{
		Stats.STATS.forEach((name, stat) ->
		{
			nbt.putInt(stat.getName(), this.statValues.getOrDefault(name, Stats.STANDARD_LEVEL));
		});
	}
	
	private void onChange()
	{
		this.statValues.forEach((stat, value) ->
		{
			Stats.STATS.get(stat).onChange(this.playerCap, value);
		});
		Stats.changeWeaponScalingAttributes(this.playerCap);
	}
	
	public int getLevel()
	{
		int level = 1;
		for (Stat stat : Stats.STATS.values())
		{
			level += this.getStatValue(stat) - Stats.STANDARD_LEVEL;
		}
		
		return level;
	}
	
	public ChangeRequest requestChange()
	{
		return new ChangeRequest(this);
	}
	
	public static class ChangeRequest
	{
		private final StatHolder stats;
		private final Map<String, Integer> values = new HashMap<>();
		
		private ChangeRequest(StatHolder stats)
		{
			this.stats = stats;
		}
		
		public Map<String, Integer> getChanges()
		{
			return Map.copyOf(this.values);
		}
		
		public ChangeRequest set(String stat, int value)
		{
			this.values.put(stat, MathUtils.clamp(value, Stats.STANDARD_LEVEL, Stats.MAX_LEVEL));
			return this;
		}
		
		public ChangeRequest set(Stat stat, int value)
		{
			this.set(stat.getName(), value);
			return this;
		}
		
		public ChangeRequest set(Map<String, Integer> changes)
		{
			changes.forEach((stat, value) ->
			{
				this.set(stat, value);
			});
			return this;
		}
		
		public void finish()
		{
			this.values.forEach((stat, value) ->
			{
				this.stats.setStatValue(stat, value);
			});
			this.stats.onChange();
		}
	}
}
