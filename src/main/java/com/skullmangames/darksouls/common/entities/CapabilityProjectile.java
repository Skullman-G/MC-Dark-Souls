package com.skullmangames.darksouls.common.entities;

import java.util.Map;
import java.util.function.Supplier;

import com.skullmangames.darksouls.common.ModCapabilities;
import com.skullmangames.darksouls.common.items.CapabilityItem;
import com.skullmangames.darksouls.common.items.CapabilityItem.WieldStyle;
import com.skullmangames.darksouls.common.items.RangedWeaponCapability;
import com.skullmangames.darksouls.core.init.AttributeInit;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;

public class CapabilityProjectile<T extends ProjectileEntity>
{
	private float impact;
	private float armorNegation;
	
	public void onJoinWorld(T projectileEntity)
	{
		Entity shooter = projectileEntity.getOwner();
		boolean flag = true;
		
		if (shooter != null && shooter instanceof LivingEntity)
		{
			LivingEntity livingshooter = (LivingEntity)shooter;
			ItemStack heldItem = livingshooter.getMainHandItem();
			CapabilityItem itemCap = heldItem.getCapability(ModCapabilities.CAPABILITY_ITEM).orElse(null);
			
			if (itemCap instanceof RangedWeaponCapability)
			{
				Map<Supplier<Attribute>, AttributeModifier> modifierMap = itemCap.getDamageAttributesInCondition(WieldStyle.TWO_HAND);
				
				this.armorNegation = (float)modifierMap.get(AttributeInit.ARMOR_NEGATION).getAmount();
				this.impact = (float)modifierMap.get(AttributeInit.IMPACT).getAmount();
				this.setMaxStrikes(projectileEntity, (int)modifierMap.get(AttributeInit.MAX_STRIKES).getAmount());
				flag = false;
			}
		}
		
		if (flag)
		{
			this.armorNegation = 0.0F;
			this.impact = 0.0F;
		}
	}
	
	protected void setMaxStrikes(T projectileEntity, int maxStrikes)
	{
		
	}
	
	public float getArmorNegation()
	{
		return this.armorNegation;
	}
	
	public float getImpact()
	{
		return this.impact;
	}
}