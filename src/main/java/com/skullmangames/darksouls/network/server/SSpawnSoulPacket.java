package com.skullmangames.darksouls.network.server;

import java.io.IOException;

import com.skullmangames.darksouls.common.entity.SoulEntity;
import com.skullmangames.darksouls.network.ModNetworkManager;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;

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
	
	public double getX()
	{
		return this.x;
	}
	
	public double getY()
	{
		return this.y;
	}
	
	public double getZ()
	{
		return this.z;
	}
	
	public int getValue()
	{
		return this.value;
	}
	
	public int getId()
	{
		return this.id;
	}

	@Override
	public void handle(IClientPlayNetHandler ihandler)
	{
		if (ModNetworkManager.connection == null) return;
		ModNetworkManager.connection.handleAddSoulEntity(this);
	}
}
