package com.skullmangames.darksouls.network.client;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.ServerPlayerData;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class CTSStamina
{
	private float stamina;
	
	public CTSStamina(float value)
	{
		this.stamina = value;
	}
	
	public static CTSStamina fromBytes(FriendlyByteBuf buf)
	{
		return new CTSStamina(buf.readFloat());
	}
	
	public static void toBytes(CTSStamina msg, FriendlyByteBuf buf)
	{
		buf.writeFloat(msg.stamina);
	}
	
	public static void handle(CTSStamina msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ServerPlayer serverPlayer = ctx.get().getSender();
			ServerPlayerData playerdata = (ServerPlayerData) serverPlayer.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			if (playerdata == null) return;
			
			playerdata.setStamina(msg.stamina);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
