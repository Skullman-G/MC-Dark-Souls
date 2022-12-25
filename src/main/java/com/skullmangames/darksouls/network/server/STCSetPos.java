package com.skullmangames.darksouls.network.server;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

public class STCSetPos
{
	private int entityId;
	private Vec3 pos;
	private float yRot;
	private float xRot;
	private boolean teleport;
	
	public STCSetPos(Vec3 pos, float yRot, float xRot, int entityId)
	{
		this(pos, yRot, xRot, entityId, false);
	}
	
	public STCSetPos(Vec3 pos, float yRot, float xRot, int entityId, boolean teleport)
	{
		this.pos = pos;
		this.yRot = yRot;
		this.xRot = xRot;
		this.entityId = entityId;
		this.teleport = teleport;
	}
	
	public static STCSetPos fromBytes(FriendlyByteBuf buf)
	{
		return new STCSetPos(new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()),
				buf.readFloat(), buf.readFloat(),
				buf.readInt(), buf.readBoolean());
	}
	
	public static void toBytes(STCSetPos msg, FriendlyByteBuf buf)
	{
		buf.writeDouble(msg.pos.x);
		buf.writeDouble(msg.pos.y);
		buf.writeDouble(msg.pos.z);
		
		buf.writeFloat(msg.yRot);
		buf.writeFloat(msg.xRot);
		
		buf.writeInt(msg.entityId);
		
		buf.writeBoolean(msg.teleport);
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
				if (msg.teleport)
				{
					livingentity.setPos(msg.pos);
					
					livingentity.yRot = msg.yRot;
					livingentity.yRotO = msg.yRot;
					livingentity.yBodyRot = msg.yRot;
					livingentity.yBodyRotO = msg.yRot;
					livingentity.yHeadRot = msg.yRot;
					livingentity.yHeadRotO = msg.yRot;
					
					livingentity.xRot = msg.xRot;
				}
				else livingentity.lerpTo(msg.pos.x, msg.pos.y, msg.pos.z, msg.yRot, msg.xRot, 3, false);
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
