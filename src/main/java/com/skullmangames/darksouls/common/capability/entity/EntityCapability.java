package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

import net.minecraft.world.entity.Entity;

public abstract class EntityCapability<T extends Entity>
{
	protected T orgEntity;

	public abstract void update();

	protected abstract void updateOnClient();

	protected abstract void updateOnServer();

	public void postInit()
	{
	}

	public void onEntityConstructed(T entityIn)
	{
		this.orgEntity = entityIn;
	}

	public void onEntityJoinWorld(T entity) {}

	public T getOriginalEntity()
	{
		return orgEntity;
	}

	public boolean isClientSide()
	{
		return orgEntity.level.isClientSide;
	}

	public void onDeath() {}
	
	public PublicMatrix4f getMatrix(float partialTicks)
	{
		return PublicMatrix4f.getModelMatrixIntegrated(0, 0, 0, 0, 0, 0, orgEntity.xRotO,
				orgEntity.xRot, orgEntity.yRotO, orgEntity.yRot, partialTicks, 1, 1, 1);
	}

	public abstract PublicMatrix4f getModelMatrix(float partialTicks);
}