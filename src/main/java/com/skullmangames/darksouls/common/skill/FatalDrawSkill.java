package com.skullmangames.darksouls.common.skill;

import com.skullmangames.darksouls.common.capability.entity.ServerPlayerData;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCResetBasicAttackCool;

import net.minecraft.network.PacketBuffer;

public class FatalDrawSkill extends SelectiveAttackSkill
{
	public FatalDrawSkill(String skillName)
	{
		super(skillName, (executer) -> executer.getOriginalEntity().isSprinting() ? 1 : 0, Animations.FATAL_DRAW, Animations.FATAL_DRAW_DASH);
	}
	
	@Override
	public void executeOnServer(ServerPlayerData executer, PacketBuffer args)
	{
		boolean isSheathed = SkillExecutionHelper.getVariableNBT().getBoolean("sheath");
		
		if(isSheathed)
		{
			executer.playAnimationSynchronize(this.attackAnimations[this.getAnimationInCondition(executer)], -0.666F);
		}
		else
		{
			executer.playAnimationSynchronize(this.attackAnimations[this.getAnimationInCondition(executer)], 0);
		}
		
		ModNetworkManager.sendToPlayer(new STCResetBasicAttackCool(), executer.getOriginalEntity());
	}
}