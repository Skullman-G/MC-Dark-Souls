package com.skullmangames.darksouls.common.capability.item;

import com.mojang.datafixers.util.Pair;
import com.skullmangames.darksouls.common.particle.HitParticleType;
import com.skullmangames.darksouls.core.init.AttributeInit;
import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.util.physics.Collider;

import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;

public class ShovelCapability extends MaterialItemCapability {
	public ShovelCapability(Item item) {
		super(item, WeaponCategory.SHOVEL);
	}
	
	@Override
	protected void registerAttribute() {
		double impact = this.itemTier.getLevel() * 0.4D + 0.8D;
		this.addStyleAttibute(WieldStyle.ONE_HAND, Pair.of(AttributeInit.IMPACT, AttributeInit.getImpactModifier(impact)));
	}

	@Override
	public SoundEvent getHitSound() {
		return null;
	}

	@Override
	public HitParticleType getHitParticle() {
		return null;
	}

	@Override
	public Collider getWeaponCollider() {
		return Colliders.tools;
	}
}