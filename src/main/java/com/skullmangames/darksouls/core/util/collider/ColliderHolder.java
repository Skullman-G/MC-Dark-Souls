package com.skullmangames.darksouls.core.util.collider;

import com.skullmangames.darksouls.common.capability.entity.EntityCapability;
import com.skullmangames.darksouls.common.capability.entity.EntityState;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.util.math.vector.ModMatrix4f;

import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ColliderHolder
{
	public ModMatrix4f savedTransform;
	private Collider type;
	
	public ColliderHolder(Collider type)
	{
		this.type = type;
	}
	
	public Collider getType()
	{
		return this.type;
	}
	
	public boolean isEmpty()
	{
		return this.type == null;
	}
	
	public void setType(Collider type)
	{
		this.type = type;
		this.savedTransform = null;
	}
	
	public void correctPosition()
	{
		if (!this.isEmpty() && this.savedTransform != null)
		{
			this.type.transform(this.savedTransform);
		}
	}
	
	public void clear()
	{
		this.setType(null);
	}
	
	public Vec3 getPreviousMassCenter()
	{
		if (this.isEmpty() || this.savedTransform == null) return null;
		this.type.transform(this.savedTransform);
		return this.type.getMassCenter();
	}
	
	public Vec3 getMassCenter()
	{
		return this.type.getMassCenter();
	}
	
	public Vec3 top()
	{
		return this.type.top();
	}
	
	public Vec3 bottom()
	{
		return this.type.bottom();
	}
	
	public ModMatrix4f update(EntityCapability<?> entityCap, String jointName, float partialTicks)
	{
		return this.update(entityCap, jointName, partialTicks, false);
	}
	
	public ModMatrix4f update(EntityCapability<?> entityCap, String jointName, float partialTicks, boolean safe)
	{
		ModMatrix4f transformMatrix = this.type.update(entityCap, jointName, partialTicks);
		if (safe) this.savedTransform = transformMatrix;
		return transformMatrix;
	}

	@OnlyIn(Dist.CLIENT)
	public void draw(EntityCapability<?> entityCap, String jointName, float partialTicks)
	{
		boolean red = entityCap instanceof LivingCap<?> livingCap ? livingCap.getEntityState() == EntityState.CONTACT : false;
		this.update(entityCap, jointName, partialTicks);

		this.type.drawInternal(red);
	}
	
	@Override
	public String toString()
	{
		return String.format("Center : [%f, %f, %f]", this.type.getWorldCenter().x, this.type.getWorldCenter().y,
				this.type.getWorldCenter().z);
	}
}
