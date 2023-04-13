package com.skullmangames.darksouls.network.client;

import java.util.Map;
import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.ServerPlayerCap;
import com.skullmangames.darksouls.common.entity.stats.Stats;
import com.skullmangames.darksouls.core.init.ModCriteriaTriggers;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCStat;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class CTSLevelUp
{
	private Map<String, Integer> addition;
	
	public CTSLevelUp(Map<String, Integer> addition)
	{
		this.addition = addition;
	}
	
	public static CTSLevelUp fromBytes(FriendlyByteBuf buf)
	{
		return new CTSLevelUp(buf.readMap((b) -> b.readUtf(), (b) -> b.readInt()));
	}
	
	public static void toBytes(CTSLevelUp msg, FriendlyByteBuf buf)
	{
		buf.writeMap(msg.addition, (b, s) -> b.writeUtf(s), (b, i) -> b.writeInt(i));
	}
	
	public static void handle(CTSLevelUp msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ServerPlayer serverPlayer = ctx.get().getSender();
			ServerPlayerCap playerCap = (ServerPlayerCap) serverPlayer.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			if (playerCap == null) return;
			
			Stats playerstats = playerCap.getStats();
			for (String stat : Stats.STATS.keySet())
			{
				msg.addition.put(stat, Math.min(msg.addition.get(stat), 99 - playerstats.getStatValue(stat)));
				if (!serverPlayer.isCreative()) msg.addition.put(stat, Math.max(msg.addition.get(stat), 0));
			}
			
			int preLevel = playerCap.getSoulLevel();
			int postLevel = preLevel;
			if (serverPlayer.isCreative())
			{
				addStatValues(serverPlayer, playerstats, msg.addition);
				postLevel = playerCap.getSoulLevel();
			}
			else
			{
				for (int add : msg.addition.values()) postLevel += add;
				
				int cost = 0;
				for (int i = preLevel; i < postLevel; i++)
				{
					cost += Stats.getCost(i);
				}
				
				if (preLevel < postLevel && cost <= playerCap.getSouls())
				{
					playerCap.raiseSouls(-cost);
					addStatValues(serverPlayer, playerstats, msg.addition);
				}
			}
			
			if (preLevel < postLevel) ModCriteriaTriggers.LEVEL_UP.trigger(serverPlayer, true);
		});
		
		ctx.get().setPacketHandled(true);
	}
	
	public static void addStatValues(ServerPlayer player, Stats stats, Map<String, Integer> addition)
	{
		for (String stat : Stats.STATS.keySet())
		{
			int value = stats.getStatValue(stat) + addition.get(stat);
			stats.setStatValue(player, stat, value);
			ModNetworkManager.sendToPlayer(new STCStat(player.getId(), stat, value), player);
		}
	}
}
