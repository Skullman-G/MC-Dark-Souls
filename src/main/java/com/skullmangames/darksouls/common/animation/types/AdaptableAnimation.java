package com.skullmangames.darksouls.common.animation.types;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.skullmangames.darksouls.client.animation.AnimationLayer.LayerPart;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.AnimBuilder;
import com.skullmangames.darksouls.common.animation.AnimationType;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.Property.StaticAnimationProperty;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.Models;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class AdaptableAnimation extends StaticAnimation
{
	private final ImmutableMap<LivingMotion, StaticAnimation> animations;
	
	protected AdaptableAnimation(ImmutableMap<LivingMotion, StaticAnimation> animations)
	{
		super();
		this.animations = animations;
	}
	
	@Override
	public StaticAnimation get(LivingCap<?> entityCap, LayerPart layerPart)
	{
		LivingMotion motion = entityCap.baseMotion;
		if (this.animations.containsKey(motion)) return this.animations.get(motion).get(entityCap, layerPart);
		else return Animations.DUMMY_ANIMATION;
	}
	
	public StaticAnimation getForMotion(LivingMotion motion)
	{
		if (this.animations.containsKey(motion)) return this.animations.get(motion);
		else return Animations.DUMMY_ANIMATION;
	}
	
	public Set<LivingMotion> getAvailableMotions()
	{
		return this.animations.keySet();
	}
	
	@Override
	public void loadAnimation(ResourceManager resourceManager, Models<?> models)
	{
		for (StaticAnimation anim : this.animations.values())
		{
			anim.loadAnimation(resourceManager, models);
		}
	}
	
	public static class Builder extends AnimBuilder
	{
		private final ResourceLocation id;
		private final float convertTime;
		private final boolean repeat;
		private final Function<Models<?>, Model> model;
		
		private final Map<LivingMotion, AnimBuilder> entries = new HashMap<>();
		private final ImmutableMap.Builder<LivingMotion, StaticAnimation> animations = ImmutableMap.builder();
		
		public Builder(ResourceLocation id, float convertTime, boolean repeatPlay, Function<Models<?>, Model> model)
		{
			this.id = id;
			this.convertTime = convertTime;
			this.repeat = repeatPlay;
			this.model = model;
		}
		
		public Builder(ResourceLocation id, JsonObject json)
		{
			this.id = id;
			this.convertTime = json.get("convert_time").getAsFloat();
			this.repeat = json.get("repeat").getAsBoolean();
			ResourceLocation modelName = new ResourceLocation(json.get("model").getAsString());
			this.model = (models) -> models.findModel(modelName);
			
			JsonArray animations = json.get("animations").getAsJsonArray();
			for (JsonElement e : animations)
			{
				JsonObject o = e.getAsJsonObject();
				boolean mirrored = o.get("mirrored").getAsBoolean();
				LivingMotion motion = LivingMotion.valueOf(o.get("motion").getAsString());
				boolean applyLayerParts = o.get("apply_layer_parts").getAsBoolean();
				
				if (!mirrored)
				{
					ResourceLocation path = new ResourceLocation(o.get("location").getAsString());
					this.addEntry(motion, path, applyLayerParts);
				}
				else
				{
					ResourceLocation path1 = new ResourceLocation(o.get("location_1").getAsString());
					ResourceLocation path2 = new ResourceLocation(o.get("location_2").getAsString());
					this.addEntry(motion, path1, path2, applyLayerParts);
				}
			}
		}

		@Override
		public JsonObject toJson()
		{
			JsonObject root = new JsonObject();
			
			root.addProperty("animation_type", this.getAnimType().toString());
			root.addProperty("convert_time", this.convertTime);
			root.addProperty("repeat", this.repeat);
			root.addProperty("model", this.model.apply(Models.SERVER).getId().toString());
			
			JsonArray jsonEntries = new JsonArray();
			root.add("animations", jsonEntries);
			this.entries.forEach((motion, e) ->
			{
				if (e instanceof StaticAnimation.Builder entry)
				{
					JsonObject jsonEntry = new JsonObject();
					jsonEntry.addProperty("motion", motion.name());
					jsonEntry.addProperty("apply_layer_parts", (boolean)entry.properties.build().getOrDefault(StaticAnimationProperty.SHOULD_SYNC, false));
					
					boolean mirrored = entry instanceof MirrorAnimation.Builder;
					jsonEntry.addProperty("mirrored", mirrored);
					
					if (!mirrored)
					{
						jsonEntry.addProperty("location", entry.location.toString());
					}
					else
					{
						jsonEntry.addProperty("location_1", entry.location.toString());
						jsonEntry.addProperty("location_2", ((MirrorAnimation.Builder)entry).location2.toString());
					}
					jsonEntries.add(jsonEntry);
				}
			});
			
			return root;
		}
		
		public Builder addEntry(LivingMotion motion, ResourceLocation path, boolean applyLayerParts)
		{
			ResourceLocation entryId = new ResourceLocation(id.getNamespace(), id.getPath()+"_"+motion.toString().toLowerCase());
			StaticAnimation.Builder anim = new StaticAnimation.Builder(entryId, this.convertTime, this.repeat, path, this.model);
			if (applyLayerParts)
			{
				anim.addProperty(StaticAnimationProperty.LAYER_PART, LayerPart.UP);
				anim.addProperty(StaticAnimationProperty.SHOULD_SYNC, true);
			}
			this.entries.put(motion, anim);
			return this;
		}
		
		public Builder addEntry(LivingMotion motion, ResourceLocation path1, ResourceLocation path2, boolean applyLayerParts)
		{
			ResourceLocation entryId = new ResourceLocation(id.getNamespace(), id.getPath()+"_"+motion.toString().toLowerCase());
			MirrorAnimation.Builder anim = new MirrorAnimation.Builder(entryId, this.convertTime, this.repeat, applyLayerParts, path1, path2, this.model);
			if (applyLayerParts) anim.addProperty(StaticAnimationProperty.SHOULD_SYNC, true);
			this.entries.put(motion, anim);
			return this;
		}
		
		public void register(ImmutableMap.Builder<ResourceLocation, StaticAnimation> register)
		{
			this.entries.forEach((motion, entry) ->
			{
				entry.register(register);
				this.animations.put(motion, register.build().get(entry.getId()));
			});
			register.put(this.getId(), new AdaptableAnimation(this.animations.build()));
		}

		@Override
		public ResourceLocation getId()
		{
			return this.id;
		}

		@Override
		public AnimationType getAnimType()
		{
			return AnimationType.ADAPTABLE;
		}
	}
}
