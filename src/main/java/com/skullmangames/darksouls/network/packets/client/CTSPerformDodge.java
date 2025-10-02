package com.skullmangames.darksouls.network.packets.client;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.ServerPlayerCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.network.packets.NetworkPacket;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class CTSPerformDodge implements NetworkPacket
{
	private DodgeType type;
	
	public CTSPerformDodge(DodgeType type)
	{
		this.type = type;
	}
	
	public CTSPerformDodge(FriendlyByteBuf buf)
	{
		this.type = buf.readEnum(DodgeType.class);
	}
	
	@Override
	public void encode(FriendlyByteBuf buf)
	{
		buf.writeEnum(this.type);
	}
	
	@Override
	public void handle(Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ServerPlayer serverPlayer = ctx.get().getSender();
			ServerPlayerCap playerCap = (ServerPlayerCap) serverPlayer.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
			if (playerCap == null) return;
			playerCap.performDodge(this.type);
		});
		
		ctx.get().setPacketHandled(true);
	}
	
	public enum DodgeType
	{
		JUMP_BACK, FORWARD, BACK, LEFT, RIGHT
	}
}
