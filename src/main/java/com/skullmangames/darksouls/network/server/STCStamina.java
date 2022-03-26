package com.skullmangames.darksouls.network.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class STCStamina
{
	private int entityId;
	private float stamina;
	
	public STCStamina(int entityid, float value)
	{
		this.entityId = entityid;
		this.stamina = value;
	}
	
	public static STCStamina fromBytes(FriendlyByteBuf buf)
	{
		return new STCStamina(buf.readInt(), buf.readFloat());
	}
	
	public static void toBytes(STCStamina msg, FriendlyByteBuf buf)
	{
		buf.writeInt(msg.entityId);
		buf.writeFloat(msg.stamina);
	}
	
	public static void handle(STCStamina msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			Minecraft minecraft = Minecraft.getInstance();
			Entity entity = minecraft.player.level.getEntity(msg.entityId);
			if (entity == null) return;
			
			PlayerCap<?> entitydata = (PlayerCap<?>) entity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			if (entitydata == null) return;
			
			entitydata.setStamina(msg.stamina);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
