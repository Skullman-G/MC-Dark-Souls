package com.skullmangames.darksouls.network.packets.server;

import java.util.Map;
import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.common.entity.stats.StatHolder.ChangeRequest;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.network.packets.NetworkPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class STCStat implements NetworkPacket
{
	private int entityId;
	private Map<String, Integer> changes;
	
	public STCStat(int entityid, ChangeRequest changes)
	{
		this(entityid, changes.getChanges());
	}
	
	public STCStat(int entityid, Map<String, Integer> changes)
	{
		this.entityId = entityid;
		this.changes = changes;
	}
	
	public STCStat(FriendlyByteBuf buf)
	{
		this.entityId = buf.readInt();
		this.changes = buf.readMap((b) -> b.readUtf(), (b) -> b.readInt());
	}
	
	@Override
	public void encode(FriendlyByteBuf buf)
	{
		buf.writeInt(this.entityId);
		buf.writeMap(this.changes, (b, s) -> b.writeUtf(s), (b, i) -> b.writeInt(i));
	}
	
	@Override
	public void handle(Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			Minecraft minecraft = Minecraft.getInstance();
			Entity entity = minecraft.player.level.getEntity(this.entityId);
			if (entity == null) return;
			
			PlayerCap<?> entityCap = (PlayerCap<?>) entity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			if (entityCap == null) return;
			
			entityCap.getStats().requestChange().set(this.changes).finish();
		});
		
		ctx.get().setPacketHandled(true);
	}
}
