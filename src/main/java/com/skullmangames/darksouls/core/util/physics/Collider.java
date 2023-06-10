package com.skullmangames.darksouls.core.util.physics;

import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.skullmangames.darksouls.client.renderer.entity.model.Armature;
import com.skullmangames.darksouls.common.animation.Animator;
import com.skullmangames.darksouls.common.capability.entity.EntityState;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class Collider
{
	protected final Vec3 modelCenter;
	protected final AABB outerAABB;
	protected Vec3 worldCenter;

	public Collider(Vec3 center, @Nullable AABB outerAABB)
	{
		this.modelCenter = center;
		this.outerAABB = outerAABB;
		this.worldCenter = new Vec3(0, 0, 0);
	}
	
	@Nullable
	public ResourceLocation getId()
	{
		return Colliders.getId(this);
	}
	
	public Vec3 getWorldCenter()
	{
		return new Vec3(-this.worldCenter.x, this.worldCenter.y, -this.worldCenter.z);
	}

	protected void transform(PublicMatrix4f mat)
	{
		this.worldCenter = PublicMatrix4f.transform(mat, this.modelCenter);
	}
	
	public void update(LivingCap<?> entityCap, String jointName, float partialTicks)
	{
		PublicMatrix4f transformMatrix;
		Armature armature = entityCap.getEntityModel(Models.SERVER).getArmature();
		int pathIndex = armature.searchPathIndex(jointName);

		if (pathIndex == -1) transformMatrix = new PublicMatrix4f();
		else transformMatrix = Animator.getParentboundTransform(entityCap.getAnimator().getPose(partialTicks), armature, pathIndex);
		transformMatrix.mulFront(entityCap.getModelMatrix(partialTicks));
		this.transform(transformMatrix);
	}

	public List<Entity> updateAndFilterCollideEntity(LivingCap<?> entityCap, String jointName, float partialTicks)
	{
		this.update(entityCap, jointName, partialTicks);
		return this.getCollideEntities(entityCap.getOriginalEntity());
	}
	
	public abstract Collider getScaledCollider(float scale);

	public List<Entity> getCollideEntities(Entity self)
	{
		List<Entity> list = self.level.getEntities(self, this.getHitboxAABB());
		this.filterHitEntities(list);
		return list;
	}

	@OnlyIn(Dist.CLIENT)
	public abstract void drawInternal(PoseStack matrixStackIn, MultiBufferSource buffer, PublicMatrix4f pose, boolean red);

	@OnlyIn(Dist.CLIENT)
	public void draw(PoseStack poseStack, MultiBufferSource buffer, LivingCap<?> entityCap, String boneName, float partialTicks)
	{
		Armature armature = entityCap.getEntityModel(Models.SERVER).getArmature();
		int pathIndex = armature.searchPathIndex(boneName);
		boolean red = entityCap.getEntityState() == EntityState.CONTACT;
		PublicMatrix4f mat = null;

		if (pathIndex == -1)
		{
			mat = new PublicMatrix4f();
		} else
		{
			mat = Animator.getParentboundTransform(entityCap.getAnimator().getPose(partialTicks), armature, pathIndex);
		}

		this.drawInternal(poseStack, buffer, mat, red);
	}

	protected abstract boolean collide(Entity opponent);

	protected void filterHitEntities(List<Entity> entities)
	{
		entities.removeIf((entity) -> !this.collide(entity));
	}

	public AABB getHitboxAABB()
	{
		return this.outerAABB.move(-this.worldCenter.x, this.worldCenter.y, -this.worldCenter.z);
	}
}