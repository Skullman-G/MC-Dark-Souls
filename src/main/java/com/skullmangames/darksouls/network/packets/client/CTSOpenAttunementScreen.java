package com.skullmangames.darksouls.network.packets.client;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.ServerPlayerCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.network.packets.NetworkPacket;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class CTSOpenAttunementScreen implements NetworkPacket
{
	public CTSOpenAttunementScreen() {}
	
	public CTSOpenAttunementScreen(FriendlyByteBuf buf) {}

	@Override
	public void encode(FriendlyByteBuf buf) {}

	@Override
	public void handle(Supplier<NetworkEvent.Context> ctx)
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
