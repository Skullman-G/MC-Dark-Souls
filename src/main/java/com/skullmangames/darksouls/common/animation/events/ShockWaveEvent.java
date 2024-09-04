package com.skullmangames.darksouls.common.animation.events;

import java.util.List;

import com.google.gson.JsonObject;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.capability.item.Shield.Deflection;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.CoreDamageType;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.Damages;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.StunType;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ShockWaveEvent extends AnimEvent
{
	public static final String TYPE = "shock_wave";
	
	private final double radius;
	private final Anchor anchor;
	
	public ShockWaveEvent(float time, double radius, Anchor anchor)
	{
		super(time, Side.SERVER);
		this.radius = radius;
		this.anchor = anchor;
	}
	
	public ShockWaveEvent(JsonObject json)
	{
		super(json);
		this.radius = json.get("radius").getAsDouble();
		this.anchor = Anchor.valueOf(json.get("anchor").getAsString());
	}
	
	@Override
	public JsonObject toJson()
	{
		JsonObject json = super.toJson();
		json.addProperty("radius", this.radius);
		json.addProperty("anchor", this.anchor.name());
		return json;
	}

	@Override
	protected void invoke(LivingCap<?> cap)
	{
		Vec3 pos = this.anchor == Anchor.WEAPON && !cap.weaponCollider.isEmpty() ? cap.weaponCollider.getMassCenter() : cap.getOriginalEntity().position();
		List<Entity> targets = cap.getLevel().getEntities(cap.getOriginalEntity(), new AABB(pos.x - this.radius, pos.y - this.radius, pos.z - this.radius,
				pos.x + this.radius, pos.y + this.radius, pos.z + this.radius));
		for (Entity target : targets)
		{
			if (target instanceof LivingEntity)
			{
				LivingCap<?> targetCap = (LivingCap<?>)target.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
				if (targetCap != null)
				{
					ExtendedDamageSource dmgSource = cap.getDamageSource(cap.getOriginalEntity().position(), 0,
							StunType.FLY, Deflection.NONE, 0, Damages.create().put(CoreDamageType.PHYSICAL, 0));
					target.hurt((DamageSource)dmgSource, dmgSource.getAmount());
				}
				else cap.knockBackEntity(target, 0.5F);
			}
			else cap.knockBackEntity(target, 1.0F);
		}
	}

	@Override
	protected String getType()
	{
		return TYPE;
	}
}
