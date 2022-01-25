package com.skullmangames.darksouls.common.capability.item;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;

public class VanillaArmorCap extends ArmorCap
{
	public VanillaArmorCap(Item item)
	{
		super(item);

		if (item instanceof ArmorItem)
		{
			ArmorItem armorItem = ((ArmorItem) item);
			if (armorItem.getMaterial() instanceof ArmorMaterial)
			{
				switch ((ArmorMaterials) armorItem.getMaterial())
				{
					case LEATHER:
						this.poise = 1.0F;
						this.standardDef = 0.1F;
						this.strikeDef = 0.1F;
						this.slashDef = 0.05F;
						this.thrustDef = 0.05F;
						break;
					case GOLD:
						this.poise = 1.0F;
						this.standardDef = 0.1F;
						this.strikeDef = 0.1F;
						this.slashDef = 0.1F;
						this.thrustDef = 0.1F;
						break;
					case CHAIN:
						this.poise = 1.0F;
						this.standardDef = 0.12F;
						this.strikeDef = 0.1F;
						this.slashDef = 0.12F;
						this.thrustDef = 0.12F;
						break;
					case IRON:
						this.poise = 1.0F;
						this.standardDef = 0.25F;
						this.strikeDef = 0.2F;
						this.slashDef = 0.25F;
						this.thrustDef = 0.2F;
						break;
					case DIAMOND:
						this.poise = 1.0F;
						this.standardDef = 0.35F;
						this.strikeDef = 0.0F;
						this.slashDef = 0.35F;
						this.thrustDef = 0.12F;
						break;
					case NETHERITE:
						this.poise = 1.0F;
						this.standardDef = 0.45F;
						this.strikeDef = 0.45F;
						this.slashDef = 0.45F;
						this.thrustDef = 0.45F;
						break;
					default:
						this.poise = 0.0F;
				}
			} else
			{
				this.poise = 0.0F;
			}
		}
	}
}