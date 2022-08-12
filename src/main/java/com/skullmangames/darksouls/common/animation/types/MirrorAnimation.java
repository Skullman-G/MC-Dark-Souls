package com.skullmangames.darksouls.common.animation.types;

import java.util.function.Function;

import com.skullmangames.darksouls.client.animation.AnimationLayer.LayerPart;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.Property;
import com.skullmangames.darksouls.common.animation.Property.StaticAnimationProperty;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.Models;

import net.minecraft.resources.IResourceManager;

public class MirrorAnimation extends StaticAnimation
{
	public StaticAnimation mirror;
	
	public MirrorAnimation(float convertTime, boolean repeatPlay, String path1, String path2, Function<Models<?>, Model> model)
	{
		this(convertTime, repeatPlay, true, path1, path2, model);
	}
	
	public MirrorAnimation(float convertTime, boolean repeatPlay, boolean applyLayerParts, String path1, String path2, Function<Models<?>, Model> model)
	{
		super(convertTime, repeatPlay, path1, model);
		this.mirror = new StaticAnimation(convertTime, repeatPlay, path2, model, true);
		
		if (applyLayerParts)
		{
			this.addProperty(StaticAnimationProperty.LAYER_PART, LayerPart.RIGHT);
			this.mirror.addProperty(StaticAnimationProperty.LAYER_PART, LayerPart.LEFT);
		}
	}
	
	@Override
	public <V> MirrorAnimation addProperty(Property<V> propertyType, V value)
	{
		super.addProperty(propertyType, value);
		return this;
	}
	
	@Override
	public StaticAnimation checkAndReturnAnimation(LivingCap<?> entityCap, LayerPart layerPart)
	{
		switch (layerPart)
		{
			case RIGHT: return this;
			case LEFT: return this.mirror;
			default: switch(entityCap.getOriginalEntity().getUsedItemHand())
			{
				case MAIN_HAND: return this;
				case OFF_HAND: return this.mirror;
				default: return Animations.DUMMY_ANIMATION;
			}
		}
	}
	
	@Override
	public void loadAnimation(IResourceManager resourceManager, Models<?> models)
	{
		load(resourceManager, models, this);
		load(resourceManager, models, this.mirror);
	}
}