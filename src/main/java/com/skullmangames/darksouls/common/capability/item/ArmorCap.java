package com.skullmangames.darksouls.common.capability.item;

import java.util.List;
import com.google.common.collect.Multimap;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.client.input.ModKeys;
import com.skullmangames.darksouls.client.renderer.entity.model.ClientModel;
import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.util.math.MathUtils;
import com.skullmangames.darksouls.core.init.ClientModels;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class ArmorCap extends AttributeItemCap
{
	private final EquipmentSlot equipmentSlot;
	
	protected final float standardDef;
	protected final float strikeDef;
	protected final float slashDef;
	protected final float thrustDef;
	
	protected float poise;
	protected float weight;
	
	public ArmorCap(Item item)
	{
		super(item);
		ArmorItem armorItem = (ArmorItem) item;
		this.equipmentSlot = armorItem.getSlot();
		this.poise = armorItem.getDefense() * 2.0F;
		
		this.standardDef = armorItem.getDefense() * 0.5F;
		this.slashDef = this.standardDef;
		this.strikeDef = this.standardDef * 1.1F;
		this.thrustDef = this.standardDef * 0.9F;
	}
	
	@Override
	public ArmorItem getOriginalItem()
	{
		return (ArmorItem)this.orgItem;
	}
	
	@Override
	public void modifyItemTooltip(List<Component> itemTooltip, PlayerCap<?> playerdata, ItemStack stack)
	{
		if (!(this.orgItem instanceof IForgeRegistryEntry)) return;

		while (itemTooltip.size() >= 2) itemTooltip.remove(1);

		if (ClientManager.INSTANCE.inputManager.isKeyDown(ModKeys.SHOW_ITEM_INFO))
		{
			String languagePath = "tooltip." + DarkSouls.MOD_ID + "."
					+ ((IForgeRegistryEntry<Item>) this.orgItem).getRegistryName().getPath() + ".extended";
			String description = new TranslatableComponent(languagePath).getString();

			if (!description.contains(languagePath))
				itemTooltip.add(new TextComponent("\u00A77\n" + description));
		}
		else
		{
			itemTooltip.add(new TextComponent(""));
			itemTooltip.add(new TranslatableComponent(ModAttributes.STANDARD_DEFENSE.get().getDescriptionId()).withStyle(ChatFormatting.BLUE)
					.append(new TextComponent(ChatFormatting.BLUE+": "+MathUtils.round(this.standardDef, 100))));
			
			itemTooltip.add(new TranslatableComponent(ModAttributes.STRIKE_DEFENSE.get().getDescriptionId()).withStyle(ChatFormatting.BLUE)
					.append(new TextComponent(ChatFormatting.BLUE+": "+MathUtils.round(this.strikeDef, 100))));
			
			itemTooltip.add(new TranslatableComponent(ModAttributes.SLASH_DEFENSE.get().getDescriptionId()).withStyle(ChatFormatting.BLUE)
					.append(new TextComponent(ChatFormatting.BLUE+": "+MathUtils.round(this.slashDef, 100))));
			
			itemTooltip.add(new TranslatableComponent(ModAttributes.THRUST_DEFENSE.get().getDescriptionId()).withStyle(ChatFormatting.BLUE)
					.append(new TextComponent(ChatFormatting.BLUE+": "+MathUtils.round(this.thrustDef, 100))));
			
			itemTooltip.add(new TextComponent(""));
			itemTooltip.add(new TranslatableComponent(ModAttributes.POISE.get().getDescriptionId()).withStyle(ChatFormatting.BLUE)
					.append(new TextComponent(ChatFormatting.BLUE+": "+MathUtils.round(this.poise, 100))));
			itemTooltip.add(new TranslatableComponent("attribute.darksouls.weight").withStyle(ChatFormatting.BLUE)
					.append(new TextComponent(ChatFormatting.BLUE+": "+MathUtils.round(this.weight, 100))));
		}
	}
	
	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot)
	{
		Multimap<Attribute, AttributeModifier> map = super.getAttributeModifiers(slot);
		
		if (slot == this.equipmentSlot)
		{
			map.put(ModAttributes.POISE.get(), ModAttributes.getAttributeModifierForSlot(this.equipmentSlot, this.poise));
			map.put(ModAttributes.EQUIP_LOAD.get(), ModAttributes.getAttributeModifierForSlot(this.equipmentSlot, this.weight));
			
			map.put(ModAttributes.STANDARD_DEFENSE.get(), ModAttributes.getAttributeModifierForSlot(this.equipmentSlot, this.standardDef));
			map.put(ModAttributes.STRIKE_DEFENSE.get(), ModAttributes.getAttributeModifierForSlot(this.equipmentSlot, this.strikeDef));
			map.put(ModAttributes.SLASH_DEFENSE.get(), ModAttributes.getAttributeModifierForSlot(this.equipmentSlot, this.slashDef));
			map.put(ModAttributes.THRUST_DEFENSE.get(), ModAttributes.getAttributeModifierForSlot(this.equipmentSlot, this.thrustDef));
		}
		
        return map;
    }
	
	@OnlyIn(Dist.CLIENT)
	public ClientModel getArmorModel(EquipmentSlot slot)
	{
		return getBipedArmorModel(slot);
	}
	
	@OnlyIn(Dist.CLIENT)
	public static ClientModel getBipedArmorModel(EquipmentSlot slot)
	{
		ClientModels modelDB = ClientModels.CLIENT;
		
		switch (slot)
		{
				case HEAD:
					return modelDB.ITEM_HELMET;
					
				case CHEST:
					return modelDB.ITEM_CHESTPLATE;
					
				case LEGS:
					return modelDB.ITEM_LEGGINS;
					
				case FEET:
					return modelDB.ITEM_BOOTS;
					
				default:
					return null;
		}
	}
}
