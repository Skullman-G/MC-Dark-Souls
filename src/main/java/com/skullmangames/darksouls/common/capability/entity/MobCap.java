package com.skullmangames.darksouls.common.capability.entity;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.google.common.collect.Lists;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.capability.item.AttributeItemCap;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.ChasingGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.RangeAttackGoal;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.util.math.MathUtils;
import com.skullmangames.darksouls.network.server.STCMobInitialSetting;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
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
		resetCombatAI();
	}

	protected void resetCombatAI()
	{
		Stream<PrioritizedGoal> goals = this.orgEntity.goalSelector.getRunningGoals();
		Iterator<PrioritizedGoal> iterator = goals.iterator();
		List<Goal> toRemove = Lists.<Goal>newArrayList();
		
		while (iterator.hasNext())
		{
        	PrioritizedGoal goal = iterator.next();
            Goal inner = goal.getGoal();
            
            if (inner instanceof MeleeAttackGoal || inner instanceof RangedBowAttackGoal  || inner instanceof RangeAttackGoal || inner instanceof ChasingGoal
            		|| inner instanceof RangedAttackGoal || inner instanceof AttackGoal || inner instanceof RangedCrossbowAttackGoal)
            {
            	toRemove.add(inner);
            }
        }
        
		for (Goal AI : toRemove)
		{
        	orgEntity.goalSelector.removeGoal(AI);
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
	
	@Override
	public float getAttackDirectionPitch()
	{
		Entity attackTarget = this.getTarget();
		if (attackTarget != null)
		{
			float partialTicks = DarkSouls.isPhysicalClient() ? Minecraft.getInstance().getFrameTime() : 1.0F;
			Vector3d target = attackTarget.getEyePosition(partialTicks);
			Vector3d vector3d = this.orgEntity.getEyePosition(partialTicks);
			double d0 = target.x - vector3d.x;
			double d1 = target.y - vector3d.y;
			double d2 = target.z - vector3d.z;
			double d3 = (double) Math.sqrt(d0 * d0 + d2 * d2);
			return MathUtils.clamp(MathHelper.wrapDegrees((float) ((Math.atan2(d1, d3) * (double) (180F / (float) Math.PI)))), -30.0F, 30.0F);
		}
		else
		{
			return super.getAttackDirectionPitch();
		}
	}
}