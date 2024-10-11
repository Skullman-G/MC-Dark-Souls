package com.skullmangames.darksouls.network.play;

import java.util.List;

import com.mojang.datafixers.util.Pair;
import com.skullmangames.darksouls.common.entity.covenant.Covenant;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class ModPlayNetHandler
{
	public void setTitle(Component text, int fadein, int stay, int fadeout) {}

	public void setOverlayMessage(Component text) {}

	public void openBonfireNameScreen(BlockPos blockPos) {}

	public void openBonfireScreen(BlockPos blockPos) {}

	public void tryPlayBonfireAmbientSound(BlockPos blockPos) {}

	public void removeBonfireAmbientSound(BlockPos blockPos) {}

	public void openFireKeeperScreen(int entityId) {}

	public void openJoinCovenantScreen(Covenant covenant) {}

	public void openCovenantScreen(Covenant covenant) {}

	public void openBonfireTeleportScreen(BlockPos blockPos, List<Pair<String, BlockPos>> teleports) {}

	public void shakeCam(Vec3 source, int duration, float magnitude) {}

	public void shakeCamForEntity(Entity entity, int duration, float magnitude) {}

	public void playEntitySound(Entity entity, SoundEvent sound, float volume) {}

	public void playSound(Entity entity, SoundEvent sound, float volume) {}

	public void bonfireKindleEffect(BlockPos pos) {}

	public void makeImpactParticles(Entity entity, Vec3 impactPos, boolean blocked) {}
	
	public void spawnParticlesCircle(SimpleParticleType particle, Vec3 pos, float radius) {}
}
