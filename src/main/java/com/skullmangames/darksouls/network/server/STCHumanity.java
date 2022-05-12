package com.skullmangames.darksouls.network.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class STCHumanity
{
	private int entityId;
	private int humanity;
	
	public STCHumanity(int entityid, int value)
	{
		this.entityId = entityid;
		this.humanity = value;
	}
	
	public static STCHumanity fromBytes(FriendlyByteBuf buf)
	{
		return new STCHumanity(buf.readInt(), buf.readInt());
	}
	
	public static void toBytes(STCHumanity msg, FriendlyByteBuf buf)
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
			
			PlayerCap<?> entityCap = (PlayerCap<?>) entity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			if (entityCap == null) return;
			
			entityCap.setHumanity(msg.humanity);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
