package com.skullmangames.darksouls.common.entity.stats;

import java.util.ArrayList;
import java.util.List;

import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

public class Stats
{
	public static final int STANDARD_LEVEL = 10;
	public static final int MAX_LEVEL = 99;
	public static final List<Stat> STATS = new ArrayList<>();
	
	public static final ModifyingStat VIGOR = register(new ModifyingStat("vigor", "35031b47-45fa-401b-92dc-12b6d258e553", () -> Attributes.MAX_HEALTH)
			{
				@Override		
				public void onChange(PlayerEntity player, int value)
				{
					super.onChange(player, value);
					player.setHealth(player.getMaxHealth());
				}
				
				@Override
				public double getModifyValue(PlayerEntity player, Attribute attribute, int value)
				{
					return -0.0065D * (value - STANDARD_LEVEL) * (value - MAX_LEVEL * 2 + STANDARD_LEVEL);
				}
			});
	public static final ModifyingStat ENDURANCE = register(new ModifyingStat("endurance", "8bbd5d2d-0188-41be-a673-cfca6cd8da8c", ModAttributes.MAX_STAMINA)
			{
				@Override
				public double getModifyValue(PlayerEntity player, Attribute attribute, int value)
				{
					return -0.0065D * (value - STANDARD_LEVEL) * (value - MAX_LEVEL * 2 + STANDARD_LEVEL);
				}
			});
	public static final ModifyingStat VITALITY = register(new ModifyingStat("vitality", "1858d77f-b8fd-46a7-a9e1-373e5a2dac0a", ModAttributes.MAX_EQUIP_LOAD)
			{
				@Override
				public double getModifyValue(PlayerEntity player, Attribute attribute, int value)
				{
					return -0.019D * (value - STANDARD_LEVEL) * (value - MAX_LEVEL * 2 + STANDARD_LEVEL);
				}
			});
	public static final ModifyingStat ATTUNEMENT = register(new ModifyingStat("attunement", "25c989d7-9585-4f9d-be78-fc1e3aba4fc6", ModAttributes.MAX_FOCUS_POINTS, ModAttributes.ATTUNEMENT_SLOTS)
			{
				@Override
				public void onChange(PlayerEntity player, int value)
				{
					super.onChange(player, value);
					PlayerCap<?> playerCap = (PlayerCap<?>)player.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
					playerCap.getAttunements().updateSize();
				}
				
				@Override
				public void init(PlayerEntity player, int value)
				{
					super.init(player, value);
					PlayerCap<?> playerCap = (PlayerCap<?>)player.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
					playerCap.getAttunements().updateSize();
				}
				
				@Override
				public double getModifyValue(PlayerEntity player, Attribute attribute, int value)
				{
					if (attribute == ModAttributes.ATTUNEMENT_SLOTS.get())
					{
						return (int)(-0.001262468 * (value - STANDARD_LEVEL) * (value - MAX_LEVEL * 2 + STANDARD_LEVEL));
					}
					else return -0.0076 * (value - STANDARD_LEVEL) * (value - MAX_LEVEL * 2 + STANDARD_LEVEL);
				}
			});
	public static final ScalingStat STRENGTH = register(new ScalingStat("strength", "c16888c7-e522-4260-8492-0a2da90482b8"));
	public static final ScalingStat DEXTERITY = register(new ScalingStat("dexterity", "2e316050-52aa-446f-8b05-0abefbbb6cb2"));
	public static final ScalingStat FAITH = register(new ScalingStat("faith", "2939c660-37cc-4e0e-9cca-2b08d011f472"));
	
	private static <T extends Stat> T register(T stat)
	{
		STATS.add(stat);
		return stat;
	}
	
	public static int getCost(int level)
	{
		return level * (10 + level);
	}
	
	public static double getTotalDamageMultiplier(PlayerEntity player, int strength, int dex, int faith)
	{
		return STRENGTH.getModifyValue(player, null, strength)
				+ DEXTERITY.getModifyValue(player, null, dex)
				+ FAITH.getModifyValue(player, null, faith);
	}
	
	public static double getTotalDamageMultiplier(PlayerEntity player, float baseDamage, int strength, int dex, int faith)
	{
		return STRENGTH.getModifyValue(player, null, baseDamage, strength)
				+ DEXTERITY.getModifyValue(player, null, baseDamage, dex)
				+ FAITH.getModifyValue(player, null, baseDamage, faith);
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
	
	public void setStatValue(PlayerEntity player, Stat stat, int value)
	{
		this.setStatValue(player, STATS.indexOf(stat), value);
	}
	
	public void setStatValue(PlayerEntity player, int index, int value)
	{
		this.statValues[index] = value;
		STATS.get(index).onChange(player, value);
	}
	
	public void loadStats(PlayerEntity player, CompoundNBT nbt)
	{
		for (int i = 0; i < STATS.size(); i++)
		{
			int value = Math.max(STANDARD_LEVEL, Math.min(nbt.getInt(STATS.get(i).toString()), 99));
			this.initStatValue(player, i, value);
		}
	}
	
	public void initStatValue(PlayerEntity player, int index, int value)
	{
		this.statValues[index] = value;
		STATS.get(index).init(player, value);
	}
	
	public void saveStats(CompoundNBT nbt)
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
