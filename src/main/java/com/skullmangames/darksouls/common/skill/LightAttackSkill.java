package com.skullmangames.darksouls.common.skill;

import java.util.List;

import javax.annotation.Nullable;

import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.common.capability.entity.PlayerData;
import com.skullmangames.darksouls.common.capability.entity.ServerPlayerData;
import com.skullmangames.darksouls.common.capability.item.WeaponCapability;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCResetBasicAttackCool;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;

public class LightAttackSkill extends AttackSkill
{
	protected final List<AttackAnimation> attackAnimations;
	protected final AttackAnimation dashAnimation;
	protected int combo;
	
	public LightAttackSkill(int duration, String skillName, List<AttackAnimation> attackanimations)
	{
		this(duration, skillName, attackanimations, null);
	}
	
	public LightAttackSkill(int duration, String skillName, List<AttackAnimation> attackanimations, @Nullable AttackAnimation dashanimation)
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
		
		AttackAnimation animation;
		if (this.dashAnimation == null) animation = this.attackAnimations.get(combo);
		else animation = executer.getOriginalEntity().isSprinting() ? this.dashAnimation : this.attackAnimations.get(combo);
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
		WeaponCapability item = executer.getHeldWeaponCapability(Hand.MAIN_HAND);
		if (item == null) return false;
		if (this != item.getLightAttack()) return false;
		
		return super.canExecute(executer);
	}
}
