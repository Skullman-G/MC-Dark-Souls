package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.client.animation.AnimatorClient;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.entity.DataKeys;
import com.skullmangames.darksouls.common.entity.Faction;
import com.skullmangames.darksouls.common.entity.HollowEntity;
import com.skullmangames.darksouls.common.entity.MobAttackPatterns;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackPatternGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.ChasingGoal;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ModelInit;
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
		this.orgEntity.getEntityData().define(DataKeys.STUN_ARMOR, Float.valueOf(0.0F));
	}
	
	@Override
	public void postInit()
	{
		super.postInit();
		
		if (!this.isRemote())
		{
			if (!this.orgEntity.canPickUpLoot())
			{
				this.orgEntity.setCanPickUpLoot(isArmed());
			}
		}
		else
		{
			ModNetworkManager.sendToServer(new CTSReqSpawnInfo(this.orgEntity.getId()));
		}
	}
	
	@Override
	protected void initAnimator(AnimatorClient animatorClient)
	{
		super.initAnimator(animatorClient);
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.BIPED_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.WALKING, Animations.BIPED_WALK);
		animatorClient.addLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		animatorClient.addLivingAnimation(LivingMotion.MOUNT, Animations.BIPED_MOUNT);
		animatorClient.addLivingAnimation(LivingMotion.DEATH, Animations.BIPED_DEATH);
		animatorClient.setCurrentLivingMotionsToDefault();
	}
	
	@Override
	public void updateMotion()
	{
		super.commonCreatureUpdateMotion();
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
			animator.addLivingAnimation(LivingMotion.IDLE, Animations.BIPED_IDLE);
			animator.addLivingAnimation(LivingMotion.WALKING, Animations.BIPED_WALK);
		}
	}
	
	@Override
	public void setAIAsUnarmed()
	{
		orgEntity.goalSelector.addGoal(1, new ChasingGoal(this, this.orgEntity, 1.0D, false, Animations.ZOMBIE_CHASE, Animations.ZOMBIE_WALK, !orgEntity.isBaby()));
		orgEntity.goalSelector.addGoal(0, new AttackPatternGoal(this, this.orgEntity, 0.0D, 1.75D, true, MobAttackPatterns.ZOMBIE_NORAML));
	}
	
	@Override
	public <M extends Model> M getEntityModel(ModelInit<M> modelDB)
	{
		return modelDB.ENTITY_BIPED_64_32_TEX;
	}
}
