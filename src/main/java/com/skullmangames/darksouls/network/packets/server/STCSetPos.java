package com.skullmangames.darksouls.network.packets.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.network.packets.NetworkPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

public class STCSetPos implements NetworkPacket
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
	
	public STCSetPos(FriendlyByteBuf buf)
	{
		this.pos = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
		this.yRot = buf.readFloat();
		this.xRot = buf.readFloat();
		this.entityId = buf.readInt();
		this.teleport = buf.readBoolean();
	}
	
	@Override
	public void encode(FriendlyByteBuf buf)
	{
		buf.writeDouble(this.pos.x);
		buf.writeDouble(this.pos.y);
		buf.writeDouble(this.pos.z);
		
		buf.writeFloat(this.yRot);
		buf.writeFloat(this.xRot);
		
		buf.writeInt(this.entityId);
		
		buf.writeBoolean(this.teleport);
	}
	
	@Override
	public void handle(Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			Minecraft minecraft = Minecraft.getInstance();
			Entity entity = minecraft.level.getEntity(this.entityId);
			
			if(entity != null && entity instanceof LivingEntity)
			{
				LivingEntity livingentity = ((LivingEntity)entity);
				if (this.teleport)
				{
					livingentity.setPos(this.pos);
					
					livingentity.yRot = this.yRot;
					livingentity.yRotO = this.yRot;
					livingentity.yBodyRot = this.yRot;
					livingentity.yBodyRotO = this.yRot;
					livingentity.yHeadRot = this.yRot;
					livingentity.yHeadRotO = this.yRot;
					
					livingentity.xRot = this.xRot;
				}
				else livingentity.lerpTo(this.pos.x, this.pos.y, this.pos.z, this.yRot, this.xRot, 3, false);
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
