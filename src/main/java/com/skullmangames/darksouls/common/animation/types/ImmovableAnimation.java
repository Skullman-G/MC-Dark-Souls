package com.skullmangames.darksouls.common.animation.types;

import java.util.function.Function;

import com.skullmangames.darksouls.client.animation.AnimationLayer.LayerPart;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.Models;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal.Flag;

import com.skullmangames.darksouls.common.animation.Property.ActionAnimationProperty;
import com.skullmangames.darksouls.common.capability.entity.EntityState;

public class ImmovableAnimation extends StaticAnimation
{
	public ImmovableAnimation(float convertTime, String path, Function<Models<?>, Model> model)
	{
		super(convertTime, false, path, model);
	}

	@Override
	public void onStart(LivingCap<?> entityCap)
	{
		super.onStart(entityCap);

		if (entityCap.isClientSide())
		{
			entityCap.getClientAnimator().startInaction();
			boolean allowAddons = this.getProperty(ActionAnimationProperty.ALLOW_MIX_LAYERS).orElse(false);
			for (LayerPart part : LayerPart.mixLayers())
			{
				if (allowAddons && entityCap.currentMixMotions.get(part).isAddon()) continue;
				entityCap.getClientAnimator().resetMixMotionFor(part);
				entityCap.getClientAnimator().baseLayer.disableLayer(part);
			}
		}
		else if (entityCap.getOriginalEntity() instanceof Mob)
		{
			((Mob)entityCap.getOriginalEntity()).goalSelector.disableControlFlag(Flag.MOVE);
		}
	}

	@Override
	public void onUpdate(LivingCap<?> entityCap)
	{
		super.onUpdate(entityCap);
		entityCap.getOriginalEntity().animationSpeed = 0;
	}
	
	@Override
	public void onFinish(LivingCap<?> entityCap, boolean isEnd)
	{
		super.onFinish(entityCap, isEnd);
		
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