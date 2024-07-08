package com.skullmangames.darksouls.common.animation.events;

import com.google.gson.JsonObject;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.ModParticles;

public class TeleportParticleEvent extends AnimEvent
{
	public static final String TYPE = "teleport_particle";
	
	public TeleportParticleEvent(float time)
	{
		super(time, Side.CLIENT);
	}
	
	public TeleportParticleEvent(JsonObject json)
	{
		super(json);
	}

	@Override
	protected void invoke(LivingCap<?> cap)
	{
		double r = 0.1F;
		for (int i = 0; i < 360; i++)
		{
			if (i % 40 == 0)
			{
				double ir = Math.toRadians(i);
				cap.getLevel().addParticle(ModParticles.DUST_CLOUD.get(), cap.getX(), cap.getY(), cap.getZ(), Math.sin(ir) * r, 0, Math.cos(ir) * r);
			}
		}
	}

	@Override
	protected String getType()
	{
		return TYPE;
	}
}
