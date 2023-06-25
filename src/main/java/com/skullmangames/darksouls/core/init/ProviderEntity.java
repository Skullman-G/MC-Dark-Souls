package com.skullmangames.darksouls.core.init;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.StrayDemonCap;
import com.skullmangames.darksouls.common.capability.entity.TaurusDemonCap;
import com.skullmangames.darksouls.common.capability.entity.LocalPlayerCap;
import com.skullmangames.darksouls.common.capability.entity.EntityCapability;
import com.skullmangames.darksouls.common.capability.entity.FalconerCap;
import com.skullmangames.darksouls.common.capability.entity.FireKeeperCap;
import com.skullmangames.darksouls.common.capability.entity.HollowCap;
import com.skullmangames.darksouls.common.capability.entity.HollowLordranSoldierCap;
import com.skullmangames.darksouls.common.capability.entity.HollowLordranWarriorCap;
import com.skullmangames.darksouls.common.capability.entity.AbstractClientPlayerCap;
import com.skullmangames.darksouls.common.capability.entity.AnastaciaOfAstoraCap;
import com.skullmangames.darksouls.common.capability.entity.ArmorStandCap;
import com.skullmangames.darksouls.common.capability.entity.BlackKnightCap;
import com.skullmangames.darksouls.common.capability.entity.ServerPlayerCap;
import com.skullmangames.darksouls.common.capability.entity.SimpleHumanoidCap;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;

public class ProviderEntity implements ICapabilityProvider, NonNullSupplier<EntityCapability<?>>
{
	private static final Map<EntityType<?>, Function<Entity, Supplier<EntityCapability<?>>>> CAPABILITIES = new HashMap<EntityType<?>, Function<Entity, Supplier<EntityCapability<?>>>>();
	
	public static void makeMap()
	{
		CAPABILITIES.put(EntityType.PLAYER, (entity) -> ServerPlayerCap::new);
		CAPABILITIES.put(ModEntities.HOLLOW.get(), (entity) -> HollowCap::new);
		CAPABILITIES.put(ModEntities.HOLLOW_LORDRAN_WARRIOR.get(), (entity) -> HollowLordranWarriorCap::new);
		CAPABILITIES.put(ModEntities.HOLLOW_LORDRAN_SOLDIER.get(), (entity) -> HollowLordranSoldierCap::new);
		CAPABILITIES.put(ModEntities.STRAY_DEMON.get(), (entity) -> StrayDemonCap::new);
		CAPABILITIES.put(ModEntities.CRESTFALLEN_WARRIOR.get(), (entity) -> SimpleHumanoidCap::new);
		CAPABILITIES.put(ModEntities.ANASTACIA_OF_ASTORA.get(), (entity) -> AnastaciaOfAstoraCap::new);
		CAPABILITIES.put(ModEntities.FIRE_KEEPER.get(), (entity) -> FireKeeperCap::new);
		CAPABILITIES.put(ModEntities.PETRUS_OF_THOROLUND.get(), (entity) -> SimpleHumanoidCap::new);
		CAPABILITIES.put(ModEntities.FALCONER.get(), (entity) -> FalconerCap::new);
		CAPABILITIES.put(ModEntities.BLACK_KNIGHT.get(), (entity) -> BlackKnightCap::new);
		CAPABILITIES.put(ModEntities.TAURUS_DEMON.get(), (entity) -> TaurusDemonCap::new);
		
		CAPABILITIES.put(EntityType.ARMOR_STAND, (entity) -> ArmorStandCap::new);
		CAPABILITIES.put(EntityType.ZOMBIE, (entity) -> SimpleHumanoidCap::new);
		CAPABILITIES.put(EntityType.HUSK, (entity) -> SimpleHumanoidCap::new);
		CAPABILITIES.put(EntityType.DROWNED, (entity) -> SimpleHumanoidCap::new);
	}
	
	public static void makeMapClient()
	{
		CAPABILITIES.put(EntityType.PLAYER, (entityIn) ->
		{
			if(entityIn instanceof LocalPlayer)
			{
				return LocalPlayerCap::new;
			}
			else if (entityIn instanceof RemotePlayer)
			{
				return AbstractClientPlayerCap<RemotePlayer>::new;
			}
			else if (entityIn instanceof ServerPlayer)
			{
				return ServerPlayerCap::new;
			}
			else
			{
				return ()->null;
			}
		});
	}
	
	private EntityCapability<?> capability;
	private LazyOptional<EntityCapability<?>> optional = LazyOptional.of(this);
	
	public ProviderEntity(Entity entity)
	{
		if(CAPABILITIES.containsKey(entity.getType()))
		{
			capability = CAPABILITIES.get(entity.getType()).apply(entity).get();
		}
	}
	
	public boolean hasCapability()
	{
		return capability != null;
	}
	
	@Override
	public EntityCapability<?> get()
	{
		return this.capability;
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
	{
		return cap == ModCapabilities.CAPABILITY_ENTITY ? optional.cast() :  LazyOptional.empty();
	}
}