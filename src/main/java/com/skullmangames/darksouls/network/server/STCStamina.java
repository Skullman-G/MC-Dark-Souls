package com.skullmangames.darksouls.network.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class STCStamina
{
	private int entityId;
	private float stamina;
	
	public STCStamina(int entityid, float value)
	{
		this.entityId = entityid;
		this.stamina = value;
	}
	
	public static STCStamina fromBytes(PacketBuffer buf)
	{
		return new STCStamina(buf.readInt(), buf.readFloat());
	}
	
	public static void toBytes(STCStamina msg, PacketBuffer buf)
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
			
			PlayerCap<?> entityCap = (PlayerCap<?>) entity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			if (entityCap == null) return;
			
			entityCap.setStamina(msg.stamina);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
