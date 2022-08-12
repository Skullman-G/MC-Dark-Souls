package com.skullmangames.darksouls.network.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.client.ClientManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class STCLoadPlayerData
{
	private CompoundNBT nbt;
	
	public STCLoadPlayerData(CompoundNBT value)
	{
		this.nbt = value;
	}
	
	public static STCLoadPlayerData fromBytes(PacketBuffer buf)
	{
		return new STCLoadPlayerData(buf.readNbt());
	}
	
	public static void toBytes(STCLoadPlayerData msg, PacketBuffer buf)
	{
		buf.writeNbt(msg.nbt);
	}
	
	public static void handle(STCLoadPlayerData msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ClientManager.INSTANCE.getPlayerCap().onLoad(msg.nbt);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
