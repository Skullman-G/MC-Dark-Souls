package com.skullmangames.darksouls.common.item;

import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import net.minecraft.world.item.Item;

public class SpellItem extends Item
{
	private final StaticAnimation castingAnimation;
	private final float fpConsumption;
	
	public SpellItem(StaticAnimation castingAnimation, float fpConsumption, Properties properties)
	{
		super(properties.stacksTo(1));
		this.castingAnimation = castingAnimation;
		this.fpConsumption = fpConsumption;
	}

	public StaticAnimation getCastingAnimation()
	{
		return this.castingAnimation;
	}
	
	public float getFPConsumption()
	{
		return this.fpConsumption;
	}
}
