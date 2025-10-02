package com.skullmangames.darksouls.network.packets.server;

import java.util.Collection;
import java.util.function.Supplier;

import com.skullmangames.darksouls.core.util.ExtendedDamageSource.CoreDamageType;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.packets.NetworkPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

public class STCEntityImpactParticles implements NetworkPacket
{
	private int entityId;
	private Vec3 impactPos;
	private boolean blocked;
	private Collection<CoreDamageType> damageTypes;
	
	public STCEntityImpactParticles(int entityId, Vec3 impactPos, boolean blocked, Collection<CoreDamageType> damageTypes)
	{
		this.entityId = entityId;
		this.impactPos = impactPos;
		this.blocked = blocked;
		this.damageTypes = damageTypes;
	}
	
	public STCEntityImpactParticles(FriendlyByteBuf buf)
	{
		this.entityId = buf.readInt();
		this.impactPos = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
		this.blocked = buf.readBoolean();
		this.damageTypes = buf.readList(b -> b.readEnum(CoreDamageType.class));
	}
	
	@Override
	public void encode(FriendlyByteBuf buf)
	{
		buf.writeInt(this.entityId);
		buf.writeDouble(this.impactPos.x);
		buf.writeDouble(this.impactPos.y);
		buf.writeDouble(this.impactPos.z);
		buf.writeBoolean(this.blocked);
		buf.writeCollection(this.damageTypes, (b, type) -> b.writeEnum(type));
	}
	
	@Override
	public void handle(Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			Minecraft minecraft = Minecraft.getInstance();
			Entity entity = minecraft.level.getEntity(this.entityId);
			ModNetworkManager.connection.impactSfx(entity, this.impactPos, this.blocked, this.damageTypes);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
