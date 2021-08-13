package com.skullmangames.darksouls.common.capability.item;

import java.util.ArrayList;
import java.util.List;

import com.mojang.datafixers.util.Pair;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.PlayerData;
import com.skullmangames.darksouls.common.particle.HitParticleType;
import com.skullmangames.darksouls.common.skill.Skill;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.AttributeInit;
import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.init.Skills;
import com.skullmangames.darksouls.core.util.physics.Collider;

import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;

public class AxeCapability extends MaterialItemCapability
{
	public static List<StaticAnimation> axeAttackMotions = new ArrayList<StaticAnimation> ();
	
	static
	{
		axeAttackMotions = new ArrayList<StaticAnimation> ();
		axeAttackMotions.add(Animations.AXE_AUTO1);
		axeAttackMotions.add(Animations.AXE_AUTO2);
		axeAttackMotions.add(Animations.AXE_DASH);
	}
	
	public AxeCapability(Item item) {
		super(item, WeaponCategory.AXE);
	}
	
	@Override
	public List<StaticAnimation> getAutoAttckMotion(PlayerData<?> playerdata) {
		return axeAttackMotions;
	}

	@Override
	public Skill getSpecialAttack(PlayerData<?> playerdata) {
		return Skills.GUILLOTINE_AXE;
	}
	
	@Override
	protected void registerAttribute() {
		int i = this.itemTier.getLevel();
		
		if(i != 0) {
			this.addStyleAttibute(WieldStyle.ONE_HAND, Pair.of(AttributeInit.ARMOR_NEGATION, AttributeInit.getArmorNegationModifier(10.0D * i)));
		}
		
		this.addStyleAttibute(WieldStyle.ONE_HAND, Pair.of(AttributeInit.IMPACT, AttributeInit.getImpactModifier(0.7D + 0.3D * i)));
	}
	
	@Override
	public HitParticleType getHitParticle() {
		return null;
	}
	
	@Override
	public SoundEvent getHitSound() {
		return null;
	}

	@Override
	public Collider getWeaponCollider() {
		return Colliders.tools;
	}
}