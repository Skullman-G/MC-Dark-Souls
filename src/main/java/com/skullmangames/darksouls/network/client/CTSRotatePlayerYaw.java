package com.skullmangames.darksouls.network.client;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCNotifyPlayerYawChanged;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class CTSRotatePlayerYaw
{
	private float yaw;
	
	public CTSRotatePlayerYaw()
	{
		this.yaw = 0F;
	}
	
	public CTSRotatePlayerYaw(float yaw)
	{
		this.yaw = yaw;
	}
	
	public static CTSRotatePlayerYaw fromBytes(FriendlyByteBuf buf)
	{
		return new CTSRotatePlayerYaw(buf.readFloat());
	}

	public static void toBytes(CTSRotatePlayerYaw msg, FriendlyByteBuf buf)
	{
		buf.writeFloat(msg.yaw);
	}
	
	public static void handle(CTSRotatePlayerYaw msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->{
			ServerPlayer player = ctx.get().getSender();
			
			if(player != null)
			{
				PlayerCap<?> entityCap = (PlayerCap<?>) player.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				
				if(entityCap != null)
				{
					ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCNotifyPlayerYawChanged(player.getId(), msg.yaw), player);
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
}