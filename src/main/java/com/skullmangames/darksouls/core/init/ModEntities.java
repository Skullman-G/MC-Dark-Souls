package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.entity.AsylumDemonEntity;
import com.skullmangames.darksouls.common.entity.CrestfallenWarrior;
import com.skullmangames.darksouls.common.entity.FireKeeperEntity;
import com.skullmangames.darksouls.common.entity.Hollow;
import com.skullmangames.darksouls.common.entity.SoulEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities
{
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, DarkSouls.MOD_ID);

	public static final RegistryObject<EntityType<FireKeeperEntity>> FIRE_KEEPER = register("fire_keeper",
			EntityType.Builder.<FireKeeperEntity>of(FireKeeperEntity::new, MobCategory.CREATURE)
					.sized(0.6F, 1.95F)
					.canSpawnFarFromPlayer());

	public static final RegistryObject<EntityType<Hollow>> HOLLOW = register("hollow",
			EntityType.Builder.<Hollow>of(Hollow::new, MobCategory.MONSTER)
					.sized(0.6F, 1.95F));

	public static final RegistryObject<EntityType<SoulEntity>> SOUL = register("soul",
			EntityType.Builder.<SoulEntity>of(SoulEntity::new, MobCategory.MISC)
					.sized(0.5F, 0.5F)
					.clientTrackingRange(6)
					.updateInterval(20));

	public static final RegistryObject<EntityType<AsylumDemonEntity>> ASYLUM_DEMON = register("asylum_demon",
			EntityType.Builder.<AsylumDemonEntity>of(AsylumDemonEntity::new, MobCategory.MONSTER)
					.sized(3.5F, 7.7F)
					.canSpawnFarFromPlayer());
	
	public static final RegistryObject<EntityType<CrestfallenWarrior>> CRESTFALLEN_WARRIOR = register("crestfallen_warrior",
			EntityType.Builder.<CrestfallenWarrior>of(CrestfallenWarrior::new, MobCategory.CREATURE)
				.sized(0.6F, 1.95F)
				.canSpawnFarFromPlayer());
	
	public static <T extends Entity> RegistryObject<EntityType<T>> register(String name, EntityType.Builder<T> builder)
	{
		return ENTITIES.register(name, () -> builder.build(new ResourceLocation(DarkSouls.MOD_ID, name).toString()));
	}

	public static void registerEntitySpawnPlacement()
	{
		SpawnPlacements.register(HOLLOW.get(), SpawnPlacements.Type.ON_GROUND,
				Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Hollow::checkSpawnRules);
	}
}
