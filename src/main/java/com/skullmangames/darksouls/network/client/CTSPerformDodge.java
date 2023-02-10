package com.skullmangames.darksouls.network.client;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.ServerPlayerCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import net.minecraft.network.PacketBuffer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;

public class CTSPerformDodge
{
	private DodgeType type;
	
	public CTSPerformDodge(DodgeType type)
	{
		this.type = type;
	}
	
	public static CTSPerformDodge fromBytes(PacketBuffer buf)
	{
		return new CTSPerformDodge(buf.readEnum(DodgeType.class));
	}
	
	public static void toBytes(CTSPerformDodge msg, PacketBuffer buf)
	{
		buf.writeEnum(msg.type);
	}
	
	public static void handle(CTSPerformDodge msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ServerPlayerEntity serverPlayer = ctx.get().getSender();
			ServerPlayerCap playerCap = (ServerPlayerCap) serverPlayer.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			if (playerCap == null) return;
			playerCap.performDodge(msg.type);
		});
		
		ctx.get().setPacketHandled(true);
	}
	
	public enum DodgeType
	{
		JUMP_BACK, FORWARD, BACK, LEFT, RIGHT
	}
}
