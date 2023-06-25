package com.skullmangames.darksouls.common.entity;

import java.util.Random;

import com.skullmangames.darksouls.core.init.ModSoundEvents;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class TaurusDemon extends PathfinderMob implements Demon
{
	private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS);
	
	public TaurusDemon(EntityType<? extends PathfinderMob> type, Level level)
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
	
	@Override
	protected int getExperienceReward(Player player)
	{
		return 3000;
	}
	
	public static boolean checkSpawnRules(EntityType<StrayDemon> entitytype, ServerLevelAccessor level, MobSpawnType spawntype, BlockPos pos, Random random)
	{
		return level.getDifficulty() != Difficulty.PEACEFUL && checkMobSpawnRules(entitytype, level, spawntype, pos, random);
	}
	
	@Override
	protected SoundEvent getAmbientSound()
	{
		return ModSoundEvents.TAURUS_DEMON_AMBIENT.get();
	}
	
	@Override
	protected SoundEvent getDeathSound()
	{
		return ModSoundEvents.TAURUS_DEMON_DEATH.get();
	}
	
	@Override
	protected void customServerAiStep()
	{
		super.customServerAiStep();
		this.bossInfo.setProgress((this.getHealth() / this.getMaxHealth()));
	}

	@Override
	public void stopSeenByPlayer(ServerPlayer player)
	{
	    super.stopSeenByPlayer(player);
	    this.bossInfo.removePlayer(player);
	}
	
	@Override
	public void setTarget(LivingEntity target)
	{
		super.setTarget(target);
		if (target instanceof ServerPlayer) this.bossInfo.addPlayer((ServerPlayer) target);
	}
	
	@Override
	public boolean canBeCollidedWith()
	{
		return true;
	}
	
	@Override
	public boolean isPushable()
	{
		return false;
	}
	
	public static AttributeSupplier.Builder createAttributes()
	{
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 1215D)
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
	protected void registerGoals()
	{
		this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.8D));
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
	}
}
