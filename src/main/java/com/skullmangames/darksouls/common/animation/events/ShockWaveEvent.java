package com.skullmangames.darksouls.common.animation.events;

import java.util.List;

import com.google.gson.JsonObject;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.capability.item.Shield.Deflection;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.util.DamageSourceExtended;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.CoreDamageType;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.Damages;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.StunType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

public class ShockWaveEvent extends AnimEvent
{
	public static final String TYPE = "shock_wave";
	
	private final double radius;
	
	public ShockWaveEvent(float time, double radius)
	{
		super(time, Side.SERVER);
		this.radius = radius;
	}
	
	public ShockWaveEvent(JsonObject json)
	{
		super(json);
		this.radius = json.get("radius").getAsDouble();
	}
	
	@Override
	public JsonObject toJson()
	{
		JsonObject json = super.toJson();
		json.addProperty("radius", this.radius);
		return json;
	}

	@Override
	protected void invoke(LivingCap<?> cap)
	{
		List<Entity> targets = cap.getLevel().getEntities(cap.getOriginalEntity(), new AABB(cap.getX() - this.radius, cap.getY() - this.radius, cap.getZ() - this.radius,
				cap.getX() + this.radius, cap.getY() + this.radius, cap.getZ() + this.radius));
		for (Entity target : targets)
		{
			if (target instanceof LivingEntity)
			{
				LivingCap<?> targetCap = (LivingCap<?>)target.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
				if (targetCap != null)
				{
					DamageSourceExtended dmgSource = cap.getOriginalEntity() instanceof Player ?
							ExtendedDamageSource.causePlayerDamage((Player)cap.getOriginalEntity(), cap.getOriginalEntity().position(),
									StunType.FLY, Deflection.NONE, 0, 0, Damages.create().put(CoreDamageType.PHYSICAL, 0))
							: ExtendedDamageSource.causeMobDamage(cap.getOriginalEntity(), cap.getOriginalEntity().position(),
									StunType.FLY, Deflection.NONE, 0, 0, Damages.create().put(CoreDamageType.PHYSICAL, 0));
					target.hurt(dmgSource, 0);
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
