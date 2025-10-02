package com.skullmangames.darksouls.network.packets.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.network.packets.NetworkPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

public class STCNPCChat implements NetworkPacket
{
	private int entityId;
	private String location;
	
	public STCNPCChat(int entityid, String location)
	{
		this.entityId = entityid;
		this.location = location;
	}
	
	public STCNPCChat(FriendlyByteBuf buf)
	{
		this.entityId = buf.readInt();
		this.location = buf.readUtf();
	}
	
	@Override
	public void encode(FriendlyByteBuf buf)
	{
		buf.writeInt(this.entityId);
		buf.writeUtf(this.location);
	}
	
	@Override
	public void handle(Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			Minecraft minecraft = Minecraft.getInstance();
			Entity entity = minecraft.level.getEntity(this.entityId);
			
			ClientManager.INSTANCE.npcChat.start(entity, this.location);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
