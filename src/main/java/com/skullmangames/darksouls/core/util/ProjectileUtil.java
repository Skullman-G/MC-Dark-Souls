package com.skullmangames.darksouls.core.util;

import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class ProjectileUtil
{
	@Nullable
	public static EntityHitResult getEntityHitResult(Level p_37305_, Entity p_37306_, Vec3 p_37307_, Vec3 p_37308_,
			AABB p_37309_, Predicate<Entity> p_37310_)
	{
		return getEntityHitResult(p_37305_, p_37306_, p_37307_, p_37308_, p_37309_, p_37310_, 0.3F);
	}

	@Nullable
	public static EntityHitResult getEntityHitResult(Level p_150176_, Entity p_150177_, Vec3 p_150178_, Vec3 p_150179_,
			AABB p_150180_, Predicate<Entity> p_150181_, float p_150182_)
	{
		double d0 = Double.MAX_VALUE;
		Entity entity = null;

		for (Entity entity1 : p_150176_.getEntities(p_150177_, p_150180_, p_150181_))
		{
			AABB aabb = entity1.getBoundingBox().inflate((double) p_150182_);
			Optional<Vec3> optional = aabb.clip(p_150178_, p_150179_);
			if (optional.isPresent())
			{
				double d1 = p_150178_.distanceToSqr(optional.get());
				if (d1 < d0)
				{
					entity = entity1;
					d0 = d1;
				}
			}
		}

		return entity == null ? null : new EntityHitResult(entity);
	}

	public static void rotateTowardsMovement(Entity p_37285_, float p_37286_)
	{
		Vec3 vec3 = p_37285_.getDeltaMovement();
		if (vec3.lengthSqr() != 0.0D)
		{
			double d0 = vec3.horizontalDistance();
			p_37285_.setYRot((float) (Mth.atan2(vec3.z, vec3.x) * (double) (180F / (float) Math.PI)) + 90.0F);
			p_37285_.setXRot((float) (Mth.atan2(d0, vec3.y) * (double) (180F / (float) Math.PI)) - 90.0F);

			while (p_37285_.getXRot() - p_37285_.xRotO < -180.0F)
			{
				p_37285_.xRotO -= 360.0F;
			}

			while (p_37285_.getXRot() - p_37285_.xRotO >= 180.0F)
			{
				p_37285_.xRotO += 360.0F;
			}

			while (p_37285_.getYRot() - p_37285_.yRotO < -180.0F)
			{
				p_37285_.yRotO -= 360.0F;
			}

			while (p_37285_.getYRot() - p_37285_.yRotO >= 180.0F)
			{
				p_37285_.yRotO += 360.0F;
			}

			p_37285_.setXRot(Mth.lerp(p_37286_, p_37285_.xRotO, p_37285_.getXRot()));
			p_37285_.setYRot(Mth.lerp(p_37286_, p_37285_.yRotO, p_37285_.getYRot()));
		}
	}
}
