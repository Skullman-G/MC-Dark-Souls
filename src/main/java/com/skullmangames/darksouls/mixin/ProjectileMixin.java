package com.skullmangames.darksouls.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.skullmangames.darksouls.common.capability.projectile.ProjectileCapability;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;

@Mixin(Projectile.class)
public abstract class ProjectileMixin
{
	@Inject(at = @At(value = "HEAD"), method = "onHitEntity(Lnet/minecraft/world/phys/EntityHitResult;)V", cancellable = true)
	protected void onHitEntity(EntityHitResult hitResult, CallbackInfo info)
	{
		ProjectileCapability<?> cap = ((Projectile)(Object)this).getCapability(ModCapabilities.CAPABILITY_PROJECTILE).orElse(null);
		if (cap != null)
		{
			cap.onHurt(hitResult.getEntity());
			info.cancel();
		}
	}
}
