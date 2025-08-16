package com.skullmangames.darksouls.core.util.collider;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.skullmangames.darksouls.core.util.math.vector.ModMatrix4f;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class MultiCollider extends Collider
{
	private final Collider[] colliders;
	
	public MultiCollider(ColliderType<?> type, Collider... colliders)
	{
		super(type, new AABB(0, 0, 0, 0, 0, 0));
		this.colliders = colliders;
	}

	@Override
	protected Vec3 min()
	{
		return this.colliders[0].min();
	}

	@Override
	protected Vec3 max()
	{
		return this.colliders[0].max();
	}

	@Override
	public Vec3 top()
	{
		return this.colliders[0].top();
	}

	@Override
	public Vec3 bottom()
	{
		return this.colliders[0].bottom();
	}
	
	@Override
	public List<Entity> getEntityCollisions(Entity self, Predicate<Entity> additionalFilters)
	{
		List<Entity> allCollisions = new ArrayList<>();
		for (Collider collider : this.colliders)
		{
			List<Entity> singleCollisions = collider.getEntityCollisions(self, additionalFilters.or((entity) -> allCollisions.contains(entity)));
			allCollisions.addAll(singleCollisions);
		}
		return allCollisions;
	}
	
	@Override
	protected List<Entity> getShieldCollisions(Entity self, Predicate<Entity> additionalFilters)
	{
		List<Entity> allCollisions = new ArrayList<>();
		for (Collider collider : this.colliders)
		{
			List<Entity> singleCollisions = collider.getShieldCollisions(self, additionalFilters.or((entity) -> allCollisions.contains(entity)));
			allCollisions.addAll(singleCollisions);
		}
		return allCollisions;
	}

	@Override
	public boolean collidesWith(Collider other)
	{
		for (Collider collider : this.colliders)
		{
			if (collider.collidesWith(other)) return true;
		}
		return false;
	}

	@Override
	public void drawInternal(boolean red)
	{
		for (Collider c : this.colliders) c.drawInternal(red);
	}

	@Override
	protected boolean collidesWith(Entity opponent)
	{
		return false;
	}

	@Override
	public Vec3 getMassCenter()
	{
		return this.colliders[0].getMassCenter();
	}
	
	@Override
	public void transform(ModMatrix4f mat)
	{
		for (Collider collider : this.colliders)
		{
			collider.transform(mat);
		}
	}
	
	@Override
	public String toString()
	{
		return super.toString() + " [multi]";
	}
}
