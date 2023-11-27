package com.skullmangames.darksouls.network.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.network.ModNetworkManager;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

public class STCEntityImpactParticles
{
	private int entityId;
	private Vec3 impactPos;
	private boolean blocked;
	
	public STCEntityImpactParticles(int entityId, Vec3 impactPos, boolean blocked)
	{
		this.entityId = entityId;
		this.impactPos = impactPos;
		this.blocked = blocked;
	}
	
	public static STCEntityImpactParticles fromBytes(FriendlyByteBuf buf)
	{
		return new STCEntityImpactParticles(buf.readInt(), new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()), buf.readBoolean());
	}
	
	public static void toBytes(STCEntityImpactParticles msg, FriendlyByteBuf buf)
	{
		buf.writeInt(msg.entityId);
		buf.writeDouble(msg.impactPos.x);
		buf.writeDouble(msg.impactPos.y);
		buf.writeDouble(msg.impactPos.z);
		buf.writeBoolean(msg.blocked);
	}
	
	public static void handle(STCEntityImpactParticles msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			Minecraft minecraft = Minecraft.getInstance();
			Entity entity = minecraft.level.getEntity(msg.entityId);
			ModNetworkManager.connection.makeImpactParticles(entity, msg.impactPos, msg.blocked);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
