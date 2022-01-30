package com.skullmangames.darksouls.common.entity;

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
	}
	
	@Override
	public boolean canBeCollidedWith()
	{
		return true;
	}
	
	@Override
	protected InteractionResult mobInteract(Player player, InteractionHand hand)
	{
		IModClientPlayNetHandler handler = ModNetworkManager.connection;
		if (player.level.isClientSide && handler != null && !this.chatTimer.isTicking())
		{
			switch (this.getCurrentQuestPath())
			{
			case "0":
				String[] sentences = new TranslatableComponent("dialogue.darksouls.crestfallen_warrior.0").getString().split("%");
				this.chatTimer.start(60, sentences);
				break;
			}
		}

		return InteractionResult.sidedSuccess(player.level.isClientSide);
	}
	
	@Override
	protected Item getEquipmentForSlot(EquipmentSlot slot)
	{
		switch (slot)
		{
			default: return Items.AIR;
			case MAINHAND: return Items.IRON_SWORD;
			//case OFFHAND: return Items.SHIELD;
		}
	}
}
