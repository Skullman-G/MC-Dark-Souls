package com.skullmangames.darksouls.common.item;

import javax.annotation.Nullable;

import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import net.minecraft.world.item.Item;

public class SpellItem extends Item
{
	private final StaticAnimation castingAnim;
	@Nullable
	private final StaticAnimation horsebackAnim;
	private final int reqFaith;
	private final float fpConsumption;
	
	public SpellItem(StaticAnimation castingAnim, int reqFaith, float fpConsumption, Properties properties)
	{
		this(castingAnim, null, reqFaith, fpConsumption, properties);
	}
	
	public SpellItem(StaticAnimation castingAnim, StaticAnimation horsebackAnim, int reqFaith, float fpConsumption, Properties properties)
	{
		super(properties.stacksTo(1));
		this.castingAnim = castingAnim;
		this.horsebackAnim = horsebackAnim;
		this.reqFaith = reqFaith;
		this.fpConsumption = fpConsumption;
	}

	public StaticAnimation getCastingAnimation()
	{
		return this.castingAnim;
	}
	
	@Nullable
	public StaticAnimation getHorsebackAnimation()
	{
		return this.horsebackAnim;
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
