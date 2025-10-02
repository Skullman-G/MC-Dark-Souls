package com.skullmangames.darksouls.network.packets.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.packets.NetworkPacket;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class STCPlayBonfireAmbientSound implements NetworkPacket
{
	private BlockPos pos;
	
	public STCPlayBonfireAmbientSound(BlockPos pos)
	{
		this.pos = pos;
	}
	
	public STCPlayBonfireAmbientSound(FriendlyByteBuf buf)
	{
		this.pos = buf.readBlockPos();
	}
	
	@Override
	public void encode(FriendlyByteBuf buf)
	{
		buf.writeBlockPos(this.pos);
	}
	
	@Override
	public void handle(Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			if (ctx.get().getSender() != null) return;
			ModNetworkManager.connection.tryPlayBonfireAmbientSound(this.pos);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
