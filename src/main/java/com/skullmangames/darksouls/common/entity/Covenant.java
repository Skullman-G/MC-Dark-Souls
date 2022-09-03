package com.skullmangames.darksouls.common.entity;

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
		if (this.rewards.length > 0)
		{
			if (!playerCap.getCovenant().is(this)) return this.rewards[0];
			for (Reward r : this.rewards)
			{
				if (playerCap.getCovenantProgress() < r.reqCount) return r;
			}
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
