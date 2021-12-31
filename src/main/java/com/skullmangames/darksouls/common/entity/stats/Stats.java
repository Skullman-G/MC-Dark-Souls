package com.skullmangames.darksouls.common.entity.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCStat;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;

public class Stats
{
	public static List<Stat> STATS = new ArrayList<>();
	
	public static final Stat VIGOR = register(new ModifyingStat("vigor", "35031b47-45fa-401b-92dc-12b6d258e553")
			{
				@Override		
				public void onChange(PlayerEntity player, int value)
				{
					player.setHealth(player.getMaxHealth());
				}
				
				@Override
				public void modifyAttributes(PlayerEntity player, int value)
				{
					ModifiableAttributeInstance instance = player.getAttribute(Attributes.MAX_HEALTH);
					if (instance.getModifier(this.getModifierUUID()) != null) instance.removeModifier(this.getModifierUUID());
					AttributeModifier modifier = new AttributeModifier(this.getModifierUUID(), this.toString(), value - 1, Operation.ADDITION);
					instance.addPermanentModifier(modifier);
				}
			});
	public static final Stat ENDURANCE = register(new ModifyingStat("endurance", "8bbd5d2d-0188-41be-a673-cfca6cd8da8c")
			{
				@Override		
				public void modifyAttributes(PlayerEntity player, int value)
				{
					ModifiableAttributeInstance instance = player.getAttribute(ModAttributes.MAX_STAMINA.get());
					if (instance.getModifier(this.getModifierUUID()) != null) instance.removeModifier(this.getModifierUUID());
					AttributeModifier modifier = new AttributeModifier(this.getModifierUUID(), this.toString(), value - 1, Operation.ADDITION);
					instance.addPermanentModifier(modifier);
				};
			});
	public static final Stat STRENGTH = register(new Stat("strength"));
	
	private static Stat register(Stat stat)
	{
		STATS.add(stat);
		return stat;
	}
	
	private final Map<String, Integer> statValues = new HashMap<>();
	
	public int getStatValue(Stat stat)
	{
		return statValues.getOrDefault(stat.toString(), 1);
	}
	
	public void setStatValue(PlayerEntity player, Stat stat, int value)
	{
		this.statValues.put(stat.toString(), value);
		stat.onChange(player, value);
	}
	
	public void setStatValue(PlayerEntity player, String statname, int value)
	{
		this.statValues.put(statname, value);
		STATS.forEach((stat) -> { if (stat.toString() == statname) stat.onChange(player, value); });
	}
	
	public void loadStats(ServerPlayerEntity player, CompoundNBT nbt)
	{
		for (Stat stat : STATS)
		{
			int value = nbt.getInt(stat.toString());
			if (value <= 0) value = 1;
			this.initStatValue(player, stat.toString(), value);
			ModNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(new STCStat(player.getId(), stat.toString(), value), player);
		}
	}
	
	public void initStatValue(PlayerEntity player, String statname, int value)
	{
		this.statValues.put(statname, value);
		STATS.forEach((stat) -> { if (stat.toString() == statname) stat.init(player, value); });
	}
	
	public void saveStats(CompoundNBT nbt)
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
			level += this.getStatValue(stat) - 1;
		}
		
		return level;
	}
}
