package com.skullmangames.darksouls.common.effects;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.InstantEffect;

public class InstantHeal extends InstantEffect
{
	public InstantHeal()
	{
		super(EffectType.BENEFICIAL, 16262179);
	}
	
	@Override
	public void applyInstantenousEffect(@Nullable Entity p_180793_1_, @Nullable Entity p_180793_2_, LivingEntity getter, int p_180793_4_, double p_180793_5_)
	{
	    getter.heal(2);
	}
}
