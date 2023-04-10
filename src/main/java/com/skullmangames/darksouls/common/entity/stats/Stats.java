package com.skullmangames.darksouls.common.entity.stats;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;

public class Stats
{
	public static final int STANDARD_LEVEL = 10;
	public static final int MAX_LEVEL = 99;
	public static final Map<String, Stat> STATS = new LinkedHashMap<>();
	
	public static final ModifyingStat VIGOR = register(new ModifyingStat("vigor", "35031b47-45fa-401b-92dc-12b6d258e553", () -> Attributes.MAX_HEALTH)
	{
		@Override
		public void onChange(Player player, int value)
		{
			super.onChange(player, value);
			player.setHealth(player.getMaxHealth());
		}

		@Override
		public double getModifyValue(Player player, Attribute attribute, int value)
		{
			return -0.0065D * (value - STANDARD_LEVEL) * (value - MAX_LEVEL * 2 + STANDARD_LEVEL);
		}
	});
	public static final ModifyingStat ENDURANCE = register(new ModifyingStat("endurance", "8bbd5d2d-0188-41be-a673-cfca6cd8da8c", ModAttributes.MAX_STAMINA)
	{
		@Override
		public void onChange(Player player, int value)
		{
			super.onChange(player, value);
			PlayerCap<?> cap = (PlayerCap<?>)player.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
			if (cap != null) cap.setStamina(cap.getMaxStamina());
		}

		@Override
		public double getModifyValue(Player player, Attribute attribute, int value)
		{
			return -0.0065D * (value - STANDARD_LEVEL) * (value - MAX_LEVEL * 2 + STANDARD_LEVEL);
		}
	});
	public static final ModifyingStat VITALITY = register(new ModifyingStat("vitality", "1858d77f-b8fd-46a7-a9e1-373e5a2dac0a", ModAttributes.MAX_EQUIP_LOAD)
	{
		@Override
		public double getModifyValue(Player player, Attribute attribute, int value)
		{
			return -0.019D * (value - STANDARD_LEVEL) * (value - MAX_LEVEL * 2 + STANDARD_LEVEL);
		}
	});
	public static final ModifyingStat ATTUNEMENT = register(new ModifyingStat("attunement", "25c989d7-9585-4f9d-be78-fc1e3aba4fc6", ModAttributes.MAX_FOCUS_POINTS, ModAttributes.ATTUNEMENT_SLOTS)
	{
		@Override
		public void onChange(Player player, int value)
		{
			super.onChange(player, value);
			PlayerCap<?> playerCap = (PlayerCap<?>) player.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
			if (playerCap != null)
			{
				playerCap.getAttunements().updateSize();
				playerCap.setFP(playerCap.getMaxFP());
			}
		}

		@Override
		public void init(Player player, int value)
		{
			super.init(player, value);
			PlayerCap<?> playerCap = (PlayerCap<?>) player.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
			if (playerCap != null)
			{
				playerCap.getAttunements().updateSize();
				playerCap.setFP(playerCap.getMaxFP());
			}
		}

		@Override
		public double getModifyValue(Player player, Attribute attribute, int value)
		{
			if (attribute == ModAttributes.ATTUNEMENT_SLOTS.get())
			{
				return (int) (-0.001262468 * (value - STANDARD_LEVEL) * (value - MAX_LEVEL * 2 + STANDARD_LEVEL));
			}
			else return -0.0076 * (value - STANDARD_LEVEL) * (value - MAX_LEVEL * 2 + STANDARD_LEVEL);
		}
	});
	public static final ScalingStat STRENGTH = register(new ScalingStat("strength", "c16888c7-e522-4260-8492-0a2da90482b8"));
	public static final ScalingStat DEXTERITY = register(new ScalingStat("dexterity", "2e316050-52aa-446f-8b05-0abefbbb6cb2"));
	public static final ScalingStat FAITH = register(new ScalingStat("faith", "2939c660-37cc-4e0e-9cca-2b08d011f472"));
	
	private static <T extends Stat> T register(T stat)
	{
		STATS.put(stat.toString(), stat);
		return stat;
	}
	
	public static int getCost(int level)
	{
		return (int)(0.02F * Math.pow(level, 3) + 3.06F * Math.pow(level, 2) + 105.6F * level);
	}
	
	public static double getTotalDamageMultiplier(Player player, int strength, int dex, int faith)
	{
		return STRENGTH.getModifyValue(player, null, strength)
				+ DEXTERITY.getModifyValue(player, null, dex)
				+ FAITH.getModifyValue(player, null, faith);
	}
	
	public static double getTotalDamageMultiplier(Player player, float baseDamage, int strength, int dex, int faith)
	{
		return STRENGTH.getModifyValue(player, null, baseDamage, strength)
				+ DEXTERITY.getModifyValue(player, null, baseDamage, dex)
				+ FAITH.getModifyValue(player, null, baseDamage, faith);
	}
	
	private final Map<String, Integer> statValues = new HashMap<>();
	
	public int getStatValue(Stat stat)
	{
		return this.getStatValue(stat.getName());
	}
	
	public int getStatValue(String name)
	{
		return this.statValues.get(name);
	}
	
	public void setStatValue(Player player, Stat stat, int value)
	{
		this.setStatValue(player, stat.getName(), value);
	}
	
	public void setStatValue(Player player, String name, int value)
	{
		this.statValues.put(name, value);
		STATS.get(name).onChange(player, value);
	}
	
	public void loadStats(Player player, CompoundTag nbt)
	{
		for (String name : STATS.keySet())
		{
			int value = Math.max(STANDARD_LEVEL, Math.min(nbt.getInt(name), 99));
			this.initStatValue(player, name, value);
		}
	}
	
	public void initStatValue(Player player, String name, int value)
	{
		this.statValues.put(name, value);
		STATS.get(name).init(player, value);
	}
	
	public void saveStats(CompoundTag nbt)
	{
		STATS.forEach((name, stat) ->
		{
			nbt.putInt(stat.getName(), this.statValues.get(name));
		});
	}
	
	public int getLevel()
	{
		int level = 1;
		for (Stat stat : STATS.values())
		{
			level += this.getStatValue(stat) - STANDARD_LEVEL;
		}
		
		return level;
	}
}
