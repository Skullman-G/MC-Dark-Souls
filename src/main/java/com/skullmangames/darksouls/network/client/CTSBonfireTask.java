package com.skullmangames.darksouls.network.client;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.block.BonfireBlock;
import com.skullmangames.darksouls.common.blockentity.BonfireBlockEntity;
import com.skullmangames.darksouls.common.capability.entity.ServerPlayerCap;
import com.skullmangames.darksouls.core.init.ModCriteriaTriggers;
import com.skullmangames.darksouls.core.util.ModUtil;
import com.skullmangames.darksouls.core.init.ModBlockEntities;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.util.math.BlockPos;
import net.minecraft.network.PacketBuffer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;

public class CTSBonfireTask
{
	private Task task;
	private BlockPos bonfirePos;
	private String name;
	
	public CTSBonfireTask(Task task, BlockPos bonfirePos, String name)
	{
		this.task = task;
		this.bonfirePos = bonfirePos;
		this.name = name;
	}
	
	public static CTSBonfireTask fromBytes(PacketBuffer buf)
	{
		return new CTSBonfireTask(buf.readEnum(Task.class), buf.readBlockPos(), buf.readUtf());
	}
	
	public static void toBytes(CTSBonfireTask msg, PacketBuffer buf)
	{
		buf.writeEnum(msg.task);
		buf.writeBlockPos(msg.bonfirePos);
		buf.writeUtf(msg.name);
	}
	
	public static void handle(CTSBonfireTask msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ServerPlayerEntity serverplayer = ctx.get().getSender();
			ServerPlayerCap playerCap = (ServerPlayerCap) serverplayer.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			if (playerCap == null) return;
			
			BonfireBlockEntity bonfire = ModUtil.getBlockEntity(serverplayer.level, msg.bonfirePos, ModBlockEntities.BONFIRE.get()).orElse(null);
			if (bonfire == null) return;
			
			switch(msg.task)
			{
				case REVERSE_HOLLOWING:
					if (playerCap.isHuman() || !bonfire.getBlockState().getValue(BonfireBlock.LIT) || !playerCap.hasEnoughHumanity(1)) return;
					playerCap.raiseHumanity(-1);
					playerCap.setHuman(true);
					break;
					
				case KINDLE:
					if (!playerCap.isHuman() || !bonfire.canKindle() || !bonfire.getBlockState().getValue(BonfireBlock.LIT) || !playerCap.hasEnoughHumanity(1)) return;
					playerCap.raiseHumanity(-1);
					bonfire.kindle();
					break;
					
				case NAME:
					if (bonfire.hasName() || msg.name == "") return;
					bonfire.setName(msg.name);
					if (!bonfire.getBlockState().getValue(BonfireBlock.LIT))
					{
						bonfire.setLit(true);
						ModCriteriaTriggers.BONFIRE_LIT.trigger(serverplayer, true);
					}
					break;
			}
		});
		
		ctx.get().setPacketHandled(true);
	}
	
	public enum Task
	{
		REVERSE_HOLLOWING, KINDLE, NAME
	}
}
