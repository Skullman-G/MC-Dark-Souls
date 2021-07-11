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
		    compoundnbt.putInt("Heal", 250);
		}
		
		return compoundnbt;
	}
	
	public static int getHeal(ItemStack itemstack)
	{
		CompoundNBT compoundnbt = getOrCreateNBT(itemstack);
		return compoundnbt.getInt("Heal");
	}
	
	public static void setHeal(ItemStack itemstack, int heallevel)
	{
		CompoundNBT compoundnbt = getOrCreateNBT(itemstack);
		
		switch (heallevel)
		{
		case 1:
			compoundnbt.putInt("Heal", 250);
		case 2:
			compoundnbt.putInt("Heal", 335);
		case 3:
			compoundnbt.putInt("Heal", 410);
		case 4:
			compoundnbt.putInt("Heal", 470);
		case 5:
			compoundnbt.putInt("Heal", 515);
		case 6:
			compoundnbt.putInt("Heal", 535);
		case 7:
			compoundnbt.putInt("Heal", 550);
		case 8:
			compoundnbt.putInt("Heal", 565);
		case 9:
			compoundnbt.putInt("Heal", 580);
		case 10:
			compoundnbt.putInt("Heal", 590);
		}
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
	    if (value == getUses(itemstack))
	    {
	    	return;
	    }
		if (value < 0)
	    {
	    	value = 0;
	    }
	    if (value > getTotalUses(itemstack))
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
		    		  playerentity.heal(getHeal(itemstack));
		    	  }
		    	  else
		    	  {
		    		  playerentity.hurt(DamageSource.IN_FIRE, getHeal(itemstack));
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
			setUses(itemusecontext.getItemInHand(), blockstate.getValue(BonfireBlock.ESTUS_VOLUME_LEVEL) * 5);
			setHeal(itemusecontext.getItemInHand(), blockstate.getValue(BonfireBlock.ESTUS_HEAL_LEVEL));
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
