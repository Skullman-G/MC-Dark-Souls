package com.skullmangames.darksouls.common.capability.item;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.skullmangames.darksouls.client.renderer.entity.model.ClientModel;
import com.skullmangames.darksouls.common.capability.entity.LivingData;
import com.skullmangames.darksouls.common.capability.entity.PlayerData;
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
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ArmorCap extends AttributeItemCap
{
	protected static final UUID[] ARMOR_MODIFIERS = new UUID[] {UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
	
	private final EquipmentSlot equipmentSlot;
	
	protected float standardDef;
	protected float strikeDef;
	protected float slashDef;
	protected float thrustDef;
	
	protected float poise;
	
	public ArmorCap(Item item)
	{
		super(item);
		ArmorItem armorItem = (ArmorItem) item;
		ArmorMaterial armorMaterial = armorItem.getMaterial();
		this.equipmentSlot = armorItem.getSlot();
		this.poise = armorMaterial.getDefenseForSlot(this.equipmentSlot) * 2.0F;
	}
	
	@Override
	public void modifyItemTooltip(List<Component> itemTooltip, PlayerData<?> playerdata, ItemStack stack)
	{
		itemTooltip.add(new TextComponent(""));
		itemTooltip.add(new TranslatableComponent(ModAttributes.STANDARD_DEFENSE.get().getDescriptionId()).withStyle(ChatFormatting.BLUE)
				.append(new TextComponent(ChatFormatting.BLUE+": "+(int)(this.standardDef*100)+"%")));
		
		itemTooltip.add(new TranslatableComponent(ModAttributes.STRIKE_DEFENSE.get().getDescriptionId()).withStyle(ChatFormatting.BLUE)
				.append(new TextComponent(ChatFormatting.BLUE+": "+(int)(this.strikeDef*100)+"%")));
		
		itemTooltip.add(new TranslatableComponent(ModAttributes.SLASH_DEFENSE.get().getDescriptionId()).withStyle(ChatFormatting.BLUE)
				.append(new TextComponent(ChatFormatting.BLUE+": "+(int)(this.slashDef*100)+"%")));
		
		itemTooltip.add(new TranslatableComponent(ModAttributes.THRUST_DEFENSE.get().getDescriptionId()).withStyle(ChatFormatting.BLUE)
				.append(new TextComponent(ChatFormatting.BLUE+": "+(int)(this.thrustDef*100)+"%")));
		
		if(this.poise > 0.0F)
		{
			itemTooltip.add(new TextComponent(""));
			itemTooltip.add(new TranslatableComponent(ModAttributes.POISE.get().getDescriptionId()).withStyle(ChatFormatting.BLUE)
					.append(new TextComponent(ChatFormatting.BLUE+": "+MathUtils.round(this.poise, 100))));
		}
	}
	
	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot equipmentSlot, LivingData<?> entitydata)
	{
		Multimap<Attribute, AttributeModifier> map = HashMultimap.<Attribute, AttributeModifier>create();
		
		if (entitydata != null && equipmentSlot == this.equipmentSlot)
		{
			map.put(ModAttributes.POISE.get(), new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Armor modifier", this.poise, Operation.ADDITION));
			
			map.put(ModAttributes.STANDARD_DEFENSE.get(), new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Armor modifier", this.standardDef, Operation.ADDITION));
			map.put(ModAttributes.STRIKE_DEFENSE.get(), new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Armor modifier", this.strikeDef, Operation.ADDITION));
			map.put(ModAttributes.SLASH_DEFENSE.get(), new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Armor modifier", this.slashDef, Operation.ADDITION));
			map.put(ModAttributes.THRUST_DEFENSE.get(), new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Armor modifier", this.thrustDef, Operation.ADDITION));
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
