package com.skullmangames.darksouls.network.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.network.NetworkEvent;

public class STCCovenantProgress
{
	private int entityId;
	private int progress;
	
	public STCCovenantProgress(int entityid, int value)
	{
		this.entityId = entityid;
		this.progress = value;
	}
	
	public static STCCovenantProgress fromBytes(PacketBuffer buf)
	{
		return new STCCovenantProgress(buf.readInt(), buf.readInt());
	}
	
	public static void toBytes(STCCovenantProgress msg, PacketBuffer buf)
	{
		buf.writeInt(msg.entityId);
		buf.writeInt(msg.progress);
	}
	
	public static void handle(STCCovenantProgress msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			Minecraft minecraft = Minecraft.getInstance();
			Entity entity = minecraft.player.level.getEntity(msg.entityId);
			if (entity == null) return;
			
			PlayerCap<?> entityCap = (PlayerCap<?>) entity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			if (entityCap == null) return;
			
			entityCap.setCovenantProgress(msg.progress);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
