package com.skullmangames.darksouls.common.entities.stats;

import java.util.UUID;

import net.minecraft.entity.LivingEntity;

public class Stat
{
	private final String name;
	private final UUID MODIFIER_UUID;
	
	public Stat(String name, String uuid)
	{
		this.name = name;
		this.MODIFIER_UUID = UUID.fromString(uuid);
	}
	
	public UUID getModifierUUID()
	{
		return this.MODIFIER_UUID;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public void onChange(LivingEntity livingentity, boolean isinit, int value)
	{
		return;
	}
	
	public int getValue(LivingEntity livingentity)
	{
		return livingentity.getPersistentData().getInt(this.name) <= 0 ? 1 : livingentity.getPersistentData().getInt(this.name);
	}
	
	public void init(LivingEntity livingentity)
	{
		this.onChange(livingentity, true, this.getValue(livingentity));
	}
	
	public void setValue(LivingEntity livingentity, int value)
	{
		if (value > 0 && livingentity.getPersistentData().getInt(this.name) != value)
		{
			livingentity.getPersistentData().putInt(this.name, value);
			this.onChange(livingentity, false, value);
		}
	}
}
