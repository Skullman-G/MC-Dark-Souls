package com.skullmangames.darksouls.core.util;

import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class ProjectileUtil
{
	@Nullable
	public static EntityRayTraceResult getEntityHitResult(World p_37305_, Entity p_37306_, Vector3d p_37307_, Vector3d p_37308_,
			AxisAlignedBB p_37309_, Predicate<Entity> p_37310_)
	{
		return getEntityHitResult(p_37305_, p_37306_, p_37307_, p_37308_, p_37309_, p_37310_, 0.3F);
	}

	@Nullable
	public static EntityRayTraceResult getEntityHitResult(World p_150176_, Entity p_150177_, Vector3d p_150178_, Vector3d p_150179_,
			AxisAlignedBB p_150180_, Predicate<Entity> p_150181_, float p_150182_)
	{
		double d0 = Double.MAX_VALUE;
		Entity entity = null;

		for (Entity entity1 : p_150176_.getEntities(p_150177_, p_150180_, p_150181_))
		{
			AxisAlignedBB aabb = entity1.getBoundingBox().inflate((double) p_150182_);
			Optional<Vector3d> optional = aabb.clip(p_150178_, p_150179_);
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

		return entity == null ? null : new EntityRayTraceResult(entity);
	}

	public static void rotateTowardsMovement(Entity entity, float percentage)
	{
		Vector3d vec3 = entity.getDeltaMovement();
		if (vec3.lengthSqr() != 0.0D)
		{
			double d0 = MathHelper.sqrt(Entity.getHorizontalDistanceSqr(vec3));
			entity.xRot = (float)(MathHelper.atan2(d0, vec3.y) * (double) (180F / (float) Math.PI)) - 90.0F;
			entity.yRot = (float)(MathHelper.atan2(vec3.z, vec3.x) * (double) (180F / (float) Math.PI)) + 90.0F;

			while (entity.xRot - entity.xRotO < -180.0F)
			{
				entity.xRotO -= 360.0F;
			}

			while (entity.xRot - entity.xRotO >= 180.0F)
			{
				entity.xRotO += 360.0F;
			}

			while (entity.yRot - entity.yRotO < -180.0F)
			{
				entity.yRotO -= 360.0F;
			}

			while (entity.yRot - entity.yRotO >= 180.0F)
			{
				entity.yRotO += 360.0F;
			}

			entity.xRot = MathHelper.lerp(percentage, entity.xRotO, entity.xRot);
			entity.yRot = MathHelper.lerp(percentage, entity.yRotO, entity.yRot);
		}
	}
}