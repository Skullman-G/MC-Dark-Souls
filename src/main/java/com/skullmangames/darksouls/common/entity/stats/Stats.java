package com.skullmangames.darksouls.common.entity.stats;

import java.util.ArrayList;
import java.util.List;
import com.skullmangames.darksouls.core.init.ModAttributes;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;

public class Stats
{
	public static final int STANDARD_LEVEL = 10;
	public static final List<Stat> STATS = new ArrayList<>();
	
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
					return -0.0065D * (value - STANDARD_LEVEL) * (value - 188);
				}
			});
	public static final ModifyingStat ENDURANCE = register(new ModifyingStat("endurance", "8bbd5d2d-0188-41be-a673-cfca6cd8da8c", ModAttributes.MAX_STAMINA)
			{
				@Override
				public double getModifyValue(Player player, int value)
				{
					return -0.0065D * (value - STANDARD_LEVEL) * (value - 188);
				}
			});
	public static final ModifyingStat VITALITY = register(new ModifyingStat("vitality", "1858d77f-b8fd-46a7-a9e1-373e5a2dac0a", ModAttributes.MAX_EQUIP_LOAD)
			{
				@Override
				public double getModifyValue(Player player, int value)
				{
					return -0.019D * (value - STANDARD_LEVEL) * (value - 188);
				}
			});
	public static final ScalingStat STRENGTH = register(new ScalingStat("strength", "c16888c7-e522-4260-8492-0a2da90482b8"));
	public static final ScalingStat DEXTERITY = register(new ScalingStat("dexterity", "2e316050-52aa-446f-8b05-0abefbbb6cb2"));
	
	private static <T extends Stat>T register(T stat)
	{
		STATS.add(stat);
		return stat;
	}
	
	public static int getCost(int level)
	{
		return level * (10 + level);
	}
	
	public static double getTotalDamageAmount(Player player, int strength, int dex)
	{
		return STRENGTH.getModifyValue(player, strength)
				+ DEXTERITY.getModifyValue(player, dex);
	}
	
	private final int[] statValues = new int[STATS.size()];
	
	public int getStatValue(Stat stat)
	{
		return this.getStatValue(STATS.indexOf(stat));
	}
	
	public int getStatValue(int index)
	{
		return this.statValues[index];
	}
	
	public void setStatValue(Player player, Stat stat, int value)
	{
		this.setStatValue(player, STATS.indexOf(stat), value);
	}
	
	public void setStatValue(Player player, int index, int value)
	{
		this.statValues[index] = value;
		STATS.get(index).onChange(player, value);
	}
	
	public void loadStats(Player player, CompoundTag nbt)
	{
		for (int i = 0; i < STATS.size(); i++)
		{
			int value = Math.max(STANDARD_LEVEL, Math.min(nbt.getInt(STATS.get(i).toString()), 99));
			this.initStatValue(player, i, value);
		}
	}
	
	public void initStatValue(Player player, int index, int value)
	{
		this.statValues[index] = value;
		STATS.get(index).init(player, value);
	}
	
	public void saveStats(CompoundTag nbt)
	{
		for (int i = 0; i < STATS.size(); i++)
		{
			nbt.putInt(STATS.get(i).toString(), this.statValues[i]);
		}
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
