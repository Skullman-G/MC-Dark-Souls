package com.skullmangames.darksouls.common.animation.types;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.animation.AnimationLayer;
import com.skullmangames.darksouls.client.animation.AnimationLayer.LayerPart;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.AnimBuilder;
import com.skullmangames.darksouls.common.animation.AnimationManager;
import com.skullmangames.darksouls.common.animation.AnimationPlayer;
import com.skullmangames.darksouls.common.animation.AnimationType;
import com.skullmangames.darksouls.common.animation.Property;
import com.skullmangames.darksouls.common.animation.Property.StaticAnimationProperty;
import com.skullmangames.darksouls.common.animation.events.AnimEvent;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.config.ClientConfig;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.parser.xml.collada.AnimationDataExtractor;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class StaticAnimation extends DynamicAnimation
{
	protected final ResourceLocation animationId;
	protected final Map<Property<?>, Object> properties = new HashMap<>();
	protected final Function<Models<?>, Model> model;
	protected final ResourceLocation path;

	public StaticAnimation()
	{
		super(0.0F, false);
		this.animationId = new ResourceLocation("null", "null");
		this.path = null;
		this.model = null;
	}

	public StaticAnimation(ResourceLocation id, boolean isRepeat, ResourceLocation path, Function<Models<?>, Model> model)
	{
		this(id, ClientConfig.GENERAL_ANIMATION_CONVERT_TIME, isRepeat, path, model);
	}

	public StaticAnimation(ResourceLocation id, float convertTime, boolean isRepeat, ResourceLocation path, Function<Models<?>, Model> model)
	{
		super(convertTime, isRepeat);
		this.animationId = id;
		this.path = path;
		this.model = model;
	}
	
	public StaticAnimation get(LivingCap<?> entityCap, LayerPart layerPart)
	{
		return this;
	}

	@OnlyIn(Dist.CLIENT)
	public AnimationLayer.LayerPart getLayerPart()
	{
		return this.getProperty(StaticAnimationProperty.LAYER_PART).orElse(AnimationLayer.LayerPart.FULL);
	}

	public <V> StaticAnimation addProperty(Property<V> propertyType, V value)
	{
		this.properties.put(propertyType, value);
		return this;
	}

	public ResourceLocation getPath()
	{
		return this.path;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <V> Optional<V> getProperty(Property<V> propertyType)
	{
		return (Optional<V>) Optional.ofNullable(this.properties.get(propertyType));
	}

	public void loadAnimation(ResourceManager resourceManager, Models<?> models)
	{
		load(resourceManager, models, this);
	}

	public static void load(ResourceManager resourceManager, Models<?> models, StaticAnimation animation)
	{
		ResourceLocation extenderPath = new ResourceLocation(animation.path.getNamespace(),
				"animations/" + animation.path.getPath() + ".dae");
		AnimationDataExtractor.extractAnimation(resourceManager, extenderPath, animation, animation.model.apply(models).getArmature());
	}
	
	@Override
	public boolean shouldSync()
	{
		return this.getProperty(StaticAnimationProperty.SHOULD_SYNC).orElse(false) && this.getLayerPart() != LayerPart.FULL;
	}

	@Override
	public void onStart(LivingCap<?> entityCap)
	{
		this.getProperty(StaticAnimationProperty.EVENTS).ifPresent((events) ->
		{
			for (AnimEvent event : events)
			{
				if (event.time == AnimEvent.ON_BEGIN)
				{
					event.tryExecuting(entityCap);
				}
			}
		});
	}

	@Override
	public void onUpdate(LivingCap<?> entityCap)
	{
		this.getProperty(StaticAnimationProperty.EVENTS).ifPresent((events) ->
		{
			AnimationPlayer player = entityCap.getAnimator().getPlayerFor(this);

			if (player != null)
			{
				float prevElapsed = player.getPrevElapsedTime();
				float elapsed = player.getElapsedTime();

				for (AnimEvent event : events)
				{
					if (event.time != AnimEvent.ON_BEGIN && event.time != AnimEvent.ON_END)
					{
						if (event.time < prevElapsed || event.time >= elapsed)
						{
							continue;
						} else
						{
							event.tryExecuting(entityCap);
						}
					}
				}
			}
		});
	}

	@Override
	public void onFinish(LivingCap<?> entityCap, boolean isEnd)
	{
		this.getProperty(StaticAnimationProperty.EVENTS).ifPresent((events) ->
		{
			for (AnimEvent event : events)
			{
				if (event.time == AnimEvent.ON_END)
				{
					event.tryExecuting(entityCap);
				}
			}
		});
	}
	
	@Override
	public float getPlaySpeed(LivingCap<?> entityCap)
	{
		float speed = super.getPlaySpeed(entityCap);
		if (entityCap.getOriginalEntity().isUnderWater()) speed *= 0.75F;
		return speed;
	}
	
	@Override
	public DeathAnimation getDeathAnimation()
	{
		ResourceLocation rl = this.getProperty(StaticAnimationProperty.DEATH_ANIMATION).orElse(null);
		if (rl != null)
		{
			try
			{
				StaticAnimation deathAnimation = AnimationManager.getAnimation(rl);
				return deathAnimation instanceof DeathAnimation ? (DeathAnimation)deathAnimation : null;
			}
			catch (IllegalArgumentException e)
			{
				DarkSouls.LOGGER.error(e);
			}
		}
		return null;
	}

	@Nullable
	public ResourceLocation getId()
	{
		return this.animationId;
	}

	@Override
	public String toString()
	{
		return this.animationId.toString();
	}
	
	public static class Builder extends AnimBuilder
	{
		protected final ResourceLocation id;
		protected final ResourceLocation location;
		protected final float convertTime;
		protected final boolean repeat;
		protected final Function<Models<?>, Model> model;
		
		protected final ImmutableMap.Builder<Property<?>, Object> properties = new ImmutableMap.Builder<>();
		
		public Builder()
		{
			this.id = new ResourceLocation("null", "null");
			this.location = null;
			this.model = null;
			this.convertTime = 0.0F;
			this.repeat = false;
		}

		public Builder(ResourceLocation id, boolean isRepeat, ResourceLocation path, Function<Models<?>, Model> model)
		{
			this(id, ClientConfig.GENERAL_ANIMATION_CONVERT_TIME, isRepeat, path, model);
		}

		public Builder(ResourceLocation id, float convertTime, boolean isRepeat, ResourceLocation path, Function<Models<?>, Model> model)
		{
			this.id = id;
			this.location = path;
			this.model = model;
			this.convertTime = convertTime;
			this.repeat = isRepeat;
		}
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public Builder(ResourceLocation location, JsonObject json)
		{
			this.id = location;
			this.location = new ResourceLocation(json.get("location").getAsString());
			
			this.convertTime = json.get("convert_time").getAsFloat();
			this.repeat = json.get("repeat").getAsBoolean();
			
			ResourceLocation modelName = new ResourceLocation(json.get("model").getAsString());
			this.model = (models) -> models.findModel(modelName);
			
			JsonObject properties = json.get("properties").getAsJsonObject();
			for (Map.Entry<String, JsonElement> entry : properties.entrySet())
			{
				Property property = Property.GET_BY_NAME.get(entry.getKey());
				this.addProperty(property, property.jsonConverter.fromJson(entry.getValue()));
			}
		}
		
		public JsonObject toJson()
		{
			JsonObject root = new JsonObject();
			
			root.addProperty("animation_type", this.getAnimType().toString());
			root.addProperty("location", this.location.toString());
			root.addProperty("convert_time", this.convertTime);
			root.addProperty("repeat", this.repeat);
			root.addProperty("model", this.model.apply(Models.SERVER).getId().toString());
			
			JsonObject properties = new JsonObject();
			root.add("properties", properties);
			
			this.properties.build().forEach((p, v) ->
			{
				properties.add(p.name, p.jsonConverter.toJson(v));
			});
			
			return root;
		}
		
		public <V> Builder addProperty(Property<V> property, V value)
		{
			this.properties.put(property, value);
			return this;
		}
		
		public ResourceLocation getId()
		{
			return this.id;
		}
		
		public AnimationType getAnimType()
		{
			return AnimationType.STATIC;
		}
		
		public StaticAnimation build()
		{
			return new StaticAnimation(this.id, this.convertTime, this.repeat, this.location, this.model);
		}
	}
}