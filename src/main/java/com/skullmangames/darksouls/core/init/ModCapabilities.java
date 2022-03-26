package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.common.capability.entity.EntityCapability;
import com.skullmangames.darksouls.common.capability.item.AttributeItemCap;
import com.skullmangames.darksouls.common.capability.item.ItemCapability;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap;
import com.skullmangames.darksouls.common.capability.item.WeaponCap;
import com.skullmangames.darksouls.common.capability.projectile.CapabilityProjectile;

import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;

public class ModCapabilities
{
	public static final Capability<EntityCapability<?>> CAPABILITY_ENTITY = null;
	
    public static final Capability<ItemCapability> CAPABILITY_ITEM = null;
	
    public static final Capability<CapabilityProjectile<Projectile>> CAPABILITY_PROJECTILE = null;
	
	
	public static ItemCapability getItemCapability(ItemStack stack)
	{
		return stack.getCapability(CAPABILITY_ITEM, null).orElse(null);
	}
	
	public static WeaponCap getWeaponCap(ItemStack stack)
	{
		ItemCapability cap = getItemCapability(stack);
		return cap instanceof WeaponCap ? (WeaponCap)cap : null;
	}
	
	public static MeleeWeaponCap getMeleeWeaponCap(ItemStack stack)
	{
		ItemCapability cap = getItemCapability(stack);
		return cap instanceof MeleeWeaponCap ? (MeleeWeaponCap)cap : null;
	}
	
	public static AttributeItemCap getAttributeItemCap(ItemStack stack)
	{
		ItemCapability cap = getItemCapability(stack);
		return cap instanceof AttributeItemCap ? (AttributeItemCap)cap : null;
	}
}