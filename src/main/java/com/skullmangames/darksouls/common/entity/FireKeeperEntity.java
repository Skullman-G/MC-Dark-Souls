package com.skullmangames.darksouls.common.entity;

import java.util.ArrayList;
import java.util.List;

import com.skullmangames.darksouls.common.entity.ai.goal.WalkAroundBonfireGoal;
import com.skullmangames.darksouls.common.inventory.container.ReinforceEstusFlaskContainer;
import com.skullmangames.darksouls.common.tileentity.BonfireTileEntity;
import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.network.ModNetworkManager;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

public class FireKeeperEntity extends QuestEntity
{
	private BlockPos linkedBonfirePos;
	private boolean hasLinkedBonfire = false;

	public FireKeeperEntity(EntityType<? extends QuestEntity> entity, World level)
	{
		super(entity, level);
	}
	
	@Override
	public ILivingEntityData finalizeSpawn(IServerWorld world, DifficultyInstance difficulty, SpawnReason reason,
			ILivingEntityData data, CompoundNBT nbt)
	{
		if (!this.hasLinkedBonfire)
		{
			for (TileEntity t : this.level.blockEntityList)
			{
				if (t instanceof BonfireTileEntity
						&& !((BonfireTileEntity) t).hasFireKeeper()
						&& t.getBlockPos().distSqr(this.getX(), this.getY(), this.getZ(), false) <= 1000)
				{
					this.linkBonfire(t.getBlockPos());
					break;
				}
			}
		}
		
		return super.finalizeSpawn(world, difficulty, reason, data, nbt);
	}

	private void linkBonfire(BlockPos blockpos)
	{
		this.linkedBonfirePos = blockpos;
		this.getLinkedBonfire().addFireKeeper(this.stringUUID);
		this.hasLinkedBonfire = true;
	}

	public BlockPos getLinkedBonfirePos()
	{
		return this.linkedBonfirePos;
	}

	public boolean hasLinkedBonfire()
	{
		return this.hasLinkedBonfire;
	}

	public BonfireTileEntity getLinkedBonfire()
	{
		TileEntity tileentity = this.level.getBlockEntity(this.linkedBonfirePos);
		return tileentity instanceof BonfireTileEntity
				? (BonfireTileEntity) tileentity
				: null;
	}

	public static AttributeModifierMap.MutableAttribute createAttributes()
	{
		return MobEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.MOVEMENT_SPEED, 0.15D);
	}

	@Override
	protected void registerGoals()
	{
		this.goalSelector.addGoal(0, new SwimGoal(this));
		this.goalSelector.addGoal(1, new WalkAroundBonfireGoal(this, 1.0D));
		this.goalSelector.addGoal(2, new LookAtGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.addGoal(3, new LookRandomlyGoal(this));
	}

	@Override
	public boolean removeWhenFarAway(double p_213397_1_)
	{
		return !this.hasLinkedBonfire;
	}

	@Override
	public void addAdditionalSaveData(CompoundNBT nbt)
	{
		super.addAdditionalSaveData(nbt);
		nbt.putInt("LinkedBonfireX", this.linkedBonfirePos.getX());
		nbt.putInt("LinkedBonfireY", this.linkedBonfirePos.getY());
		nbt.putInt("LinkedBonfireZ", this.linkedBonfirePos.getZ());
		nbt.putBoolean("HasLinkedBonfire", this.hasLinkedBonfire);
		nbt.putString("QuestPath", this.getCurrentQuestPath());
	}

	@Override
	public void readAdditionalSaveData(CompoundNBT nbt)
	{
		super.readAdditionalSaveData(nbt);
		this.linkedBonfirePos = new BlockPos(nbt.getInt("LinkedBonfireX"), nbt.getInt("LinkedBonfireY"), nbt.getInt("LinkedBonfireZ"));
		this.hasLinkedBonfire = nbt.getBoolean("HasLinkedBonfire");
		this.setCurrentQuestPath(nbt.getString("QuestPath"));
	}

	@Override
	public void tick()
	{
		super.tick();
		
		if (!this.hasLinkedBonfire)
		{
			for (TileEntity t : this.level.blockEntityList)
			{
				if (t instanceof BonfireTileEntity
						&& !((BonfireTileEntity) t).hasFireKeeper()
						&& t.getBlockPos().distSqr(this.getX(), this.getY(), this.getZ(), false) <= 1000)
				{
					this.linkBonfire(t.getBlockPos());
					break;
				}
			}
		}
		
		if (!this.level.isClientSide() && !this.isDeadOrDying())
		{
			if (this.hasLinkedBonfire && this.getLinkedBonfire() == null)
			{
				this.hurt(DamageSource.STARVE, this.getHealth());
			}
		}
	}

	@Override
	public void die(DamageSource source)
	{
		if (!this.level.isClientSide())
		{
			if (this.hasLinkedBonfire && this.getLinkedBonfire() != null && this.getLinkedBonfire().getFireKeeperStringUUID() == this.stringUUID)
			{
				this.getLinkedBonfire().setLit(false);
			}
		}
		super.die(source);
	}

	@Override
	protected ActionResultType mobInteract(PlayerEntity player, Hand hand)
	{
		if (player.level.isClientSide)
		{
			switch (this.getCurrentQuestPath())
			{
				case "1":
					player.sendMessage(new TranslationTextComponent("dialogue.darksouls.fire_keeper.introduction"),  Util.NIL_UUID);
					this.setCurrentQuestPath("2");
					break;
		
				case "2":
					if (player.inventory.contains(new ItemStack(ModItems.ESTUS_SHARD.get())))
					{
						player.sendMessage(new TranslationTextComponent("dialogie.darksouls.fire_keeper.estus_shard"),  Util.NIL_UUID);
					} else
					{
						player.sendMessage(new TranslationTextComponent("dialogie.darksouls.fire_keeper.general"),  Util.NIL_UUID);
					}
					if (ModNetworkManager.connection != null) ModNetworkManager.connection.openFireKeeperScreen(this.getId());
					break;
			}
		}

		return ActionResultType.sidedSuccess(player.level.isClientSide);
	}

	public void openContainer(ServerPlayerEntity serverplayer)
	{
		SimpleNamedContainerProvider container = new SimpleNamedContainerProvider((id, inventory, p_235576_4_) ->
		{
			return new ReinforceEstusFlaskContainer(id, inventory, IWorldPosCallable.create(this.level, this.blockPosition()));
		}, new TranslationTextComponent("container.reinforce_estus_flask.title"));
		serverplayer.openMenu(container);
	}

	@Override
	public List<String> getQuestPaths()
	{
		List<String> list = new ArrayList<String>();
		list.add("1");
		list.add("2");
		return list;
	}

	@Override
	protected SoundEvent getAmbientSound()
	{
		return SoundEvents.VILLAGER_AMBIENT;
	}

	@Override
	protected SoundEvent getDeathSound()
	{
		return SoundEvents.VILLAGER_DEATH;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source)
	{
		return SoundEvents.VILLAGER_HURT;
	}
}