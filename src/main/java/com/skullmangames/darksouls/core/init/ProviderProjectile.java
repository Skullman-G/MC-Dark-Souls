package com.skullmangames.darksouls.core.init;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.projectile.ProjectileCapability;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;

public class ProviderProjectile<P extends Projectile> implements ICapabilityProvider, NonNullSupplier<ProjectileCapability<?>>
{
	public static final Map<EntityType<?>, Supplier<ProjectileCapability<?>>> CAPABILITIES = new HashMap<>();
	
	private ProjectileCapability<?> capability;
	private LazyOptional<ProjectileCapability<?>> optional = LazyOptional.of(this);
	
	public ProviderProjectile(P entity)
	{
		if(CAPABILITIES.containsKey(entity.getType()))
		{
			this.capability = CAPABILITIES.get(entity.getType()).get();
		}
	}
	
	public boolean hasCapability()
	{
		return this.capability != null;
	}
	
	@Override
	public ProjectileCapability<?> get()
	{
		return this.capability;
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
	{
		return cap == ModCapabilities.CAPABILITY_PROJECTILE ? this.optional.cast() : LazyOptional.empty();
	}
}