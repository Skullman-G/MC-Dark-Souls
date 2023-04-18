package com.skullmangames.darksouls.common.capability.item;

import java.util.List;
import java.util.function.Function;

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
	private final EquipmentSlot equipSlot;
	private Function<ClientModels, ClientModel> customModel;
	
	protected final float standardDef;
	protected final float strikeDef;
	protected final float slashDef;
	protected final float thrustDef;
	protected final float fireDef;
	protected final float lightningDef;
	
	protected float poise;
	protected float weight;
	
	public ArmorCap(Item item)
	{
		this(item, null);
	}
	
	public ArmorCap(Item item, Function<ClientModels, ClientModel> customModel)
	{
		super(item);
		ArmorItem armorItem = this.getOriginalItem();
		this.equipSlot = armorItem.getSlot();
		this.customModel = customModel;
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
					.append(new TextComponent(ChatFormatting.BLUE+": "+MathUtils.round(this.standardDef, 2))));
			
			itemTooltip.add(new TranslatableComponent(ModAttributes.STRIKE_DEFENSE.get().getDescriptionId()).withStyle(ChatFormatting.BLUE)
					.append(new TextComponent(ChatFormatting.BLUE+": "+MathUtils.round(this.strikeDef, 2))));
			
			itemTooltip.add(new TranslatableComponent(ModAttributes.SLASH_DEFENSE.get().getDescriptionId()).withStyle(ChatFormatting.BLUE)
					.append(new TextComponent(ChatFormatting.BLUE+": "+MathUtils.round(this.slashDef, 2))));
			
			itemTooltip.add(new TranslatableComponent(ModAttributes.THRUST_DEFENSE.get().getDescriptionId()).withStyle(ChatFormatting.BLUE)
					.append(new TextComponent(ChatFormatting.BLUE+": "+MathUtils.round(this.thrustDef, 2))));
			
			itemTooltip.add(new TranslatableComponent(ModAttributes.FIRE_DEFENSE.get().getDescriptionId()).withStyle(ChatFormatting.BLUE)
					.append(new TextComponent(ChatFormatting.BLUE+": "+MathUtils.round(this.fireDef, 2))));
			
			itemTooltip.add(new TranslatableComponent(ModAttributes.LIGHTNING_DEFENSE.get().getDescriptionId()).withStyle(ChatFormatting.BLUE)
					.append(new TextComponent(ChatFormatting.BLUE+": "+MathUtils.round(this.lightningDef, 2))));
			
			itemTooltip.add(new TextComponent(""));
			itemTooltip.add(new TranslatableComponent(ModAttributes.POISE.get().getDescriptionId()).withStyle(ChatFormatting.BLUE)
					.append(new TextComponent(ChatFormatting.BLUE+": "+MathUtils.round(this.poise, 2))));
			itemTooltip.add(new TranslatableComponent("attribute.darksouls.weight").withStyle(ChatFormatting.BLUE)
					.append(new TextComponent(ChatFormatting.BLUE+": "+MathUtils.round(this.weight, 2))));
		}
	}
	
	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot)
	{
		Multimap<Attribute, AttributeModifier> map = super.getAttributeModifiers(slot);
		
		if (this.equipSlot == slot)
		{
			map.put(ModAttributes.POISE.get(), ModAttributes.getAttributeModifierForSlot(this.equipSlot, this.poise));
			map.put(ModAttributes.EQUIP_LOAD.get(), ModAttributes.getAttributeModifierForSlot(this.equipSlot, this.weight));
			
			map.put(ModAttributes.STANDARD_DEFENSE.get(), ModAttributes.getAttributeModifierForSlot(this.equipSlot, this.standardDef));
			map.put(ModAttributes.STRIKE_DEFENSE.get(), ModAttributes.getAttributeModifierForSlot(this.equipSlot, this.strikeDef));
			map.put(ModAttributes.SLASH_DEFENSE.get(), ModAttributes.getAttributeModifierForSlot(this.equipSlot, this.slashDef));
			map.put(ModAttributes.THRUST_DEFENSE.get(), ModAttributes.getAttributeModifierForSlot(this.equipSlot, this.thrustDef));
			
			map.put(ModAttributes.FIRE_DEFENSE.get(), ModAttributes.getAttributeModifierForSlot(this.equipSlot, this.fireDef));
			map.put(ModAttributes.LIGHTNING_DEFENSE.get(), ModAttributes.getAttributeModifierForSlot(this.equipSlot, this.lightningDef));
		}
		
        return map;
    }
	
	@OnlyIn(Dist.CLIENT)
	public ClientModel getArmorModel()
	{
		return this.customModel != null ? this.customModel.apply(ClientModels.CLIENT) : getDefaultArmorModel(this.equipSlot);
	}
	
	@OnlyIn(Dist.CLIENT)
	public static ClientModel getDefaultArmorModel(EquipmentSlot slot)
	{
		ClientModels models = ClientModels.CLIENT;
		
		switch (slot)
		{
				case HEAD:
					return models.ITEM_HELMET;
					
				case CHEST:
					return models.ITEM_CHESTPLATE;
					
				case LEGS:
					return models.ITEM_LEGGINS;
					
				case FEET:
					return models.ITEM_BOOTS;
					
				default:
					return null;
		}
	}
}
