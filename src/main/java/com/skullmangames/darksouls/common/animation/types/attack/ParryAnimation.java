package com.skullmangames.darksouls.common.animation.types.attack;

import java.util.List;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.AnimationType;
import com.skullmangames.darksouls.common.animation.Property;
import com.skullmangames.darksouls.common.animation.types.ActionAnimation;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.EntityState;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.init.ModSoundEvents;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.init.data.Colliders;
import com.skullmangames.darksouls.core.util.collider.Collider;
import com.skullmangames.darksouls.core.util.collider.ColliderHolder;
import com.skullmangames.darksouls.core.util.math.vector.ModMatrix4f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ParryAnimation extends ActionAnimation
{
	public final float start;
	public final float end;
	private final String jointName;
	
	public ParryAnimation(ResourceLocation id, float convertTime, float start, float end, String jointName, ResourceLocation path,
			Function<Models<?>, Model> model, ImmutableMap<Property<?>, Object> properties)
	{
		super(id, convertTime, path, model, properties);
		this.start = start;
		this.end = end;
		this.jointName = jointName;
	}
	
	@Override
	public void onUpdate(LivingCap<?> entityCap)
	{
		super.onUpdate(entityCap);
		
		if (!entityCap.isClientSide())
		{
			if (entityCap.getEntityState() == EntityState.CONTACT)
			{
				LivingEntity orgEntity = entityCap.getOriginalEntity();
				List<Entity> entities = entityCap.getLevel().getEntities(orgEntity, orgEntity.getBoundingBox().inflate(2.0D));
				MeleeWeaponCap weapon = entityCap.getHeldMeleeWeaponCap(InteractionHand.OFF_HAND);
				
				ModMatrix4f modelMat = entityCap.getModelMatrix(1.0F).rotateDeg(90, Vector3f.YP);
				ModMatrix4f mat = modelMat.translate(0.8F, entityCap.getOriginalEntity().getBbHeight() / 2, 0).scale(1.75F, 1.75F, 1.75F);
				Collider collider = Colliders.SHIELD.get();
				collider.transform(mat);
				
				for (Entity entity : entities)
				{
					if (entity instanceof LivingEntity livingEntity)
					{
						LivingCap<?> targetCap = (LivingCap<?>) livingEntity.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
						if (targetCap != null
								&& targetCap.getEntityState().getContactLevel() == 2 && !targetCap.weaponCollider.isEmpty()
								&& collider.collidesWith(targetCap.weaponCollider.getType()))
						{
							if (weapon != null) entityCap.playSound(weapon.getBlockSound());
							
							if (targetCap.canBeParried())
							{
								targetCap.playSound(ModSoundEvents.GENERIC_PARRY_SUCCESS.get());
								targetCap.playAnimationSynchronized(Animations.BIPED_DISARMED_RIGHT.get(), 0.0F);
								entityCap.onParrySuccess();
							}
						}
					}
				}
			}
		}
		else this.onClientUpdate(entityCap);
	}
	
	@OnlyIn(Dist.CLIENT)
	private void onClientUpdate(LivingCap<?> entityCap)
	{
		if (entityCap.getEntityState() == EntityState.CONTACT)
		{
			LivingEntity orgEntity = entityCap.getOriginalEntity();
			List<Entity> entities = entityCap.getLevel().getEntities(orgEntity, orgEntity.getBoundingBox().inflate(2.0D));
			ColliderHolder collider = this.getCollider(entityCap);
			collider.update(entityCap, this.jointName, 1.0F);
			
			for (Entity entity : entities)
			{
				if (entity instanceof LivingEntity livingEntity)
				{
					LivingCap<?> cap = (LivingCap<?>) livingEntity.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
					if (cap != null && !cap.weaponCollider.isEmpty() && collider.getType().collidesWith(cap.weaponCollider.getType()))
					{
						entityCap.makeImpactParticles(collider.getMassCenter(), true);
					}
				}
			}
		}
	}
	
	@Override
	public <V> ParryAnimation addProperty(Property<V> propertyType, V value)
	{
		super.addProperty(propertyType, value);
		return this;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void renderDebugging(PoseStack poseStack, MultiBufferSource buffer, LivingCap<?> entityCap, float partialTicks)
	{
		this.getCollider(entityCap).draw(entityCap, this.jointName, partialTicks);
	}
	
	public ColliderHolder getCollider(LivingCap<?> entityCap)
	{
		return new ColliderHolder(entityCap.getColliderMatching(InteractionHand.OFF_HAND));
	}
	
	@Override
	public EntityState getState(float time)
	{
		if (time < this.start)
		{
			return EntityState.PRE_CONTACT;
		}
		else if (time < this.end)
		{
			return EntityState.CONTACT;
		}
		else return EntityState.POST_CONTACT;
	}
	
	public static class Builder extends ActionAnimation.Builder
	{
		protected final float start;
		protected final float end;
		protected final String jointName;
		
		public Builder(ResourceLocation id, float convertTime, float start, float end, String jointName, ResourceLocation path,
				Function<Models<?>, Model> model)
		{
			super(id, convertTime, path, model);
			this.start = start;
			this.end = end;
			this.jointName = jointName;
		}
		
		public Builder(ResourceLocation location, JsonObject json)
		{
			super(location, json);
			this.start = json.get("start").getAsFloat();
			this.end = json.get("end").getAsFloat();
			this.jointName = json.get("joint_name").getAsString();
		}
		
		@Override
		public JsonObject toJson()
		{
			JsonObject json = super.toJson();
			json.addProperty("start", this.start);
			json.addProperty("end", this.end);
			json.addProperty("joint_name", this.jointName);
			return json;
		}
		
		@Override
		public AnimationType getAnimType()
		{
			return AnimationType.PARRY;
		}
		
		@Override
		public void register(ImmutableMap.Builder<ResourceLocation, StaticAnimation> register)
		{
			register.put(this.getId(), new ParryAnimation(this.id, this.convertTime,
					this.start, this.end, this.jointName, this.location, this.model, this.properties.build()));
		}
	}
}
