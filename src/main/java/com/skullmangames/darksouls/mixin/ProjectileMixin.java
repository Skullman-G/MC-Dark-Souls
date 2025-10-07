package com.skullmangames.darksouls.mixin;

import org.spongepowered.asm.mixin.Mixin;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.skullmangames.darksouls.common.capability.projectile.ProjectileCapability;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;

@Mixin(Projectile.class)
public abstract class ProjectileMixin
{
	@WrapMethod(method = "onHitEntity")
	protected void onHitEntity(EntityHitResult hitResult, Operation<Void> original)
	{
		Projectile self = (Projectile) (Object) this;
		ProjectileCapability<?> cap = self.getCapability(ModCapabilities.CAPABILITY_PROJECTILE).orElse(null);
		if (cap != null)
		{
			cap.onHurt(hitResult.getEntity());
		}
		else
		{
			original.call(hitResult);
		}
	}
}
