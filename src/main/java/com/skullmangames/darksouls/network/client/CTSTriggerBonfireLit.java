package com.skullmangames.darksouls.network.client;

import java.util.function.Supplier;

import com.skullmangames.darksouls.core.init.CriteriaTriggerInit;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class CTSTriggerBonfireLit
{
	public static CTSTriggerBonfireLit fromBytes(PacketBuffer buf)
	{
		return new CTSTriggerBonfireLit();
	}
	
	public static void toBytes(CTSTriggerBonfireLit msg, PacketBuffer buf) {}
	
	public static void handle(CTSTriggerBonfireLit msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ServerPlayerEntity serverplayer = ctx.get().getSender();
			CriteriaTriggerInit.BONFIRE_LIT.trigger(serverplayer, true);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
