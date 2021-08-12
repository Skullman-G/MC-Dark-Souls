package com.skullmangames.darksouls.common.items;

import java.util.ArrayList;
import java.util.List;

import com.mojang.datafixers.util.Pair;
import com.skullmangames.darksouls.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.entities.LivingData;
import com.skullmangames.darksouls.common.entities.PlayerData;
import com.skullmangames.darksouls.common.particle.HitParticleType;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.AttributeInit;
import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.physics.Collider;

import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;

public class TridentCapability extends RangedWeaponCapability {
	private static List<StaticAnimation> attackMotion;
	private static List<StaticAnimation> mountAttackMotion;
	
	public TridentCapability(Item item) {
		super(item, null, Animations.BIPED_JAVELIN_AIM, Animations.BIPED_JAVELIN_REBOUND);

		if (attackMotion == null) {
			attackMotion = new ArrayList<StaticAnimation> ();
			attackMotion.add(Animations.SPEAR_ONEHAND_AUTO);
			attackMotion.add(Animations.SPEAR_DASH);
		}
		
		if (mountAttackMotion == null) {
			mountAttackMotion = new ArrayList<StaticAnimation> ();
			mountAttackMotion.add(Animations.SPEAR_MOUNT_ATTACK);
		}
	}
	
	@Override
	public WieldStyle getStyle(LivingData<?> entitydata) {
		return WieldStyle.ONE_HAND;
	}
	
	@Override
	protected void registerAttribute() {
		this.addStyleAttibute(WieldStyle.ONE_HAND, Pair.of(AttributeInit.IMPACT, AttributeInit.getImpactModifier(2.25D)));
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
		return Colliders.spearNarrow;
	}
	
	@Override
	public List<StaticAnimation> getAutoAttckMotion(PlayerData<?> playerdata) {
		return attackMotion;
	}
	
	@Override
	public List<StaticAnimation> getMountAttackMotion() {
		return mountAttackMotion;
	}
	
	@Override
	public final HandProperty getHandProperty() {
		return HandProperty.MAINHAND_ONLY;
	}
}