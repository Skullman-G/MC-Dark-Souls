package com.skullmangames.darksouls.common.capability.item;

import java.util.List;

import com.mojang.datafixers.util.Pair;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.PlayerData;
import com.skullmangames.darksouls.common.particle.HitParticleType;
import com.skullmangames.darksouls.core.init.AttributeInit;
import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.util.physics.Collider;

import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;

public class HoeCapability extends MaterialItemCapability {
	public HoeCapability(Item item) {
		super(item, WeaponCategory.HOE);
	}
	
	@Override
	protected void registerAttribute() {
		this.addStyleAttibute(WieldStyle.ONE_HAND, Pair.of(AttributeInit.IMPACT, AttributeInit.getImpactModifier(-0.4D + 0.1D * this.itemTier.getLevel())));
	}
	
	@Override
	public List<StaticAnimation> getAutoAttckMotion(PlayerData<?> playerdata) {
		return toolAttackMotion;
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