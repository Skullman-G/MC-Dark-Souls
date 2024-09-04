package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.core.util.collider.CapsuleCollider;
import com.skullmangames.darksouls.core.util.collider.ColliderHolder;
import com.skullmangames.darksouls.core.util.math.vector.ModMatrix4f;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class EntityCapability<T extends Entity>
{
	protected T orgEntity;
	private ColliderHolder entityCollider = new ColliderHolder(null);

	public abstract void update();

	protected abstract void updateOnClient();

	protected abstract void updateOnServer();

	public void postInit() {}

	public void onEntityConstructed(T entityIn)
	{
		this.orgEntity = entityIn;
		this.entityCollider.setType(new CapsuleCollider(DarkSouls.rl("entity_hitbox"),
				this.orgEntity.getBbWidth(), this.orgEntity.getBbHeight() + this.orgEntity.getBbWidth() / 2F, Vec3.ZERO, -90F, 0F));
	}

	public void onEntityJoinWorld(T entity) {}

	public T getOriginalEntity()
	{
		return orgEntity;
	}
	
	public Level getLevel()
	{
		return this.orgEntity.level;
	}
	
	public double getX()
	{
		return this.orgEntity.getX();
	}
	
	public double getY()
	{
		return this.orgEntity.getY();
	}
	
	public double getZ()
	{
		return this.orgEntity.getZ();
	}
	
	public float getXRot()
	{
		return this.orgEntity.getXRot();
	}
	
	public float getYRot()
	{
		return this.orgEntity.getYRot();
	}

	public boolean isClientSide()
	{
		return this.orgEntity.level.isClientSide;
	}
	
	public ModMatrix4f getMatrix(float partialTicks)
	{
		return ModMatrix4f.createModelMatrix(0, 0, 0, 0, 0, 0, orgEntity.xRotO,
				orgEntity.xRot, orgEntity.yRotO, orgEntity.yRot, partialTicks, 1, 1, 1);
	}

	public abstract ModMatrix4f getModelMatrix(float partialTicks);
	
	public boolean isInvincible()
	{
		return false;
	}
	
	public ColliderHolder getEntityCollider()
	{
		return this.entityCollider;
	}
}