package com.skullmangames.darksouls.common.entity;

import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.core.init.ModSoundEvents;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class BerenikeKnight extends ArmoredMob
{
	public BerenikeKnight(EntityType<? extends BerenikeKnight> entitytype, Level level)
	{
		super(entitytype, level);
	}
	
	public static AttributeSupplier.Builder createAttributes()
	{
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 645D)
				.add(Attributes.ATTACK_DAMAGE, 1.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.24D);
	}
	
	@Override
	protected void registerGoals()
	{
		this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.8D));
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	@Override
	protected Item getEquipmentForSlot(int percentage, EquipmentSlot slot)
	{
		if (percentage <= 40)
		{
			if (slot == EquipmentSlot.MAINHAND) return ModItems.ZWEIHANDER.get();
			if (slot == EquipmentSlot.OFFHAND) return ModItems.BALDER_SHIELD.get();
		}
		else
		{
			if (slot == EquipmentSlot.MAINHAND) return ModItems.MACE.get();
			if (slot == EquipmentSlot.OFFHAND) return ModItems.BUCKLER.get();
		}
		
		switch (slot)
		{
		default:
			return null;
		case HEAD:
			return ModItems.BERENIKE_HELM.get();
		case CHEST:
			return ModItems.BERENIKE_ARMOR.get();
		case LEGS:
			return ModItems.BERENIKE_LEGGINGS.get();
		}
	}
	
	@Override
	public Fallsounds getFallSounds()
	{
		return new Fallsounds(ModSoundEvents.BERENIKE_KNIGHT_FALL_SMALL.get(), ModSoundEvents.BERENIKE_KNIGHT_FALL_BIG.get());
	}
	
	@Override
	protected SoundEvent getAmbientSound()
	{
		return ModSoundEvents.BERENIKE_KNIGHT_AMBIENT.get();
	}
	
	@Override
	protected SoundEvent getHurtSound(DamageSource source)
	{
		return ModSoundEvents.BERENIKE_KNIGHT_DAMAGE.get();
	}
	
	@Override
	protected SoundEvent getDeathSound()
	{
		return ModSoundEvents.BERENIKE_KNIGHT_DEATH.get();
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
