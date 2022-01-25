package com.skullmangames.darksouls.common.entity;

import java.util.ArrayList;
import java.util.List;

import com.skullmangames.darksouls.common.entity.ai.goal.WalkAroundBonfireGoal;
import com.skullmangames.darksouls.common.inventory.container.ReinforceEstusFlaskContainer;
import com.skullmangames.darksouls.common.tileentity.BonfireBlockEntity;
import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.core.init.ModBlockEntities;
import com.skullmangames.darksouls.network.ModNetworkManager;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class FireKeeperEntity extends QuestEntity
{
	private BlockPos linkedBonfirePos;
	private boolean hasLinkedBonfire = false;

	public FireKeeperEntity(EntityType<? extends QuestEntity> entity, Level level)
	{
		super(entity, level);
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

	public BonfireBlockEntity getLinkedBonfire()
	{
		BlockEntity tileentity = this.level.getBlockEntity(this.linkedBonfirePos);
		return tileentity instanceof BonfireBlockEntity
				? (BonfireBlockEntity) tileentity
				: null;
	}

	public static AttributeSupplier.Builder createAttributes()
	{
		return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.MOVEMENT_SPEED, 0.15D);
	}

	@Override
	protected void registerGoals()
	{
		this.goalSelector.addGoal(0, new WalkAroundBonfireGoal(this, 1.0D));
		this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 15.0F, 1.0F));
		this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
	}

	@Override
	public boolean removeWhenFarAway(double p_213397_1_)
	{
		return !this.hasLinkedBonfire;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag nbt)
	{
		super.addAdditionalSaveData(nbt);
		nbt.putInt("LinkedBonfireX", this.linkedBonfirePos.getX());
		nbt.putInt("LinkedBonfireY", this.linkedBonfirePos.getY());
		nbt.putInt("LinkedBonfireZ", this.linkedBonfirePos.getZ());
		nbt.putBoolean("HasLinkedBonfire", this.hasLinkedBonfire);
		nbt.putString("QuestPath", this.getCurrentQuestPath());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag nbt)
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
			for (BlockPos pos : this.level.getChunk(this.blockPosition()).getBlockEntitiesPos())
			{
				BonfireBlockEntity t = this.level.getBlockEntity(pos, ModBlockEntities.BONFIRE.get()).orElse(null);
				if (t != null
						&& !t.hasFireKeeper()
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
	protected InteractionResult mobInteract(Player player, InteractionHand hand)
	{
		if (player.level.isClientSide)
		{
			switch (this.getCurrentQuestPath())
			{
				case "1":
					player.sendMessage(new TranslatableComponent("dialogue.darksouls.fire_keeper.introduction"),  Util.NIL_UUID);
					this.setCurrentQuestPath("2");
					break;
		
				case "2":
					if (player.getInventory().contains(new ItemStack(ModItems.ESTUS_SHARD.get())))
					{
						player.sendMessage(new TranslatableComponent("dialogie.darksouls.fire_keeper.estus_shard"),  Util.NIL_UUID);
					} else
					{
						player.sendMessage(new TranslatableComponent("dialogie.darksouls.fire_keeper.general"),  Util.NIL_UUID);
					}
					if (ModNetworkManager.connection != null) ModNetworkManager.connection.openFireKeeperScreen(this.getId());
					break;
			}
		}

		return InteractionResult.sidedSuccess(player.level.isClientSide);
	}

	public void openContainer(ServerPlayer serverplayer)
	{
		SimpleMenuProvider container = new SimpleMenuProvider((id, inventory, p_235576_4_) ->
		{
			return new ReinforceEstusFlaskContainer(id, inventory, ContainerLevelAccess.create(this.level, this.blockPosition()));
		}, new TranslatableComponent("container.reinforce_estus_flask.title"));
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