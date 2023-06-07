package com.skullmangames.darksouls.core.util;

import com.skullmangames.darksouls.core.util.math.MathUtils;

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

	public IndirectDamageSourceExtended(String damageTypeIn, Entity source, Entity owner, StunType stunType, float poiseDamage, float staminaDamage, Damages damages)
	{
		super(damageTypeIn, source, owner);
		this.stunType = stunType;
		this.damages = damages;
		this.poiseDamage = poiseDamage;
		this.staminaDamage = staminaDamage;
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
	public int getRequiredDeflectionLevel()
	{
		return 0;
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
		float yRot = MathUtils.toNormalRot(target.getYRot());
		return Math.abs(-yRot - attackAngle);
	}

	@Override
	public Vec3 getAttackPos()
	{
		return this.getSource().position();
	}
}