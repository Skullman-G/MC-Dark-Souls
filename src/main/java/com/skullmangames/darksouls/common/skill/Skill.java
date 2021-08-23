package com.skullmangames.darksouls.common.skill;

import java.util.List;

import com.google.common.collect.Lists;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.input.InputManager;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.capability.entity.ClientPlayerData;
import com.skullmangames.darksouls.common.capability.entity.PlayerData;
import com.skullmangames.darksouls.common.capability.entity.ServerPlayerData;
import com.skullmangames.darksouls.common.capability.entity.LivingData.EntityState;
import com.skullmangames.darksouls.common.capability.item.CapabilityItem;
import com.skullmangames.darksouls.core.util.Formulars;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCSetSkillValue;
import com.skullmangames.darksouls.network.server.STCSetSkillValue.Target;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Skill
{
	protected ResourceLocation registryName;
	protected final boolean isActiveSkill;
	protected final int duration;
	protected final int maxStackSize;
	
	public Skill(String skillName)
	{
		this(0, 1, true, skillName);
	}
	
	public Skill(int maxStack, String skillName)
	{
		this(0, maxStack, true, skillName);
	}
	
	public Skill(int duration, boolean isActiveSkill, String skillName)
	{
		this(duration, 1, true, skillName);
	}
	
	public Skill(int duration, int maxStack, boolean isActiveSkill, String skillName)
	{
		this.duration = duration;
		this.isActiveSkill = isActiveSkill;
		this.maxStackSize = maxStack;
		this.registryName = new ResourceLocation(DarkSouls.MOD_ID, skillName);
	}
	
	public PacketBuffer gatherArguments(ClientPlayerData executer, InputManager inputManager)
	{
		return null;
	}
	
	public boolean isExecutableState(PlayerData<?> executer)
	{
		EntityState playerState = executer.getEntityState();
		return !(executer.getOriginalEntity().isFallFlying() || executer.currentMotion == LivingMotion.FALL || !playerState.canAct());
	}
	
	public boolean canExecute(PlayerData<?> executer)
	{
		return true;
	}
	
	/**
	 * Gather arguments in client and send packet
	 * Process the skill execution with given arguments
	 */
	public void executeOnClient(ClientPlayerData executer, PacketBuffer args) {}
	
	public void executeOnServer(ServerPlayerData executer, PacketBuffer args)
	{
		setDurationSynchronize(executer, this.duration);
	}
	
	/**
	 * Use this method when skill is end
	 */
	public void cancelOnClient(ClientPlayerData executer, PacketBuffer args) {}
	
	public void execute()
	{
		SkillExecutionHelper.setActiveSkill(this);
		SkillExecutionHelper.duration = this.duration;
		SkillExecutionHelper.isActivated = true;
	}
	
	public void onInitiate() {}

	public void onDeleted() {}

	public void onReset() {}
	
	public void update()
	{
		PlayerData<?> executer = SkillExecutionHelper.getExecuter();
		SkillExecutionHelper.prevDuration = SkillExecutionHelper.duration;
		
		if (SkillExecutionHelper.isActivated)
		{
			if (SkillExecutionHelper.consumeDuration)
			{
				SkillExecutionHelper.duration--;
			}

			if (SkillExecutionHelper.duration <= 0)
			{
				if(SkillExecutionHelper.getExecuter().isRemote())
				{
					SkillExecutionHelper.getActiveSkill().cancelOnClient((ClientPlayerData)executer, null);
				}
				else
				{
					SkillExecutionHelper.getActiveSkill().cancelOnServer((ServerPlayerData)executer, null);
				}
				SkillExecutionHelper.isActivated = false;
				SkillExecutionHelper.duration = 0;
			}
		}
	}
	
	public void cancelOnServer(ServerPlayerData executer, PacketBuffer args) {}
	
	public static void setDurationSynchronize(ServerPlayerData executer, int amount)
	{
		SkillExecutionHelper.setDuration(amount);
		ModNetworkManager.sendToPlayer(new STCSetSkillValue(Target.DURATION, amount, false), executer.getOriginalEntity());
	}
	
	public static void setDurationConsumeSynchronize(ServerPlayerData executer, boolean bool)
	{
		SkillExecutionHelper.setDurationConsume(bool);
		ModNetworkManager.sendToPlayer(new STCSetSkillValue(Target.DURATION_CONSUME, 0, bool), executer.getOriginalEntity());
	}
	
	@OnlyIn(Dist.CLIENT)
	public List<ITextComponent> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerData<?> playerCap)
	{
		List<ITextComponent> list = Lists.<ITextComponent>newArrayList();
		return list;
	}
	
	public ResourceLocation getRegistryName()
	{
		return this.registryName;
	}

	public float getRegenTimePerTick(PlayerData<?> player)
	{
		return Formulars.getSkillRegen((float)player.getWeight(), player);
	}

	public int getMaxStack()
	{
		return this.maxStackSize;
	}

	public boolean isActiveSkill()
	{
		return this.isActiveSkill;
	}
}