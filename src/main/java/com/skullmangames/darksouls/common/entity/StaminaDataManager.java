package com.skullmangames.darksouls.common.entity;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.skullmangames.darksouls.common.entity.nbt.MobNBTManager;
import com.skullmangames.darksouls.core.init.EntityTypeInit;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;

public class StaminaDataManager
{
	private static final Map<EntityType<? extends LivingEntity>, Integer> baseStamina = (ImmutableMap.<EntityType<? extends LivingEntity>, Integer>builder()
			.put(EntityType.PLAYER, 89)
			.put(EntityTypeInit.HOLLOW.get(), 89).build());
	
	public static void initMaxStamina(LivingEntity livingentity)
	{
		if (livingentity.getPersistentData().getInt("MaxStamina") == 0)
		{
			setMaxStamina(livingentity, baseStamina.get(livingentity.getType()) != null ? baseStamina.get(livingentity.getType()).intValue() : 10);
		}
		else
		{
			setMaxStamina(livingentity, livingentity.getPersistentData().getInt("MaxStamina"));
		}
	}
	
	private static void setMaxStamina(LivingEntity livingentity, int value)
	{
		livingentity.getPersistentData().putInt("MaxStamina", value);
	}
	
	public static int getMaxStamina(LivingEntity livingentity)
	{
		if (livingentity instanceof ClientPlayerEntity) livingentity = MobNBTManager.getServerPlayer((ClientPlayerEntity)livingentity);
		return livingentity.getPersistentData().getInt("MaxStamina");
	}
	
	public static int getStamina(LivingEntity livingentity)
	{
		if (livingentity instanceof ClientPlayerEntity) livingentity = MobNBTManager.getServerPlayer((ClientPlayerEntity)livingentity);
		return livingentity.getPersistentData().getInt("Stamina");
	}
	
	public static void setStamina(LivingEntity livingentity, int value)
	{
		if (value != getStamina(livingentity) && value <= getMaxStamina(livingentity) && value >= 0)
		{
			livingentity.getPersistentData().putInt("Stamina", value);
			if (value == 0) resetExhaustion(livingentity);
		}
	}
	
	public static void shrinkStamina(LivingEntity livingentity, int shrink)
	{
		setStamina(livingentity, getStamina(livingentity) - shrink);
	}
	
	public static void raiseStamina(LivingEntity livingentity, int raise)
	{
		setStamina(livingentity, getStamina(livingentity) + raise);
	}
	
	public static int getExhaustion(LivingEntity livingentity)
	{
		if (livingentity instanceof ClientPlayerEntity) livingentity = MobNBTManager.getServerPlayer((ClientPlayerEntity)livingentity);
		return livingentity.getPersistentData().getInt("Exhaustion");
	}
	
	public static void resetExhaustion(LivingEntity livingentity)
	{
		setExhaustion(livingentity, 0);
	}
	
	public static void recover(LivingEntity livingentity)
	{
		setExhaustion(livingentity, getExhaustion(livingentity) + 1);
	}
	
	private static void setExhaustion(LivingEntity livingentity, int value)
	{
		if (value != getExhaustion(livingentity))
		{
			livingentity.getPersistentData().putInt("Exhaustion", value);
		}
	}
}
