package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.entity.AnastaciaOfAstora;
import com.skullmangames.darksouls.common.entity.StrayDemon;
import com.skullmangames.darksouls.common.entity.projectile.LightningSpear;
import com.skullmangames.darksouls.common.entity.CrestfallenWarrior;
import com.skullmangames.darksouls.common.entity.FireKeeper;
import com.skullmangames.darksouls.common.entity.Hollow;
import com.skullmangames.darksouls.common.entity.HollowLordranSoldier;
import com.skullmangames.darksouls.common.entity.HollowLordranWarrior;
import com.skullmangames.darksouls.common.entity.HumanityEntity;
import com.skullmangames.darksouls.common.entity.PetrusOfThorolund;
import com.skullmangames.darksouls.common.entity.SoulEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.common.world.MobSpawnSettingsBuilder;

public class ModEntities
{
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, DarkSouls.MOD_ID);

	public static final RegistryObject<EntityType<FireKeeper>> FIRE_KEEPER = register("fire_keeper",
			EntityType.Builder.<FireKeeper>of(FireKeeper::new, MobCategory.CREATURE)
				.sized(0.6F, 1.95F)
				.canSpawnFarFromPlayer());

	public static final RegistryObject<EntityType<Hollow>> HOLLOW = register("hollow",
			EntityType.Builder.<Hollow>of(Hollow::new, MobCategory.MONSTER)
				.sized(0.6F, 1.95F));
	
	public static final RegistryObject<EntityType<HollowLordranWarrior>> HOLLOW_LORDRAN_WARRIOR = register("hollow_lordran_warrior",
			EntityType.Builder.<HollowLordranWarrior>of(HollowLordranWarrior::new, MobCategory.MONSTER)
			.sized(0.6F, 1.95F));
	
	public static final RegistryObject<EntityType<HollowLordranSoldier>> HOLLOW_LORDRAN_SOLDIER = register("hollow_lordran_soldier",
			EntityType.Builder.<HollowLordranSoldier>of(HollowLordranSoldier::new, MobCategory.MONSTER)
			.sized(0.6F, 1.95F));

	public static final RegistryObject<EntityType<SoulEntity>> SOUL = register("soul",
			EntityType.Builder.<SoulEntity>of(SoulEntity::new, MobCategory.MISC)
				.sized(0.5F, 0.5F)
				.clientTrackingRange(6)
				.updateInterval(20));
	
	public static final RegistryObject<EntityType<HumanityEntity>> HUMANITY = register("humanity",
			EntityType.Builder.<HumanityEntity>of(HumanityEntity::new, MobCategory.MISC)
				.sized(0.5F, 0.5F)
				.clientTrackingRange(6)
				.updateInterval(20));

	public static final RegistryObject<EntityType<StrayDemon>> STRAY_DEMON = register("stray_demon",
			EntityType.Builder.<StrayDemon>of(StrayDemon::new, MobCategory.MONSTER)
				.sized(3.5F, 7.7F)
				.canSpawnFarFromPlayer());
	
	public static final RegistryObject<EntityType<CrestfallenWarrior>> CRESTFALLEN_WARRIOR = register("crestfallen_warrior",
			EntityType.Builder.<CrestfallenWarrior>of(CrestfallenWarrior::new, MobCategory.CREATURE)
				.sized(0.6F, 1.95F)
				.canSpawnFarFromPlayer());
	
	public static final RegistryObject<EntityType<AnastaciaOfAstora>> ANASTACIA_OF_ASTORA = register("anastacia_of_astora",
			EntityType.Builder.<AnastaciaOfAstora>of(AnastaciaOfAstora::new, MobCategory.CREATURE)
				.sized(0.6F, 1.95F)
				.canSpawnFarFromPlayer());
	
	public static final RegistryObject<EntityType<PetrusOfThorolund>> PETRUS_OF_THOROLUND = register("petrus_of_thorolund",
			EntityType.Builder.<PetrusOfThorolund>of(PetrusOfThorolund::new, MobCategory.CREATURE)
				.sized(0.6F, 1.95F)
				.canSpawnFarFromPlayer());
	
	//Projectiles
	public static final RegistryObject<EntityType<LightningSpear>> LIGHTNING_SPEAR = register("lightning_spear",
			EntityType.Builder.<LightningSpear>of(LightningSpear::lightningSpear, MobCategory.MISC)
			.sized(1.5F, 0.5F)
			.clientTrackingRange(6)
			.updateInterval(20));
	
	public static final RegistryObject<EntityType<LightningSpear>> GREAT_LIGHTNING_SPEAR = register("great_lightning_spear",
			EntityType.Builder.<LightningSpear>of(LightningSpear::greatLightningSpear, MobCategory.MISC)
			.sized(2.0F, 1.0F));
	
	public static <T extends Entity> RegistryObject<EntityType<T>> register(String name, EntityType.Builder<T> builder)
	{
		return ENTITIES.register(name, () -> builder.build(new ResourceLocation(DarkSouls.MOD_ID, name).toString()));
	}

	public static void registerEntitySpawnPlacement()
	{
		SpawnPlacements.register(HOLLOW.get(), SpawnPlacements.Type.ON_GROUND,
				Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Hollow::checkSpawnRules);
		SpawnPlacements.register(STRAY_DEMON.get(), SpawnPlacements.Type.ON_GROUND,
				Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, StrayDemon::checkSpawnRules);
	}
	
	public static void addEntitySpawns(final BiomeLoadingEvent event)
	{
		MobSpawnSettingsBuilder s = event.getSpawns();
		
		if (event.getClimate().temperature > 0.5F)
		{
			s.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.HOLLOW.get(), 10, 1, 2));
			s.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.STRAY_DEMON.get(), 1, 1, 1));
		}
	}
}
