package com.skullmangames.darksouls.common.entity;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
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
}
