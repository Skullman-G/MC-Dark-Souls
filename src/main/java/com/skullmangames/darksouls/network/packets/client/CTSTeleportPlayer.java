package com.skullmangames.darksouls.network.packets.client;

import java.util.Optional;
import java.util.function.Supplier;

import com.skullmangames.darksouls.common.block.BonfireBlock;
import com.skullmangames.darksouls.common.blockentity.BonfireBlockEntity;
import com.skullmangames.darksouls.common.capability.entity.ServerPlayerCap;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.network.packets.NetworkPacket;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

public class CTSTeleportPlayer implements NetworkPacket
{
	private BlockPos blockPos;
	
	public CTSTeleportPlayer(BlockPos pos)
	{
		this.blockPos = pos;
	}
	
	public CTSTeleportPlayer(FriendlyByteBuf buf)
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
			ServerPlayer player = ctx.get().getSender();
			ServerPlayerCap playerCap = (ServerPlayerCap)player.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
			BlockEntity blockEntity = player.level.getBlockEntity(this.blockPos);
			Optional<Vec3> optPos = BonfireBlock.findStandUpPosition(player.getType(), player.level, this.blockPos);
			
			if (blockEntity instanceof BonfireBlockEntity && optPos.isPresent() && playerCap != null)
			{
				 playerCap.futureTeleport = optPos.get();
				 playerCap.playAnimationSynchronized(Animations.BIPED_TOUCH_BONFIRE.get(), 0.0F);
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
