package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public abstract class EntityCapability<T extends Entity>
{
	protected T orgEntity;

	public abstract void update();

	protected abstract void updateOnClient();

	protected abstract void updateOnServer();

	public void postInit() {}

	public void onEntityConstructed(T entityIn)
	{
		this.orgEntity = entityIn;
	}

	public void onEntityJoinWorld(T entity) {}

	public T getOriginalEntity()
	{
		return orgEntity;
	}
	
	public World getLevel()
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
		return this.orgEntity.xRot;
	}
	
	public float getYRot()
	{
		return this.orgEntity.yRot;
	}

	public boolean isClientSide()
	{
		return this.orgEntity.level.isClientSide;
	}
	
	public PublicMatrix4f getMatrix(float partialTicks)
	{
		return PublicMatrix4f.getModelMatrixIntegrated(0, 0, 0, 0, 0, 0, orgEntity.xRotO,
				orgEntity.xRot, orgEntity.yRotO, orgEntity.yRot, partialTicks, 1, 1, 1);
	}

	public abstract PublicMatrix4f getModelMatrix(float partialTicks);
}