package com.skullmangames.darksouls.core.data;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.common.capability.item.Shield.ShieldType;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap.WeaponMaterial;
import com.skullmangames.darksouls.common.capability.item.WeaponCap.Scaling;
import com.skullmangames.darksouls.common.entity.stats.Stats;
import com.skullmangames.darksouls.core.init.AuxEffects;
import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.core.init.WeaponMovesets;
import com.skullmangames.darksouls.core.init.WeaponSkills;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.CoreDamageType;
import com.skullmangames.darksouls.core.util.WeaponCategory;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

public class MeleeWeaponConfigProvider implements DataProvider
{
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
	private final DataGenerator generator;
	
	public MeleeWeaponConfigProvider(DataGenerator generator)
	{
		this.generator = generator;
	}
	@Override
	public void run(HashCache cache) throws IOException
	{
		Path path = this.generator.getOutputFolder();
		
		for (MeleeWeaponCap.Builder builder : defaultConfigs())
		{
			Path path1 = createPath(path, builder.getLocation());
			try
			{
				DataProvider.save(GSON, cache, builder.toJson(), path1);
			}
			catch (IOException ioexception)
			{
				LOGGER.error("Couldn't save melee weapon config {}", path1, ioexception);
			}
		}
	}
	
	private static List<MeleeWeaponCap.Builder> defaultConfigs()
	{
		return ImmutableList.of
		(
			// Greathammers
			MeleeWeaponCap.builder(ModItems.DEMON_GREAT_HAMMER.get(), WeaponCategory.GREAT_HAMMER, WeaponMovesets.GREAT_HAMMER, Colliders.GREAT_HAMMER.getId(), 22.0F)
			.putDamageInfo(CoreDamageType.PHYSICAL, 138, 0.50F)
			.putDamageInfo(CoreDamageType.MAGIC, 0, 0.10F)
			.putDamageInfo(CoreDamageType.FIRE, 0, 0.35F)
			.putDamageInfo(CoreDamageType.LIGHTNING, 0, 0.35F)
			.putDamageInfo(CoreDamageType.DARK, 0, 0.35F)
			.putDamageInfo(CoreDamageType.HOLY, 0, 0.35F)
			.setCritical(1.00F)
			.putStatInfo(Stats.STRENGTH, 46, Scaling.B)
			.putStatInfo(Stats.DEXTERITY, 0, Scaling.NONE)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setStability(0.32F)
			.setWeaponMaterial(WeaponMaterial.WOODEN_WEAPON),
			
			// Axes
			MeleeWeaponCap.builder(Items.WOODEN_AXE, WeaponCategory.AXE, WeaponMovesets.AXE, Colliders.AXE.getId(), 0.75F)
			.putDamageInfo(CoreDamageType.PHYSICAL, 110, 0.55F)
			.putDamageInfo(CoreDamageType.MAGIC, 0, 0.10F)
			.putDamageInfo(CoreDamageType.FIRE, 0, 0.40F)
			.putDamageInfo(CoreDamageType.LIGHTNING, 0, 0.40F)
			.putDamageInfo(CoreDamageType.DARK, 0, 0.30F)
			.putDamageInfo(CoreDamageType.HOLY, 0, 0.30F)
			.setCritical(1.00F)
			.putStatInfo(Stats.STRENGTH, 8, Scaling.D)
			.putStatInfo(Stats.DEXTERITY, 8, Scaling.D)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setStability(0.30F)
			.setWeaponMaterial(WeaponMaterial.WOODEN_WEAPON),
			
			MeleeWeaponCap.builder(Items.STONE_AXE, WeaponCategory.AXE, WeaponMovesets.AXE, Colliders.AXE.getId(), 0.8F)
			.putDamageInfo(CoreDamageType.PHYSICAL, 110, 0.55F)
			.putDamageInfo(CoreDamageType.MAGIC, 0, 0.10F)
			.putDamageInfo(CoreDamageType.FIRE, 0, 0.40F)
			.putDamageInfo(CoreDamageType.LIGHTNING, 0, 0.40F)
			.putDamageInfo(CoreDamageType.DARK, 0, 0.30F)
			.putDamageInfo(CoreDamageType.HOLY, 0, 0.30F)
			.setCritical(1.00F)
			.putStatInfo(Stats.STRENGTH, 8, Scaling.D)
			.putStatInfo(Stats.DEXTERITY, 8, Scaling.D)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setStability(0.30F)
			.setWeaponMaterial(WeaponMaterial.WOODEN_WEAPON),
			
			MeleeWeaponCap.builder(Items.IRON_AXE, WeaponCategory.AXE, WeaponMovesets.AXE, Colliders.AXE.getId(), 1.0F)
			.putDamageInfo(CoreDamageType.PHYSICAL, 110, 0.55F)
			.putDamageInfo(CoreDamageType.MAGIC, 0, 0.10F)
			.putDamageInfo(CoreDamageType.FIRE, 0, 0.40F)
			.putDamageInfo(CoreDamageType.LIGHTNING, 0, 0.40F)
			.putDamageInfo(CoreDamageType.DARK, 0, 0.30F)
			.putDamageInfo(CoreDamageType.HOLY, 0, 0.30F)
			.setCritical(1.00F)
			.putStatInfo(Stats.STRENGTH, 8, Scaling.D)
			.putStatInfo(Stats.DEXTERITY, 8, Scaling.D)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setStability(0.30F)
			.setWeaponMaterial(WeaponMaterial.WOODEN_WEAPON),
			
			MeleeWeaponCap.builder(Items.GOLDEN_AXE, WeaponCategory.AXE, WeaponMovesets.AXE, Colliders.AXE.getId(), 1.0F)
			.putDamageInfo(CoreDamageType.PHYSICAL, 110, 0.55F)
			.putDamageInfo(CoreDamageType.MAGIC, 0, 0.10F)
			.putDamageInfo(CoreDamageType.FIRE, 0, 0.40F)
			.putDamageInfo(CoreDamageType.LIGHTNING, 0, 0.40F)
			.putDamageInfo(CoreDamageType.DARK, 0, 0.30F)
			.putDamageInfo(CoreDamageType.HOLY, 0, 0.30F)
			.setCritical(1.00F)
			.putStatInfo(Stats.STRENGTH, 8, Scaling.D)
			.putStatInfo(Stats.DEXTERITY, 8, Scaling.D)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setStability(0.30F)
			.setWeaponMaterial(WeaponMaterial.WOODEN_WEAPON),
			
			MeleeWeaponCap.builder(Items.DIAMOND_AXE, WeaponCategory.AXE, WeaponMovesets.AXE, Colliders.AXE.getId(), 1.0F)
			.putDamageInfo(CoreDamageType.PHYSICAL, 110, 0.55F)
			.putDamageInfo(CoreDamageType.MAGIC, 0, 0.10F)
			.putDamageInfo(CoreDamageType.FIRE, 0, 0.40F)
			.putDamageInfo(CoreDamageType.LIGHTNING, 0, 0.40F)
			.putDamageInfo(CoreDamageType.DARK, 0, 0.30F)
			.putDamageInfo(CoreDamageType.HOLY, 0, 0.30F)
			.setCritical(1.00F)
			.putStatInfo(Stats.STRENGTH, 8, Scaling.D)
			.putStatInfo(Stats.DEXTERITY, 8, Scaling.D)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setStability(0.30F)
			.setWeaponMaterial(WeaponMaterial.WOODEN_WEAPON),
			
			MeleeWeaponCap.builder(Items.NETHERITE_AXE, WeaponCategory.AXE, WeaponMovesets.AXE, Colliders.AXE.getId(), 1.0F)
			.putDamageInfo(CoreDamageType.PHYSICAL, 150, 0.55F)
			.putDamageInfo(CoreDamageType.MAGIC, 0, 0.10F)
			.putDamageInfo(CoreDamageType.FIRE, 0, 0.40F)
			.putDamageInfo(CoreDamageType.LIGHTNING, 0, 0.40F)
			.putDamageInfo(CoreDamageType.DARK, 0, 0.30F)
			.putDamageInfo(CoreDamageType.HOLY, 0, 0.30F)
			.setCritical(1.00F)
			.putStatInfo(Stats.STRENGTH, 8, Scaling.D)
			.putStatInfo(Stats.DEXTERITY, 8, Scaling.D)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setStability(0.30F)
			.setWeaponMaterial(WeaponMaterial.WOODEN_WEAPON),
			
			MeleeWeaponCap.builder(ModItems.BATTLE_AXE.get(), WeaponCategory.AXE, WeaponMovesets.AXE, Colliders.BATTLE_AXE.getId(), 4.0F)
			.putDamageInfo(CoreDamageType.PHYSICAL, 125, 0.45F)
			.putDamageInfo(CoreDamageType.MAGIC, 0, 0.30F)
			.putDamageInfo(CoreDamageType.FIRE, 0, 0.25F)
			.putDamageInfo(CoreDamageType.LIGHTNING, 0, 0.25F)
			.putDamageInfo(CoreDamageType.DARK, 0, 0.30F)
			.putDamageInfo(CoreDamageType.HOLY, 0, 0.30F)
			.setCritical(1.00F)
			.putStatInfo(Stats.STRENGTH, 12, Scaling.D)
			.putStatInfo(Stats.DEXTERITY, 8, Scaling.D)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setStability(0.30F)
			.setWeaponMaterial(WeaponMaterial.METAL_WEAPON),
			
			// Hammers
			MeleeWeaponCap.builder(Items.WOODEN_PICKAXE, WeaponCategory.HAMMER, WeaponMovesets.HAMMER, Colliders.PICKAXE.getId(), 5.0F)
			.putDamageInfo(CoreDamageType.PHYSICAL, 140, 0.50F)
			.putDamageInfo(CoreDamageType.MAGIC, 0, 0.35F)
			.putDamageInfo(CoreDamageType.FIRE, 0, 0.30F)
			.putDamageInfo(CoreDamageType.LIGHTNING, 0, 0.30F)
			.putDamageInfo(CoreDamageType.DARK, 0, 0.35F)
			.putDamageInfo(CoreDamageType.HOLY, 0, 0.30F)
			.setCritical(1.00F)
			.setStability(0.40F)
			.putStatInfo(Stats.STRENGTH, 18, Scaling.C)
			.putStatInfo(Stats.DEXTERITY, 9, Scaling.NONE)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setWeaponMaterial(WeaponMaterial.WOODEN_WEAPON),
			
			MeleeWeaponCap.builder(Items.STONE_PICKAXE, WeaponCategory.HAMMER, WeaponMovesets.HAMMER, Colliders.PICKAXE.getId(), 5.0F)
			.putDamageInfo(CoreDamageType.PHYSICAL, 140, 0.50F)
			.putDamageInfo(CoreDamageType.MAGIC, 0, 0.35F)
			.putDamageInfo(CoreDamageType.FIRE, 0, 0.30F)
			.putDamageInfo(CoreDamageType.LIGHTNING, 0, 0.30F)
			.putDamageInfo(CoreDamageType.DARK, 0, 0.35F)
			.putDamageInfo(CoreDamageType.HOLY, 0, 0.30F)
			.setCritical(1.00F)
			.setStability(0.40F)
			.putStatInfo(Stats.STRENGTH, 18, Scaling.C)
			.putStatInfo(Stats.DEXTERITY, 9, Scaling.NONE)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setWeaponMaterial(WeaponMaterial.STONE_WEAPON),
			
			MeleeWeaponCap.builder(Items.IRON_PICKAXE, WeaponCategory.HAMMER, WeaponMovesets.HAMMER, Colliders.PICKAXE.getId(), 5.0F)
			.putDamageInfo(CoreDamageType.PHYSICAL, 140, 0.50F)
			.putDamageInfo(CoreDamageType.MAGIC, 0, 0.35F)
			.putDamageInfo(CoreDamageType.FIRE, 0, 0.30F)
			.putDamageInfo(CoreDamageType.LIGHTNING, 0, 0.30F)
			.putDamageInfo(CoreDamageType.DARK, 0, 0.35F)
			.putDamageInfo(CoreDamageType.HOLY, 0, 0.30F)
			.setCritical(1.00F)
			.setStability(0.40F)
			.putStatInfo(Stats.STRENGTH, 18, Scaling.C)
			.putStatInfo(Stats.DEXTERITY, 9, Scaling.NONE)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setWeaponMaterial(WeaponMaterial.METAL_WEAPON),
			
			MeleeWeaponCap.builder(Items.GOLDEN_PICKAXE, WeaponCategory.HAMMER, WeaponMovesets.HAMMER, Colliders.PICKAXE.getId(), 5.0F)
			.putDamageInfo(CoreDamageType.PHYSICAL, 140, 0.50F)
			.putDamageInfo(CoreDamageType.MAGIC, 0, 0.35F)
			.putDamageInfo(CoreDamageType.FIRE, 0, 0.30F)
			.putDamageInfo(CoreDamageType.LIGHTNING, 0, 0.30F)
			.putDamageInfo(CoreDamageType.DARK, 0, 0.35F)
			.putDamageInfo(CoreDamageType.HOLY, 0, 0.30F)
			.setCritical(1.00F)
			.setStability(0.40F)
			.putStatInfo(Stats.STRENGTH, 18, Scaling.C)
			.putStatInfo(Stats.DEXTERITY, 9, Scaling.NONE)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setWeaponMaterial(WeaponMaterial.METAL_WEAPON),
			
			MeleeWeaponCap.builder(Items.DIAMOND_PICKAXE, WeaponCategory.HAMMER, WeaponMovesets.HAMMER, Colliders.PICKAXE.getId(), 5.0F)
			.putDamageInfo(CoreDamageType.PHYSICAL, 140, 0.50F)
			.putDamageInfo(CoreDamageType.MAGIC, 0, 0.35F)
			.putDamageInfo(CoreDamageType.FIRE, 0, 0.30F)
			.putDamageInfo(CoreDamageType.LIGHTNING, 0, 0.30F)
			.putDamageInfo(CoreDamageType.DARK, 0, 0.35F)
			.putDamageInfo(CoreDamageType.HOLY, 0, 0.30F)
			.setCritical(1.00F)
			.setStability(0.40F)
			.putStatInfo(Stats.STRENGTH, 18, Scaling.C)
			.putStatInfo(Stats.DEXTERITY, 9, Scaling.NONE)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setWeaponMaterial(WeaponMaterial.METAL_WEAPON),
			
			MeleeWeaponCap.builder(Items.NETHERITE_PICKAXE, WeaponCategory.HAMMER, WeaponMovesets.HAMMER, Colliders.PICKAXE.getId(), 5.0F)
			.putDamageInfo(CoreDamageType.PHYSICAL, 140, 0.50F)
			.putDamageInfo(CoreDamageType.MAGIC, 0, 0.35F)
			.putDamageInfo(CoreDamageType.FIRE, 0, 0.30F)
			.putDamageInfo(CoreDamageType.LIGHTNING, 0, 0.30F)
			.putDamageInfo(CoreDamageType.DARK, 0, 0.35F)
			.putDamageInfo(CoreDamageType.HOLY, 0, 0.35F)
			.setCritical(1.00F)
			.setStability(0.40F)
			.putStatInfo(Stats.STRENGTH, 18, Scaling.C)
			.putStatInfo(Stats.DEXTERITY, 9, Scaling.NONE)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setWeaponMaterial(WeaponMaterial.METAL_WEAPON),
			
			MeleeWeaponCap.builder(ModItems.MACE.get(), WeaponCategory.HAMMER, WeaponMovesets.HAMMER, Colliders.MACE.getId(), 5.0F)
			.putDamageInfo(CoreDamageType.PHYSICAL, 115, 0.45F)
			.putDamageInfo(CoreDamageType.MAGIC, 0, 0.30F)
			.putDamageInfo(CoreDamageType.FIRE, 0, 0.25F)
			.putDamageInfo(CoreDamageType.LIGHTNING, 0, 0.25F)
			.putDamageInfo(CoreDamageType.DARK, 0, 0.30F)
			.putDamageInfo(CoreDamageType.HOLY, 0, 0.30F)
			.setCritical(1.00F)
			.setStability(0.30F)
			.putStatInfo(Stats.STRENGTH, 12, Scaling.C)
			.putStatInfo(Stats.DEXTERITY, 7, Scaling.E)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setWeaponMaterial(WeaponMaterial.METAL_WEAPON),
			
			// Straight Swords
			MeleeWeaponCap.builder(Items.WOODEN_SWORD, WeaponCategory.STRAIGHT_SWORD, WeaponMovesets.STRAIGHT_SWORD, Colliders.SHORTSWORD.getId(), 2.0F)
			.putDamageInfo(CoreDamageType.PHYSICAL, 79, 0.45F)
			.putDamageInfo(CoreDamageType.MAGIC, 0, 0.30F)
			.putDamageInfo(CoreDamageType.FIRE, 0, 0.25F)
			.putDamageInfo(CoreDamageType.LIGHTNING, 0, 0.25F)
			.putDamageInfo(CoreDamageType.DARK, 0, 0.30F)
			.putDamageInfo(CoreDamageType.HOLY, 0, 0.30F)
			.setCritical(1.10F)
			.setStability(0.30F)
			.putStatInfo(Stats.STRENGTH, 8, Scaling.D)
			.putStatInfo(Stats.DEXTERITY, 10, Scaling.C)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setWeaponMaterial(WeaponMaterial.WOODEN_WEAPON),
			
			MeleeWeaponCap.builder(Items.STONE_SWORD, WeaponCategory.STRAIGHT_SWORD, WeaponMovesets.STRAIGHT_SWORD, Colliders.SHORTSWORD.getId(), 2.0F)
			.putDamageInfo(CoreDamageType.PHYSICAL, 85, 0.45F)
			.putDamageInfo(CoreDamageType.MAGIC, 0, 0.30F)
			.putDamageInfo(CoreDamageType.FIRE, 0, 0.25F)
			.putDamageInfo(CoreDamageType.LIGHTNING, 0, 0.25F)
			.putDamageInfo(CoreDamageType.DARK, 0, 0.30F)
			.putDamageInfo(CoreDamageType.HOLY, 0, 0.30F)
			.setCritical(1.10F)
			.setStability(0.30F)
			.putStatInfo(Stats.STRENGTH, 8, Scaling.D)
			.putStatInfo(Stats.DEXTERITY, 10, Scaling.C)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setWeaponMaterial(WeaponMaterial.STONE_WEAPON),
			
			MeleeWeaponCap.builder(Items.IRON_SWORD, WeaponCategory.STRAIGHT_SWORD, WeaponMovesets.STRAIGHT_SWORD, Colliders.SHORTSWORD.getId(), 2.0F)
			.putDamageInfo(CoreDamageType.PHYSICAL, 99, 0.45F)
			.putDamageInfo(CoreDamageType.MAGIC, 0, 0.30F)
			.putDamageInfo(CoreDamageType.FIRE, 0, 0.25F)
			.putDamageInfo(CoreDamageType.LIGHTNING, 0, 0.25F)
			.putDamageInfo(CoreDamageType.DARK, 0, 0.30F)
			.putDamageInfo(CoreDamageType.HOLY, 0, 0.30F)
			.setCritical(1.10F)
			.setStability(0.30F)
			.putStatInfo(Stats.STRENGTH, 8, Scaling.D)
			.putStatInfo(Stats.DEXTERITY, 10, Scaling.C)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setWeaponMaterial(WeaponMaterial.METAL_WEAPON),
			
			MeleeWeaponCap.builder(Items.GOLDEN_SWORD, WeaponCategory.STRAIGHT_SWORD, WeaponMovesets.STRAIGHT_SWORD, Colliders.SHORTSWORD.getId(), 2.0F)
			.putDamageInfo(CoreDamageType.PHYSICAL, 77, 0.45F)
			.putDamageInfo(CoreDamageType.MAGIC, 0, 0.30F)
			.putDamageInfo(CoreDamageType.FIRE, 0, 0.25F)
			.putDamageInfo(CoreDamageType.LIGHTNING, 0, 0.25F)
			.putDamageInfo(CoreDamageType.DARK, 0, 0.30F)
			.putDamageInfo(CoreDamageType.HOLY, 0, 0.30F)
			.setCritical(1.10F)
			.setStability(0.30F)
			.putStatInfo(Stats.STRENGTH, 8, Scaling.D)
			.putStatInfo(Stats.DEXTERITY, 10, Scaling.C)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setWeaponMaterial(WeaponMaterial.METAL_WEAPON),
			
			MeleeWeaponCap.builder(Items.DIAMOND_SWORD, WeaponCategory.STRAIGHT_SWORD, WeaponMovesets.STRAIGHT_SWORD, Colliders.SHORTSWORD.getId(), 2.0F)
			.putDamageInfo(CoreDamageType.PHYSICAL, 110, 0.45F)
			.putDamageInfo(CoreDamageType.MAGIC, 0, 0.30F)
			.putDamageInfo(CoreDamageType.FIRE, 0, 0.25F)
			.putDamageInfo(CoreDamageType.LIGHTNING, 0, 0.25F)
			.putDamageInfo(CoreDamageType.DARK, 0, 0.30F)
			.putDamageInfo(CoreDamageType.HOLY, 0, 0.30F)
			.setCritical(1.10F)
			.setStability(0.30F)
			.putStatInfo(Stats.STRENGTH, 8, Scaling.D)
			.putStatInfo(Stats.DEXTERITY, 10, Scaling.C)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setWeaponMaterial(WeaponMaterial.METAL_WEAPON),
			
			MeleeWeaponCap.builder(Items.NETHERITE_SWORD, WeaponCategory.STRAIGHT_SWORD, WeaponMovesets.STRAIGHT_SWORD, Colliders.SHORTSWORD.getId(), 2.0F)
			.putDamageInfo(CoreDamageType.PHYSICAL, 125, 0.45F)
			.putDamageInfo(CoreDamageType.MAGIC, 0, 0.30F)
			.putDamageInfo(CoreDamageType.FIRE, 0, 0.25F)
			.putDamageInfo(CoreDamageType.LIGHTNING, 0, 0.25F)
			.putDamageInfo(CoreDamageType.DARK, 0, 0.30F)
			.putDamageInfo(CoreDamageType.HOLY, 0, 0.30F)
			.setCritical(1.10F)
			.setStability(0.30F)
			.putStatInfo(Stats.STRENGTH, 8, Scaling.D)
			.putStatInfo(Stats.DEXTERITY, 10, Scaling.C)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setWeaponMaterial(WeaponMaterial.METAL_WEAPON),
			
			MeleeWeaponCap.builder(ModItems.BROKEN_STRAIGHT_SWORD.get(), WeaponCategory.STRAIGHT_SWORD, WeaponMovesets.STRAIGHT_SWORD, Colliders.BROKEN_SWORD.getId(), 1.0F)
			.putDamageInfo(CoreDamageType.PHYSICAL, 70, 0.40F)
			.putDamageInfo(CoreDamageType.MAGIC, 0, 0.25F)
			.putDamageInfo(CoreDamageType.FIRE, 0, 0.20F)
			.putDamageInfo(CoreDamageType.LIGHTNING, 0, 0.20F)
			.putDamageInfo(CoreDamageType.DARK, 0, 0.25F)
			.putDamageInfo(CoreDamageType.HOLY, 0, 0.25F)
			.setCritical(1.00F)
			.setStability(0.30F)
			.putStatInfo(Stats.STRENGTH, 8, Scaling.D)
			.putStatInfo(Stats.DEXTERITY, 8, Scaling.D)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setWeaponMaterial(WeaponMaterial.METAL_WEAPON),
			
			MeleeWeaponCap.builder(ModItems.STRAIGHT_SWORD_HILT.get(), WeaponCategory.STRAIGHT_SWORD, WeaponMovesets.STRAIGHT_SWORD, Colliders.FIST.getId(), 1.0F)
			.putDamageInfo(CoreDamageType.PHYSICAL, 20, 0.20F)
			.putDamageInfo(CoreDamageType.MAGIC, 0, 0.05F)
			.putDamageInfo(CoreDamageType.FIRE, 0, 0.15F)
			.putDamageInfo(CoreDamageType.LIGHTNING, 0, 0.15F)
			.putDamageInfo(CoreDamageType.DARK, 0, 0.15F)
			.putDamageInfo(CoreDamageType.HOLY, 0, 0.15F)
			.setCritical(1.00F)
			.setStability(0.10F)
			.putStatInfo(Stats.STRENGTH, 6, Scaling.E)
			.putStatInfo(Stats.DEXTERITY, 6, Scaling.E)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setWeaponMaterial(WeaponMaterial.METAL_WEAPON),
			
			MeleeWeaponCap.builder(ModItems.LONGSWORD.get(), WeaponCategory.STRAIGHT_SWORD, WeaponMovesets.STRAIGHT_SWORD, Colliders.LONGSWORD.getId(), 3.0F)
			.putDamageInfo(CoreDamageType.PHYSICAL, 110, 0.45F)
			.putDamageInfo(CoreDamageType.MAGIC, 0, 0.30F)
			.putDamageInfo(CoreDamageType.FIRE, 0, 0.25F)
			.putDamageInfo(CoreDamageType.LIGHTNING, 0, 0.25F)
			.putDamageInfo(CoreDamageType.DARK, 0, 0.30F)
			.putDamageInfo(CoreDamageType.HOLY, 0, 0.30F)
			.setCritical(1.00F)
			.setStability(0.30F)
			.putStatInfo(Stats.STRENGTH, 10, Scaling.D)
			.putStatInfo(Stats.DEXTERITY, 10, Scaling.D)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setWeaponMaterial(WeaponMaterial.METAL_WEAPON),
			
			MeleeWeaponCap.builder(ModItems.BALDER_SIDE_SWORD.get(), WeaponCategory.STRAIGHT_SWORD, WeaponMovesets.STRAIGHT_SWORD, Colliders.LONGSWORD.getId(), 3.0F)
			.putDamageInfo(CoreDamageType.PHYSICAL, 80, 0.5F)
			.putDamageInfo(CoreDamageType.MAGIC, 0, 0.10F)
			.putDamageInfo(CoreDamageType.FIRE, 0, 0.35F)
			.putDamageInfo(CoreDamageType.LIGHTNING, 0, 0.35F)
			.putDamageInfo(CoreDamageType.DARK, 0, 0.10F)
			.putDamageInfo(CoreDamageType.HOLY, 0, 0.10F)
			.setCritical(1.00F)
			.setStability(0.32F)
			.putStatInfo(Stats.STRENGTH, 10, Scaling.E)
			.putStatInfo(Stats.DEXTERITY, 14, Scaling.B)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setWeaponMaterial(WeaponMaterial.METAL_WEAPON),
			
			// Greatswords
			MeleeWeaponCap.builder(ModItems.BLACK_KNIGHT_SWORD.get(), WeaponCategory.GREATSWORD, WeaponMovesets.BLACK_KNIGHT_SWORD, Colliders.GREATSWORD.getId(), 10.0F)
			.putDamageInfo(CoreDamageType.PHYSICAL, 173, 0.45F)
			.putDamageInfo(CoreDamageType.MAGIC, 0, 0.40F)
			.putDamageInfo(CoreDamageType.FIRE, 0, 0.40F)
			.putDamageInfo(CoreDamageType.LIGHTNING, 0, 0.35F)
			.putDamageInfo(CoreDamageType.DARK, 0, 0.40F)
			.putDamageInfo(CoreDamageType.HOLY, 0, 0.40F)
			.setCritical(1.00F)
			.setStability(0.40F)
			.putStatInfo(Stats.STRENGTH, 20, Scaling.D)
			.putStatInfo(Stats.DEXTERITY, 18, Scaling.D)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setWeaponMaterial(WeaponMaterial.METAL_WEAPON)
			.addAuxEffect(AuxEffects.ANTI_DEMON),
			
			MeleeWeaponCap.builder(ModItems.CLAYMORE.get(), WeaponCategory.GREATSWORD, WeaponMovesets.CLAYMORE, Colliders.GREATSWORD.getId(), 9.0F)
			.putDamageInfo(CoreDamageType.PHYSICAL, 138, 0.50F)
			.putDamageInfo(CoreDamageType.MAGIC, 0, 0.35F)
			.putDamageInfo(CoreDamageType.FIRE, 0, 0.30F)
			.putDamageInfo(CoreDamageType.LIGHTNING, 0, 0.30F)
			.putDamageInfo(CoreDamageType.DARK, 0, 0.35F)
			.putDamageInfo(CoreDamageType.HOLY, 0, 0.35F)
			.setCritical(1.00F)
			.setStability(0.35F)
			.putStatInfo(Stats.STRENGTH, 16, Scaling.D)
			.putStatInfo(Stats.DEXTERITY, 13, Scaling.D)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setWeaponMaterial(WeaponMaterial.METAL_WEAPON),
			
			// Spears
			MeleeWeaponCap.builder(ModItems.SPEAR.get(), WeaponCategory.SPEAR, WeaponMovesets.SPEAR, Colliders.SPEAR.getId(), 4.5F)
			.putDamageInfo(CoreDamageType.PHYSICAL, 104, 0.40F)
			.putDamageInfo(CoreDamageType.MAGIC, 0, 0.25F)
			.putDamageInfo(CoreDamageType.FIRE, 0, 0.20F)
			.putDamageInfo(CoreDamageType.LIGHTNING, 0, 0.20F)
			.putDamageInfo(CoreDamageType.DARK, 0, 0.25F)
			.putDamageInfo(CoreDamageType.HOLY, 0, 0.25F)
			.setCritical(1.00F)
			.setStability(0.30F)
			.putStatInfo(Stats.STRENGTH, 11, Scaling.D)
			.putStatInfo(Stats.DEXTERITY, 10, Scaling.C)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setWeaponMaterial(WeaponMaterial.METAL_WEAPON),
			
			MeleeWeaponCap.builder(ModItems.WINGED_SPEAR.get(), WeaponCategory.SPEAR, WeaponMovesets.SPEAR, Colliders.WINGED_SPEAR.getId(), 6.0F)
			.putDamageInfo(CoreDamageType.PHYSICAL, 95, 0.40F)
			.putDamageInfo(CoreDamageType.MAGIC, 0, 0.25F)
			.putDamageInfo(CoreDamageType.FIRE, 0, 0.20F)
			.putDamageInfo(CoreDamageType.LIGHTNING, 0, 0.20F)
			.putDamageInfo(CoreDamageType.DARK, 0, 0.25F)
			.putDamageInfo(CoreDamageType.HOLY, 0, 0.25F)
			.setCritical(1.00F)
			.setStability(0.20F)
			.putStatInfo(Stats.STRENGTH, 12, Scaling.D)
			.putStatInfo(Stats.DEXTERITY, 15, Scaling.C)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setWeaponMaterial(WeaponMaterial.METAL_WEAPON),
			
			// Ultra Greatswords
			MeleeWeaponCap.builder(ModItems.ZWEIHANDER.get(), WeaponCategory.ULTRA_GREATSWORD, WeaponMovesets.ULTRA_GREATSWORD, Colliders.ULTRA_GREATSWORD.getId(), 10.0F)
			.putDamageInfo(CoreDamageType.PHYSICAL, 145, 0.50F)
			.putDamageInfo(CoreDamageType.MAGIC, 0, 0.40F)
			.putDamageInfo(CoreDamageType.FIRE, 0, 0.35F)
			.putDamageInfo(CoreDamageType.LIGHTNING, 0, 0.35F)
			.putDamageInfo(CoreDamageType.DARK, 0, 0.40F)
			.putDamageInfo(CoreDamageType.HOLY, 0, 0.40F)
			.setCritical(1.00F)
			.setStability(0.40F)
			.putStatInfo(Stats.STRENGTH, 19, Scaling.D)
			.putStatInfo(Stats.DEXTERITY, 11, Scaling.D)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setWeaponMaterial(WeaponMaterial.METAL_WEAPON),
			
			// Greataxes
			MeleeWeaponCap.builder(ModItems.DEMON_GREATAXE.get(), WeaponCategory.GREATAXE, WeaponMovesets.ULTRA_GREATSWORD, Colliders.GREATAXE.getId(), 22.0F)
			.putDamageInfo(CoreDamageType.PHYSICAL, 114, 0.55F)
			.putDamageInfo(CoreDamageType.MAGIC, 0, 0.10F)
			.putDamageInfo(CoreDamageType.FIRE, 0, 0.40F)
			.putDamageInfo(CoreDamageType.LIGHTNING, 0, 0.40F)
			.putDamageInfo(CoreDamageType.DARK, 0, 0.10F)
			.putDamageInfo(CoreDamageType.HOLY, 0, 0.10F)
			.setCritical(1.00F)
			.setStability(0.38F)
			.putStatInfo(Stats.STRENGTH, 46, Scaling.A)
			.putStatInfo(Stats.DEXTERITY, 11, Scaling.NONE)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setWeaponMaterial(WeaponMaterial.WOODEN_WEAPON),
			
			// Thrusting Swords
			MeleeWeaponCap.builder(ModItems.RAPIER.get(), WeaponCategory.THRUSTING_SWORD, WeaponMovesets.THRUSTING_SWORD, Colliders.LONGSWORD.getId(), 20.0F)
			.putDamageInfo(CoreDamageType.PHYSICAL, 95, 0.35F)
			.putDamageInfo(CoreDamageType.MAGIC, 0, 0.20F)
			.putDamageInfo(CoreDamageType.FIRE, 0, 0.15F)
			.putDamageInfo(CoreDamageType.LIGHTNING, 0, 0.15F)
			.putDamageInfo(CoreDamageType.DARK, 0, 0.20F)
			.putDamageInfo(CoreDamageType.HOLY, 0, 0.20F)
			.setCritical(1.30F)
			.setStability(0.15F)
			.putStatInfo(Stats.STRENGTH, 7, Scaling.E)
			.putStatInfo(Stats.DEXTERITY, 12, Scaling.C)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setWeaponMaterial(WeaponMaterial.METAL_WEAPON),
			
			// Shields
			MeleeWeaponCap.builder(Items.SHIELD, WeaponCategory.MEDIUM_SHIELD, WeaponMovesets.SHIELD, Colliders.SHIELD.getId(), 4.0F)
			.putDamageInfo(CoreDamageType.PHYSICAL, 88, 0.84F)
			.putDamageInfo(CoreDamageType.MAGIC, 0, 0.47F)
			.putDamageInfo(CoreDamageType.FIRE, 0, 0.69F)
			.putDamageInfo(CoreDamageType.LIGHTNING, 0, 0.64F)
			.putDamageInfo(CoreDamageType.DARK, 0, 0.67F)
			.putDamageInfo(CoreDamageType.HOLY, 0, 0.67F)
			.setCritical(1.00F)
			.setStability(0.57F)
			.putStatInfo(Stats.STRENGTH, 12, Scaling.D)
			.putStatInfo(Stats.DEXTERITY, 0, Scaling.NONE)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setWeaponMaterial(WeaponMaterial.WOODEN_SHIELD)
			.setShieldType(ShieldType.STANDARD),
			
			MeleeWeaponCap.builder(ModItems.HEATER_SHIELD.get(), WeaponCategory.MEDIUM_SHIELD, WeaponMovesets.SHIELD, Colliders.SHIELD.getId(), 2.0F)
			.setSkill(WeaponSkills.PARRY)
			.putDamageInfo(CoreDamageType.PHYSICAL, 60, 1.00F)
			.putDamageInfo(CoreDamageType.MAGIC, 0, 0.30F)
			.putDamageInfo(CoreDamageType.FIRE, 0, 0.60F)
			.putDamageInfo(CoreDamageType.LIGHTNING, 0, 0.50F)
			.putDamageInfo(CoreDamageType.DARK, 0, 0.30F)
			.putDamageInfo(CoreDamageType.HOLY, 0, 0.30F)
			.setCritical(1.00F)
			.setStability(0.55F)
			.putStatInfo(Stats.STRENGTH, 8, Scaling.D)
			.putStatInfo(Stats.DEXTERITY, 0, Scaling.NONE)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setWeaponMaterial(WeaponMaterial.METAL_SHIELD)
			.setShieldType(ShieldType.STANDARD),
			
			MeleeWeaponCap.builder(ModItems.CRACKED_ROUND_SHIELD.get(), WeaponCategory.SMALL_SHIELD, WeaponMovesets.SHIELD, Colliders.SHIELD.getId(), 1.0F)
			.setSkill(WeaponSkills.PARRY)
			.putDamageInfo(CoreDamageType.PHYSICAL, 46, 0.65F)
			.putDamageInfo(CoreDamageType.MAGIC, 0, 0.55F)
			.putDamageInfo(CoreDamageType.FIRE, 0, 0.10F)
			.putDamageInfo(CoreDamageType.LIGHTNING, 0, 0.45F)
			.putDamageInfo(CoreDamageType.DARK, 0, 0.55F)
			.putDamageInfo(CoreDamageType.HOLY, 0, 0.55F)
			.setCritical(1.00F)
			.setStability(0.30F)
			.putStatInfo(Stats.STRENGTH, 6, Scaling.D)
			.putStatInfo(Stats.DEXTERITY, 0, Scaling.NONE)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setWeaponMaterial(WeaponMaterial.WOODEN_SHIELD)
			.setShieldType(ShieldType.CRACKED_ROUND_SHIELD),
			
			MeleeWeaponCap.builder(ModItems.LORDRAN_SOLDIER_SHIELD.get(), WeaponCategory.MEDIUM_SHIELD, WeaponMovesets.SHIELD, Colliders.SHIELD.getId(), 3.5F)
			.setSkill(WeaponSkills.PARRY)
			.putDamageInfo(CoreDamageType.PHYSICAL, 66, 1.00F)
			.putDamageInfo(CoreDamageType.MAGIC, 0, 0.30F)
			.putDamageInfo(CoreDamageType.FIRE, 0, 0.65F)
			.putDamageInfo(CoreDamageType.LIGHTNING, 0, 0.50F)
			.putDamageInfo(CoreDamageType.DARK, 0, 0.30F)
			.putDamageInfo(CoreDamageType.HOLY, 0, 0.30F)
			.setCritical(1.00F)
			.setStability(0.56F)
			.putStatInfo(Stats.STRENGTH, 11, Scaling.D)
			.putStatInfo(Stats.DEXTERITY, 0, Scaling.NONE)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setWeaponMaterial(WeaponMaterial.METAL_SHIELD)
			.setShieldType(ShieldType.STANDARD),
			
			MeleeWeaponCap.builder(ModItems.KNIGHT_SHIELD.get(), WeaponCategory.MEDIUM_SHIELD, WeaponMovesets.SHIELD, Colliders.SHIELD.getId(), 5.5F)
			.setSkill(WeaponSkills.PARRY)
			.putDamageInfo(CoreDamageType.PHYSICAL, 68, 1.00F)
			.putDamageInfo(CoreDamageType.MAGIC, 0, 0.30F)
			.putDamageInfo(CoreDamageType.FIRE, 0, 0.60F)
			.putDamageInfo(CoreDamageType.LIGHTNING, 0, 0.40F)
			.putDamageInfo(CoreDamageType.DARK, 0, 0.30F)
			.putDamageInfo(CoreDamageType.HOLY, 0, 0.30F)
			.setCritical(1.00F)
			.setStability(0.56F)
			.putStatInfo(Stats.STRENGTH, 10, Scaling.D)
			.putStatInfo(Stats.DEXTERITY, 0, Scaling.NONE)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setWeaponMaterial(WeaponMaterial.METAL_SHIELD)
			.setShieldType(ShieldType.STANDARD),
			
			MeleeWeaponCap.builder(ModItems.GOLDEN_FALCON_SHIELD.get(), WeaponCategory.SMALL_SHIELD, WeaponMovesets.SHIELD, Colliders.SHIELD.getId(), 2.5F)
			.setSkill(WeaponSkills.PARRY)
			.putDamageInfo(CoreDamageType.PHYSICAL, 75, 0.55F)
			.putDamageInfo(CoreDamageType.MAGIC, 0, 0.45F)
			.putDamageInfo(CoreDamageType.FIRE, 0, 0.39F)
			.putDamageInfo(CoreDamageType.LIGHTNING, 0, 0.20F)
			.putDamageInfo(CoreDamageType.DARK, 0, 0.37F)
			.putDamageInfo(CoreDamageType.HOLY, 0, 0.37F)
			.setCritical(1.00F)
			.setStability(0.48F)
			.putStatInfo(Stats.STRENGTH, 10, Scaling.D)
			.putStatInfo(Stats.DEXTERITY, 0, Scaling.NONE)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setWeaponMaterial(WeaponMaterial.METAL_SHIELD)
			.setShieldType(ShieldType.SMALL),
			
			MeleeWeaponCap.builder(ModItems.BALDER_SHIELD.get(), WeaponCategory.MEDIUM_SHIELD, WeaponMovesets.SHIELD, Colliders.SHIELD.getId(), 4.0F)
			.setSkill(WeaponSkills.PARRY)
			.putDamageInfo(CoreDamageType.PHYSICAL, 66, 1.00F)
			.putDamageInfo(CoreDamageType.MAGIC, 0, 0.20F)
			.putDamageInfo(CoreDamageType.FIRE, 0, 0.60F)
			.putDamageInfo(CoreDamageType.LIGHTNING, 0, 0.50F)
			.putDamageInfo(CoreDamageType.DARK, 0, 0.25F)
			.putDamageInfo(CoreDamageType.HOLY, 0, 0.25F)
			.setCritical(1.00F)
			.setStability(0.63F)
			.putStatInfo(Stats.STRENGTH, 12, Scaling.D)
			.putStatInfo(Stats.DEXTERITY, 0, Scaling.NONE)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setWeaponMaterial(WeaponMaterial.METAL_SHIELD)
			.setShieldType(ShieldType.STANDARD),
			
			MeleeWeaponCap.builder(ModItems.BLACK_KNIGHT_SHIELD.get(), WeaponCategory.MEDIUM_SHIELD, WeaponMovesets.SHIELD, Colliders.SHIELD.getId(), 4.0F)
			.setSkill(WeaponSkills.PARRY)
			.putDamageInfo(CoreDamageType.PHYSICAL, 113, 1.00F)
			.putDamageInfo(CoreDamageType.MAGIC, 0, 0.65F)
			.putDamageInfo(CoreDamageType.FIRE, 0, 0.85F)
			.putDamageInfo(CoreDamageType.LIGHTNING, 0, 0.52F)
			.putDamageInfo(CoreDamageType.DARK, 0, 0.63F)
			.putDamageInfo(CoreDamageType.HOLY, 0, 0.63F)
			.setCritical(1.00F)
			.setStability(0.60F)
			.putStatInfo(Stats.STRENGTH, 18, Scaling.D)
			.putStatInfo(Stats.DEXTERITY, 0, Scaling.NONE)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setWeaponMaterial(WeaponMaterial.METAL_SHIELD)
			.setShieldType(ShieldType.STANDARD),
			
			MeleeWeaponCap.builder(ModItems.BUCKLER.get(), WeaponCategory.SMALL_SHIELD, WeaponMovesets.SHIELD, Colliders.SHIELD.getId(), 1.0F)
			.setSkill(WeaponSkills.FAST_PARRY)
			.putDamageInfo(CoreDamageType.PHYSICAL, 67, 0.49F)
			.putDamageInfo(CoreDamageType.MAGIC, 0, 0.35F)
			.putDamageInfo(CoreDamageType.FIRE, 0, 0.30F)
			.putDamageInfo(CoreDamageType.LIGHTNING, 0, 0.11F)
			.putDamageInfo(CoreDamageType.DARK, 0, 0.28F)
			.putDamageInfo(CoreDamageType.HOLY, 0, 0.28F)
			.setCritical(1.00F)
			.setStability(0.43F)
			.putStatInfo(Stats.STRENGTH, 7, Scaling.E)
			.putStatInfo(Stats.DEXTERITY, 13, Scaling.NONE)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setWeaponMaterial(WeaponMaterial.METAL_SHIELD)
			.setShieldType(ShieldType.SMALL)
		);
	}

	private static Path createPath(Path path, ResourceLocation location)
	{
		return path.resolve("data/" + location.getNamespace() + "/weapon_configs/melee/" + location.getPath() + ".json");
	}

	@Override
	public String getName()
	{
		return "MeleeWeaponConfigs";
	}
}
