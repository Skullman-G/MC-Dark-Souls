package com.skullmangames.darksouls.common.animation.events;

import com.google.gson.JsonObject;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.entity.projectile.MagicProjectile;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

public class ShootMagicProjectileEvent extends AnimEvent
{
	public static final String TYPE = "shoot_magic_projectile";
	private final EntityType<?> projectile;
	private final float baseDamage;
	private final float staminaDamage;
	private final float poiseDamage;
	
	public ShootMagicProjectileEvent(float time, EntityType<?> projectile, float baseDamage, float staminaDamage, float poiseDamage)
	{
		super(time, Side.SERVER);
		this.projectile = projectile;
		this.baseDamage = baseDamage;
		this.staminaDamage = staminaDamage;
		this.poiseDamage = poiseDamage;
	}
	
	public ShootMagicProjectileEvent(JsonObject json)
	{
		super(json);
		this.projectile = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(json.get("projectile").getAsString()));
		this.baseDamage = json.get("base_damage").getAsFloat();
		this.staminaDamage = json.get("stamina_damage").getAsFloat();
		this.poiseDamage = json.get("poise_damage").getAsFloat();
	}
	
	@Override
	public JsonObject toJson()
	{
		JsonObject json = super.toJson();
		json.addProperty("projectile", this.projectile.getRegistryName().toString());
		json.addProperty("base_damage", this.baseDamage);
		json.addProperty("stamina_damage", this.staminaDamage);
		json.addProperty("poise_damage", this.poiseDamage);
		return json;
	}

	@Override
	protected void invoke(LivingCap<?> cap)
	{
		Entity e = this.projectile.create(cap.getLevel());
		if (e instanceof MagicProjectile p)
		{
			p.initProjectile(cap, this.baseDamage, this.staminaDamage, this.poiseDamage);
			p.shootFromRotation(cap.getOriginalEntity(), cap.getXRot(), cap.getYRot(), 0.0F, 2.0F, 0.0F);
			cap.getLevel().addFreshEntity(p);
		}
	}

	@Override
	protected String getType()
	{
		return TYPE;
	}
}
