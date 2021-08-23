package com.skullmangames.darksouls.common.skill;

import com.skullmangames.darksouls.client.ClientEngine;
import com.skullmangames.darksouls.common.capability.entity.ClientPlayerData;
import com.skullmangames.darksouls.common.capability.entity.PlayerData;
import com.skullmangames.darksouls.common.capability.entity.ServerPlayerData;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SkillExecutionHelper
{
	protected static int prevDuration = 0;
	protected static int duration = 0;
	protected static boolean isActivated = false;
	protected static boolean consumeDuration;
	protected static CompoundNBT skillVariables = new CompoundNBT();
	
	private static Skill ACTIVE_SKILL;
	private static PlayerData<?> EXECUTER;
	
	
	public static PlayerData<?> getExecuter()
	{
		return EXECUTER;
	}
	
	public static Skill getActiveSkill()
	{
		return ACTIVE_SKILL;
	}
	
	public static void setActiveSkill(Skill skill)
	{
		ACTIVE_SKILL = skill;
	}
	
	public static void reset(boolean consume)
	{
		isActivated = false;
		consumeDuration = true;
		prevDuration = 0;
		duration = 0;

		if (getActiveSkill() != null && getActiveSkill().maxStackSize <= 1)
		{
			getActiveSkill().onReset();
		}
	}
	
	public static void setDuration(int value)
	{
		if (ACTIVE_SKILL != null)
		{
			if(!isActivated && value > 0)
			{
				isActivated = true;
			}
			
			duration = value;
			duration = Math.min(ACTIVE_SKILL.duration, Math.max(duration, 0));
		}
		else
		{
			duration = 0;
		}
	}
	
	public static void setDurationConsume(boolean set)
	{
		consumeDuration = set;
	}

	@OnlyIn(Dist.CLIENT)
	public static void execute(ClientPlayerData executer, Skill skill)
	{
		if(canExecute(executer, skill))
		{
			EXECUTER = executer;
			ACTIVE_SKILL = skill;
			skill.executeOnClient((ClientPlayerData)executer, skill.gatherArguments(executer, ClientEngine.INSTANCE.inputController));
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public void cancel(ClientPlayerData executer, PacketBuffer pb, Skill skill)
	{
		skill.cancelOnClient(executer, pb);
	}
	
	public static boolean requestExecute(ServerPlayerData executer, PacketBuffer buf)
	{
		Skill skill = getActiveSkill();
		if (canExecute(executer, skill))
		{
			skill.execute();
			skill.executeOnServer(executer, buf);
			return true;
		}
		
		return false;
	}
	
	public static CompoundNBT getVariableNBT()
	{
		return skillVariables;
	}

	public static int getRemainDuration()
	{
		return duration;
	}
	
	public static boolean canExecute(PlayerData<?> executer)
	{
		return canExecute(executer, getActiveSkill());
	}

	public static boolean canExecute(PlayerData<?> executer, Skill skill)
	{
		return skill.canExecute(executer);
	}
	
	public static void update()
	{
		if(ACTIVE_SKILL != null)
		{
			ACTIVE_SKILL.update();
		}
	}

	public static boolean hasSkill(Skill skill)
	{
		return ACTIVE_SKILL != null ? ACTIVE_SKILL.equals(skill) : false;
	}

	public float getDurationRatio(float partialTicks)
	{
		return ACTIVE_SKILL != null && ACTIVE_SKILL.duration > 0 ? (prevDuration + ((duration - prevDuration) *
				partialTicks)) / ACTIVE_SKILL.duration : 0;
	}
}