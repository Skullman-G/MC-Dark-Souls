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
	
	public MirrorAnimation(ResourceLocation id, float convertTime, boolean repeatPlay, StaticAnimation right, StaticAnimation left,
			Function<Models<?>, Model> model, ImmutableMap<Property<?>, Object> properties)
	{
		super();
		this.right = right;
		this.left = left;
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
		public void register(ImmutableMap.Builder<ResourceLocation, StaticAnimation> register)
		{
			ResourceLocation rightId = new ResourceLocation(this.getId().getNamespace(), this.getId().getPath()+"_right");
			ResourceLocation leftId = new ResourceLocation(this.getId().getNamespace(), this.getId().getPath()+"_left");
			
			ImmutableMap<Property<?>, Object> builtProperties = this.properties.build();
			ImmutableMap<Property<?>, Object> tempProperties = this.properties.build();
			
			if (this.applyLayerParts)
				tempProperties = ImmutableMap.<Property<?>, Object>builder().putAll(builtProperties).put(StaticAnimationProperty.LAYER_PART, LayerPart.RIGHT).build();
			StaticAnimation right = new StaticAnimation(rightId, convertTime, this.repeat, this.location, model, tempProperties);
			
			if (this.applyLayerParts)
				tempProperties = ImmutableMap.<Property<?>, Object>builder().putAll(builtProperties).put(StaticAnimationProperty.LAYER_PART, LayerPart.LEFT).build();
			StaticAnimation left = new StaticAnimation(leftId, convertTime, this.repeat, this.location2, model, tempProperties);
			
			register.put(this.getId(), new MirrorAnimation(this.id, this.convertTime, this.repeat, right, left, this.model, this.properties.build()));
			register.put(rightId, right);
			register.put(leftId, left);
		}
	}
}