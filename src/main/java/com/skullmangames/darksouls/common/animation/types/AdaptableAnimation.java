package com.skullmangames.darksouls.common.animation.types;

import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import net.minecraft.world.InteractionHand;

// This should only be used by a mix layer
public class AdaptableAnimation extends StaticAnimation
{
	private final Map<LivingMotion, MirrorAnimation> animations;
	
	public AdaptableAnimation(float convertTime, boolean repeatPlay, String armature, Map<LivingMotion, AnimConfig> paths)
	{
		super();
		Builder<LivingMotion, MirrorAnimation> builder = ImmutableMap.builder();
		paths.forEach((motion, config) ->
		{
			builder.put(motion, new MirrorAnimation(convertTime, repeatPlay, config.path1, config.path2, armature, true, false, config.sync));
		});
		this.animations = builder.build();
	}
	
	@Nullable
	public StaticAnimation getAnimation(LivingMotion motion, InteractionHand hand)
	{
		if (this.animations.containsKey(motion)) return this.animations.get(motion).getAnimation(hand);
		else return null;
	}
	
	public static class AnimConfig
	{
		private String path1;
		private String path2;
		private boolean sync;
		
		public AnimConfig(String path1, String path2, boolean sync)
		{
			this.path1 = path1;
			this.path2 = path2;
			this.sync = sync;
		}
	}
}
