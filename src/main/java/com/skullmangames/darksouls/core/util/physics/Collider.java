package com.skullmangames.darksouls.core.util.physics;

import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.skullmangames.darksouls.client.renderer.entity.model.Armature;
import com.skullmangames.darksouls.common.animation.Animator;
import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.common.capability.entity.EntityState;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class Collider
{
	protected final Vector3d modelCenter;
	protected final AxisAlignedBB outerAABB;
	protected Vector3d worldCenter;

	public Collider(Vector3d center, @Nullable AxisAlignedBB outerAABB)
	{
		this.modelCenter = center;
		this.outerAABB = outerAABB;
		this.worldCenter = new Vector3d(0, 0, 0);
	}
	
	public Vector3d getWorldCenter()
	{
		return new Vector3d(-this.worldCenter.x, this.worldCenter.y, -this.worldCenter.z);
	}

	protected void transform(PublicMatrix4f mat)
	{
		this.worldCenter = PublicMatrix4f.transform(mat, this.modelCenter);
	}
	
	public void update(LivingCap<?> entityCap, String jointName)
	{
		PublicMatrix4f transformMatrix;
		Armature armature = entityCap.getEntityModel(Models.SERVER).getArmature();
		int pathIndex = armature.searchPathIndex(jointName);

		if (pathIndex == -1) transformMatrix = new PublicMatrix4f();
		else transformMatrix = Animator.getParentboundTransform(entityCap.getAnimator().getPose(1.0F), armature, pathIndex);
		transformMatrix.mulFront(entityCap.getModelMatrix(1.0F));
		this.transform(transformMatrix);
	}

	public List<Entity> updateAndFilterCollideEntity(LivingCap<?> entityCap, AttackAnimation attackAnimation,
			float prevElapsedTime, float elapsedTime, String jointName, float attackSpeed)
	{
		this.update(entityCap, jointName);
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
	public abstract void drawInternal(MatrixStack matrixStackIn, IRenderTypeBuffer buffer, PublicMatrix4f pose, boolean red);

	@OnlyIn(Dist.CLIENT)
	public void draw(MatrixStack matrixStackIn, IRenderTypeBuffer buffer, LivingCap<?> entityCap,
			AttackAnimation animation, float prevElapsedTime, float elapsedTime, float partialTicks, float attackSpeed)
	{
		Armature armature = entityCap.getEntityModel(Models.SERVER).getArmature();
		int pathIndex = armature.searchPathIndex(animation.getPathIndexByTime(elapsedTime));
		boolean red = entityCap.getEntityState() == EntityState.CONTACT;
		PublicMatrix4f mat = null;

		if (pathIndex == -1)
		{
			mat = new PublicMatrix4f();
		} else
		{
			mat = Animator.getParentboundTransform(entityCap.getAnimator().getPose(partialTicks), armature, pathIndex);
		}

		this.drawInternal(matrixStackIn, buffer, mat, red);
	}

	protected abstract boolean collide(Entity opponent);

	protected void filterHitEntities(List<Entity> entities)
	{
		entities.removeIf((entity) -> !this.collide(entity));
	}

	public AxisAlignedBB getHitboxAABB()
	{
		return this.outerAABB.move(-this.worldCenter.x, this.worldCenter.y, -this.worldCenter.z);
	}
}