package com.skullmangames.darksouls.core.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import com.skullmangames.darksouls.common.capability.item.Shield.Deflection;
import com.skullmangames.darksouls.core.init.ModAttributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

public interface ExtendedDamageSource
{
	public static DamageSourceExtended causePlayerDamage(Player player, Vec3 attackPos, StunType stunType,
			int reqDeflection, float poiseDamage, float staminaDamage, Damages damages)
	{
        return new DamageSourceExtended("player", player, attackPos, stunType, reqDeflection, poiseDamage, staminaDamage, damages);
    }
	
	public static DamageSourceExtended causeMobDamage(LivingEntity mob, Vec3 attackPos, StunType stunType,
			int reqDeflection, float poiseDamage, float staminaDamage, Damages damages)
	{
        return new DamageSourceExtended("mob", mob, attackPos, stunType, reqDeflection, poiseDamage, staminaDamage, damages);
    }
	
	public static IndirectDamageSourceExtended causeProjectileDamage(Projectile projectile, Entity owner, StunType stunType,
			float poiseDamage, float staminaDamage, Damages damages)
	{
		return new IndirectDamageSourceExtended("projectile", projectile, owner, stunType, poiseDamage, staminaDamage, damages);
	}
	
	public static DamageSourceExtended getDirectFrom(ExtendedDamageSource org)
	{
		DamageSourceExtended source = new DamageSourceExtended(org.getType(), org.getOwner(), org.getAttackPos(), org.getStunType(),
				org.getRequiredDeflectionLevel(), org.getPoiseDamage(), org.getStaminaDamage(), org.getDamages());
		source.addAuxEffects(org.getAuxEffects());
		return source;
	}
	
	public static DamageSourceExtended getDirectFrom(DamageSource org, float amount)
	{
		int reqDeflection = amount > 5.0F ? Deflection.MEDIUM.getLevel() : amount > 10.0F ? Deflection.HEAVY.getLevel() : Deflection.LIGHT.getLevel();
		StunType stunType = amount > 10.0F ? StunType.SMASH : StunType.LIGHT;
		float poiseDamage = 1.0F;
		float staminaDamage = 1.0F;
		
		Entity attacker = org.getDirectEntity();
		
		return new DamageSourceExtended(org.getMsgId(), attacker, attacker != null ? attacker.position() : Vec3.ZERO,
				stunType, reqDeflection, poiseDamage, staminaDamage, Damages.create().put(CoreDamageType.PHYSICAL, amount));
	}
	
	public static IndirectDamageSourceExtended getIndirectFrom(ExtendedDamageSource org)
	{
		return new IndirectDamageSourceExtended(org.getType(), org.getSource(), org.getOwner(), org.getStunType(),
				org.getPoiseDamage(), org.getStaminaDamage(), org.getDamages()).addAuxEffects(org.getAuxEffects());
	}
	
	public static IndirectDamageSourceExtended getIndirectFrom(IndirectEntityDamageSource org, float amount)
	{
		return new IndirectDamageSourceExtended(org.getMsgId(), org.getDirectEntity(), org.getEntity(),
				StunType.LIGHT, 1.0F, 0.0F, Damages.create().put(CoreDamageType.PHYSICAL, amount));
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
	public Damages getDamages();
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
	public Set<AuxEffect> getAuxEffects();
	public ExtendedDamageSource addAuxEffect(AuxEffect auxEffect);
	public ExtendedDamageSource addAuxEffects(Set<AuxEffect> auxEffects);
	
	public enum StunType
	{
		NONE(0), BACKSTABBED(0), DISARMED(0), LIGHT(1), HEAVY(2), SMASH(3), FLY(3);
		
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
			if (this.level >= 3) return HEAVY;
			else if (this.level > 0) return NONE;
			else return this;
		}
	}
	
	public static interface DamageType
	{
		public Attribute getDefenseAttribute();
		public CoreDamageType coreType();
	}
	
	public enum CoreDamageType implements DamageType
	{
		PHYSICAL, MAGIC, FIRE, LIGHTNING, HOLY, DARK;
		
		public static Map<DamageType, Float> damages(LivingEntity entity)
		{
			Map<DamageType, Float> map = new HashMap<>();
			map.put(PHYSICAL, (float)entity.getAttributeValue(Attributes.ATTACK_DAMAGE));
			map.put(MAGIC, (float)entity.getAttributeValue(ModAttributes.MAGIC_DAMAGE.get()));
			map.put(FIRE, (float)entity.getAttributeValue(ModAttributes.FIRE_DAMAGE.get()));
			map.put(LIGHTNING, (float)entity.getAttributeValue(ModAttributes.LIGHTNING_DAMAGE.get()));
			map.put(HOLY, (float)entity.getAttributeValue(ModAttributes.HOLY_DAMAGE.get()));
			map.put(DARK, (float)entity.getAttributeValue(ModAttributes.DARK_DAMAGE.get()));
			return map;
		}

		@Override
		public Attribute getDefenseAttribute()
		{
			switch (this)
			{
				default: return ModAttributes.STANDARD_PROTECTION.get();
				case MAGIC: return ModAttributes.MAGIC_PROTECTION.get();
				case FIRE: return ModAttributes.FIRE_PROTECTION.get();
				case LIGHTNING: return ModAttributes.LIGHTNING_PROTECTION.get();
				case DARK: return ModAttributes.DARK_PROTECTION.get();
				case HOLY: return ModAttributes.HOLY_PROTECTION.get();
			}
		}

		@Override
		public CoreDamageType coreType()
		{
			return this;
		}
	}
	
	public enum MovementDamageType implements DamageType
	{
		REGULAR, STRIKE, SLASH, THRUST;
		
		@Override
		public Attribute getDefenseAttribute()
		{
			switch (this)
			{
				default: return ModAttributes.STANDARD_PROTECTION.get();
				case STRIKE: return ModAttributes.STRIKE_PROTECTION.get();
				case SLASH: return ModAttributes.SLASH_PROTECTION.get();
				case THRUST: return ModAttributes.THRUST_PROTECTION.get();
			}
		}

		@Override
		public CoreDamageType coreType()
		{
			return CoreDamageType.PHYSICAL;
		}
	}
	
	public class Damages
	{
		private Map<DamageType, Float> damages = new HashMap<>();
		
		public static Damages create()
		{
			return new Damages();
		}
		
		public boolean isEmpty()
		{
			return this.damages.isEmpty();
		}
		
		public Damages put(DamageType type, float amount)
		{
			this.damages.put(type, amount);
			return this;
		}
		
		public Damages putAll(Map<DamageType, Float> entries)
		{
			this.damages.putAll(entries);
			return this;
		}
		
		public float get(DamageType type)
		{
			return this.damages.get(type);
		}
		
		public void replace(DamageType org, DamageType replacer)
		{
			float amount = this.damages.get(org);
			this.damages.remove(org);
			this.damages.put(replacer, amount);
		}
		
		public float getFullAmount()
		{
			float amount = 0F;
			for (float f : this.damages.values())
			{
				amount += f;
			}
			return amount;
		}
		
		public void foreach(BiConsumer<DamageType, Float> consumer)
		{
			this.damages.forEach(consumer);
		}
		
		public Damages mul(float multiplier)
		{
			this.damages.forEach((type, amount) ->
			{
				this.damages.put(type, amount * multiplier);
			});
			return this;
		}
		
		
	}
}