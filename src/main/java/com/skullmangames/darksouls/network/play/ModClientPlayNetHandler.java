package com.skullmangames.darksouls.network.play;

import java.util.HashSet;
import java.util.Set;

import com.skullmangames.darksouls.client.gui.screens.BonfireNameScreen;
import com.skullmangames.darksouls.client.gui.screens.BonfireScreen;
import com.skullmangames.darksouls.client.gui.screens.FireKeeperScreen;
import com.skullmangames.darksouls.client.sound.BonfireAmbientSoundInstance;
import com.skullmangames.darksouls.common.blockentity.BonfireBlockEntity;
import com.skullmangames.darksouls.core.init.ModBlockEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModClientPlayNetHandler implements IModClientPlayNetHandler
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
	public void openFireKeeperScreen(int firekeeperid)
	{
		this.minecraft.setScreen(new FireKeeperScreen(firekeeperid));
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
}
