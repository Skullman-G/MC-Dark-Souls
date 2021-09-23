package com.skullmangames.darksouls.common.block;

import com.skullmangames.darksouls.common.tileentity.LockableDoorTileEntity;
import com.skullmangames.darksouls.core.init.TileEntityTypeInit;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class LockableBlock extends Block
{
	public LockableBlock(Properties p_i48413_1_)
	{
		super(p_i48413_1_);
	}
	public BlockPos[] getPartPositions(World world, BlockPos blockpos)
	{
		return new BlockPos[] { blockpos };
	}
	
	public boolean isLocked(World world, BlockPos blockpos)
	{
		return ((LockableDoorTileEntity)world.getBlockEntity(blockpos)).isLocked();
	}
	
	public String getKeyName(World world, BlockPos blockpos)
	{
		return ((LockableDoorTileEntity)world.getBlockEntity(blockpos)).getKeyName();
	}
	
	@Override
	public boolean hasTileEntity(BlockState state)
	{
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		return TileEntityTypeInit.LOCKABLE_BLOCK.get().create();
	}
}
