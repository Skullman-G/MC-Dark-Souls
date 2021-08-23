package com.skullmangames.darksouls.common.skill;

import java.util.List;

import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.PlayerData;
import com.skullmangames.darksouls.common.capability.entity.ServerPlayerData;
import com.skullmangames.darksouls.common.capability.item.CapabilityItem;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCResetBasicAttackCool;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;

public class LightAttackSkill extends AttackSkill
{
	protected final List<StaticAnimation> attackAnimations;
	protected final StaticAnimation dashAnimation;
	protected int combo;
	
	public LightAttackSkill(int duration, String skillName, List<StaticAnimation> attackanimations, StaticAnimation dashanimation)
	{
		super(duration, true, skillName);
		this.attackAnimations = attackanimations;
		this.dashAnimation = dashanimation;
		this.combo = 0;
	}
	
	@Override
	public void executeOnServer(ServerPlayerData executer, PacketBuffer args)
	{
		if (combo >= attackAnimations.size() - 1) this.combo = 0;
		
		StaticAnimation animation = executer.getOriginalEntity().isSprinting() ? this.dashAnimation : this.attackAnimations.get(combo);
		executer.playAnimationSynchronize(animation, 0);
		ModNetworkManager.sendToPlayer(new STCResetBasicAttackCool(), executer.getOriginalEntity());
		
		this.combo++;
	}

	@Override
	public AttackSkill registerPropertiesToAnimation()
	{
		return null;
	}
	
	@Override
	public boolean canExecute(PlayerData<?> executer)
	{
		CapabilityItem item = executer.getHeldItemCapability(Hand.MAIN_HAND);
		if (item == null) return false;
		if (this != item.getLightAttack()) return false;
		
		return super.canExecute(executer);
	}
}
