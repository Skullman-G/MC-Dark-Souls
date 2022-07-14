package com.skullmangames.darksouls.network.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.client.ClientManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.network.NetworkEvent;

public class STCNPCChat
{
	private int entityId;
	private String location;
	
	public STCNPCChat(int entityid, String location)
	{
		this.entityId = entityid;
		this.location = location;
	}
	
	public static STCNPCChat fromBytes(PacketBuffer buf)
	{
		return new STCNPCChat(buf.readInt(), buf.readUtf());
	}
	
	public static void toBytes(STCNPCChat msg, PacketBuffer buf)
	{
		buf.writeInt(msg.entityId);
		buf.writeUtf(msg.location);
	}
	
	public static void handle(STCNPCChat msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			Minecraft minecraft = Minecraft.getInstance();
			Entity entity = minecraft.level.getEntity(msg.entityId);
			
			ClientManager.INSTANCE.npcChat.start(entity, msg.location);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
