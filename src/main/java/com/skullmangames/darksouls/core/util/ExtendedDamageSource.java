package com.skullmangames.darksouls.core.util;

import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.capability.item.IShield.Deflection;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

public interface ExtendedDamageSource
{
	public static DamageSourceExtended causePlayerDamage(Player player, Vec3 attackPos, StunType stunType, int reqDeflection, float poiseDamage, float staminaDamage, Damage... damages)
	{
        return new DamageSourceExtended("player", player, attackPos, stunType, reqDeflection, poiseDamage, staminaDamage, damages);
    }
	
	public static DamageSourceExtended causeMobDamage(LivingEntity mob, Vec3 attackPos, StunType stunType, int reqDeflection, float poiseDamage, float staminaDamage, Damage... damages)
	{
        return new DamageSourceExtended("mob", mob, attackPos, stunType, reqDeflection, poiseDamage, staminaDamage, damages);
    }
	
	public static IndirectDamageSourceExtended causeProjectileDamage(Projectile projectile, Entity owner, StunType stunType, float poiseDamage, float staminaDamage, Damage... damages)
	{
		return new IndirectDamageSourceExtended("projectile", projectile, owner, stunType, poiseDamage, staminaDamage, damages);
	}
	
	public static DamageSourceExtended getDirectFrom(ExtendedDamageSource org)
	{
		return new DamageSourceExtended(org.getType(), org.getOwner(), org.getAttackPos(), org.getStunType(), org.getRequiredDeflectionLevel(), org.getPoiseDamage(), org.getStaminaDamage(), org.getDamages());
	}
	
	public static DamageSourceExtended getDirectFrom(DamageSource org, float amount)
	{
		int reqDeflection = amount > 5.0F ? Deflection.MEDIUM.getLevel() : amount > 10.0F ? Deflection.HEAVY.getLevel() : Deflection.LIGHT.getLevel();
		StunType stunType = amount > 10.0F ? StunType.SMASH : StunType.LIGHT;
		float poiseDamage = 1.0F;
		float staminaDamage = 1.0F;
		
		Entity attacker = org.getDirectEntity();
		LivingCap<?> cap = org.getDirectEntity() instanceof LivingEntity ? (LivingCap<?>)org.getDirectEntity().getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null) : null;
		if (cap != null)
		{
			MeleeWeaponCap weapon = cap.getHeldWeaponCapability(InteractionHand.MAIN_HAND);
			if (weapon != null)
			{
				poiseDamage = weapon.poiseDamage;
				staminaDamage = weapon.getStaminaDamage();
			}
			
		}
		
		return new DamageSourceExtended(org.getMsgId(), attacker, attacker != null ? attacker.position() : Vec3.ZERO, stunType, reqDeflection, poiseDamage, staminaDamage, new Damage(DamageType.REGULAR, amount));
	}
	
	public static IndirectDamageSourceExtended getIndirectFrom(ExtendedDamageSource org)
	{
		return new IndirectDamageSourceExtended(org.getType(), org.getSource(), org.getOwner(), org.getStunType(), org.getPoiseDamage(), org.getStaminaDamage(), org.getDamages());
	}
	
	public static IndirectDamageSourceExtended getIndirectFrom(IndirectEntityDamageSource org, float amount)
	{
		return new IndirectDamageSourceExtended(org.getMsgId(), org.getDirectEntity(), org.getEntity(), StunType.LIGHT, 1.0F, 0.0F, new Damage(DamageType.REGULAR, amount));
	}
	
	public static ExtendedDamageSource getFrom(DamageSource org, float amount)
	{
		if (org instanceof ExtendedDamageSource) return (ExtendedDamageSource)org;
		else if (org instanceof IndirectEntityDamageSource) return getIndirectFrom((IndirectEntityDamageSource)org, amount);
		else return getDirectFrom(org, amount);
	}
	
	public float getAmount();
	public StunType getStunType();
	public Entity getOwner();
	public Entity getSource();
	public String getType();
	public int getRequiredDeflectionLevel();
	public Damage[] getDamages();
	public float getPoiseDamage();
	public boolean isHeadshot();
	public void setHeadshot(boolean value);
	public float getStaminaDamage();
	public void setStunType(StunType value);
	public boolean isIndirect();
	public boolean wasBlocked();
	public void setWasBlocked(boolean value);
	public float getAttackAngle(Entity target);
	public Vec3 getAttackPos();
	
	public enum StunType
	{
		NONE(0), DISARMED(0), LIGHT(1), HEAVY(2), SMASH(3), FLY(3);
		
		private final int level;
		
		StunType(int level)
		{
			this.level = level;
		}
		
		public int getLevel()
		{
			return this.level;
		}
		
		public StunType downgrade()
		{
			if (this.level == 3) return HEAVY;
			else if (this.level > 0) return NONE;
			else return this;
		}
	}
	
	public enum DamageType
	{
		REGULAR, STRIKE, SLASH, THRUST, FIRE, LIGHTNING;
		
		public Attribute getDefenseAttribute()
		{
			switch (this)
			{
				default: return ModAttributes.STANDARD_DEFENSE.get();
				case STRIKE: return ModAttributes.STRIKE_DEFENSE.get();
				case SLASH: return ModAttributes.SLASH_DEFENSE.get();
				case THRUST: return ModAttributes.THRUST_DEFENSE.get();
				case FIRE: return ModAttributes.FIRE_DEFENSE.get();
				case LIGHTNING: return ModAttributes.LIGHTNING_DEFENSE.get();
			}
		}
	}
	
	public class Damage
	{
		private DamageType type;
		private float amount;
		
		public Damage(DamageType type, float amount)
		{
			this.type = type;
			this.amount = amount;
		}
		
		public DamageType getType()
		{
			return this.type;
		}
		
		public float getAmount()
		{
			return this.amount;
		}
		
		public void setAmount(float value)
		{
			this.amount = value;
		}
	}
}