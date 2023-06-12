package com.skullmangames.darksouls.core.util;

import java.util.function.BiConsumer;

import com.skullmangames.darksouls.common.capability.entity.LivingCap;

import net.minecraft.resources.ResourceLocation;

public class AuxEffect
{
	private final ResourceLocation id;
	private final BiConsumer<LivingCap<?>, ExtendedDamageSource> action;
	
	public AuxEffect(ResourceLocation id, BiConsumer<LivingCap<?>, ExtendedDamageSource> action)
	{
		this.id = id;
		this.action = action;
	}
	
	public ResourceLocation getId()
	{
		return this.id;
	}
	
	public void apply(LivingCap<?> cap, ExtendedDamageSource dmgSource)
	{
		this.action.accept(cap, dmgSource);
	}
	
	@Override
	public String toString()
	{
		return this.getId().toString();
	}
}
