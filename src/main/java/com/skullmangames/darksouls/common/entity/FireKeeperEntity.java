package com.skullmangames.darksouls.common.entity;

import java.util.ArrayList;
import java.util.List;

import com.skullmangames.darksouls.client.util.ClientUtils;
import com.skullmangames.darksouls.common.entity.ai.goal.WalkAroundBonfireGoal;
import com.skullmangames.darksouls.common.inventory.container.ReinforceEstusFlaskContainer;
import com.skullmangames.darksouls.common.tileentity.BonfireTileEntity;
import com.skullmangames.darksouls.core.init.ModItems;

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
	public ILivingEntityData finalizeSpawn(IServerWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_,
			ILivingEntityData p_213386_4_, CompoundNBT p_213386_5_)
	{
		for (TileEntity t : this.level.blockEntityList)
		{
			if (t instanceof BonfireTileEntity && !((BonfireTileEntity) t).hasFireKeeper()
					&& t.getBlockPos().distSqr(this.position().x, this.position().y, this.position().z, false) <= 1000)
			{
				this.linkBonfire(t.getBlockPos());
				break;
			}
		}
		return super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
	}

	public void linkBonfire(BlockPos blockpos)
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
		return this.level.getBlockEntity(this.linkedBonfirePos) instanceof BonfireTileEntity
				? (BonfireTileEntity) this.level.getBlockEntity(this.linkedBonfirePos)
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
		nbt.putInt("linked_bonfire_x", this.linkedBonfirePos.getX());
		nbt.putInt("linked_bonfire_y", this.linkedBonfirePos.getY());
		nbt.putInt("linked_bonfire_z", this.linkedBonfirePos.getZ());
		nbt.putBoolean("has_linked_bonfire", this.hasLinkedBonfire);
		nbt.putString("QuestPath", this.getCurrentQuestPath());
	}

	@Override
	public void readAdditionalSaveData(CompoundNBT nbt)
	{
		super.readAdditionalSaveData(nbt);
		this.linkedBonfirePos = new BlockPos(nbt.getInt("linked_bonfire_x"), nbt.getInt("linked_bonfire_y"), nbt.getInt("linked_bonfire_z"));
		this.hasLinkedBonfire = nbt.getBoolean("has_linked_bonfire");
		this.setCurrentQuestPath(nbt.getString("QuestPath"));
	}

	@Override
	public void tick()
	{
		super.tick();

		if (!this.level.isClientSide() && !this.isDeadOrDying())
		{
			if (this.hasLinkedBonfire && this.getLinkedBonfire() == null)
			{
				this.hurt(DamageSource.STARVE, this.getHealth());
			}
		}
	}

	@Override
	public void checkDespawn()
	{
		super.checkDespawn();

		if (!this.hasLinkedBonfire)
		{
			this.remove();
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
		if (player instanceof ServerPlayerEntity)
		{
			ServerPlayerEntity serverplayer = (ServerPlayerEntity) player;

			switch (this.getCurrentQuestPath())
			{
			case "1":
				serverplayer.sendMessage(new TranslationTextComponent("dialogue.darksouls.fire_keeper.introduction"), serverplayer.getUUID());
				this.setCurrentQuestPath("2");
				break;

			case "2":
				if (serverplayer.inventory.contains(new ItemStack(ModItems.ESTUS_SHARD.get())))
				{
					serverplayer.sendMessage(new TranslationTextComponent("dialogie.darksouls.fire_keeper.estus_shard"), serverplayer.getUUID());
				} else
				{
					serverplayer.sendMessage(new TranslationTextComponent("dialogie.darksouls.fire_keeper.general"), serverplayer.getUUID());
				}
				ClientUtils.openFireKeeperScreen(this, serverplayer);
				break;
			}
		}

		return ActionResultType.SUCCESS;
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