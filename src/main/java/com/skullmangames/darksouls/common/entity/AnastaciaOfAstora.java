package com.skullmangames.darksouls.common.entity;

import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCNPCChat;
import com.skullmangames.darksouls.network.server.gui.STCOpenFireKeeperScreen;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class AnastaciaOfAstora extends AbstractFireKeeper
{
	private static final String DIALOGUE_0 = "dialogue.darksouls.anastacia_of_astora.0";
	
	public AnastaciaOfAstora(EntityType<? extends QuestEntity> entity, World level)
	{
		super(entity, level);
	}
	
	public static AttributeModifierMap.MutableAttribute createAttributes()
	{
		return MobEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.MOVEMENT_SPEED, 0.0D);
	}
	
	@Override
	public void onFinishChat(ServerPlayerEntity player, String location)
	{
		switch(location)
		{
			case DIALOGUE_0:
				ModNetworkManager.sendToPlayer(new STCOpenFireKeeperScreen(this.getId()), player);
				break;
		}
	}
	
	@Override
	protected ActionResultType mobInteract(PlayerEntity player, Hand hand)
	{
		if (!player.level.isClientSide)
		{
			ModNetworkManager.sendToPlayer(new STCNPCChat(this.getId(), DIALOGUE_0), (ServerPlayerEntity)player);
		}

		return ActionResultType.sidedSuccess(player.level.isClientSide);
	}
	
	@Override
	protected int getExperienceReward(PlayerEntity p_21511_)
	{
		return 50;
	}
	
	@Override
	protected Item getEquipmentForSlot(EquipmentSlotType slot)
	{
		switch (slot)
		{
			default: return Items.AIR;
			case CHEST: return ModItems.DINGY_ROBE.get();
			case LEGS: return ModItems.BLOOD_STAINED_SKIRT.get();
		}
	}
}
