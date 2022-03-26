package com.skullmangames.darksouls.network.client;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.ServerPlayerCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class CTSPerformDodge
{
	public static CTSPerformDodge fromBytes(FriendlyByteBuf buf)
	{
		return new CTSPerformDodge();
	}
	
	public static void toBytes(CTSPerformDodge msg, FriendlyByteBuf buf) {}
	
	public static void handle(CTSPerformDodge msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ServerPlayer serverPlayer = ctx.get().getSender();
			ServerPlayerCap playerdata = (ServerPlayerCap) serverPlayer.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			if (playerdata == null) return;
			playerdata.performDodge();
		});
		
		ctx.get().setPacketHandled(true);
	}
}
