package com.skullmangames.darksouls.client.sound;

import com.skullmangames.darksouls.common.blockentity.BonfireBlockEntity;
import com.skullmangames.darksouls.core.init.ModSoundEvents;
import com.skullmangames.darksouls.network.ModNetworkManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BonfireAmbientSoundInstance extends TickableSound
{
	private static final float VOLUME_MIN = 0.0F;
	private static final float VOLUME_MAX = 0.5F;
	private final BonfireBlockEntity bonfire;

	public BonfireAmbientSoundInstance(BonfireBlockEntity bonfire)
	{
		super(ModSoundEvents.BONFIRE_AMBIENT.get(), SoundCategory.BLOCKS);
		this.bonfire = bonfire;
		this.x = bonfire.getBlockPos().getX();
		this.y = bonfire.getBlockPos().getY();
		this.z = bonfire.getBlockPos().getZ();
		this.looping = true;
		this.delay = 0;
		this.pitch = 1.0F;
	}

	@Override
	public void tick()
	{
		if (this.bonfire.isRemoved())
		{
			ModNetworkManager.connection.removeBonfireAmbientSound(this.bonfire.getBlockPos());
			this.stop();
			return;
		}
		if (!this.bonfire.isLit())
		{
			this.volume = 0.0F;
			return;
		}

		Minecraft minecraft = Minecraft.getInstance();
		ClientPlayerEntity player = minecraft.player;
		float f = (float)player.distanceToSqr(this.x, this.y, this.z);
		this.volume = MathHelper.lerp(MathHelper.clamp(1.0F - (f * 0.005F), 0.0F, 0.5F), VOLUME_MIN, VOLUME_MAX);
	}
}
