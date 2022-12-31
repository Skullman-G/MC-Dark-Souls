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
	private final ArmorPart armorPart;
	
	protected final float standardDef;
	protected final float strikeDef;
	protected final float slashDef;
	protected final float thrustDef;
	protected final float fireDef;
	protected final float lightningDef;
	
	protected float poise;
	protected float weight;
	
	public ArmorCap(Item item, ArmorPart armorPart)
	{
		super(item);
		ArmorItem armorItem = (ArmorItem) item;
		this.armorPart = armorPart.slot == armorItem.getSlot() ? armorPart : ArmorPart.getFrom(armorItem.getSlot());
		this.poise = armorItem.getDefense() * 2.0F;
		
		this.standardDef = armorItem.getDefense() * 0.5F;
		this.slashDef = this.standardDef;
		this.strikeDef = this.standardDef * 1.1F;
		this.thrustDef = this.standardDef * 0.9F;
		this.fireDef = this.standardDef * 0.5F;
		this.lightningDef = this.standardDef * 0.5F;
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
			
			itemTooltip.add(new TranslatableComponent(ModAttributes.FIRE_DEFENSE.get().getDescriptionId()).withStyle(ChatFormatting.BLUE)
					.append(new TextComponent(ChatFormatting.BLUE+": "+MathUtils.round(this.fireDef, 100))));
			
			itemTooltip.add(new TranslatableComponent(ModAttributes.LIGHTNING_DEFENSE.get().getDescriptionId()).withStyle(ChatFormatting.BLUE)
					.append(new TextComponent(ChatFormatting.BLUE+": "+MathUtils.round(this.lightningDef, 100))));
			
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
		
		if (slot == this.armorPart.slot)
		{
			map.put(ModAttributes.POISE.get(), ModAttributes.getAttributeModifierForSlot(this.armorPart.slot, this.poise));
			map.put(ModAttributes.EQUIP_LOAD.get(), ModAttributes.getAttributeModifierForSlot(this.armorPart.slot, this.weight));
			
			map.put(ModAttributes.STANDARD_DEFENSE.get(), ModAttributes.getAttributeModifierForSlot(this.armorPart.slot, this.standardDef));
			map.put(ModAttributes.STRIKE_DEFENSE.get(), ModAttributes.getAttributeModifierForSlot(this.armorPart.slot, this.strikeDef));
			map.put(ModAttributes.SLASH_DEFENSE.get(), ModAttributes.getAttributeModifierForSlot(this.armorPart.slot, this.slashDef));
			map.put(ModAttributes.THRUST_DEFENSE.get(), ModAttributes.getAttributeModifierForSlot(this.armorPart.slot, this.thrustDef));
			
			map.put(ModAttributes.FIRE_DEFENSE.get(), ModAttributes.getAttributeModifierForSlot(this.armorPart.slot, this.fireDef));
			map.put(ModAttributes.LIGHTNING_DEFENSE.get(), ModAttributes.getAttributeModifierForSlot(this.armorPart.slot, this.lightningDef));
		}
		
        return map;
    }
	
	@OnlyIn(Dist.CLIENT)
	public ClientModel getArmorModel()
	{
		return getDefaultArmorModel(this.armorPart);
	}
	
	@OnlyIn(Dist.CLIENT)
	public static ClientModel getDefaultArmorModel(ArmorPart armorPart)
	{
		ClientModels models = ClientModels.CLIENT;
		
		switch (armorPart)
		{
				case HELMET:
					return models.ITEM_HELMET;
					
				case CHESTPLATE:
					return models.ITEM_CHESTPLATE;
					
				case LEGGINS:
					return models.ITEM_LEGGINS;
					
				case SKIRT:
					return models.ITEM_SKIRT;
					
				case BOOTS:
					return models.ITEM_BOOTS;
					
				case ONE_SHOE:
					return models.ITEM_ONE_SHOE;
					
				case FALCONER_HELM:
					return models.ITEM_FALCONER_HELM;
					
				case FALCONER_ARMOR:
					return models.ITEM_FALCONER_ARMOR;
					
				default:
					return null;
		}
	}
	
	public enum ArmorPart
	{
		HELMET(EquipmentSlot.HEAD), CHESTPLATE(EquipmentSlot.CHEST),
		LEGGINS(EquipmentSlot.LEGS), SKIRT(EquipmentSlot.LEGS),
		BOOTS(EquipmentSlot.FEET), ONE_SHOE(EquipmentSlot.FEET),
		FALCONER_HELM(EquipmentSlot.HEAD), FALCONER_ARMOR(EquipmentSlot.CHEST);
		
		private final EquipmentSlot slot;
		
		private ArmorPart(EquipmentSlot slot)
		{
			this.slot = slot;
		}
		
		public static ArmorPart getFrom(EquipmentSlot slot)
		{
			switch (slot)
			{
				default:	
				case HEAD:
					return HELMET;
					
				case CHEST:
					return CHESTPLATE;
					
				case LEGS:
					return LEGGINS;
					
				case FEET:
					return BOOTS;
			}
		}
		
		public static ArmorPart getFrom(Item item)
		{
			if (item instanceof ArmorItem) return getFrom(((ArmorItem)item).getSlot());
			else return ArmorPart.HELMET;
		}
	}
}
