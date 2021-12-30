package com.skullmangames.darksouls.network.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.PlayerData;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class STCHumanity
{
	private int entityId;
	private int humanity;
	
	public STCHumanity(int entityid, int value)
	{
		this.entityId = entityid;
		this.humanity = value;
	}
	
	public static STCHumanity fromBytes(PacketBuffer buf)
	{
		return new STCHumanity(buf.readInt(), buf.readInt());
	}
	
	public static void toBytes(STCHumanity msg, PacketBuffer buf)
	{
		buf.writeInt(msg.entityId);
		buf.writeInt(msg.humanity);
	}
	
	public static void handle(STCHumanity msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			Minecraft minecraft = Minecraft.getInstance();
			Entity entity = minecraft.player.level.getEntity(msg.entityId);
			if (entity == null) return;
			
			PlayerData<?> entitydata = (PlayerData<?>) entity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			if (entitydata == null) return;
			
			entitydata.setHumanity(msg.humanity);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
