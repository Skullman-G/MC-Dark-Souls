package com.skullmangames.darksouls.core.init;

import java.util.LinkedHashMap;
import java.util.Map;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.entity.Demon;
import com.skullmangames.darksouls.core.util.AuxEffect;

import net.minecraft.resources.ResourceLocation;

public class AuxEffects
{
	private static final Map<ResourceLocation, AuxEffect> EFFECTS = new LinkedHashMap<>();
	
	public static final AuxEffect ANTI_DEMON = register(new AuxEffect(DarkSouls.rl("anti_demon"), (targetCap, source) ->
	{
		if (targetCap.getOriginalEntity() instanceof Demon) source.getDamages().mul(1.20F);
	}));
	
	private static AuxEffect register(AuxEffect effect)
	{
		EFFECTS.put(effect.getId(), effect);
		return effect;
	}
	
	public static AuxEffect fromId(ResourceLocation id)
	{
		return EFFECTS.get(id);
	}
}
