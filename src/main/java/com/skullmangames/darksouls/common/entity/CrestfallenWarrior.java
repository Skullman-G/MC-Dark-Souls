package com.skullmangames.darksouls.common.entity;

import com.skullmangames.darksouls.common.capability.entity.PlayerData;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.play.IModClientPlayNetHandler;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class CrestfallenWarrior extends QuestEntity
{
	public CrestfallenWarrior(EntityType<? extends PathfinderMob> type, Level level)
	{
		super(type, level);
		this.questFlags = new boolean[2];
	}
	
	@Override
	protected InteractionResult mobInteract(Player player, InteractionHand hand)
	{
		IModClientPlayNetHandler handler = ModNetworkManager.connection;
		if (player.level.isClientSide && handler != null && !this.chatTimer.isTicking())
		{
			PlayerData<?> playerdata = (PlayerData<?>)player.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
			if (!this.questFlags[0])
			{
				String[] sentences = new TranslatableComponent("dialogue.darksouls.crestfallen_warrior.0").getString().split("%");
				this.chatTimer.start(60, sentences);
				this.questFlags[0] = true;
			}
			else if (!this.questFlags[1] && !playerdata.isHuman())
			{
				String[] sentences = new TranslatableComponent("dialogue.darksouls.crestfallen_warrior.1").getString().split("%");
				this.chatTimer.start(60, sentences);
				this.questFlags[1] = true;
			}
			else
			{
				String[] sentences = new TranslatableComponent("dialogue.darksouls.crestfallen_warrior.2").getString().split("%");
				this.chatTimer.start(60, sentences);
			}
		}

		return InteractionResult.sidedSuccess(player.level.isClientSide);
	}
	
	@Override
	protected int getExperienceReward(Player p_21511_)
	{
		return 1000;
	}
	
	@Override
	protected Item getEquipmentForSlot(EquipmentSlot slot)
	{
		switch (slot)
		{
			default: return Items.AIR;
			case MAINHAND: return Items.IRON_SWORD;
			case OFFHAND: return ModItems.HEATER_SHIELD.get();
			
			case CHEST: return Items.CHAINMAIL_CHESTPLATE;
			case LEGS: return Items.CHAINMAIL_LEGGINGS;
			case FEET: return Items.CHAINMAIL_BOOTS;
		}
	}
}
