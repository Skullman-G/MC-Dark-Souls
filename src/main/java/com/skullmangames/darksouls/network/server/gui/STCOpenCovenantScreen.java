package com.skullmangames.darksouls.network.server.gui;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.entity.covenant.Covenant;
import com.skullmangames.darksouls.common.entity.covenant.Covenants;
import com.skullmangames.darksouls.network.ModNetworkManager;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class STCOpenCovenantScreen
{
private Covenant covenant;
	
	public STCOpenCovenantScreen(Covenant covenant)
	{
		this.covenant = covenant;
	}
	
	public static STCOpenCovenantScreen fromBytes(FriendlyByteBuf buf)
	{
		return new STCOpenCovenantScreen(Covenants.COVENANTS.get(buf.readInt()));
	}
	
	public static void toBytes(STCOpenCovenantScreen msg, FriendlyByteBuf buf)
	{
		buf.writeInt(Covenants.COVENANTS.indexOf(msg.covenant));
	}
	
	public static void handle(STCOpenCovenantScreen msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ModNetworkManager.connection.openCovenantScreen(msg.covenant);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
