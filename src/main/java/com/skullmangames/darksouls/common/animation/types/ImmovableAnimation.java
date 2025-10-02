package com.skullmangames.darksouls.common.animation.types;

import java.util.function.Function;

import com.google.common.collect.ImmutableMap;
import com.skullmangames.darksouls.client.animation.AnimationLayer.LayerPart;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.Models;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal.Flag;

import com.skullmangames.darksouls.common.animation.AnimFrameData;
import com.skullmangames.darksouls.common.animation.AnimationPlayer;
import com.skullmangames.darksouls.common.animation.Property;
import com.skullmangames.darksouls.common.capability.entity.EntityState;

public class ImmovableAnimation extends StaticAnimation
{
	public ImmovableAnimation(ResourceLocation id, float convertTime, AnimFrameData frameData,
			Function<Models<?>, Model> model, ImmutableMap<Property<?>, Object> properties)
	{
		super(id, convertTime, false, frameData, model, properties);
	}

	@Override
	public void onStart(LivingCap<?> entityCap, AnimationPlayer animPlayer)
	{
		super.onStart(entityCap, animPlayer);

		if (entityCap.isClientSide())
		{
			entityCap.getClientAnimator().startInaction();
			for (LayerPart part : LayerPart.mixLayers())
			{
				entityCap.getClientAnimator().resetMixMotionFor(part);
				entityCap.getClientAnimator().baseLayer.disableLayer(part);
			}
		}
		else if (entityCap.getOriginalEntity() instanceof Mob mob)
		{
			mob.goalSelector.disableControlFlag(Flag.MOVE);
		}
	}

	@Override
	public void onUpdate(LivingCap<?> entityCap, AnimationPlayer animPlayer)
	{
		super.onUpdate(entityCap, animPlayer);
		entityCap.getOriginalEntity().animationSpeed = 0;
	}
	
	@Override
	public void onFinish(LivingCap<?> entityCap, AnimationPlayer animPlayer)
	{
		super.onFinish(entityCap, animPlayer);
		
		if (entityCap.getOriginalEntity() instanceof Mob)
		{
			((Mob)entityCap.getOriginalEntity()).goalSelector.enableControlFlag(Flag.MOVE);
		}
	}

	@Override
	public boolean isMainFrameAnimation()
	{
		return true;
	}

	@Override
	public EntityState getState(float time)
	{
		return EntityState.PRE_CONTACT;
	}
}