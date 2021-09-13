package com.skullmangames.darksouls.common.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.skullmangames.darksouls.common.entity.stats.Stat;
import com.skullmangames.darksouls.common.entity.stats.Stats;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class WeaponItem extends SwordItem
{
	protected final Map<Stat, Integer> requiredStats = new HashMap<Stat, Integer>();
	
	public WeaponItem(IItemTier itemtier, int damage, float speed, Properties properties)
	{
		super(itemtier, damage, speed, properties);
	}
	
	public WeaponItem addStat(Stat stat, int value)
	{
		this.requiredStats.put(stat, value);
		return this;
	}
	
	public boolean meetRequirement(Stat stat, LivingEntity livingentity)
	{
		return this.getRequiredStat(stat) <= Stats.STRENGTH.getValue(livingentity);
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
	public UseAction getUseAnimation(ItemStack p_77661_1_)
	{
		return UseAction.BLOCK;
	}
	
	@Override
	public int getUseDuration(ItemStack p_77626_1_)
	{
	   return 72000;
	}
	
	@Override
	public ActionResult<ItemStack> use(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_)
	{
		ItemStack itemstack = p_77659_2_.getItemInHand(p_77659_3_);
	    p_77659_2_.startUsingItem(p_77659_3_);
	    return ActionResult.consume(itemstack);
	}
	
	@Override
	public void appendHoverText(ItemStack p_77624_1_, World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_) {}
}
