package com.skullmangames.darksouls.common.entity;

import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCNPCChat;
import com.skullmangames.darksouls.network.server.gui.STCOpenFireKeeperScreen;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class AnastaciaOfAstora extends AbstractFireKeeper
{
	private static final String DIALOGUE_0 = "dialogue.darksouls.anastacia_of_astora.0";
	
	public AnastaciaOfAstora(EntityType<? extends QuestEntity> entity, Level level)
	{
		super(entity, level);
	}
	
	public static AttributeSupplier.Builder createAttributes()
	{
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 100.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.0D);
	}
	
	@Override
	public void onFinishChat(ServerPlayer player, String location)
	{
		switch(location)
		{
			case DIALOGUE_0:
				ModNetworkManager.sendToPlayer(new STCOpenFireKeeperScreen(this.getId()), player);
				break;
		}
	}
	
	@Override
	protected InteractionResult mobInteract(Player player, InteractionHand hand)
	{
		if (!player.level.isClientSide)
		{
			ModNetworkManager.sendToPlayer(new STCNPCChat(this.getId(), DIALOGUE_0), (ServerPlayer)player);
		}

		return InteractionResult.sidedSuccess(player.level.isClientSide);
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
