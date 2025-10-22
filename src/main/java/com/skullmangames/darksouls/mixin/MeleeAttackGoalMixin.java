package com.skullmangames.darksouls.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

@Mixin(MeleeAttackGoal.class)
public abstract class MeleeAttackGoalMixin
{
	@Shadow
	private int ticksUntilNextAttack;

	@Shadow
	protected abstract boolean requiresUpdateEveryTick();

	private int adjustedTickDelay(int ticks)
	{
		return this.requiresUpdateEveryTick() ? ticks : Goal.reducedTickDelay(ticks);
	}

	@Inject(method = "resetAttackCooldown", at = @At("HEAD"), cancellable = true)
	protected void onResetAttackCooldown(CallbackInfo info)
	{
		info.cancel();
		this.ticksUntilNextAttack = this.adjustedTickDelay(100);
	}

	@Inject(method = "getAttackInterval", at = @At("RETURN"))
	protected void onGetAttackInterval(CallbackInfoReturnable<Integer> info)
	{
		info.setReturnValue(this.adjustedTickDelay(100));
	}
}
