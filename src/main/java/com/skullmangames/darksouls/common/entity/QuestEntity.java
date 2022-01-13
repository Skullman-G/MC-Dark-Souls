package com.skullmangames.darksouls.common.entity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public class QuestEntity extends PathfinderMob
{
	private String questPath = this.getQuestPaths().get(0);
	
	protected QuestEntity(EntityType<? extends PathfinderMob> p_i48575_1_, Level p_i48575_2_)
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
