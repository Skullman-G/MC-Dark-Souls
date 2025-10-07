package com.skullmangames.darksouls.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

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

	@WrapMethod(method = "resetAttackCooldown")
	protected void onResetAttackCooldown(Operation<Void> original)
	{
		this.ticksUntilNextAttack = this.adjustedTickDelay(100);
	}

	@WrapMethod(method = "getAttackInterval")
	protected int onGetAttackInterval(Operation<Integer> original)
	{
		return this.adjustedTickDelay(100);
	}
}
