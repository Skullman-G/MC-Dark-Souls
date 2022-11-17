package com.skullmangames.darksouls.network.play;

import java.util.List;

import com.mojang.datafixers.util.Pair;
import com.skullmangames.darksouls.common.entity.Covenant;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

public interface ModPlayNetHandler
{
	void setTitle(Component text, int fadein, int stay, int fadeout);
	
	void setOverlayMessage(Component text);
	
	void openBonfireNameScreen(BlockPos blockPos);
	
	void openBonfireScreen(BlockPos blockPos);
	
	void tryPlayBonfireAmbientSound(BlockPos blockPos);
	
	void removeBonfireAmbientSound(BlockPos blockPos);
	
	void openFireKeeperScreen(int entityId);
	
	void openJoinCovenantScreen(Covenant covenant);
	
	void openCovenantScreen(Covenant covenant);
	
	void openBonfireTeleportScreen(BlockPos blockPos, List<Pair<String, BlockPos>> teleports);
	
	void shakeCam(Vec3 source, int duration, float magnitude);
}
