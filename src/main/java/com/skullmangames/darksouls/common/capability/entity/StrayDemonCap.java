package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.client.animation.ClientAnimator;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.DeathAnimation;
import com.skullmangames.darksouls.common.capability.item.GreatHammerCap;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap;
import com.skullmangames.darksouls.common.entity.StrayDemon;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackInstance;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackGoal;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;
import com.skullmangames.darksouls.core.util.physics.Collider;

import net.minecraft.world.InteractionHand;

public class StrayDemonCap extends MobCap<StrayDemon>
{
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB)
	{
		return modelDB.ENTITY_STRAY_DEMON;
	}

	@Override
	public void initAnimator(ClientAnimator animatorClient)
	{
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.STRAY_DEMON_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.WALKING, Animations.STRAY_DEMON_MOVE);
		animatorClient.setCurrentMotionsToDefault();
	}
	
	public static float getWeaponScale()
	{
		return 1.5F;
	}
	
	@Override
	public Collider getColliderMatching(InteractionHand hand)
	{
		MeleeWeaponCap cap = this.getHeldWeaponCapability(hand);
		if (cap instanceof GreatHammerCap) return Colliders.STRAY_DEMON_GREAT_HAMMER;
		return cap != null ? cap.getWeaponCollider() : Colliders.FIST;
	}
	
	@Override
	protected void initAI()
	{
		super.initAI();
		this.orgEntity.goalSelector.addGoal(0, new AttackGoal(this, 1.0F, 1, true, false, false)
				.addAttack(new AttackInstance(4, 5.0F, Animations.STRAY_DEMON_LIGHT_ATTACK))
				.addAttack(new AttackInstance(4, 5.0F, Animations.STRAY_DEMON_HAMMER_DRIVE))
				.addAttack(new AttackInstance(1, 10.0F, 15.0F, Animations.STRAY_DEMON_JUMP_ATTACK))
				.addAttack(new AttackInstance(2, 5.0F, Animations.STRAY_DEMON_GROUND_POUND)));
	}
	
	@Override
	public void updateMotion()
	{
		if (orgEntity.animationSpeed > 0.01F) this.currentMotion = LivingMotion.WALKING;
		else this.currentMotion = LivingMotion.IDLE;
	}
	
	@Override
	public DeathAnimation getDeathAnimation(ExtendedDamageSource dmgSource)
	{
		return Animations.STRAY_DEMON_DEATH;
	}
	
	@Override
	public PublicMatrix4f getHeadMatrix(float partialTicks)
	{
		return PublicMatrix4f.getModelMatrixIntegrated(0, 0, 0, 0, 0, 0, 1, 1, 1, 1, partialTicks, 1, 1, 1);
	}
}
