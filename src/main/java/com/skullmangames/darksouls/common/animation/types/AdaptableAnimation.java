package com.skullmangames.darksouls.common.animation.types;

import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.util.Pair;
import com.skullmangames.darksouls.common.animation.LivingMotion;

import net.minecraft.world.InteractionHand;

public class AdaptableAnimation extends StaticAnimation
{
	private final Map<LivingMotion, MirrorAnimation> animations;
	
	public AdaptableAnimation(float convertTime, boolean repeatPlay, String armature, Map<LivingMotion, Pair<String, String>> paths)
	{
		super();
		Builder<LivingMotion, MirrorAnimation> builder = ImmutableMap.builder();
		paths.forEach((motion, path) ->
		{
			builder.put(motion, new MirrorAnimation(convertTime, repeatPlay, path.getFirst(), path.getSecond(), armature, true, true));
		});
		this.animations = builder.build();
	}
	
	@Nullable
	public StaticAnimation getAnimation(LivingMotion motion, InteractionHand hand)
	{
		if (this.animations.containsKey(motion)) return this.animations.get(motion).getAnimation(hand);
		else return null;
	}
}
