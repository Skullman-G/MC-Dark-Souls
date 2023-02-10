package com.skullmangames.darksouls.network.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.network.ModNetworkManager;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.NetworkEvent;

public class STCEntityImpactParticles
{
	private int entityId;
	private Vector3d impactPos;
	private boolean blocked;
	
	public STCEntityImpactParticles(int entityId, Vector3d impactPos, boolean blocked)
	{
		this.entityId = entityId;
		this.impactPos = impactPos;
		this.blocked = blocked;
	}
	
	public static STCEntityImpactParticles fromBytes(PacketBuffer buf)
	{
		return new STCEntityImpactParticles(buf.readInt(), new Vector3d(buf.readDouble(), buf.readDouble(), buf.readDouble()), buf.readBoolean());
	}
	
	public static void toBytes(STCEntityImpactParticles msg, PacketBuffer buf)
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
			ModNetworkManager.connection.makeImpactParticles(msg.entityId, msg.impactPos, msg.blocked);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
