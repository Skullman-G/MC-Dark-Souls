package com.skullmangames.darksouls.common.animation;

import java.util.HashMap;
import java.util.Map;

import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ClientModels;
import com.skullmangames.darksouls.core.init.Models;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class AnimationManager extends SimplePreparableReloadListener<Map<Integer, StaticAnimation>>
{
	private final Map<Integer, StaticAnimation> animationById = new HashMap<>();
	private final Map<ResourceLocation, StaticAnimation> animationByName = new HashMap<>();
	private int counter = 0;

	public StaticAnimation findAnimationById(int animationId)
	{
		if (this.animationById.containsKey(animationId))
		{
			return this.animationById.get(animationId);
		}
		
		throw new IllegalArgumentException("Unable to find animation with id: " + animationId);
	}

	public StaticAnimation findAnimationByResourceLocation(String resourceLocation)
	{
		ResourceLocation rl = new ResourceLocation(resourceLocation);

		if (this.animationByName.containsKey(rl))
		{
			return this.animationByName.get(rl);
		}

		throw new IllegalArgumentException("Unable to find animation with path: " + resourceLocation);
	}

	public void loadAnimationsInit(ResourceManager resourceManager)
	{
		Models<?> models = FMLEnvironment.dist == Dist.CLIENT ? ClientModels.CLIENT : Models.SERVER;
		this.animationById.values().forEach((animation) ->
		{
			animation.loadAnimation(resourceManager, models);
		});
	}

	@Override
	protected Map<Integer, StaticAnimation> prepare(ResourceManager resourceManager,
			ProfilerFiller profilerIn)
	{
		Animations.buildClient();
		return this.animationById;
	}

	@Override
	protected void apply(Map<Integer, StaticAnimation> objectIn, ResourceManager resourceManager,
			ProfilerFiller profilerIn)
	{
		Models<?> models = FMLEnvironment.dist == Dist.CLIENT ? ClientModels.CLIENT : Models.SERVER;
		objectIn.values().forEach((animation) ->
		{
			animation.loadAnimation(resourceManager, models);
		});
	}

	public int getIdCounter()
	{
		return this.counter++;
	}

	public Map<Integer, StaticAnimation> getIdMap()
	{
		return this.animationById;
	}

	public Map<ResourceLocation, StaticAnimation> getNameMap()
	{
		return this.animationByName;
	}
}
