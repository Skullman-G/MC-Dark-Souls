package com.skullmangames.darksouls.common.entity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public class QuestEntity extends CreatureEntity
{
	private String questPath = this.getQuestPaths().get(0);
	
	protected QuestEntity(EntityType<? extends CreatureEntity> p_i48575_1_, World p_i48575_2_)
	{
		super(p_i48575_1_, p_i48575_2_);
	}
	
	public List<String> getQuestPaths()
	{
		return new ArrayList<String>();
	}
	
	public String getCurrentQuestPath()
	{
		return this.questPath;
	}
	
	public void setCurrentQuestPath(String value)
	{
		if (this.getQuestPaths().contains(value))
		{
			this.questPath = value;
		}
	}
}
