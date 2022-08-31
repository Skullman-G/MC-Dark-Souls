package com.skullmangames.darksouls.network.server.gui;

import java.util.function.Supplier;

import com.skullmangames.darksouls.client.gui.screens.JoinCovenantScreen;
import com.skullmangames.darksouls.common.entity.Covenant;
import net.minecraft.client.Minecraft;
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
		return new STCOpenJoinCovenantScreen(buf.readEnum(Covenant.class));
	}
	
	public static void toBytes(STCOpenJoinCovenantScreen msg, FriendlyByteBuf buf)
	{
		buf.writeEnum(msg.covenant);
	}
	
	public static void handle(STCOpenJoinCovenantScreen msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			Minecraft minecraft = Minecraft.getInstance();
			minecraft.setScreen(new JoinCovenantScreen(msg.covenant));
		});
		
		ctx.get().setPacketHandled(true);
	}
}
