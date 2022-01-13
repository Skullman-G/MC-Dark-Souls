package com.skullmangames.darksouls.common.entity.ai.goal;

import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.common.capability.entity.MobData;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCPlayAnimationTarget;

import net.minecraft.world.entity.Mob;
import net.minecraft.util.Mth;

public class AttackInstance
{
	protected final AttackAnimation[] animation;
	protected final float offset;
	protected final float range;
	protected final int priority;
	
	public AttackInstance(int priority, float range, AttackAnimation... animation)
	{
		this(priority, 0.0F, range, animation);
	}
	
	public AttackInstance(int priority, float offset, float range, AttackAnimation... animation)
	{
		this.animation = animation;
		this.offset = offset;
		this.range = range;
		this.priority = Mth.clamp(priority, 0, 9);
	}
	
	public boolean isValidRange(double targetRange)
	{
		return offset <= targetRange && targetRange <= range;
	}
	
	public void performAttack(MobData<?> mobdata, int combo)
	{
		Mob attacker = mobdata.getOriginalEntity();
		mobdata.getServerAnimator().playAnimation(this.animation[combo], 0);
    	mobdata.updateInactionState();
    	ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimationTarget(this.animation[combo].getId(), attacker.getId(), 0, attacker.getTarget().getId()), attacker);
	}
}
