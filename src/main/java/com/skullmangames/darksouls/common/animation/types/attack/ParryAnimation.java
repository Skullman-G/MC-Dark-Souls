package com.skullmangames.darksouls.common.animation.types.attack;

import java.util.List;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
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
import com.skullmangames.darksouls.core.util.physics.Collider;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ParryAnimation extends ActionAnimation
{
	private final float start;
	private final float end;
	private final String jointName;
	
	public ParryAnimation(ResourceLocation id, float convertTime, float start, float end, String jointName, ResourceLocation path,
			Function<Models<?>, Model> model)
	{
		super(id, convertTime, path, model);
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
				MeleeWeaponCap weapon = entityCap.getHeldWeaponCapability(InteractionHand.OFF_HAND);
				Collider collider = entityCap.getColliderMatching(InteractionHand.OFF_HAND);
				collider.update(entityCap, this.jointName, 1.0F);
				
				for (Entity entity : entities)
				{
					if (entity instanceof LivingEntity livingEntity)
					{
						LivingCap<?> cap = (LivingCap<?>) livingEntity.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
						if (cap != null && cap.weaponCollider != null && collider.collidesWith(cap.weaponCollider))
						{
							if (weapon != null) entityCap.playSound(weapon.getBlockSound());
							
							if (cap.canBeParried())
							{
								cap.playSound(ModSoundEvents.PLAYER_SHIELD_DISARMED.get());
								cap.playAnimationSynchronized(Animations.BIPED_DISARMED_RIGHT, 0.0F);
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
			Collider collider = entityCap.getColliderMatching(InteractionHand.OFF_HAND);
			collider.update(entityCap, this.jointName, 1.0F);
			
			for (Entity entity : entities)
			{
				if (entity instanceof LivingEntity livingEntity)
				{
					LivingCap<?> cap = (LivingCap<?>) livingEntity.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
					if (cap != null && cap.weaponCollider != null && collider.collidesWith(cap.weaponCollider))
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
		entityCap.getColliderMatching(InteractionHand.OFF_HAND).draw(entityCap, this.jointName, partialTicks);
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
	
	@Override
	public ParryAnimation register(Builder<ResourceLocation, StaticAnimation> builder)
	{
		super.register(builder);
		return this;
	}
}
