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

public class PickaxeCapability extends MaterialItemCapability {
	public PickaxeCapability(Item item) {
		super(item, WeaponCategory.PICKAXE);
	}
	
	@Override
	protected void registerAttribute() {
		this.addStyleAttibute(WieldStyle.ONE_HAND, Pair.of(AttributeInit.IMPACT, AttributeInit.getImpactModifier(-0.4D + 0.1D * this.itemTier.getLevel())));
		this.addStyleAttibute(WieldStyle.ONE_HAND, Pair.of(AttributeInit.ARMOR_NEGATION, AttributeInit.getArmorNegationModifier(6.0D * this.itemTier.getLevel())));
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