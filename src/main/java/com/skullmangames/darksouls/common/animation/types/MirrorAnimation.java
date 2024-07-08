package com.skullmangames.darksouls.common.animation.types;

import java.util.function.Function;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.skullmangames.darksouls.client.animation.AnimationLayer.LayerPart;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.AnimationType;
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
	
	public MirrorAnimation(ResourceLocation id, float convertTime, boolean repeatPlay, ResourceLocation path1, ResourceLocation path2,
			Function<Models<?>, Model> model, ImmutableMap<Property<?>, Object> properties)
	{
		this(id, convertTime, repeatPlay, true, path1, path2, model, properties);
	}
	
	public MirrorAnimation(ResourceLocation id, float convertTime, boolean repeatPlay, boolean applyLayerParts, ResourceLocation path1, ResourceLocation path2,
			Function<Models<?>, Model> model, ImmutableMap<Property<?>, Object> properties)
	{
		super();
		
		ResourceLocation rightId = new ResourceLocation(id.getNamespace(), id.getPath()+"_right");
		ResourceLocation leftId = new ResourceLocation(id.getNamespace(), id.getPath()+"_left");
		
		ImmutableMap<Property<?>, Object> p = properties;
		if (applyLayerParts) p = ImmutableMap.<Property<?>, Object>builder().putAll(properties).put(StaticAnimationProperty.LAYER_PART, LayerPart.RIGHT).build();
		this.right = new StaticAnimation(rightId, convertTime, repeatPlay, path1, model, p);
		if (applyLayerParts) p = ImmutableMap.<Property<?>, Object>builder().putAll(properties).put(StaticAnimationProperty.LAYER_PART, LayerPart.LEFT).build();
		this.left = new StaticAnimation(leftId, convertTime, repeatPlay, path2, model, p);
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
	
	public static class Builder extends StaticAnimation.Builder
	{
		protected final ResourceLocation location2;
		protected final boolean applyLayerParts;
		
		public Builder(ResourceLocation id, float convertTime, boolean isRepeat,
				ResourceLocation path1, ResourceLocation path2, Function<Models<?>, Model> model)
		{
			this(id, convertTime, isRepeat, true, path1, path2, model);
		}

		public Builder(ResourceLocation id, float convertTime, boolean isRepeat, boolean applyLayerParts,
				ResourceLocation path1, ResourceLocation path2, Function<Models<?>, Model> model)
		{
			super(id, convertTime, isRepeat, path1, model);
			this.location2 = path2;
			this.applyLayerParts = applyLayerParts;
		}
		
		public Builder(ResourceLocation id, JsonObject json)
		{
			super(id, json);
			this.location2 = new ResourceLocation(json.get("mirrored_location").getAsString());
			this.applyLayerParts = json.get("apply_layer_parts").getAsBoolean();
		}
		
		@Override
		public JsonObject toJson()
		{
			JsonObject json = super.toJson();
			json.addProperty("mirrored_location", this.location2.toString());
			json.addProperty("apply_layer_parts", this.applyLayerParts);
			return json;
		}

		@Override
		public AnimationType getAnimType()
		{
			return AnimationType.MIRROR;
		}
		
		@Override
		public MirrorAnimation build()
		{
			return new MirrorAnimation(this.id, this.convertTime, this.repeat, this.location, this.location2, this.model, this.properties.build());
		}
	}
}