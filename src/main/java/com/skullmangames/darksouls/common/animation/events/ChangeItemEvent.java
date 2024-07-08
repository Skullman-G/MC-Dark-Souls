package com.skullmangames.darksouls.common.animation.events;

import com.google.gson.JsonObject;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.capability.entity.MobCap;

public class ChangeItemEvent extends AnimEvent
{
	public static final String TYPE = "change_item";
	
	public ChangeItemEvent(float time)
	{
		super(time, Side.SERVER);
	}
	
	public ChangeItemEvent(JsonObject json)
	{
		super(json);
	}

	@Override
	protected void invoke(LivingCap<?> cap)
	{
		if (cap instanceof MobCap<?> mob)
		{
			mob.changeItem();
		}
	}

	@Override
	protected String getType()
	{
		return TYPE;
	}
}
