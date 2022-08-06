package com.skullmangames.darksouls.common.item;

import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import net.minecraft.world.item.Item;

public class SpellItem extends Item
{
	private final StaticAnimation castingAnimation;
	private final int reqFaith;
	private final float fpConsumption;
	
	public SpellItem(StaticAnimation castingAnimation, int reqFaith, float fpConsumption, Properties properties)
	{
		super(properties.stacksTo(1));
		this.castingAnimation = castingAnimation;
		this.reqFaith = reqFaith;
		this.fpConsumption = fpConsumption;
	}

	public StaticAnimation getCastingAnimation()
	{
		return this.castingAnimation;
	}
	
	public int getRequiredFaith()
	{
		return this.reqFaith;
	}
	
	public float getFPConsumption()
	{
		return this.fpConsumption;
	}
}
