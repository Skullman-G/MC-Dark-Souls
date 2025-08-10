package com.skullmangames.darksouls.core.util.collider;

import net.minecraft.resources.ResourceLocation;

public class ColliderType<T extends Collider>
{
	private final ResourceLocation id;
	private final ColliderFactory<T> factory;
	
	public ColliderType(ResourceLocation id, ColliderFactory<T> factory)
	{
		this.id = id;
		this.factory = factory;
	}
	
	public ResourceLocation getId()
	{
		return this.id;
	}
	
	public T create()
	{
		return this.factory.create(this);
	}
	
	@FunctionalInterface
	public interface ColliderFactory<T extends Collider>
	{
		public T create(ColliderType<T> type);
	}
}
