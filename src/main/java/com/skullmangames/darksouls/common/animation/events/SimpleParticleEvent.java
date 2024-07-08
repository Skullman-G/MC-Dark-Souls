package com.skullmangames.darksouls.common.animation.events;

import java.util.function.Supplier;

import com.google.gson.JsonObject;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.ModParticles;
import com.skullmangames.darksouls.network.ModNetworkManager;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

public class SimpleParticleEvent extends AnimEvent
{
	public static final String TYPE = "add_simple_particle";
	
	private final Supplier<SimpleParticleType> particleType;
	private final Spawner spawner;
	private final Anchor anchor;
	private final double xOffset;
	private final double yOffset;
	private final double zOffset;
	private final double xSpeed;
	private final double ySpeed;
	private final double zSpeed;
	
	public SimpleParticleEvent(float time, Supplier<SimpleParticleType> particleType,
			double xOffset, double yOffset, double zOffset)
	{
		this(time, particleType, Spawner.SINGLE, Anchor.ENTITY, xOffset, yOffset, zOffset, 0, 0, 0);
	}
	
	public SimpleParticleEvent(float time, Supplier<SimpleParticleType> particleType, Spawner spawner, Anchor anchor,
			double xOffset, double yOffset, double zOffset,
			double xSpeed, double ySpeed, double zSpeed)
	{
		super(time, Side.CLIENT);
		this.particleType = particleType;
		this.spawner = spawner;
		this.anchor = anchor;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.zOffset = zOffset;
		this.xSpeed = xSpeed;
		this.ySpeed = ySpeed;
		this.zSpeed = zSpeed;
	}
	
	public SimpleParticleEvent(JsonObject json)
	{
		super(json);
		this.particleType = () ->
		{
			ParticleType<?> type = ForgeRegistries.PARTICLE_TYPES.getValue(new ResourceLocation(json.get("particle_type").getAsString()));
			return type instanceof SimpleParticleType ? (SimpleParticleType)type : null;
		};
		this.spawner = Spawner.valueOf(json.get("spawner").getAsString());
		this.anchor = Anchor.valueOf(json.get("anchor").getAsString());
		this.xOffset = json.get("x_offset").getAsDouble();
		this.yOffset = json.get("y_offset").getAsDouble();
		this.zOffset = json.get("z_offset").getAsDouble();
		this.xSpeed = json.get("x_speed").getAsDouble();
		this.ySpeed = json.get("y_speed").getAsDouble();
		this.zSpeed = json.get("z_speed").getAsDouble();
	}
	
	@Override
	public JsonObject toJson()
	{
		JsonObject json = super.toJson();
		json.addProperty("particle_type", this.particleType.get().getRegistryName().toString());
		json.addProperty("spawner", this.spawner.name());
		json.addProperty("anchor", this.anchor.name());
		json.addProperty("x_offset", this.xOffset);
		json.addProperty("y_offset", this.yOffset);
		json.addProperty("z_offset", this.zOffset);
		json.addProperty("x_speed", this.xSpeed);
		json.addProperty("y_speed", this.ySpeed);
		json.addProperty("z_speed", this.zSpeed);
		return json;
	}

	@Override
	protected void invoke(LivingCap<?> cap)
	{
		SimpleParticleType type = this.particleType.get();
		if (type == null) return;
		
		Vec3 pos = Vec3.ZERO;
		switch(this.anchor)
		{
			case ENTITY:
				pos = cap.getOriginalEntity().position();
				break;
			case WEAPON:
				if (cap.weaponCollider != null) pos = cap.weaponCollider.getMassCenter();
				break;
		}
		pos = pos.add(this.xOffset, this.yOffset, this.zOffset);
		
		if (this.spawner == Spawner.CIRCLE)
		{
			ModNetworkManager.connection.spawnParticlesCircle(ModParticles.DUST_CLOUD, pos, (float)Math.max(this.xSpeed, Math.max(this.ySpeed, this.zSpeed)));
		}
		else
		{
			cap.getLevel().addAlwaysVisibleParticle(type, pos.x, pos.y, pos.z, this.xSpeed, this.ySpeed, this.zSpeed);
		}
	}

	@Override
	protected String getType()
	{
		return TYPE;
	}
	
	public static enum Spawner
	{
		SINGLE, CIRCLE
	}
}
