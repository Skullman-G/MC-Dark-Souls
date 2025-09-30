package com.skullmangames.darksouls.common.animation.types.attack;

import java.util.function.Function;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.AnimFrameData;
import com.skullmangames.darksouls.common.animation.AnimationType;
import com.skullmangames.darksouls.common.animation.Property;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap.AttackType;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.init.data.AnimationManager;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.Damages;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.StunType;
import com.skullmangames.darksouls.core.util.math.ModMath;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCSetPos;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class BackstabCheckAnimation extends AttackAnimation
{
	private final ResourceLocation followUp;
	private final boolean isWeak;
	
	public BackstabCheckAnimation(ResourceLocation id, AttackType attackType, float convertTime,
			boolean isWeak, AnimFrameData frameData,
			Function<Models<?>, Model> model, ImmutableMap<Property<?>, Object> properties,
			ResourceLocation followUp, AttackAnimation.Phase... phases)
	{
		super(id, attackType, convertTime, frameData, model, properties, phases);
		this.followUp = followUp;
		this.isWeak = isWeak;
	}
	
	@Override
	protected boolean onDamageTarget(LivingCap<?> entityCap, Entity target)
	{
		LivingEntity attacker = entityCap.getOriginalEntity();
		LivingCap<?> targetCap = (LivingCap<?>)target.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
		if (targetCap == null || !entityCap.canBackstab(target)) return false;
		
		double yRotAttacker = Math.toRadians(ModMath.toNormalRot(attacker.getYRot()));
		double dist = 1.0D;
		Vec3 dir = new Vec3(Math.sin(yRotAttacker) * dist, 0, Math.cos(yRotAttacker) * dist);
		target.setPos(attacker.position().add(dir));
		yRotAttacker = Math.toRadians(ModMath.toNormalRot(attacker.getYRot()) - 90);
		dist = 0.5D;
		dir = new Vec3(Math.sin(yRotAttacker) * dist, 0, Math.cos(yRotAttacker) * dist);
		target.setPos(target.position().add(dir));
		target.yRot = attacker.yRot;
		target.yRotO = attacker.yRot;
		ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCSetPos(target.position(), target.getYRot(), target.getXRot(), target.getId(), true), target);
		if (target instanceof ServerPlayer)
		{
			ModNetworkManager.sendToPlayer(new STCSetPos(target.position(), target.getYRot(), target.getXRot(), target.getId()), (ServerPlayer)target);
		}
		
		StaticAnimation followUpAnim = AnimationManager.getAnimation(this.followUp);
		if (followUpAnim instanceof CriticalFollowupAnimation) entityCap.criticalTarget = target;
		return true;
	}
	
	@Override
	protected void onAttackFinish(LivingCap<?> entityCap, boolean critical)
	{
		StaticAnimation followUpAnim = AnimationManager.getAnimation(this.followUp);
		if (critical) entityCap.playAnimationSynchronized(followUpAnim, 0);
	}
	
	@Override
	protected StunType getStunType(LivingCap<?> entityCap, Entity target, Phase phase)
	{
		return entityCap.canBackstab(target) ? StunType.BACKSTABBED : super.getStunType(entityCap, target, phase);
	}
	
	@Override
	protected ExtendedDamageSource getDamageSourceExt(LivingCap<?> entityCap, boolean wasBlocked, Vec3 attackPos, Entity target, Phase phase, Damages damages)
	{
		MeleeWeaponCap weapon = entityCap.getHeldMeleeWeaponCap(phase.hand);
		damages.mul(entityCap.canBackstab(target) && !this.isWeak ? weapon.getCritical() : 0.01F);
		
		attackPos = CriticalFollowupAnimation.calcAttackPos(target);
		
		return super.getDamageSourceExt(entityCap, wasBlocked, attackPos, target, phase, damages);
	}
	
	public static class Builder extends AttackAnimation.Builder
	{
		protected final ResourceLocation followUp;
		protected final boolean isWeak;
		
		public Builder(ResourceLocation id, AttackType attackType, float convertTime, boolean isWeak,
				ResourceLocation path, Function<Models<?>, Model> model, ResourceLocation followUp, PhaseBuilder... phases)
		{
			super(id, attackType, convertTime, path, model, phases);
			this.followUp = followUp;
			this.isWeak = isWeak;
		}
		
		public Builder(ResourceLocation id, AttackType attackType, float convertTime, float begin, float contactStart, float contactEnd,
				float end, boolean isWeak,
				String index, ResourceLocation path, Function<Models<?>, Model> model, ResourceLocation followUp)
		{
			super(id, attackType, convertTime, begin, contactStart, contactEnd, end, index, path, model);
			this.followUp = followUp;
			this.isWeak = isWeak;
		}

		public Builder(ResourceLocation location, JsonObject json)
		{
			super(location, json);
			this.followUp = new ResourceLocation(json.get("followup_animation").getAsString());
			this.isWeak = json.get("is_weak").getAsBoolean();
		}
		
		@Override
		public JsonObject toJson()
		{
			JsonObject json = super.toJson();
			json.addProperty("followup_animation", this.followUp.toString());
			json.addProperty("is_weak", this.isWeak);
			return json;
		}
		
		@Override
		public AnimationType getAnimType()
		{
			return AnimationType.BACKSTAB_CHECK;
		}
		
		@Override
		public void register(ImmutableMap.Builder<ResourceLocation, StaticAnimation> register)
		{
			Phase[] builtPhases = new Phase[this.phases.length];
			for (int i = 0; i < builtPhases.length; i++)
			{
				builtPhases[i] = this.phases[i].build();
			}
			
			register.put(this.getId(), new BackstabCheckAnimation(this.id, this.attackType, this.convertTime,
					this.isWeak, this.getFrameData(), this.model, this.properties.build(), this.followUp, builtPhases));
		}
	}
}
