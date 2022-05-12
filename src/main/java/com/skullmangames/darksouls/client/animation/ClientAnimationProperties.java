package com.skullmangames.darksouls.client.animation;

import com.skullmangames.darksouls.common.animation.types.attack.Property.StaticAnimationProperty;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientAnimationProperties
{
	public static final StaticAnimationProperty<AnimationLayer.LayerType> LAYER_TYPE = new StaticAnimationProperty<AnimationLayer.LayerType>();
	public static final StaticAnimationProperty<AnimationLayer.Priority> PRIORITY = new StaticAnimationProperty<AnimationLayer.Priority>();
	public static final StaticAnimationProperty<JointMaskEntry> POSE_MODIFIER = new StaticAnimationProperty<JointMaskEntry>();
}
