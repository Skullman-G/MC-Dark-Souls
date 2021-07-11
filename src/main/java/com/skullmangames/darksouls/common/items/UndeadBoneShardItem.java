package com.skullmangames.darksouls.common.items;

import com.skullmangames.darksouls.common.blocks.BonfireBlock;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class UndeadBoneShardItem extends DescriptionItem
{
	public UndeadBoneShardItem(Properties properties)
	{
		super(properties);
	}
	
	@Override
	public ActionResultType useOn(ItemUseContext itemusecontext)
	{
		World world = itemusecontext.getLevel();
		BlockPos blockpos = itemusecontext.getClickedPos();
		BlockState blockstate = world.getBlockState(blockpos);
		ItemStack itemstack = itemusecontext.getItemInHand();
		if (blockstate.getBlock() instanceof BonfireBlock && blockstate.getValue(BonfireBlock.LIT))
		{
			((BonfireBlock)blockstate.getBlock()).raiseEstusHealLevel(world, blockstate, blockpos);
			if (!itemusecontext.getPlayer().abilities.instabuild)
			{
				itemstack.shrink(1);
	        }
			return ActionResultType.SUCCESS;
		}
		
		return ActionResultType.PASS;
	}
}
