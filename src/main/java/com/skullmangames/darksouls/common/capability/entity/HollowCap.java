package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.client.animation.ClientAnimator;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.entity.Hollow;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackInstance;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.BowAttackGoal;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.util.WeaponCategory;
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
		
		this.orgEntity.getAttribute(ModAttributes.STANDARD_PROTECTION.get()).setBaseValue(0.1D);
		this.orgEntity.getAttribute(ModAttributes.STRIKE_PROTECTION.get()).setBaseValue(0.1D);
		this.orgEntity.getAttribute(ModAttributes.SLASH_PROTECTION.get()).setBaseValue(0.1D);
		this.orgEntity.getAttribute(ModAttributes.THRUST_PROTECTION.get()).setBaseValue(0.1D);
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
	public int getSoulReward()
	{
		return 20;
	}
	
	@Override
	public void initAnimator(ClientAnimator animatorClient)
	{
		animatorClient.putLivingAnimation(LivingMotion.IDLE, Animations.HOLLOW_IDLE);
		animatorClient.putLivingAnimation(LivingMotion.WALKING, Animations.HOLLOW_WALK);
		animatorClient.putLivingAnimation(LivingMotion.RUNNING, Animations.HOLLOW_RUN);
		animatorClient.putLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		animatorClient.putLivingAnimation(LivingMotion.MOUNTED, Animations.BIPED_HORSEBACK_IDLE);
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
			animator.putLivingAnimation(LivingMotion.IDLE, Animations.HOLLOW_IDLE);
			animator.putLivingAnimation(LivingMotion.WALKING, Animations.HOLLOW_WALK);
		}
	}
	
	@Override
	public void setAttackGoals(WeaponCategory category)
	{
		if (category == WeaponCategory.BOW && this.orgEntity instanceof RangedAttackMob)
		{
			this.orgEntity.goalSelector.addGoal(0, new BowAttackGoal<Hollow, HollowCap>(this, 40, 15.0F));
		}
		else
		{
			if (category != WeaponCategory.STRAIGHT_SWORD) return;
			this.orgEntity.goalSelector.addGoal(1, new AttackGoal(this, 0.0F, true, false, true)
					.addAttack(new AttackInstance(4, 2.0F, Animations.HOLLOW_LIGHT_ATTACKS))
					.addAttack(new AttackInstance(4, 2.0F, Animations.HOLLOW_BARRAGE))
					.addAttack(new AttackInstance(4, 2.0F, Animations.HOLLOW_OVERHEAD_SWING))
					.addAttack(new AttackInstance(5, 3.0F, 4.0F, Animations.HOLLOW_JUMP_ATTACK))
					.addDodge(Animations.BIPED_JUMP_BACK));
		}
	}

	@Override
	public void updateMotion()
	{
		super.commonMotionUpdate();
	}
}
