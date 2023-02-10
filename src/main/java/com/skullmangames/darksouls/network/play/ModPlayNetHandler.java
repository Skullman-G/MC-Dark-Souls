package com.skullmangames.darksouls.network.play;

import java.util.List;

import com.mojang.datafixers.util.Pair;
import com.skullmangames.darksouls.common.entity.Covenant;

import net.minecraft.entity.Entity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;

public interface ModPlayNetHandler
{
	void setTitle(ITextComponent text, int fadein, int stay, int fadeout);
	
	void setOverlayMessage(ITextComponent text);
	
	void openBonfireNameScreen(BlockPos blockPos);
	
	void openBonfireScreen(BlockPos blockPos);
	
	void tryPlayBonfireAmbientSound(BlockPos blockPos);
	
	void removeBonfireAmbientSound(BlockPos blockPos);
	
	void openFireKeeperScreen(int entityId);
	
	void openJoinCovenantScreen(Covenant covenant);
	
	void openCovenantScreen(Covenant covenant);
	
	void openBonfireTeleportScreen(BlockPos blockPos, List<Pair<String, BlockPos>> teleports);
	
	void shakeCam(Vector3d source, int duration, float magnitude);
	
	void playEntitySound(Entity entity, SoundEvent sound, float volume);
	
	void playSound(Entity entity, SoundEvent sound, float volume);
	
	void bonfireKindleEffect(BlockPos pos);
	
	void makeImpactParticles(int entityId, Vector3d impactPos, boolean blocked);
}
