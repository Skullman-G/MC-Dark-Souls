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
	
	public ShootMagicProjectileEvent(float time, EntityType<?> projectile)
	{
		super(time, Side.SERVER);
		this.projectile = projectile;
	}
	
	public ShootMagicProjectileEvent(JsonObject json)
	{
		super(json);
		this.projectile = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(json.get("projectile").getAsString()));
	}
	
	@Override
	public JsonObject toJson()
	{
		JsonObject json = super.toJson();
		json.addProperty("projectile", this.projectile.getRegistryName().toString());
		return json;
	}

	@Override
	protected void invoke(LivingCap<?> cap)
	{
		Entity e = this.projectile.create(cap.getLevel());
		if (e instanceof MagicProjectile p)
		{
			p.initProjectile(cap);
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
