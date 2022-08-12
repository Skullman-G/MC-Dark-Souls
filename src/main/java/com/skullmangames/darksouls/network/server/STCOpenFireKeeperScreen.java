package com.skullmangames.darksouls.network.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.client.gui.screens.FireKeeperScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class STCOpenFireKeeperScreen
{
	private int entityId;
	
	public STCOpenFireKeeperScreen(int entityid)
	{
		this.entityId = entityid;
	}
	
	public static STCOpenFireKeeperScreen fromBytes(PacketBuffer buf)
	{
		return new STCOpenFireKeeperScreen(buf.readInt());
	}
	
	public static void toBytes(STCOpenFireKeeperScreen msg, PacketBuffer buf)
	{
		buf.writeInt(msg.entityId);
	}
	
	public static void handle(STCOpenFireKeeperScreen msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			Minecraft minecraft = Minecraft.getInstance();
			minecraft.setScreen(new FireKeeperScreen(msg.entityId));
		});
		
		ctx.get().setPacketHandled(true);
	}
}
