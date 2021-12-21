package com.skullmangames.darksouls.common.entity;

import java.util.Random;

import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.core.init.ModSoundEvents;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class HollowEntity extends CreatureEntity implements IRangedAttackMob
{
	public HollowEntity(EntityType<? extends CreatureEntity> p_i48576_1_, World p_i48576_2_)
	{
		super(p_i48576_1_, p_i48576_2_);
	}
	
	public static AttributeModifierMap.MutableAttribute createAttributes()
	{
		return MobEntity.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 10.0D)
				.add(Attributes.ATTACK_DAMAGE, 1.0D)
				.add(Attributes.ATTACK_KNOCKBACK, 1.0D)
				.add(Attributes.ATTACK_SPEED, 1.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.2D);
	}
	
	public static boolean checkSpawnRules(EntityType<? extends CreatureEntity> p_223324_0_, IWorld p_223324_1_, SpawnReason p_223324_2_, BlockPos p_223324_3_, Random p_223324_4_)
	{
		return p_223324_1_.getDifficulty() != Difficulty.PEACEFUL && checkMobSpawnRules(p_223324_0_, p_223324_1_, p_223324_2_, p_223324_3_, p_223324_4_);
	}
	
	@Override
	protected void registerGoals()
	{
	    this.targetSelector.addGoal(0, new AvoidEntityGoal<>(this, AsylumDemonEntity.class, 10.0F, 1.6D, 1.4D));
	    this.targetSelector.addGoal(0, new AvoidEntityGoal<>(this, WitherEntity.class, 10.0F, 1.6D, 1.4D));
	    this.targetSelector.addGoal(0, new AvoidEntityGoal<>(this, EnderDragonEntity.class, 10.0F, 1.6D, 1.4D));
	    this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
	    this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillagerEntity.class, true));
	}
	
	@Override
	protected void populateDefaultEquipmentSlots(DifficultyInstance p_180481_1_)
	{
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
		
		this.setItemSlot(EquipmentSlotType.MAINHAND, item);
		this.setDropChance(EquipmentSlotType.MAINHAND, 0.04F);
	}
	
	@Override
	public ILivingEntityData finalizeSpawn(IServerWorld p_213386_1_, DifficultyInstance difficulty, SpawnReason p_213386_3_, ILivingEntityData data, CompoundNBT nbt)
	{
		data = super.finalizeSpawn(p_213386_1_, difficulty, p_213386_3_, data, nbt);
		this.populateDefaultEquipmentSlots(difficulty);
		
		return data;
	}
	
	@Override
	protected SoundEvent getAmbientSound()
	{
		return ModSoundEvents.HOLLOW_AMBIENT;
	}
	
	@Override
	protected SoundEvent getDeathSound()
	{
		return ModSoundEvents.HOLLOW_DEATH;
	}
	
	@Override
	public boolean canBeCollidedWith()
	{
		return true;
	}

	@Override
	public void performRangedAttack(LivingEntity p_82196_1_, float p_82196_2_)
	{
		ItemStack itemstack = this.getProjectile(this.getItemInHand(ProjectileHelper.getWeaponHoldingHand(this, Items.BOW)));
	    AbstractArrowEntity abstractarrowentity = this.getArrow(itemstack, p_82196_2_);
	    if (this.getMainHandItem().getItem() instanceof net.minecraft.item.BowItem)
	       abstractarrowentity = ((net.minecraft.item.BowItem)this.getMainHandItem().getItem()).customArrow(abstractarrowentity);
	    double d0 = p_82196_1_.getX() - this.getX();
	    double d1 = p_82196_1_.getY(0.3333333333333333D) - abstractarrowentity.getY();
	    double d2 = p_82196_1_.getZ() - this.getZ();
	    double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
	    abstractarrowentity.shoot(d0, d1 + d3 * (double)0.2F, d2, 1.6F, (float)(14 - this.level.getDifficulty().getId() * 4));
	    this.playSound(net.minecraft.util.SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
	    this.level.addFreshEntity(abstractarrowentity);
	}
	
	protected AbstractArrowEntity getArrow(ItemStack itemstack, float p_213624_2_)
	{
		return ProjectileHelper.getMobArrow(this, itemstack, p_213624_2_);
	}
	
	@Override
	protected int getExperienceReward(PlayerEntity player)
	{
		return 10;
	}
}
