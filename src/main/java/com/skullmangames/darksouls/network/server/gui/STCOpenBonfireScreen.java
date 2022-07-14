package com.skullmangames.darksouls.network.server.gui;

import java.util.function.Supplier;

import com.skullmangames.darksouls.network.ModNetworkManager;

import net.minecraft.util.math.BlockPos;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class STCOpenBonfireScreen
{
	private BlockPos blockPos;
	
	public STCOpenBonfireScreen(BlockPos pos)
	{
		this.blockPos = pos;
	}
	
	public static STCOpenBonfireScreen fromBytes(PacketBuffer buf)
	{
		return new STCOpenBonfireScreen(buf.readBlockPos());
	}
	
	public static void toBytes(STCOpenBonfireScreen msg, PacketBuffer buf)
	{
		buf.writeBlockPos(msg.blockPos);
	}
	
	public static void handle(STCOpenBonfireScreen msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ModNetworkManager.connection.openBonfireScreen(msg.blockPos);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
