package com.skullmangames.darksouls.common.animation.types;

import java.util.function.Function;

import com.skullmangames.darksouls.client.animation.AnimationLayer;
import com.skullmangames.darksouls.client.animation.ClientAnimationProperties;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.common.capability.entity.EntityState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
			entityCap.getClientAnimator().resetCompositeMotion();
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

	@Override
	@OnlyIn(Dist.CLIENT)
	public AnimationLayer.Priority getPriority()
	{
		return this.getProperty(ClientAnimationProperties.PRIORITY).orElse(AnimationLayer.Priority.HIGHEST);
	}
}