package com.skullmangames.darksouls.network.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.network.ModNetworkManager;

import net.minecraft.util.math.BlockPos;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class STCPlayBonfireAmbientSound
{
	private BlockPos pos;
	
	public STCPlayBonfireAmbientSound(BlockPos pos)
	{
		this.pos = pos;
	}
	
	public static STCPlayBonfireAmbientSound fromBytes(PacketBuffer buf)
	{
		return new STCPlayBonfireAmbientSound(buf.readBlockPos());
	}
	
	public static void toBytes(STCPlayBonfireAmbientSound msg, PacketBuffer buf)
	{
		buf.writeBlockPos(msg.pos);
	}
	
	public static void handle(STCPlayBonfireAmbientSound msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			if (ctx.get().getSender() != null) return;
			ModNetworkManager.connection.tryPlayBonfireAmbientSound(msg.pos);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
