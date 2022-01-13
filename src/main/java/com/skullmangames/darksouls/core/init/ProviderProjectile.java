package com.skullmangames.darksouls.core.init;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.projectile.CapabilityProjectile;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;

public class ProviderProjectile<P extends Projectile> implements ICapabilityProvider, NonNullSupplier<CapabilityProjectile<?>>
{
	private static final Map<EntityType<?>, Supplier<CapabilityProjectile<?>>> CAPABILITY_BY_TYPE
					= new HashMap<EntityType<?>, Supplier<CapabilityProjectile<?>>> ();
	
	public static void makeMap()
	{
		CAPABILITY_BY_TYPE.computeIfAbsent(EntityType.ARROW, (type) -> CapabilityProjectile<Arrow>::new);
		CAPABILITY_BY_TYPE.computeIfAbsent(EntityType.SNOWBALL, (type) -> CapabilityProjectile<Snowball>::new);
		CAPABILITY_BY_TYPE.computeIfAbsent(EntityType.EGG, (type) -> CapabilityProjectile<ThrownEgg>::new);
	}
	
	private CapabilityProjectile<?> capability;
	private LazyOptional<CapabilityProjectile<?>> optional = LazyOptional.of(this);
	
	public ProviderProjectile(P entity)
	{
		if(CAPABILITY_BY_TYPE.containsKey(entity.getType()))
		{
			this.capability = CAPABILITY_BY_TYPE.get(entity.getType()).get();
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