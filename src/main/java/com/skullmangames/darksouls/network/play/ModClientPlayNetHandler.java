package com.skullmangames.darksouls.network.play;

import java.util.HashSet;
import java.util.Set;

import com.skullmangames.darksouls.client.gui.screens.BonfireNameScreen;
import com.skullmangames.darksouls.client.gui.screens.BonfireScreen;
import com.skullmangames.darksouls.client.sound.BonfireAmbientSoundInstance;
import com.skullmangames.darksouls.common.blockentity.BonfireBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
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
	public void setTitle(ITextComponent text, int fadein, int stay, int fadeout)
	{
		this.minecraft.gui.setTitles(text, StringTextComponent.EMPTY, fadein, stay, fadeout);
	}
	
	@Override
	public void setOverlayMessage(ITextComponent text)
	{
		this.minecraft.gui.setOverlayMessage(text, false);
	}

	@Override
	public void openBonfireNameScreen(BlockPos blockPos)
	{
		TileEntity tileentity = this.minecraft.level.getBlockEntity(blockPos);
		BonfireBlockEntity bonfire = tileentity instanceof BonfireBlockEntity ? (BonfireBlockEntity)tileentity : null;
		if (bonfire != null) this.minecraft.setScreen(new BonfireNameScreen(bonfire));
	}

	@Override
	public void openBonfireScreen(BlockPos blockPos)
	{
		TileEntity tileentity = this.minecraft.level.getBlockEntity(blockPos);
		BonfireBlockEntity bonfire = tileentity instanceof BonfireBlockEntity ? (BonfireBlockEntity)tileentity : null;
		if (bonfire != null) this.minecraft.setScreen(new BonfireScreen(bonfire));
	}

	
	Set<BlockPos> sounds = new HashSet<>();
	@Override
	public void tryPlayBonfireAmbientSound(BlockPos blockPos)
	{
		float dist = (float)this.minecraft.player.distanceToSqr(blockPos.getX(), blockPos.getY(), blockPos.getZ());
		if (!this.sounds.contains(blockPos) && (1.0F - (dist * 0.005F)) >= 0.0F)
		{
			TileEntity tileentity = this.minecraft.player.level.getBlockEntity(blockPos);
			BonfireBlockEntity bonfire = tileentity instanceof BonfireBlockEntity ? (BonfireBlockEntity)tileentity : null;
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
