package com.skullmangames.darksouls.common.animation.types.attack;

import java.util.function.Function;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.AnimFrameData;
import com.skullmangames.darksouls.common.animation.AnimationPlayer;
import com.skullmangames.darksouls.common.animation.AnimationType;
import com.skullmangames.darksouls.common.animation.Property;
import com.skullmangames.darksouls.common.animation.types.InvincibleAnimation;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap;
import com.skullmangames.darksouls.common.capability.item.Shield.Deflection;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.Damages;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.StunType;
import com.skullmangames.darksouls.core.util.math.ModMath;
import com.skullmangames.darksouls.core.util.math.vector.ModMatrix4f;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.packets.server.STCEntityBloodImpactParticles;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class CriticalFollowupAnimation extends InvincibleAnimation
{
	private final float hit;
	private final boolean dealsDamage;
	
	public CriticalFollowupAnimation(ResourceLocation id, float convertTime, float hit, boolean dealsDamage,
			AnimFrameData frameData, Function<Models<?>, Model> model, ImmutableMap<Property<?>, Object> properties)
	{
		super(id, convertTime, frameData, model, properties);
		this.hit = hit;
		this.dealsDamage = dealsDamage;
	}
	
	@Override
	public void onUpdate(LivingCap<?> entityCap, AnimationPlayer animPlayer)
	{
		super.onUpdate(entityCap, animPlayer);
		
		Entity target = entityCap.criticalTarget;
		if (target == null) return;
		
		float time = animPlayer.getElapsedTime();
		float prevTime = animPlayer.getPrevElapsedTime();
		if (time >= this.hit && prevTime < this.hit)
		{
			if (this.dealsDamage)
			{
				MeleeWeaponCap weapon = entityCap.getHeldMeleeWeaponCap(InteractionHand.MAIN_HAND);
				Damages damages = entityCap.getDamageToEntity(target, InteractionHand.MAIN_HAND);
				if (weapon != null) damages.mul(weapon.getCritical());
				ExtendedDamageSource extDmgSource = entityCap.getDamageSource(calcAttackPos(target), 0, StunType.INVINCIBILITY_BYPASS,
						Deflection.NONE, 0, damages);
				entityCap.hurtEntity(target, InteractionHand.MAIN_HAND, extDmgSource);
			}
			else
			{
				ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCEntityBloodImpactParticles(target.getId(), calcAttackPos(target)), target);
			}
			
			entityCap.criticalTarget = null;
		}
	}
	
	public static Vec3 calcAttackPos(Entity target)
	{
		float yRot = ModMath.toNormalRot(target.getYRot());
		Vec3 pos = new Vec3(0, target.getBbHeight() * 0.5F, 0);
		pos = ModMatrix4f.createRotatorDeg(yRot, Vector3f.YP).transform(pos);
		return target.position().add(pos);
	}
	
	public static class Builder extends InvincibleAnimation.Builder
	{
		protected float hit;
		protected boolean dealsDamage;
		
		public Builder(ResourceLocation id, float convertTime, float hit, boolean dealsDamage, ResourceLocation path, Function<Models<?>, Model> model)
		{
			super(id, convertTime, path, model);
			this.hit = hit;
			this.dealsDamage = dealsDamage;
		}
		
		public Builder(ResourceLocation location, JsonObject json)
		{
			super(location, json);
			this.hit = json.get("hit").getAsFloat();
			this.dealsDamage = json.get("deals_damage").getAsBoolean();
		}
		
		@Override
		public JsonObject toJson()
		{
			JsonObject json = super.toJson();
			json.addProperty("hit", this.hit);
			json.addProperty("deals_damage", this.dealsDamage);
			return json;
		}
		
		@Override
		public AnimationType getAnimType()
		{
			return AnimationType.CRITICAL_FOLLOWUP;
		}
		
		@Override
		public void register(ImmutableMap.Builder<ResourceLocation, StaticAnimation> register)
		{
			register.put(this.getId(), new CriticalFollowupAnimation(this.id, this.convertTime,
					this.hit, this.dealsDamage, this.getFrameData(), this.model, this.properties.build()));
		}
	}
}
