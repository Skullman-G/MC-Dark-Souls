package com.skullmangames.darksouls.network.client;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.ServerPlayerCap;
import com.skullmangames.darksouls.common.entity.stats.Stats;
import com.skullmangames.darksouls.core.init.ModCriteriaTriggers;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCStat;

import net.minecraft.network.PacketBuffer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;

public class CTSLevelUp
{
	private int[] stats;
	
	public CTSLevelUp(int[] stats)
	{
		this.stats = stats;
	}
	
	public static CTSLevelUp fromBytes(PacketBuffer buf)
	{
		return new CTSLevelUp(buf.readVarIntArray());
	}
	
	public static void toBytes(CTSLevelUp msg, PacketBuffer buf)
	{
		buf.writeVarIntArray(msg.stats);
	}
	
	public static void handle(CTSLevelUp msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ServerPlayerEntity serverPlayer = ctx.get().getSender();
			ServerPlayerCap playerCap = (ServerPlayerCap) serverPlayer.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			if (playerCap == null) return;
			
			Stats playerstats = playerCap.getStats();
			for (int i = 0; i < Stats.STATS.size(); i++)
			{
				msg.stats[i] = Math.min(msg.stats[i], 99 - playerstats.getStatValue(i));
				if (!serverPlayer.isCreative()) msg.stats[i] = Math.max(msg.stats[i], 0);
			}
			
			int preLevel = playerCap.getSoulLevel();
			if (serverPlayer.isCreative())
			{
				addStatValues(serverPlayer, playerstats, msg.stats);
			}
			else
			{
				int level = playerCap.getSoulLevel();
				for (int add : msg.stats) level += add;
				int cost = Stats.getCost(level);
				
				if (cost <= playerCap.getSouls())
				{
					playerCap.raiseSouls(cost);
					addStatValues(serverPlayer, playerstats, msg.stats);
				}
			}
			
			int postLevel = playerCap.getSoulLevel();
			if (preLevel < postLevel) ModCriteriaTriggers.LEVEL_UP.trigger(serverPlayer, true);
		});
		
		ctx.get().setPacketHandled(true);
	}
	
	public static void addStatValues(ServerPlayerEntity player, Stats stats, int[] addition)
	{
		for (int i = 0; i < Stats.STATS.size(); i++)
		{
			int value = stats.getStatValue(i) + addition[i];
			stats.setStatValue(player, i, value);
			ModNetworkManager.sendToPlayer(new STCStat(player.getId(), i, value), player);
		}
	}
}
