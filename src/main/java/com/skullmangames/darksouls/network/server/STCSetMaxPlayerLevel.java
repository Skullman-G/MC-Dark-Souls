package com.skullmangames.darksouls.network.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.client.ClientManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class STCSetMaxPlayerLevel
{
	private int maxLevel;
	
	public STCSetMaxPlayerLevel(int value)
	{
		this.maxLevel = value;
	}
	
	public static STCSetMaxPlayerLevel fromBytes(FriendlyByteBuf buf)
	{
		return new STCSetMaxPlayerLevel(buf.readInt());
	}
	
	public static void toBytes(STCSetMaxPlayerLevel msg, FriendlyByteBuf buf)
	{
		buf.writeInt(msg.maxLevel);
	}
	
	public static void handle(STCSetMaxPlayerLevel msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ClientManager.INSTANCE.maxPlayerLevel = msg.maxLevel;
		});
		
		ctx.get().setPacketHandled(true);
	}
}
