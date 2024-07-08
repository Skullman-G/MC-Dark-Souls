package com.skullmangames.darksouls.common.animation.events;

import java.util.function.Function;

import com.google.gson.JsonObject;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.entity.projectile.MagicProjectile;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.registries.ForgeRegistries;

public class ShootMagicProjectileEvent extends AnimEvent
{
	public static final String TYPE = "shoot_magic_projectile";
	
	private final Function<LivingCap<?>, MagicProjectile> projectile;
	
	public ShootMagicProjectileEvent(float time, Function<LivingCap<?>, MagicProjectile> projectile)
	{
		super(time, Side.SERVER);
		this.projectile = projectile;
	}
	
	public ShootMagicProjectileEvent(JsonObject json)
	{
		super(json);
		this.projectile = (cap) ->
		{
			Entity e = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(json.get("projectile").getAsString())).create(cap.getLevel());
			if (e instanceof MagicProjectile p)
			{
				p.initProjectile(cap);
				return p;
			}
			return null;
		};
	}

	@Override
	protected void invoke(LivingCap<?> cap)
	{
		MagicProjectile p = this.projectile.apply(cap);
		p.shootFromRotation(cap.getOriginalEntity(), cap.getXRot(), cap.getYRot(), 0.0F, 2.0F, 0.0F);
		cap.getLevel().addFreshEntity(p);
	}

	@Override
	protected String getType()
	{
		return TYPE;
	}
}
