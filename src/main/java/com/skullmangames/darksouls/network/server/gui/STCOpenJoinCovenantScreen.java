package com.skullmangames.darksouls.network.server.gui;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.entity.Covenant;
import com.skullmangames.darksouls.common.entity.Covenants;
import com.skullmangames.darksouls.network.ModNetworkManager;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class STCOpenJoinCovenantScreen
{
	private Covenant covenant;
	
	public STCOpenJoinCovenantScreen(Covenant covenant)
	{
		this.covenant = covenant;
	}
	
	public static STCOpenJoinCovenantScreen fromBytes(FriendlyByteBuf buf)
	{
		return new STCOpenJoinCovenantScreen(Covenants.COVENANTS.get(buf.readInt()));
	}
	
	public static void toBytes(STCOpenJoinCovenantScreen msg, FriendlyByteBuf buf)
	{
		buf.writeInt(Covenants.COVENANTS.indexOf(msg.covenant));
	}
	
	public static void handle(STCOpenJoinCovenantScreen msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ModNetworkManager.connection.openJoinCovenantScreen(msg.covenant);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
