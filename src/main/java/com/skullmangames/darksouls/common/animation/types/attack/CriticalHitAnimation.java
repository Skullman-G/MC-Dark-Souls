package com.skullmangames.darksouls.common.animation.types.attack;

import java.util.function.Function;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.AnimationType;
import com.skullmangames.darksouls.common.animation.Property;
import com.skullmangames.darksouls.common.animation.types.InvincibleAnimation;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap;
import com.skullmangames.darksouls.common.capability.item.Shield.Deflection;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.Damages;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.StunType;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;

public class CriticalHitAnimation extends InvincibleAnimation
{
	private final float hit;
	
	public CriticalHitAnimation(ResourceLocation id, float convertTime, float hit, ResourceLocation path,
			Function<Models<?>, Model> model, ImmutableMap<Property<?>, Object> properties)
	{
		super(id, convertTime, path, model, properties);
		this.hit = hit;
	}
	
	@Override
	public void onUpdate(LivingCap<?> entityCap)
	{
		super.onUpdate(entityCap);
		Entity target = entityCap.criticalTarget;
		if (target == null) return;
		float time = entityCap.getAnimator().getMainPlayer().getElapsedTime();
		float prevTime = entityCap.getAnimator().getMainPlayer().getPrevElapsedTime();
		if (time >= this.hit && prevTime < this.hit)
		{
			MeleeWeaponCap weapon = entityCap.getHeldMeleeWeaponCap(InteractionHand.MAIN_HAND);
			Damages damages = entityCap.getDamageToEntity(target, InteractionHand.MAIN_HAND);
			if (weapon != null) damages.mul(weapon.getCritical());
			ExtendedDamageSource extDmgSource = entityCap.getDamageSource(entityCap.getOriginalEntity().position(), 0, StunType.INVINCIBILITY_BYPASS,
					Deflection.NONE, 0, damages);
			entityCap.hurtEntity(target, InteractionHand.MAIN_HAND, extDmgSource);
			entityCap.criticalTarget = null;
		}
	}
	
	public static class Builder extends InvincibleAnimation.Builder
	{
		protected float hit;
		
		public Builder(ResourceLocation id, float convertTime, float hit, ResourceLocation path, Function<Models<?>, Model> model)
		{
			super(id, convertTime, path, model);
			this.hit = hit;
		}
		
		public Builder(ResourceLocation location, JsonObject json)
		{
			super(location, json);
			this.hit = json.get("hit").getAsFloat();
		}
		
		@Override
		public JsonObject toJson()
		{
			JsonObject json = super.toJson();
			json.addProperty("hit", this.hit);
			return json;
		}
		
		@Override
		public AnimationType getAnimType()
		{
			return AnimationType.CRITICAL_HIT;
		}
		
		@Override
		public CriticalHitAnimation build()
		{
			return new CriticalHitAnimation(this.id, this.convertTime, this.hit, this.location, this.model, this.properties.build());
		}
	}
}
