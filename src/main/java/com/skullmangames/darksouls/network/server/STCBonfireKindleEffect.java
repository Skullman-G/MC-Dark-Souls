package com.skullmangames.darksouls.network.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.network.ModNetworkManager;

import net.minecraft.util.math.BlockPos;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class STCBonfireKindleEffect
{
	private BlockPos blockPos;
	
	public STCBonfireKindleEffect(BlockPos blockPos)
	{
		this.blockPos = blockPos;
	}
	
	public static STCBonfireKindleEffect fromBytes(PacketBuffer buf)
	{
		return new STCBonfireKindleEffect(buf.readBlockPos());
	}
	
	public static void toBytes(STCBonfireKindleEffect msg, PacketBuffer buf)
	{
		buf.writeBlockPos(msg.blockPos);
	}
	
	public static void handle(STCBonfireKindleEffect msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ModNetworkManager.connection.bonfireKindleEffect(msg.blockPos);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
