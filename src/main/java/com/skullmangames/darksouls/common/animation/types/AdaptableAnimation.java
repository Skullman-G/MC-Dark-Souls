package com.skullmangames.darksouls.common.animation.types;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;
import com.skullmangames.darksouls.client.animation.AnimationLayer.LayerPart;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.Property.StaticAnimationProperty;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.Models;

import net.minecraft.resources.ResourceLocation;

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
	
	@Override
	public StaticAnimation register(ImmutableMap.Builder<ResourceLocation, StaticAnimation> builder)
	{
		for (StaticAnimation a : this.animations.values()) a.register(builder);
		return this;
	}
	
	public static class Builder
	{
		private final ResourceLocation id;
		private final float convertTime;
		private final boolean repeatPlay;
		private final Function<Models<?>, Model> model;
		
		private final Map<LivingMotion, Entry> entries = new HashMap<>();
		private final ImmutableMap.Builder<LivingMotion, StaticAnimation> animations = ImmutableMap.builder();
		
		public Builder(ResourceLocation id, float convertTime, boolean repeatPlay, Function<Models<?>, Model> model)
		{
			this.id = id;
			this.convertTime = convertTime;
			this.repeatPlay = repeatPlay;
			this.model = model;
		}
		
		public Builder addEntry(LivingMotion motion, ResourceLocation path, boolean applyLayerParts)
		{
			this.entries.put(motion, new Entry(path, applyLayerParts));
			return this;
		}
		
		public Builder addEntry(LivingMotion motion, ResourceLocation path1, ResourceLocation path2, boolean applyLayerParts)
		{
			this.entries.put(motion, new MirrorEntry(path1, path2, applyLayerParts));
			return this;
		}
		
		public AdaptableAnimation build()
		{
			this.entries.forEach((motion, entry) ->
			{
				this.animations.put(motion, entry.build(this.id, motion, this.convertTime, this.repeatPlay, this.model));
			});
			return new AdaptableAnimation(this.animations.build());
		}
	}
	
	private static class Entry
	{
		protected final ResourceLocation path1;
		protected final boolean applyLayerParts;
		
		public Entry(ResourceLocation path1, boolean applyLayerParts)
		{
			this.path1 = path1;
			this.applyLayerParts = applyLayerParts;
		}
		
		protected StaticAnimation build(ResourceLocation id, LivingMotion motion, float convertTime, boolean repeatPlay, Function<Models<?>, Model> model)
		{
			StaticAnimation anim = new StaticAnimation(new ResourceLocation(id.getNamespace(), id.getPath()+"_"+motion.toString().toLowerCase()),
					convertTime, repeatPlay, this.path1, model);
			if (this.applyLayerParts)
			{
				anim.addProperty(StaticAnimationProperty.LAYER_PART, LayerPart.UP);
				anim.addProperty(StaticAnimationProperty.SHOULD_SYNC, true);
			}
			return anim;
		}
	}
	
	private static class MirrorEntry extends Entry
	{
		private final ResourceLocation path2;
		
		public MirrorEntry(ResourceLocation path1, ResourceLocation path2, boolean applyLayerParts)
		{
			super(path1, applyLayerParts);
			this.path2 = path2;
		}
		
		@Override
		protected StaticAnimation build(ResourceLocation id, LivingMotion motion, float convertTime, boolean repeatPlay, Function<Models<?>, Model> model)
		{
			MirrorAnimation anim = new MirrorAnimation(new ResourceLocation(id.getNamespace(), id.getPath()+"_"+motion.toString().toLowerCase()),
					convertTime, repeatPlay, this.applyLayerParts, this.path1, this.path2, model);
			if (this.applyLayerParts) anim.addProperty(StaticAnimationProperty.SHOULD_SYNC, true);
			return anim;
		}
	}
}
