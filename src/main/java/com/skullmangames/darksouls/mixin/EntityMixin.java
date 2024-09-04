package com.skullmangames.darksouls.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

@Mixin(Entity.class)
public abstract class EntityMixin
{
	@Inject(at = @At(value = "HEAD"), method = "collide(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;", cancellable = true)
	protected void onCollide(Vec3 movement, CallbackInfoReturnable<Vec3> callback)
	{
		/*Entity entity = ((Entity) (Object) this);
		EntityCapability<?> cap = (EntityCapability<?>)entity.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
		if (cap != null)
		{
			// Vanilla block collision
			AABB aabb = entity.getBoundingBox();
			List<VoxelShape> emptyList = new ArrayList<>();
			Vec3 collidedMovement = movement.lengthSqr() == 0.0D ? movement
					: Entity.collideBoundingBox(entity, movement, aabb, entity.level, emptyList);
			
			// Checking if the entity can be pushed one step up
			boolean xChanged = movement.x != collidedMovement.x;
			boolean yChanged = movement.y != collidedMovement.y;
			boolean zChanged = movement.z != collidedMovement.z;
			boolean flag3 = entity.isOnGround() || yChanged && movement.y < 0.0D;
			double stepHeight = (double)entity.getStepHeight();
			if (stepHeight > 0.0F && flag3 && (xChanged || zChanged))
			{
				Vec3 stepUpMovement = Entity.collideBoundingBox(entity, new Vec3(movement.x, stepHeight, movement.z), aabb,
						entity.level, emptyList);
				
				// I think this is used for slabs
				Vec3 onlyStepUp = Entity.collideBoundingBox(entity, new Vec3(0.0D, stepHeight, 0.0D),
						aabb.expandTowards(movement.x, 0.0D, movement.z), entity.level, emptyList);
				if (onlyStepUp.y < stepHeight)
				{
					Vec3 postStepUpMovement = Entity.collideBoundingBox(entity, new Vec3(movement.x, 0.0D, movement.z), aabb.move(onlyStepUp),
							entity.level, emptyList).add(onlyStepUp);
					if (postStepUpMovement.horizontalDistanceSqr() > stepUpMovement.horizontalDistanceSqr())
					{
						stepUpMovement = postStepUpMovement;
					}
				}

				if (stepUpMovement.horizontalDistanceSqr() > collidedMovement.horizontalDistanceSqr())
				{
					collidedMovement = stepUpMovement.add(Entity.collideBoundingBox(entity, new Vec3(0.0D, -stepUpMovement.y + movement.y, 0.0D),
							aabb.move(stepUpMovement), entity.level, emptyList));
				}
			}
			
			// Entity collision
			ColliderHolder collider = cap.getEntityCollider();
			collider.update(cap, "Root", 1.0F, true);
			List<ColliderHolder> entityColliders = entity.level.getEntities(entity, collider.getType().getHitboxAABB().expandTowards(movement))
					.stream()
					.map((e) ->
					{
						EntityCapability<?> c = (EntityCapability<?>) e.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
						if (c == null) return null;
						c.getEntityCollider().update(cap, "Root", 1.0F, true);
						return c.getEntityCollider();
					})
					.filter((c) -> c != null)
					.toList();
			
			collidedMovement = collider.getType().collide(collidedMovement, entityColliders);
			
			callback.setReturnValue(collidedMovement);
		}*/
	}
}
