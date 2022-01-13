package com.skullmangames.darksouls.common.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.skullmangames.darksouls.common.capability.entity.PlayerData;
import com.skullmangames.darksouls.common.entity.stats.Stat;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class WeaponItem extends SwordItem
{
	protected final Map<Stat, Integer> requiredStats = new HashMap<Stat, Integer>();
	
	public WeaponItem(Tier itemtier, int damage, float speed, Properties properties)
	{
		super(itemtier, damage, speed, properties);
	}
	
	public WeaponItem addStat(Stat stat, int value)
	{
		this.requiredStats.put(stat, value);
		return this;
	}
	
	public boolean meetRequirement(Stat stat, PlayerData<?> playerdata)
	{
		return this.getRequiredStat(stat) <= playerdata.getStats().getStatValue(stat);
	}
	
	public boolean meetRequirements(PlayerData<?> playerdata)
	{
		for (Stat stat : this.requiredStats.keySet()) if (!this.meetRequirement(stat, playerdata)) return false;
		return true;
	}
	
	public int getRequiredStat(Stat stat)
	{
		return this.requiredStats.getOrDefault(stat, 0);
	}
	
	public Map<Stat, Integer> getRequiredStats()
	{
		return this.requiredStats;
	}
	
	@Override
	public UseAnim getUseAnimation(ItemStack p_77661_1_)
	{
		return UseAnim.BLOCK;
	}
	
	@Override
	public int getUseDuration(ItemStack p_77626_1_)
	{
	   return 72000;
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level p_77659_1_, Player p_77659_2_, InteractionHand p_77659_3_)
	{
		ItemStack itemstack = p_77659_2_.getItemInHand(p_77659_3_);
	    p_77659_2_.startUsingItem(p_77659_3_);
	    return InteractionResultHolder.consume(itemstack);
	}
	
	@Override
	public void appendHoverText(ItemStack p_77624_1_, Level p_77624_2_, List<Component> p_77624_3_, TooltipFlag p_77624_4_) {}
}
