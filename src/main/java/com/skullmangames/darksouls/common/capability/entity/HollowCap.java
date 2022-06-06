package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.client.animation.ClientAnimator;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.capability.item.WeaponCap.WeaponCategory;
import com.skullmangames.darksouls.common.entity.Hollow;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackInstance;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackPatternGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.BowAttackGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.ChasingGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.StrafingGoal;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.client.CTSReqSpawnInfo;
import com.skullmangames.darksouls.network.server.STCMobInitialSetting;

import io.netty.buffer.ByteBuf;
import net.minecraft.world.entity.monster.RangedAttackMob;

public class HollowCap extends HumanoidCap<Hollow>
{
	@Override
	protected void initAttributes()
	{
		super.initAttributes();
		
		this.orgEntity.getAttribute(ModAttributes.STANDARD_DEFENSE.get()).setBaseValue(0.1D);
		this.orgEntity.getAttribute(ModAttributes.STRIKE_DEFENSE.get()).setBaseValue(0.1D);
		this.orgEntity.getAttribute(ModAttributes.SLASH_DEFENSE.get()).setBaseValue(0.1D);
		this.orgEntity.getAttribute(ModAttributes.THRUST_DEFENSE.get()).setBaseValue(0.1D);
	}
	
	@Override
	public void postInit()
	{
		super.postInit();
		
		if (!this.isClientSide())
		{
			if (!this.orgEntity.canPickUpLoot()) this.orgEntity.setCanPickUpLoot(false);
		}
		else ModNetworkManager.sendToServer(new CTSReqSpawnInfo(this.orgEntity.getId()));
	}
	
	@Override
	public void initAnimator(ClientAnimator animatorClient)
	{
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.HOLLOW_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.WALKING, Animations.HOLLOW_WALK);
		animatorClient.addLivingAnimation(LivingMotion.RUNNING, Animations.HOLLOW_RUN);
		animatorClient.addLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		animatorClient.addLivingAnimation(LivingMotion.MOUNT, Animations.BIPED_MOUNT);
		animatorClient.addLivingAnimation(LivingMotion.DEATH, Animations.BIPED_DEATH);
		animatorClient.setCurrentMotionsToDefault();
	}
	
	@Override
	public STCMobInitialSetting sendInitialInformationToClient()
	{
		STCMobInitialSetting packet = new STCMobInitialSetting(this.orgEntity.getId());
        ByteBuf buf = packet.getBuffer();
        buf.writeBoolean(this.orgEntity.canPickUpLoot());
        
		return packet;
	}
	
	@Override
	public void clientInitialSettings(ByteBuf buf)
	{
		ClientAnimator animator = this.getClientAnimator();
		
		if (buf.readBoolean())
		{
			animator.addLivingAnimation(LivingMotion.IDLE, Animations.HOLLOW_IDLE);
			animator.addLivingAnimation(LivingMotion.WALKING, Animations.HOLLOW_WALK);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void setAttackGoals(WeaponCategory category)
	{
		if (category == WeaponCategory.BOW && this.orgEntity instanceof RangedAttackMob)
		{
			this.orgEntity.goalSelector.addGoal(0, new BowAttackGoal(this, 40, 15.0F));
		}
		else
		{
			if (category != WeaponCategory.STRAIGHT_SWORD) return;
			this.orgEntity.goalSelector.addGoal(2, new ChasingGoal(this, false));
			this.orgEntity.goalSelector.addGoal(1, new AttackPatternGoal(this, 0.0F, true, Animations.BIPED_JUMP_BACK)
					.addAttack(new AttackInstance(2, 3.0F, Animations.HOLLOW_LIGHT_ATTACKS))
					.addAttack(new AttackInstance(2, 3.0F, Animations.HOLLOW_BARRAGE))
					.addAttack(new AttackInstance(2, 3.0F, Animations.HOLLOW_OVERHEAD_SWING))
					.addAttack(new AttackInstance(5, 4.0F, 5.0F, Animations.HOLLOW_JUMP_ATTACK)));
			this.orgEntity.goalSelector.addGoal(0, new StrafingGoal(this));
		}
	}

	@Override
	public void updateMotion()
	{
		super.commonCreatureUpdateMotion();
	}
}
