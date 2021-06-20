package com.skullmangames.darksouls.common.entities;

import com.skullmangames.darksouls.common.entities.ai.goal.WalkAroundBonfireGoal;
import com.skullmangames.darksouls.common.tiles.BonfireTileEntity;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FireKeeperEntity extends CreatureEntity
{
	private BlockPos linkedBonfire = BlockPos.ZERO;
	private boolean hasLinkedBonfire = false;
	
	public FireKeeperEntity(EntityType<? extends CreatureEntity> entity, World world)
	{
		super(entity, world);
	}
	
	public boolean hasLinkedBonfire()
	{
		return this.hasLinkedBonfire;
	}
	
	public void linkBonfire(BlockPos pos)
	{
		this.linkedBonfire = pos;
		this.hasLinkedBonfire = true;
	}
	
	public BlockPos getLinkedBonfire()
	{
		return this.linkedBonfire;
	}
	
	public static AttributeModifierMap.MutableAttribute createAttributes()
	{
		return MobEntity.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 20.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.15D);
	}
	
	@Override
	protected void registerGoals()
	{
		this.goalSelector.addGoal(0, new SwimGoal(this));
		this.goalSelector.addGoal(1, new WalkAroundBonfireGoal(this, 1.0D));
		this.goalSelector.addGoal(2, new LookAtGoal(this, PlayerEntity.class, 6.0F));
	    this.goalSelector.addGoal(3, new LookRandomlyGoal(this));
	}
	
	@Override
	public boolean removeWhenFarAway(double p_213397_1_)
	{
		return this.linkedBonfire == null;
	}
	
	@Override
	public void addAdditionalSaveData(CompoundNBT nbt)
	{
		super.addAdditionalSaveData(nbt);
		nbt.putInt("linked_bonfire_x", this.linkedBonfire.getX());
		nbt.putInt("linked_bonfire_y", this.linkedBonfire.getY());
		nbt.putInt("linked_bonfire_z", this.linkedBonfire.getZ());
		nbt.putBoolean("has_linked_bonfire", this.hasLinkedBonfire);
	}
	
	@Override
	public void readAdditionalSaveData(CompoundNBT nbt)
	{
		super.readAdditionalSaveData(nbt);
		this.linkedBonfire = new BlockPos(nbt.getInt("linked_bonfire_x"), nbt.getInt("linked_bonfire_y"), nbt.getInt("linked_bonfire_z"));
		this.hasLinkedBonfire = nbt.getBoolean("has_linked_bonfire");
	}
	
	@Override
	public void tick()
	{
		if (this.hasLinkedBonfire && this.level.isEmptyBlock(this.linkedBonfire))
		{
			this.die(DamageSource.OUT_OF_WORLD);
		}
		
		super.tick();
	}
	
	@Override
	public void die(DamageSource p_70645_1_)
	{
		if (this.hasLinkedBonfire && !this.level.isEmptyBlock(this.linkedBonfire))
		{
			((BonfireTileEntity)this.level.getBlockEntity(this.linkedBonfire)).setLit(null, false);
		}
		super.die(p_70645_1_);
	}
}