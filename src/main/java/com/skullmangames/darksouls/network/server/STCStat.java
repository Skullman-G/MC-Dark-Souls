package com.skullmangames.darksouls.network.server;

import java.util.Map;
import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.common.entity.stats.StatHolder.ChangeRequest;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class STCStat
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
	
	public static STCStat fromBytes(FriendlyByteBuf buf)
	{
		return new STCStat(buf.readInt(), buf.readMap((b) -> b.readUtf(), (b) -> b.readInt()));
	}
	
	public static void toBytes(STCStat msg, FriendlyByteBuf buf)
	{
		buf.writeInt(msg.entityId);
		buf.writeMap(msg.changes, (b, s) -> b.writeUtf(s), (b, i) -> b.writeInt(i));
	}
	
	public static void handle(STCStat msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			Minecraft minecraft = Minecraft.getInstance();
			Entity entity = minecraft.player.level.getEntity(msg.entityId);
			if (entity == null) return;
			
			PlayerCap<?> entityCap = (PlayerCap<?>) entity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			if (entityCap == null) return;
			
			entityCap.getStats().requestChange().set(msg.changes).finish();
		});
		
		ctx.get().setPacketHandled(true);
	}
}
