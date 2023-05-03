package com.skullmangames.darksouls.common.entity;

import com.skullmangames.darksouls.core.init.ModItems;

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
				.add(Attributes.MAX_HEALTH, 50.0D)
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
	protected int getExperienceReward(Player player)
	{
		return 1800;
	}

	@Override
	protected Item getEquipmentForSlot(int percentage, EquipmentSlot slot)
	{
		if (slot == EquipmentSlot.MAINHAND) return ModItems.BLACK_KNIGHT_SWORD.get();
		else if (slot == EquipmentSlot.OFFHAND) return null;

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
}
