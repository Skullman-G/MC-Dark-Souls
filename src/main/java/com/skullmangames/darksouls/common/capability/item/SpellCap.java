package com.skullmangames.darksouls.common.capability.item;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.entity.stats.Stat;
import com.skullmangames.darksouls.core.util.SpellType;

import net.minecraft.world.item.Item;

public class SpellCap extends ItemCapability
{
	private final SpellType spellType;
	private final StaticAnimation castingAnim;
	@Nullable private final StaticAnimation horsebackAnim;
	private final ImmutableMap<Stat, Integer> statRequirements;
	private final float fpConsumption;
	
	public SpellCap(Item item, SpellType spellType, StaticAnimation castingAnim, @Nullable StaticAnimation horsebackAnim,
			ImmutableMap<Stat, Integer> statRequirements, float fpConsumption)
	{
		super(item);
		this.spellType = spellType;
		this.castingAnim = castingAnim;
		this.horsebackAnim = horsebackAnim;
		this.statRequirements = statRequirements;
		this.fpConsumption = fpConsumption;
	}
}
