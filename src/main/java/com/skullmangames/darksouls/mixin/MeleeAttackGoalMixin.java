package com.skullmangames.darksouls.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

@Mixin(MeleeAttackGoal.class)
public abstract class MeleeAttackGoalMixin
{
	@Shadow private int ticksUntilNextAttack;
	
	@Inject(at = @At(value = "HEAD"), method = "Lnet/minecraft/world/entity/ai/goal/MeleeAttackGoal;resetAttackCooldown()V", cancellable = true)
	protected void onResetAttackCooldown(CallbackInfo info)
	{
		info.cancel();
		System.out.print("\nworks");
		this.ticksUntilNextAttack = this.adjustedTickDelay(1000);
	}
	
	@Inject(at = @At(value = "RETURN"), method = "Lnet/minecraft/world/entity/ai/goal/MeleeAttackGoal;getAttackInterval()I", cancellable = true)
	protected void onGetAttackInterval(CallbackInfoReturnable<Integer> cir)
	{
		cir.setReturnValue(this.adjustedTickDelay(1000));
	}
	
	@Shadow protected abstract int adjustedTickDelay(int delay);
}
