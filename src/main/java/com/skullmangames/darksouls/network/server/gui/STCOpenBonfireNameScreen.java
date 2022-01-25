package com.skullmangames.darksouls.network.server.gui;

import java.util.function.Supplier;

import com.skullmangames.darksouls.client.gui.screens.BonfireNameScreen;
import com.skullmangames.darksouls.common.tileentity.BonfireBlockEntity;
import com.skullmangames.darksouls.core.init.ModBlockEntities;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class STCOpenBonfireNameScreen
{
	private BlockPos blockPos;
	
	public STCOpenBonfireNameScreen(BlockPos pos)
	{
		this.blockPos = pos;
	}
	
	public static STCOpenBonfireNameScreen fromBytes(FriendlyByteBuf buf)
	{
		return new STCOpenBonfireNameScreen(buf.readBlockPos());
	}
	
	public static void toBytes(STCOpenBonfireNameScreen msg, FriendlyByteBuf buf)
	{
		buf.writeBlockPos(msg.blockPos);
	}
	
	public static void handle(STCOpenBonfireNameScreen msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			Minecraft minecraft = Minecraft.getInstance();
			BonfireBlockEntity bonfire = minecraft.level.getBlockEntity(msg.blockPos, ModBlockEntities.BONFIRE.get()).orElse(null);
			if (bonfire != null) minecraft.setScreen(new BonfireNameScreen(bonfire));
		});
		
		ctx.get().setPacketHandled(true);
	}
}
