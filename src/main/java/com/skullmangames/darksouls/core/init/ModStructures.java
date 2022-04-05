package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.structures.FireKeeperRuins;
import com.skullmangames.darksouls.common.structures.LordranCamp;

import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModStructures
{
	public static final DeferredRegister<StructureFeature<?>> STRUCTURES = DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, DarkSouls.MOD_ID);

	public static final RegistryObject<StructureFeature<?>> FIRE_KEEPER_RUINS = STRUCTURES.register("fire_keeper_ruins", FireKeeperRuins::new);
	public static final RegistryObject<StructureFeature<?>> LORDRAN_CAMP = STRUCTURES.register("lordran_camp", LordranCamp::new);
}
