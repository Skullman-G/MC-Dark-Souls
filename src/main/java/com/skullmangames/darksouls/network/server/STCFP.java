package com.skullmangames.darksouls.network.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

public class STCFP
{
	private int entityId;
	private float fp;
	
	public STCFP(int entityid, float value)
	{
		this.entityId = entityid;
		this.fp = value;
	}
	
	public static STCFP fromBytes(FriendlyByteBuf buf)
	{
		return new STCFP(buf.readInt(), buf.readFloat());
	}
	
	public static void toBytes(STCFP msg, FriendlyByteBuf buf)
	{
		buf.writeInt(msg.entityId);
		buf.writeFloat(msg.fp);
	}
	
	public static void handle(STCFP msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			Minecraft minecraft = Minecraft.getInstance();
			Entity entity = minecraft.player.level.getEntity(msg.entityId);
			if (entity == null) return;
			
			PlayerCap<?> entityCap = (PlayerCap<?>) entity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			if (entityCap == null) return;
			
			entityCap.setFP(msg.fp);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
