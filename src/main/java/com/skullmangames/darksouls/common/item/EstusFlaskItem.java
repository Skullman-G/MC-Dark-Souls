package com.skullmangames.darksouls.common.item;

import java.util.List;

import com.skullmangames.darksouls.common.block.BonfireBlock;
import com.skullmangames.darksouls.core.init.ModItems;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.DrinkHelper;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.stats.Stats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.UseAction;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EstusFlaskItem extends Item
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
		    compoundnbt.putInt("Uses", 0);
		    compoundnbt.putInt("Heal", 5);
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
		compoundnbt.putInt("Heal", 5 + heallevel - 1);
	}
	
	public static int getTotalUses(ItemStack itemstack)
	{
		CompoundNBT compoundnbt = getOrCreateNBT(itemstack);
		return compoundnbt.getInt("TotalUses");
	}
	
	public static void setTotalUses(ItemStack itemstack, int value)
	{
	    if (value <= 20)
	    {
			CompoundNBT compoundnbt = getOrCreateNBT(itemstack);
		    compoundnbt.putInt("TotalUses", value);
	    }
	}
	
	public static int getUses(ItemStack itemstack)
	{
	    CompoundNBT compoundnbt = getOrCreateNBT(itemstack);
	    return compoundnbt.getInt("Uses");
	}

	public static void setUses(ItemStack itemstack, int value)
	{
	    if (value == getUses(itemstack)) return;
		if (value < 0) value = 0;
	    if (value > getTotalUses(itemstack)) value = getTotalUses(itemstack);
		CompoundNBT compoundnbt = getOrCreateNBT(itemstack);
	    compoundnbt.putInt("Uses", value);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, World level, List<ITextComponent> tooltip, ITooltipFlag flagIn)
	{
		super.appendHoverText(stack, level, tooltip, flagIn);
		tooltip.add(new StringTextComponent("\n\u00A77Increase: " + getHeal(stack)));
		tooltip.add(new StringTextComponent("\u00A77Uses: " + getUses(stack) + "/" + getTotalUses(stack)));
	}
	
	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, World worldIn, LivingEntity livingentity)
	{
	      PlayerEntity player = livingentity instanceof PlayerEntity ? (PlayerEntity)livingentity : null;
	      if (player instanceof ServerPlayerEntity)
	      {
	         CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayerEntity)player, itemstack);
	      }

	      if (player != null) // PlayerEntity
	      {
	    	  if (!worldIn.isClientSide)
		      {
		    	  if (player.inventory.contains(new ItemStack(ModItems.DARKSIGN.get())))
		    	  {
		    		  this.activate(player, itemstack);
		    	  }
		    	  else
		    	  {
		    		  player.hurt(DamageSource.IN_FIRE, getHeal(itemstack));
		    	  }
		      }
	    	  player.awardStat(Stats.ITEM_USED.get(this));
	    	  if (!player.abilities.instabuild)
	    	  {
	    		  setUses(itemstack, getUses(itemstack) - 1);
	    	  }
	      }
	      else // LivingEntity
	      {
	    	  if (!worldIn.isClientSide)
		      {
	    		  this.activate(livingentity, itemstack);
		      }
	    	  setUses(itemstack, getUses(itemstack) - 1);
	      }

	      return itemstack;
	}
	
	protected void activate(LivingEntity entity, ItemStack stack)
	{
		entity.heal(getHeal(stack));
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
		World level = itemusecontext.getLevel();
		BlockPos blockpos = itemusecontext.getClickedPos();
		BlockState blockstate = level.getBlockState(blockpos);
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
