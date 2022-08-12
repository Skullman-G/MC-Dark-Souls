package com.skullmangames.darksouls.network.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class STCHuman
{
	private int entityId;
	private boolean human;
	
	public STCHuman(int entityid, boolean value)
	{
		this.entityId = entityid;
		this.human = value;
	}
	
	public static STCHuman fromBytes(PacketBuffer buf)
	{
		return new STCHuman(buf.readInt(), buf.readBoolean());
	}
	
	public static void toBytes(STCHuman msg, PacketBuffer buf)
	{
		buf.writeInt(msg.entityId);
		buf.writeBoolean(msg.human);
	}
	
	public static void handle(STCHuman msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			Minecraft minecraft = Minecraft.getInstance();
			Entity entity = minecraft.player.level.getEntity(msg.entityId);
			if (entity == null) return;
			
			PlayerCap<?> entityCap = (PlayerCap<?>) entity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			if (entityCap == null) return;
			
			entityCap.setHuman(msg.human);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
