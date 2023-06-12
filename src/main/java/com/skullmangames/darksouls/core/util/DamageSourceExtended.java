package com.skullmangames.darksouls.core.util;

import java.util.HashSet;
import java.util.Set;

import com.skullmangames.darksouls.core.util.math.MathUtils;

import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class DamageSourceExtended extends EntityDamageSource implements ExtendedDamageSource
{
	private final int requiredDeflectionLevel;
	private final float poiseDamage;
	private final float staminaDamage;
	private final Vec3 attackPos;
	private boolean wasBlocked;
	private StunType stunType;
	private final Damages damages;
	private final Set<AuxEffect> auxEffects = new HashSet<>();
	
	public DamageSourceExtended(String damageTypeIn, Entity source, Vec3 attackPos, StunType stunType,
			int requiredDeflectionLevel, float poiseDamage, float staminaDamage, Damages damages)
	{
		super(damageTypeIn, source);
		
		this.stunType = stunType;
		this.damages = damages;
		this.poiseDamage = poiseDamage;
		this.requiredDeflectionLevel = requiredDeflectionLevel;
		this.staminaDamage = staminaDamage;
		this.attackPos = attackPos;
	}
	
	@Override
	public boolean isIndirect()
	{
		return false;
	}
	
	@Override
	public float getAmount()
	{
		return this.damages.getFullAmount();
	}
	
	@Override
	public int getRequiredDeflectionLevel()
	{
		return this.requiredDeflectionLevel;
	}

	@Override
	public StunType getStunType()
	{
		return this.stunType;
	}

	@Override
	public Entity getOwner()
	{
		return super.getDirectEntity();
	}

	@Override
	public String getType()
	{
		return super.getMsgId();
	}

	@Override
	public Damages getDamages()
	{
		return this.damages;
	}

	@Override
	public float getPoiseDamage()
	{
		return this.poiseDamage;
	}

	@Override
	public boolean isHeadshot()
	{
		return false;
	}

	@Override
	public void setHeadshot(boolean value) {}

	@Override
	public float getStaminaDamage()
	{
		return this.staminaDamage;
	}

	@Override
	public void setStunType(StunType value)
	{
		this.stunType = value;
	}

	@Override
	public Entity getSource()
	{
		return this.getDirectEntity();
	}

	@Override
	public boolean wasBlocked()
	{
		return this.wasBlocked;
	}

	@Override
	public void setWasBlocked(boolean value)
	{
		this.wasBlocked = value;
	}

	@Override
	public float getAttackAngle(Entity target)
	{
		Vec3 attacker = this.getSource().position();
		float attackAngle = ((float)Math.toDegrees(Math.atan2(target.getX() - attacker.x, target.getZ() - attacker.z)) + 360F) % 360F;
		float yRot = MathUtils.toNormalRot(target.getYRot());
		return Math.abs(-yRot - attackAngle);
	}

	@Override
	public Vec3 getAttackPos()
	{
		return this.attackPos;
	}

	@Override
	public Set<AuxEffect> getAuxEffects()
	{
		return this.auxEffects;
	}

	@Override
	public DamageSourceExtended addAuxEffect(AuxEffect auxEffect)
	{
		this.auxEffects.add(auxEffect);
		return this;
	}

	@Override
	public DamageSourceExtended addAuxEffects(Set<AuxEffect> auxEffects)
	{
		this.auxEffects.addAll(auxEffects);
		return this;
	}
}