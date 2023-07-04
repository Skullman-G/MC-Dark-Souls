package com.skullmangames.darksouls.common.animation.types;

import java.util.function.BiFunction;

import com.skullmangames.darksouls.client.animation.AnimationLayer.LayerPart;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;

public class SupplierAnimation extends StaticAnimation
{
	private final BiFunction<LivingCap<?>, LayerPart, StaticAnimation> biFunction;
	
	public SupplierAnimation(BiFunction<LivingCap<?>, LayerPart, StaticAnimation> biFunction)
	{
		super();
		this.biFunction = biFunction;
	}
	
	@Override
	public StaticAnimation get(LivingCap<?> entityCap, LayerPart layerPart)
	{
		return this.biFunction.apply(entityCap, layerPart).get(entityCap, layerPart);
	}
}
