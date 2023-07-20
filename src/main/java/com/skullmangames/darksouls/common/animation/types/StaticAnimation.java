package com.skullmangames.darksouls.common.animation.types;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.skullmangames.darksouls.client.animation.AnimationLayer;
import com.skullmangames.darksouls.client.animation.AnimationLayer.LayerPart;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.AnimationPlayer;
import com.skullmangames.darksouls.common.animation.Property;
import com.skullmangames.darksouls.common.animation.Property.StaticAnimationProperty;
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
	
	public StaticAnimation register(ImmutableMap.Builder<ResourceLocation, StaticAnimation> builder)
	{
		builder.put(this.getId(), this);
		return this;
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
			for (Event event : events)
			{
				if (event.time == Event.ON_BEGIN)
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

				for (Event event : events)
				{
					if (event.time != Event.ON_BEGIN && event.time != Event.ON_END)
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
			for (Event event : events)
			{
				if (event.time == Event.ON_END)
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

	public static class Event implements Comparable<Event>
	{
		public static final float ON_BEGIN = Float.MIN_VALUE;
		public static final float ON_END = Float.MAX_VALUE;
		final float time;
		final Side executionSide;
		final Consumer<LivingCap<?>> event;

		private Event(float time, Side executionSide, Consumer<LivingCap<?>> event)
		{
			this.time = time;
			this.executionSide = executionSide;
			this.event = event;
		}

		@Override
		public int compareTo(Event arg0)
		{
			if (this.time == arg0.time) return 0;
			else return this.time > arg0.time ? 1 : -1;
		}

		public void tryExecuting(LivingCap<?> entityCap)
		{
			if (this.executionSide.predicate.test(entityCap.isClientSide()))
			{
				this.event.accept(entityCap);
			}
		}

		public static Event create(float time, Side isRemote, Consumer<LivingCap<?>> event)
		{
			return new Event(time, isRemote, event);
		}

		public enum Side
		{
			CLIENT((isLogicalClient) -> isLogicalClient), SERVER((isLogicalClient) -> !isLogicalClient),
			BOTH((isLogicalClient) -> true);

			Predicate<Boolean> predicate;

			Side(Predicate<Boolean> predicate)
			{
				this.predicate = predicate;
			}
		}
	}
}