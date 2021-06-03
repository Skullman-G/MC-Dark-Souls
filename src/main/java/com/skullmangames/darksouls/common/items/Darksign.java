package com.skullmangames.darksouls.common.items;

import java.util.List;

import com.skullmangames.darksouls.core.init.SoundEventInit;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Darksign extends Item
{
	public Darksign(Properties properties)
	{
		super(properties.stacksTo(1));
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
	{
		String description = new TranslationTextComponent("tooltip.darksouls.darksign").getString();
		tooltip.add(new StringTextComponent("\u00A77" + description));
	}
	
	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand)
	{
		//world.playSound((PlayerEntity)null, player.getX(), player.getY(),  player.getZ(), SoundEventInit.DARKSIGN_USE.get(), SoundCategory.PLAYERS, 1.0F, 1.0F);
		return CastingHelper.startCasting(world, player, hand);
	}
	
	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, World world, LivingEntity livingentity)
	{
		PlayerEntity playerentity = livingentity instanceof PlayerEntity ? (PlayerEntity)livingentity : null;
	      if (playerentity instanceof ServerPlayerEntity)
	      {
	         CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayerEntity)playerentity, itemstack);
	      }
	      
	      if (!world.isClientSide && livingentity instanceof ServerPlayerEntity)
			{
				ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)livingentity;
				serverplayerentity.teleportTo(serverplayerentity.getRespawnPosition().getX(), serverplayerentity.getRespawnPosition().getY(), serverplayerentity.getRespawnPosition().getZ());
			}

	      if (playerentity != null)
	      {
	         playerentity.awardStat(Stats.ITEM_USED.get(this));
	      }

	      return itemstack;
	}
	
	@Override
	public UseAction getUseAnimation(ItemStack itemstack)
	{
		return UseAction.NONE;
	}
	
	@Override
	public int getUseDuration(ItemStack itemstack)
	{
		return 32;
	}
	
	@Override
	public void onUseTick(World world, LivingEntity livingentity, ItemStack itemstack, int durationremaining)
	{
		System.out.print("hoe");
		CastingHelper.triggerItemUseEffects(livingentity, itemstack, 5, durationremaining);
	}
	
	public SoundEvent getCastingSound()
	{
		return SoundEventInit.DARKSIGN_USE.get();
	}
}
