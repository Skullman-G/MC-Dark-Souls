package com.skullmangames.darksouls.common.items;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.IItemTier;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SwordDescriptionItem extends SwordItem
{
	public SwordDescriptionItem(IItemTier tier, int attackdamage, float attackspeed, Properties properties)
	{
		super(tier, attackdamage, attackspeed, properties);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
	{
		String description = new TranslationTextComponent("tooltip.darksouls." + this.getRegistryName().getPath()).getString();
		tooltip.add(new StringTextComponent("\u00A77" + description + "\n"));
	}
}
