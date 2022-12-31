package com.skullmangames.darksouls.network.client;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.ServerPlayerCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class CTSOpenAttunementScreen
{
	public static CTSOpenAttunementScreen fromBytes(FriendlyByteBuf buf)
	{
		return new CTSOpenAttunementScreen();
	}

	public static void toBytes(CTSOpenAttunementScreen msg, FriendlyByteBuf buf) {}

	public static void handle(CTSOpenAttunementScreen msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() ->
		{
			ServerPlayer player = ctx.get().getSender();
			ServerPlayerCap playerCap = (ServerPlayerCap)player.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
			if (playerCap == null) return;
			playerCap.openAttunementMenu();
		});

		ctx.get().setPacketHandled(true);
	}
}
