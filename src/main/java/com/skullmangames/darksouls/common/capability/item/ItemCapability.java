package com.skullmangames.darksouls.common.capability.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.client.input.ModKeys;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.common.entity.stats.Stats;
import com.skullmangames.darksouls.common.item.SpellItem;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class ItemCapability
{
	protected final Item orgItem;
	
	public ItemCapability(Item item)
	{
		this.orgItem = item;
	}
	
	public Item getOriginalItem()
	{
		return this.orgItem;
	}
	
	public boolean isTwoHanded()
	{
		return false;
	}
	
	@NonNull
	public Map<LivingMotion, StaticAnimation> getLivingMotionChanges(LivingCap<?> cap)
	{
		return new HashMap<>();
	}
	
	@OnlyIn(Dist.CLIENT)
	public boolean canBeRenderedBoth(ItemStack item)
	{
		return !item.isEmpty();
	}
	
	public boolean canUsedInOffhand()
	{
		return true;
	}
	
	public boolean canBeRenderedOnBack()
	{
		return false;
	}
	
	public void modifyItemTooltip(List<Component> itemTooltip, PlayerCap<?> playerCap, ItemStack stack)
	{
		if (!(this.orgItem instanceof IForgeRegistryEntry)) return;
		
		int index = 1;
		String languagePath = "tooltip."+DarkSouls.MOD_ID+"."+((IForgeRegistryEntry<Item>)this.orgItem).getRegistryName().getPath();
		String description = new TranslatableComponent(languagePath).getString();
		boolean isSpell = this.orgItem instanceof SpellItem;

		if (!description.contains(languagePath))
		{
			itemTooltip.add(index++, new TextComponent("\u00A77" + description));
			if (isSpell) itemTooltip.add(new TextComponent(""));
		}
		if (isSpell)
		{
			int reqFaith = ((SpellItem)this.orgItem).getRequiredFaith();
			String color = playerCap.getStats().getStatValue(Stats.FAITH) >= reqFaith ? "\u00A7f" : "\u00A74";
			itemTooltip.add(new TextComponent("Requirements:"));
			itemTooltip.add(new TextComponent("  " + new TranslatableComponent(Stats.FAITH.toString()).getString() + ": "
					+ color + reqFaith));
		}
		
		if (!ClientManager.INSTANCE.inputManager.isKeyDown(ModKeys.SHOW_ITEM_INFO)) return;
		
		languagePath = "tooltip."+DarkSouls.MOD_ID+"."+((IForgeRegistryEntry<Item>)this.orgItem).getRegistryName().getPath()+".extended";
		description = new TranslatableComponent(languagePath).getString();
		
		if (!description.contains(languagePath)) itemTooltip.add(index++, new TextComponent("\u00A77\n" + description));
	}
	
	public void onHeld(PlayerCap<?> playerCap) {}
}