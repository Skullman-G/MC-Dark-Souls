package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.common.capability.entity.EntityData;
import com.skullmangames.darksouls.common.capability.item.CapabilityItem;
import com.skullmangames.darksouls.common.capability.item.WeaponCapability;
import com.skullmangames.darksouls.common.capability.projectile.CapabilityProjectile;

import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;

public class ModCapabilities
{
	public static final Capability<EntityData<?>> CAPABILITY_ENTITY = null;
	
    public static final Capability<CapabilityItem> CAPABILITY_ITEM = null;
	
    public static final Capability<CapabilityProjectile<Projectile>> CAPABILITY_PROJECTILE = null;
	
	
	public static CapabilityItem getItemCapability(ItemStack stack)
	{
		return stack.getCapability(CAPABILITY_ITEM, null).orElse(null);
	}
	
	public static WeaponCapability getWeaponCapability(ItemStack stack)
	{
		CapabilityItem cap = getItemCapability(stack);
		return cap instanceof WeaponCapability ? (WeaponCapability)cap : null;
	}
}