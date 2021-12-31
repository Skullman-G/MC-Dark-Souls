package com.skullmangames.darksouls.network.client;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.ServerPlayerData;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class CTSStat
{
	private String name;
	private int value;
	
	public CTSStat(String name, int value)
	{
		this.name = name;
		this.value = value;
	}
	
	public static CTSStat fromBytes(PacketBuffer buf)
	{
		return new CTSStat(buf.readUtf(), buf.readInt());
	}
	
	public static void toBytes(CTSStat msg, PacketBuffer buf)
	{
		buf.writeUtf(msg.name);
		buf.writeInt(msg.value);
	}
	
	public static void handle(CTSStat msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ServerPlayerEntity serverPlayer = ctx.get().getSender();
			ServerPlayerData playerdata = (ServerPlayerData) serverPlayer.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			if (playerdata == null) return;
			
			playerdata.setStatValue(msg.name, msg.value);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
