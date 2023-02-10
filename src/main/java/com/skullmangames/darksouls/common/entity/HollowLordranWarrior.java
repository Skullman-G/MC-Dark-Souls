package com.skullmangames.darksouls.common.entity;

import java.util.Random;

import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.core.init.ModSoundEvents;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IServerWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class HollowLordranWarrior extends ArmoredMob
{
	public HollowLordranWarrior(EntityType<? extends HollowLordranWarrior> entitytype, World level)
	{
		super(entitytype, level);
	}
	
	public static AttributeModifierMap.MutableAttribute createAttributes()
	{
		return MobEntity.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 20.0D)
				.add(Attributes.ATTACK_DAMAGE, 1.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.24D);
	}
	
	public static boolean checkSpawnRules(EntityType<Hollow> entitytype, IServerWorld level, SpawnReason spawntype, BlockPos pos, Random random)
	{
		return level.getDifficulty() != Difficulty.PEACEFUL && checkMobSpawnRules(entitytype, level, spawntype, pos, random);
	}
	
	@Override
	protected void registerGoals()
	{
		this.goalSelector.addGoal(4, new RandomWalkingGoal(this, 0.8D));
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Hollow.class, true));
	}
	
	@Override
	protected int getExperienceReward(PlayerEntity player)
	{
		return 60;
	}
	
	protected Item getEquipmentForSlot(int percentage, EquipmentSlotType slot)
	{
		if (percentage <= 75)
		{
			if (slot == EquipmentSlotType.MAINHAND) return Items.IRON_SWORD;
			else if (slot == EquipmentSlotType.OFFHAND) return ModItems.CRACKED_ROUND_SHIELD.get();
		}
		else
		{
			if (slot == EquipmentSlotType.MAINHAND) return ModItems.BATTLE_AXE.get();
		}
		
		switch (slot)
		{
			default: return null;
			case HEAD: return ModItems.LORDRAN_WARRIOR_HELM.get();
			case CHEST: return ModItems.LORDRAN_WARRIOR_ARMOR.get();
			case LEGS: return ModItems.LORDRAN_WARRIOR_WAISTCLOTH.get();
			case FEET: return ModItems.LORDRAN_WARRIOR_BOOTS.get();
		}
	}
	
	@Override
	protected SoundEvent getAmbientSound()
	{
		return ModSoundEvents.HOLLOW_AMBIENT.get();
	}
	
	@Override
	protected SoundEvent getDeathSound()
	{
		return ModSoundEvents.HOLLOW_DEATH.get();
	}
	
	@Override
	protected float getSoundVolume()
	{
		return 0.5F;
	}
	
	@Override
	public int getAmbientSoundInterval()
	{
		return 1000;
	}
}
