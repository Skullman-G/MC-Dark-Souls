package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.client.animation.AnimatorClient;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.entity.DataKeys;
import com.skullmangames.darksouls.common.entity.Faction;
import com.skullmangames.darksouls.common.entity.HollowEntity;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackInstance;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackPatternGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.ChasingGoal;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.client.CTSReqSpawnInfo;
import com.skullmangames.darksouls.network.server.STCMobInitialSetting;

import io.netty.buffer.ByteBuf;

public class HollowData extends BipedMobData<HollowEntity>
{
	public HollowData()
	{
		super(Faction.UNDEAD);
	}
	
	@Override
	public void onEntityJoinWorld(HollowEntity entityIn)
	{
		super.onEntityJoinWorld(entityIn);
		this.orgEntity.getEntityData().define(DataKeys.STUN_ARMOR, Float.valueOf(10.0F));
	}
	
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
	public StaticAnimation getDeflectAnimation()
	{
		return Animations.HOLLOW_DEFLECTED;
	}
	
	@Override
	public void postInit()
	{
		super.postInit();
		
		if (!this.isClientSide())
		{
			if (!this.orgEntity.canPickUpLoot()) this.orgEntity.setCanPickUpLoot(this.isArmed());
		}
		else ModNetworkManager.sendToServer(new CTSReqSpawnInfo(this.orgEntity.getId()));
	}
	
	@Override
	protected void initAnimator(AnimatorClient animatorClient)
	{
		super.initAnimator(animatorClient);
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.HOLLOW_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.WALKING, Animations.HOLLOW_WALK);
		animatorClient.addLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		animatorClient.addLivingAnimation(LivingMotion.MOUNT, Animations.BIPED_MOUNT);
		animatorClient.addLivingAnimation(LivingMotion.DEATH, Animations.BIPED_DEATH);
		animatorClient.setCurrentLivingMotionsToDefault();
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
		AnimatorClient animator = this.getClientAnimator();
		
		if (buf.readBoolean())
		{
			animator.addLivingAnimation(LivingMotion.IDLE, Animations.HOLLOW_IDLE);
			animator.addLivingAnimation(LivingMotion.WALKING, Animations.HOLLOW_WALK);
		}
	}
	
	@Override
	public void setAIAsArmed()
	{
		this.orgEntity.goalSelector.addGoal(1, new ChasingGoal(this, this.orgEntity, 1.0D, false));
		this.orgEntity.goalSelector.addGoal(0, new AttackPatternGoal(this, 0.0F, true)
				.addAttack(new AttackInstance(4, 1.0F, Animations.HOLLOW_LIGHT_ATTACKS))
				.addAttack(new AttackInstance(4, 1.0F, Animations.HOLLOW_BARRAGE))
				.addAttack(new AttackInstance(4, 1.0F, Animations.HOLLOW_OVERHEAD_SWING))
				.addAttack(new AttackInstance(4, 3.0F, Animations.HOLLOW_JUMP_ATTACK)));
	}
	
	@Override
	public <M extends Model>M getEntityModel(Models<M> modelDB)
	{
		return modelDB.ENTITY_BIPED_64_32_TEX;
	}

	@Override
	public void updateMotion()
	{
		super.commonCreatureUpdateMotion();
	}
}
