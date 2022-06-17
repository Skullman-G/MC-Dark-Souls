package com.skullmangames.darksouls.common.entity;

import com.skullmangames.darksouls.common.entity.ai.goal.WalkAroundBonfireGoal;
import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.network.ModNetworkManager;

import net.minecraft.network.chat.TranslatableComponent;
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
	protected InteractionResult mobInteract(Player player, InteractionHand hand)
	{
		if (player.level.isClientSide && !this.chatTimer.isTicking())
		{
			if (!this.questFlags[0])
			{
				String[] sentences = new TranslatableComponent("dialogue.darksouls.fire_keeper.0").getString().split("%");
				this.chatTimer.start(60, (time) -> this.questFlags[0] = true, sentences);
			}
			else
			{
				String[] sentences;
				if (player.getInventory().contains(new ItemStack(ModItems.ESTUS_SHARD.get())))
				{
					sentences = new TranslatableComponent("dialogue.darksouls.fire_keeper.1.estus_shard").getString().split("%");
				}
				else
				{
					sentences = new TranslatableComponent("dialogue.darksouls.fire_keeper.1").getString().split("%");
				}
				
				this.chatTimer.start(60, (time) -> ModNetworkManager.connection.openFireKeeperScreen(this.getId()), sentences);
			}
		}

		return InteractionResult.sidedSuccess(player.level.isClientSide);
	}
}
