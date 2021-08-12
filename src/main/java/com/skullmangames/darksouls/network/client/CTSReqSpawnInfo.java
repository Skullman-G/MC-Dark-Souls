package com.skullmangames.darksouls.network.client;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.ModCapabilities;
import com.skullmangames.darksouls.common.entities.BipedMobData;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.play.server.STCMobInitialSetting;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class CTSReqSpawnInfo
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
	
	public static CTSReqSpawnInfo fromBytes(PacketBuffer buf)
	{
		return new CTSReqSpawnInfo(buf.readInt());
	}
	
	public static void toBytes(CTSReqSpawnInfo msg, PacketBuffer buf)
	{
		buf.writeInt(msg.entityId);
	}
	
	public static void handle(CTSReqSpawnInfo msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			Entity entity = ctx.get().getSender().level.getEntity(msg.entityId);
			
			if(entity != null)
			{
				BipedMobData<?> entitydata = (BipedMobData<?>) entity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				
				if(entitydata != null)
				{
					STCMobInitialSetting mobSet = entitydata.sendInitialInformationToClient();
					
					if(mobSet != null)
						ModNetworkManager.sendToPlayer(mobSet, ctx.get().getSender());
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
}