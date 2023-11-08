package com.skullmangames.darksouls.common.capability.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.mojang.datafixers.util.Pair;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.RangeAttackGoal;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.network.server.STCMobInitialSetting;

import io.netty.buffer.ByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedCrossbowAttackGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;

public abstract class MobCap<T extends Mob> extends LivingCap<T>
{
	private Pair<InteractionHand, ItemStack> itemChange;
	private Consumer<MobCap<T>> afterItemChange;
	
	@Override
	public void onEntityJoinWorld(T entityIn)
	{
		super.onEntityJoinWorld(entityIn);
		this.initAI();
	}
	
	@Override
	protected void updateOnServer()
	{
		super.updateOnServer();
		
		EntityState state = this.getEntityState();
		if (state.canAct() || state.getContactLevel() == 3)
		{
			float staminaIncr = 2F;
			if (this.isBlocking() || this.orgEntity.onClimbable()) staminaIncr *= 0.5F;
			
			this.increaseStamina(staminaIncr);
		}
	}
	
	public boolean isChangingItem()
	{
		return this.itemChange != null;
	}
	
	public void changeItemAnimated(InteractionHand hand, ItemStack itemStack)
	{
		this.changeItemAnimated(hand, itemStack, null);
	}
	
	public void changeItemAnimated(InteractionHand hand, ItemStack itemStack, Consumer<MobCap<T>> action)
	{
		if (this.orgEntity.getItemInHand(hand) == itemStack) return;
		
		this.itemChange = new Pair<>(hand, itemStack);
		this.afterItemChange = action;
		
		StaticAnimation animation = hand == InteractionHand.OFF_HAND ? Animations.BIPED_CHANGE_ITEM_LEFT :
			Animations.BIPED_CHANGE_ITEM_RIGHT;
		
		this.playAnimationSynchronized(animation, 0.0F);
	}
	
	public void changeItem()
	{
		if (this.itemChange != null)
		{
			InteractionHand hand = this.itemChange.getFirst();
			ItemStack itemStack = this.itemChange.getSecond();
			
			this.orgEntity.setItemInHand(hand, itemStack);
			this.itemChange = null;
			
			if (this.afterItemChange != null)
			{
				this.afterItemChange.accept(this);
				this.afterItemChange = null;
			}
		}
	}
	
	protected void initAI()
	{
		this.resetCombatAI();
	}

	protected void resetCombatAI()
	{
		Stream<WrappedGoal> goals = this.orgEntity.goalSelector.getRunningGoals();
		Iterator<WrappedGoal> iterator = goals.iterator();
		List<Goal> toRemove = new ArrayList<>();
		
		while (iterator.hasNext())
		{
			WrappedGoal goal = iterator.next();
			Goal inner = goal.getGoal();
            
            if (inner instanceof MeleeAttackGoal || inner instanceof RangedBowAttackGoal || inner instanceof RangeAttackGoal
            		|| inner instanceof RangedAttackGoal || inner instanceof AttackGoal || inner instanceof RangedCrossbowAttackGoal)
            {
            	toRemove.add(inner);
            }
        }
        
		for (Goal goal : toRemove)
		{
        	this.orgEntity.goalSelector.removeGoal(goal);
        }
	}
	
	public STCMobInitialSetting sendInitialInformationToClient()
	{
		return null;
	}

	public void clientInitialSettings(ByteBuf buf) {}
	
	@Override
	public boolean isTeam(Entity entity)
	{
		if (entity instanceof Projectile) return false;
		EntityCapability<?> cap = entity.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
		if(cap != null && cap instanceof MobCap)
		{
			Optional<LivingEntity> opt = Optional.ofNullable(this.getTarget());
			return opt.map((attackTarget)->!attackTarget.equals(entity)).orElse(true);
		}
		
		return super.isTeam(entity);
	}
	
	@Override
	public LivingEntity getTarget()
	{
		return this.orgEntity.getTarget();
	}
}