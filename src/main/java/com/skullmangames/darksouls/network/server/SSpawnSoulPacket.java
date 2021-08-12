package com.skullmangames.darksouls.network.server;

import java.io.IOException;

import com.skullmangames.darksouls.common.entity.SoulEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketThreadUtil;

public class SSpawnSoulPacket implements IPacket<IClientPlayNetHandler>
{
	private int id;
	private double x;
	private double y;
	private double z;
	private int value;
	
	public SSpawnSoulPacket(SoulEntity entity)
	{
		this.id = entity.getId();
		this.x = entity.getX();
		this.y = entity.getY();
		this.z = entity.getZ();
		this.value = entity.getValue();
	}
	
	@Override
	public void read(PacketBuffer buffer) throws IOException
	{
		this.id = buffer.readVarInt();
	    this.x = buffer.readDouble();
	    this.y = buffer.readDouble();
	    this.z = buffer.readDouble();
	}

	@Override
	public void write(PacketBuffer buffer) throws IOException
	{
		buffer.writeVarInt(this.id);
	    buffer.writeDouble(this.x);
	    buffer.writeDouble(this.y);
	    buffer.writeDouble(this.z);
	    buffer.writeInt(this.value);
	}

	@Override
	public void handle(IClientPlayNetHandler ihandler)
	{
		if (ihandler instanceof ClientPlayNetHandler)
		{
			ClientPlayNetHandler handler = (ClientPlayNetHandler)ihandler;
			PacketThreadUtil.ensureRunningOnSameThread(this, handler, Minecraft.getInstance());
			Entity entity = new SoulEntity(handler.getLevel(), this.x, this.y, this.z, this.value);
			entity.setPacketCoordinates(this.x, this.y, this.z);
		    entity.yRot = 0.0F;
		    entity.xRot = 0.0F;
		    entity.setId(this.id);
		    handler.getLevel().putNonPlayerEntity(this.id, entity);
		}
	}
}
