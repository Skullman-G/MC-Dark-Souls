package com.skullmangames.darksouls.network.client;

import java.util.Optional;
import java.util.function.Supplier;

import com.skullmangames.darksouls.common.block.BonfireBlock;
import com.skullmangames.darksouls.common.blockentity.BonfireBlockEntity;
import com.skullmangames.darksouls.common.capability.entity.ServerPlayerCap;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

public class CTSTeleportPlayer
{
	private BlockPos blockPos;
	
	public CTSTeleportPlayer(BlockPos pos)
	{
		this.blockPos = pos;
	}
	
	public static CTSTeleportPlayer fromBytes(FriendlyByteBuf buf)
	{
		return new CTSTeleportPlayer(buf.readBlockPos());
	}
	
	public static void toBytes(CTSTeleportPlayer msg, FriendlyByteBuf buf)
	{
		buf.writeBlockPos(msg.blockPos);
	}
	
	public static void handle(CTSTeleportPlayer msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ServerPlayer player = ctx.get().getSender();
			ServerPlayerCap playerCap = (ServerPlayerCap)player.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
			BlockEntity blockEntity = player.level.getBlockEntity(msg.blockPos);
			Optional<Vec3> optPos = BonfireBlock.findStandUpPosition(player.getType(), player.level, msg.blockPos);
			
			if (blockEntity instanceof BonfireBlockEntity && optPos.isPresent() && playerCap != null)
			{
				 playerCap.futureTeleport = optPos.get();
				 playerCap.playAnimationSynchronized(Animations.BIPED_TOUCH_BONFIRE, 0.0F);
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
