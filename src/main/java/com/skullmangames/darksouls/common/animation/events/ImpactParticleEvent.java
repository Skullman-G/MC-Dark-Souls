package com.skullmangames.darksouls.common.animation.events;

import com.google.gson.JsonObject;
import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.util.math.MathUtils;
import com.skullmangames.darksouls.core.util.math.vector.ModMatrix4f;

import net.minecraft.world.phys.Vec3;

public class ImpactParticleEvent extends AnimEvent
{
	public static final String TYPE = "impact_particle";
	
	private final double xOffset;
	private final double yOffset;
	private final double zOffset;
	
	public ImpactParticleEvent(float time, double xOffset, double yOffset, double zOffset)
	{
		super(time, Side.CLIENT);
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.zOffset = zOffset;
	}
	
	public ImpactParticleEvent(JsonObject json)
	{
		super(json);
		this.xOffset = json.get("x_offset").getAsDouble();
		this.yOffset = json.get("y_offset").getAsDouble();
		this.zOffset = json.get("z_offset").getAsDouble();
	}
	
	@Override
	public JsonObject toJson()
	{
		JsonObject json = super.toJson();
		json.addProperty("x_offset", this.xOffset);
		json.addProperty("y_offset", this.yOffset);
		json.addProperty("z_offset", this.zOffset);
		return json;
	}

	@Override
	protected void invoke(LivingCap<?> cap)
	{
		float yRot = MathUtils.toNormalRot(cap.getYRot());
		float y = cap.getOriginalEntity().getBbHeight() * 0.5F;
		Vec3 pos = new Vec3(this.xOffset, this.yOffset + y, this.zOffset);
		pos = ModMatrix4f.createRotatorDeg(yRot, Vector3f.YP).transform(pos);
		pos = cap.getOriginalEntity().position().add(pos);
		cap.makeImpactParticles(pos, false);
	}

	@Override
	protected String getType()
	{
		return TYPE;
	}
}
