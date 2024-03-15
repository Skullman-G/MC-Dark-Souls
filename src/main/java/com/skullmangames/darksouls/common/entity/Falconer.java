package com.skullmangames.darksouls.common.entity;

import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.core.init.ModSoundEvents;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
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

public class Falconer extends ArmedMob implements RangedAttackMob
{
	public Falconer(EntityType<? extends Falconer> entitytype, Level level)
	{
		super(entitytype, level);
	}
	
	public static AttributeSupplier.Builder createAttributes()
	{
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 244D)
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
	public Item getEquipmentForSlot(int equipmentType, EquipmentSlot slot)
	{
		switch (equipmentType)
		{
			default:
				break;
				
			case 0:
				if (slot == EquipmentSlot.MAINHAND) return ModItems.LONGSWORD.get();
				else if (slot == EquipmentSlot.OFFHAND) return ModItems.GOLDEN_FALCON_SHIELD.get();
				break;
				
			case 1:
				if (slot == EquipmentSlot.MAINHAND) return Items.BOW;
				break;
		}

		switch (slot)
		{
			default: return null;
			case HEAD: return ModItems.FALCONER_HELM.get();
			case CHEST: return ModItems.FALCONER_ARMOR.get();
			case LEGS: return ModItems.FALCONER_LEGGINGS.get();
			case FEET: return ModItems.FALCONER_BOOTS.get();
		}
	}
	
	@Override
	protected int getMaxEquipmentTypes()
	{
		return 2;
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
