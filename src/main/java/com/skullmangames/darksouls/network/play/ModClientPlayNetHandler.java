package com.skullmangames.darksouls.network.play;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mojang.datafixers.util.Pair;
import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.client.gui.screens.BonfireNameScreen;
import com.skullmangames.darksouls.client.gui.screens.BonfireScreen;
import com.skullmangames.darksouls.client.gui.screens.BonfireTeleportScreen;
import com.skullmangames.darksouls.client.gui.screens.CovenantScreen;
import com.skullmangames.darksouls.client.gui.screens.FireKeeperScreen;
import com.skullmangames.darksouls.client.gui.screens.JoinCovenantScreen;
import com.skullmangames.darksouls.client.sound.BonfireAmbientSoundInstance;
import com.skullmangames.darksouls.common.blockentity.BonfireBlockEntity;
import com.skullmangames.darksouls.common.entity.Covenant;
import com.skullmangames.darksouls.core.init.ModBlockEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModClientPlayNetHandler implements ModPlayNetHandler
{
	private final Minecraft minecraft;
	
	public ModClientPlayNetHandler()
	{
		this.minecraft = Minecraft.getInstance();
	}

	@Override
	public void setTitle(Component text, int fadein, int stay, int fadeout)
	{
		this.minecraft.gui.setTimes(fadein, stay, fadeout);
		this.minecraft.gui.setTitle(text);
	}
	
	@Override
	public void setOverlayMessage(Component text)
	{
		this.minecraft.gui.setOverlayMessage(text, false);
	}

	@Override
	public void openBonfireNameScreen(BlockPos blockPos)
	{
		BonfireBlockEntity bonfire = this.minecraft.level.getBlockEntity(blockPos, ModBlockEntities.BONFIRE.get()).orElse(null);
		if (bonfire != null) this.minecraft.setScreen(new BonfireNameScreen(bonfire));
	}

	@Override
	public void openBonfireScreen(BlockPos blockPos)
	{
		BonfireBlockEntity bonfire = this.minecraft.level.getBlockEntity(blockPos, ModBlockEntities.BONFIRE.get()).orElse(null);
		if (bonfire != null) this.minecraft.setScreen(new BonfireScreen(bonfire));
	}

	
	Set<BlockPos> sounds = new HashSet<>();
	@Override
	public void tryPlayBonfireAmbientSound(BlockPos blockPos)
	{
		float dist = (float)this.minecraft.player.distanceToSqr(blockPos.getX(), blockPos.getY(), blockPos.getZ());
		if (!this.sounds.contains(blockPos) && (1.0F - (dist * 0.005F)) >= 0.0F)
		{
			BonfireBlockEntity bonfire = this.minecraft.player.level.getBlockEntity(blockPos, ModBlockEntities.BONFIRE.get()).orElse(null);
			if (bonfire == null) return;
			BonfireAmbientSoundInstance soundInstance = new BonfireAmbientSoundInstance(bonfire);
			this.minecraft.getSoundManager().queueTickingSound(soundInstance);
			this.sounds.add(blockPos.immutable());
		}
	}

	@Override
	public void removeBonfireAmbientSound(BlockPos blockPos)
	{
		this.sounds.remove(blockPos);
	}

	@Override
	public void openFireKeeperScreen(int entityId)
	{
		this.minecraft.setScreen(new FireKeeperScreen(entityId));
	}
	
	@Override
	public void openJoinCovenantScreen(Covenant covenant)
	{
		this.minecraft.setScreen(new JoinCovenantScreen(covenant));
	}

	@Override
	public void openCovenantScreen(Covenant covenant)
	{
		this.minecraft.setScreen(new CovenantScreen(covenant));
	}

	@Override
	public void openBonfireTeleportScreen(BlockPos blockPos, List<Pair<String, BlockPos>> teleports)
	{
		BonfireBlockEntity bonfire = this.minecraft.level.getBlockEntity(blockPos, ModBlockEntities.BONFIRE.get()).orElse(null);
		if (bonfire != null) this.minecraft.setScreen(new BonfireTeleportScreen(teleports));
	}

	@Override
	public void shakeCam(Vec3 source, int duration, float magnitude)
	{
		if (this.minecraft.player.distanceToSqr(source) < 1000)
		{
			ClientManager.INSTANCE.mainCamera.shake(duration, magnitude);
		}
	}
}
