package com.skullmangames.darksouls.common.capability.item;

import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;

public class VanillaArmorCap extends ArmorCap
{
	public VanillaArmorCap(Item item)
	{
		super(item);

		switch ((ArmorMaterials) this.getOriginalItem().getMaterial())
		{
			case LEATHER:
				this.poise = 1.0F;
				switch (this.getOriginalItem().getSlot())
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
				switch (this.getOriginalItem().getSlot())
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
				switch (this.getOriginalItem().getSlot())
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
				switch (this.getOriginalItem().getSlot())
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
				switch (this.getOriginalItem().getSlot())
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
				switch (this.getOriginalItem().getSlot())
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
	}
}