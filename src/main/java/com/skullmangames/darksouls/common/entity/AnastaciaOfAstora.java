package com.skullmangames.darksouls.common.entity;

import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.network.ModNetworkManager;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class AnastaciaOfAstora extends FireKeeper
{
	public AnastaciaOfAstora(EntityType<? extends QuestEntity> entity, Level level)
	{
		super(entity, level);
	}
	
	@Override
	protected InteractionResult mobInteract(Player player, InteractionHand hand)
	{
		if (player.level.isClientSide && !this.chatTimer.isTicking())
		{
			String[] sentences = { "..." };
			this.chatTimer.start(60, (time) -> ModNetworkManager.connection.openFireKeeperScreen(this.getId()), sentences);
		}

		return InteractionResult.sidedSuccess(player.level.isClientSide);
	}
	
	@Override
	protected int getExperienceReward(Player p_21511_)
	{
		return 50;
	}
	
	@Override
	protected Item getEquipmentForSlot(EquipmentSlot slot)
	{
		switch (slot)
		{
			default: return Items.AIR;
			case CHEST: return ModItems.DINGY_ROBE.get();
			case LEGS: return ModItems.BLOOD_STAINED_SKIRT.get();
		}
	}
}
