package com.skullmangames.darksouls.network.client;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.entity.QuestEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

public class CTSFinishNPCChat
{
	private int entityId;
	private String location;
	
	public CTSFinishNPCChat(int entityid, String location)
	{
		this.entityId = entityid;
		this.location = location;
	}
	
	public static CTSFinishNPCChat fromBytes(FriendlyByteBuf buf)
	{
		return new CTSFinishNPCChat(buf.readInt(), buf.readUtf());
	}
	
	public static void toBytes(CTSFinishNPCChat msg, FriendlyByteBuf buf)
	{
		buf.writeInt(msg.entityId);
		buf.writeUtf(msg.location);
	}
	
	public static void handle(CTSFinishNPCChat msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ServerPlayer player = ctx.get().getSender();
			Entity entity = player.level.getEntity(msg.entityId);
			if (!(entity instanceof QuestEntity)) return;
			
			((QuestEntity)entity).onFinishChat(player, msg.location);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
