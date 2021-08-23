package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.common.capability.entity.EntityData;
import com.skullmangames.darksouls.common.capability.item.CapabilityItem;
import com.skullmangames.darksouls.common.capability.item.FistCapability;
import com.skullmangames.darksouls.common.capability.projectile.CapabilityProjectile;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class ModCapabilities
{
	@SuppressWarnings("rawtypes")
	@CapabilityInject(EntityData.class)
	public static final Capability<EntityData> CAPABILITY_ENTITY = null;
	
	@CapabilityInject(CapabilityItem.class)
    public static final Capability<CapabilityItem> CAPABILITY_ITEM = null;
	
	@SuppressWarnings("rawtypes")
	@CapabilityInject(CapabilityProjectile.class)
    public static final Capability<CapabilityProjectile> CAPABILITY_PROJECTILE = null;
	
	@SuppressWarnings("rawtypes")
	public static void registerCapabilities()
	{
		CapabilityManager.INSTANCE.register(CapabilityItem.class, new IStorage<CapabilityItem>()
		{
			@Override
			public INBT writeNBT(Capability<CapabilityItem> capability, CapabilityItem instance, Direction side)
			{
				return null;
			}

			@Override
			public void readNBT(Capability<CapabilityItem> capability, CapabilityItem instance, Direction side, INBT nbt)
			{
			}
		}, () -> null);
		
		CapabilityManager.INSTANCE.register(EntityData.class, new IStorage<EntityData>()
		{
			@Override
			public INBT writeNBT(Capability<EntityData> capability, EntityData instance, Direction side)
			{
				return null;
			}

			@Override
			public void readNBT(Capability<EntityData> capability, EntityData instance, Direction side, INBT nbt)
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
	
	public static final FistCapability FIST = new FistCapability(Items.AIR);
	
	public static CapabilityItem stackCapabilityGetter(ItemStack stack)
	{
		return stack.isEmpty() ? FIST : stack.getCapability(CAPABILITY_ITEM, null).orElse(null);
	}
}