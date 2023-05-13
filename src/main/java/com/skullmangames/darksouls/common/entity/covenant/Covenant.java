package com.skullmangames.darksouls.common.entity.covenant;

import com.skullmangames.darksouls.common.capability.entity.PlayerCap;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class Covenant
{
	private final String name;
	private final Reward[] rewards;
	
	public Covenant(String name, Reward... rewards)
	{
		this.name = "covenant.darksouls."+name;
		this.rewards = rewards;
	}
	
	public boolean is(Covenant covenant)
	{
		return this.name == covenant.name;
	}
	
	@Override
	public String toString()
	{
		return this.name;
	}
	
	public String getRegistryName()
	{
		return new TranslatableComponent(this.name).getString();
	}
	
	public String getDescription()
	{
		return new TranslatableComponent(this.name+".description").getString();
	}
	
	public Reward[] getRewards()
	{
		return this.rewards;
	}
	
	public Reward getNextReward(PlayerCap<?> playerCap)
	{
		for (Reward r : this.rewards)
		{
			int progress = playerCap.getLastProgressFor(this);
			if (!playerCap.getCovenant().is(this) && progress == 0) return r;
			if (progress < r.reqCount) return r;
		}
		return null;
	}
	
	public int getProgressTillNextReward(PlayerCap<?> playerCap)
	{
		Reward reward = this.getNextReward(playerCap);
		if (reward == null) return 0;
		return reward.reqCount - playerCap.getCovenantProgress();
	}
	
	public static class Reward
	{
		private final int reqCount;
		private final Item reqItem;
		private final ItemStack reward;
		
		public Reward(int reqCount, Item reqItem, ItemStack reward)
		{
			this.reqCount = reqCount;
			this.reqItem = reqItem;
			this.reward = reward;
		}
		
		public int getReqCount()
		{
			return reqCount;
		}
		
		public Item getReqItem()
		{
			return reqItem;
		}
		
		public ItemStack getRewardItem()
		{
			return reward;
		}
	}
}
