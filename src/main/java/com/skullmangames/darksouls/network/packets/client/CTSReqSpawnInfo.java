package com.skullmangames.darksouls.network.packets.client;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.HumanoidCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.packets.NetworkPacket;
import com.skullmangames.darksouls.network.packets.server.STCMobInitialSetting;

import net.minecraft.world.entity.Entity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class CTSReqSpawnInfo implements NetworkPacket
{
	private int entityId;
	
	public CTSReqSpawnInfo()
	{
		this.entityId = 0;
	}
	
	public CTSReqSpawnInfo(int entityId)
	{
		this.entityId = entityId;
	}
	
	public CTSReqSpawnInfo(FriendlyByteBuf buf)
	{
		this.entityId = buf.readInt();
	}
	
	@Override
	public void encode(FriendlyByteBuf buf)
	{
		buf.writeInt(this.entityId);
	}
	
	@Override
	public void handle(Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			Entity entity = ctx.get().getSender().level.getEntity(this.entityId);
			
			if(entity != null)
			{
				HumanoidCap<?> entityCap = (HumanoidCap<?>) entity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				
				if(entityCap != null)
				{
					STCMobInitialSetting mobSet = entityCap.sendInitialInformationToClient();
					
					if(mobSet != null)
						ModNetworkManager.sendToPlayer(mobSet, ctx.get().getSender());
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
}