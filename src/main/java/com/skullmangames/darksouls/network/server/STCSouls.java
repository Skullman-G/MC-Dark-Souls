package com.skullmangames.darksouls.network.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class STCSouls
{
	private int entityId;
	private int souls;
	
	public STCSouls(int entityid, int value)
	{
		this.entityId = entityid;
		this.souls = value;
	}
	
	public static STCSouls fromBytes(PacketBuffer buf)
	{
		return new STCSouls(buf.readInt(), buf.readInt());
	}
	
	public static void toBytes(STCSouls msg, PacketBuffer buf)
	{
		buf.writeInt(msg.entityId);
		buf.writeInt(msg.souls);
	}
	
	public static void handle(STCSouls msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			Minecraft minecraft = Minecraft.getInstance();
			Entity entity = minecraft.player.level.getEntity(msg.entityId);
			if (entity == null) return;
			
			PlayerCap<?> entityCap = (PlayerCap<?>) entity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			if (entityCap == null) return;
			
			entityCap.setSouls(msg.souls);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
