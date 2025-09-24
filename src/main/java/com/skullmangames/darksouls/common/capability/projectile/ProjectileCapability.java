package com.skullmangames.darksouls.common.capability.projectile;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.skullmangames.darksouls.common.capability.entity.EntityCapability;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.CoreDamageType;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.DamageType;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.Damages;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.StunType;
import com.skullmangames.darksouls.core.util.JsonBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraftforge.registries.ForgeRegistries;

public class ProjectileCapability<T extends Projectile>
{
	protected T orgProjectile;
	
	private final Map<DamageType, Float> damages;
	private final float staminaDamage;
	private final float poiseDamage;
	private final StunType stunType;
	private final boolean useSpellBuff;
	
	public ProjectileCapability(Map<DamageType, Float> damages, float staminaDamage, float poiseDamage, StunType stunType, boolean useSpellBuff)
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
		private EntityType<?> entityType;
		
		private Map<DamageType, Float> damages = new LinkedHashMap<>();
		private float staminaDamage;
		private float poiseDamage;
		private StunType stunType;
		private boolean useSpellBuff;
		
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
		
		private Builder(ResourceLocation location, JsonObject json)
		{
			ResourceLocation registryName = ResourceLocation.tryParse(json.get("registry_name").getAsString());
			this.entityType = ForgeRegistries.ENTITIES.getValue(registryName);
			
			JsonObject damage = json.get("damage").getAsJsonObject();
			for (CoreDamageType type : CoreDamageType.values())
			{
				float dmg = Optional.ofNullable(damage.get(type.toString())).orElse(new JsonPrimitive(0)).getAsFloat();
				this.damages.put(type, dmg);
			}
			
			this.staminaDamage = json.get("stamina_damage").getAsFloat();
			this.poiseDamage = json.get("poise_damage").getAsFloat();
			this.stunType = StunType.valueOf(json.get("stun_type").getAsString());
			this.useSpellBuff = json.get("use_spell_buff").getAsBoolean();
		}
		
		@Override
		public JsonObject toJson()
		{
			JsonObject json = new JsonObject();
			
			json.addProperty("registry_name", this.entityType.getRegistryName().toString());
			
			JsonObject damage = new JsonObject();
			json.add("damage", damage);
			this.damages.forEach((type, dmg) ->
			{
				damage.addProperty(type.toString(), dmg);
			});
			
			json.addProperty("stamina_damage", this.staminaDamage);
			json.addProperty("poise_damage", this.poiseDamage);
			json.addProperty("stun_type", this.stunType.toString());
			json.addProperty("use_spell_buff", this.useSpellBuff);
			
			return json;
		}
		
		public static Builder fromJson(ResourceLocation location, JsonObject json)
		{
			return new Builder(location, json);
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