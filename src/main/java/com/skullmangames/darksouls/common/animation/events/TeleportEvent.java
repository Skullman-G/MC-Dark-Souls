package com.skullmangames.darksouls.common.animation.events;

import com.google.gson.JsonObject;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;

public class TeleportEvent extends AnimEvent
{
	public static final String TYPE = "teleport";
	
	public TeleportEvent(float time)
	{
		super(time, Side.SERVER);
	}
	
	public TeleportEvent(JsonObject json)
	{
		super(json);
	}

	@Override
	protected void invoke(LivingCap<?> cap)
	{
		cap.getOriginalEntity().teleportTo(cap.futureTeleport.x, cap.futureTeleport.y, cap.futureTeleport.z);
	}

	@Override
	protected String getType()
	{
		return TYPE;
	}
}
