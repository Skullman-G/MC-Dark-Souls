package com.skullmangames.darksouls.common.entity;

import com.skullmangames.darksouls.common.entity.ai.goal.NearestNotKindOfMeTargetGoal;
import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.core.init.ModSoundEvents;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;

public class AsylumDemonEntity extends CreatureEntity
{
	private final ServerBossInfo bossInfo = new ServerBossInfo(this.getDisplayName(), BossInfo.Color.RED, BossInfo.Overlay.PROGRESS);
	
	public AsylumDemonEntity(EntityType<? extends CreatureEntity> p_i48575_1_, World p_i48575_2_)
	{
		super(p_i48575_1_, p_i48575_2_);
	}
	
	@Override
	protected int getExperienceReward(PlayerEntity p_70693_1_)
	{
		return 100;
	}
	
	@Override
	protected SoundEvent getAmbientSound()
	{
		return ModSoundEvents.ASYLUM_DEMON_AMBIENT;
	}
	
	@Override
	protected void customServerAiStep()
	{
		super.customServerAiStep();
		this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
	}
	
	public void startSeenByPlayer(ServerPlayerEntity player)
	{
	    super.startSeenByPlayer(player);
	    this.bossInfo.addPlayer(player);
	}

	public void stopSeenByPlayer(ServerPlayerEntity player)
	{
	    super.stopSeenByPlayer(player);
	    this.bossInfo.removePlayer(player);
	}
	
	@Override
	protected void populateDefaultEquipmentSlots(DifficultyInstance difficulty)
	{
		super.populateDefaultEquipmentSlots(difficulty);
		
		this.setItemSlot(EquipmentSlotType.MAINHAND, new ItemStack(ModItems.DEMON_GREAT_HAMMER.get()));
		this.setDropChance(EquipmentSlotType.MAINHAND, 0.04F);
	}
	
	@Override
	public ILivingEntityData finalizeSpawn(IServerWorld level, DifficultyInstance difficulty, SpawnReason reason, ILivingEntityData data, CompoundNBT nbt)
	{
		data = super.finalizeSpawn(level, difficulty, reason, data, nbt);
		this.populateDefaultEquipmentSlots(difficulty);
		return data;
	}
	
	@Override
	public boolean canBeCollidedWith()
	{
		return true;
	}
	
	public static AttributeModifierMap.MutableAttribute createAttributes()
	{
		return MobEntity.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 41.25D)
				.add(Attributes.ATTACK_DAMAGE, 1.0D)
				.add(Attributes.ATTACK_KNOCKBACK, 1.0D)
				.add(Attributes.ATTACK_SPEED, 1.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.2D);
	}
	
	@Override
	public boolean removeWhenFarAway(double p_213397_1_)
	{
		return false;
	}
	
	@Override
	protected void registerGoals()
	{
		this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8.0F));
	    this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
	    this.goalSelector.addGoal(7, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
	    
	    this.targetSelector.addGoal(2, new NearestNotKindOfMeTargetGoal<>(this, LivingEntity.class, true));
	}
}
