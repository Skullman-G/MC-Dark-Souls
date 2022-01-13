package com.skullmangames.darksouls.network.server;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class STCPlayAnimationTarget extends STCPlayAnimation
{
	protected int targetId;
	
	public STCPlayAnimationTarget()
	{
		super();
		this.targetId = 0;
	}
	
	public STCPlayAnimationTarget(int animation, int entityId, float modifyTime, int targetId)
	{
		this(animation, entityId, modifyTime, false, targetId);
	}
	
	public STCPlayAnimationTarget(int animation, int entityId, float modifyTime, boolean mixLayer, int targetId)
	{
		super(animation, entityId, modifyTime, mixLayer);
		this.targetId = targetId;
	}
	
	@Override
	public void onArrive()
	{
		super.onArrive();
		
		Minecraft minecraft = Minecraft.getInstance();
		Entity entity = minecraft.player.level.getEntity(entityId);
		Entity target = minecraft.player.level.getEntity(targetId);
		
		if(entity instanceof Mob && target instanceof LivingEntity)
		{
			Mob entityliving = (Mob) entity;
			entityliving.setTarget((LivingEntity) target);
		}
	}
	
	public static STCPlayAnimationTarget fromBytes(FriendlyByteBuf buf)
	{
		return new STCPlayAnimationTarget(buf.readInt(), buf.readInt(), buf.readFloat(), buf.readBoolean(), buf.readInt());
	}
	
	public static void toBytes(STCPlayAnimationTarget msg, FriendlyByteBuf buf)
	{
		buf.writeInt(msg.animationId);
		buf.writeInt(msg.entityId);
		buf.writeFloat(msg.modifyTime);
		buf.writeBoolean(msg.mixLayer);
		buf.writeInt(msg.targetId);
	}
	
	public static void handle(STCPlayAnimationTarget msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->{
			msg.onArrive();
		});
		ctx.get().setPacketHandled(true);
	}
}