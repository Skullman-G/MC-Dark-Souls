package com.skullmangames.darksouls.network.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.network.ModNetworkManager;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

public class STCEntityBloodImpactParticles
{
	private int entityId;
	private Vec3 impactPos;
	
	public STCEntityBloodImpactParticles(int entityId, Vec3 impactPos)
	{
		this.entityId = entityId;
		this.impactPos = impactPos;
	}
	
	public static STCEntityBloodImpactParticles fromBytes(FriendlyByteBuf buf)
	{
		return new STCEntityBloodImpactParticles(buf.readInt(),
				new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()));
	}
	
	public static void toBytes(STCEntityBloodImpactParticles msg, FriendlyByteBuf buf)
	{
		buf.writeInt(msg.entityId);
		buf.writeDouble(msg.impactPos.x);
		buf.writeDouble(msg.impactPos.y);
		buf.writeDouble(msg.impactPos.z);
	}
	
	public static void handle(STCEntityBloodImpactParticles msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			Minecraft minecraft = Minecraft.getInstance();
			Entity entity = minecraft.level.getEntity(msg.entityId);
			ModNetworkManager.connection.physicalBloodImpactSfx(entity, msg.impactPos);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
