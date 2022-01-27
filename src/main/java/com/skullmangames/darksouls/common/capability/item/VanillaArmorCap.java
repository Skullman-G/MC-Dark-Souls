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
						switch (armorItem.getSlot())
						{
							case HEAD:
								this.weight = 1.2F;
								break;
							
							case CHEST:
								this.weight = 4.7F;
								break;
							
							case LEGS:
								this.weight = 2.8F;
								break;
							
							case FEET:
								this.weight = 2.8F;
								break;
								
							default:
								break;
						}
						break;
					
					case GOLD:
						this.poise = 1.0F;
						this.standardDef = 0.1F;
						this.strikeDef = 0.1F;
						this.slashDef = 0.1F;
						this.thrustDef = 0.1F;
						switch (armorItem.getSlot())
						{
							case HEAD:
								this.weight = 4.5F;
								break;
							
							case CHEST:
								this.weight = 11.7F;
								break;
							
							case LEGS:
								this.weight = 6.9F;
								break;
							
							case FEET:
								this.weight = 5.0F;
								break;
								
							default:
								break;
						}
						break;
					
					case CHAIN:
						this.poise = 1.0F;
						this.standardDef = 0.12F;
						this.strikeDef = 0.1F;
						this.slashDef = 0.12F;
						this.thrustDef = 0.12F;
						switch (armorItem.getSlot())
						{
							case HEAD:
								this.weight = 3.0F;
								break;
							
							case CHEST:
								this.weight = 6.0F;
								break;
							
							case LEGS:
								this.weight = 4.6F;
								break;
							
							case FEET:
								this.weight = 3.6F;
								break;
								
							default:
								break;
						}
						break;
					
					case IRON:
						this.poise = 1.0F;
						this.standardDef = 0.25F;
						this.strikeDef = 0.2F;
						this.slashDef = 0.25F;
						this.thrustDef = 0.2F;
						switch (armorItem.getSlot())
						{
							case HEAD:
								this.weight = 4.0F;
								break;
							
							case CHEST:
								this.weight = 7.0F;
								break;
							
							case LEGS:
								this.weight = 5.6F;
								break;
							
							case FEET:
								this.weight = 4.6F;
								break;
								
							default:
								break;
						}
						break;
					
					case DIAMOND:
						this.poise = 1.0F;
						this.standardDef = 0.35F;
						this.strikeDef = 0.0F;
						this.slashDef = 0.35F;
						this.thrustDef = 0.12F;
						switch (armorItem.getSlot())
						{
							case HEAD:
								this.weight = 5.0F;
								break;
							
							case CHEST:
								this.weight = 8.0F;
								break;
							
							case LEGS:
								this.weight = 6.6F;
								break;
							
							case FEET:
								this.weight = 4.6F;
								break;
								
							default:
								break;
						}
						break;
					
					case NETHERITE:
						this.poise = 1.0F;
						this.standardDef = 0.45F;
						this.strikeDef = 0.45F;
						this.slashDef = 0.45F;
						this.thrustDef = 0.45F;
						switch (armorItem.getSlot())
						{
							case HEAD:
								this.weight = 5.0F;
								break;
							
							case CHEST:
								this.weight = 9.0F;
								break;
							
							case LEGS:
								this.weight = 7.9F;
								break;
							
							case FEET:
								this.weight = 5.8F;
								break;
								
							default:
								break;
						}
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