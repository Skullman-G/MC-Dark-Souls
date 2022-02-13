package com.skullmangames.darksouls.common.entity.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCStat;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public class Stats
{
	public static List<Stat> STATS = new ArrayList<>();
	
	public static final ModifyingStat VIGOR = register(new ModifyingStat("vigor", "35031b47-45fa-401b-92dc-12b6d258e553", () -> Attributes.MAX_HEALTH)
			{
				@Override		
				public void onChange(Player player, int value)
				{
					super.onChange(player, value);
					player.setHealth(player.getMaxHealth());
				}
				
				@Override
				public double getModifyValue(Player player, int value)
				{
					return -0.0065D * (value - 10) * (value - 188);
				}
			});
	public static final ModifyingStat ENDURANCE = register(new ModifyingStat("endurance", "8bbd5d2d-0188-41be-a673-cfca6cd8da8c", ModAttributes.MAX_STAMINA)
			{
				@Override
				public double getModifyValue(Player player, int value)
				{
					return -0.0065D * (value - 10) * (value - 188);
				}
			});
	public static final ModifyingStat VITALITY = register(new ModifyingStat("vitality", "1858d77f-b8fd-46a7-a9e1-373e5a2dac0a", ModAttributes.MAX_EQUIP_LOAD)
			{
				@Override
				public double getModifyValue(Player player, int value)
				{
					return -0.019D * (value - 10) * (value - 188);
				}
			});
	public static final ScalingStat STRENGTH = register(new ScalingStat("strength", "c16888c7-e522-4260-8492-0a2da90482b8"));
	public static final ScalingStat DEXTERITY = register(new ScalingStat("dexterity", "2e316050-52aa-446f-8b05-0abefbbb6cb2"));
	
	private static <T extends Stat>T register(T stat)
	{
		STATS.add(stat);
		return stat;
	}
	
	public static double getTotalDamageAmount(Player player, int strength, int dex)
	{
		return STRENGTH.getModifyValue(player, strength)
				+ DEXTERITY.getModifyValue(player, dex);
	}
	
	private final Map<String, Integer> statValues = new HashMap<>();
	
	public int getStatValue(Stat stat)
	{
		return statValues.getOrDefault(stat.toString(), 10);
	}
	
	public void setStatValue(Player player, Stat stat, int value)
	{
		this.statValues.put(stat.toString(), value);
		stat.onChange(player, value);
	}
	
	public void setStatValue(Player player, String statname, int value)
	{
		this.statValues.put(statname, value);
		for (Stat stat : STATS) if (stat.toString().equals(statname)) stat.onChange(player, value);
	}
	
	public void loadStats(ServerPlayer player, CompoundTag nbt)
	{
		for (Stat stat : STATS)
		{
			int value = nbt.getInt(stat.toString());
			if (value <= 0) value = 10;
			this.initStatValue(player, stat.toString(), value);
			ModNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(new STCStat(player.getId(), stat.toString(), value), player);
		}
	}
	
	public void initStatValue(Player player, String statname, int value)
	{
		this.statValues.put(statname, value);
		STATS.forEach((stat) -> { if (stat.toString() == statname) stat.init(player, value); });
	}
	
	public void saveStats(CompoundTag nbt)
	{
		this.statValues.forEach((name, value) ->
		{
			nbt.putInt(name, value);
		});
	}
	
	public int getLevel()
	{
		int level = 1;
		for (Stat stat : STATS)
		{
			level += this.getStatValue(stat) - 10;
		}
		
		return level;
	}
}
