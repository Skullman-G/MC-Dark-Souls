package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.client.animation.ClientAnimator;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap;
import com.skullmangames.darksouls.common.entity.TaurusDemon;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.init.data.Colliders;
import com.skullmangames.darksouls.core.util.collider.Collider;
import com.skullmangames.darksouls.core.util.math.vector.ModMatrix4f;

import net.minecraft.world.InteractionHand;

public class TaurusDemonCap extends MobCap<TaurusDemon>
{
	public static final float WEAPON_SCALE = 1.3F;
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB)
	{
		return modelDB.ENTITY_TAURUS_DEMON;
	}

	@Override
	public void initAnimator(ClientAnimator animatorClient)
	{
		animatorClient.putLivingAnimation(LivingMotion.IDLE, Animations.DUMMY_ANIMATION);
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
		if (cap.getWeaponCollider() == Colliders.DEMONS_GREATAXE.get()) return Colliders.TAURUS_DEMON_GREATAXE.get();
		return cap != null ? cap.getWeaponCollider() : Colliders.FIST.get();
	}
	
	@Override
	public int getSoulReward()
	{
		return 3000;
	}
	
	@Override
	public void updateMotion()
	{
		this.baseMotion = LivingMotion.IDLE;
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
