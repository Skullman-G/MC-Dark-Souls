package com.skullmangames.darksouls.core.util.physics;

import java.util.List;

import javax.annotation.Nullable;

import com.skullmangames.darksouls.client.renderer.entity.model.Armature;
import com.skullmangames.darksouls.common.animation.Animator;
import com.skullmangames.darksouls.common.capability.entity.EntityState;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.math.vector.ModMatrix4f;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class Collider
{
	private Vec3 worldCenter;
	protected Vec3[] vertices;
	protected Vec3[] normals;
	
	/**
	 * Test hitbox.
	 * Bigger than collider hitbox.
	 * Uses mass center instead of world center.
	 **/
	protected final AABB outerAABB;

	public Collider(AABB outerAABB)
	{
		this.outerAABB = outerAABB;
		this.worldCenter = Vec3.ZERO;
	}
	
	@Nullable
	public ResourceLocation getId()
	{
		return Colliders.getId(this);
	}
	
	public Vec3 getWorldCenter()
	{
		return this.worldCenter;
	}

	protected void transform(ModMatrix4f mat)
	{
		Vec3 pos = ModMatrix4f.transform(mat, Vec3.ZERO);
		this.moveTo(new Vec3(-pos.x, pos.y, -pos.z));
	}
	
	protected void moveTo(Vec3 pos)
	{
		this.worldCenter = pos;
		for (int i = 0; i < this.vertices.length; i++)
		{
			this.vertices[i] = this.vertices[i].add(this.worldCenter);
		}
	}
	
	protected Vec3 min()
	{
		return this.vertices[0];
	}
	
	protected Vec3 max()
	{
		return this.vertices[6];
	}
	
	public abstract boolean collidesWith(Collider other);
	
	public void update(LivingCap<?> entityCap, String jointName, float partialTicks)
	{
		ModMatrix4f transformMatrix;
		Armature armature = entityCap.getEntityModel(Models.SERVER).getArmature();
		int pathIndex = armature.searchPathIndex(jointName);

		if (pathIndex == -1) transformMatrix = new ModMatrix4f();
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
	public abstract void drawInternal(boolean red);

	@OnlyIn(Dist.CLIENT)
	public void draw(LivingCap<?> entityCap, String jointName, float partialTicks)
	{
		boolean red = entityCap.getEntityState() == EntityState.CONTACT;
		this.update(entityCap, jointName, partialTicks);

		this.drawInternal(red);
	}

	protected abstract boolean collidesWith(Entity opponent);

	protected void filterHitEntities(List<Entity> entities)
	{
		entities.removeIf((entity) -> !this.collidesWith(entity));
	}

	public AABB getHitboxAABB()
	{
		return this.outerAABB.move(this.getMassCenter());
	}
	
	public abstract Vec3 getMassCenter();
}