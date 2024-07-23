package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.common.capability.entity.EntityCapability;
import com.skullmangames.darksouls.common.capability.item.AttributeItemCap;
import com.skullmangames.darksouls.common.capability.item.ItemCapability;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap;
import com.skullmangames.darksouls.common.capability.item.SpellcastingWeaponCap;
import com.skullmangames.darksouls.common.capability.item.WeaponCap;
import com.skullmangames.darksouls.common.capability.projectile.CapabilityProjectile;

import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class ModCapabilities
{
	public static final Capability<EntityCapability<?>> CAPABILITY_ENTITY = CapabilityManager.get(new CapabilityToken<>(){});
	
    public static final Capability<ItemCapability> CAPABILITY_ITEM = CapabilityManager.get(new CapabilityToken<>(){});
	
    public static final Capability<CapabilityProjectile<Projectile>> CAPABILITY_PROJECTILE = CapabilityManager.get(new CapabilityToken<>(){});
	
	
	public static ItemCapability getItemCapability(ItemStack stack)
	{
		return stack.isEmpty() ? null : stack.getCapability(CAPABILITY_ITEM, null).orElse(null);
	}
	
	public static SpellcastingWeaponCap getSpellcasterWeaponCap(ItemStack stack)
	{
		ItemCapability cap = getItemCapability(stack);
		return cap instanceof SpellcastingWeaponCap weaponCap ? weaponCap : null;
	}
	
	public static WeaponCap getWeaponCap(ItemStack stack)
	{
		ItemCapability cap = getItemCapability(stack);
		return cap instanceof WeaponCap weaponCap ? weaponCap : null;
	}
	
	public static MeleeWeaponCap getMeleeWeaponCap(ItemStack stack)
	{
		ItemCapability cap = getItemCapability(stack);
		return cap instanceof MeleeWeaponCap weaponCap ? weaponCap : null;
	}
	
	public static AttributeItemCap getAttributeItemCap(ItemStack stack)
	{
		ItemCapability cap = getItemCapability(stack);
		return cap instanceof AttributeItemCap attributeCap ? attributeCap : null;
	}
}