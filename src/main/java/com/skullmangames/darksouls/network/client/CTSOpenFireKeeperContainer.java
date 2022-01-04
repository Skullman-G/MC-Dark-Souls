package com.skullmangames.darksouls.network.client;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.entity.FireKeeperEntity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class CTSOpenFireKeeperContainer
{
	private int id;
	
	public CTSOpenFireKeeperContainer(int id)
	{
		this.id = id;
	}
	
	public static CTSOpenFireKeeperContainer fromBytes(PacketBuffer buf)
	{
		return new CTSOpenFireKeeperContainer(buf.readInt());
	}
	
	public static void toBytes(CTSOpenFireKeeperContainer msg, PacketBuffer buf)
	{
		buf.writeInt(msg.id);
	}
	
	public static void handle(CTSOpenFireKeeperContainer msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ServerPlayerEntity serverPlayer = ctx.get().getSender();
			Entity entity = serverPlayer.level.getEntity(msg.id);
			if (entity instanceof FireKeeperEntity) ((FireKeeperEntity)entity).openContainer(serverPlayer);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
