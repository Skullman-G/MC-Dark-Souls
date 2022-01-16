package com.skullmangames.darksouls.common.entity;

import java.util.Random;

import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.core.init.ModSoundEvents;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.entity.npc.AbstractVillager;

public class HollowEntity extends PathfinderMob implements RangedAttackMob
{
	public HollowEntity(EntityType<? extends PathfinderMob> p_i48576_1_, Level p_i48576_2_)
	{
		super(p_i48576_1_, p_i48576_2_);
	}
	
	public static AttributeSupplier.Builder createAttributes()
	{
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 10.0D)
				.add(Attributes.ATTACK_DAMAGE, 1.0D)
				.add(Attributes.ATTACK_KNOCKBACK, 1.0D)
				.add(Attributes.ATTACK_SPEED, 1.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.2D);
	}
	
	public static boolean checkSpawnRules(EntityType<HollowEntity> entitytype, ServerLevelAccessor level, MobSpawnType spawntype, BlockPos pos, Random random)
	{
		return level.getDifficulty() != Difficulty.PEACEFUL && checkMobSpawnRules(entitytype, level, spawntype, pos, random);
	}
	
	@Override
	protected void registerGoals()
	{
	    this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
	    this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
	}
	
	@Override
	protected void populateDefaultEquipmentSlots(DifficultyInstance p_180481_1_)
	{
		if (this.getMainHandItem().getItem() != Items.AIR) return;
		super.populateDefaultEquipmentSlots(p_180481_1_);
		
		Random random = this.level.random;
		ItemStack item;
		int weaponid = random.nextInt(4);
		
		if (weaponid <= 1)
		{
			item = new ItemStack(ModItems.BROKEN_STRAIGHT_SWORD.get());
		}
		else if (weaponid <= 2)
		{
			item = new ItemStack(ModItems.STRAIGHT_SWORD_HILT.get());
		}
		else
		{
			item = new ItemStack(Items.BOW);
		}
		
		this.setItemSlot(EquipmentSlot.MAINHAND, item);
		this.setDropChance(EquipmentSlot.MAINHAND, 0.04F);
	}
	
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_213386_1_, DifficultyInstance difficulty, MobSpawnType p_213386_3_, SpawnGroupData data, CompoundTag nbt)
	{
		data = super.finalizeSpawn(p_213386_1_, difficulty, p_213386_3_, data, nbt);
		this.populateDefaultEquipmentSlots(difficulty);
		
		return data;
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
	public boolean canBeCollidedWith()
	{
		return true;
	}

	@Override
	public void performRangedAttack(LivingEntity p_82196_1_, float p_82196_2_)
	{
		ItemStack itemstack = this.getProjectile(this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, item -> item instanceof BowItem)));
	    AbstractArrow abstractarrowentity = this.getArrow(itemstack, p_82196_2_);
	    if (this.getMainHandItem().getItem() instanceof BowItem)
	       abstractarrowentity = ((BowItem)this.getMainHandItem().getItem()).customArrow(abstractarrowentity);
	    double d0 = p_82196_1_.getX() - this.getX();
	    double d1 = p_82196_1_.getY(0.3333333333333333D) - abstractarrowentity.getY();
	    double d2 = p_82196_1_.getZ() - this.getZ();
	    double d3 = (double)Math.sqrt(d0 * d0 + d2 * d2);
	    abstractarrowentity.shoot(d0, d1 + d3 * (double)0.2F, d2, 1.6F, (float)(14 - this.level.getDifficulty().getId() * 4));
	    this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
	    this.level.addFreshEntity(abstractarrowentity);
	}
	
	protected AbstractArrow getArrow(ItemStack itemstack, float p_213624_2_)
	{
		return ProjectileUtil.getMobArrow(this, itemstack, p_213624_2_);
	}
	
	@Override
	protected int getExperienceReward(Player player)
	{
		return 10;
	}
}
