package com.skullmangames.darksouls.common.entity;

import java.util.UUID;

import com.skullmangames.darksouls.common.capability.entity.MobCap;
import com.skullmangames.darksouls.common.entity.ai.goal.SpellAttackGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.SpellAttackInstance;
import com.skullmangames.darksouls.common.inventory.SoulMerchantOffer;
import com.skullmangames.darksouls.common.inventory.SoulMerchantOffers;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCNPCChat;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class PetrusOfThorolund extends QuestEntity implements SoulMerchant
{
	private static final String DIALOGUE_0 = "dialogue.darksouls.petrus_of_thorolund.0";
	private static final String DIALOGUE_1 = "dialogue.darksouls.petrus_of_thorolund.1";
	private static final String DIALOGUE_2 = "dialogue.darksouls.petrus_of_thorolund.2";
	
	private PlayerEntity tradingPlayer;
	private SoulMerchantOffers offers;
	
	public PetrusOfThorolund(EntityType<? extends PetrusOfThorolund> type, World level)
	{
		super(type, level);
	}
	
	@Override
	protected void registerGoals()
	{
		this.goalSelector.addGoal(2, new LookAtGoal(this, PlayerEntity.class, 15.0F, 1.0F));
		this.goalSelector.addGoal(3, new LookRandomlyGoal(this));
		MobCap<?> cap = (MobCap<?>)this.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
		if (cap != null)
		{
			this.targetSelector.addGoal(0, new SpellAttackGoal(cap, ModItems.THOROLUND_TALISMAN.get().getDefaultInstance(), new SpellAttackInstance(Animations.BIPED_CAST_MIRACLE_FORCE, (mob) -> 
			{
				LivingEntity target = mob.getTarget();
				return target != null && mob.getOriginalEntity().distanceTo(target) < 2;
			}), new SpellAttackInstance(Animations.BIPED_CAST_MIRACLE_HEAL, (mob) -> 
			{
				LivingEntity target = mob.getTarget();
				float per = mob.getOriginalEntity().getHealth() / mob.getOriginalEntity().getMaxHealth();
				return target != null && mob.getOriginalEntity().distanceTo(target) > 10 && per < 0.5F;
			})));
		}
	}
	
	@Override
	protected ActionResultType mobInteract(PlayerEntity player, Hand hand)
	{
		if (!player.level.isClientSide && this.getTarget() == null)
		{
			if (!this.getQuestFlag(player.getUUID(), 0))
			{
				ModNetworkManager.sendToPlayer(new STCNPCChat(this.getId(), DIALOGUE_0), (ServerPlayerEntity)player);
			}
			else if (!this.getQuestFlag(player.getUUID(), 1))
			{
				ModNetworkManager.sendToPlayer(new STCNPCChat(this.getId(), DIALOGUE_1), (ServerPlayerEntity)player);
			}
			else if (!this.getQuestFlag(player.getUUID(), 2))
			{
				ModNetworkManager.sendToPlayer(new STCNPCChat(this.getId(), DIALOGUE_2), (ServerPlayerEntity)player);
			}
			else if (!this.getOffers().isEmpty())
			{
				this.startTrading(player);
			}
		}

		return ActionResultType.sidedSuccess(player.level.isClientSide);
	}
	
	@Override
	protected Item getEquipmentForSlot(EquipmentSlotType slot)
	{
		switch (slot)
		{
			default: return Items.AIR;	
			case MAINHAND: return ModItems.MACE.get();
			case OFFHAND: return ModItems.KNIGHT_SHIELD.get();
			case CHEST: return ModItems.ELITE_CLERIC_ARMOR.get();
			case LEGS: return ModItems.ELITE_CLERIC_LEGGINGS.get();
			case FEET: return Items.IRON_BOOTS;
		}
	}
	
	private void startTrading(PlayerEntity player)
	{
		this.setTradingPlayer(player);
		this.openTradingScreen(player, this.getDisplayName());
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
				this.spawnAtLocation(Items.GOLD_INGOT);
				break;
			case DIALOGUE_2:
				this.setQuestFlag(uuid, 2, true);
				break;
		}
	}
	
	@Override
	protected int getExperienceReward(PlayerEntity player)
	{
		return 1000;
	}

	@Override
	public PlayerEntity getTradingPlayer()
	{
		return this.tradingPlayer;
	}

	@Override
	public void notifyTrade(SoulMerchantOffer offer) {}

	@Override
	public boolean isClientSide()
	{
		return this.level.isClientSide;
	}

	@Override
	public SoundEvent getNotifyTradeSound()
	{
		return SoundEvents.VILLAGER_YES;
	}

	@Override
	public void setTradingPlayer(PlayerEntity player)
	{
		this.tradingPlayer = player;
	}

	@Override
	public void setOffers(SoulMerchantOffers value)
	{
		this.offers = value;
	}

	@Override
	public SoulMerchantOffers getOffers()
	{
		if (this.offers == null)
		{
			this.offers = new SoulMerchantOffers();
			this.offers.add(new SoulMerchantOffer(1000, ModItems.TALISMAN.get().getDefaultInstance()));
			this.offers.add(new SoulMerchantOffer(5000, ModItems.THOROLUND_TALISMAN.get().getDefaultInstance()));
			this.offers.add(new SoulMerchantOffer(4000, ModItems.MIRACLE_FORCE.get().getDefaultInstance()));
			this.offers.add(new SoulMerchantOffer(4000, ModItems.MIRACLE_HEAL.get().getDefaultInstance()));
			this.offers.add(new SoulMerchantOffer(1000, ModItems.MIRACLE_HEAL_AID.get().getDefaultInstance()));
			this.offers.add(new SoulMerchantOffer(8000, ModItems.MIRACLE_HOMEWARD.get().getDefaultInstance()));
		}

		return this.offers;
	}
}
