package com.skullmangames.darksouls.common.entity;

import java.util.Random;
import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.core.init.ModSoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IServerWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class Hollow extends ArmoredMob implements IRangedAttackMob
{
	public Hollow(EntityType<? extends Hollow> entitytype, World level)
	{
		super(entitytype, level);
	}
	
	public static AttributeModifierMap.MutableAttribute createAttributes()
	{
		return MobEntity.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 20.0D)
				.add(Attributes.ATTACK_DAMAGE, 1.0D)
				.add(Attributes.ATTACK_KNOCKBACK, 1.0D)
				.add(Attributes.ATTACK_SPEED, 1.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.25D);
	}
	
	@Override
	public boolean canSpawnSprintParticle()
	{
		return super.canSpawnSprintParticle() && this.getAttributeValue(Attributes.MOVEMENT_SPEED) >= 0.3F;
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
	    this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillagerEntity.class, true));
	    this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, HollowLordranWarrior.class, true));
	    this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, HollowLordranSoldier.class, true));
	}
	
	@Override
	protected Item getEquipmentForSlot(int percentage, EquipmentSlotType slot)
	{
		if (slot != EquipmentSlotType.MAINHAND) return null;
		if (percentage <= 75)
		{
			return ModItems.BROKEN_STRAIGHT_SWORD.get();
		}
		else if (percentage <= 90)
		{
			return Items.BOW;
		}
		else
		{
			return ModItems.STRAIGHT_SWORD_HILT.get();
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

	@Override
	public void performRangedAttack(LivingEntity target, float p_82196_2_)
	{
		ItemStack itemstack = this.getProjectile(this.getItemInHand(ProjectileHelper.getWeaponHoldingHand(this, item -> item instanceof BowItem)));
	    AbstractArrowEntity abstractarrowentity = this.getArrow(itemstack, p_82196_2_);
	    if (this.getMainHandItem().getItem() instanceof BowItem)
	       abstractarrowentity = ((BowItem)this.getMainHandItem().getItem()).customArrow(abstractarrowentity);
	    double d0 = target.getX() - this.getX();
	    double d1 = target.getY(0.3333333333333333D) - abstractarrowentity.getY();
	    double d2 = target.getZ() - this.getZ();
	    double d3 = (double)Math.sqrt(d0 * d0 + d2 * d2);
	    abstractarrowentity.shoot(d0, d1 + d3 * (double)0.2F, d2, 1.6F, (float)(14 - this.level.getDifficulty().getId() * 4));
	    this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
	    this.level.addFreshEntity(abstractarrowentity);
	}
	
	protected AbstractArrowEntity getArrow(ItemStack itemstack, float p_213624_2_)
	{
		return ProjectileHelper.getMobArrow(this, itemstack, p_213624_2_);
	}
	
	@Override
	protected int getExperienceReward(PlayerEntity player)
	{
		return 20;
	}
}
