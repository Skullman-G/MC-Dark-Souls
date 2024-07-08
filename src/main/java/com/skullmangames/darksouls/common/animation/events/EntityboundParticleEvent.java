package com.skullmangames.darksouls.common.animation.events;

import java.util.function.Supplier;

import com.google.gson.JsonObject;
import com.skullmangames.darksouls.client.particles.EntityboundParticleOptions;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityboundParticleEvent extends AnimEvent
{
	public static final String TYPE = "add_entitybound_particle";
	
	private final Supplier<ParticleType<EntityboundParticleOptions>> particleType;
	private final double xOffset;
	private final double yOffset;
	private final double zOffset;
	
	public EntityboundParticleEvent(float time, Supplier<ParticleType<EntityboundParticleOptions>> particleType,
			double xOffset, double yOffset, double zOffset)
	{
		super(time, Side.CLIENT);
		this.particleType = particleType;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.zOffset = zOffset;
	}
	
	@SuppressWarnings("unchecked")
	public EntityboundParticleEvent(JsonObject json)
	{
		super(json);
		this.particleType = () ->
		{
			ParticleType<?> type = ForgeRegistries.PARTICLE_TYPES.getValue(new ResourceLocation(json.get("particle_type").getAsString()));
			return type.getDeserializer() == EntityboundParticleOptions.DESERIALIZER ?
					(ParticleType<EntityboundParticleOptions>)type : null;
		};
		this.xOffset = json.get("x_offset").getAsDouble();
		this.yOffset = json.get("y_offset").getAsDouble();
		this.zOffset = json.get("z_offset").getAsDouble();
	}
	
	@Override
	public JsonObject toJson()
	{
		JsonObject json = super.toJson();
		json.addProperty("particle_type", this.particleType.get().getRegistryName().toString());
		json.addProperty("x_offset", this.xOffset);
		json.addProperty("y_offset", this.yOffset);
		json.addProperty("z_offset", this.zOffset);
		return json;
	}

	@Override
	protected void invoke(LivingCap<?> cap)
	{
		ParticleType<EntityboundParticleOptions> type = this.particleType.get();
		if (type == null) return;
		
		cap.getLevel().addAlwaysVisibleParticle(new EntityboundParticleOptions(type, cap.getOriginalEntity().getId()),
				cap.getX() + this.xOffset, cap.getY() + this.yOffset, cap.getZ() + this.zOffset, 0, 0, 0);
	}

	@Override
	protected String getType()
	{
		return TYPE;
	}
}
