package com.skullmangames.darksouls.network.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

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
	
	public static STCNotifyPlayerYawChanged fromBytes(FriendlyByteBuf buf)
	{
		return new STCNotifyPlayerYawChanged(buf.readInt(), buf.readFloat());
	}
	
	public static void toBytes(STCNotifyPlayerYawChanged msg, FriendlyByteBuf buf)
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
				PlayerCap<?> entitydata = (PlayerCap<?>) entity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				
				if(entitydata != null)
				{
					entitydata.changeYaw(msg.yaw);
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
}