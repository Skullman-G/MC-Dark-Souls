package com.skullmangames.darksouls.common.item;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import net.minecraft.world.item.Item;

public class SpellItem extends Item
{
	private final Supplier<StaticAnimation> castingAnim;
	@Nullable private final Supplier<StaticAnimation> horsebackAnim;
	private final int reqFaith;
	private final float fpConsumption;
	
	public SpellItem(Supplier<StaticAnimation> castingAnim, int reqFaith, float fpConsumption, Properties properties)
	{
		this(castingAnim, null, reqFaith, fpConsumption, properties);
	}
	
	public SpellItem(Supplier<StaticAnimation> castingAnim, Supplier<StaticAnimation> horsebackAnim, int reqFaith, float fpConsumption, Properties properties)
	{
		super(properties.stacksTo(1));
		this.castingAnim = castingAnim;
		this.horsebackAnim = horsebackAnim;
		this.reqFaith = reqFaith;
		this.fpConsumption = fpConsumption;
	}

	public StaticAnimation getCastingAnimation()
	{
		return this.castingAnim.get();
	}
	
	@Nullable
	public StaticAnimation getHorsebackAnimation()
	{
		return this.horsebackAnim.get();
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
