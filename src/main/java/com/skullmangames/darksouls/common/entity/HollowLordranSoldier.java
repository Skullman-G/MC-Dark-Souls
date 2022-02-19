package com.skullmangames.darksouls.common.entity;

import java.util.Random;

import com.skullmangames.darksouls.core.init.ModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class HollowLordranSoldier extends ArmoredMob
{
	public HollowLordranSoldier(EntityType<? extends HollowLordranSoldier> entitytype, Level level)
	{
		super(entitytype, level);
	}
	
	public static AttributeSupplier.Builder createAttributes()
	{
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 10.5D)
				.add(Attributes.ATTACK_DAMAGE, 1.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.2D);
	}
	
	public static boolean checkSpawnRules(EntityType<Hollow> entitytype, ServerLevelAccessor level, MobSpawnType spawntype, BlockPos pos, Random random)
	{
		return level.getDifficulty() != Difficulty.PEACEFUL && checkMobSpawnRules(entitytype, level, spawntype, pos, random);
	}
	
	@Override
	protected void registerGoals()
	{
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}
	
	@Override
	protected int getExperienceReward(Player player)
	{
		return 60;
	}
	
	protected Item getEquipmentForSlot(int percentage, EquipmentSlot slot)
	{
		if (percentage <= 75)
		{
			if (slot == EquipmentSlot.MAINHAND) return ModItems.LONGSWORD.get();
			else if (slot == EquipmentSlot.OFFHAND) return ModItems.LORDRAN_SOLDIER_SHIELD.get();
		}
		else if (percentage <= 90)
		{
			if (slot == EquipmentSlot.MAINHAND) return ModItems.SPEAR.get();
			else if (slot == EquipmentSlot.OFFHAND) return ModItems.LORDRAN_SOLDIER_SHIELD.get();
		}
		else
		{
			if (slot == EquipmentSlot.MAINHAND) return Items.CROSSBOW;
		}
		
		switch (slot)
		{
			default: return null;
			case HEAD: return ModItems.LORDRAN_SOLDIER_HELM.get();
			case CHEST: return ModItems.LORDRAN_SOLDIER_ARMOR.get();
			case LEGS: return ModItems.LORDRAN_SOLDIER_WAISTCLOTH.get();
			case FEET: return ModItems.LORDRAN_SOLDIER_BOOTS.get();
		}
	}
}
