package com.skullmangames.darksouls.common.animation.types;

import java.util.function.Function;

import com.google.common.collect.ImmutableMap.Builder;
import com.skullmangames.darksouls.client.animation.AnimationLayer.LayerPart;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.Property;
import com.skullmangames.darksouls.common.animation.Property.StaticAnimationProperty;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.Models;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class MirrorAnimation extends StaticAnimation
{
	public StaticAnimation right;
	public StaticAnimation left;
	
	public MirrorAnimation(ResourceLocation id, float convertTime, boolean repeatPlay, ResourceLocation path1, ResourceLocation path2, Function<Models<?>, Model> model)
	{
		this(id, convertTime, repeatPlay, true, path1, path2, model);
	}
	
	public MirrorAnimation(ResourceLocation id, float convertTime, boolean repeatPlay, boolean applyLayerParts, ResourceLocation path1, ResourceLocation path2, Function<Models<?>, Model> model)
	{
		super();
		
		ResourceLocation rightId = new ResourceLocation(id.getNamespace(), id.getPath()+"_right");
		ResourceLocation leftId = new ResourceLocation(id.getNamespace(), id.getPath()+"_left");
		
		this.right = new StaticAnimation(rightId, convertTime, repeatPlay, path1, model);
		this.left = new StaticAnimation(leftId, convertTime, repeatPlay, path2, model);
		
		if (applyLayerParts)
		{
			this.right.addProperty(StaticAnimationProperty.LAYER_PART, LayerPart.RIGHT);
			this.left.addProperty(StaticAnimationProperty.LAYER_PART, LayerPart.LEFT);
		}
	}
	
	@Override
	public <V> MirrorAnimation addProperty(Property<V> propertyType, V value)
	{
		this.right.addProperty(propertyType, value);
		this.left.addProperty(propertyType, value);
		return this;
	}
	
	@Override
	public StaticAnimation get(LivingCap<?> entityCap, LayerPart layerPart)
	{
		switch (layerPart)
		{
			case RIGHT: return this.right;
			case LEFT: return this.left;
			default: switch(entityCap.getOriginalEntity().getUsedItemHand())
			{
				case MAIN_HAND: return this.right;
				case OFF_HAND: return this.left;
				default: return Animations.DUMMY_ANIMATION;
			}
		}
	}
	
	@Override
	public void loadAnimation(ResourceManager resourceManager, Models<?> models)
	{
		load(resourceManager, models, this.right);
		load(resourceManager, models, this.left);
	}
	
	@Override
	public MirrorAnimation register(Builder<ResourceLocation, StaticAnimation> builder)
	{
		this.right.register(builder);
		this.left.register(builder);
		return this;
	}
}