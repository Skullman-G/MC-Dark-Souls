package com.skullmangames.darksouls.network.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.client.ClientManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class STCLoadPlayerData
{
	private CompoundTag nbt;
	
	public STCLoadPlayerData(CompoundTag value)
	{
		this.nbt = value;
	}
	
	public static STCLoadPlayerData fromBytes(FriendlyByteBuf buf)
	{
		return new STCLoadPlayerData(buf.readNbt());
	}
	
	public static void toBytes(STCLoadPlayerData msg, FriendlyByteBuf buf)
	{
		buf.writeNbt(msg.nbt);
	}
	
	public static void handle(STCLoadPlayerData msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ClientManager.INSTANCE.getPlayerData().onLoad(msg.nbt);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
