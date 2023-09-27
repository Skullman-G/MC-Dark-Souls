package com.skullmangames.darksouls.common.entity;

import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.core.init.ModSoundEvents;

import net.minecraft.sounds.SoundEvent;
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

public class BalderKnight extends ArmoredMob
{
	public BalderKnight(EntityType<? extends BalderKnight> entitytype, Level level)
	{
		super(entitytype, level);
	}
	
	public static AttributeSupplier.Builder createAttributes()
	{
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 129D)
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
		if (slot == EquipmentSlot.OFFHAND) return ModItems.BALDER_SHIELD.get();
		
		switch (slot)
		{
		default:
			return null;
		case HEAD:
			return ModItems.BALDER_HELM.get();
		case CHEST:
			return ModItems.BALDER_ARMOR.get();
		case LEGS:
			return ModItems.BALDER_LEGGINGS.get();
		case FEET:
			return ModItems.BALDER_BOOTS.get();
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
