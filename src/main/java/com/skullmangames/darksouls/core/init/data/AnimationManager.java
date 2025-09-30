package com.skullmangames.darksouls.core.init.data;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.animation.AnimBuilder;
import com.skullmangames.darksouls.common.animation.AnimationType;
import com.skullmangames.darksouls.common.animation.types.AdaptableAnimation;
import com.skullmangames.darksouls.common.animation.types.DeathAnimation;
import com.skullmangames.darksouls.common.animation.types.MirrorAnimation;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.ParryAnimation;
import net.minecraft.resources.ResourceLocation;

public class AnimationManager extends DSJsonDataRegister<AnimBuilder>
{
	private static final Logger LOGGER = LogUtils.getLogger();
	
	private Map<ResourceLocation, StaticAnimation> animations = ImmutableMap.of();
	
	@Override
	public String getDirectory()
	{
		return "animation_data";
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
		throw new IllegalArgumentException("Unable to find attack animation with path: " + id);
	}
	
	public static DeathAnimation getDeathAnimation(ResourceLocation id)
	{
		StaticAnimation animation = getAnimation(id);
		if (animation instanceof DeathAnimation a) return a;
		throw new IllegalArgumentException("Unable to find death animation with path: " + id);
	}
	
	public static MirrorAnimation getMirrorAnimation(ResourceLocation id)
	{
		StaticAnimation animation = getAnimation(id);
		if (animation instanceof MirrorAnimation a) return a;
		throw new IllegalArgumentException("Unable to find mirror animation with path: " + id);
	}
	
	public static ParryAnimation getParryAnimation(ResourceLocation id)
	{
		StaticAnimation animation = getAnimation(id);
		if (animation instanceof ParryAnimation a) return a;
		throw new IllegalArgumentException("Unable to find parry animation with path: " + id);
	}
	
	public static AdaptableAnimation getAdaptableAnimation(ResourceLocation id)
	{
		StaticAnimation animation = getAnimation(id);
		if (animation instanceof AdaptableAnimation a) return a;
		throw new IllegalArgumentException("Unable to find adaptable animation with path: " + id);
	}
	
	@Override
	protected void finish(Set<AnimBuilder> builders)
	{
		ImmutableMap.Builder<ResourceLocation, StaticAnimation> mapBuilder = ImmutableMap.builder();
		builders.forEach(builder ->
		{
			builder.register(mapBuilder);
		});
		this.animations = mapBuilder.build();
	}
	
	@Override
	protected AnimBuilder builderFromJson(ResourceLocation location, JsonObject json)
	{
		AnimationType type = AnimationType.fromString(json.get("animation_type").getAsString());
		return type.getAnimBuilder(location, json);
	}
	
	@Override
	public Logger getLogger()
	{
		return LOGGER;
	}
}
