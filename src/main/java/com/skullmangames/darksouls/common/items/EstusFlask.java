package com.skullmangames.darksouls.common.items;

import java.util.List;

import com.skullmangames.darksouls.core.init.EffectInit;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class EstusFlask extends Item
{
	private int uses;
	private int totaluses;
	
	public EstusFlask() 
	{
		super(new Item.Properties().tab(ItemGroup.TAB_BREWING).stacksTo(1).food(new Food.Builder().effect(() -> new EffectInstance(EffectInit.INSTANT_HEAL.get(), 1, 1), 1.0f).build()));
		this.uses = 5;
		this.totaluses = 5;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) 
	{
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		tooltip.add(new TranslationTextComponent("tooltip.estus_flask" + uses + "/" + totaluses));
	}
}
