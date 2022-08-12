package com.skullmangames.darksouls.common.item;

import com.skullmangames.darksouls.common.block.BonfireBlock;
import com.skullmangames.darksouls.common.blockentity.BonfireBlockEntity;
import com.skullmangames.darksouls.core.init.ModCriteriaTriggers;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class UndeadBoneShardItem extends Item
{
	public UndeadBoneShardItem(Properties properties)
	{
		super(properties);
	}
	
	@Override
	public InteractionResult useOn(UseOnContext itemusecontext)
	{
		Level level = itemusecontext.getLevel();
		BlockPos blockpos = itemusecontext.getClickedPos();
		BlockState blockstate = level.getBlockState(blockpos);
		ItemStack itemstack = itemusecontext.getItemInHand();
		if (level.getBlockEntity(blockpos) instanceof BonfireBlockEntity && blockstate.getValue(BonfireBlock.LIT))
		{
			BonfireBlockEntity bonfire = (BonfireBlockEntity)level.getBlockEntity(blockpos);
			if (blockstate.getValue(BonfireBlock.ESTUS_HEAL_LEVEL) == 9)
			{
				if (itemusecontext.getPlayer() instanceof ServerPlayer)
				{
					ModCriteriaTriggers.MAX_ESTUS_HEAL_LEVEL_TRIGGER.trigger((ServerPlayer)itemusecontext.getPlayer());
				}
			}
			bonfire.raiseEstusHealLevel();
			if (!itemusecontext.getPlayer().getAbilities().instabuild)
			{
				itemstack.shrink(1);
	        }
			return InteractionResult.SUCCESS;
		}
		
		return InteractionResult.PASS;
	}
}
