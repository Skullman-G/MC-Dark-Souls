package com.skullmangames.darksouls.common.item;

import java.util.UUID;

import com.skullmangames.darksouls.common.capability.entity.PlayerData;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.init.ModSoundEvents;

import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.sounds.SoundEvent;

public class Teleport2BonfireItem extends Item implements IHaveDarkSoulsUseAction
{
	private static final UUID SPEED_MODIFIER_CASTING_UUID = UUID.fromString("7b6eb570-5411-4c74-9b82-ce52e68c5ac5");
	private final boolean looseAfterUse;
	private final boolean binding;
	private final DarkSoulsUseAction useAction;
	private final boolean looseSouls;
	
	public Teleport2BonfireItem(DarkSoulsUseAction useaction, boolean binding, boolean looseafteruse, boolean loosesouls, Properties properties)
	{
		super(properties);
		this.looseAfterUse = looseafteruse;
		this.binding = binding;
		this.useAction = useaction;
		this.looseSouls = loosesouls;
	}
	
	@Override
	public boolean onDroppedByPlayer(ItemStack item, Player player)
	{
		if (this.binding)
		{
			player.addItem(item);
			return false;
		}
		else
		{
			return super.onDroppedByPlayer(item, player);
		}
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
	{
		AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
		AttributeModifier speedmodifier = new AttributeModifier(SPEED_MODIFIER_CASTING_UUID, "item use speed reduction", 0, Operation.MULTIPLY_TOTAL);
		if (speed.getModifier(SPEED_MODIFIER_CASTING_UUID) == null) speed.addTransientModifier(speedmodifier);
		return ItemUser.startUsing(this, level, player, hand);
	}
	
	@Override
	public void releaseUsing(ItemStack p_77615_1_, Level p_77615_2_, LivingEntity livingentity, int p_77615_4_)
	{
		AttributeInstance speed = livingentity.getAttribute(Attributes.MOVEMENT_SPEED);
		if (speed.getModifier(SPEED_MODIFIER_CASTING_UUID) != null) speed.removeModifier(SPEED_MODIFIER_CASTING_UUID);
	}
	
	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level level, LivingEntity livingentity)
	{
		AttributeInstance speed = livingentity.getAttribute(Attributes.MOVEMENT_SPEED);
		if (speed.getModifier(SPEED_MODIFIER_CASTING_UUID) != null) speed.removeModifier(SPEED_MODIFIER_CASTING_UUID);
		Player playerentity = livingentity instanceof Player ? (Player)livingentity : null;
	    if (!livingentity.level.isClientSide)
	    {
	    	ServerPlayer serverplayerentity = (ServerPlayer)livingentity;
	    	PlayerData<?> playerdata = (PlayerData<?>)serverplayerentity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
	    	CriteriaTriggers.CONSUME_ITEM.trigger(serverplayerentity, itemstack);
	    	
	    	if (serverplayerentity.getRespawnPosition() != null)
	    	{
	    		if (this.looseSouls)
	    		{
	    			playerdata.setHumanity(0);
	    			playerdata.setSouls(0);
	    		}
	    		serverplayerentity.teleportTo(serverplayerentity.getRespawnPosition().getX(), serverplayerentity.getRespawnPosition().getY(), serverplayerentity.getRespawnPosition().getZ());
	    	}
	    	else
	    	{
	    		serverplayerentity.sendMessage(new TranslatableComponent("gui.darksouls.darksign_didnt_work"), Util.NIL_UUID);
	    	}
		}

	    if (playerentity != null)
	    {
	    	playerentity.awardStat(Stats.ITEM_USED.get(this));
	    }
	    
	    if (this.looseAfterUse && (playerentity == null || !playerentity.getAbilities().instabuild))
	    {
	    	itemstack.shrink(1);
	    }

	    return itemstack;
	}
	
	@Override
	public int getUseDuration(ItemStack itemstack)
	{
		return 32;
	}
	
	@Override
	public void onUseTick(Level level, LivingEntity livingentity, ItemStack itemstack, int durationremaining)
	{
		ItemUser.triggerItemUseEffects(livingentity, itemstack, this, durationremaining);
	}

	@Override
	public DarkSoulsUseAction getDarkSoulsUseAnimation()
	{
		return this.useAction;
	}

	@Override
	public SoundEvent getUseSound()
	{
		return ModSoundEvents.DARKSIGN_USE.get();
	}
}
