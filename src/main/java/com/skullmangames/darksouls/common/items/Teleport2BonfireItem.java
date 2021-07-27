package com.skullmangames.darksouls.common.items;

import java.util.UUID;

import com.skullmangames.darksouls.common.entities.ModEntityDataManager;
import com.skullmangames.darksouls.core.init.SoundEventInit;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class Teleport2BonfireItem extends DescriptionItem implements IHaveDarkSoulsUseAction
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
	public boolean onDroppedByPlayer(ItemStack item, PlayerEntity player)
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
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand)
	{
		ModifiableAttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
		AttributeModifier speedmodifier = new AttributeModifier(SPEED_MODIFIER_CASTING_UUID, "item use speed reduction", -speed.getValue(), Operation.ADDITION);
		if (speed.getModifier(SPEED_MODIFIER_CASTING_UUID) == null) speed.addTransientModifier(speedmodifier);
		return ItemUser.startUsing(this, world, player, hand);
	}
	
	@Override
	public void releaseUsing(ItemStack p_77615_1_, World p_77615_2_, LivingEntity livingentity, int p_77615_4_)
	{
		ModifiableAttributeInstance speed = livingentity.getAttribute(Attributes.MOVEMENT_SPEED);
		if (speed.getModifier(SPEED_MODIFIER_CASTING_UUID) != null) speed.removeModifier(SPEED_MODIFIER_CASTING_UUID);
	}
	
	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, World world, LivingEntity livingentity)
	{
		ModifiableAttributeInstance speed = livingentity.getAttribute(Attributes.MOVEMENT_SPEED);
		if (speed.getModifier(SPEED_MODIFIER_CASTING_UUID) != null) speed.removeModifier(SPEED_MODIFIER_CASTING_UUID);
		
		PlayerEntity playerentity = livingentity instanceof PlayerEntity ? (PlayerEntity)livingentity : null;
	      
	    // SERVER SIDE
	    if (livingentity instanceof ServerPlayerEntity)
	    {
	    	ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)livingentity;
	    	CriteriaTriggers.CONSUME_ITEM.trigger(serverplayerentity, itemstack);
	    	
	    	if (serverplayerentity.getRespawnPosition() != null)
	    	{
	    		if (this.looseSouls)
	    		{
	    			ModEntityDataManager.setHumanity(livingentity, 0);
	    			ModEntityDataManager.setSouls(livingentity, 0);
	    		}
	    		serverplayerentity.teleportTo(serverplayerentity.getRespawnPosition().getX(), serverplayerentity.getRespawnPosition().getY(), serverplayerentity.getRespawnPosition().getZ());
	    	}
	    	else
	    	{
	    		serverplayerentity.sendMessage(new TranslationTextComponent("gui.darksouls.darksign_didnt_work"), Util.NIL_UUID);
	    	}
		}

	    if (playerentity != null)
	    {
	    	playerentity.awardStat(Stats.ITEM_USED.get(this));
	    }
	    
	    if (this.looseAfterUse && (playerentity == null || !playerentity.abilities.instabuild))
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
	public void onUseTick(World world, LivingEntity livingentity, ItemStack itemstack, int durationremaining)
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
		return SoundEventInit.DARKSIGN_USE.get();
	}
}
