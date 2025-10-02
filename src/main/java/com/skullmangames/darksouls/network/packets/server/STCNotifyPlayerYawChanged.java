package com.skullmangames.darksouls.network.packets.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.network.packets.NetworkPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class STCNotifyPlayerYawChanged implements NetworkPacket
{
	private int entityId;
	private float yaw;
	
	public STCNotifyPlayerYawChanged()
	{
		this.entityId = 0;
		this.yaw = 0;
	}
	
	public STCNotifyPlayerYawChanged(int entityId, float yaw)
	{
		this.entityId = entityId;
		this.yaw = yaw;
	}
	
	public STCNotifyPlayerYawChanged(FriendlyByteBuf buf)
	{
		this.entityId = buf.readInt();
		this.yaw = buf.readFloat();
	}
	
	@Override
	public void encode(FriendlyByteBuf buf)
	{
		buf.writeInt(this.entityId);
		buf.writeFloat(this.yaw);
	}
	
	@Override
	public void handle(Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->{
			Entity entity = Minecraft.getInstance().player.level.getEntity(this.entityId);
			
			if(entity != null)
			{
				PlayerCap<?> entityCap = (PlayerCap<?>) entity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				
				if(entityCap != null)
				{
					entityCap.changeYaw(this.yaw);
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
}