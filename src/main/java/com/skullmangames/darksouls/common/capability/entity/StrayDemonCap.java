package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.client.animation.ClientAnimator;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.DeathAnimation;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap;
import com.skullmangames.darksouls.common.entity.StrayDemon;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackInstance;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource;
import com.skullmangames.darksouls.core.util.math.vector.ModMatrix4f;
import com.skullmangames.darksouls.core.util.physics.Collider;

import net.minecraft.world.InteractionHand;

public class StrayDemonCap extends MobCap<StrayDemon>
{
	public static final float WEAPON_SCALE = 2.5F;
	
	@Override
	protected void initAttributes()
	{
		super.initAttributes();
		
		this.orgEntity.getAttribute(ModAttributes.STANDARD_PROTECTION.get()).setBaseValue(20D);
		this.orgEntity.getAttribute(ModAttributes.STRIKE_PROTECTION.get()).setBaseValue(20D);
		this.orgEntity.getAttribute(ModAttributes.SLASH_PROTECTION.get()).setBaseValue(20D);
		this.orgEntity.getAttribute(ModAttributes.THRUST_PROTECTION.get()).setBaseValue(20D);
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB)
	{
		return modelDB.ENTITY_STRAY_DEMON;
	}

	@Override
	public void initAnimator(ClientAnimator animatorClient)
	{
		animatorClient.putLivingAnimation(LivingMotion.IDLE, Animations.STRAY_DEMON_IDLE);
		animatorClient.putLivingAnimation(LivingMotion.WALKING, Animations.STRAY_DEMON_WALK);
		animatorClient.setCurrentMotionsToDefault();
	}
	
	@Override
	public float getWeaponScale()
	{
		return WEAPON_SCALE;
	}
	
	@Override
	public Collider getColliderMatching(InteractionHand hand)
	{
		MeleeWeaponCap cap = this.getHeldMeleeWeaponCap(hand);
		if (cap.getWeaponCollider() == Colliders.GREAT_HAMMER) return Colliders.STRAY_DEMON_GREAT_HAMMER;
		return cap != null ? cap.getWeaponCollider() : Colliders.FIST;
	}
	
	@Override
	public int getSoulReward()
	{
		return 1000;
	}
	
	@Override
	protected void initAI()
	{
		super.initAI();
		this.orgEntity.goalSelector.addGoal(0, new AttackGoal(this, 1.0F, 1, true, false, false)
				.addAttack(new AttackInstance(5, 3F, 6F, Animations.STRAY_DEMON_HAMMER_LIGHT_ATTACK))
				.addAttack(new AttackInstance(5, 3F, 6F, Animations.STRAY_DEMON_HAMMER_ALT_LIGHT_ATTACK))
				.addAttack(new AttackInstance(5, 8F, 10F, Animations.STRAY_DEMON_HAMMER_HEAVY_ATTACK))
				.addAttack(new AttackInstance(4, 4F, Animations.STRAY_DEMON_HAMMER_DRIVE))
				.addAttack(new AttackInstance(5, 10.0F, 12.0F, Animations.STRAY_DEMON_HAMMER_DASH_ATTACK))
				.addAttack(new AttackInstance(4, 3F, Animations.STRAY_DEMON_GROUND_POUND)));
	}
	
	@Override
	public void updateMotion()
	{
		if (orgEntity.animationSpeed > 0.01F) this.baseMotion = LivingMotion.WALKING;
		else this.baseMotion = LivingMotion.IDLE;
	}
	
	@Override
	public DeathAnimation getDeathAnimation(ExtendedDamageSource dmgSource)
	{
		return Animations.STRAY_DEMON_DEATH;
	}
	
	@Override
	public ModMatrix4f getHeadMatrix(float partialTicks)
	{
		return ModMatrix4f.createModelMatrix(0, 0, 0, 0, 0, 0, 1, 1, 1, 1, partialTicks, 1, 1, 1);
	}

	@Override
	public boolean canBeParried()
	{
		return false;
	}

	@Override
	public boolean canBePunished()
	{
		return false;
	}

	@Override
	public boolean canBeBackstabbed()
	{
		return false;
	}
}
