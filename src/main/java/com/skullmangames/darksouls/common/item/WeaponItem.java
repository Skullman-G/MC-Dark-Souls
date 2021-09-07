package com.skullmangames.darksouls.common.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class WeaponItem extends SwordItem
{
	public WeaponItem(IItemTier p_i48460_1_, int p_i48460_2_, float p_i48460_3_, Properties p_i48460_4_)
	{
		super(p_i48460_1_, p_i48460_2_, p_i48460_3_, p_i48460_4_);
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
}
