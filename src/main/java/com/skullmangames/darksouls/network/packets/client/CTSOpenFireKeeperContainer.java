package com.skullmangames.darksouls.network.packets.client;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.entity.AbstractFireKeeper;
import com.skullmangames.darksouls.network.packets.NetworkPacket;

import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class CTSOpenFireKeeperContainer implements NetworkPacket
{
	private int id;
	
	public CTSOpenFireKeeperContainer(int id)
	{
		this.id = id;
	}
	
	public CTSOpenFireKeeperContainer(FriendlyByteBuf buf)
	{
		this.id = buf.readInt();
	}
	
	@Override
	public void encode(FriendlyByteBuf buf)
	{
		buf.writeInt(this.id);
	}
	
	@Override
	public void handle(Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ServerPlayer serverPlayer = ctx.get().getSender();
			Entity entity = serverPlayer.level.getEntity(this.id);
			if (entity instanceof AbstractFireKeeper) ((AbstractFireKeeper)entity).openContainer(serverPlayer);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
