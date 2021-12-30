package com.skullmangames.darksouls.network.client;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.ServerPlayerData;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class CTSSouls
{
	private int souls;
	
	public CTSSouls(int value)
	{
		this.souls = value;
	}
	
	public static CTSSouls fromBytes(PacketBuffer buf)
	{
		return new CTSSouls(buf.readInt());
	}
	
	public static void toBytes(CTSSouls msg, PacketBuffer buf)
	{
		buf.writeInt(msg.souls);
	}
	
	public static void handle(CTSSouls msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ServerPlayerEntity serverPlayer = ctx.get().getSender();
			ServerPlayerData playerdata = (ServerPlayerData) serverPlayer.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			if (playerdata == null) return;
			
			playerdata.setSouls(msg.souls);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
