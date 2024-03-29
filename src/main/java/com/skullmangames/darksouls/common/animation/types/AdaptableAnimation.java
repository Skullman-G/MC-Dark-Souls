package com.skullmangames.darksouls.common.animation.types;

import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
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
	private final Map<LivingMotion, MirrorAnimation> animations;
	
	public AdaptableAnimation(ResourceLocation id, float convertTime, boolean repeatPlay, Function<Models<?>, Model> model, AnimConfig... configs)
	{
		super();
		ImmutableMap.Builder<LivingMotion, MirrorAnimation> builder = ImmutableMap.builder();
		for (AnimConfig config : configs)
		{
			MirrorAnimation anim = new MirrorAnimation(new ResourceLocation(id.getNamespace(), id.getPath()+"_"+config.motion.toString().toLowerCase()),
					convertTime, repeatPlay, config.applyLayerParts, config.path1, config.path2, model);
			if (config.applyLayerParts)
			{
				anim.addProperty(StaticAnimationProperty.SHOULD_SYNC, true);
				anim.mirror.addProperty(StaticAnimationProperty.SHOULD_SYNC, true);
			}
			builder.put(config.motion, anim);
		}
		this.animations = builder.build();
	}
	
	@Override
	public StaticAnimation checkAndReturnAnimation(LivingCap<?> entityCap, LayerPart layerPart)
	{
		LivingMotion motion = entityCap.currentMotion;
		if (this.animations.containsKey(motion)) return this.animations.get(motion).checkAndReturnAnimation(entityCap, layerPart);
		else return Animations.DUMMY_ANIMATION;
	}
	
	@Override
	public StaticAnimation register(Builder<ResourceLocation, StaticAnimation> builder)
	{
		for (MirrorAnimation a : this.animations.values()) a.register(builder);
		return this;
	}
	
	public static class AnimConfig
	{
		private LivingMotion motion;
		private ResourceLocation path1;
		private ResourceLocation path2;
		private boolean applyLayerParts;
		
		public AnimConfig(LivingMotion motion, ResourceLocation path1, ResourceLocation path2, boolean applyLayerParts)
		{
			this.motion = motion;
			this.path1 = path1;
			this.path2 = path2;
			this.applyLayerParts = applyLayerParts;
		}
	}
}
