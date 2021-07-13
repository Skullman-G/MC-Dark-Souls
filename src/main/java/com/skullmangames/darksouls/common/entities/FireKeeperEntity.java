package com.skullmangames.darksouls.common.entities;

import java.util.ArrayList;
import java.util.List;

import com.skullmangames.darksouls.common.containers.ReinforceEstusFlaskContainer;
import com.skullmangames.darksouls.common.entities.ai.goal.WalkAroundBonfireGoal;
import com.skullmangames.darksouls.common.tiles.BonfireTileEntity;
import com.skullmangames.darksouls.core.init.ItemInit;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
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
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class FireKeeperEntity extends QuestEntity
{
	private BlockPos linkedBonfire = BlockPos.ZERO;
	private boolean hasLinkedBonfire = false;
	public boolean talking = false;
	
	public FireKeeperEntity(EntityType<? extends QuestEntity> entity, World world)
	{
		super(entity, world);
	}
	
	public boolean hasLinkedBonfire()
	{
		return this.hasLinkedBonfire;
	}
	
	public void linkBonfire(BlockPos pos, BonfireTileEntity tileentity)
	{
		tileentity.addFireKeeper(this.stringUUID);
		this.linkedBonfire = pos;
		this.hasLinkedBonfire = true;
	}
	
	public BlockPos getLinkedBonfire()
	{
		return this.linkedBonfire;
	}
	
	public static AttributeModifierMap.MutableAttribute createAttributes()
	{
		return MobEntity.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 20.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.15D);
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
		return this.linkedBonfire == null;
	}
	
	@Override
	public void addAdditionalSaveData(CompoundNBT nbt)
	{
		super.addAdditionalSaveData(nbt);
		nbt.putInt("linked_bonfire_x", this.linkedBonfire.getX());
		nbt.putInt("linked_bonfire_y", this.linkedBonfire.getY());
		nbt.putInt("linked_bonfire_z", this.linkedBonfire.getZ());
		nbt.putBoolean("has_linked_bonfire", this.hasLinkedBonfire);
		nbt.putString("QuestPath", this.getCurrentQuestPath());
	}
	
	@Override
	public void readAdditionalSaveData(CompoundNBT nbt)
	{
		super.readAdditionalSaveData(nbt);
		this.linkedBonfire = new BlockPos(nbt.getInt("linked_bonfire_x"), nbt.getInt("linked_bonfire_y"), nbt.getInt("linked_bonfire_z"));
		this.hasLinkedBonfire = nbt.getBoolean("has_linked_bonfire");
		this.setCurrentQuestPath(nbt.getString("QuestPath"));
	}
	
	@Override
	public void tick()
	{
		super.tick();
		if (!this.level.isClientSide() && !this.isDeadOrDying())
		{
			BonfireTileEntity tileentity = this.level.getBlockEntity(this.linkedBonfire) != null && this.level.getBlockEntity(this.linkedBonfire) instanceof BonfireTileEntity ? (BonfireTileEntity)this.level.getBlockEntity(this.linkedBonfire) : null;
			if (this.hasLinkedBonfire && (tileentity == null || tileentity.getFireKeeperStringUUID() != this.stringUUID))
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
			BonfireTileEntity tileentity = this.level.getBlockEntity(this.linkedBonfire) != null && this.level.getBlockEntity(this.linkedBonfire) instanceof BonfireTileEntity ? (BonfireTileEntity)this.level.getBlockEntity(this.linkedBonfire) : null;
			if (this.hasLinkedBonfire && tileentity != null && tileentity.getFireKeeperStringUUID() == this.stringUUID)
			{
				((BonfireTileEntity)this.level.getBlockEntity(this.linkedBonfire)).setLit(null, false);
			}
		}
		super.die(source);
	}
	
	@Override
	protected ActionResultType mobInteract(PlayerEntity player, Hand hand)
	{
		if (player instanceof ServerPlayerEntity)
		{
			ServerPlayerEntity serverplayer = (ServerPlayerEntity)player;
			
			switch(this.getCurrentQuestPath())
			{
			case "1":
				serverplayer.sendMessage(new TranslationTextComponent("dialogue.darksouls.fire_keeper.introduction"), serverplayer.getUUID());
				this.setCurrentQuestPath("2");
				break;
			
			case "2":
				if (serverplayer.inventory.contains(new ItemStack(ItemInit.ESTUS_SHARD.get())))
				{
					serverplayer.sendMessage(new TranslationTextComponent("dialogie.darksouls.fire_keeper.estus_shard"), serverplayer.getUUID());
				}
				else
				{
					serverplayer.sendMessage(new TranslationTextComponent("dialogie.darksouls.fire_keeper.general"), serverplayer.getUUID());
				}
				SimpleNamedContainerProvider container = new SimpleNamedContainerProvider((id, inventory, p_235576_4_) ->
				{
			         return new ReinforceEstusFlaskContainer(id, inventory, this);
			    }, new TranslationTextComponent("container.reinforce_estus_flask.title"));
				serverplayer.openMenu(container);
				break;
			}
		}
		
		return ActionResultType.SUCCESS;
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