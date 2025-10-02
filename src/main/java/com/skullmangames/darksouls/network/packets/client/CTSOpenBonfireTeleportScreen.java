package com.skullmangames.darksouls.network.packets.client;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.mojang.datafixers.util.Pair;
import com.skullmangames.darksouls.common.blockentity.BonfireBlockEntity;
import com.skullmangames.darksouls.common.capability.entity.ServerPlayerCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.packets.NetworkPacket;
import com.skullmangames.darksouls.network.packets.server.gui.STCOpenBonfireTeleportScreen;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class CTSOpenBonfireTeleportScreen implements NetworkPacket
{
	private BlockPos blockPos;
	
	public CTSOpenBonfireTeleportScreen(BlockPos pos)
	{
		this.blockPos = pos;
	}
	
	public CTSOpenBonfireTeleportScreen(FriendlyByteBuf buf)
	{
		this.blockPos = buf.readBlockPos();
	}
	
	@Override
	public void encode(FriendlyByteBuf buf)
	{
		buf.writeBlockPos(this.blockPos);
	}
	
	@Override
	public void handle(Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ServerPlayer entity = ctx.get().getSender();
			ServerPlayerCap playerCap = (ServerPlayerCap)entity.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
			BlockEntity blockEntity = entity.level.getBlockEntity(this.blockPos);
			
			if (playerCap != null && blockEntity instanceof BonfireBlockEntity)
			{
				List<Pair<String, BlockPos>> teleports = new ArrayList<>();
				playerCap.updateTeleports();
				for (BonfireBlockEntity bonfire : playerCap.teleports)
				{
					teleports.add(new Pair<String, BlockPos>(bonfire.getName(), bonfire.getBlockPos()));
				}
				ModNetworkManager.sendToPlayer(new STCOpenBonfireTeleportScreen(this.blockPos, teleports), entity);
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
