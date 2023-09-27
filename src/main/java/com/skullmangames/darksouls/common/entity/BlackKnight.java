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
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class BlackKnight extends ArmoredMob
{
	public BlackKnight(EntityType<? extends BlackKnight> entitytype, Level level)
	{
		super(entitytype, level);
	}
	
	public static AttributeSupplier.Builder createAttributes()
	{
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 672D)
				.add(Attributes.ATTACK_DAMAGE, 1.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.27D);
	}
	
	@Override
	protected void registerGoals()
	{
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, StrayDemon.class, true));
	}

	@Override
	protected Item getEquipmentForSlot(int percentage, EquipmentSlot slot)
	{
		if (slot == EquipmentSlot.MAINHAND) return ModItems.BLACK_KNIGHT_SWORD.get();
		else if (slot == EquipmentSlot.OFFHAND) return ModItems.BLACK_KNIGHT_SHIELD.get();

		switch (slot)
		{
		default:
			return null;
		case HEAD:
			return ModItems.BLACK_KNIGHT_HELM.get();
		case CHEST:
			return ModItems.BLACK_KNIGHT_ARMOR.get();
		case LEGS:
			return ModItems.BLACK_KNIGHT_LEGGINGS.get();
		case FEET:
			return null;
		}
	}
	
	@Override
	protected SoundEvent getHurtSound(DamageSource source)
	{
		return ModSoundEvents.BLACK_KNIGHT_DAMAGE.get();
	}
	
	@Override
	protected SoundEvent getDeathSound()
	{
		return ModSoundEvents.BLACK_KNIGHT_DEATH.get();
	}
}
