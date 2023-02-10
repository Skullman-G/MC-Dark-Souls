package com.skullmangames.darksouls.network.server.gui;

import java.util.List;
import java.util.function.Supplier;

import com.mojang.datafixers.util.Pair;
import com.skullmangames.darksouls.core.util.ModUtil;
import com.skullmangames.darksouls.network.ModNetworkManager;

import net.minecraft.util.math.BlockPos;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class STCOpenBonfireTeleportScreen
{
	private BlockPos blockPos;
	private List<Pair<String, BlockPos>> teleports;
	
	public STCOpenBonfireTeleportScreen(BlockPos pos, List<Pair<String, BlockPos>> teleports)
	{
		this.blockPos = pos;
		this.teleports = teleports;
	}
	
	public static STCOpenBonfireTeleportScreen fromBytes(PacketBuffer buf)
	{
		return new STCOpenBonfireTeleportScreen(buf.readBlockPos(), ModUtil.readList(buf, (b) -> new Pair<String, BlockPos>(b.readUtf(), b.readBlockPos())));
	}
	
	public static void toBytes(STCOpenBonfireTeleportScreen msg, PacketBuffer buf)
	{
		buf.writeBlockPos(msg.blockPos);
		ModUtil.writeList(buf, msg.teleports, (b, p) ->
		{
			b.writeUtf(p.getFirst());
			b.writeBlockPos(p.getSecond());
		});
	}
	
	public static void handle(STCOpenBonfireTeleportScreen msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ModNetworkManager.connection.openBonfireTeleportScreen(msg.blockPos, msg.teleports);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
