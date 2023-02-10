package com.skullmangames.darksouls.network.client;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.common.entity.Covenant;
import com.skullmangames.darksouls.common.entity.Covenants;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import net.minecraft.network.PacketBuffer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;

public class CTSCovenant
{
	private Covenant covenant;
	
	public CTSCovenant(Covenant covenant)
	{
		this.covenant = covenant;
	}
	
	public static CTSCovenant fromBytes(PacketBuffer buf)
	{
		return new CTSCovenant(Covenants.COVENANTS.get(buf.readInt()));
	}
	
	public static void toBytes(CTSCovenant msg, PacketBuffer buf)
	{
		buf.writeInt(Covenants.COVENANTS.indexOf(msg.covenant));
	}
	
	public static void handle(CTSCovenant msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ServerPlayerEntity player = ctx.get().getSender();
			
			PlayerCap<?> playerCap = (PlayerCap<?>) player.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
			if (playerCap == null) return;
			
			playerCap.setCovenant(msg.covenant);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
