package com.skullmangames.darksouls.common.blocks;

import com.skullmangames.darksouls.common.containers.SmithingTableContainerOverride;

import net.minecraft.block.BlockState;
import net.minecraft.block.SmithingTableBlock;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class SmithingTableBlockOverride extends SmithingTableBlock
{
	private static final ITextComponent CONTAINER_TITLE = new TranslationTextComponent("container.upgrade");
	
	public SmithingTableBlockOverride(Properties properties)
	{
		super(properties);
	}
	
	@Override
	public INamedContainerProvider getMenuProvider(BlockState p_220052_1_, World p_220052_2_, BlockPos p_220052_3_)
	{
		System.out.print("providing");
		return new SimpleNamedContainerProvider((p_235576_2_, p_235576_3_, p_235576_4_) ->
		{
	         return new SmithingTableContainerOverride(p_235576_2_, p_235576_3_, IWorldPosCallable.create(p_220052_2_, p_220052_3_));
	    }, CONTAINER_TITLE);
	}
}
