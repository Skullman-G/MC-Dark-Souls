package com.skullmangames.darksouls.common.entity;

import java.util.Random;
import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.core.init.ModSoundEvents;
import com.skullmangames.darksouls.core.util.math.MathUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.entity.npc.AbstractVillager;

public class Hollow extends ArmoredMob implements RangedAttackMob
{
	protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(Hollow.class, EntityDataSerializers.BYTE);
	
	public Hollow(EntityType<? extends Hollow> entitytype, Level level)
	{
		super(entitytype, level);
	}
	
	public static AttributeSupplier.Builder createAttributes()
	{
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 53D)
				.add(Attributes.ATTACK_DAMAGE, 1.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.25D);
	}
	
	@Override
	protected void defineSynchedData()
	{
		super.defineSynchedData();
		this.entityData.define(DATA_FLAGS_ID, (byte)this.random.nextInt(3));
	}
	
	private void setTextureId(int value)
	{
		value = MathUtils.clamp(value, 0, 2);
		this.entityData.set(DATA_FLAGS_ID, (byte)value);
	}
	
	public int getTextureId()
	{
		return this.entityData.get(DATA_FLAGS_ID).intValue();
	}
	
	@Override
	public void addAdditionalSaveData(CompoundTag nbt)
	{
		super.addAdditionalSaveData(nbt);
		nbt.putInt("TextureId", this.getTextureId());
	}
	
	@Override
	public void readAdditionalSaveData(CompoundTag nbt)
	{
		super.readAdditionalSaveData(nbt);
		this.setTextureId(nbt.getInt("TextureId"));
	}
	
	@Override
	public boolean canSpawnSprintParticle()
	{
		return super.canSpawnSprintParticle() && this.getAttributeValue(Attributes.MOVEMENT_SPEED) >= 0.3F;
	}
	
	public static boolean checkSpawnRules(EntityType<Hollow> entitytype, ServerLevelAccessor level, MobSpawnType spawntype, BlockPos pos, Random random)
	{
		return level.getDifficulty() != Difficulty.PEACEFUL && checkMobSpawnRules(entitytype, level, spawntype, pos, random);
	}
	
	@Override
	protected void registerGoals()
	{
		this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.8D));
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
	    this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
	    this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, HollowLordranWarrior.class, true));
	    this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, HollowLordranSoldier.class, true));
	}
	
	@Override
	protected Item getEquipmentForSlot(int percentage, EquipmentSlot slot)
	{
		if (slot == EquipmentSlot.MAINHAND)
		{
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
		
		if (this.random.nextFloat() < 0.2F)
		{
			switch (slot)
			{
				default:
					return null;
				case CHEST:
					return ModItems.BURNT_SHIRT.get();
				case LEGS:
					return ModItems.BURNT_TROUSERS.get();
			}
		}
		
		return null;
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
		ItemStack itemstack = this.getProjectile(this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, item -> item instanceof BowItem)));
	    AbstractArrow abstractarrowentity = this.getArrow(itemstack, p_82196_2_);
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
	
	protected AbstractArrow getArrow(ItemStack itemstack, float p_213624_2_)
	{
		return ProjectileUtil.getMobArrow(this, itemstack, p_213624_2_);
	}
}
