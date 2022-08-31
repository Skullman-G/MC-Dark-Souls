package com.skullmangames.darksouls.network.client;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.common.entity.Covenant;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class CTSCovenant
{
	private Covenant covenant;
	
	public CTSCovenant(Covenant covenant)
	{
		this.covenant = covenant;
	}
	
	public static CTSCovenant fromBytes(FriendlyByteBuf buf)
	{
		return new CTSCovenant(buf.readEnum(Covenant.class));
	}
	
	public static void toBytes(CTSCovenant msg, FriendlyByteBuf buf)
	{
		buf.writeEnum(msg.covenant);
	}
	
	public static void handle(CTSCovenant msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ServerPlayer player = ctx.get().getSender();
			
			PlayerCap<?> playerCap = (PlayerCap<?>) player.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
			if (playerCap == null) return;
			
			playerCap.setCovenant(msg.covenant);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
