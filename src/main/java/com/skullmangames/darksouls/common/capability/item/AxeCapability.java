package com.skullmangames.darksouls.common.capability.item;

import java.util.ArrayList;
import java.util.List;

import com.mojang.datafixers.util.Pair;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.util.physics.Collider;

import net.minecraft.item.Item;

public class AxeCapability extends MaterialItemCapability
{
	public static List<StaticAnimation> axeAttackMotions = new ArrayList<StaticAnimation>();
	
	static
	{
		axeAttackMotions = new ArrayList<StaticAnimation>();
	}
	
	public AxeCapability(Item item)
	{
		super(item, WeaponCategory.AXE);
	}

	@Override
	protected AttackAnimation getHeavyAttack()
	{
		return Animations.GUILLOTINE_AXE;
	}
	
	@Override
	protected AttackAnimation getDashAttack()
	{
		return Animations.AXE_DASH_ATTACK;
	}
	
	@Override
	protected void registerAttribute()
	{
		int i = this.itemTier.getLevel();
		this.addStyleAttibute(WieldStyle.ONE_HAND, Pair.of(ModAttributes.IMPACT, ModAttributes.getImpactModifier(0.7D + 0.3D * i)));
	}

	@Override
	public Collider getWeaponCollider()
	{
		return Colliders.tools;
	}
}