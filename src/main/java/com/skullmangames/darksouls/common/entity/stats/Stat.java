package com.skullmangames.darksouls.common.entity.stats;

import java.util.UUID;

import com.skullmangames.darksouls.DarkSouls;
import net.minecraft.entity.LivingEntity;

public class Stat
{
	private final String name;
	private final UUID MODIFIER_UUID;
	
	public Stat(String name, String uuid)
	{
		this.name = "stat."+DarkSouls.MOD_ID+"."+name;
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
	
	@Override
	public String toString()
	{
		return this.name;
	}
	
	public void onChange(LivingEntity livingentity, boolean isinit, int value) {}
	
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
		if (value <= 0 || value > 99 || livingentity.getPersistentData().getInt(this.name) == value) return;
		livingentity.getPersistentData().putInt(this.name, value);
		this.onChange(livingentity, false, value);
	}
}
