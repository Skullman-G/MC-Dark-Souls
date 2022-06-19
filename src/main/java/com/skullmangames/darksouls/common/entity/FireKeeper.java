package com.skullmangames.darksouls.common.entity;

import java.util.UUID;

import com.skullmangames.darksouls.common.entity.ai.goal.WalkAroundBonfireGoal;
import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCNPCChat;
import com.skullmangames.darksouls.network.server.STCOpenFireKeeperScreen;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FireKeeper extends AbstractFireKeeper
{
	private static final String DIALOGUE_0 = "dialogue.darksouls.fire_keeper.0";
	private static final String DIALOGUE_1 = "dialogue.darksouls.fire_keeper.1";
	private static final String DIALOGUE_1_ESTUS_SHARD = "dialogue.darksouls.fire_keeper.1.estus_shard";
	
	public FireKeeper(EntityType<? extends QuestEntity> entity, Level level)
	{
		super(entity, level);
	}

	public static AttributeSupplier.Builder createAttributes()
	{
		return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.MOVEMENT_SPEED, 0.15D);
	}
	
	@Override
	protected void registerGoals()
	{
		this.goalSelector.addGoal(0, new WalkAroundBonfireGoal(this, 1.0D));
		this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 15.0F, 1.0F));
		this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
	}
	
	@Override
	public void onFinishChat(ServerPlayer player, String location)
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
	protected InteractionResult mobInteract(Player player, InteractionHand hand)
	{
		if (!player.level.isClientSide)
		{
			if (!this.getQuestFlag(player.getUUID(), 0))
			{
				ModNetworkManager.sendToPlayer(new STCNPCChat(this.getId(), DIALOGUE_0), (ServerPlayer)player);
			}
			else
			{
				if (player.getInventory().contains(new ItemStack(ModItems.ESTUS_SHARD.get())))
				{
					ModNetworkManager.sendToPlayer(new STCNPCChat(this.getId(), DIALOGUE_1_ESTUS_SHARD), (ServerPlayer)player);
				}
				else
				{
					ModNetworkManager.sendToPlayer(new STCNPCChat(this.getId(), DIALOGUE_1), (ServerPlayer)player);
				}
			}
		}

		return InteractionResult.sidedSuccess(player.level.isClientSide);
	}
}
