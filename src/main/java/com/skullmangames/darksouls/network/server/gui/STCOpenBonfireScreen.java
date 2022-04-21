package com.skullmangames.darksouls.network.server.gui;

import java.util.function.Supplier;

import com.skullmangames.darksouls.client.gui.screens.BonfireScreen;
import com.skullmangames.darksouls.common.blockentity.BonfireBlockEntity;
import com.skullmangames.darksouls.core.init.ModBlockEntities;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class STCOpenBonfireScreen
{
	private BlockPos blockPos;
	
	public STCOpenBonfireScreen(BlockPos pos)
	{
		this.blockPos = pos;
	}
	
	public static STCOpenBonfireScreen fromBytes(FriendlyByteBuf buf)
	{
		return new STCOpenBonfireScreen(buf.readBlockPos());
	}
	
	public static void toBytes(STCOpenBonfireScreen msg, FriendlyByteBuf buf)
	{
		buf.writeBlockPos(msg.blockPos);
	}
	
	public static void handle(STCOpenBonfireScreen msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			Minecraft minecraft = Minecraft.getInstance();
			BonfireBlockEntity bonfire = minecraft.level.getBlockEntity(msg.blockPos, ModBlockEntities.BONFIRE.get()).orElse(null);
			if (bonfire != null) minecraft.setScreen(new BonfireScreen(bonfire));
		});
		
		ctx.get().setPacketHandled(true);
	}
}
