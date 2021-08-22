package com.skullmangames.darksouls.common.entity.ai.goal;

import com.skullmangames.darksouls.common.entity.HollowEntity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

public class NearestNotKindOfMeTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T>
{
	public NearestNotKindOfMeTargetGoal(MobEntity mob, Class<T> target, boolean donttrackBehindWalls)
	{
		super(mob, target, donttrackBehindWalls);
	}
	
	@Override
	protected void findTarget()
	{
		if (this.targetType != PlayerEntity.class && this.targetType != ServerPlayerEntity.class)
		{
			LivingEntity livingentity = this.mob.level.getNearestLoadedEntity(this.targetType, this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ(), this.getTargetSearchArea(this.getFollowDistance()));
			
			if (!(livingentity instanceof HollowEntity) && !(livingentity instanceof MonsterEntity))
			{
				this.target = livingentity;
			}
	    }
		else
		{
	        this.target = this.mob.level.getNearestPlayer(this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
	    }
	}
}
