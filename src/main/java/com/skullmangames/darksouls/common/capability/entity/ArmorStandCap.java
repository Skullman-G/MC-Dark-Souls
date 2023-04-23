package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.client.animation.ClientAnimator;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.Models;
import net.minecraft.world.entity.decoration.ArmorStand;

public class ArmorStandCap extends LivingCap<ArmorStand>
{
	@Override
	public void initAnimator(ClientAnimator animatorClient)
	{
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.DUMMY_ANIMATION);
	}

	@Override
	public void updateMotion()
	{
		this.currentMotion = LivingMotion.IDLE;
	}

	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB)
	{
		return modelDB.ENTITY_ARMOR_STAND;
	}
}
