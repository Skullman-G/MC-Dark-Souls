package com.skullmangames.darksouls.network.client;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.block.BonfireBlock;
import com.skullmangames.darksouls.common.capability.entity.ServerPlayerData;
import com.skullmangames.darksouls.common.tileentity.BonfireBlockEntity;
import com.skullmangames.darksouls.core.init.CriteriaTriggerInit;
import com.skullmangames.darksouls.core.init.ModBlockEntities;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

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
	
	public static CTSBonfireTask fromBytes(FriendlyByteBuf buf)
	{
		return new CTSBonfireTask(buf.readEnum(Task.class), buf.readBlockPos(), buf.readUtf());
	}
	
	public static void toBytes(CTSBonfireTask msg, FriendlyByteBuf buf)
	{
		buf.writeEnum(msg.task);
		buf.writeBlockPos(msg.bonfirePos);
		buf.writeUtf(msg.name);
	}
	
	public static void handle(CTSBonfireTask msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ServerPlayer serverplayer = ctx.get().getSender();
			ServerPlayerData playerdata = (ServerPlayerData) serverplayer.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			if (playerdata == null || playerdata.hasEnoughHumanity(1)) return;
			
			BonfireBlockEntity bonfire = serverplayer.level.getBlockEntity(msg.bonfirePos, ModBlockEntities.BONFIRE.get()).orElse(null);
			if (bonfire == null) return;
			
			switch(msg.task)
			{
				case REVERSE_HOLLOWING:
					if (playerdata.isHuman() || !bonfire.getBlockState().getValue(BonfireBlock.LIT)) return;
					playerdata.raiseHumanity(-1);
					playerdata.setHuman(true);
					break;
					
				case KINDLE:
					if (!playerdata.isHuman() || !bonfire.canKindle() || !bonfire.getBlockState().getValue(BonfireBlock.LIT)) return;
					playerdata.raiseHumanity(-1);
					bonfire.kindle();
					break;
					
				case NAME:
					if (bonfire.hasName() || msg.name == "") return;
					bonfire.setName(msg.name);
					if (!bonfire.getBlockState().getValue(BonfireBlock.LIT))
					{
						bonfire.setLit(true);
						CriteriaTriggerInit.BONFIRE_LIT.trigger(serverplayer, true);
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
