package com.skullmangames.darksouls.core.init;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.AsylumDemonData;
import com.skullmangames.darksouls.common.capability.entity.ClientPlayerData;
import com.skullmangames.darksouls.common.capability.entity.EntityData;
import com.skullmangames.darksouls.common.capability.entity.HollowData;
import com.skullmangames.darksouls.common.capability.entity.RemoteClientPlayerData;
import com.skullmangames.darksouls.common.capability.entity.ServerPlayerData;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;

public class ProviderEntity implements ICapabilityProvider, NonNullSupplier<EntityData<?>>
{
	private static final Map<EntityType<?>, Function<Entity, Supplier<EntityData<?>>>> capabilityMap = new HashMap<EntityType<?>, Function<Entity, Supplier<EntityData<?>>>> ();
	
	public static void makeMap()
	{
		capabilityMap.put(EntityType.PLAYER, (entityIn) -> ServerPlayerData::new);
		capabilityMap.put(EntityTypeInit.HOLLOW.get(), (entityIn) -> HollowData::new);
		capabilityMap.put(EntityTypeInit.ASYLUM_DEMON.get(), (entityIn) -> AsylumDemonData::new);
		/*capabilityMap.put(EntityType.ZOMBIE, (entityIn) -> ZombieData<ZombieEntity>::new);
		capabilityMap.put(EntityType.CREEPER, (entityIn) -> CreeperData::new);
		capabilityMap.put(EntityType.ENDERMAN, (entityIn) -> EndermanData::new);
		capabilityMap.put(EntityType.SKELETON, (entityIn) -> SkeletonData<SkeletonEntity>::new);
		capabilityMap.put(EntityType.WITHER_SKELETON, (entityIn) -> WitherSkeletonData::new);
		capabilityMap.put(EntityType.STRAY, (entityIn) -> StrayData::new);
		capabilityMap.put(EntityType.ZOMBIFIED_PIGLIN, (entityIn) -> ZombifiedPiglinData::new);
		capabilityMap.put(EntityType.ZOMBIE_VILLAGER, (entityIn) -> ZombieVillagerData::new);
		capabilityMap.put(EntityType.HUSK, (entityIn) -> ZombieData<HuskEntity>::new);
		capabilityMap.put(EntityType.SPIDER, (entityIn) -> SpiderData::new);
		capabilityMap.put(EntityType.CAVE_SPIDER, (entityIn) -> CaveSpiderData::new);
		capabilityMap.put(EntityType.IRON_GOLEM, (entityIn) -> IronGolemData::new);
		capabilityMap.put(EntityType.VINDICATOR, (entityIn) -> VindicatorData::new);
		capabilityMap.put(EntityType.EVOKER, (entityIn) -> EvokerData::new);
		capabilityMap.put(EntityType.WITCH, (entityIn) -> WitchData::new);
		capabilityMap.put(EntityType.DROWNED, (entityIn) -> DrownedData::new);
		capabilityMap.put(EntityType.PILLAGER, (entityIn) -> PillagerData::new);
		capabilityMap.put(EntityType.RAVAGER, (entityIn) -> RavagerData::new);
		capabilityMap.put(EntityType.VEX, (entityIn) -> VexData::new);
		capabilityMap.put(EntityType.PIGLIN, (entityIn) -> PiglinData::new);
		capabilityMap.put(EntityType.PIGLIN_BRUTE, (entityIn) -> PiglinBruteData::new);
		capabilityMap.put(EntityType.HOGLIN, (entityIn) -> HoglinData::new);
		capabilityMap.put(EntityType.ZOGLIN, (entityIn) -> ZoglinData::new);*/
	}
	
	public static void makeMapClient()
	{
		capabilityMap.put(EntityType.PLAYER, (entityIn) ->
		{
			if(entityIn instanceof ClientPlayerEntity)
			{
				return ClientPlayerData::new;
			}
			else if (entityIn instanceof RemoteClientPlayerEntity)
			{
				return RemoteClientPlayerData<RemoteClientPlayerEntity>::new;
			}
			else if (entityIn instanceof ServerPlayerEntity)
			{
				return ServerPlayerData::new;
			}
			else
			{
				return ()->null;
			}
		});
	}
	
	private EntityData<?> capability;
	private LazyOptional<EntityData<?>> optional = LazyOptional.of(this);
	
	public ProviderEntity(Entity entity)
	{
		if(capabilityMap.containsKey(entity.getType()))
		{
			capability = capabilityMap.get(entity.getType()).apply(entity).get();
		}
	}
	
	public boolean hasCapability()
	{
		return capability != null;
	}
	
	@Override
	public EntityData<?> get()
	{
		return this.capability;
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
	{
		return cap == ModCapabilities.CAPABILITY_ENTITY ? optional.cast() :  LazyOptional.empty();
	}
}