package com.skullmangames.darksouls.network.packets.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.packets.NetworkPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

public class STCEntityBloodImpactParticles implements NetworkPacket
{
	private int entityId;
	private Vec3 impactPos;
	
	public STCEntityBloodImpactParticles(int entityId, Vec3 impactPos)
	{
		this.entityId = entityId;
		this.impactPos = impactPos;
	}
	
	public STCEntityBloodImpactParticles(FriendlyByteBuf buf)
	{
		this.entityId = buf.readInt();
		this.impactPos = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
	}
	
	@Override
	public void encode(FriendlyByteBuf buf)
	{
		buf.writeInt(this.entityId);
		buf.writeDouble(this.impactPos.x);
		buf.writeDouble(this.impactPos.y);
		buf.writeDouble(this.impactPos.z);
	}
	
	@Override
	public void handle(Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			Minecraft minecraft = Minecraft.getInstance();
			Entity entity = minecraft.level.getEntity(this.entityId);
			ModNetworkManager.connection.physicalBloodImpactSfx(entity, this.impactPos);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
