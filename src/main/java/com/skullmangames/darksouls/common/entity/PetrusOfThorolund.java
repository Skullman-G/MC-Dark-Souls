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

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class PetrusOfThorolund extends QuestEntity implements SoulMerchant
{
	private static final String DIALOGUE_0 = "dialogue.darksouls.petrus_of_thorolund.0";
	private static final String DIALOGUE_1 = "dialogue.darksouls.petrus_of_thorolund.1";
	private static final String DIALOGUE_2 = "dialogue.darksouls.petrus_of_thorolund.2";
	
	private Player tradingPlayer;
	private SoulMerchantOffers offers;
	
	public PetrusOfThorolund(EntityType<? extends PetrusOfThorolund> type, Level level)
	{
		super(type, level);
	}
	
	@Override
	protected void registerGoals()
	{
		this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 15.0F, 1.0F));
		this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
		MobCap<?> cap = (MobCap<?>)this.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
		if (cap != null)
		{
			this.targetSelector.addGoal(0, new SpellAttackGoal(cap, ModItems.THOROLUND_TALISMAN.get().getDefaultInstance(),
					new SpellAttackInstance(Animations.BIPED_CAST_MIRACLE_FORCE.get(), (mob) -> 
			{
				LivingEntity target = mob.getTarget();
				return target != null && mob.getOriginalEntity().distanceTo(target) < 2;
			}), new SpellAttackInstance(Animations.BIPED_CAST_MIRACLE_HEAL.get(), (mob) -> 
			{
				LivingEntity target = mob.getTarget();
				float per = mob.getOriginalEntity().getHealth() / mob.getOriginalEntity().getMaxHealth();
				return target != null && mob.getOriginalEntity().distanceTo(target) > 10 && per < 0.5F;
			})));
		}
	}
	
	@Override
	protected InteractionResult mobInteract(Player player, InteractionHand hand)
	{
		if (!player.level.isClientSide && this.getTarget() == null)
		{
			if (!this.getQuestFlag(player.getUUID(), 0))
			{
				ModNetworkManager.sendToPlayer(new STCNPCChat(this.getId(), DIALOGUE_0), (ServerPlayer)player);
			}
			else if (!this.getQuestFlag(player.getUUID(), 1))
			{
				ModNetworkManager.sendToPlayer(new STCNPCChat(this.getId(), DIALOGUE_1), (ServerPlayer)player);
			}
			else if (!this.getQuestFlag(player.getUUID(), 2))
			{
				ModNetworkManager.sendToPlayer(new STCNPCChat(this.getId(), DIALOGUE_2), (ServerPlayer)player);
			}
			else if (!this.getOffers().isEmpty())
			{
				this.startTrading(player);
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
			case MAINHAND: return ModItems.MACE.get();
			case OFFHAND: return ModItems.KNIGHT_SHIELD.get();
			case CHEST: return ModItems.ELITE_CLERIC_ARMOR.get();
			case LEGS: return ModItems.ELITE_CLERIC_LEGGINGS.get();
			case FEET: return Items.IRON_BOOTS;
		}
	}
	
	private void startTrading(Player player)
	{
		this.setTradingPlayer(player);
		this.openTradingScreen(player, this.getDisplayName());
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
				this.spawnAtLocation(Items.COPPER_INGOT);
				break;
			case DIALOGUE_2:
				this.setQuestFlag(uuid, 2, true);
				break;
		}
	}
	
	@Override
	protected int getExperienceReward(Player player)
	{
		return 1000;
	}

	@Override
	public Player getTradingPlayer()
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
	public void setTradingPlayer(Player player)
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
