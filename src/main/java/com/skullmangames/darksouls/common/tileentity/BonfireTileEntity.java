package com.skullmangames.darksouls.common.tileentity;

import java.util.Random;

import com.skullmangames.darksouls.common.block.BonfireBlock;
import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.core.init.ModSoundEvents;
import com.skullmangames.darksouls.core.init.ModTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class BonfireTileEntity extends TileEntity implements ITickableTileEntity
{
	private String name = "";
	private boolean hasFireKeeper;
	private String fireKeeperStringUUID = "";

	public BonfireTileEntity()
	{
		super(ModTileEntities.BONFIRE.get());
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt)
	{
		super.save(nbt);
		nbt.putString("name", this.name);
		nbt.putBoolean("has_fire_keeper", this.hasFireKeeper);
		nbt.putString("fire_keeper_string_uuid", this.fireKeeperStringUUID);
		return nbt;
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt)
	{
		super.load(state, nbt);
		this.name = nbt.getString("name");
		this.hasFireKeeper = nbt.getBoolean("has_fire_keeper");
		this.fireKeeperStringUUID = nbt.getString("fire_keeper_string_uuid");
	}
	
	public void loadOnClient(String name)
	{
		this.name = name;
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket()
	{
		CompoundNBT nbt = this.save(new CompoundNBT());
		return new SUpdateTileEntityPacket(this.worldPosition, -1, nbt);
	}
	
	@Override
	public CompoundNBT getUpdateTag()
	{
		return this.save(super.getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
	{
		this.load(this.getBlockState(), pkt.getTag());
		this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 2);
	}

	private void markDirty()
	{
		this.setChanged();
		this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2);
	}

	public void setName(String value)
	{
		this.name = value;
		this.markDirty();
	}

	public void setLit(boolean value)
	{
		if (this.getBlockState().getValue(BonfireBlock.LIT) == value) return;
		if (value) this.playKindleSound();
		this.level.setBlock(this.worldPosition, this.getBlockState().setValue(BonfireBlock.LIT, value), 3);
	}

	public String getName()
	{
		return this.name;
	}

	public void kindle()
	{
		int volumelevel = this.getBlockState().getValue(BonfireBlock.ESTUS_VOLUME_LEVEL);
		if (volumelevel >= 4)
			return;
		this.playKindleSound();
		level.setBlock(this.worldPosition, this.getBlockState().setValue(BonfireBlock.ESTUS_VOLUME_LEVEL, volumelevel + 1), 3);
	}

	private void playKindleSound()
	{
		this.level.playSound(null, this.worldPosition, ModSoundEvents.BONFIRE_LIT.get(), SoundCategory.BLOCKS, 1.0F, 1.0F);
	}

	public void raiseEstusHealLevel()
	{
		int heallevel = this.getBlockState().getValue(BonfireBlock.ESTUS_HEAL_LEVEL);
		if (heallevel >= 10) return;
		this.playKindleSound();
		this.level.setBlock(this.worldPosition, this.getBlockState().setValue(BonfireBlock.ESTUS_HEAL_LEVEL, heallevel + 1), 3);
	}

	public boolean hasName()
	{
		return this.name != "";
	}

	public void addFireKeeper(String uuid)
	{
		this.fireKeeperStringUUID = uuid;
		this.setLit(true);
		this.hasFireKeeper = true;
		this.kindle();
		this.markDirty();
	}

	public boolean hasFireKeeper()
	{
		return this.hasFireKeeper;
	}

	public String getFireKeeperStringUUID()
	{
		return this.fireKeeperStringUUID;
	}

	private int ticktimer;

	@Override
	public void tick()
	{
		if (this.level instanceof ServerWorld)
		{
			ServerWorld serverworld = (ServerWorld) this.level;
			Random random = serverworld.random;

			if (this.ticktimer >= 1000)
			{
				this.ticktimer = 0;

				if (random.nextInt(10) == 1 && this.getBlockState().getValue(BonfireBlock.LIT))
				{
					int i = (random.nextInt(1)) * (random.nextBoolean() ? -1 : 1);
					int j = (random.nextInt(1)) * (random.nextBoolean() ? -1 : 1);
					BlockPos blockpos = this.worldPosition.offset(i, this.worldPosition.getZ(), j);
					ItemEntity homewardbone = new ItemEntity(serverworld, blockpos.getX(), blockpos.getY(), blockpos.getZ(),
							new ItemStack(ModItems.HOMEWARD_BONE.get()));
					serverworld.addFreshEntity(homewardbone);
				}
			} else
			{
				this.ticktimer++;
			}
		}
	}
}
