package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.client.animation.AnimatorClient;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.entity.AsylumDemonEntity;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource.StunType;

public class AsylumDemonData extends MobData<AsylumDemonEntity>
{
	@Override
	protected void initAnimator(AnimatorClient animatorClient)
	{
		animatorClient.mixLayer.setJointMask("Root", "Torso");
	}

	@Override
	public void updateMotion() {}

	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB)
	{
		return modelDB.ENTITY_ASYLUM_DEMON;
	}

	@Override
	public StaticAnimation getHitAnimation(StunType stunType)
	{
		return null;
	}
}
