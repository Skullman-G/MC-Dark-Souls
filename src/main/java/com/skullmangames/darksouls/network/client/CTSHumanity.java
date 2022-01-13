package com.skullmangames.darksouls.network.client;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.ServerPlayerData;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class CTSHumanity
{
	private int humanity;
	
	public CTSHumanity(int value)
	{
		this.humanity = value;
	}
	
	public static CTSHumanity fromBytes(FriendlyByteBuf buf)
	{
		return new CTSHumanity(buf.readInt());
	}
	
	public static void toBytes(CTSHumanity msg, FriendlyByteBuf buf)
	{
		buf.writeInt(msg.humanity);
	}
	
	public static void handle(CTSHumanity msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ServerPlayer serverPlayer = ctx.get().getSender();
			ServerPlayerData playerdata = (ServerPlayerData) serverPlayer.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			if (playerdata == null) return;
			
			playerdata.setHumanity(msg.humanity);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
