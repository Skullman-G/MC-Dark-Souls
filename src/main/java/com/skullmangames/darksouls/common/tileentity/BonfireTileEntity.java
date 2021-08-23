package com.skullmangames.darksouls.common.tileentity;

import java.util.Random;
import javax.annotation.Nullable;

import com.skullmangames.darksouls.common.block.BonfireBlock;
import com.skullmangames.darksouls.common.entity.FireKeeperEntity;
import com.skullmangames.darksouls.core.init.CriteriaTriggerInit;
import com.skullmangames.darksouls.core.init.EntityTypeInit;
import com.skullmangames.darksouls.core.init.ItemInit;
import com.skullmangames.darksouls.core.init.SoundEvents;
import com.skullmangames.darksouls.core.init.TileEntityTypeInit;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.WorldEntitySpawner;

public class BonfireTileEntity extends TileEntity implements ITickableTileEntity
{
	private String name = "";
	private boolean hasFireKeeper;
	private boolean needsFireKeeper;
	private String fireKeeperStringUUID = "";
	
	public BonfireTileEntity(TileEntityType<?> tileEntityTypeIn) 
	{
		super(tileEntityTypeIn);
	}
	
	public BonfireTileEntity() 
	{
		this(TileEntityTypeInit.BONFIRE_TILE_ENTITY.get());
	}
	
	@Override
	public void onLoad()
	{
		super.onLoad();
		this.needsFireKeeper = this.getLevel().getRandom().nextBoolean();
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
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket()
	{
		CompoundNBT nbt = this.save(new CompoundNBT());
		return new SUpdateTileEntityPacket(this.worldPosition, -1, nbt);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
	{
		CompoundNBT nbt = pkt.getTag();
		this.load(getBlockState(), nbt);
		this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 2);
	}
	
	public void setName(String value)
	{
		this.name = value;
		this.setChanged();
	}
	
	public BonfireBlock getBlock()
	{
		return (BonfireBlock)this.getBlockState().getBlock();
	}
	
	public void setLit(@Nullable PlayerEntity player, boolean value)
	{
		if (this.getBlock().isLit(this.getBlockState()) != value)
		{
			this.getBlock().setLit(this.level, this.getBlockState(), this.worldPosition, value);
			
			if (value)
			{
				this.level.playSound(null, this.worldPosition, SoundEvents.BONFIRE_LIT, SoundCategory.BLOCKS, 1.0F, 1.0F);
				
				if (player != null)
				{
					ServerPlayerEntity serverplayer = player.getServer().getPlayerList().getPlayer(player.getUUID());
					serverplayer.connection.send(new STitlePacket(STitlePacket.Type.TITLE, new TranslationTextComponent("gui.darksouls.bonfire_lit_message")));
					CriteriaTriggerInit.BONFIRE_LIT.trigger(serverplayer, this.getBlockState().getValue(BonfireBlock.LIT));
				}
			}
		}
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public void kindle()
	{
		if (!this.hasFireKeeper)
		{
			this.level.playSound(null, this.worldPosition, SoundEvents.BONFIRE_LIT, SoundCategory.BLOCKS, 1.0F, 1.0F);
		}
		this.getBlock().kindle(this.level, this.getBlockState(), this.worldPosition);
	}
	
	public void raiseEstusHealLevel()
	{
		this.level.playSound(null, this.worldPosition, SoundEvents.BONFIRE_LIT, SoundCategory.BLOCKS, 1.0F, 1.0F);
		this.getBlock().raiseEstusHealLevel(this.level, this.getBlockState(), this.worldPosition);
	}
	
	public boolean hasName()
	{
		return this.name != "";
	}
	
	public void addFireKeeper(String uuid)
	{
		this.fireKeeperStringUUID = uuid;
		this.setLit(null, true);
		this.hasFireKeeper = true;
		this.kindle();
		this.setChanged();
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
			ServerWorld serverworld = (ServerWorld)this.level;
			Random random = serverworld.random;
			
			if (this.needsFireKeeper && !this.hasFireKeeper && !this.getBlock().isLit(this.getBlockState()))
			{
				int i = (random.nextInt(10)) * (random.nextBoolean() ? -1 : 1);
	            int j = ( random.nextInt(10)) * (random.nextBoolean() ? -1 : 1);
				BlockPos blockpos = this.worldPosition.offset(i, 0, j);
				if (WorldEntitySpawner.isSpawnPositionOk(EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, serverworld, blockpos, EntityTypeInit.FIRE_KEEPER.get()))
				{
					FireKeeperEntity entity = EntityTypeInit.FIRE_KEEPER.get().create(serverworld);
					if (entity != null)
					{
						entity.linkBonfire(this.worldPosition);
						entity.finalizeSpawn(serverworld, serverworld.getCurrentDifficultyAt(blockpos), SpawnReason.NATURAL, (ILivingEntityData)null, (CompoundNBT)null);
				        entity.moveTo(blockpos, 0.0F, 0.0F);
						serverworld.addFreshEntityWithPassengers(entity);
						this.needsFireKeeper = false;
					}
				}
			}
			
			if (this.ticktimer >= 1000)
			{
				this.ticktimer = 0;
				
				if (random.nextInt(10) == 1 && this.getBlock().isLit(this.getBlockState()))
				{
					int i = (random.nextInt(1)) * (random.nextBoolean() ? -1 : 1);
			        int j = ( random.nextInt(1)) * (random.nextBoolean() ? -1 : 1);
					BlockPos blockpos = this.worldPosition.offset(i, this.worldPosition.getZ(), j);
					ItemEntity homewardbone = new ItemEntity(serverworld, blockpos.getX(), blockpos.getY(), blockpos.getZ(), new ItemStack(ItemInit.HOMEWARD_BONE.get()));
					serverworld.addFreshEntity(homewardbone);
				}
			}
			else
			{
				this.ticktimer++;
			}
		}
	}
}
