package com.skullmangames.darksouls.common.entity;

import java.util.UUID;

import com.skullmangames.darksouls.common.entity.ai.goal.WalkAroundBonfireGoal;
import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCNPCChat;
import com.skullmangames.darksouls.network.server.STCOpenFireKeeperScreen;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class FireKeeper extends AbstractFireKeeper
{
	private static final String DIALOGUE_0 = "dialogue.darksouls.fire_keeper.0";
	private static final String DIALOGUE_1 = "dialogue.darksouls.fire_keeper.1";
	private static final String DIALOGUE_1_ESTUS_SHARD = "dialogue.darksouls.fire_keeper.1.estus_shard";
	
	public FireKeeper(EntityType<? extends QuestEntity> entity, World level)
	{
		super(entity, level);
	}

	public static AttributeModifierMap.MutableAttribute createAttributes()
	{
		return MobEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.MOVEMENT_SPEED, 0.15D);
	}
	
	@Override
	protected void registerGoals()
	{
		this.goalSelector.addGoal(0, new WalkAroundBonfireGoal(this, 1.0D));
		this.goalSelector.addGoal(2, new LookAtGoal(this, PlayerEntity.class, 15.0F, 1.0F));
		this.goalSelector.addGoal(3, new LookRandomlyGoal(this));
	}
	
	@Override
	public void onFinishChat(ServerPlayerEntity player, String location)
	{
		UUID uuid = player.getUUID();
		switch(location)
		{
			case DIALOGUE_0:
				this.setQuestFlag(uuid, 0, true);
				break;
			case DIALOGUE_1:
				ModNetworkManager.sendToPlayer(new STCOpenFireKeeperScreen(this.getId()), player);
				break;
			case DIALOGUE_1_ESTUS_SHARD:
				ModNetworkManager.sendToPlayer(new STCOpenFireKeeperScreen(this.getId()), player);
				break;
		}
	}
	
	@Override
	protected ActionResultType mobInteract(PlayerEntity player, Hand hand)
	{
		if (!player.level.isClientSide)
		{
			if (!this.getQuestFlag(player.getUUID(), 0))
			{
				ModNetworkManager.sendToPlayer(new STCNPCChat(this.getId(), DIALOGUE_0), (ServerPlayerEntity)player);
			}
			else
			{
				if (player.inventory.contains(new ItemStack(ModItems.ESTUS_SHARD.get())))
				{
					ModNetworkManager.sendToPlayer(new STCNPCChat(this.getId(), DIALOGUE_1_ESTUS_SHARD), (ServerPlayerEntity)player);
				}
				else
				{
					ModNetworkManager.sendToPlayer(new STCNPCChat(this.getId(), DIALOGUE_1), (ServerPlayerEntity)player);
				}
			}
		}

		return ActionResultType.sidedSuccess(player.level.isClientSide);
	}
}
