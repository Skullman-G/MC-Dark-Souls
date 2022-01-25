package com.skullmangames.darksouls.core.init;

import java.util.HashMap;
import java.util.Map;
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
	private static final Map<EntityType<?>, CapabilityProjectile<?>> CAPABILITIES = new HashMap<> ();
	
	public static void makeMap()
	{
		CAPABILITIES.put(EntityType.ARROW, new CapabilityProjectile<Arrow>(20F));
		CAPABILITIES.put(EntityType.SNOWBALL, new CapabilityProjectile<Snowball>(5F));
		CAPABILITIES.put(EntityType.EGG, new CapabilityProjectile<ThrownEgg>(5F));
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