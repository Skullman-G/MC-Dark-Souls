package com.skullmangames.darksouls.common.animation.events;

import com.google.gson.JsonObject;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.network.ModNetworkManager;

import net.minecraft.world.phys.Vec3;

public class ShakeCamGlobalEvent extends AnimEvent
{
	public static final String TYPE = "shake_cam_global";
	
	private final int duration;
	private final float magnitude;
	private final Anchor anchor;
	
	public ShakeCamGlobalEvent(float time, int duration, float magnitude)
	{
		this(time, duration, magnitude, Anchor.ENTITY);
	}
	
	public ShakeCamGlobalEvent(float time, int duration, float magnitude, Anchor anchor)
	{
		super(time, Side.CLIENT);
		this.duration = duration;
		this.magnitude = magnitude;
		this.anchor = anchor;
	}
	
	public ShakeCamGlobalEvent(JsonObject json)
	{
		super(json);
		this.duration = json.get("duration").getAsInt();
		this.magnitude = json.get("magnitude").getAsFloat();
		this.anchor = Anchor.valueOf(json.get("anchor").getAsString());
	}
	
	@Override
	public JsonObject toJson()
	{
		JsonObject json = super.toJson();
		json.addProperty("duration", this.duration);
		json.addProperty("magnitude", this.magnitude);
		json.addProperty("anchor", this.anchor.name());
		return json;
	}

	@Override
	protected void invoke(LivingCap<?> cap)
	{
		Vec3 pos = Vec3.ZERO;
		switch(this.anchor)
		{
			case ENTITY:
				pos = cap.getOriginalEntity().position();
				break;
			case WEAPON:
				if (!cap.weaponCollider.isEmpty()) pos = cap.weaponCollider.getMassCenter();
				break;
		}
		
		ModNetworkManager.connection.shakeCam(pos, this.duration, this.magnitude);
	}

	@Override
	protected String getType()
	{
		return TYPE;
	}
}
