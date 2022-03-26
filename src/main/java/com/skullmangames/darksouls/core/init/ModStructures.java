package com.skullmangames.darksouls.core.init;

import java.util.function.Supplier;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.world.structures.CheckpointPlainsStructure;
import com.skullmangames.darksouls.common.world.structures.UndeadAsylumStructure;

import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModStructures
{
	public static final DeferredRegister<StructureFeature<?>> STRUCTURES = DeferredRegister
			.create(ForgeRegistries.STRUCTURE_FEATURES, DarkSouls.MOD_ID);

	public static final RegistryObject<StructureFeature<JigsawConfiguration>> CHECKPOINT_PLAINS = register("checkpoint_plains", Decoration.SURFACE_STRUCTURES,
			() -> new CheckpointPlainsStructure(JigsawConfiguration.CODEC));
	public static final RegistryObject<StructureFeature<JigsawConfiguration>> UNDEAD_ASYLUM = register("undead_asylum", Decoration.SURFACE_STRUCTURES,
			() -> new UndeadAsylumStructure(JigsawConfiguration.CODEC));
	
	private static <T extends FeatureConfiguration>RegistryObject<StructureFeature<T>> register(String name, Decoration deco,
			Supplier<StructureFeature<T>> sup)
	{
		StructureFeature.STEP.put(sup.get(), deco);
		return STRUCTURES.register(name, sup);
	}
}
