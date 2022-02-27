package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.client.animation.AnimatorClient;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.capability.item.GreatHammerCap;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap;
import com.skullmangames.darksouls.common.entity.AsylumDemon;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackInstance;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackPatternGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.ChasingGoal;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;
import com.skullmangames.darksouls.core.util.physics.Collider;

import net.minecraft.world.InteractionHand;

public class AsylumDemonData extends MobData<AsylumDemon>
{
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB)
	{
		return modelDB.ENTITY_ASYLUM_DEMON;
	}

	@Override
	protected void initAnimator(AnimatorClient animatorClient)
	{
		animatorClient.mixLayerLeft.setJointMask("Shoulder_L", "Arm_L", "Hand_L");
		animatorClient.mixLayerRight.setJointMask("Shoulder_R", "Arm_R", "Hand_R");
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.ASYLUM_DEMON_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.WALKING, Animations.ASYLUM_DEMON_MOVE);
		animatorClient.addLivingAnimation(LivingMotion.DEATH, Animations.ASYLUM_DEMON_DEATH);
		animatorClient.setCurrentLivingMotionsToDefault();
	}
	
	public static float getWeaponScale()
	{
		return 1.5F;
	}
	
	@Override
	public Collider getColliderMatching(InteractionHand hand)
	{
		MeleeWeaponCap cap = this.getHeldWeaponCapability(hand);
		if (cap instanceof GreatHammerCap) return Colliders.ASYLUM_DEMON_GREAT_HAMMER;
		return cap != null ? cap.getWeaponCollider() : Colliders.FIST;
	}
	
	@Override
	protected void updateOnClient()
	{
		AnimatorClient animator = getClientAnimator();
		
		if(this.inaction)
		{
			this.currentMotion = LivingMotion.IDLE;
		}
		else
		{
			this.updateMotion();
			if(!animator.compareMotion(currentMotion))
			{
				animator.playLoopMotion();
			}
			if(!animator.compareMixMotion(currentMixMotion))
			{
				animator.playMixLoopMotion();
			}
		}
	}
	
	@Override
	protected void initAI()
	{
		super.initAI();
		orgEntity.goalSelector.addGoal(1, new ChasingGoal(this, 1.0D, false));
		orgEntity.goalSelector.addGoal(0, new AttackPatternGoal(this, 1.0F, 1, true)
				.addAttack(new AttackInstance(4, 5.0F, Animations.ASYLUM_DEMON_LIGHT_ATTACK))
				.addAttack(new AttackInstance(4, 5.0F, Animations.ASYLUM_DEMON_HAMMER_DRIVE))
				.addAttack(new AttackInstance(1, 10.0F, 15.0F, Animations.ASYLUM_DEMON_JUMP_ATTACK))
				.addAttack(new AttackInstance(2, 5.0F, Animations.ASYLUM_DEMON_GROUND_POUND)));
	}
	
	@Override
	public void updateMotion()
	{
		if(this.orgEntity.getHealth() <= 0.0F) this.currentMotion = LivingMotion.DEATH;
		else if (orgEntity.animationSpeed > 0.01F) this.currentMotion = LivingMotion.WALKING;
		else this.currentMotion = LivingMotion.IDLE;
	}
	
	@Override
	public PublicMatrix4f getHeadMatrix(float partialTicks)
	{
		return PublicMatrix4f.getModelMatrixIntegrated(0, 0, 0, 0, 0, 0, 1, 1, 1, 1, partialTicks, 1, 1, 1);
	}
}
