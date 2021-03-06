package com.skullmangames.darksouls.network.client;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.entity.AbstractFireKeeper;

import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class CTSOpenFireKeeperContainer
{
	private int id;
	
	public CTSOpenFireKeeperContainer(int id)
	{
		this.id = id;
	}
	
	public static CTSOpenFireKeeperContainer fromBytes(FriendlyByteBuf buf)
	{
		return new CTSOpenFireKeeperContainer(buf.readInt());
	}
	
	public static void toBytes(CTSOpenFireKeeperContainer msg, FriendlyByteBuf buf)
	{
		buf.writeInt(msg.id);
	}
	
	public static void handle(CTSOpenFireKeeperContainer msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ServerPlayer serverPlayer = ctx.get().getSender();
			Entity entity = serverPlayer.level.getEntity(msg.id);
			if (entity instanceof AbstractFireKeeper) ((AbstractFireKeeper)entity).openContainer(serverPlayer);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
