package com.skullmangames.darksouls.network.client;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.tileentity.BonfireTileEntity;
import com.skullmangames.darksouls.core.init.CriteriaTriggerInit;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class CTSUpdateBonfireBlock
{
	private String name;
	private boolean lit;
	private boolean estusVolumeLevel;
	private BlockPos blockPos;
	
	public CTSUpdateBonfireBlock(String name, boolean lit, boolean estusvolumelevel, BlockPos pos)
	{
		this.name = name;
		this.lit = lit;
		this.estusVolumeLevel = estusvolumelevel;
		this.blockPos = pos;
	}

	public static CTSUpdateBonfireBlock fromBytes(FriendlyByteBuf buf)
	{
		return new CTSUpdateBonfireBlock(buf.readUtf(), buf.readBoolean(), buf.readBoolean(), buf.readBlockPos());
	}

	public static void toBytes(CTSUpdateBonfireBlock msg, FriendlyByteBuf buf)
	{
		buf.writeUtf(msg.name);
		buf.writeBoolean(msg.lit);
		buf.writeBoolean(msg.estusVolumeLevel);
		buf.writeBlockPos(msg.blockPos);
	}

	public static void handle(CTSUpdateBonfireBlock msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() ->
		{
			ServerPlayer serverplayer = ctx.get().getSender();
			BonfireTileEntity bonfire = (BonfireTileEntity)serverplayer.level.getBlockEntity(msg.blockPos);
			if (bonfire == null) return;
			if (msg.name != "") bonfire.setName(msg.name);
			if (msg.lit)
			{
				bonfire.setLit(msg.lit);
				CriteriaTriggerInit.BONFIRE_LIT.trigger(serverplayer, msg.lit);
			}
			if (msg.estusVolumeLevel) bonfire.kindle();
		});

		ctx.get().setPacketHandled(true);
	}
}
