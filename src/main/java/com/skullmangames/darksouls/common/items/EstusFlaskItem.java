package com.skullmangames.darksouls.common.items;

import java.util.List;

import com.skullmangames.darksouls.common.blocks.BonfireBlock;
import com.skullmangames.darksouls.core.init.ItemInit;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.PotionUtils;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.DrinkHelper;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EstusFlaskItem extends Item
{
	public EstusFlaskItem(Item.Properties properties)
	{
		super(properties.stacksTo(1));
	}
	
	public static int getTotalUses(ItemStack itemstack)
	{
		CompoundNBT compoundnbt = itemstack.getTag();
		if (compoundnbt == null)
	    {
			compoundnbt = itemstack.getOrCreateTag();
			compoundnbt.putInt("TotalUses", 1);
		    compoundnbt.putInt("Uses", compoundnbt.getInt("TotalUses"));
	    }
		
		return compoundnbt.getInt("TotalUses");
	}
	
	public static void setTotalUses(ItemStack itemstack, int value)
	{
	    CompoundNBT compoundnbt = itemstack.getOrCreateTag();
	    compoundnbt.putInt("TotalUses", value);
	}
	
	public static int getUses(ItemStack itemstack)
	{
	    CompoundNBT compoundnbt = itemstack.getTag();
	    if (compoundnbt == null)
	    {
	    	return getTotalUses(itemstack);
	    }
	    else
	    {
	    	return compoundnbt.getInt("Uses");
	    }
	}

	public static void setUses(ItemStack itemstack, int value)
	{
	    if (value < 0)
	    {
	    	value = 0;
	    }
		CompoundNBT compoundnbt = itemstack.getOrCreateTag();
	    compoundnbt.putInt("Uses", value);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
	{
		String description = new TranslationTextComponent("tooltip.darksouls.estus_flask").getString();
		tooltip.add(new StringTextComponent("\u00A77" + description + "\nUses: " + getUses(stack) + "/" + getTotalUses(stack)));
	}
	
	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, World worldIn, LivingEntity livingentity)
	{
	      PlayerEntity playerentity = livingentity instanceof PlayerEntity ? (PlayerEntity)livingentity : null;
	      if (playerentity instanceof ServerPlayerEntity)
	      {
	         CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayerEntity)playerentity, itemstack);
	      }

	      if (playerentity != null)
	      {
	    	  if (!worldIn.isClientSide)
		      {
		    	  if (playerentity.inventory.contains(new ItemStack(ItemInit.DARKSIGN.get())))
		    	  {
		    		  playerentity.heal(10.0F);
		    	  }
		    	  else
		    	  {
		    		  playerentity.hurt(DamageSource.IN_FIRE, 10.0F);
		    	  }
		      }
	    	  playerentity.awardStat(Stats.ITEM_USED.get(this));
	    	  if (!playerentity.abilities.instabuild)
	    	  {
	    		  setUses(itemstack, getUses(itemstack) - 1);
	    	  }
	      }

	      return itemstack;
	}
	
	@Override
	public int getUseDuration(ItemStack itemstack)
	{
		return 32;
	}
	
	@Override
	public UseAction getUseAnimation(ItemStack itemstack)
	{
		return UseAction.DRINK;
	}
	
	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity player, Hand hand)
	{
		ItemStack itemstack = player.getItemInHand(hand);
		if (getUses(itemstack) > 0)
		{
			return DrinkHelper.useDrink(worldIn, player, hand);
		}
		else
		{
			return ActionResult.pass(itemstack);
		}
	}
	
	@Override
	public ActionResultType useOn(ItemUseContext itemusecontext)
	{
		World world = itemusecontext.getLevel();
		BlockPos blockpos = itemusecontext.getClickedPos();
		BlockState blockstate = world.getBlockState(blockpos);
		if (blockstate.getBlock() instanceof BonfireBlock && blockstate.getValue(BonfireBlock.LIT))
		{
			setUses(itemusecontext.getItemInHand(), getTotalUses(itemusecontext.getItemInHand()));
			return ActionResultType.SUCCESS;
		}
		
		return ActionResultType.PASS;
	}
	
	@Override
	public boolean isFoil(ItemStack itemstack)
	{
		return super.isFoil(itemstack) || !PotionUtils.getMobEffects(itemstack).isEmpty();
	}
}
