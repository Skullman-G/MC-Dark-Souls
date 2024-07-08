package com.skullmangames.darksouls.common.animation.events;

import com.google.gson.JsonObject;
import com.skullmangames.darksouls.common.block.LightSource;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;

public class SetLightSourceEvent extends AnimEvent
{
	public static final String TYPE = "set_light_source";
	
	private final int lightEmmission;
	private final float duration;
	
	public SetLightSourceEvent(float time, int lightEmmission, float duration)
	{
		super(time, Side.SERVER);
		this.lightEmmission = lightEmmission;
		this.duration = duration;
	}
	
	public SetLightSourceEvent(JsonObject json)
	{
		super(json);
		this.lightEmmission = json.get("light_emmission").getAsInt();
		this.duration = json.get("duration").getAsFloat();
	}
	
	@Override
	public JsonObject toJson()
	{
		JsonObject json = super.toJson();
		json.addProperty("light_emmission", this.lightEmmission);
		json.addProperty("duration", this.duration);
		return json;
	}
	
	@Override
	protected void invoke(LivingCap<?> cap)
	{
		LightSource.setLightSource(cap.getLevel(), cap.getOriginalEntity().blockPosition(), this.lightEmmission, this.duration);
	}

	@Override
	protected String getType()
	{
		return TYPE;
	}
}
