package com.skullmangames.darksouls.network.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class STCStat
{
	private int entityId;
	private int stat;
	private int value;
	
	public STCStat(int entityid, int stat, int value)
	{
		this.entityId = entityid;
		this.stat = stat;
		this.value = value;
	}
	
	public static STCStat fromBytes(PacketBuffer buf)
	{
		return new STCStat(buf.readInt(), buf.readInt(), buf.readInt());
	}
	
	public static void toBytes(STCStat msg, PacketBuffer buf)
	{
		buf.writeInt(msg.entityId);
		buf.writeInt(msg.stat);
		buf.writeInt(msg.value);
	}
	
	public static void handle(STCStat msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			Minecraft minecraft = Minecraft.getInstance();
			Entity entity = minecraft.player.level.getEntity(msg.entityId);
			if (entity == null) return;
			
			PlayerCap<?> entityCap = (PlayerCap<?>) entity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			if (entityCap == null) return;
			
			entityCap.getStats().initStatValue(entityCap.getOriginalEntity(), msg.stat, msg.value);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
