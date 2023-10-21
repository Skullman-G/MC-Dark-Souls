package com.skullmangames.darksouls.common.animation;

import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.BackstabCheckAnimation;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap.AttackType;
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
	
	private Map<ResourceLocation, StaticAnimation> defaultAnimations = ImmutableMap.of();
	private Map<ResourceLocation, StaticAnimation> additionalAnimations = ImmutableMap.of();
	
	public AnimationManager()
	{
		super(GSON, "animation_data");
	}
	
	public static AnimationManager getInstance()
	{
		return DarkSouls.getInstance().animationManager;
	}

	public StaticAnimation getAnimation(ResourceLocation resourceLocation)
	{
		if (this.additionalAnimations.containsKey(resourceLocation))
		{
			return this.additionalAnimations.get(resourceLocation);
		}

		if (this.defaultAnimations.containsKey(resourceLocation))
		{
			return this.defaultAnimations.get(resourceLocation);
		}

		throw new IllegalArgumentException("Unable to find animation with path: " + resourceLocation);
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> objects, ResourceManager resourceManager, ProfilerFiller profiler)
	{
		//Init default animations
		this.defaultAnimations = Animations.init().build();
		
		//Load additional from json
		ImmutableMap.Builder<ResourceLocation, StaticAnimation> builder = ImmutableMap.builder();
		objects.forEach((location, json) ->
		{
			try
			{
				StaticAnimation animation = AnimBuilder.fromJson(location, json.getAsJsonObject()).build();
				builder.put(location, animation);
			}
			catch (Exception e)
			{
				LOGGER.error("Parsing error loading additional animation {}", location, e);
			}
		});
		
		this.additionalAnimations = builder.build();
		LOGGER.info("Loaded "+this.additionalAnimations.size()+" additional animations");
		
		//Load collada data
		Models<?> models = FMLEnvironment.dist == Dist.CLIENT ? ClientModels.CLIENT : Models.SERVER;
		
		for (StaticAnimation animation : this.defaultAnimations.values())
		{
			animation.loadAnimation(resourceManager, models);
		}
		for (StaticAnimation animation : this.additionalAnimations.values())
		{
			animation.loadAnimation(resourceManager, models);
		}
	}
	
	private static class AnimBuilder
	{
		protected final ResourceLocation id;
		protected final ResourceLocation location;
		protected final float convertTime;
		protected final boolean isRepeat;
		protected final Function<Models<?>, Model> model;
		
		protected AnimBuilder(ResourceLocation location, JsonObject json) throws Exception
		{
			this.id = location;
			this.location = new ResourceLocation(json.get("location").getAsString());
			
			this.convertTime = json.get("convertTime").getAsFloat();
			this.isRepeat = json.get("isRepeat").getAsBoolean();
			
			this.model = (models) ->
			{
				return models.findModel(new ResourceLocation(json.get("model").getAsString()));
			};
		}
		
		public StaticAnimation build()
		{
			return new StaticAnimation(this.id, this.convertTime, this.isRepeat, this.location, this.model);
		}
		
		public static AnimBuilder fromJson(ResourceLocation location, JsonObject json) throws Exception
		{
			AnimationType type = AnimationType.fromString(json.get("animation_type").getAsString());
			switch(type)
			{
				default: case STATIC: return new AnimBuilder(location, json);
				case ATTACK: return new AttackAnimBuilder(location, json);
				case CRITICAL: return new CriticalAnimBuilder(location, json);
			}
		}
	}
	
	private static class AttackAnimBuilder extends AnimBuilder
	{
		protected final AttackType attackType;
		protected final AttackAnimation.Phase[] phases;
		
		protected AttackAnimBuilder(ResourceLocation location, JsonObject json) throws Exception
		{
			super(location, json);
			
			String attackTypeString = json.get("attackType").getAsString();
			this.attackType = AttackType.fromString(attackTypeString);
			
			JsonArray jsonPhases = json.get("phases").getAsJsonArray();
			int phasesLength = jsonPhases.size();
			AttackAnimation.Phase[] ps = new AttackAnimation.Phase[phasesLength];
			
			for (int i = 0; i < phasesLength; i++)
			{
				JsonObject jsonPhase = jsonPhases.get(i).getAsJsonObject();
				float start = jsonPhase.get("start").getAsFloat();
				float preDelay = jsonPhase.get("pre_delay").getAsFloat();
				float contact = jsonPhase.get("contact").getAsFloat();
				float end = jsonPhase.get("end").getAsFloat();
				String weaponBoneName = jsonPhase.get("weapon_bone_name").getAsString();
				ps[i] = new AttackAnimation.Phase(start, preDelay, contact, end, weaponBoneName);
			}
			
			this.phases = ps;
		}
		
		@Override
		public StaticAnimation build()
		{
			return new AttackAnimation(this.id, this.attackType, this.convertTime, this.location, this.model, this.phases);
		}
	}
	
	private static class CriticalAnimBuilder extends AttackAnimBuilder
	{
		protected final boolean isWeak;
		protected final AnimBuilder followUp;
		
		protected CriticalAnimBuilder(ResourceLocation location, JsonObject json) throws Exception
		{
			super(location, json);
			
			this.isWeak = json.get("isWeak").getAsBoolean();
			
			JsonObject followUpJson = json.get("followUp").getAsJsonObject();
			ResourceLocation followUpLocation = new ResourceLocation(followUpJson.get("location").getAsString());
			
			this.followUp = new AnimBuilder(followUpLocation, followUpJson);
		}
		
		@Override
		public StaticAnimation build()
		{
			return new BackstabCheckAnimation(this.id, this.attackType, this.convertTime, this.isWeak, this.location, this.model, this.followUp.build(), this.phases);
		}
	}
	
	private static enum AnimationType
	{
		STATIC("static"), ATTACK("attack"), CRITICAL("critical");
		
		private final String id;
		
		private AnimationType(String id)
		{
			this.id = id;
		}
		
		private static AnimationType fromString(String id)
		{
			for (AnimationType type : AnimationType.values())
			{
				if (type.id.equals(id)) return type;
			}
			return null;
		}
		
		public String toString()
		{
			return this.id;
		}
	}
}
