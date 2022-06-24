package com.skullmangames.darksouls.common.item;

import java.util.List;

import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.client.input.ModKeys;
import com.skullmangames.darksouls.common.block.BonfireBlock;
import com.skullmangames.darksouls.core.init.ModItems;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EstusFlaskItem extends Item
{
	public EstusFlaskItem(Item.Properties properties)
	{
		super(properties.stacksTo(1));
	}
	
	private static CompoundTag getOrCreateNBT(ItemStack itemstack)
	{
		CompoundTag compoundnbt = itemstack.getTag();
		if (compoundnbt == null)
		{
			compoundnbt = itemstack.getOrCreateTag();
			compoundnbt.putInt("TotalUses", 1);
		    compoundnbt.putInt("Uses", compoundnbt.getInt("TotalUses"));
		    compoundnbt.putInt("Heal", 5);
		}
		
		return compoundnbt;
	}
	
	public static int getHeal(ItemStack itemstack)
	{
		CompoundTag compoundnbt = getOrCreateNBT(itemstack);
		return compoundnbt.getInt("Heal");
	}
	
	public static void setHeal(ItemStack itemstack, int heallevel)
	{
		CompoundTag compoundnbt = getOrCreateNBT(itemstack);
		compoundnbt.putInt("Heal", 5 + heallevel - 1);
	}
	
	public static int getTotalUses(ItemStack itemstack)
	{
		CompoundTag compoundnbt = getOrCreateNBT(itemstack);
		return compoundnbt.getInt("TotalUses");
	}
	
	public static void setTotalUses(ItemStack itemstack, int value)
	{
	    if (value <= 20)
	    {
			CompoundTag compoundnbt = getOrCreateNBT(itemstack);
		    compoundnbt.putInt("TotalUses", value);
	    }
	}
	
	public static int getUses(ItemStack itemstack)
	{
	    CompoundTag compoundnbt = getOrCreateNBT(itemstack);
	    return compoundnbt.getInt("Uses");
	}

	public static void setUses(ItemStack itemstack, int value)
	{
	    if (value == getUses(itemstack)) return;
		if (value < 0) value = 0;
	    if (value > getTotalUses(itemstack)) value = getTotalUses(itemstack);
		CompoundTag compoundnbt = getOrCreateNBT(itemstack);
	    compoundnbt.putInt("Uses", value);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
	{
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		if (ClientManager.INSTANCE == null) return;
		if (!ClientManager.INSTANCE.inputManager.isKeyDown(ModKeys.SHOW_ITEM_INFO)) tooltip.add(new TextComponent("\n\u00A77Uses: " + getUses(stack) + "/" + getTotalUses(stack)));
	}
	
	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level worldIn, LivingEntity livingentity)
	{
	      Player player = livingentity instanceof Player ? (Player)livingentity : null;
	      if (player instanceof ServerPlayer)
	      {
	         CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)player, itemstack);
	      }

	      if (player != null) // Player
	      {
	    	  if (!worldIn.isClientSide)
		      {
		    	  if (player.getInventory().contains(new ItemStack(ModItems.DARKSIGN.get())))
		    	  {
		    		  this.activate(player, itemstack);
		    	  }
		    	  else
		    	  {
		    		  player.hurt(DamageSource.IN_FIRE, getHeal(itemstack));
		    	  }
		      }
	    	  player.awardStat(Stats.ITEM_USED.get(this));
	    	  if (!player.getAbilities().instabuild)
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
	public UseAnim getUseAnimation(ItemStack itemstack)
	{
		return UseAnim.DRINK;
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player player, InteractionHand hand)
	{
		ItemStack itemstack = player.getItemInHand(hand);
		if (getUses(itemstack) > 0)
		{
			return ItemUtils.startUsingInstantly(worldIn, player, hand);
		}
		else
		{
			return InteractionResultHolder.pass(itemstack);
		}
	}
	
	@Override
	public InteractionResult useOn(UseOnContext itemusecontext)
	{
		Level level = itemusecontext.getLevel();
		BlockPos blockpos = itemusecontext.getClickedPos();
		BlockState blockstate = level.getBlockState(blockpos);
		if (blockstate.getBlock() instanceof BonfireBlock && blockstate.getValue(BonfireBlock.LIT))
		{
			setUses(itemusecontext.getItemInHand(), blockstate.getValue(BonfireBlock.ESTUS_VOLUME_LEVEL) * 5);
			setHeal(itemusecontext.getItemInHand(), blockstate.getValue(BonfireBlock.ESTUS_HEAL_LEVEL));
			return InteractionResult.SUCCESS;
		}
		
		return InteractionResult.PASS;
	}
	
	@Override
	public boolean isFoil(ItemStack itemstack)
	{
		return super.isFoil(itemstack) || !PotionUtils.getMobEffects(itemstack).isEmpty();
	}
}
