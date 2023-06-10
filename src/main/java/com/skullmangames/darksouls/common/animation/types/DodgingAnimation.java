package com.skullmangames.darksouls.common.animation.types;

import java.util.List;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.capability.entity.EntityState;
import com.skullmangames.darksouls.common.capability.entity.EquipLoaded;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.common.entity.TerracottaVase;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.AttackResult;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.CoreDamageType;
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
	protected float encumbrance = 0.0F;
	private final boolean canRotate;
	
	public DodgingAnimation(ResourceLocation id, float convertTime, ResourceLocation path, Function<Models<?>, Model> model)
	{
		this(id, convertTime, false, path, model);
	}
	
	public DodgingAnimation(ResourceLocation id, float convertTime, boolean canRotate, ResourceLocation path, Function<Models<?>, Model> model)
	{
		this(id, convertTime, canRotate, 0.0F, path, model);
	}
	
	public DodgingAnimation(ResourceLocation id, float convertTime, boolean canRotate, float delayTime, ResourceLocation path, Function<Models<?>, Model> model)
	{
		super(id, convertTime, delayTime, path, model);
		this.canRotate = canRotate;
	}
	
	@Override
	public void onStart(LivingCap<?> entityCap)
	{
		super.onStart(entityCap);
		if (entityCap instanceof EquipLoaded loaded)
		{
			this.encumbrance = loaded.getEncumbrance();
		}
		
		entityCap.currentlyAttackedEntities.clear();
	}
	
	public Entity getTrueEntity(Entity entity)
	{
		if (entity instanceof PartEntity)
		{
			return ((PartEntity<?>) entity).getParent();
		}

		return entity;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void renderDebugging(PoseStack poseStack, MultiBufferSource buffer, LivingCap<?> entityCap, float partialTicks)
	{
		AABB aabb = entityCap.getOriginalEntity().getBoundingBox();
		Collider collider = new CubeCollider(aabb.maxX - aabb.minX, aabb.maxY - aabb.minY, aabb.maxZ - aabb.minZ, 0, 0, 0);
		collider.draw(poseStack, buffer, entityCap, "Root", partialTicks);
	}
	
	@Override
	public void onUpdate(LivingCap<?> entityCap)
	{
		super.onUpdate(entityCap);
		
		LivingEntity entity = entityCap.getOriginalEntity();
		
		AABB aabb = entityCap.getOriginalEntity().getBoundingBox();
		Collider collider = new CubeCollider(aabb.maxX - aabb.minX, aabb.maxY - aabb.minY, aabb.maxZ - aabb.minZ, 0, 0, 0);
		entityCap.getEntityModel(Models.SERVER).getArmature().initializeTransform();
		List<Entity> list = collider.updateAndFilterCollideEntity(entityCap, "Root", 1.0F);
		
		if (list.size() > 0)
		{
			AttackResult attackResult = new AttackResult(entity, list);
			boolean flag1 = true;
			do
			{
				Entity e = attackResult.getEntity();
				Entity trueEntity = this.getTrueEntity(e);
				if (!entityCap.currentlyAttackedEntities.contains(trueEntity) && !entityCap.isTeam(trueEntity) && (trueEntity instanceof LivingEntity
					|| trueEntity instanceof TerracottaVase))
				{
					if (entity.level.clip(new ClipContext(new Vec3(e.getX(), e.getY() + (double) e.getEyeHeight(), e.getZ()),
									new Vec3(entity.getX(), entity.getY() + entity.getBbHeight() * 0.5F, entity.getZ()),
									ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity))
							.getType() == HitResult.Type.MISS)
					{
						Damages damages = Damages.create().put(CoreDamageType.PHYSICAL, 0F);
						ExtendedDamageSource source = entityCap.getDamageSource(entityCap.getOriginalEntity().position(), 0, StunType.HEAVY,
								0, 0, damages);
						
						if (entityCap.hurtEntity(e, InteractionHand.MAIN_HAND, source))
						{
							e.invulnerableTime = 0;
							if (flag1 && entityCap instanceof PlayerCap && trueEntity instanceof LivingEntity)
							{
								entityCap.getOriginalEntity().getItemInHand(InteractionHand.MAIN_HAND).hurtEnemy((LivingEntity) trueEntity,
										((PlayerCap<?>) entityCap).getOriginalEntity());
								flag1 = false;
							}
						}
						entityCap.currentlyAttackedEntities.add(trueEntity);
					}
				}
			} while (attackResult.next());
		}
	}
	
	@Override
	public void onFinish(LivingCap<?> entityCap, boolean isEnd)
	{
		super.onFinish(entityCap, isEnd);
		this.encumbrance = 0.0F;
	}
	
	@Override
	protected Vector3f getCoordVector(LivingCap<?> entityCap, DynamicAnimation animation)
	{
		Vector3f vec = super.getCoordVector(entityCap, animation);
		vec.mul(1.01F - this.encumbrance);
		return vec;
	}
	
	@Override
	public EntityState getState(float time)
	{
		return this.canRotate ? EntityState.R_INVINCIBLE : EntityState.INVINCIBLE;
	}
	
	@Override
	public DodgingAnimation register(Builder<ResourceLocation, StaticAnimation> builder)
	{
		return (DodgingAnimation)super.register(builder);
	}
}