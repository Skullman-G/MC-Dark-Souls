package com.skullmangames.darksouls.network.client;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.ServerPlayerData;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class CTSHuman
{
	private boolean human;
	
	public CTSHuman(boolean value)
	{
		this.human = value;
	}
	
	public static CTSHuman fromBytes(PacketBuffer buf)
	{
		return new CTSHuman(buf.readBoolean());
	}
	
	public static void toBytes(CTSHuman msg, PacketBuffer buf)
	{
		buf.writeBoolean(msg.human);
	}
	
	public static void handle(CTSHuman msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ServerPlayerEntity serverPlayer = ctx.get().getSender();
			ServerPlayerData playerdata = (ServerPlayerData) serverPlayer.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			if (playerdata == null) return;
			
			playerdata.setHuman(msg.human);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
