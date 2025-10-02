package com.skullmangames.darksouls.network.packets.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.packets.NetworkPacket;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class STCBonfireKindleEffect implements NetworkPacket
{
	private BlockPos blockPos;
	
	public STCBonfireKindleEffect(BlockPos blockPos)
	{
		this.blockPos = blockPos;
	}
	
	public STCBonfireKindleEffect(FriendlyByteBuf buf)
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
			ModNetworkManager.connection.bonfireKindleEffect(this.blockPos);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
