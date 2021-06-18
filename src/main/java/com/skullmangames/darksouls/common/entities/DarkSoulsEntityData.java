package com.skullmangames.darksouls.common.entities;

import java.util.Map;

import com.google.common.collect.Maps;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.util.text.TranslationTextComponent;

public class DarkSoulsEntityData
{
	private final static Map<LivingEntity, DarkSoulsEntityData> dataMap = Maps.newHashMap();
	private final LivingEntity entity;
	private int humanity = 0;
	private boolean human = true;
	
	public DarkSoulsEntityData(LivingEntity livingentity)
	{
		this.entity = livingentity;
		dataMap.put(this.entity, this);
	}
	
	public static DarkSoulsEntityData get(LivingEntity livingentity)
	{
		DarkSoulsEntityData instance = dataMap.get(livingentity) != null ? dataMap.get(livingentity) : new DarkSoulsEntityData(livingentity);
		return instance;
	}
	
	public void setHumanity(int value)
	{
		this.humanity = value;
	}
	
	public void raiseHumanity(int raise)
	{
		this.humanity += raise;
	}
	
	public void shrinkHumanity(int shrink)
	{
		this.humanity -= shrink;
	}
	
	public int getHumanity()
	{
		return this.humanity;
	}
	
	public boolean isHuman()
	{
		return this.human;
	}
	
	public void setHuman(boolean value)
	{
		if (this.human != value)
		{
			this.human = value;
			
			if (value && this.entity instanceof ServerPlayerEntity)
			{
				((ServerPlayerEntity)this.entity).connection.send(new STitlePacket(STitlePacket.Type.TITLE, new TranslationTextComponent("gui.darksouls.humanity_restored_message")));
			}
		}
	}
	
	public String getStringHumanity()
	{
		return Integer.toString(this.humanity);
	}
	
	public CompoundNBT save(CompoundNBT compoundnbt)
	{
		compoundnbt.putInt("Humanity", this.getHumanity());
		compoundnbt.putBoolean("IsHuman", this.isHuman());
		return compoundnbt;
	}
	
	public void load (CompoundNBT compoundnbt)
	{
		this.humanity = compoundnbt.getInt("Humanity");
		this.human = compoundnbt.getBoolean("IsHuman");
	}
}
