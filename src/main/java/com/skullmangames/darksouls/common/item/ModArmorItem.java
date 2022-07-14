package com.skullmangames.darksouls.common.item;

import com.skullmangames.darksouls.DarkSouls;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;

public class ModArmorItem extends ArmorItem
{
	private final String texture;
	
	public ModArmorItem(ArmorMaterial material, EquipmentSlotType slot, String texture, Properties properties)
	{
		super(material, slot, properties);
		this.texture = texture;
	}
	
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type)
	{
		return DarkSouls.MOD_ID + ":textures/models/armor/" + this.texture + ".png";
	}
}
