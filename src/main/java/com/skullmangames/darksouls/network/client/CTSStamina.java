package com.skullmangames.darksouls.network.client;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.ServerPlayerData;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class CTSStamina
{
	private float stamina;
	
	public CTSStamina(float value)
	{
		this.stamina = value;
	}
	
	public static CTSStamina fromBytes(PacketBuffer buf)
	{
		return new CTSStamina(buf.readFloat());
	}
	
	public static void toBytes(CTSStamina msg, PacketBuffer buf)
	{
		buf.writeFloat(msg.stamina);
	}
	
	public static void handle(CTSStamina msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ServerPlayerEntity serverPlayer = ctx.get().getSender();
			ServerPlayerData playerdata = (ServerPlayerData) serverPlayer.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			if (playerdata == null) return;
			
			playerdata.setStamina(msg.stamina);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
