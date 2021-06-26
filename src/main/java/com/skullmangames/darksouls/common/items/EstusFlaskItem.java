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
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EstusFlaskItem extends DescriptionItem
{
	public EstusFlaskItem(Item.Properties properties)
	{
		super(properties.stacksTo(1));
	}
	
	private static CompoundNBT getOrCreateNBT(ItemStack itemstack)
	{
		CompoundNBT compoundnbt = itemstack.getTag();
		if (compoundnbt == null)
		{
			compoundnbt = itemstack.getOrCreateTag();
			compoundnbt.putInt("TotalUses", 1);
		    compoundnbt.putInt("Uses", compoundnbt.getInt("TotalUses"));
		    compoundnbt.putDouble("HealMultiplier", 1);
		}
		
		return compoundnbt;
	}
	
	public static double getHealMultiplier(ItemStack itemstack)
	{
		CompoundNBT compoundnbt = getOrCreateNBT(itemstack);
		return compoundnbt.getDouble("HealMultiplier");
	}
	
	public static void setHealMultiplier(ItemStack itemstack, double value)
	{
		CompoundNBT compoundnbt = getOrCreateNBT(itemstack);
		compoundnbt.putDouble("HealMultiplier", value);
	}
	
	public static int getTotalUses(ItemStack itemstack)
	{
		CompoundNBT compoundnbt = getOrCreateNBT(itemstack);
		return compoundnbt.getInt("TotalUses");
	}
	
	public static void setTotalUses(ItemStack itemstack, int value)
	{
	    CompoundNBT compoundnbt = getOrCreateNBT(itemstack);
	    compoundnbt.putInt("TotalUses", value);
	}
	
	public static int getUses(ItemStack itemstack)
	{
	    CompoundNBT compoundnbt = getOrCreateNBT(itemstack);
	    return compoundnbt.getInt("Uses");
	}

	public static void setUses(ItemStack itemstack, int value)
	{
	    if (value < getUses(itemstack))
	    {
	    	value = getUses(itemstack);
	    }
	    else if (value < 0)
	    {
	    	value = 0;
	    }
	    else if (value > getTotalUses(itemstack))
	    {
	    	value = getTotalUses(itemstack);
	    }
		CompoundNBT compoundnbt = getOrCreateNBT(itemstack);
	    compoundnbt.putInt("Uses", value);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
	{
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		tooltip.add(new StringTextComponent("\n\u00A77Uses: " + getUses(stack) + "/" + getTotalUses(stack)));
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
			setUses(itemusecontext.getItemInHand(), blockstate.getValue(BonfireBlock.FIRE_LEVEL) * 5);
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
