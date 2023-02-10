package com.skullmangames.darksouls.common.capability.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.skullmangames.darksouls.common.capability.item.AttributeItemCap;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.ChasingGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.RangeAttackGoal;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.network.server.STCMobInitialSetting;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.ai.goal.RangedAttackGoal;
import net.minecraft.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.entity.ai.goal.RangedCrossbowAttackGoal;
import net.minecraft.entity.projectile.ProjectileEntity;

public abstract class MobCap<T extends MobEntity> extends LivingCap<T>
{
	@Override
	public void onEntityJoinWorld(T entityIn)
	{
		super.onEntityJoinWorld(entityIn);
		initAI();
	}
	
	@Override
	protected void updateOnServer()
	{
		super.updateOnServer();
		
		EntityState state = this.getEntityState();
		if (state.canAct() || state.getContactLevel() == 3)
		{
			float staminaIncr = 0.2F;
			if (this.isBlocking() || this.orgEntity.onClimbable()) staminaIncr *= 0.2F;
			
			this.increaseStamina(staminaIncr);
		}
	}
	
	protected void initAI()
	{
		this.resetCombatAI();
	}

	protected void resetCombatAI()
	{
		Stream<PrioritizedGoal> goals = this.orgEntity.goalSelector.getRunningGoals();
		Iterator<PrioritizedGoal> iterator = goals.iterator();
		List<Goal> toRemove = new ArrayList<>();
		
		while (iterator.hasNext())
		{
			PrioritizedGoal goal = iterator.next();
			Goal inner = goal.getGoal();
            
            if (inner instanceof MeleeAttackGoal || inner instanceof RangedBowAttackGoal || inner instanceof RangeAttackGoal || inner instanceof ChasingGoal
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
	public void onArmorSlotChanged(AttributeItemCap fromCap, AttributeItemCap toCap, EquipmentSlotType slotType) {}
	
	@Override
	public boolean isTeam(Entity entity)
	{
		if (entity instanceof ProjectileEntity) return false;
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