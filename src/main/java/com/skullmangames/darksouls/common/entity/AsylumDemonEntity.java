package com.skullmangames.darksouls.common.entity;

import com.skullmangames.darksouls.common.entity.ai.goal.NearestNotKindOfMeTargetGoal;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class AsylumDemonEntity extends CreatureEntity
{
	public AsylumDemonEntity(EntityType<? extends CreatureEntity> p_i48575_1_, World p_i48575_2_)
	{
		super(p_i48575_1_, p_i48575_2_);
	}
	
	@Override
	public boolean canBeCollidedWith()
	{
		return true;
	}
	
	public static AttributeModifierMap.MutableAttribute createAttributes()
	{
		return MobEntity.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 41.25D)
				.add(Attributes.ATTACK_DAMAGE, 1.0D)
				.add(Attributes.ATTACK_KNOCKBACK, 1.0D)
				.add(Attributes.ATTACK_SPEED, 1.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.2D);
	}
	
	@Override
	protected void registerGoals()
	{
		this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8.0F));
	    this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
	    this.goalSelector.addGoal(7, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
	    
	    this.targetSelector.addGoal(2, new NearestNotKindOfMeTargetGoal<>(this, LivingEntity.class, true));
	}
}
