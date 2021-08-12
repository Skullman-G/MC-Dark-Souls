package com.skullmangames.darksouls.network.server;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class STCResetBasicAttackCool
{
	public static STCResetBasicAttackCool fromBytes(PacketBuffer buf)
	{
		STCResetBasicAttackCool msg = new STCResetBasicAttackCool();
		return msg;
	}

	public static void toBytes(STCResetBasicAttackCool msg, PacketBuffer buf)
	{

	}

	public static void handle(STCResetBasicAttackCool msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() ->
		{
			@SuppressWarnings("resource")
			ClientPlayerEntity clientPlayer = Minecraft.getInstance().player;
			clientPlayer.setNoActionTime(10000);
		});
		ctx.get().setPacketHandled(true);
	}
}