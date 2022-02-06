package com.skullmangames.darksouls.common.item;

import com.skullmangames.darksouls.DarkSouls;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;

public class ModArmorItem extends ArmorItem
{
	private final String texture;
	
	public ModArmorItem(ArmorMaterial material, EquipmentSlot slot, String texture, Properties properties)
	{
		super(material, slot, properties);
		this.texture = texture;
	}
	
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type)
	{
		return DarkSouls.MOD_ID + ":textures/models/armor/" + this.texture + ".png";
	}
}
