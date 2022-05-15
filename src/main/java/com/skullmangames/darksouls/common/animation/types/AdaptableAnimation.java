package com.skullmangames.darksouls.common.animation.types;

import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.Models;

public class AdaptableAnimation extends StaticAnimation
{
	private final Map<LivingMotion, MirrorAnimation> animations;
	
	public AdaptableAnimation(float convertTime, boolean repeatPlay, Function<Models<?>, Model> model, AnimConfig... configs)
	{
		super();
		Builder<LivingMotion, MirrorAnimation> builder = ImmutableMap.builder();
		for (AnimConfig config : configs)
		{
			builder.put(config.motion, new MirrorAnimation(convertTime, repeatPlay, config.applyLayerParts, config.path1, config.path2, model));
		}
		this.animations = builder.build();
	}
	
	@Override
	public StaticAnimation checkAndReturnAnimation(LivingCap<?> entityCap)
	{
		LivingMotion motion = entityCap.currentMotion;
		if (this.animations.containsKey(motion)) return this.animations.get(motion).checkAndReturnAnimation(entityCap);
		else return Animations.DUMMY_ANIMATION;
	}
	
	public static class AnimConfig
	{
		private LivingMotion motion;
		private String path1;
		private String path2;
		private boolean applyLayerParts;
		
		public AnimConfig(LivingMotion motion, String path1, String path2, boolean applyLayerParts)
		{
			this.motion = motion;
			this.path1 = path1;
			this.path2 = path2;
			this.applyLayerParts = applyLayerParts;
		}
	}
}
