package com.skullmangames.darksouls.network.server.gui;

import java.util.function.Supplier;

import com.skullmangames.darksouls.network.ModNetworkManager;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class STCOpenBonfireNameScreen
{
	private BlockPos blockPos;
	
	public STCOpenBonfireNameScreen(BlockPos pos)
	{
		this.blockPos = pos;
	}
	
	public static STCOpenBonfireNameScreen fromBytes(FriendlyByteBuf buf)
	{
		return new STCOpenBonfireNameScreen(buf.readBlockPos());
	}
	
	public static void toBytes(STCOpenBonfireNameScreen msg, FriendlyByteBuf buf)
	{
		buf.writeBlockPos(msg.blockPos);
	}
	
	public static void handle(STCOpenBonfireNameScreen msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ModNetworkManager.connection.openBonfireNameScreen(msg.blockPos);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
