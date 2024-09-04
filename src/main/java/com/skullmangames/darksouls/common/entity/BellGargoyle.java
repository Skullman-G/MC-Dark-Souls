package com.skullmangames.darksouls.common.entity;

import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.core.init.ModSoundEvents;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class BellGargoyle extends AbstractBoss
{
	public BellGargoyle(EntityType<? extends PathfinderMob> type, Level level)
	{
		super(type, level);
	}
	
	@Override
	public boolean canSpawnSprintParticle()
	{
		return false;
	}
	
	@Override
	protected int calculateFallDamage(float distance, float p_225508_2_)
	{
		if (distance <= 30.0F) return 0;
		return super.calculateFallDamage(distance, p_225508_2_);
	}
	
	public static AttributeSupplier.Builder createAttributes()
	{
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 825D)
				.add(Attributes.ATTACK_DAMAGE, 1.0D)
				.add(Attributes.ATTACK_KNOCKBACK, 1.0D)
				.add(Attributes.ATTACK_SPEED, 1.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.2D);
	}
	
	@Override
	public boolean removeWhenFarAway(double distance)
	{
		return false;
	}
	
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, SpawnGroupData data, CompoundTag nbt)
	{
		data = super.finalizeSpawn(level, difficulty, reason, data, nbt);
		this.populateDefaultEquipmentSlots(difficulty);
		return data;
	}
	
	@Override
	protected void populateDefaultEquipmentSlots(DifficultyInstance difficulty)
	{
		super.populateDefaultEquipmentSlots(difficulty);
		
		this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.GARGOYLE_HALBERD.get()));
		this.setDropChance(EquipmentSlot.MAINHAND, 0.00F);
		this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(ModItems.GARGOYLE_SHIELD.get()));
		this.setDropChance(EquipmentSlot.OFFHAND, 0.00F);
	}
	
	@Override
	protected void registerGoals()
	{
		this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.8D));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
	    this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
	    this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Hollow.class, true));
	}
	
	@Override
	protected SoundEvent getDeathSound()
	{
		return ModSoundEvents.BELL_GARGOYLE_DEATH.get();
	}
	
	@Override
	protected SoundEvent getHurtSound(DamageSource source)
	{
		return ModSoundEvents.BELL_GARGOYLE_DAMAGE.get();
	}
}
