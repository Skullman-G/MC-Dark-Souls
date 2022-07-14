package com.skullmangames.darksouls.common.entity;

import java.util.UUID;

import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCNPCChat;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class CrestfallenWarrior extends QuestEntity
{
	private static final String DIALOGUE_0 = "dialogue.darksouls.crestfallen_warrior.0";
	private static final String DIALOGUE_1 = "dialogue.darksouls.crestfallen_warrior.1";
	private static final String DIALOGUE_2 = "dialogue.darksouls.crestfallen_warrior.2";
	
	public CrestfallenWarrior(EntityType<? extends CreatureEntity> type, World level)
	{
		super(type, level);
	}
	
	@Override
	protected ActionResultType mobInteract(PlayerEntity player, Hand hand)
	{
		if (!player.level.isClientSide && this.getTarget() == null)
		{
			PlayerCap<?> playerCap = (PlayerCap<?>)player.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
			if (!this.getQuestFlag(player.getUUID(), 0))
			{
				ModNetworkManager.sendToPlayer(new STCNPCChat(this.getId(), DIALOGUE_0), (ServerPlayerEntity)player);
			}
			else if (!this.getQuestFlag(player.getUUID(), 1) && !playerCap.isHuman())
			{
				ModNetworkManager.sendToPlayer(new STCNPCChat(this.getId(), DIALOGUE_1), (ServerPlayerEntity)player);
			}
			else
			{
				ModNetworkManager.sendToPlayer(new STCNPCChat(this.getId(), DIALOGUE_2), (ServerPlayerEntity)player);
			}
		}

		return ActionResultType.sidedSuccess(player.level.isClientSide);
	}
	
	@Override
	public void onFinishChat(ServerPlayerEntity player, String location)
	{
		UUID uuid = player.getUUID();
		switch (location)
		{
			case DIALOGUE_0:
				this.setQuestFlag(uuid, 0, true);
				break;
			case DIALOGUE_1:
				this.setQuestFlag(uuid, 1, true);
				break;
		}
	}
	
	@Override
	protected int getExperienceReward(PlayerEntity p_21511_)
	{
		return 1000;
	}
	
	@Override
	protected Item getEquipmentForSlot(EquipmentSlotType slot)
	{
		switch (slot)
		{
			default: return Items.AIR;
			case MAINHAND: return ModItems.LONGSWORD.get();
			case OFFHAND: return ModItems.HEATER_SHIELD.get();
			
			case CHEST: return Items.CHAINMAIL_CHESTPLATE;
			case LEGS: return Items.CHAINMAIL_LEGGINGS;
			case FEET: return Items.CHAINMAIL_BOOTS;
		}
	}
}
