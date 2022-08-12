package com.skullmangames.darksouls.network.client;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.ServerPlayerCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import net.minecraft.network.PacketBuffer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;

public class CTSPerformDodge
{
	private boolean moving;
	
	public CTSPerformDodge(boolean moving)
	{
		this.moving = moving;
	}
	
	public static CTSPerformDodge fromBytes(PacketBuffer buf)
	{
		return new CTSPerformDodge(buf.readBoolean());
	}
	
	public static void toBytes(CTSPerformDodge msg, PacketBuffer buf)
	{
		buf.writeBoolean(msg.moving);
	}
	
	public static void handle(CTSPerformDodge msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ServerPlayerEntity serverPlayer = ctx.get().getSender();
			ServerPlayerCap playerdata = (ServerPlayerCap) serverPlayer.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			if (playerdata == null) return;
			playerdata.performDodge(msg.moving);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
