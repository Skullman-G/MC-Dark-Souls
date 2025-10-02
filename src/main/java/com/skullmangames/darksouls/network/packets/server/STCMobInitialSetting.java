package com.skullmangames.darksouls.network.packets.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.HumanoidCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.network.packets.NetworkPacket;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class STCMobInitialSetting implements NetworkPacket
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
		this.buffer = new FriendlyByteBuf(Unpooled.buffer());
	}
	
	public FriendlyByteBuf getBuffer()
	{
		return this.buffer;
	}
	
	public STCMobInitialSetting(FriendlyByteBuf buf)
	{
		this.entityId = buf.readInt();
		this.buffer = new FriendlyByteBuf(Unpooled.buffer());
		
		while(buf.isReadable())
		{
			this.buffer.writeByte(buf.readByte());
		}
	}
	
	@Override
	public void encode(FriendlyByteBuf buf)
	{
		buf.writeInt(this.entityId);
		
		while(this.buffer.isReadable())
		{
			buf.writeByte(this.buffer.readByte());
		}
	}
	
	@Override
	public void handle(Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			Minecraft minecraft = Minecraft.getInstance();
			Entity entity = minecraft.player.level.getEntity(this.entityId);
			if(entity != null)
			{
				HumanoidCap<?> entityCap = (HumanoidCap<?>) entity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				entityCap.clientInitialSettings(this.getBuffer());
			}
		});
		
		ctx.get().setPacketHandled(true);
	}
}
