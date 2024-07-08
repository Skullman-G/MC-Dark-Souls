package com.skullmangames.darksouls.common.animation.events;

import com.google.gson.JsonObject;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.network.ModNetworkManager;

public class ShakeCamEvent extends AnimEvent
{
	public static final String TYPE = "shake_cam";
	
	private final int duration;
	private final float magnitude;
	
	public ShakeCamEvent(float time, int duration, float magnitude)
	{
		super(time, Side.CLIENT);
		this.duration = duration;
		this.magnitude = magnitude;
	}
	
	public ShakeCamEvent(JsonObject json)
	{
		super(json);
		this.duration = json.get("duration").getAsInt();
		this.magnitude = json.get("magnitude").getAsFloat();
	}
	
	@Override
	public JsonObject toJson()
	{
		JsonObject json = super.toJson();
		json.addProperty("duration", this.duration);
		json.addProperty("magnitude", this.magnitude);
		return json;
	}

	@Override
	protected void invoke(LivingCap<?> cap)
	{
		ModNetworkManager.connection.shakeCamForEntity(cap.getOriginalEntity(), this.duration, this.magnitude);
	}

	@Override
	protected String getType()
	{
		return TYPE;
	}
}
