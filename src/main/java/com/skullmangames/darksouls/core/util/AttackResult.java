package com.skullmangames.darksouls.core.util;

import java.util.ArrayList;
import java.util.List;

import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.entity.BreakableObject;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;

public class AttackResult
{
	private final LivingCap<?> attackerCap;
	private final Entity attacker;
	private final List<TargetInfo> targetInfoList;
	
	public AttackResult(LivingCap<?> attackerCap)
	{
		this.attackerCap = attackerCap;
		this.attacker = attackerCap.getOriginalEntity();
		this.targetInfoList = new ArrayList<>();
	}
	
	public List<TargetInfo> getTargetInfos()
	{
		return this.targetInfoList;
	}
	
	public boolean isEmpty()
	{
		return this.targetInfoList.isEmpty();
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
		
		for(; index < this.targetInfoList.size(); index++)
		{
			if(distance < this.targetInfoList.get(index).getDistanceToAttacker()) break;
			
		}
		
		TargetInfo targetInfo = new TargetInfo(entity, distance, blocked);
		if (targetInfo.isValidAttackTarget()) this.targetInfoList.add(index, targetInfo);
	}
	
	public class TargetInfo
	{
		private final Entity entity;
		private final double distanceToAttacker;
		private final boolean blocked;
		
		private TargetInfo(Entity entity, double distanceToAttacker, boolean blocked)
		{
			this.entity = entity;
			this.distanceToAttacker = distanceToAttacker;
			this.blocked = blocked;
		}
		
		public Entity getEntity()
		{
			return this.entity;
		}
		
		private double getDistanceToAttacker()
		{
			return this.distanceToAttacker;
		}
		
		public boolean wasBlocked()
		{
			return this.blocked;
		}
		
		public Entity getTrueEntity()
		{
			return this.entity instanceof PartEntity ? ((PartEntity<?>) this.entity).getParent() : this.entity;
		}
		
		private boolean isValidAttackTarget()
		{
			Entity trueEntity = this.getTrueEntity();
			return (trueEntity instanceof LivingEntity || trueEntity instanceof BreakableObject)
					&& !AttackResult.this.attackerCap.currentlyAttackedEntities.contains(trueEntity)
					&& !AttackResult.this.attackerCap.isTeam(trueEntity)
					&& this.hasNoBlockBetween();
		}
		
		private boolean hasNoBlockBetween()
		{
			return AttackResult.this.attacker.level.clip(new ClipContext
					(
						new Vec3(this.entity.getX(), this.entity.getY() + this.entity.getEyeHeight(), this.entity.getZ()),
						new Vec3(AttackResult.this.attacker.getX(), AttackResult.this.attacker.getY() + AttackResult.this.attacker.getBbHeight() * 0.5F, AttackResult.this.attacker.getZ()),
						ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, AttackResult.this.attacker
					))
					.getType() == HitResult.Type.MISS;
		}
	}
}
