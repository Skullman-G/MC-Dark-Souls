package com.skullmangames.darksouls.network.packets.client;

import java.util.Map;
import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.ServerPlayerCap;
import com.skullmangames.darksouls.common.entity.stats.StatHolder;
import com.skullmangames.darksouls.common.entity.stats.StatHolder.ChangeRequest;
import com.skullmangames.darksouls.common.entity.stats.Stats;
import com.skullmangames.darksouls.core.init.ModCriteriaTriggers;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.packets.NetworkPacket;
import com.skullmangames.darksouls.network.packets.server.STCStat;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class CTSLevelUp implements NetworkPacket
{
	private Map<String, Integer> addition;
	
	public CTSLevelUp(Map<String, Integer> addition)
	{
		this.addition = addition;
	}
	
	public CTSLevelUp(FriendlyByteBuf buf)
	{
		this.addition = buf.readMap((b) -> b.readUtf(), (b) -> b.readInt());
	}
	
	@Override
	public void encode(FriendlyByteBuf buf)
	{
		buf.writeMap(this.addition, (b, s) -> b.writeUtf(s), (b, i) -> b.writeInt(i));
	}
	
	@Override
	public void handle(Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ServerPlayer serverPlayer = ctx.get().getSender();
			ServerPlayerCap playerCap = (ServerPlayerCap) serverPlayer.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			if (playerCap == null) return;
			
			StatHolder playerstats = playerCap.getStats();
			for (String stat : Stats.STATS.keySet())
			{
				this.addition.put(stat, Math.min(this.addition.get(stat), 99 - playerstats.getStatValue(stat)));
				if (!serverPlayer.isCreative()) this.addition.put(stat, Math.max(this.addition.get(stat), 0));
			}
			
			int preLevel = playerCap.getSoulLevel();
			int postLevel = preLevel;
			if (serverPlayer.isCreative())
			{
				addStatValues(serverPlayer, playerstats, this.addition);
				postLevel = playerCap.getSoulLevel();
			}
			else
			{
				for (int add : this.addition.values()) postLevel += add;
				
				int cost = 0;
				for (int i = preLevel; i < postLevel; i++)
				{
					cost += Stats.getCost(i);
				}
				
				if (preLevel < postLevel && cost <= playerCap.getSouls())
				{
					playerCap.raiseSouls(-cost);
					addStatValues(serverPlayer, playerstats, this.addition);
				}
			}
			
			if (preLevel < postLevel) ModCriteriaTriggers.LEVEL_UP.trigger(serverPlayer, true);
		});
		
		ctx.get().setPacketHandled(true);
	}
	
	public static void addStatValues(ServerPlayer player, StatHolder stats, Map<String, Integer> addition)
	{
		ChangeRequest request = stats.requestChange();
		for (String stat : Stats.STATS.keySet())
		{
			int value = stats.getStatValue(stat) + addition.get(stat);
			request.set(stat, value);
		}
		ModNetworkManager.sendToPlayer(new STCStat(player.getId(), request), player);
		request.finish();
	}
}
