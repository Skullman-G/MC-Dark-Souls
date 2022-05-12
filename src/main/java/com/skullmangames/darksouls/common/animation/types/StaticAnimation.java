package com.skullmangames.darksouls.common.animation.types;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.animation.AnimationLayer;
import com.skullmangames.darksouls.client.animation.ClientAnimationProperties;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.AnimationManager;
import com.skullmangames.darksouls.common.animation.AnimationPlayer;
import com.skullmangames.darksouls.common.animation.types.attack.Property;
import com.skullmangames.darksouls.common.animation.types.attack.Property.StaticAnimationProperty;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.config.IngameConfig;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.parser.xml.collada.AnimationDataExtractor;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class StaticAnimation extends DynamicAnimation
{
	protected final Map<Property<?>, Object> properties = new HashMap<>();
	protected final Function<Models<?>, Model> model;
	protected final ResourceLocation resourceLocation;
	protected final int animationId;

	public StaticAnimation()
	{
		super(0.0F, false);
		this.animationId = -1;
		this.resourceLocation = null;
		this.model = null;
	}

	public StaticAnimation(boolean repeatPlay, String path, Function<Models<?>, Model> model)
	{
		this(IngameConfig.GENERAL_ANIMATION_CONVERT_TIME, repeatPlay, path, model);
	}

	public StaticAnimation(float convertTime, boolean isRepeat, String path, Function<Models<?>, Model> model)
	{
		super(convertTime, isRepeat);
		AnimationManager animationManager = DarkSouls.getInstance().animationManager;
		this.animationId = animationManager.getIdCounter();
		animationManager.getIdMap().put(this.animationId, this);
		this.resourceLocation = new ResourceLocation(DarkSouls.MOD_ID, "animations/" + path);
		animationManager.getNameMap().put(new ResourceLocation(DarkSouls.MOD_ID, path), this);
		this.model = model;
	}

	public StaticAnimation(float convertTime, boolean repeatPlay, String path, Function<Models<?>, Model> model,
			boolean doNotRegister)
	{
		super(convertTime, repeatPlay);
		this.animationId = -1;
		this.resourceLocation = new ResourceLocation(DarkSouls.MOD_ID,
				"animations/" + path);
		this.model = model;
	}

	@OnlyIn(Dist.CLIENT)
	public AnimationLayer.Priority getPriority()
	{
		return this.getProperty(ClientAnimationProperties.PRIORITY).orElse(AnimationLayer.Priority.LOWEST);
	}

	@OnlyIn(Dist.CLIENT)
	public AnimationLayer.LayerType getLayerType()
	{
		return this.getProperty(ClientAnimationProperties.LAYER_TYPE).orElse(AnimationLayer.LayerType.BASE_LAYER);
	}

	public <V> StaticAnimation addProperty(StaticAnimationProperty<V> propertyType, V value)
	{
		this.properties.put(propertyType, value);
		return this;
	}

	public ResourceLocation getLocation()
	{
		return this.resourceLocation;
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
		ResourceLocation extenderPath = new ResourceLocation(animation.resourceLocation.getNamespace(),
				animation.resourceLocation.getPath() + ".dae");
		AnimationDataExtractor.extractAnimation(extenderPath, animation, animation.model.apply(models).getArmature());
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

	public int getId()
	{
		return this.animationId;
	}

	@Override
	public String toString()
	{
		return this.resourceLocation.toString();
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