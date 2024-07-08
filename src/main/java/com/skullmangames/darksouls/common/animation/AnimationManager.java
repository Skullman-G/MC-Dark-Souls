package com.skullmangames.darksouls.common.animation;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.animation.types.AdaptableAnimation;
import com.skullmangames.darksouls.common.animation.types.DeathAnimation;
import com.skullmangames.darksouls.common.animation.types.MirrorAnimation;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.ParryAnimation;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ClientModels;
import com.skullmangames.darksouls.core.init.Models;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class AnimationManager extends SimpleJsonResourceReloadListener
{
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
	
	private Map<ResourceLocation, StaticAnimation> animations = ImmutableMap.of();
	
	public AnimationManager()
	{
		super(GSON, "animation_data");
	}

	public static StaticAnimation getAnimation(ResourceLocation id)
	{
		AnimationManager manager = DarkSouls.getInstance().animationManager;
		if (manager.animations.containsKey(id))
		{
			return manager.animations.get(id);
		}
		throw new IllegalArgumentException("Unable to find animation with path: " + id);
	}
	
	public static AttackAnimation getAttackAnimation(ResourceLocation id)
	{
		StaticAnimation animation = getAnimation(id);
		if (animation instanceof AttackAnimation a) return a;
		throw new IllegalArgumentException("Unable to final attack animation with path: " + id);
	}
	
	public static DeathAnimation getDeathAnimation(ResourceLocation id)
	{
		StaticAnimation animation = getAnimation(id);
		if (animation instanceof DeathAnimation a) return a;
		throw new IllegalArgumentException("Unable to final death animation with path: " + id);
	}
	
	public static MirrorAnimation getMirrorAnimation(ResourceLocation id)
	{
		StaticAnimation animation = getAnimation(id);
		if (animation instanceof MirrorAnimation a) return a;
		throw new IllegalArgumentException("Unable to final mirror animation with path: " + id);
	}
	
	public static ParryAnimation getParryAnimation(ResourceLocation id)
	{
		StaticAnimation animation = getAnimation(id);
		if (animation instanceof ParryAnimation a) return a;
		throw new IllegalArgumentException("Unable to final parry animation with path: " + id);
	}
	
	public static AdaptableAnimation getAdaptableAnimation(ResourceLocation id)
	{
		StaticAnimation animation = getAnimation(id);
		if (animation instanceof AdaptableAnimation a) return a;
		throw new IllegalArgumentException("Unable to final adaptable animation with path: " + id);
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> objects, ResourceManager resourceManager, ProfilerFiller profiler)
	{
		//Load animation data from json
		ImmutableMap.Builder<ResourceLocation, StaticAnimation> builder = ImmutableMap.builder();
		objects.forEach((location, json) ->
		{
			try
			{
				AnimationType type = AnimationType.fromString(json.getAsJsonObject().get("animation_type").getAsString());
				StaticAnimation animation = type.getAnimBuilder(location, json.getAsJsonObject()).build();
				builder.put(location, animation);
			}
			catch (Exception e)
			{
				LOGGER.error("Parsing error loading additional animation {}", location, e);
			}
		});
		
		this.animations = builder.build();
		LOGGER.info("Loaded "+this.animations.size()+" animations");
		
		//Load collada data
		Models<?> models = FMLEnvironment.dist == Dist.CLIENT ? ClientModels.CLIENT : Models.SERVER;
		
		for (StaticAnimation animation : this.animations.values())
		{
			animation.loadAnimation(resourceManager, models);
		}
		
		Animations.init();
	}
	
	
	/*
	 * Use only to initialize animations while generating data
	 */
	public static void initForDataGenerator(List<AnimBuilder> builders)
	{
		AnimationManager manager = DarkSouls.getInstance().animationManager;
		
		//Load animation data from json
		ImmutableMap.Builder<ResourceLocation, StaticAnimation> builder = ImmutableMap.builder();
		for (AnimBuilder a : builders)
		{
			builder.put(a.getId(), a.build());
		}
		
		manager.animations = builder.build();
		LOGGER.info("Loaded "+manager.animations.size()+" animations");
		
		Animations.init();
	}
}
