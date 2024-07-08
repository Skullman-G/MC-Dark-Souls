package com.skullmangames.darksouls.common.animation.events;

import java.util.List;

import com.google.gson.JsonObject;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

public class HealInRadiusEvent extends AnimEvent
{
	public static final String TYPE = "heal_in_radius";
	
	private final float baseAmount;
	private final float radius;
	
	public HealInRadiusEvent(float time, float baseAmount, float radius)
	{
		super(time, Side.SERVER);
		this.baseAmount = baseAmount;
		this.radius = radius;
	}
	
	public HealInRadiusEvent(JsonObject json)
	{
		super(json);
		this.baseAmount = json.get("base_amount").getAsFloat();
		this.radius = json.get("radius").getAsFloat();
	}
	
	@Override
	public JsonObject toJson()
	{
		JsonObject json = super.toJson();
		json.addProperty("base_amount", this.baseAmount);
		json.addProperty("radius", this.radius);
		return json;
	}

	@Override
	protected void invoke(LivingCap<?> cap)
	{
		List<Entity> targets = cap.getLevel().getEntities(null, new AABB(cap.getX() - this.radius, cap.getY() - this.radius, cap.getZ() - this.radius,
				cap.getX() + this.radius, cap.getY() + this.radius, cap.getZ() + this.radius));
		for (Entity target : targets)
		{
			if (target instanceof LivingEntity livingTarget)
			{
				livingTarget.heal(this.baseAmount * cap.getSpellBuff());
			}
		}
	}

	@Override
	protected String getType()
	{
		return TYPE;
	}
}
