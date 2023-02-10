package com.skullmangames.darksouls.network.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.common.entity.Covenant;
import com.skullmangames.darksouls.common.entity.Covenants;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.network.ModNetworkManager;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.network.NetworkEvent;

public class STCCovenant
{
	private int entityId;
	private Covenant covenant;
	
	public STCCovenant(int entityid, Covenant covenant)
	{
		this.entityId = entityid;
		this.covenant = covenant;
	}
	
	public static STCCovenant fromBytes(PacketBuffer buf)
	{
		return new STCCovenant(buf.readInt(), Covenants.COVENANTS.get(buf.readInt()));
	}
	
	public static void toBytes(STCCovenant msg, PacketBuffer buf)
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
				ModNetworkManager.connection.setOverlayMessage(new StringTextComponent("Covenant left"));
			}
			else ModNetworkManager.connection.setOverlayMessage(new StringTextComponent("Covenant joined"));
		});
		
		ctx.get().setPacketHandled(true);
	}
}
