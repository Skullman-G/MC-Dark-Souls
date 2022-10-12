package com.skullmangames.darksouls.network.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.block.BonfireBlock;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class STCBonfireKindleEffect
{
	private BlockPos blockPos;
	
	public STCBonfireKindleEffect(BlockPos blockPos)
	{
		this.blockPos = blockPos;
	}
	
	public static STCBonfireKindleEffect fromBytes(FriendlyByteBuf buf)
	{
		return new STCBonfireKindleEffect(buf.readBlockPos());
	}
	
	public static void toBytes(STCBonfireKindleEffect msg, FriendlyByteBuf buf)
	{
		buf.writeBlockPos(msg.blockPos);
	}
	
	public static void handle(STCBonfireKindleEffect msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			Minecraft minecraft = Minecraft.getInstance();
			BonfireBlock.kindleEffect(minecraft.level, msg.blockPos);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
