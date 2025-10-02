package com.skullmangames.darksouls.network.packets.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.common.entity.covenant.Covenant;
import com.skullmangames.darksouls.common.entity.covenant.Covenants;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.packets.NetworkPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

public class STCCovenant implements NetworkPacket
{
	private int entityId;
	private Covenant covenant;
	
	public STCCovenant(int entityid, Covenant covenant)
	{
		this.entityId = entityid;
		this.covenant = covenant;
	}
	
	public STCCovenant(FriendlyByteBuf buf)
	{
		this.entityId = buf.readInt();
		this.covenant = Covenants.COVENANTS.get(buf.readInt());
	}
	
	@Override
	public void encode(FriendlyByteBuf buf)
	{
		buf.writeInt(this.entityId);
		buf.writeInt(Covenants.COVENANTS.indexOf(this.covenant));
	}
	
	@Override
	public void handle(Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			Minecraft minecraft = Minecraft.getInstance();
			Entity entity = minecraft.player.level.getEntity(this.entityId);
			if (entity == null) return;
			
			PlayerCap<?> playerCap = (PlayerCap<?>) entity.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
			if (playerCap == null) return;
			
			playerCap.setCovenant(this.covenant);
			if (this.covenant.is(Covenants.NONE))
			{
				ModNetworkManager.connection.setOverlayMessage(new TextComponent("Covenant left"));
			}
			else ModNetworkManager.connection.setOverlayMessage(new TextComponent("Covenant joined"));
		});
		
		ctx.get().setPacketHandled(true);
	}
}
