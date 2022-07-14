package com.skullmangames.darksouls.common.entity;

import java.util.UUID;

import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCNPCChat;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class CrestfallenWarrior extends QuestEntity
{
	private static final String DIALOGUE_0 = "dialogue.darksouls.crestfallen_warrior.0";
	private static final String DIALOGUE_1 = "dialogue.darksouls.crestfallen_warrior.1";
	private static final String DIALOGUE_2 = "dialogue.darksouls.crestfallen_warrior.2";
	
	public CrestfallenWarrior(EntityType<? extends PathfinderMob> type, Level level)
	{
		super(type, level);
	}
	
	@Override
	protected void registerGoals()
	{
		this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 15.0F, 1.0F));
		this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
	}
	
	@Override
	protected InteractionResult mobInteract(Player player, InteractionHand hand)
	{
		if (!player.level.isClientSide && this.getTarget() == null)
		{
			PlayerCap<?> playerCap = (PlayerCap<?>)player.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
			if (!this.getQuestFlag(player.getUUID(), 0))
			{
				ModNetworkManager.sendToPlayer(new STCNPCChat(this.getId(), DIALOGUE_0), (ServerPlayer)player);
			}
			else if (!this.getQuestFlag(player.getUUID(), 1) && !playerCap.isHuman())
			{
				ModNetworkManager.sendToPlayer(new STCNPCChat(this.getId(), DIALOGUE_1), (ServerPlayer)player);
			}
			else
			{
				ModNetworkManager.sendToPlayer(new STCNPCChat(this.getId(), DIALOGUE_2), (ServerPlayer)player);
			}
		}

		return InteractionResult.sidedSuccess(player.level.isClientSide);
	}
	
	@Override
	public void onFinishChat(ServerPlayer player, String location)
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
			case MAINHAND: return ModItems.LONGSWORD.get();
			case OFFHAND: return ModItems.HEATER_SHIELD.get();
			
			case CHEST: return Items.CHAINMAIL_CHESTPLATE;
			case LEGS: return Items.CHAINMAIL_LEGGINGS;
			case FEET: return Items.CHAINMAIL_BOOTS;
		}
	}
}
