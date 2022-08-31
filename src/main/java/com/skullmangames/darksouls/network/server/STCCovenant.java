package com.skullmangames.darksouls.network.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.common.entity.Covenant;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

public class STCCovenant
{
	private int entityId;
	private Covenant covenant;
	
	public STCCovenant(int entityid, Covenant covenant)
	{
		this.entityId = entityid;
		this.covenant = covenant;
	}
	
	public static STCCovenant fromBytes(FriendlyByteBuf buf)
	{
		return new STCCovenant(buf.readInt(), buf.readEnum(Covenant.class));
	}
	
	public static void toBytes(STCCovenant msg, FriendlyByteBuf buf)
	{
		buf.writeInt(msg.entityId);
		buf.writeEnum(msg.covenant);
	}
	
	public static void handle(STCCovenant msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			Minecraft minecraft = Minecraft.getInstance();
			Entity entity = minecraft.player.level.getEntity(msg.entityId);
			if (entity == null) return;
			
			PlayerCap<?> playerCap = (PlayerCap<?>) entity.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
			if (playerCap == null) return;
			
			playerCap.setCovenant(msg.covenant);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
