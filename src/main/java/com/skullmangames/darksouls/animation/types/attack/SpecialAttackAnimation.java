package com.skullmangames.darksouls.animation.types.attack;

import javax.annotation.Nullable;

import com.skullmangames.darksouls.animation.property.Property.DamageProperty;
import com.skullmangames.darksouls.common.entities.LivingData;
import com.skullmangames.darksouls.physics.Collider;
import com.skullmangames.darksouls.util.math.ValueCorrector;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.util.Hand;

public class SpecialAttackAnimation extends AttackAnimation
{
	public SpecialAttackAnimation(int id, float convertTime, float antic, float preDelay, float contact, float recovery, boolean affectY, @Nullable Collider collider,
			String index, String path)
	{
		this(id, convertTime, affectY, path, new Phase(antic, preDelay, contact, recovery, index, collider));
	}
	
	public SpecialAttackAnimation(int id, float convertTime, float antic, float preDelay, float contact, float recovery, boolean affectY, Hand hand, @Nullable Collider collider,
			String index, String path)
	{
		this(id, convertTime, affectY, path, new Phase(antic, preDelay, contact, recovery, hand, index, collider));
	}
	
	public SpecialAttackAnimation(int id, float convertTime, boolean affectY, String path, Phase... phases)
	{
		super(id, convertTime, affectY, path, phases);
	}
	
	@Override
	protected float getDamageAmount(LivingData<?> entitydata, Entity target, Phase phase)
	{
		float f = entitydata.getDamageToEntity(target, phase.hand);
		int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, entitydata.getOriginalEntity());
		ValueCorrector cor = new ValueCorrector(0, (i > 0) ? (float)i / (float)(i + 1.0F) : 0.0F, 0);
		phase.getProperty(DamageProperty.DAMAGE).ifPresent((opt)->cor.merge(opt));
		return cor.get(f);
	}
}