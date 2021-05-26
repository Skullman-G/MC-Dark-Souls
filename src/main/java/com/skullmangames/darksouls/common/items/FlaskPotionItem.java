package com.skullmangames.darksouls.common.items;

import java.util.List;

import com.skullmangames.darksouls.core.init.EffectInit;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.stats.Stats;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class FlaskPotionItem extends Item
{
	private int uses;
	private int totaluses;
	
	public FlaskPotionItem(Item.Properties properties) 
	{
		super(properties);
		this.uses = 5;
		this.totaluses = 5;
	}
	
	/*@Override
	public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) 
	{
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		tooltip.add(new TranslationTextComponent("tooltip.estus_flask" + uses + "/" + totaluses));
	}*/
	
	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, World worldIn, LivingEntity livingentity)
	{
	      PlayerEntity playerentity = livingentity instanceof PlayerEntity ? (PlayerEntity)livingentity : null;
	      if (playerentity instanceof ServerPlayerEntity)
	      {
	         CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayerEntity)playerentity, itemstack);
	      }

	      if (!worldIn.isClientSide)
	      {
	         for(EffectInstance effectinstance : PotionUtils.getMobEffects(itemstack))
	         {
	            if (effectinstance.getEffect().isInstantenous())
	            {
	               effectinstance.getEffect().applyInstantenousEffect(playerentity, playerentity, livingentity, effectinstance.getAmplifier(), 1.0D);
	            }
	            else
	            {
	               livingentity.addEffect(new EffectInstance(effectinstance));
	            }
	         }
	      }

	      if (playerentity != null)
	      {
	         playerentity.awardStat(Stats.ITEM_USED.get(this));
	         if (!playerentity.abilities.instabuild)
	         {
	            itemstack.shrink(1);
	         }
	      }

	      if (playerentity == null || !playerentity.abilities.instabuild)
	      {
	         if (itemstack.isEmpty())
	         {
	            return new ItemStack(Items.GLASS_BOTTLE);
	         }

	         if (playerentity != null)
	         {
	            playerentity.inventory.add(new ItemStack(Items.GLASS_BOTTLE));
	         }
	      }

	      return itemstack;
	   }
}
