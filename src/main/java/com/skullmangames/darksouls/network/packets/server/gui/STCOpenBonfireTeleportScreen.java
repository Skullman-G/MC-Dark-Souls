package com.skullmangames.darksouls.network.packets.server.gui;

import java.util.List;
import java.util.function.Supplier;

import com.mojang.datafixers.util.Pair;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.packets.NetworkPacket;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class STCOpenBonfireTeleportScreen implements NetworkPacket
{
	private BlockPos blockPos;
	private List<Pair<String, BlockPos>> teleports;
	
	public STCOpenBonfireTeleportScreen(BlockPos pos, List<Pair<String, BlockPos>> teleports)
	{
		this.blockPos = pos;
		this.teleports = teleports;
	}
	
	public STCOpenBonfireTeleportScreen(FriendlyByteBuf buf)
	{
		this.blockPos = buf.readBlockPos();
		this.teleports = buf.readList((b) -> new Pair<String, BlockPos>(b.readUtf(), b.readBlockPos()));
	}
	
	@Override
	public void encode(FriendlyByteBuf buf)
	{
		buf.writeBlockPos(this.blockPos);
		buf.writeCollection(this.teleports, (b, p) ->
		{
			b.writeUtf(p.getFirst());
			b.writeBlockPos(p.getSecond());
		});
	}
	
	@Override
	public void handle(Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ModNetworkManager.connection.openBonfireTeleportScreen(this.blockPos, this.teleports);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
