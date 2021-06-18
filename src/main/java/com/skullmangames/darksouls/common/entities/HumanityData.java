package com.skullmangames.darksouls.common.entities;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;

public class HumanityData
{
	private final static Map<LivingEntity, HumanityData> humanityMap = Maps.newHashMap();
	private final LivingEntity entity;
	private int value = 0;
	
	public HumanityData(LivingEntity livingentity)
	{
		this.entity = livingentity;
		humanityMap.put(this.entity, this);
	}
	
	public static HumanityData get(LivingEntity livingentity)
	{
		HumanityData instance = humanityMap.get(livingentity) != null ? humanityMap.get(livingentity) : new HumanityData(livingentity);
		return instance;
	}
	
	public void setValue(int value)
	{
		this.value = value;
	}
	
	public void raiseValue(int raise)
	{
		System.out.print(this.value);
		this.value += raise;
	}
	
	public void shrinkValue(int shrink)
	{
		this.value -= shrink;
	}
	
	public int getValue()
	{
		return this.value;
	}
	
	public String getStringValue()
	{
		return Integer.toString(this.value);
	}
	
	public CompoundNBT save(CompoundNBT compoundnbt)
	{
		compoundnbt.putInt("Humanity", this.getValue());
		return compoundnbt;
	}
}
