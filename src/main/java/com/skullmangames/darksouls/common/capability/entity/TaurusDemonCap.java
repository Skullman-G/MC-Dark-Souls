package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.client.animation.ClientAnimator;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.entity.TaurusDemon;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

public class TaurusDemonCap extends MobCap<TaurusDemon>
{
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB)
	{
		return modelDB.ENTITY_TAURUS_DEMON;
	}

	@Override
	public void initAnimator(ClientAnimator animatorClient)
	{
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.DUMMY_ANIMATION);
		animatorClient.setCurrentMotionsToDefault();
	}
	
	@Override
	public void updateMotion()
	{
		this.currentMotion = LivingMotion.IDLE;
	}
	
	@Override
	public PublicMatrix4f getHeadMatrix(float partialTicks)
	{
		return PublicMatrix4f.getModelMatrixIntegrated(0, 0, 0, 0, 0, 0, 1, 1, 1, 1, partialTicks, 1, 1, 1);
	}
}
