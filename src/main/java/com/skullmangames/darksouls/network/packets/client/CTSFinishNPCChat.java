package com.skullmangames.darksouls.network.packets.client;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.entity.QuestEntity;
import com.skullmangames.darksouls.network.packets.NetworkPacket;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

public class CTSFinishNPCChat implements NetworkPacket
{
	private int entityId;
	private String location;
	
	public CTSFinishNPCChat(int entityid, String location)
	{
		this.entityId = entityid;
		this.location = location;
	}
	
	public CTSFinishNPCChat(FriendlyByteBuf buf)
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
			ServerPlayer player = ctx.get().getSender();
			Entity entity = player.level.getEntity(this.entityId);
			if (!(entity instanceof QuestEntity)) return;
			
			((QuestEntity)entity).onFinishChat(player, this.location);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
