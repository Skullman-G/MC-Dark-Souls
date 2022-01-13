package com.skullmangames.darksouls.network.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.BipedMobData;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class STCMobInitialSetting
{
	private int entityId;
	private FriendlyByteBuf buffer;
	
	public STCMobInitialSetting()
	{
		this.entityId = 0;
		buffer = new FriendlyByteBuf(Unpooled.buffer());
	}
	
	public STCMobInitialSetting(int entityId)
	{
		this.entityId = entityId;
		buffer = new FriendlyByteBuf(Unpooled.buffer());
	}
	
	public FriendlyByteBuf getBuffer()
	{
		return this.buffer;
	}
	
	public static STCMobInitialSetting fromBytes(FriendlyByteBuf buf)
	{
		STCMobInitialSetting msg = new STCMobInitialSetting(buf.readInt());
		
		while(buf.isReadable())
		{
			msg.buffer.writeByte(buf.readByte());
		}
		
		return msg;
	}
	
	public static void toBytes(STCMobInitialSetting msg, FriendlyByteBuf buf)
	{
		buf.writeInt(msg.entityId);
		
		while(msg.buffer.isReadable())
		{
			buf.writeByte(msg.buffer.readByte());
		}
	}
	
	public static void handle(STCMobInitialSetting msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			Minecraft minecraft = Minecraft.getInstance();
			Entity entity = minecraft.player.level.getEntity(msg.entityId);
			if(entity != null)
			{
				BipedMobData<?> entitydata = (BipedMobData<?>) entity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				entitydata.clientInitialSettings(msg.getBuffer());
			}
		});
		
		ctx.get().setPacketHandled(true);
	}
}
