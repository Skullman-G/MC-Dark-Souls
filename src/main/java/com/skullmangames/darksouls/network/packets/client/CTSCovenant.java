package com.skullmangames.darksouls.network.packets.client;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.common.entity.covenant.Covenant;
import com.skullmangames.darksouls.common.entity.covenant.Covenants;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.network.packets.NetworkPacket;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class CTSCovenant implements NetworkPacket
{
	private Covenant covenant;
	
	public CTSCovenant(Covenant covenant)
	{
		this.covenant = covenant;
	}
	
	public CTSCovenant(FriendlyByteBuf buf)
	{
		this.covenant = Covenants.COVENANTS.get(buf.readInt());
	}
	
	@Override
	public void encode(FriendlyByteBuf buf)
	{
		buf.writeInt(Covenants.COVENANTS.indexOf(this.covenant));
	}
	
	@Override
	public void handle(Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ServerPlayer player = ctx.get().getSender();
			
			PlayerCap<?> playerCap = (PlayerCap<?>) player.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
			if (playerCap == null) return;
			
			playerCap.setCovenant(this.covenant);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
