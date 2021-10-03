package com.skullmangames.darksouls.common.entity;

import com.skullmangames.darksouls.common.entity.ai.goal.NearestNotKindOfMeTargetGoal;
import com.skullmangames.darksouls.core.init.ItemInit;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

public class AsylumDemonEntity extends CreatureEntity
{
	public AsylumDemonEntity(EntityType<? extends CreatureEntity> p_i48575_1_, World p_i48575_2_)
	{
		super(p_i48575_1_, p_i48575_2_);
	}
	
	@Override
	protected void populateDefaultEquipmentSlots(DifficultyInstance p_180481_1_)
	{
		super.populateDefaultEquipmentSlots(p_180481_1_);
		
		this.setItemSlot(EquipmentSlotType.MAINHAND, new ItemStack(ItemInit.DEMON_GREAT_HAMMER.get()));
		this.setDropChance(EquipmentSlotType.MAINHAND, 0.04F);
	}
	
	@Override
	public ILivingEntityData finalizeSpawn(IServerWorld p_213386_1_, DifficultyInstance difficulty,	SpawnReason p_213386_3_, ILivingEntityData data, CompoundNBT nbt)
	{
		data = super.finalizeSpawn(p_213386_1_, difficulty, p_213386_3_, data, nbt);
		this.populateDefaultEquipmentSlots(difficulty);
		
		return data;
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
	public boolean removeWhenFarAway(double p_213397_1_)
	{
		return false;
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
