package com.skullmangames.darksouls.network.packets.client;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.network.packets.NetworkPacket;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class CTSTwoHanding implements NetworkPacket
{
	private boolean twoHanding;
	
	public CTSTwoHanding(boolean value)
	{
		this.twoHanding = value;
	}
	
	public CTSTwoHanding(FriendlyByteBuf buf)
	{
		this.twoHanding = buf.readBoolean();
	}
	
	@Override
	public void encode(FriendlyByteBuf buf)
	{
		buf.writeBoolean(this.twoHanding);
	}
	
	@Override
	public void handle(Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ServerPlayer player = ctx.get().getSender();
			
			player.getCapability(ModCapabilities.CAPABILITY_ENTITY).ifPresent((cap) ->
			{
				if (cap instanceof PlayerCap<?> playerCap)
				{
					playerCap.setTwoHanding(this.twoHanding);
				}
			});
		});
		
		ctx.get().setPacketHandled(true);
	}
}
