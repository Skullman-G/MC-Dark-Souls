package com.skullmangames.darksouls.network.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.PlayerData;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class STCNotifyPlayerYawChanged
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
	
	public static STCNotifyPlayerYawChanged fromBytes(PacketBuffer buf)
	{
		return new STCNotifyPlayerYawChanged(buf.readInt(), buf.readFloat());
	}
	
	public static void toBytes(STCNotifyPlayerYawChanged msg, PacketBuffer buf)
	{
		buf.writeInt(msg.entityId);
		buf.writeFloat(msg.yaw);
	}
	
	public static void handle(STCNotifyPlayerYawChanged msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->{
			@SuppressWarnings("resource")
			Entity entity = Minecraft.getInstance().player.level.getEntity(msg.entityId);
			
			if(entity != null)
			{
				PlayerData<?> entitydata = (PlayerData<?>) entity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				
				if(entitydata != null)
				{
					entitydata.changeYaw(msg.yaw);
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
}