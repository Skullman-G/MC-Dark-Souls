package com.skullmangames.darksouls.server;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.skullmangames.darksouls.common.entities.player.ServerPlayerEntityOverride;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.network.play.server.SRespawnPacket;
import net.minecraft.network.play.server.SServerDifficultyPacket;
import net.minecraft.network.play.server.SSetExperiencePacket;
import net.minecraft.network.play.server.SWorldSpawnChangedPacket;
import net.minecraft.server.integrated.IntegratedPlayerList;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.management.DemoPlayerInteractionManager;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.DynamicRegistries.Impl;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.IWorldInfo;
import net.minecraft.world.storage.PlayerData;

public class IntegratedPlayerListOverride extends IntegratedPlayerList
{
	public IntegratedPlayerListOverride(IntegratedServer p_i232493_1_, Impl p_i232493_2_, PlayerData p_i232493_3_)
	{
		super(p_i232493_1_, p_i232493_2_, p_i232493_3_);
	}
	
	@Override
	public ServerPlayerEntity getPlayerForLogin(GameProfile gameprofile)
	{
		UUID uuid = PlayerEntity.createPlayerUUID(gameprofile);
	      List<ServerPlayerEntityOverride> list = Lists.newArrayList();

	      for(int i = 0; i < this.players.size(); ++i) {
	         ServerPlayerEntityOverride serverplayerentity = (ServerPlayerEntityOverride)this.players.get(i);
	         if (serverplayerentity.getUUID().equals(uuid)) {
	            list.add(serverplayerentity);
	         }
	      }

	      ServerPlayerEntityOverride serverplayerentity2 = (ServerPlayerEntityOverride)this.playersByUUID.get(gameprofile.getId());
	      if (serverplayerentity2 != null && !list.contains(serverplayerentity2)) {
	         list.add(serverplayerentity2);
	      }

	      for(ServerPlayerEntity serverplayerentity1 : list) {
	         serverplayerentity1.connection.disconnect(new TranslationTextComponent("multiplayer.disconnect.duplicate_login"));
	      }

	      ServerWorld serverworld = this.server.overworld();
	      PlayerInteractionManager playerinteractionmanager;
	      if (this.server.isDemo()) {
	         playerinteractionmanager = new DemoPlayerInteractionManager(serverworld);
	      } else {
	         playerinteractionmanager = new PlayerInteractionManager(serverworld);
	      }

	      return new ServerPlayerEntityOverride(this.server, serverworld, gameprofile, playerinteractionmanager);
	}
	
	@Override
	public ServerPlayerEntity respawn(ServerPlayerEntity p_232644_1_, boolean p_232644_2_)
	{
	      this.removePlayer(p_232644_1_);
	      p_232644_1_.getLevel().removePlayer(p_232644_1_, true); // Forge: keep data until copyFrom called
	      BlockPos blockpos = p_232644_1_.getRespawnPosition();
	      float f = p_232644_1_.getRespawnAngle();
	      boolean flag = p_232644_1_.isRespawnForced();
	      ServerWorld serverworld = this.server.getLevel(p_232644_1_.getRespawnDimension());
	      Optional<Vector3d> optional;
	      if (serverworld != null && blockpos != null) {
	         optional = PlayerEntity.findRespawnPositionAndUseSpawnBlock(serverworld, blockpos, f, flag, p_232644_2_);
	      } else {
	         optional = Optional.empty();
	      }

	      ServerWorld serverworld1 = serverworld != null && optional.isPresent() ? serverworld : this.server.overworld();
	      PlayerInteractionManager playerinteractionmanager;
	      if (this.server.isDemo()) {
	         playerinteractionmanager = new DemoPlayerInteractionManager(serverworld1);
	      } else {
	         playerinteractionmanager = new PlayerInteractionManager(serverworld1);
	      }

	      ServerPlayerEntityOverride serverplayerentity = new ServerPlayerEntityOverride(this.server, serverworld1, p_232644_1_.getGameProfile(), playerinteractionmanager);
	      serverplayerentity.connection = p_232644_1_.connection;
	      serverplayerentity.restoreFrom(p_232644_1_, p_232644_2_);
	      p_232644_1_.remove(false); // Forge: clone event had a chance to see old data, now discard it
	      serverplayerentity.setId(p_232644_1_.getId());
	      serverplayerentity.setMainArm(p_232644_1_.getMainArm());

	      for(String s : p_232644_1_.getTags()) {
	         serverplayerentity.addTag(s);
	      }

	      this.updatePlayerGameMode(serverplayerentity, p_232644_1_, serverworld1);
	      boolean flag2 = false;
	      if (optional.isPresent()) {
	         BlockState blockstate = serverworld1.getBlockState(blockpos);
	         boolean flag1 = blockstate.is(Blocks.RESPAWN_ANCHOR);
	         Vector3d vector3d = optional.get();
	         float f1;
	         if (!blockstate.is(BlockTags.BEDS) && !flag1) {
	            f1 = f;
	         } else {
	            Vector3d vector3d1 = Vector3d.atBottomCenterOf(blockpos).subtract(vector3d).normalize();
	            f1 = (float)MathHelper.wrapDegrees(MathHelper.atan2(vector3d1.z, vector3d1.x) * (double)(180F / (float)Math.PI) - 90.0D);
	         }

	         serverplayerentity.moveTo(vector3d.x, vector3d.y, vector3d.z, f1, 0.0F);
	         serverplayerentity.setRespawnPosition(serverworld1.dimension(), blockpos, f, flag, false);
	         flag2 = !p_232644_2_ && flag1;
	      } else if (blockpos != null) {
	         serverplayerentity.connection.send(new SChangeGameStatePacket(SChangeGameStatePacket.NO_RESPAWN_BLOCK_AVAILABLE, 0.0F));
	      }

	      while(!serverworld1.noCollision(serverplayerentity) && serverplayerentity.getY() < 256.0D) {
	         serverplayerentity.setPos(serverplayerentity.getX(), serverplayerentity.getY() + 1.0D, serverplayerentity.getZ());
	      }

	      IWorldInfo iworldinfo = serverplayerentity.level.getLevelData();
	      serverplayerentity.connection.send(new SRespawnPacket(serverplayerentity.level.dimensionType(), serverplayerentity.level.dimension(), BiomeManager.obfuscateSeed(serverplayerentity.getLevel().getSeed()), serverplayerentity.gameMode.getGameModeForPlayer(), serverplayerentity.gameMode.getPreviousGameModeForPlayer(), serverplayerentity.getLevel().isDebug(), serverplayerentity.getLevel().isFlat(), p_232644_2_));
	      serverplayerentity.connection.teleport(serverplayerentity.getX(), serverplayerentity.getY(), serverplayerentity.getZ(), serverplayerentity.yRot, serverplayerentity.xRot);
	      serverplayerentity.connection.send(new SWorldSpawnChangedPacket(serverworld1.getSharedSpawnPos(), serverworld1.getSharedSpawnAngle()));
	      serverplayerentity.connection.send(new SServerDifficultyPacket(iworldinfo.getDifficulty(), iworldinfo.isDifficultyLocked()));
	      serverplayerentity.connection.send(new SSetExperiencePacket(serverplayerentity.experienceProgress, serverplayerentity.totalExperience, serverplayerentity.experienceLevel));
	      this.sendLevelInfo(serverplayerentity, serverworld1);
	      this.sendPlayerPermissionLevel(serverplayerentity);
	      serverworld1.addRespawnedPlayer(serverplayerentity);
	      this.addPlayer(serverplayerentity);
	      this.playersByUUID.put(serverplayerentity.getUUID(), serverplayerentity);
	      serverplayerentity.initMenu();
	      serverplayerentity.setHealth(serverplayerentity.getHealth());
	      net.minecraftforge.fml.hooks.BasicEventHooks.firePlayerRespawnEvent(serverplayerentity, p_232644_2_);
	      if (flag2) {
	         serverplayerentity.connection.send(new SPlaySoundEffectPacket(SoundEvents.RESPAWN_ANCHOR_DEPLETE, SoundCategory.BLOCKS, (double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), 1.0F, 1.0F));
	      }

	      return serverplayerentity;
	}
}
