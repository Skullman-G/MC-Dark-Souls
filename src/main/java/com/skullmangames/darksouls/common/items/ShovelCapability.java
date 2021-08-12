package com.skullmangames.darksouls.common.items;

import java.util.List;

import com.mojang.datafixers.util.Pair;
import com.skullmangames.darksouls.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.entities.PlayerData;
import com.skullmangames.darksouls.common.particle.HitParticleType;
import com.skullmangames.darksouls.core.init.AttributeInit;
import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.physics.Collider;

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
	public List<StaticAnimation> getAutoAttckMotion(PlayerData<?> playerdata) {
		return AxeCapability.axeAttackMotions;
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