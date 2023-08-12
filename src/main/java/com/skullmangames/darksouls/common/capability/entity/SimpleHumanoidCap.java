package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.client.animation.ClientAnimator;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.capability.item.Shield;
import com.skullmangames.darksouls.common.capability.item.ItemCapability;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap;
import com.skullmangames.darksouls.common.capability.item.Shield.ShieldType;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap.AttackType;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackInstance;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackGoal;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCLivingMotionChange;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;

public class SimpleHumanoidCap<T extends Mob> extends HumanoidCap<T>
{
	@Override
	public void initAnimator(ClientAnimator animatorClient)
	{
		animatorClient.putLivingAnimation(LivingMotion.IDLE, Animations.BIPED_IDLE);
		animatorClient.putLivingAnimation(LivingMotion.WALKING, Animations.BIPED_WALK);
		animatorClient.putLivingAnimation(LivingMotion.RUNNING, Animations.BIPED_RUN);
		animatorClient.putLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		animatorClient.putLivingAnimation(LivingMotion.MOUNTED, Animations.BIPED_HORSEBACK_IDLE);
		animatorClient.putLivingAnimation(LivingMotion.BLOCKING, Animations.createSupplier((cap, part) ->
		{
			if (this.isTwohanding()) return Animations.BIPED_BLOCK_TH_SWORD;
			Shield shield = cap.getHeldWeaponCapability(cap.getOriginalEntity().getUsedItemHand());
			return shield == null || shield.getShieldType() == ShieldType.NONE ? Animations.BIPED_BLOCK_HORIZONTAL
					: Animations.BIPED_BLOCK_VERTICAL;
		}));
		animatorClient.setCurrentMotionsToDefault();
	}
	
	@Override
	public void postInit()
	{
		if (this.isClientSide() || this.orgEntity.isNoAi()) return;
		super.resetCombatAI();
		
		if (this.orgEntity instanceof PathfinderMob) this.orgEntity.targetSelector.addGoal(1, new HurtByTargetGoal((PathfinderMob)this.orgEntity));
		
		ItemCapability cap = ModCapabilities.getItemCapability(this.orgEntity.getMainHandItem());
		if (cap == null || !(cap instanceof MeleeWeaponCap))
		{
			this.orgEntity.goalSelector.addGoal(0, new AttackGoal(this, 0.0F, true, false, true)
					.addAttack(new AttackInstance(1, 1.0F, Animations.FIST_LIGHT_ATTACK))
					.addAttack(new AttackInstance(1, 1.0F, Animations.FIST_HEAVY_ATTACK))
					.addAttack(new AttackInstance(1, 1.0F, Animations.FIST_DASH_ATTACK)));
		}
		else
		{
			MeleeWeaponCap weapon = (MeleeWeaponCap)cap;
			
			this.orgEntity.goalSelector.addGoal(0, new AttackGoal(this, 0.0F, true, false, true)
					.addAttack(new AttackInstance(1, 2.0F, weapon.getAttacks(AttackType.LIGHT)))
					.addAttack(new AttackInstance(1, 2.0F, weapon.getAttacks(AttackType.HEAVY)))
					.addAttack(new AttackInstance(1, 2.0F, weapon.getAttacks(AttackType.DASH))));
		}
	}

	@Override
	public void updateMotion()
	{
		this.commonMotionUpdate();
	}
	
	public void modifyLivingMotions(ItemCapability itemCap)
	{
		STCLivingMotionChange msg = new STCLivingMotionChange(this.orgEntity.getId(), false);
		
		if (itemCap != null)
		{
			msg.putEntries(itemCap.getLivingMotionChanges(this));
		}

		ModNetworkManager.sendToAllPlayerTrackingThisEntity(msg, this.orgEntity);
	}
}
