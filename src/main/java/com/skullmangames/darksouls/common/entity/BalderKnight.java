package com.skullmangames.darksouls.common.entity;

import java.util.function.Predicate;

import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.core.init.ModSoundEvents;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;

public class BalderKnight extends ArmoredMob implements CrossbowAttackMob
{
	public BalderKnight(EntityType<? extends BalderKnight> entitytype, Level level)
	{
		super(entitytype, level);
	}
	
	public static AttributeSupplier.Builder createAttributes()
	{
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 429D)
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
			if (slot == EquipmentSlot.MAINHAND) return ModItems.BALDER_SIDE_SWORD.get();
			if (slot == EquipmentSlot.OFFHAND) return ModItems.BALDER_SHIELD.get();
		}
		else if (percentage <= 80)
		{
			if (slot == EquipmentSlot.MAINHAND) return ModItems.RAPIER.get();
			if (slot == EquipmentSlot.OFFHAND) return ModItems.BUCKLER.get();
		}
		else
		{
			if (slot == EquipmentSlot.MAINHAND) return Items.CROSSBOW;
		}
		
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
		return ModSoundEvents.BALDER_KNIGHT_AMBIENT.get();
	}
	
	@Override
	protected SoundEvent getHurtSound(DamageSource source)
	{
		return ModSoundEvents.BALDER_KNIGHT_DAMAGE.get();
	}
	
	@Override
	protected SoundEvent getDeathSound()
	{
		return ModSoundEvents.BALDER_KNIGHT_DEATH.get();
	}
	
	@Override
	public void performRangedAttack(LivingEntity p_33317_, float p_33318_)
	{
		this.performCrossbowAttack(this, 1.6F);
	}

	@Override
	public void setChargingCrossbow(boolean value) {}

	@Override
	public void shootCrossbowProjectile(LivingEntity target, ItemStack crossbow, Projectile ammo, float f)
	{
		this.shootCrossbowProjectile(this, target, ammo, f, 1.6F);
	}

	@Override
	public void onCrossbowAttackPerformed()
	{
		this.noActionTime = 0;
	}

	@Override
	public ItemStack getProjectile(ItemStack stack)
	{
		if (stack.getItem() instanceof ProjectileWeaponItem)
		{
			Predicate<ItemStack> predicate = ((ProjectileWeaponItem) stack.getItem()).getSupportedHeldProjectiles();
			ItemStack itemstack = ProjectileWeaponItem.getHeldProjectile(this, predicate);
			return itemstack.isEmpty() ? new ItemStack(Items.ARROW) : itemstack;
		} else
		{
			return ItemStack.EMPTY;
		}
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
