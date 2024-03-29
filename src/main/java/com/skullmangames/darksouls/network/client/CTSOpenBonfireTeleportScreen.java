package com.skullmangames.darksouls.network.client;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.mojang.datafixers.util.Pair;
import com.skullmangames.darksouls.common.blockentity.BonfireBlockEntity;
import com.skullmangames.darksouls.common.capability.entity.ServerPlayerCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.gui.STCOpenBonfireTeleportScreen;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class CTSOpenBonfireTeleportScreen
{
	private BlockPos blockPos;
	
	public CTSOpenBonfireTeleportScreen(BlockPos pos)
	{
		this.blockPos = pos;
	}
	
	public static CTSOpenBonfireTeleportScreen fromBytes(FriendlyByteBuf buf)
	{
		return new CTSOpenBonfireTeleportScreen(buf.readBlockPos());
	}
	
	public static void toBytes(CTSOpenBonfireTeleportScreen msg, FriendlyByteBuf buf)
	{
		buf.writeBlockPos(msg.blockPos);
	}
	
	public static void handle(CTSOpenBonfireTeleportScreen msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ServerPlayer entity = ctx.get().getSender();
			ServerPlayerCap playerCap = (ServerPlayerCap)entity.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
			BlockEntity blockEntity = entity.level.getBlockEntity(msg.blockPos);
			
			if (playerCap != null && blockEntity instanceof BonfireBlockEntity)
			{
				List<Pair<String, BlockPos>> teleports = new ArrayList<>();
				playerCap.updateTeleports();
				for (BonfireBlockEntity bonfire : playerCap.teleports)
				{
					teleports.add(new Pair<String, BlockPos>(bonfire.getName(), bonfire.getBlockPos()));
				}
				ModNetworkManager.sendToPlayer(new STCOpenBonfireTeleportScreen(msg.blockPos, teleports), entity);
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
