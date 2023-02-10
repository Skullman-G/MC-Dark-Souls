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
import com.skullmangames.darksouls.common.block.BonfireBlock;
import com.skullmangames.darksouls.common.blockentity.BonfireBlockEntity;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.entity.Covenant;
import com.skullmangames.darksouls.core.init.ModBlockEntities;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.util.ModUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.SoundEvent;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3d;
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
		BonfireBlockEntity bonfire = ModUtil.getBlockEntity(this.minecraft.level, blockPos, ModBlockEntities.BONFIRE.get()).orElse(null);
		if (bonfire != null) this.minecraft.setScreen(new BonfireNameScreen(bonfire));
	}

	@Override
	public void openBonfireScreen(BlockPos blockPos)
	{
		BonfireBlockEntity bonfire = ModUtil.getBlockEntity(this.minecraft.level, blockPos, ModBlockEntities.BONFIRE.get()).orElse(null);
		if (bonfire != null) this.minecraft.setScreen(new BonfireScreen(bonfire));
	}

	
	Set<BlockPos> sounds = new HashSet<>();
	@Override
	public void tryPlayBonfireAmbientSound(BlockPos blockPos)
	{
		float dist = (float)this.minecraft.player.distanceToSqr(blockPos.getX(), blockPos.getY(), blockPos.getZ());
		if (!this.sounds.contains(blockPos) && (1.0F - (dist * 0.005F)) >= 0.0F)
		{
			BonfireBlockEntity bonfire = ModUtil.getBlockEntity(this.minecraft.level, blockPos, ModBlockEntities.BONFIRE.get()).orElse(null);
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
		BonfireBlockEntity bonfire = ModUtil.getBlockEntity(this.minecraft.level, blockPos, ModBlockEntities.BONFIRE.get()).orElse(null);
		if (bonfire != null) this.minecraft.setScreen(new BonfireTeleportScreen(teleports));
	}

	@Override
	public void shakeCam(Vector3d source, int duration, float magnitude)
	{
		if (this.minecraft.player.distanceToSqr(source) < 1000)
		{
			ClientManager.INSTANCE.mainCamera.shake(duration, magnitude);
		}
	}

	@Override
	public void playEntitySound(Entity entity, SoundEvent sound, float volume)
	{
		entity.level.playSound(this.minecraft.player, entity, sound, entity.getSoundSource(), volume, 1.0F);
	}

	@Override
	public void playSound(Entity entity, SoundEvent sound, float volume)
	{
		entity.level.playSound(this.minecraft.player, entity.getX(), entity.getY(), entity.getZ(), sound, entity.getSoundSource(), volume, 1.0F);
	}

	@Override
	public void bonfireKindleEffect(BlockPos pos)
	{
		BonfireBlock.kindleEffect(this.minecraft.level, pos);
	}

	@Override
	public void makeImpactParticles(int entityId, Vector3d impactPos, boolean blocked)
	{
		Entity entity = this.minecraft.level.getEntity(entityId);
		LivingCap<?> cap = (LivingCap<?>)entity.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
		if (cap != null)
		{
			cap.makeImpactParticles(impactPos, blocked);
		}
	}
}
