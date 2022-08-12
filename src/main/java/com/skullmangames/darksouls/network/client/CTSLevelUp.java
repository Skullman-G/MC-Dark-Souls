package com.skullmangames.darksouls.network.client;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.ServerPlayerCap;
import com.skullmangames.darksouls.common.entity.stats.Stats;
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
			ServerPlayerCap playerdata = (ServerPlayerCap) serverPlayer.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			if (playerdata == null) return;
			
			Stats playerstats = playerdata.getStats();
			for (int i = 0; i < Stats.STATS.size(); i++)
			{
				msg.stats[i] = Math.min(msg.stats[i], 99 - playerstats.getStatValue(i));
				if (!serverPlayer.isCreative()) msg.stats[i] = Math.max(msg.stats[i], 0);
			}
			
			if (serverPlayer.isCreative())
			{
				addStatValues(serverPlayer, playerstats, msg.stats);
			}
			else
			{
				int level = playerdata.getSoulLevel();
				for (int add : msg.stats) level += add;
				int cost = Stats.getCost(level);
				
				if (cost <= playerdata.getSouls())
				{
					playerdata.raiseSouls(cost);
					addStatValues(serverPlayer, playerstats, msg.stats);
				}
			}
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
