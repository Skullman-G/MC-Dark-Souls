package com.skullmangames.darksouls.network.server.gui;

import java.util.function.Supplier;

import com.skullmangames.darksouls.network.ModNetworkManager;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class STCOpenFireKeeperScreen
{
	private int entityId;
	
	public STCOpenFireKeeperScreen(int entityid)
	{
		this.entityId = entityid;
	}
	
	public static STCOpenFireKeeperScreen fromBytes(FriendlyByteBuf buf)
	{
		return new STCOpenFireKeeperScreen(buf.readInt());
	}
	
	public static void toBytes(STCOpenFireKeeperScreen msg, FriendlyByteBuf buf)
	{
		buf.writeInt(msg.entityId);
	}
	
	public static void handle(STCOpenFireKeeperScreen msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ModNetworkManager.connection.openFireKeeperScreen(msg.entityId);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
