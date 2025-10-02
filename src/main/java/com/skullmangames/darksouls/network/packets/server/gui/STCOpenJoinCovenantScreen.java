package com.skullmangames.darksouls.network.packets.server.gui;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.entity.covenant.Covenant;
import com.skullmangames.darksouls.common.entity.covenant.Covenants;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.packets.NetworkPacket;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class STCOpenJoinCovenantScreen implements NetworkPacket
{
	private Covenant covenant;
	
	public STCOpenJoinCovenantScreen(Covenant covenant)
	{
		this.covenant = covenant;
	}
	
	public STCOpenJoinCovenantScreen(FriendlyByteBuf buf)
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
			ModNetworkManager.connection.openJoinCovenantScreen(this.covenant);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
