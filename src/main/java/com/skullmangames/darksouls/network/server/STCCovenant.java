package com.skullmangames.darksouls.network.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.common.entity.covenant.Covenant;
import com.skullmangames.darksouls.common.entity.covenant.Covenants;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.network.ModNetworkManager;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
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
		return new STCCovenant(buf.readInt(), Covenants.COVENANTS.get(buf.readInt()));
	}
	
	public static void toBytes(STCCovenant msg, FriendlyByteBuf buf)
	{
		buf.writeInt(msg.entityId);
		buf.writeInt(Covenants.COVENANTS.indexOf(msg.covenant));
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
			if (msg.covenant.is(Covenants.NONE))
			{
				ModNetworkManager.connection.setOverlayMessage(new TextComponent("Covenant left"));
			}
			else ModNetworkManager.connection.setOverlayMessage(new TextComponent("Covenant joined"));
		});
		
		ctx.get().setPacketHandled(true);
	}
}
