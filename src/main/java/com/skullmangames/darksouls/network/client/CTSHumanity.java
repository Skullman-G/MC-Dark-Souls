package com.skullmangames.darksouls.network.client;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.ServerPlayerData;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class CTSHumanity
{
	private int humanity;
	
	public CTSHumanity(int value)
	{
		this.humanity = value;
	}
	
	public static CTSHumanity fromBytes(PacketBuffer buf)
	{
		return new CTSHumanity(buf.readInt());
	}
	
	public static void toBytes(CTSHumanity msg, PacketBuffer buf)
	{
		buf.writeInt(msg.humanity);
	}
	
	public static void handle(CTSHumanity msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ServerPlayerEntity serverPlayer = ctx.get().getSender();
			ServerPlayerData playerdata = (ServerPlayerData) serverPlayer.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			if (playerdata == null) return;
			
			playerdata.setHumanity(msg.humanity);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
