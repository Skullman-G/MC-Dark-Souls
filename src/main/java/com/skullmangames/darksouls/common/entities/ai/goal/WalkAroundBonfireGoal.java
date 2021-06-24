package com.skullmangames.darksouls.common.entities.ai.goal;

import java.util.Random;

import com.skullmangames.darksouls.common.blocks.BonfireBlock;
import com.skullmangames.darksouls.common.entities.FireKeeperEntity;
import com.skullmangames.darksouls.common.tiles.BonfireTileEntity;

import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WalkAroundBonfireGoal extends WaterAvoidingRandomWalkingGoal
{
	private BlockPos targetBonfirePos;
	private boolean mustLightBonfire;
	
	public WalkAroundBonfireGoal(FireKeeperEntity entity, double speedmodifier)
	{
		super(entity, speedmodifier);
		this.mustLightBonfire = false;
	}
	
	@Override
	public boolean canUse()
	{
		Random random = this.mob.getRandom();
		if (!((FireKeeperEntity)this.mob).hasLinkedBonfire())
		{
			if (this.searchForBonfire(this.mob.level))
			{
				this.wantedX = this.targetBonfirePos.getX() + 1 - random.nextInt(2);
				this.wantedY = this.targetBonfirePos.getY();
				this.wantedZ = this.targetBonfirePos.getZ() + 1 - random.nextInt(2);
				this.mustLightBonfire = true;
			}
			else
			{
				return super.canUse();
			}
		}
		else
		{
			if (this.targetBonfirePos == null)
			{
				this.targetBonfirePos = ((FireKeeperEntity)this.mob).getLinkedBonfire();
			}
			
			this.wantedX = this.targetBonfirePos.getX() + 5 - random.nextInt(10);
			this.wantedY = this.targetBonfirePos.getY();
			this.wantedZ = this.targetBonfirePos.getZ() + 5 - random.nextInt(10);
		}
		
		return true;
	}
	
	@Override
	public void start()
	{
		this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
	}
	
	private boolean searchForBonfire(World world)
	{
		for (int x = this.mob.blockPosition().getX() - 16; x < this.mob.blockPosition().getX() + 16; x++)
		{
			for (int z = this.mob.blockPosition().getZ() - 16; z < this.mob.blockPosition().getZ() + 16; z++)
			{
				for (int y = this.mob.blockPosition().getY() - 16; y < this.mob.blockPosition().getY() + 16; y++)
				{
					if (world.getBlockState(new BlockPos(x, y, z)).getBlock() instanceof BonfireBlock)
					{
						BonfireTileEntity tileentity = (BonfireTileEntity)this.mob.level.getBlockEntity(new BlockPos(x, y, z));
						if (!tileentity.hasFireKeeper() && !tileentity.getBlock().isLit(tileentity.getBlockState()))
						{
							this.targetBonfirePos = new BlockPos(x, y, z);
							return true;
						}
					}
				}
			}
		}
		
		return false;
	}
	
	@Override
	public void tick()
	{
		if (this.mustLightBonfire)
		{
			BonfireTileEntity tileentity = (BonfireTileEntity)this.mob.level.getBlockEntity(this.targetBonfirePos);
			if (tileentity.hasFireKeeper() || tileentity.getBlock().isLit(tileentity.getBlockState()))
			{
				this.mustLightBonfire = false;
				this.targetBonfirePos = null;
			}
			else if (this.mob.blockPosition().getX() <= this.targetBonfirePos.getX() + 1 || this.mob.blockPosition().getX() >= this.targetBonfirePos.getX() - 1)
			{
				if (this.mob.blockPosition().getZ() <= this.targetBonfirePos.getZ() + 1 || this.mob.blockPosition().getZ() >= this.targetBonfirePos.getZ() - 1)
				{
					if (this.mob.blockPosition().getY() == this.targetBonfirePos.getZ() || this.mob.blockPosition().getZ() == this.targetBonfirePos.getZ())
					{
						((FireKeeperEntity)this.mob).linkBonfire(this.targetBonfirePos, tileentity);
						this.mustLightBonfire = false;
					}
				}
			}
		}
	}
}
