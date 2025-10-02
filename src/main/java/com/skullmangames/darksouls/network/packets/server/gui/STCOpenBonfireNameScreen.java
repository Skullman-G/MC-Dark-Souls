package com.skullmangames.darksouls.network.packets.server.gui;

import java.util.function.Supplier;

import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.packets.NetworkPacket;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class STCOpenBonfireNameScreen implements NetworkPacket
{
	private BlockPos blockPos;
	
	public STCOpenBonfireNameScreen(BlockPos pos)
	{
		this.blockPos = pos;
	}
	
	public STCOpenBonfireNameScreen(FriendlyByteBuf buf)
	{
		this.blockPos = buf.readBlockPos();
	}
	
	@Override
	public void encode(FriendlyByteBuf buf)
	{
		buf.writeBlockPos(this.blockPos);
	}
	
	@Override
	public void handle(Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ModNetworkManager.connection.openBonfireNameScreen(this.blockPos);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
