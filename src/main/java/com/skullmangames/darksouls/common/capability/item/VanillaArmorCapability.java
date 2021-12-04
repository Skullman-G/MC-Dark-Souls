package com.skullmangames.darksouls.common.capability.item;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;

public class VanillaArmorCapability extends ArmorCapability
{
	public VanillaArmorCapability(Item item)
	{
		super(item);

		if (item instanceof ArmorItem)
		{
			ArmorItem armorItem = ((ArmorItem) item);
			if (armorItem.getMaterial() instanceof ArmorMaterial)
			{
				switch ((ArmorMaterial) armorItem.getMaterial())
				{
					case LEATHER:
						this.weight = armorItem.getToughness();
						this.stunArmor = armorItem.getToughness() * 0.25D;
						this.standardDef = 0.1F;
						this.strikeDef = 0.1F;
						this.slashDef = 0.05F;
						this.thrustDef = 0.05F;
						break;
					case GOLD:
						this.weight = armorItem.getToughness() * 2.0D;
						this.stunArmor = armorItem.getToughness() * 0.3D;
						this.standardDef = 0.1F;
						this.strikeDef = 0.1F;
						this.slashDef = 0.1F;
						this.thrustDef = 0.1F;
						break;
					case CHAIN:
						this.weight = armorItem.getToughness() * 2.5D;
						this.stunArmor = armorItem.getToughness() * 0.375D;
						this.standardDef = 0.12F;
						this.strikeDef = 0.1F;
						this.slashDef = 0.12F;
						this.thrustDef = 0.12F;
						break;
					case IRON:
						this.weight = armorItem.getToughness() * 3.0D;
						this.stunArmor = armorItem.getToughness() * 0.5D;
						this.standardDef = 0.25F;
						this.strikeDef = 0.2F;
						this.slashDef = 0.25F;
						this.thrustDef = 0.2F;
						break;
					case DIAMOND:
						this.weight = armorItem.getToughness() * 3.0D;
						this.stunArmor = armorItem.getToughness() * 0.5D;
						this.standardDef = 0.35F;
						this.strikeDef = 0.0F;
						this.slashDef = 0.35F;
						this.thrustDef = 0.12F;
						break;
					case NETHERITE:
						this.weight = armorItem.getToughness() * 3.2D;
						this.stunArmor = armorItem.getToughness() * 0.75D;
						this.standardDef = 0.45F;
						this.strikeDef = 0.45F;
						this.slashDef = 0.45F;
						this.thrustDef = 0.45F;
						break;
					default:
						this.weight = 0.0D;
						this.stunArmor = 0.0D;
				}
			} else
			{
				this.weight = 0.0D;
				this.stunArmor = 0.0D;
			}
		}
	}
}