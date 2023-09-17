package com.skullmangames.darksouls.core.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.entity.Entity;

public class AttackResult
{
	private Entity attacker;
	private List<Double> distanceToAttacker;
	private List<Entity> hitEntites;
	private List<Boolean> blocked;
	private int index;
	
	public AttackResult(Entity attacker)
	{
		this.attacker = attacker;
		this.distanceToAttacker = new ArrayList<>();
		this.hitEntites = new ArrayList<>();
		this.blocked = new ArrayList<>();
		this.index = 0;
	}
	
	public void addEntities(List<Entity> entities, boolean blocked)
	{
		for(Entity entity : entities)
		{
			this.addEntity(entity, blocked);
		}
	}
	
	private void addEntity(Entity entity, boolean blocked)
	{
		double distance = this.attacker.distanceToSqr(entity);
		int index = 0;
		
		for(; index < this.hitEntites.size(); index++)
		{
			if(distance < this.distanceToAttacker.get(index)) break;
			
		}
		
		this.hitEntites.add(index, entity);
		this.distanceToAttacker.add(index, distance);
		this.blocked.add(index, blocked);
	}
	
	public Entity getEntity()
	{
		return this.hitEntites.get(this.index);
	}
	
	public boolean wasBlocked()
	{
		return this.blocked.get(this.index);
	}
	
	public boolean next()
	{
		this.index++;
		return hitEntites.size() > this.index;
	}
}
