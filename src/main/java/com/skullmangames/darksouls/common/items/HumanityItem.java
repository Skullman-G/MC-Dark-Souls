package com.skullmangames.darksouls.common.items;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HumanityItem extends Item implements IHaveDarkSoulsUseAction
{
	public HumanityItem(Properties properties)
	{
		super(properties);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
	{
		String description = new TranslationTextComponent("tooltip.darksouls.humanity").getString();
		tooltip.add(new StringTextComponent("\u00A77" + description));
	}
	
	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand)
	{
		player.startUsingItem(hand);
	    return ActionResult.consume(player.getItemInHand(hand));
	}
	
	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, World world, LivingEntity livingentity)
	{
		livingentity.heal(livingentity.getMaxHealth() - livingentity.getHealth());
		itemstack.shrink(1);
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
	public DarkSoulsUseAction getDarkSoulsUseAnimation(ItemStack itemstack)
	{
		return DarkSoulsUseAction.SOUL_CONTAINER;
	}
}
