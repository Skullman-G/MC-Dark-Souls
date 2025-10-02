package com.skullmangames.darksouls.network.packets.server.gui;

import java.util.function.Supplier;

import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.packets.NetworkPacket;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class STCOpenFireKeeperScreen implements NetworkPacket
{
	private int entityId;
	
	public STCOpenFireKeeperScreen(int entityid)
	{
		this.entityId = entityid;
	}
	
	public STCOpenFireKeeperScreen(FriendlyByteBuf buf)
	{
		this.entityId = buf.readInt();
	}
	
	@Override
	public void encode(FriendlyByteBuf buf)
	{
		buf.writeInt(this.entityId);
	}
	
	@Override
	public void handle(Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ModNetworkManager.connection.openFireKeeperScreen(this.entityId);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
