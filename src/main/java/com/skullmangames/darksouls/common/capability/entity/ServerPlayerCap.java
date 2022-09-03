package com.skullmangames.darksouls.common.capability.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.google.common.collect.Sets;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.item.IShield;
import com.skullmangames.darksouls.common.capability.item.ItemCapability;
import com.skullmangames.darksouls.common.entity.Covenant;
import com.skullmangames.darksouls.common.entity.Covenant.Reward;
import com.skullmangames.darksouls.common.inventory.AttunementsMenu;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.init.ModSoundEvents;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.Damage;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.StunType;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCCovenant;
import com.skullmangames.darksouls.network.server.STCCovenantProgress;
import com.skullmangames.darksouls.network.server.STCFP;
import com.skullmangames.darksouls.network.server.STCHuman;
import com.skullmangames.darksouls.network.server.STCHumanity;
import com.skullmangames.darksouls.network.server.STCLivingMotionChange;
import com.skullmangames.darksouls.network.server.STCLoadPlayerData;
import com.skullmangames.darksouls.network.server.STCNotifyPlayerYawChanged;
import com.skullmangames.darksouls.network.server.STCPlayAnimation;
import com.skullmangames.darksouls.network.server.STCSouls;
import com.skullmangames.darksouls.network.server.STCStamina;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;

public class ServerPlayerCap extends PlayerCap<ServerPlayer> implements EquipLoaded
{
	private Map<LivingMotion, StaticAnimation> livingMotionMap = new HashMap<>();
	private Map<LivingMotion, StaticAnimation> defaultLivingAnimations = new HashMap<>();
	private List<LivingMotion> modifiedLivingMotions = new ArrayList<>();

	@Override
	public void onEntityJoinWorld(ServerPlayer entityIn)
	{
		super.onEntityJoinWorld(entityIn);
		livingMotionMap.put(LivingMotion.IDLE, Animations.BIPED_IDLE);
		livingMotionMap.put(LivingMotion.WALKING, Animations.BIPED_WALK);
		livingMotionMap.put(LivingMotion.RUNNING, Animations.BIPED_RUN);
		livingMotionMap.put(LivingMotion.SNEAKING, Animations.BIPED_SNEAK);
		livingMotionMap.put(LivingMotion.SWIMMING, Animations.BIPED_SWIM);
		livingMotionMap.put(LivingMotion.FLOATING, Animations.BIPED_FLOAT);
		livingMotionMap.put(LivingMotion.KNEELING, Animations.BIPED_KNEEL);
		livingMotionMap.put(LivingMotion.FALL, Animations.BIPED_FALL);
		livingMotionMap.put(LivingMotion.MOUNT, Animations.BIPED_MOUNT);
		livingMotionMap.put(LivingMotion.DEATH, Animations.BIPED_DEATH);

		for (Map.Entry<LivingMotion, StaticAnimation> entry : livingMotionMap.entrySet())
		{
			defaultLivingAnimations.put(entry.getKey(), entry.getValue());
		}

		CompoundTag nbt = entityIn.getPersistentData().getCompound(DarkSouls.MOD_ID);
		this.onLoad(nbt);
		ModNetworkManager.sendToPlayer(new STCLoadPlayerData(nbt), entityIn);
	}

	@Override
	public void updateOnServer()
	{
		super.updateOnServer();

		EntityState state = this.getEntityState();
		if (!this.isCreativeOrSpectator() && (state.canAct() || state.getContactLevel() == 3))
		{
			float staminaIncr = 0.3F;
			if (this.orgEntity.isSprinting())
				staminaIncr = -0.1F;
			else
			{
				if (this.getEncumbrance() > 1F)
					staminaIncr *= 0.7F;
				else if (this.getEncumbrance() > 0.5F)
					staminaIncr *= 0.8F;
				if (this.isBlocking() || this.orgEntity.onClimbable())
					staminaIncr *= 0.2F;
			}

			this.increaseStamina(staminaIncr);
		}
	}

	public void performDodge(boolean moving)
	{
		float e = this.getEncumbrance();
		StaticAnimation animation = !moving ? Animations.BIPED_JUMP_BACK
				: e >= 0.5F ? Animations.BIPED_FAT_ROLL : Animations.BIPED_ROLL;

		this.animator.playAnimation(animation, 0.0F);
		ModNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(
				new STCPlayAnimation(animation, this.orgEntity.getId(), 0.0F), this.orgEntity);

		if (this.isCreativeOrSpectator())
			return;
		this.increaseStamina(-4.0F);
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
			this.playSound(ModSoundEvents.GENERIC_HUMAN_FORM.get(), -0.2F, -0.2F);
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

	public void onHeldItemChange(ItemCapability toChange, ItemStack stack, InteractionHand hand)
	{
		ItemCapability mainHandCap = hand == InteractionHand.MAIN_HAND ? toChange
				: this.getHeldItemCapability(InteractionHand.MAIN_HAND);
		if (mainHandCap != null)
			mainHandCap.onHeld(this);

		this.modifyLivingMotions(mainHandCap);
	}

	@Override
	public boolean blockingAttack(ExtendedDamageSource damageSource)
	{
		if (!this.isBlocking()) return false;
		if (damageSource == null) return true;
		else if (this.isCreativeOrSpectator()) return super.blockingAttack(damageSource);

		this.increaseStamina(-damageSource.getStaminaDamage());
		if (this.getStamina() > 0.0F) return super.blockingAttack(damageSource);
		
		IShield shield = (IShield)this.getHeldWeaponCapability(this.orgEntity.getUsedItemHand());
		
		for (Damage damage : damageSource.getDamages())
		{
			damage.setAmount(damage.getAmount() * (1 - shield.getDefense(damage.getType())));
		}
		
		damageSource.setStunType(StunType.DISARMED);
		this.cancelUsingItem();
		return true;
	}

	public void modifyLivingMotions(ItemCapability itemCap)
	{
		this.resetModifiedLivingMotions();

		if (itemCap != null)
		{
			Map<LivingMotion, StaticAnimation> motionChanger = itemCap.getLivingMotionChanges(this);
			if (motionChanger != null)
			{
				Set<Map.Entry<LivingMotion, StaticAnimation>> map = Sets.newHashSet();

				for (Map.Entry<LivingMotion, StaticAnimation> entry : motionChanger.entrySet())
				{
					this.addModifiedLivingMotion(entry.getKey(), entry.getValue());
					map.add(entry);
				}
				
				STCLivingMotionChange msg = new STCLivingMotionChange(this.orgEntity.getId(), false);
				msg.putEntries(map);
				ModNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(msg, orgEntity);
				return;
			}
		}

		STCLivingMotionChange msg = new STCLivingMotionChange(this.orgEntity.getId(), false);
		ModNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(msg, orgEntity);
	}

	private void addModifiedLivingMotion(LivingMotion motion, StaticAnimation animation)
	{
		if (animation != null)
		{
			if (!this.modifiedLivingMotions.contains(motion))
			{
				this.modifiedLivingMotions.add(motion);
			}

			this.livingMotionMap.put(motion, animation);
		}
	}

	private void resetModifiedLivingMotions()
	{
		for (LivingMotion livingMotion : modifiedLivingMotions)
		{
			this.livingMotionMap.put(livingMotion, defaultLivingAnimations.get(livingMotion));
		}

		modifiedLivingMotions.clear();
	}

	public Set<Map.Entry<LivingMotion, StaticAnimation>> getLivingMotionEntrySet()
	{
		return this.livingMotionMap.entrySet();
	}

	@Override
	public void playAnimationSynchronized(StaticAnimation animation, float convertTimeModifier,
			AnimationPacketProvider packetProvider)
	{
		super.playAnimationSynchronized(animation, convertTimeModifier, packetProvider);
		ModNetworkManager.sendToPlayer(packetProvider.get(animation, convertTimeModifier, this), this.orgEntity);
	}

	@Override
	public void reserveAnimation(StaticAnimation animation)
	{
		super.reserveAnimation(animation);
		ModNetworkManager.sendToPlayer(new STCPlayAnimation(animation, this.orgEntity.getId(), 0.0F), this.orgEntity);
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

	@Override
	public void onDeath() {}

	@Override
	public float getEncumbrance()
	{
		return (float) (this.orgEntity.getAttributeValue(ModAttributes.EQUIP_LOAD.get())
				/ this.orgEntity.getAttributeValue(ModAttributes.MAX_EQUIP_LOAD.get()));
	}

	@Override
	public EquipLoadLevel getEquipLoadLevel()
	{
		float e = this.getEncumbrance();

		if (e <= 0.0F)
			return EquipLoadLevel.NONE;
		else if (e <= 0.25F)
			return EquipLoadLevel.LIGHT;
		else if (e <= 0.50F)
			return EquipLoadLevel.MEDIUM;
		else if (e <= 1.00F)
			return EquipLoadLevel.HEAVY;
		else
			return EquipLoadLevel.OVERENCUMBERED;
	}
}