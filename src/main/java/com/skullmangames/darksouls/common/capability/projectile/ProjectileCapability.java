package com.skullmangames.darksouls.common.capability.projectile;

import java.util.LinkedHashMap;
import java.util.Map;
import com.google.gson.JsonObject;
import com.skullmangames.darksouls.common.capability.entity.EntityCapability;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.CoreDamageType;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.Damages;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.StunType;
import com.skullmangames.darksouls.core.util.json.JsonBuilder;
import com.skullmangames.darksouls.core.util.json.JsonKey;
import com.skullmangames.darksouls.core.util.json.JsonMapper;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;

public class ProjectileCapability<T extends Projectile>
{
	protected T orgProjectile;
	
	private final Map<CoreDamageType, Float> damages;
	private final float staminaDamage;
	private final float poiseDamage;
	private final StunType stunType;
	private final boolean useSpellBuff;
	
	public ProjectileCapability(Map<CoreDamageType, Float> damages, float staminaDamage, float poiseDamage, StunType stunType, boolean useSpellBuff)
	{
		this.damages = damages;
		this.staminaDamage = staminaDamage;
		this.poiseDamage = poiseDamage;
		this.stunType = stunType;
		this.useSpellBuff = useSpellBuff;
	}
	
	public void onProjectileConstructed(T projectile)
	{
		this.orgProjectile = projectile;
	}
	
	public void onHurt(Entity target)
	{
		Damages dmg = Damages.create();
		dmg.putAll(this.damages);
		
		if (this.useSpellBuff)
		{
			Entity owner = this.orgProjectile.getOwner();
			if (owner != null)
			{
				EntityCapability<?> cap = owner.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
				if (cap instanceof LivingCap<?> livingCap)
				{
					dmg.mul(livingCap.getSpellBuff());
				}
			}
		}
		
		ExtendedDamageSource.causeProjectileDamage(
			this.orgProjectile,
			this.orgProjectile.getOwner(),
			this.stunType,
			this.poiseDamage,
			this.staminaDamage,
			dmg
		)
		.hurtEntity(target);
	}
	
	public static Builder builder(EntityType<?> entityType, float staminaDamage, float poiseDamage, StunType stunType, boolean useSpellBuff)
	{
		return new Builder(entityType, staminaDamage, poiseDamage, stunType, useSpellBuff);
	}
	
	public static class Builder implements JsonBuilder<ProjectileCapability<?>>
	{
		@JsonKey("registry_name")
		private EntityType<?> entityType;
		
		@JsonKey("damage")
		private Map<CoreDamageType, Float> damages = new LinkedHashMap<>();
		
		@JsonKey("stamina_damage")
		private float staminaDamage;
		
		@JsonKey("poise_damage")
		private float poiseDamage;
		
		@JsonKey("stun_type")
		private StunType stunType;
		
		@JsonKey("use_spell_buff")
		private boolean useSpellBuff;
		
		public Builder() {}
		
		private Builder(EntityType<?> entityType, float staminaDamage, float poiseDamage, StunType stunType, boolean useSpellBuff)
		{
			this.entityType = entityType;
			this.staminaDamage = staminaDamage;
			this.poiseDamage = poiseDamage;
			this.stunType = stunType;
			this.useSpellBuff = useSpellBuff;
		}
		
		public Builder putDamage(CoreDamageType type, float value)
		{
			this.damages.put(type, value);
			return this;
		}
		
		@Override
		public JsonObject toJson()
		{
			return JsonMapper.toJson(this);
		}
		
		public static Builder fromJson(ResourceLocation location, JsonObject json)
		{
			return JsonMapper.fromJson(json, Builder.class);
		}
		
		public EntityType<?> getEntityType()
		{
			return this.entityType;
		}
		
		@Override
		public ProjectileCapability<?> build()
		{
			return new ProjectileCapability<>(this.damages, this.staminaDamage, this.poiseDamage, this.stunType, this.useSpellBuff);
		}

		@Override
		public ResourceLocation getId()
		{
			return this.entityType.getRegistryName();
		}
	}
}