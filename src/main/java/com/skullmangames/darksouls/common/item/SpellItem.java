package com.skullmangames.darksouls.common.item;

import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import net.minecraft.world.item.Item;

public class SpellItem extends Item
{
	private final StaticAnimation castingAnimation;
	
	public SpellItem(StaticAnimation castingAnimation, Properties properties)
	{
		super(properties.stacksTo(1));
		this.castingAnimation = castingAnimation;
	}

	public StaticAnimation getCastingAnimation()
	{
		return this.castingAnimation;
	}
}
