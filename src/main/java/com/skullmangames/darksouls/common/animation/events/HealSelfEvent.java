package com.skullmangames.darksouls.common.animation.events;

import com.google.gson.JsonObject;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;

public class HealSelfEvent extends AnimEvent
{
	public static final String TYPE = "heal_self";
	
	private final float baseAmount;
	
	public HealSelfEvent(float time, float baseAmount)
	{
		super(time, Side.SERVER);
		this.baseAmount = baseAmount;
	}
	
	public HealSelfEvent(JsonObject json)
	{
		super(json);
		this.baseAmount = json.get("base_amount").getAsFloat();
	}
	
	@Override
	public JsonObject toJson()
	{
		JsonObject json = super.toJson();
		json.addProperty("base_amount", this.baseAmount);
		return json;
	}

	@Override
	protected void invoke(LivingCap<?> cap)
	{
		cap.getOriginalEntity().heal(this.baseAmount * cap.getSpellBuff());
	}

	@Override
	protected String getType()
	{
		return TYPE;
	}
}
