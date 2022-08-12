package com.skullmangames.darksouls.network.server;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.fml.network.NetworkEvent;

public class STCSetPos
{
	private int entityId;
	private Vector3d pos;
	private float yRot;
	private float xRot;
	
	public STCSetPos(Vector3d pos, float yRot, float xRot, int entityId)
	{
		this.pos = pos;
		this.yRot = yRot;
		this.xRot = xRot;
		this.entityId = entityId;
	}
	
	public static STCSetPos fromBytes(PacketBuffer buf)
	{
		return new STCSetPos(new Vector3d(buf.readDouble(), buf.readDouble(), buf.readDouble()),
				buf.readFloat(), buf.readFloat(),
				buf.readInt());
	}
	
	public static void toBytes(STCSetPos msg, PacketBuffer buf)
	{
		buf.writeDouble(msg.pos.x);
		buf.writeDouble(msg.pos.y);
		buf.writeDouble(msg.pos.z);
		
		buf.writeFloat(msg.yRot);
		buf.writeFloat(msg.xRot);
		
		buf.writeInt(msg.entityId);
	}
	
	public static void handle(STCSetPos msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			Minecraft minecraft = Minecraft.getInstance();
			Entity entity = minecraft.level.getEntity(msg.entityId);
			
			if(entity != null && entity instanceof LivingEntity)
			{
				LivingEntity livingentity = ((LivingEntity)entity);
				livingentity.lerpTo(msg.pos.x, msg.pos.y, msg.pos.z, msg.yRot, msg.xRot, 3, false);
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
