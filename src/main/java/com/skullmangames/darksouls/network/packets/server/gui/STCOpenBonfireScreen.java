package com.skullmangames.darksouls.network.packets.server.gui;

import java.util.function.Supplier;

import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.packets.NetworkPacket;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class STCOpenBonfireScreen implements NetworkPacket
{
	private BlockPos blockPos;
	
	public STCOpenBonfireScreen(BlockPos pos)
	{
		this.blockPos = pos;
	}
	
	public STCOpenBonfireScreen(FriendlyByteBuf buf)
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
			ModNetworkManager.connection.openBonfireScreen(this.blockPos);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
