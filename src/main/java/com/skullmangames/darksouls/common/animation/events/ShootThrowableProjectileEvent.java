package com.skullmangames.darksouls.common.animation.events;

import com.google.gson.JsonObject;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.capability.item.ThrowableCap;

import net.minecraft.world.InteractionHand;

public class ShootThrowableProjectileEvent extends AnimEvent
{
	public static final String TYPE = "shoot_throwable_projectile";
	
	public ShootThrowableProjectileEvent(float time)
	{
		super(time, Side.SERVER);
	}
	
	public ShootThrowableProjectileEvent(JsonObject json)
	{
		super(json);
	}

	@Override
	protected void invoke(LivingCap<?> cap)
	{
		if (cap.getHeldItemCapability(InteractionHand.MAIN_HAND) instanceof ThrowableCap throwable)
		{
			throwable.spawnProjectile(cap);
		}
	}

	@Override
	protected String getType()
	{
		return TYPE;
	}
}
