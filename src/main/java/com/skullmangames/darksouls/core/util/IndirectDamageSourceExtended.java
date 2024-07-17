package com.skullmangames.darksouls.core.util;

import java.util.HashSet;
import java.util.Set;

import com.skullmangames.darksouls.common.capability.item.Shield.Deflection;
import com.skullmangames.darksouls.core.util.math.ModMath;

import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class IndirectDamageSourceExtended extends IndirectEntityDamageSource implements ExtendedDamageSource
{
	private final float staminaDamage;
	private final float poiseDamage;
	private boolean headshot;
	private boolean wasBlocked;
	private StunType stunType;
	private final Damages damages;
	private Set<AuxEffect> auxEffects = new HashSet<>();

	public IndirectDamageSourceExtended(String damageTypeIn, Entity source, Entity owner, StunType stunType,
			float poiseDamage, float staminaDamage, Damages damages)
	{
		super(damageTypeIn, source, owner);
		this.stunType = stunType;
		this.damages = damages;
		this.poiseDamage = poiseDamage;
		this.staminaDamage = staminaDamage;
	}
	
	@Override
	public IndirectDamageSourceExtended addAuxEffect(AuxEffect auxEffect)
	{
		this.auxEffects.add(auxEffect);
		return this;
	}
	
	@Override
	public IndirectDamageSourceExtended addAuxEffects(Set<AuxEffect> auxEffects)
	{
		this.auxEffects.addAll(auxEffects);
		return this;
	}
	
	@Override
	public Set<AuxEffect> getAuxEffects()
	{
		return this.auxEffects;
	}
	
	@Override
	public boolean isIndirect()
	{
		return true;
	}
	
	@Override
	public float getAmount()
	{
		return this.damages.getFullAmount();
	}

	@Override
	public Deflection getRequiredDeflection()
	{
		return Deflection.NONE;
	}

	@Override
	public StunType getStunType()
	{
		return this.stunType;
	}

	@Override
	public Entity getOwner()
	{
		return this.getEntity();
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
		return this.headshot;
	}

	@Override
	public void setHeadshot(boolean value)
	{
		this.headshot = value;
	}

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
		Vec3 attacker = this.getDirectEntity().position();
		float attackAngle = ((float)Math.toDegrees(Math.atan2(target.getX() - attacker.x, target.getZ() - attacker.z)) + 360F) % 360F;
		float yRot = ModMath.toNormalRot(target.getYRot());
		return Math.abs(-yRot - attackAngle);
	}

	@Override
	public Vec3 getAttackPos()
	{
		return this.getSource().position();
	}
}