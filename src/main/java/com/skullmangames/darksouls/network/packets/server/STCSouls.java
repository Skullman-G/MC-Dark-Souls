package com.skullmangames.darksouls.network.packets.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.network.packets.NetworkPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class STCSouls implements NetworkPacket
{
	private int entityId;
	private int souls;
	
	public STCSouls(int entityid, int value)
	{
		this.entityId = entityid;
		this.souls = value;
	}
	
	public STCSouls(FriendlyByteBuf buf)
	{
		this.entityId = buf.readInt();
		this.souls = buf.readInt();
	}
	
	@Override
	public void encode(FriendlyByteBuf buf)
	{
		buf.writeInt(this.entityId);
		buf.writeInt(this.souls);
	}
	
	@Override
	public void handle(Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			Minecraft minecraft = Minecraft.getInstance();
			Entity entity = minecraft.player.level.getEntity(this.entityId);
			if (entity == null) return;
			
			PlayerCap<?> entityCap = (PlayerCap<?>) entity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			if (entityCap == null) return;
			
			entityCap.setSouls(this.souls);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
