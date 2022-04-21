package com.skullmangames.darksouls.network.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.client.sound.BonfireAmbientSoundInstance;
import com.skullmangames.darksouls.common.blockentity.BonfireBlockEntity;
import com.skullmangames.darksouls.core.init.ModBlockEntities;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class STCPlayBonfireAmbientSound
{
	private BlockPos pos;
	
	public STCPlayBonfireAmbientSound(BlockPos pos)
	{
		this.pos = pos;
	}
	
	public static STCPlayBonfireAmbientSound fromBytes(FriendlyByteBuf buf)
	{
		return new STCPlayBonfireAmbientSound(buf.readBlockPos());
	}
	
	public static void toBytes(STCPlayBonfireAmbientSound msg, FriendlyByteBuf buf)
	{
		buf.writeBlockPos(msg.pos);
	}
	
	public static void handle(STCPlayBonfireAmbientSound msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			if (ctx.get().getSender() != null) return;
			Minecraft minecraft = Minecraft.getInstance();
			BonfireBlockEntity bonfire = minecraft.player.level.getBlockEntity(msg.pos, ModBlockEntities.BONFIRE.get()).orElse(null);
			if (bonfire == null) return;
			BonfireAmbientSoundInstance soundInstance = new BonfireAmbientSoundInstance(bonfire);
			minecraft.getSoundManager().queueTickingSound(soundInstance);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
