package com.skullmangames.darksouls.network.packets.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.network.packets.NetworkPacket;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class STCLoadPlayerData implements NetworkPacket
{
	private CompoundTag nbt;
	
	public STCLoadPlayerData(CompoundTag value)
	{
		this.nbt = value;
	}
	
	public STCLoadPlayerData(FriendlyByteBuf buf)
	{
		this.nbt = buf.readNbt();
	}
	
	@Override
	public void encode(FriendlyByteBuf buf)
	{
		buf.writeNbt(this.nbt);
	}
	
	@Override
	public void handle(Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ClientManager.INSTANCE.getPlayerCap().onLoad(this.nbt);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
