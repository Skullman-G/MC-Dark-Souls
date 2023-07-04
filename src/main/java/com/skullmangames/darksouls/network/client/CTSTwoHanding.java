package com.skullmangames.darksouls.network.client;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class CTSTwoHanding
{
	private boolean twoHanding;
	
	public CTSTwoHanding(boolean value)
	{
		this.twoHanding = value;
	}
	
	public static CTSTwoHanding fromBytes(FriendlyByteBuf buf)
	{
		return new CTSTwoHanding(buf.readBoolean());
	}
	
	public static void toBytes(CTSTwoHanding msg, FriendlyByteBuf buf)
	{
		buf.writeBoolean(msg.twoHanding);
	}
	
	public static void handle(CTSTwoHanding msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ServerPlayer player = ctx.get().getSender();
			
			player.getCapability(ModCapabilities.CAPABILITY_ENTITY).ifPresent((cap) ->
			{
				if (cap instanceof PlayerCap<?> playerCap)
				{
					playerCap.setTwoHanding(msg.twoHanding);
				}
			});
		});
		
		ctx.get().setPacketHandled(true);
	}
}
