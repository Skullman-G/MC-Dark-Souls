package com.skullmangames.darksouls.core.init;

import java.util.HashMap;
import java.util.Map;
import com.skullmangames.darksouls.common.capability.projectile.CapabilityProjectile;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.EggEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.SnowballEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;

public class ProviderProjectile<P extends ProjectileEntity> implements ICapabilityProvider, NonNullSupplier<CapabilityProjectile<?>>
{
	private static final Map<EntityType<?>, CapabilityProjectile<?>> CAPABILITIES = new HashMap<> ();
	
	public static void makeMap()
	{
		CAPABILITIES.put(EntityType.ARROW, new CapabilityProjectile<ArrowEntity>(20F));
		CAPABILITIES.put(EntityType.SNOWBALL, new CapabilityProjectile<SnowballEntity>(5F));
		CAPABILITIES.put(EntityType.EGG, new CapabilityProjectile<EggEntity>(5F));
	}
	
	private CapabilityProjectile<?> capability;
	private LazyOptional<CapabilityProjectile<?>> optional = LazyOptional.of(this);
	
	public ProviderProjectile(P entity)
	{
		if(CAPABILITIES.containsKey(entity.getType()))
		{
			this.capability = CAPABILITIES.get(entity.getType());
		}
	}
	
	public boolean hasCapability()
	{
		return this.capability != null;
	}
	
	@Override
	public CapabilityProjectile<?> get()
	{
		return this.capability;
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
	{
		return cap == ModCapabilities.CAPABILITY_PROJECTILE ? this.optional.cast() : LazyOptional.empty();
	}
}