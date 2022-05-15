package com.skullmangames.darksouls.client.animation;

import com.skullmangames.darksouls.common.animation.Property.StaticAnimationProperty;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientAnimationProperties
{
	public static final StaticAnimationProperty<AnimationLayer.LayerPart> LAYER_PART = new StaticAnimationProperty<AnimationLayer.LayerPart>();
}
