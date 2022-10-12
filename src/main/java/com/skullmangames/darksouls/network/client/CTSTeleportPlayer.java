package com.skullmangames.darksouls.network.client;

import java.util.Optional;
import java.util.function.Supplier;

import com.skullmangames.darksouls.common.block.BonfireBlock;
import com.skullmangames.darksouls.common.blockentity.BonfireBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
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
			Entity entity = ctx.get().getSender();
			BlockEntity blockEntity = entity.level.getBlockEntity(msg.blockPos);
			Optional<Vec3> optPos = BonfireBlock.findStandUpPosition(entity.getType(), entity.level, msg.blockPos);
			
			if (blockEntity instanceof BonfireBlockEntity && optPos.isPresent())
			{
				Vec3 pos = optPos.get();
				entity.teleportTo(pos.x, pos.y, pos.z);
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
