package com.skullmangames.darksouls.skill;

import java.util.List;

import com.google.common.collect.Lists;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.animation.LivingMotion;
import com.skullmangames.darksouls.client.event.engine.ControllEngine;
import com.skullmangames.darksouls.common.entities.ClientPlayerData;
import com.skullmangames.darksouls.common.entities.LivingData.EntityState;
import com.skullmangames.darksouls.common.entities.PlayerData;
import com.skullmangames.darksouls.common.entities.ServerPlayerData;
import com.skullmangames.darksouls.common.items.CapabilityItem;
import com.skullmangames.darksouls.config.ConfigurationIngame;
import com.skullmangames.darksouls.core.util.Formulars;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.play.server.STCSetSkillValue;
import com.skullmangames.darksouls.network.play.server.STCSetSkillValue.Target;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Skill
{
	protected ResourceLocation registryName;
	protected final SkillSlot slot;
	protected final boolean isActiveSkill;
	protected final float cooldown;
	protected final int duration;
	protected final int maxStackSize;
	
	public Skill(SkillSlot index, float cooldown, String skillName)
	{
		this(index, cooldown, 0, 1, true, skillName);
	}
	
	public Skill(SkillSlot index, float cooldown, int maxStack, String skillName)
	{
		this(index, cooldown, 0, maxStack, true, skillName);
	}
	
	public Skill(SkillSlot index, float cooldown, int duration, boolean isActiveSkill, String skillName)
	{
		this(index, cooldown, duration, 1, true, skillName);
	}
	
	public Skill(SkillSlot index, float cooldown, int duration, int maxStack, boolean isActiveSkill, String skillName)
	{
		this.cooldown = cooldown;
		this.duration = duration;
		this.isActiveSkill = isActiveSkill;
		this.slot = index;
		this.maxStackSize = maxStack;
		this.registryName = new ResourceLocation(DarkSouls.MOD_ID, skillName);
	}
	
	public PacketBuffer gatherArguments(ClientPlayerData executer, ControllEngine controllEngine)
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
	public void executeOnClient(ClientPlayerData executer, PacketBuffer args)
	{
		
	}
	
	public void executeOnServer(ServerPlayerData executer, PacketBuffer args)
	{
		this.setDurationSynchronize(executer, this.duration);
	}
	
	/**
	 * Use this method when skill is end
	 */
	public void cancelOnClient(ClientPlayerData executer, PacketBuffer args)
	{
		
	}
	
	public void execute(SkillContainer container)
	{
		container.duration = this.duration;
		container.isActivated = true;
	}
	
	public void onInitiate(SkillContainer container)
	{

	}

	public void onDeleted(SkillContainer container)
	{

	}

	public void onReset(SkillContainer container)
	{

	}
	
	public void setCooldown(SkillContainer container, float value)
	{
		container.cooldown = value;
		container.cooldown = Math.max(container.cooldown, 0);
		container.cooldown = Math.min(container.cooldown, this.cooldown);

		if (value >= this.cooldown)
		{
			if (container.stack < this.maxStackSize)
			{
				container.stack++;
				if (container.stack < this.maxStackSize)
				{
					container.cooldown = 0;
					container.prevCooldown = 0;
				}
			}
			else
			{
				container.cooldown = this.cooldown;
				container.prevCooldown = this.cooldown;
			}
		}
		else if (value == 0 && container.stack > 0)
			--container.stack;
	}
	
	public void update(SkillContainer container)
	{
		PlayerData<?> executer = container.executer;
		container.prevCooldown = container.cooldown;
		container.prevDuration = container.duration;
		
		if(container.stack < container.containingSkill.maxStackSize)
			container.setCooldown(container.cooldown + this.getRegenTimePerTick(executer) * ConfigurationIngame.A_TICK);
		
		if (container.isActivated)
		{
			if (container.consumeDuration)
			{
				container.duration--;
			}

			if (container.duration <= 0)
			{
				if(container.executer.isRemote())
				{
					container.containingSkill.cancelOnClient((ClientPlayerData)executer, null);
				}
				else
				{
					container.containingSkill.cancelOnServer((ServerPlayerData)executer, null);
				}
				container.isActivated = false;
				container.duration = 0;
			}
		}
	}
	
	public void cancelOnServer(ServerPlayerData executer, PacketBuffer args)
	{
		setCooldownSynchronize(executer, 0);
	}

	public void setCooldownSynchronize(ServerPlayerData executer, float amount)
	{
		setCooldownSynchronize(executer, this.slot, amount);
	}

	public void setDurationSynchronize(ServerPlayerData executer, int amount)
	{
		setDurationSynchronize(executer, this.slot, amount);
	}
	
	public void setDurationConsumeSynchronize(ServerPlayerData executer, boolean bool)
	{
		setDurationConsumeSynchronize(executer, this.slot, bool);
	}

	public static void setCooldownSynchronize(ServerPlayerData executer, SkillSlot slot, float amount)
	{
		if(amount > 0)
		{
			executer.getSkill(slot).setCooldown(amount);
		}
		else
		{
			executer.getSkill(slot).reset(!executer.getOriginalEntity().isCreative());
		}
		
		ModNetworkManager.sendToPlayer(new STCSetSkillValue(Target.COOLDOWN, slot.index, amount, false), executer.getOriginalEntity());
	}
	
	public static void setDurationSynchronize(ServerPlayerData executer, SkillSlot slot, int amount)
	{
		executer.getSkill(slot).setDuration(amount);
		ModNetworkManager.sendToPlayer(new STCSetSkillValue(Target.DURATION, slot.index, amount, false), executer.getOriginalEntity());
	}
	
	public static void setDurationConsumeSynchronize(ServerPlayerData executer, SkillSlot slot, boolean bool)
	{
		executer.getSkill(slot).setDurationConsume(bool);
		ModNetworkManager.sendToPlayer(new STCSetSkillValue(Target.DURATION_CONSUME, slot.index, 0, bool), executer.getOriginalEntity());
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

	public SkillSlot getSlot()
	{
		return this.slot;
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