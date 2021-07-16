package com.skullmangames.darksouls.common.items;

import com.skullmangames.darksouls.common.blocks.BonfireBlock;
import com.skullmangames.darksouls.core.init.CriteriaTriggerInit;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
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
			BonfireBlock bonfire = (BonfireBlock)blockstate.getBlock();
			if (blockstate.getValue(BonfireBlock.ESTUS_HEAL_LEVEL) == 9)
			{
				if (itemusecontext.getPlayer() instanceof ServerPlayerEntity)
				{
					CriteriaTriggerInit.MAX_ESTUS_HEAL_LEVEL_TRIGGER.trigger((ServerPlayerEntity)itemusecontext.getPlayer());
				}
			}
			bonfire.raiseEstusHealLevel(world, blockstate, blockpos);
			if (!itemusecontext.getPlayer().abilities.instabuild)
			{
				itemstack.shrink(1);
	        }
			return ActionResultType.SUCCESS;
		}
		
		return ActionResultType.PASS;
	}
}