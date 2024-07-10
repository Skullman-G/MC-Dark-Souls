package com.skullmangames.darksouls.common.animation.types;

import java.util.List;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.AnimationType;
import com.skullmangames.darksouls.common.animation.Property;
import com.skullmangames.darksouls.common.capability.entity.EntityState;
import com.skullmangames.darksouls.common.capability.entity.EquipLoaded;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.capability.item.Shield.Deflection;
import com.skullmangames.darksouls.common.entity.BreakableObject;
import com.skullmangames.darksouls.config.ConfigManager;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.AttackResult;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.Damages;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.StunType;
import com.skullmangames.darksouls.core.util.physics.Collider;
import com.skullmangames.darksouls.core.util.physics.CubeCollider;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.entity.PartEntity;

public class DodgingAnimation extends ActionAnimation
{
	private final boolean canRotate;
	
	public DodgingAnimation(ResourceLocation id, float convertTime, ResourceLocation path,
			Function<Models<?>, Model> model, ImmutableMap<Property<?>, Object> properties)
	{
		this(id, convertTime, false, path, model, properties);
	}
	
	public DodgingAnimation(ResourceLocation id, float convertTime, boolean canRotate, ResourceLocation path,
			Function<Models<?>, Model> model, ImmutableMap<Property<?>, Object> properties)
	{
		this(id, convertTime, canRotate, 0.0F, path, model, properties);
	}
	
	public DodgingAnimation(ResourceLocation id, float convertTime, boolean canRotate, float delayTime, ResourceLocation path,
			Function<Models<?>, Model> model, ImmutableMap<Property<?>, Object> properties)
	{
		super(id, convertTime, delayTime, path, model, properties);
		this.canRotate = canRotate;
	}
	
	@Override
	public void onStart(LivingCap<?> entityCap)
	{
		super.onStart(entityCap);
		entityCap.currentlyAttackedEntities.clear();
	}
	
	public Entity getTrueEntity(Entity entity)
	{
		if (entity instanceof PartEntity<?> part)
		{
			return part.getParent();
		}

		return entity;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void renderDebugging(PoseStack poseStack, MultiBufferSource buffer, LivingCap<?> entityCap, float partialTicks)
	{
		AABB aabb = entityCap.getOriginalEntity().getBoundingBox();
		Vec3 aabbCenter = aabb.getCenter();
		Collider collider = new CubeCollider(aabb.inflate(0.1D), new Vec3(aabbCenter.x, aabbCenter.y - aabb.getYsize() / 2D, aabbCenter.z));
		collider.draw(entityCap, "Root", partialTicks);
	}
	
	@Override
	public void onUpdate(LivingCap<?> entityCap)
	{
		super.onUpdate(entityCap);
		
		if (entityCap.isClientSide()) return;
		
		LivingEntity entity = entityCap.getOriginalEntity();
		
		AABB aabb = entityCap.getOriginalEntity().getBoundingBox();
		Vec3 aabbCenter = aabb.getCenter();
		Collider collider = new CubeCollider(aabb.inflate(0.1D), new Vec3(aabbCenter.x, aabbCenter.y - aabb.getYsize() / 2D, aabbCenter.z));
		entityCap.getEntityModel(Models.SERVER).getArmature().initializeTransform();
		collider.update(entityCap, "Root", 1.0F);
		List<Entity> list = collider.getEntityCollisions(entity);
		
		if (list.size() > 0)
		{
			AttackResult attackResult = new AttackResult(entity);
			attackResult.addEntities(list, false);
			do
			{
				Entity e = attackResult.getEntity();
				Entity trueEntity = this.getTrueEntity(e);
				if (!entityCap.currentlyAttackedEntities.contains(trueEntity) && !entityCap.isTeam(trueEntity) && (trueEntity instanceof LivingEntity
					|| trueEntity instanceof BreakableObject))
				{
					if (entity.level.clip(new ClipContext(new Vec3(e.getX(), e.getY() + (double) e.getEyeHeight(), e.getZ()),
									new Vec3(entity.getX(), entity.getY() + entity.getBbHeight() * 0.5F, entity.getZ()),
									ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity))
							.getType() == HitResult.Type.MISS)
					{
						Damages damages = Damages.create();
						ExtendedDamageSource source = entityCap.getDamageSource(entityCap.getOriginalEntity().position(), 0, StunType.NONE,
								Deflection.NONE, 0, damages);
						
						entityCap.hurtEntity(e, InteractionHand.MAIN_HAND, source);
						entityCap.currentlyAttackedEntities.add(trueEntity);
					}
				}
			}
			while (attackResult.next());
		}
	}
	
	@Override
	public void onFinish(LivingCap<?> entityCap, boolean isEnd)
	{
		super.onFinish(entityCap, isEnd);
	}
	
	@Override
	protected Vector3f getCoordVector(LivingCap<?> entityCap, DynamicAnimation animation)
	{
		Vector3f vec = super.getCoordVector(entityCap, animation);
		float encumbrance = 0.0F;
		if (entityCap instanceof EquipLoaded loaded)
		{
			encumbrance = loaded.getEncumbrance();
		}
		vec.mul(1.01F - encumbrance);
		return vec;
	}
	
	@Override
	public EntityState getState(float time)
	{
		if (time < this.totalTime * ConfigManager.SERVER_CONFIG.iFramesPercentage.get())
		{
			return this.canRotate ? EntityState.R_DODGING : EntityState.DODGING;
		}
		return EntityState.PRE_CONTACT;
	}
	
	public static class Builder extends ActionAnimation.Builder
	{
		protected final boolean canRotate;
		
		public Builder(ResourceLocation id, float convertTime, ResourceLocation path, Function<Models<?>, Model> model)
		{
			this(id, convertTime, false, path, model);
		}
		
		public Builder(ResourceLocation id, float convertTime, boolean canRotate, ResourceLocation path, Function<Models<?>, Model> model)
		{
			this(id, convertTime, canRotate, 0.0F, path, model);
		}
		
		public Builder(ResourceLocation id, float convertTime, boolean canRotate, float delayTime, ResourceLocation path, Function<Models<?>, Model> model)
		{
			super(id, convertTime, delayTime, path, model);
			this.canRotate = canRotate;
		}
		
		public Builder(ResourceLocation location, JsonObject json)
		{
			super(location, json);
			this.canRotate = json.get("can_rotate").getAsBoolean();
		}
		
		@Override
		public JsonObject toJson()
		{
			JsonObject json = super.toJson();
			json.addProperty("can_rotate", this.canRotate);
			return json;
		}
		
		@Override
		public AnimationType getAnimType()
		{
			return AnimationType.DODGE;
		}
		
		@Override
		public void register(ImmutableMap.Builder<ResourceLocation, StaticAnimation> register)
		{
			register.put(this.getId(), new DodgingAnimation(this.id, this.convertTime, this.canRotate, this.delayTime,
					this.location, this.model, this.properties.build()));
		}
	}
}