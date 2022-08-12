package com.skullmangames.darksouls.common.entity;

import java.util.Random;
import java.util.function.Predicate;

import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.core.init.ModSoundEvents;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IServerWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ICrossbowUser;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShootableItem;
import net.minecraft.world.World;

public class HollowLordranSoldier extends ArmoredMob implements ICrossbowUser
{
	public HollowLordranSoldier(EntityType<? extends HollowLordranSoldier> entitytype, World level)
	{
		super(entitytype, level);
	}

	public static AttributeModifierMap.MutableAttribute createAttributes()
	{
		return MobEntity.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 17.5D)
				.add(Attributes.ATTACK_DAMAGE, 1.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.24D);
	}

	public static boolean checkSpawnRules(EntityType<Hollow> entitytype, IServerWorld level,
			SpawnReason spawntype, BlockPos pos, Random random)
	{
		return level.getDifficulty() != Difficulty.PEACEFUL
				&& checkMobSpawnRules(entitytype, level, spawntype, pos, random);
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
			if (slot == EquipmentSlotType.MAINHAND)
				return ModItems.LONGSWORD.get();
			else if (slot == EquipmentSlotType.OFFHAND)
				return ModItems.LORDRAN_SOLDIER_SHIELD.get();
		} else if (percentage <= 90)
		{
			if (slot == EquipmentSlotType.MAINHAND)
				return ModItems.SPEAR.get();
			else if (slot == EquipmentSlotType.OFFHAND)
				return ModItems.LORDRAN_SOLDIER_SHIELD.get();
		} else
		{
			if (slot == EquipmentSlotType.MAINHAND)
				return Items.CROSSBOW;
		}

		switch (slot)
		{
		default:
			return null;
		case HEAD:
			return ModItems.LORDRAN_SOLDIER_HELM.get();
		case CHEST:
			return ModItems.LORDRAN_SOLDIER_ARMOR.get();
		case LEGS:
			return ModItems.LORDRAN_SOLDIER_WAISTCLOTH.get();
		case FEET:
			return ModItems.LORDRAN_SOLDIER_BOOTS.get();
		}
	}

	@Override
	public void performRangedAttack(LivingEntity p_33317_, float p_33318_)
	{
		this.performCrossbowAttack(this, 1.6F);
	}

	@Override
	public void setChargingCrossbow(boolean value)
	{
	}

	@Override
	public void shootCrossbowProjectile(LivingEntity target, ItemStack crossbow, ProjectileEntity ammo, float f)
	{
		this.shootCrossbowProjectile(this, target, ammo, f, 1.6F);
	}

	@Override
	public void onCrossbowAttackPerformed()
	{
		this.noActionTime = 0;
	}

	@Override
	public ItemStack getProjectile(ItemStack crossbow)
	{
		if (crossbow.getItem() instanceof ShootableItem)
		{
			Predicate<ItemStack> predicate = ((ShootableItem) crossbow.getItem()).getSupportedHeldProjectiles();
			ItemStack itemstack = ShootableItem.getHeldProjectile(this, predicate);
			return itemstack.isEmpty() ? new ItemStack(Items.ARROW) : itemstack;
		} else
		{
			return ItemStack.EMPTY;
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
}
