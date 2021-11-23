package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.entity.AsylumDemonEntity;
import com.skullmangames.darksouls.common.entity.FireKeeperEntity;
import com.skullmangames.darksouls.common.entity.HollowEntity;
import com.skullmangames.darksouls.common.entity.SoulEntity;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEntities
{
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, DarkSouls.MOD_ID);
	
	public static final RegistryObject<EntityType<FireKeeperEntity>> FIRE_KEEPER = ENTITIES.register("fire_keeper", () -> EntityType.Builder.<FireKeeperEntity>of(FireKeeperEntity::new, EntityClassification.CREATURE)
			.sized(0.6F, 1.95F)
			.canSpawnFarFromPlayer()
			.build(new ResourceLocation(DarkSouls.MOD_ID, "fire_keeper").toString()));
	
	public static final RegistryObject<EntityType<HollowEntity>> HOLLOW = ENTITIES.register("hollow", () -> EntityType.Builder.<HollowEntity>of(HollowEntity::new, EntityClassification.MONSTER)
			.sized(0.6F, 1.95F)
			.build(new ResourceLocation(DarkSouls.MOD_ID, "hollow").toString()));
	
	public static final RegistryObject<EntityType<SoulEntity>> SOUL = ENTITIES.register("soul", () -> EntityType.Builder.<SoulEntity>of(SoulEntity::new, EntityClassification.MISC)
			.sized(0.5F, 0.5F)
			.clientTrackingRange(6)
			.updateInterval(20)
			.build(new ResourceLocation(DarkSouls.MOD_ID, "soul").toString()));
	
	public static final RegistryObject<EntityType<AsylumDemonEntity>> ASYLUM_DEMON = ENTITIES.register("asylum_demon", () -> EntityType.Builder.<AsylumDemonEntity>of(AsylumDemonEntity::new, EntityClassification.MONSTER)
			.sized(2.5F, 5.5F)
			.canSpawnFarFromPlayer()
			.build(new ResourceLocation(DarkSouls.MOD_ID, "asylum_demon").toString()));
	
	
	public static void registerEntitySpawnPlacement()
	{
		EntitySpawnPlacementRegistry.register(HOLLOW.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HollowEntity::checkSpawnRules);
	}
}