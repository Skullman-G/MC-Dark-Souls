package com.skullmangames.darksouls.network.client;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.ServerPlayerCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.network.PacketBuffer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;

public class CTSOpenAttunementScreen
{
	public static CTSOpenAttunementScreen fromBytes(PacketBuffer buf)
	{
		return new CTSOpenAttunementScreen();
	}

	public static void toBytes(CTSOpenAttunementScreen msg, PacketBuffer buf) {}

	public static void handle(CTSOpenAttunementScreen msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() ->
		{
			ServerPlayerEntity player = ctx.get().getSender();
			ServerPlayerCap playerCap = (ServerPlayerCap)player.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
			if (playerCap == null) return;
			playerCap.openAttunementMenu();
		});

		ctx.get().setPacketHandled(true);
	}
}
