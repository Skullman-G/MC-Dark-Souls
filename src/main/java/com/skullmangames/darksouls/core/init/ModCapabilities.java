package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.common.capability.entity.EntityCapability;
import com.skullmangames.darksouls.common.capability.item.AttributeItemCap;
import com.skullmangames.darksouls.common.capability.item.ItemCapability;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap;
import com.skullmangames.darksouls.common.capability.item.WeaponCap;
import com.skullmangames.darksouls.common.capability.projectile.CapabilityProjectile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class ModCapabilities
{
	@SuppressWarnings("rawtypes")
	@CapabilityInject(EntityCapability.class)
	public static final Capability<EntityCapability> CAPABILITY_ENTITY = null;
	
	@CapabilityInject(ItemCapability.class)
    public static final Capability<ItemCapability> CAPABILITY_ITEM = null;
	
	@SuppressWarnings("rawtypes")
	@CapabilityInject(CapabilityProjectile.class)
    public static final Capability<CapabilityProjectile> CAPABILITY_PROJECTILE = null;
	
	@SuppressWarnings("rawtypes")
	public static void registerCapabilities()
	{
		CapabilityManager.INSTANCE.register(ItemCapability.class, new IStorage<ItemCapability>()
		{
			@Override
			public INBT writeNBT(Capability<ItemCapability> capability, ItemCapability instance, Direction side)
			{
				return null;
			}

			@Override
			public void readNBT(Capability<ItemCapability> capability, ItemCapability instance, Direction side, INBT nbt)
			{
			}
		}, () -> null);
		
		CapabilityManager.INSTANCE.register(EntityCapability.class, new IStorage<EntityCapability>()
		{
			@Override
			public INBT writeNBT(Capability<EntityCapability> capability, EntityCapability instance, Direction side)
			{
				return null;
			}

			@Override
			public void readNBT(Capability<EntityCapability> capability, EntityCapability instance, Direction side, INBT nbt)
			{
			}
		}, () -> null);
		
		CapabilityManager.INSTANCE.register(CapabilityProjectile.class, new IStorage<CapabilityProjectile>()
		{
			@Override
			public INBT writeNBT(Capability<CapabilityProjectile> capability, CapabilityProjectile instance, Direction side)
			{
				return null;
			}

			@Override
			public void readNBT(Capability<CapabilityProjectile> capability, CapabilityProjectile instance, Direction side, INBT nbt)
			{
			}
		}, () -> null);
	}
	
	
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