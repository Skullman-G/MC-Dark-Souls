package com.skullmangames.darksouls.network.packets.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.network.packets.NetworkPacket;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class STCSetMaxPlayerLevel implements NetworkPacket
{
	private int maxLevel;
	
	public STCSetMaxPlayerLevel(int value)
	{
		this.maxLevel = value;
	}
	
	public STCSetMaxPlayerLevel(FriendlyByteBuf buf)
	{
		this.maxLevel = buf.readInt();
	}
	
	@Override
	public void encode(FriendlyByteBuf buf)
	{
		buf.writeInt(this.maxLevel);
	}
	
	@Override
	public void handle(Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ClientManager.INSTANCE.maxPlayerLevel = this.maxLevel;
		});
		
		ctx.get().setPacketHandled(true);
	}
}
