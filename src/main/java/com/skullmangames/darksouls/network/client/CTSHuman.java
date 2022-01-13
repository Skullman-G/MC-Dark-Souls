package com.skullmangames.darksouls.network.client;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.ServerPlayerData;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class CTSHuman
{
	private boolean human;
	
	public CTSHuman(boolean value)
	{
		this.human = value;
	}
	
	public static CTSHuman fromBytes(FriendlyByteBuf buf)
	{
		return new CTSHuman(buf.readBoolean());
	}
	
	public static void toBytes(CTSHuman msg, FriendlyByteBuf buf)
	{
		buf.writeBoolean(msg.human);
	}
	
	public static void handle(CTSHuman msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ServerPlayer serverPlayer = ctx.get().getSender();
			ServerPlayerData playerdata = (ServerPlayerData) serverPlayer.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			if (playerdata == null) return;
			
			playerdata.setHuman(msg.human);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
