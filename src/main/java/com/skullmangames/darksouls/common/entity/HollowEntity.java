package com.skullmangames.darksouls.common.entity;

import java.util.Random;

import com.skullmangames.darksouls.common.entity.ai.goal.SearchTargetsAsHollowGoal;
import com.skullmangames.darksouls.core.init.ItemInit;
import com.skullmangames.darksouls.core.init.SoundEventInit;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class HollowEntity extends MonsterEntity
{
	public HollowEntity(EntityType<? extends MonsterEntity> p_i48576_1_, World p_i48576_2_)
	{
		super(p_i48576_1_, p_i48576_2_);
	}
	
	public static AttributeModifierMap.MutableAttribute createAttributes()
	{
		return MobEntity.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 3.45D)
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
	    this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, false));
	    
	    this.targetSelector.addGoal(2, new SearchTargetsAsHollowGoal<>(this, LivingEntity.class, true));
	}
	
	@Override
	protected void populateDefaultEquipmentSlots(DifficultyInstance p_180481_1_)
	{
		super.populateDefaultEquipmentSlots(p_180481_1_);
		
		Random random = this.level.random;
		ItemStack item;
		if (random.nextBoolean())
		{
			item = new ItemStack(ItemInit.BROKEN_STRAIGHT_SWORD.get());
		}
		else
		{
			item = new ItemStack(ItemInit.STRAIGHT_SWORD_HILT.get());
		}
		
		if (random.nextInt(50) == 1)
		{
			this.setItemSlotAndDropWhenKilled(EquipmentSlotType.MAINHAND, item);
		}
		else
		{
			this.setItemSlot(EquipmentSlotType.MAINHAND, item);
		}
	}
	
	@Override
	public ILivingEntityData finalizeSpawn(IServerWorld p_213386_1_, DifficultyInstance instance, SpawnReason p_213386_3_, ILivingEntityData data, CompoundNBT nbt)
	{
		data = super.finalizeSpawn(p_213386_1_, instance, p_213386_3_, data, nbt);
		this.populateDefaultEquipmentSlots(instance);
		
		return data;
	}
	
	public static boolean canSpawnOn(EntityType<?> type, IWorld level, SpawnReason reason, BlockPos blockpos, Random random)
	{
		return level.getDifficulty() != Difficulty.PEACEFUL;
	}
	
	@Override
	protected SoundEvent getAmbientSound()
	{
		return SoundEventInit.HOLLOW_AMBIENT.get();
	}
	
	@Override
	protected SoundEvent getDeathSound()
	{
		return SoundEventInit.HOLLOW_DEATH.get();
	}
}
