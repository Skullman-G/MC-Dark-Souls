package com.skullmangames.darksouls.network.server;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class STCPlayAnimationTP extends STCPlayAnimationTarget
{
	protected double posX;
	protected double posY;
	protected double posZ;
	protected float yaw;
	
	public STCPlayAnimationTP()
	{
		super();
		posX = 0;
		posY = 0;
		posZ = 0;
		yaw = 0;
	}
	
	public STCPlayAnimationTP(int animation, int entityId, float modifyTime, int targetId, double posX, double posY, double posZ, float yaw)
	{
		this(animation, entityId, modifyTime, false, targetId, posX, posY, posZ, yaw);
	}
	
	public STCPlayAnimationTP(int animation, int entityId, float modifyTime, boolean mixLayer, int targetId, double posX, double posY, double posZ, float yaw)
	{
		super(animation, entityId, modifyTime, mixLayer, targetId);
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
		this.yaw = yaw;
	}
	
	@Override
	public void onArrive()
	{
		super.onArrive();
		Minecraft minecraft = Minecraft.getInstance();
		Entity entity = minecraft.player.level.getEntity(this.entityId);
		entity.setPos(this.posX, this.posY, this.posZ);
		entity.yRotO = yaw;
		entity.yRot = yaw;
	}
	
	public static STCPlayAnimationTP fromBytes(FriendlyByteBuf buf)
	{
		return new STCPlayAnimationTP(buf.readInt(), buf.readInt(), buf.readFloat(), buf.readBoolean(), buf.readInt(),
				buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readFloat());
	}
	
	public static void toBytes(STCPlayAnimationTP msg, FriendlyByteBuf buf)
	{
		buf.writeInt(msg.animationId);
		buf.writeInt(msg.entityId);
		buf.writeFloat(msg.modifyTime);
		buf.writeBoolean(msg.mixLayer);
		buf.writeInt(msg.targetId);
		buf.writeDouble(msg.posX);
		buf.writeDouble(msg.posY);
		buf.writeDouble(msg.posZ);
		buf.writeFloat(msg.yaw);
	}
	
	public static void handler(STCPlayAnimationTP msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			msg.onArrive();
		});
		ctx.get().setPacketHandled(true);
	}
}