package com.skullmangames.darksouls.common.item;

import java.util.List;
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
	public WeaponItem(Tier itemtier, int damage, float speed, Properties properties)
	{
		super(itemtier, damage, speed, properties);
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
