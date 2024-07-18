package com.skullmangames.darksouls.common.capability.entity;

import java.util.HashSet;
import java.util.Set;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.animation.AnimationLayer.LayerPart;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.blockentity.BonfireBlockEntity;
import com.skullmangames.darksouls.common.capability.item.Shield;
import com.skullmangames.darksouls.common.capability.item.ItemCapability;
import com.skullmangames.darksouls.common.entity.covenant.Covenant;
import com.skullmangames.darksouls.common.entity.covenant.Covenant.Reward;
import com.skullmangames.darksouls.common.inventory.AttunementsMenu;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ModGameRules;
import com.skullmangames.darksouls.core.init.ModSoundEvents;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.StunType;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.client.CTSPerformDodge.DodgeType;
import com.skullmangames.darksouls.network.server.STCCovenant;
import com.skullmangames.darksouls.network.server.STCCovenantProgress;
import com.skullmangames.darksouls.network.server.STCEntityImpactParticles;
import com.skullmangames.darksouls.network.server.STCFP;
import com.skullmangames.darksouls.network.server.STCHuman;
import com.skullmangames.darksouls.network.server.STCHumanity;
import com.skullmangames.darksouls.network.server.STCLivingMotionChange;
import com.skullmangames.darksouls.network.server.STCLoadPlayerData;
import com.skullmangames.darksouls.network.server.STCNotifyPlayerYawChanged;
import com.skullmangames.darksouls.network.server.STCPlayAnimation;
import com.skullmangames.darksouls.network.server.STCSetMaxPlayerLevel;
import com.skullmangames.darksouls.network.server.STCSouls;
import com.skullmangames.darksouls.network.server.STCStamina;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;

public class ServerPlayerCap extends PlayerCap<ServerPlayer>
{
	public final Set<BonfireBlockEntity> teleports = new HashSet<>();

	@Override
	public void onEntityJoinWorld(ServerPlayer entityIn)
	{
		super.onEntityJoinWorld(entityIn);
		CompoundTag nbt = entityIn.getPersistentData().getCompound(DarkSouls.MOD_ID);
		this.onLoad(nbt);
		ModNetworkManager.sendToPlayer(new STCLoadPlayerData(nbt), entityIn);
		ModNetworkManager.sendToPlayer(new STCSetMaxPlayerLevel(entityIn.level.getGameRules().getInt(ModGameRules.MAX_PLAYER_LEVEL)), entityIn);
	}
	
	@Override
	public void onLoad(CompoundTag nbt)
	{
		super.onLoad(nbt);
		
		ListTag teleportsNbt = nbt.getList("Teleports", 10);
		for (int i = 0; i < teleportsNbt.size(); i++)
		{
			CompoundTag tnbt = teleportsNbt.getCompound(i);
			BlockPos pos = new BlockPos(tnbt.getInt("X"), tnbt.getInt("Y"), tnbt.getInt("Z"));
			BlockEntity blockentity = this.getLevel().getBlockEntity(pos);
			if (blockentity instanceof BonfireBlockEntity)
			{
				this.teleports.add((BonfireBlockEntity)blockentity);
			}
		}
		this.updateTeleports();
	}
	
	@Override
	public void onSave(CompoundTag nbt)
	{
		super.onSave(nbt);
		
		ListTag teleportsNbt = new ListTag();
		for (BonfireBlockEntity bonfire : this.teleports)
		{
			CompoundTag tnbt = new CompoundTag();
			BlockPos pos = bonfire.getBlockPos();
			tnbt.putInt("X", pos.getX());
			tnbt.putInt("Y", pos.getY());
			tnbt.putInt("Z", pos.getZ());
			teleportsNbt.add(tnbt);
		}
		nbt.put("Teleports", teleportsNbt);
	}
	
	@Override
	public void onDeath()
	{
		super.onDeath();
		this.setHumanity(0);
		this.setHuman(false);
		this.setSouls(0);
		this.setFP(this.getMaxFP());
		this.save();
	}
	
	@Override
	public void addTeleport(BonfireBlockEntity bonfire)
	{
		if (!this.teleports.contains(bonfire)) this.teleports.add(bonfire);
		this.updateTeleports();
	}
	
	public void updateTeleports()
	{
		BonfireBlockEntity[] bonfires = new BonfireBlockEntity[this.teleports.size()];
		bonfires = this.teleports.toArray(bonfires);
		for (BonfireBlockEntity bonfire : bonfires)
		{
			if (bonfire.isRemoved()) this.teleports.remove(bonfire);
		}
	}

	@Override
	public void updateOnServer()
	{
		super.updateOnServer();

		// Stamina
		EntityState state = this.getEntityState();
		if (!this.isCreativeOrSpectator() && state.canAct() && state.getContactLevel() != 3)
		{
			float staminaIncr = 1.8F;
			if (this.orgEntity.isSprinting())
				staminaIncr = -1;
			else
			{
				if (this.getEquipLoadLevel() == EquipLoadLevel.OVERENCUMBERED)
					staminaIncr *= 0.7F;
				else if (this.getEquipLoadLevel() == EquipLoadLevel.HEAVY)
					staminaIncr *= 0.8F;
				if (this.isBlocking() || this.orgEntity.onClimbable())
					staminaIncr *= 0.2F;
			}

			this.increaseStamina(staminaIncr);
		}
	}

	public void performDodge(DodgeType type)
	{
		EquipLoadLevel e = this.getEquipLoadLevel();
		boolean fat = e == EquipLoadLevel.HEAVY;
		StaticAnimation animation = null;
		
		if (e == EquipLoadLevel.OVERENCUMBERED) animation = Animations.BIPED_ROLL_TOO_FAT;
		else
		{
			switch (type)
			{
				default:
				case JUMP_BACK:
					animation = Animations.BIPED_JUMP_BACK;
					break;
				case FORWARD:
					animation = fat ? Animations.BIPED_FAT_ROLL : Animations.BIPED_ROLL;
					break;
				case BACK:
					animation = Animations.BIPED_ROLL_BACK;
					break;
				case LEFT:
					animation = Animations.BIPED_ROLL_LEFT;
					break;
				case RIGHT:
					animation = Animations.BIPED_ROLL_RIGHT;
					break;
			}
		}

		this.animator.playAnimation(animation, 0.0F);
		ModNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(
				new STCPlayAnimation(animation, this.orgEntity.getId(), 0.0F), this.orgEntity);

		if (this.isCreativeOrSpectator()) return;
		this.increaseStamina(-15.0F);
	}
	
	@Override
	public void setCovenant(Covenant value)
	{
		if (value == this.getCovenant()) return;
		Reward reward = value.getNextReward(this);
		super.setCovenant(value);
		ModNetworkManager.sendToPlayer(new STCCovenant(this.orgEntity.getId(), this.getCovenant()), this.orgEntity);
		
		if (reward != null && reward.getReqCount() == 0)
		{
			ItemEntity itementity = new ItemEntity(this.getLevel(), this.getX(), this.getY() + 1, this.getZ(), reward.getRewardItem());
			itementity.setDefaultPickUpDelay();
			this.getLevel().addFreshEntity(itementity);
		}
	}
	
	@Override
	public void setCovenantProgress(int value)
	{
		if (value == this.getCovenantProgress()) return;
		super.setCovenantProgress(value);
		ModNetworkManager.sendToPlayer(new STCCovenantProgress(this.orgEntity.getId(), this.getCovenantProgress()), this.orgEntity);
	}

	@Override
	public void setStamina(float value)
	{
		if (value == this.getStamina()) return;
		super.setStamina(value);
		ModNetworkManager.sendToPlayer(new STCStamina(this.orgEntity.getId(), this.getStamina()), this.orgEntity);
	}

	@Override
	public void setHumanity(int value)
	{
		if (this.getHumanity() == value) return;
		super.setHumanity(value);
		ModNetworkManager.sendToPlayer(new STCHumanity(this.orgEntity.getId(), this.getHumanity()), this.orgEntity);
	}

	@Override
	public void setHuman(boolean value)
	{
		if (this.isHuman() == value) return;
		if (value)
		{
			this.playSound(ModSoundEvents.GENERIC_HUMAN_FORM.get());
		}
		super.setHuman(value);
		ModNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(new STCHuman(this.orgEntity.getId(), this.isHuman()), this.orgEntity);
	}

	@Override
	public void setSouls(int value)
	{
		if (this.getSouls() == value) return;
		super.setSouls(value);
		ModNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(new STCSouls(this.orgEntity.getId(), this.getSouls()), this.orgEntity);
	}
	
	@Override
	public void setFP(float value)
	{
		if (this.getFP() == value) return;
		super.setFP(value);
		ModNetworkManager.sendToPlayer(new STCFP(this.orgEntity.getId(), this.getFP()), this.orgEntity);
	}
	
	public void openAttunementMenu()
	{
		SimpleMenuProvider container = new SimpleMenuProvider((id, inventory, player) ->
		{
			return new AttunementsMenu(id, inventory, this.getAttunements());
		}, new TranslatableComponent("container.attunements.title"));
		this.orgEntity.openMenu(container);
	}

	@Override
	public void updateMotion() {}

	@Override
	public void onHeldItemChange(ItemCapability toChange, ItemStack stack, InteractionHand hand)
	{
		super.onHeldItemChange(toChange, stack, hand);
		ItemCapability mainHandCap = hand == InteractionHand.MAIN_HAND ? toChange
				: this.getHeldItemCapability(InteractionHand.MAIN_HAND);
		if (mainHandCap != null) mainHandCap.onHeld(this);
	}
	
	@Override
	public void makeImpactParticles(Vec3 impactPos, boolean blocked)
	{
		if (!this.isClientSide())
		{
			if (!blocked) this.playSound(ModSoundEvents.GENERIC_BLOOD.get());
			ModNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(new STCEntityImpactParticles(this.orgEntity.getId(), impactPos, blocked), this.orgEntity);
		}
	}

	@Override
	public boolean blockingAttack(ExtendedDamageSource damageSource)
	{
		Entity attacker = damageSource.getSource();
		if (attacker == null || !damageSource.wasBlocked() || !this.isBlocking()) return false;

		Shield shield = (Shield)this.getHeldMeleeWeaponCap(this.orgEntity.getUsedItemHand());
		this.increaseStamina(-damageSource.getStaminaDamage() * (1 - shield.getStability()));
		if (this.getStamina() > 0.0F) return super.blockingAttack(damageSource);
		
		damageSource.getDamages().foreach((type, amount) ->
		{
			damageSource.getDamages().put(type, amount * (1 - shield.getDefense(type.coreType())));
		});
		
		damageSource.setWasBlocked(true);
		this.playSound(shield.getBlockSound());
		damageSource.setStunType(StunType.DISARMED);
		this.cancelUsingItem();
		return true;
	}
	
	@Override
	public void setTwoHanding(boolean value)
	{
		super.setTwoHanding(value);
		this.modifyLivingMotions();
	}

	@Override
	public void modifyLivingMotions()
	{
		ItemCapability mainHandCap = this.getHeldItemCapability(InteractionHand.MAIN_HAND);
		ItemCapability offHandCap = this.getHeldItemCapability(InteractionHand.OFF_HAND);
		
		STCLivingMotionChange msg = new STCLivingMotionChange(this.orgEntity.getId(), false);
		
		if (mainHandCap != null)
		{
			mainHandCap.getLivingMotionChanges(this).forEach((motion, animation) ->
			{
				msg.put(motion, animation.get(this, LayerPart.RIGHT));
			});
		}
		if (offHandCap != null)
		{
			offHandCap.getLivingMotionChanges(this).forEach((motion, animation) ->
			{
				StaticAnimation anim = animation.get(this, LayerPart.LEFT);
				if (anim.getLayerPart() == LayerPart.LEFT)
				{
					msg.put(motion, anim);
				}
			});
		}

		ModNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(msg, this.orgEntity);
	}

	@Override
	public void playAnimationSynchronized(StaticAnimation animation, float convertTimeModifier,
			AnimationPacketProvider packetProvider)
	{
		super.playAnimationSynchronized(animation, convertTimeModifier, packetProvider);
		ModNetworkManager.sendToPlayer(packetProvider.get(animation, convertTimeModifier, this), this.orgEntity);
	}

	@Override
	public void changeYaw(float amount)
	{
		super.changeYaw(amount);
		ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCNotifyPlayerYawChanged(this.orgEntity.getId(), yaw),
				this.orgEntity);
		ModNetworkManager.sendToPlayer(new STCNotifyPlayerYawChanged(this.orgEntity.getId(), yaw), this.orgEntity);
	}

	@Override
	public ServerPlayer getOriginalEntity()
	{
		return this.orgEntity;
	}
}