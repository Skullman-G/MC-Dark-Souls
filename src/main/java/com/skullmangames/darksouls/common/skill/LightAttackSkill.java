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
	protected int combo = 0;
	protected boolean repeatCombo;
	
	public LightAttackSkill(int duration, String skillName, List<AttackAnimation> attackanimations)
	{
		this(duration, skillName, true, attackanimations, null);
	}
	
	public LightAttackSkill(int duration, String skillName, boolean repeatCombo, List<AttackAnimation> attackanimations)
	{
		this(duration, skillName, repeatCombo, attackanimations, null);
	}
	
	public LightAttackSkill(int duration, String skillName, List<AttackAnimation> attackanimations, @Nullable AttackAnimation dashanimation)
	{
		this(duration, skillName, true, attackanimations, dashanimation);
	}
	
	public LightAttackSkill(int duration, String skillName, boolean repeatCombo, List<AttackAnimation> attackanimations, @Nullable AttackAnimation dashanimation)
	{
		super(duration, true, skillName);
		this.attackAnimations = attackanimations;
		this.dashAnimation = dashanimation;
		this.repeatCombo = repeatCombo;
	}
	
	@Override
	public void executeOnServer(ServerPlayerData executer, PacketBuffer args)
	{
		if (this.combo > attackAnimations.size() - 1)
		{
			if (this.repeatCombo) this.combo = 0;
			else this.combo = attackAnimations.size() - 1;
		}
		
		AttackAnimation animation;
		animation = this.dashAnimation != null && executer.getOriginalEntity().isSprinting() ? this.dashAnimation : this.attackAnimations.get(combo);
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
		if (this != item.getLightAttack(executer.getOriginalEntity())) return false;
		
		return super.canExecute(executer);
	}
}
