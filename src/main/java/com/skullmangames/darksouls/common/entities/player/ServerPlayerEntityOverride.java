package com.skullmangames.darksouls.common.entities.player;

import java.util.List;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.stats.Stats;
import net.minecraft.util.Direction;
import net.minecraft.util.Unit;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

public class ServerPlayerEntityOverride extends ServerPlayerEntity
{
	public ServerPlayerEntityOverride(MinecraftServer p_i45285_1_, ServerWorld p_i45285_2_, GameProfile p_i45285_3_, PlayerInteractionManager p_i45285_4_)
	{
		super(p_i45285_1_, p_i45285_2_, p_i45285_3_, p_i45285_4_);
	}
	
	@Override
	public Either<PlayerEntity.SleepResult, Unit> startSleepInBed(BlockPos pos)
	{
		java.util.Optional<BlockPos> optAt = java.util.Optional.of(pos);
	      PlayerEntity.SleepResult ret = net.minecraftforge.event.ForgeEventFactory.onPlayerSleepInBed(this, optAt);
	      if (ret != null) return Either.left(ret);
	      Direction direction = this.level.getBlockState(pos).getValue(HorizontalBlock.FACING);
	      if (!this.isSleeping() && this.isAlive()) {
	         if (!this.level.dimensionType().natural()) {
	            return Either.left(PlayerEntity.SleepResult.NOT_POSSIBLE_HERE);
	         } else if (!this.bedInRange(pos, direction)) {
	            return Either.left(PlayerEntity.SleepResult.TOO_FAR_AWAY);
	         } else if (this.bedBlocked(pos, direction)) {
	            return Either.left(PlayerEntity.SleepResult.OBSTRUCTED);
	         } else {
	            if (!net.minecraftforge.event.ForgeEventFactory.fireSleepingTimeCheck(this, optAt)) {
	               return Either.left(PlayerEntity.SleepResult.NOT_POSSIBLE_NOW);
	            } else {
	               if (!this.isCreative()) {
	                  Vector3d vector3d = Vector3d.atBottomCenterOf(pos);
	                  List<MonsterEntity> list = this.level.getEntitiesOfClass(MonsterEntity.class, new AxisAlignedBB(vector3d.x() - 8.0D, vector3d.y() - 5.0D, vector3d.z() - 8.0D, vector3d.x() + 8.0D, vector3d.y() + 5.0D, vector3d.z() + 8.0D), (p_241146_1_) -> {
	                     return p_241146_1_.isPreventingPlayerRest(this);
	                  });
	                  if (!list.isEmpty()) {
	                     return Either.left(PlayerEntity.SleepResult.NOT_SAFE);
	                  }
	               }

	               Either<PlayerEntity.SleepResult, Unit> either = this.sleepInBed(pos).ifRight((p_241144_1_) -> {
	                  this.awardStat(Stats.SLEEP_IN_BED);
	                  CriteriaTriggers.SLEPT_IN_BED.trigger(this);
	               });
	               ((ServerWorld)this.level).updateSleepingPlayerList();
	               return either;
	            }
	         }
	      } else {
	         return Either.left(PlayerEntity.SleepResult.OTHER_PROBLEM);
	      }
	}
	
	private Either<PlayerEntity.SleepResult, Unit> sleepInBed(BlockPos pos)
	{
	    this.startSleeping(pos);
	    this.sleepCounter = 0;
	    return Either.right(Unit.INSTANCE);
	}
}
