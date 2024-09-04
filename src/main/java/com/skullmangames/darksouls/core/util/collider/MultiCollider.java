package com.skullmangames.darksouls.core.util.collider;

import java.util.ArrayList;
import java.util.List;

import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.init.data.Colliders;
import com.skullmangames.darksouls.core.util.math.vector.ModMatrix4f;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class MultiCollider extends Collider
{
	private final Collider[] colliders;
	
	public MultiCollider(ResourceLocation id, Collider... colliders)
	{
		super(id, new AABB(0, 0, 0, 0, 0, 0));
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
	public List<Entity> getEntityCollisions(Entity self)
	{
		List<Entity> list = new ArrayList<>();
		for (Collider collider : this.colliders)
		{
			List<Entity> entities = self.level.getEntities(self, collider.getHitboxAABB());
			for (int i = 0; i < list.size(); i++)
			{
				Entity e = list.get(i);
				if (entities.contains(e)) entities.remove(e);
			}
			collider.filterHitEntities(list);
			list.addAll(entities);
		}
		return list;
	}
	
	@Override
	public List<Entity> getShieldCollisions(Entity self)
	{
		List<Entity> newList = new ArrayList<>();
		for (Collider collider : this.colliders)
		{
			List<Entity> list = self.level.getEntities(self, collider.getHitboxAABB().inflate(5));
			for (int i = 0; i < newList.size(); i++)
			{
				Entity e = newList.get(i);
				if (list.contains(e)) list.remove(e);
			}
			for (Entity e : list)
			{
				if (e instanceof LivingEntity)
				{
					LivingCap<?> cap = (LivingCap<?>)e.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
					if (cap != null && cap.isBlocking())
					{
						ModMatrix4f modelMat = cap.getModelMatrix(1.0F).rotateDeg(90, Vector3f.YP);
						ModMatrix4f mat = modelMat.translate(0.4F, e.getBbHeight() / 2, 0);
						Collider shieldCollider = Colliders.SHIELD.get();
						shieldCollider.transform(mat);
						if (collider.collidesWith(shieldCollider)) newList.add(e);
					}
				}
			}
		}
		return newList;
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
	public Vec3 collide(Vec3 movement, List<ColliderHolder> others)
	{
		Vec3 pushOutVec = Vec3.ZERO;
		for (Collider collider : this.colliders)
		{
			Vec3 newCandidate = collider.collide(movement, others);
			if (pushOutVec.lengthSqr() < newCandidate.lengthSqr()) pushOutVec = newCandidate;
		}
		return pushOutVec;
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
}
