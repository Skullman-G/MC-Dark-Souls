package com.skullmangames.darksouls.network.packets;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public interface NetworkPacket
{
	void encode(FriendlyByteBuf buf);
	
	void handle(Supplier<NetworkEvent.Context> ctx);
}
