package com.skullmangames.darksouls.network.packets.client;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.block.BonfireBlock;
import com.skullmangames.darksouls.common.blockentity.BonfireBlockEntity;
import com.skullmangames.darksouls.common.capability.entity.ServerPlayerCap;
import com.skullmangames.darksouls.core.init.ModCriteriaTriggers;
import com.skullmangames.darksouls.network.packets.NetworkPacket;
import com.skullmangames.darksouls.core.init.ModBlockEntities;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class CTSBonfireTask implements NetworkPacket
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
	
	public CTSBonfireTask(FriendlyByteBuf buf)
	{
		this.task = buf.readEnum(Task.class);
		this.bonfirePos = buf.readBlockPos();
		this.name = buf.readUtf();
	}
	
	@Override
	public void encode(FriendlyByteBuf buf)
	{
		buf.writeEnum(this.task);
		buf.writeBlockPos(this.bonfirePos);
		buf.writeUtf(this.name);
	}
	
	@Override
	public void handle(Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ServerPlayer serverplayer = ctx.get().getSender();
			ServerPlayerCap playerdata = (ServerPlayerCap) serverplayer.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			if (playerdata == null) return;
			
			BonfireBlockEntity bonfire = serverplayer.level.getBlockEntity(this.bonfirePos, ModBlockEntities.BONFIRE.get()).orElse(null);
			if (bonfire == null) return;
			
			switch(this.task)
			{
				case REVERSE_HOLLOWING:
					if (playerdata.isHuman() || !bonfire.getBlockState().getValue(BonfireBlock.LIT) || !playerdata.hasEnoughHumanity(1)) return;
					playerdata.raiseHumanity(-1);
					playerdata.setHuman(true);
					break;
					
				case KINDLE:
					if (!playerdata.isHuman() || !bonfire.canKindle() || !bonfire.getBlockState().getValue(BonfireBlock.LIT) || !playerdata.hasEnoughHumanity(1)) return;
					playerdata.raiseHumanity(-1);
					bonfire.kindle();
					break;
					
				case NAME:
					if (bonfire.hasName() || this.name == "") return;
					bonfire.setName(this.name);
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
