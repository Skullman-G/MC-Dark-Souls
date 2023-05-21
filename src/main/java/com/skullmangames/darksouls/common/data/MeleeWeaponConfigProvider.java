package com.skullmangames.darksouls.common.data;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap.AttackType;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap.WeaponMaterial;
import com.skullmangames.darksouls.common.capability.item.WeaponCap.Scaling;
import com.skullmangames.darksouls.common.entity.stats.Stats;
import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.core.init.WeaponMovesets;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.CoreDamageType;

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
			MeleeWeaponCap.builder(ModItems.DEMON_GREAT_HAMMER.get(), WeaponMovesets.GREAT_HAMMER, Colliders.GREAT_HAMMER.getId(), 80, 22.0F)
			.putDefense(CoreDamageType.PHYSICAL, 0.50F)
			.putDefense(CoreDamageType.MAGIC, 0.10F)
			.putDefense(CoreDamageType.FIRE, 0.35F)
			.putDefense(CoreDamageType.LIGHTNING, 0.35F)
			.putPoiseDamage(AttackType.LIGHT, 50, 75)
			.putPoiseDamage(AttackType.HEAVY, 60, 75)
			.putPoiseDamage(AttackType.DASH, 50, 75)
			.putPoiseDamage(AttackType.BACKSTAB, 60, 85)
			.putStaminaUsage(AttackType.LIGHT, 35, 48)
			.putStaminaUsage(AttackType.HEAVY, 74, 85)
			.putStaminaUsage(AttackType.DASH, 45, 55)
			.putStaminaUsage(AttackType.BACKSTAB, 35, 48)
			.putStatInfo(Stats.STRENGTH, 46, Scaling.B)
			.putStatInfo(Stats.DEXTERITY, 0, Scaling.NONE)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.shouldHoldOnShoulder(),
			
			MeleeWeaponCap.builder(Items.WOODEN_AXE, WeaponMovesets.AXE, Colliders.TOOL.getId(), 38, 0.75F)
			.putDefense(CoreDamageType.PHYSICAL, 0.55F)
			.putDefense(CoreDamageType.MAGIC, 0.10F)
			.putDefense(CoreDamageType.FIRE, 0.40F)
			.putDefense(CoreDamageType.LIGHTNING, 0.40F)
			.putPoiseDamage(AttackType.LIGHT, 17, 25)
			.putPoiseDamage(AttackType.HEAVY, 20, 26)
			.putPoiseDamage(AttackType.DASH, 17, 25)
			.putPoiseDamage(AttackType.BACKSTAB, 16, 24)
			.putStaminaUsage(AttackType.LIGHT, 25, 38)
			.putStaminaUsage(AttackType.HEAVY, 50, 68)
			.putStaminaUsage(AttackType.DASH, 30, 42)
			.putStaminaUsage(AttackType.BACKSTAB, 25, 38)
			.putStatInfo(Stats.STRENGTH, 8, Scaling.C)
			.putStatInfo(Stats.DEXTERITY, 8, Scaling.D)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setWeaponMaterial(WeaponMaterial.WOODEN_WEAPON),
			
			MeleeWeaponCap.builder(Items.STONE_AXE, WeaponMovesets.AXE, Colliders.TOOL.getId(), 43, 0.8F)
			.putDefense(CoreDamageType.PHYSICAL, 0.55F)
			.putDefense(CoreDamageType.MAGIC, 0.10F)
			.putDefense(CoreDamageType.FIRE, 0.40F)
			.putDefense(CoreDamageType.LIGHTNING, 0.40F)
			.putPoiseDamage(AttackType.LIGHT, 20, 30)
			.putPoiseDamage(AttackType.HEAVY, 24, 30)
			.putPoiseDamage(AttackType.DASH, 20, 30)
			.putPoiseDamage(AttackType.BACKSTAB, 22, 32)
			.putStaminaUsage(AttackType.LIGHT, 25, 38)
			.putStaminaUsage(AttackType.HEAVY, 50, 68)
			.putStaminaUsage(AttackType.DASH, 30, 42)
			.putStaminaUsage(AttackType.BACKSTAB, 25, 38)
			.putStatInfo(Stats.STRENGTH, 8, Scaling.C)
			.putStatInfo(Stats.DEXTERITY, 8, Scaling.D)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setWeaponMaterial(WeaponMaterial.WOODEN_WEAPON),
			
			MeleeWeaponCap.builder(Items.IRON_AXE, WeaponMovesets.AXE, Colliders.TOOL.getId(), 45, 1.0F)
			.putDefense(CoreDamageType.PHYSICAL, 0.55F)
			.putDefense(CoreDamageType.MAGIC, 0.10F)
			.putDefense(CoreDamageType.FIRE, 0.40F)
			.putDefense(CoreDamageType.LIGHTNING, 0.40F)
			.putPoiseDamage(AttackType.LIGHT, 20, 30)
			.putPoiseDamage(AttackType.HEAVY, 24, 30)
			.putPoiseDamage(AttackType.DASH, 20, 30)
			.putPoiseDamage(AttackType.BACKSTAB, 22, 32)
			.putStaminaUsage(AttackType.LIGHT, 25, 38)
			.putStaminaUsage(AttackType.HEAVY, 50, 68)
			.putStaminaUsage(AttackType.DASH, 30, 42)
			.putStaminaUsage(AttackType.BACKSTAB, 25, 38)
			.putStatInfo(Stats.STRENGTH, 8, Scaling.C)
			.putStatInfo(Stats.DEXTERITY, 8, Scaling.D)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setWeaponMaterial(WeaponMaterial.WOODEN_WEAPON),
			
			MeleeWeaponCap.builder(Items.GOLDEN_AXE, WeaponMovesets.AXE, Colliders.TOOL.getId(), 45, 1.0F)
			.putDefense(CoreDamageType.PHYSICAL, 0.55F)
			.putDefense(CoreDamageType.MAGIC, 0.10F)
			.putDefense(CoreDamageType.FIRE, 0.40F)
			.putDefense(CoreDamageType.LIGHTNING, 0.40F)
			.putPoiseDamage(AttackType.LIGHT, 20, 30)
			.putPoiseDamage(AttackType.HEAVY, 24, 30)
			.putPoiseDamage(AttackType.DASH, 20, 30)
			.putPoiseDamage(AttackType.BACKSTAB, 22, 32)
			.putStaminaUsage(AttackType.LIGHT, 25, 38)
			.putStaminaUsage(AttackType.HEAVY, 50, 68)
			.putStaminaUsage(AttackType.DASH, 30, 42)
			.putStaminaUsage(AttackType.BACKSTAB, 25, 38)
			.putStatInfo(Stats.STRENGTH, 8, Scaling.C)
			.putStatInfo(Stats.DEXTERITY, 8, Scaling.D)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setWeaponMaterial(WeaponMaterial.WOODEN_WEAPON),
			
			MeleeWeaponCap.builder(Items.DIAMOND_AXE, WeaponMovesets.AXE, Colliders.TOOL.getId(), 45, 1.0F)
			.putDefense(CoreDamageType.PHYSICAL, 0.55F)
			.putDefense(CoreDamageType.MAGIC, 0.10F)
			.putDefense(CoreDamageType.FIRE, 0.40F)
			.putDefense(CoreDamageType.LIGHTNING, 0.40F)
			.putPoiseDamage(AttackType.LIGHT, 20, 30)
			.putPoiseDamage(AttackType.HEAVY, 24, 30)
			.putPoiseDamage(AttackType.DASH, 20, 30)
			.putPoiseDamage(AttackType.BACKSTAB, 22, 32)
			.putStaminaUsage(AttackType.LIGHT, 25, 38)
			.putStaminaUsage(AttackType.HEAVY, 50, 68)
			.putStaminaUsage(AttackType.DASH, 30, 42)
			.putStaminaUsage(AttackType.BACKSTAB, 25, 38)
			.putStatInfo(Stats.STRENGTH, 10, Scaling.C)
			.putStatInfo(Stats.DEXTERITY, 15, Scaling.D)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setWeaponMaterial(WeaponMaterial.WOODEN_WEAPON),
			
			MeleeWeaponCap.builder(Items.NETHERITE_AXE, WeaponMovesets.AXE, Colliders.TOOL.getId(), 45, 1.0F)
			.putDefense(CoreDamageType.PHYSICAL, 0.55F)
			.putDefense(CoreDamageType.MAGIC, 0.10F)
			.putDefense(CoreDamageType.FIRE, 0.40F)
			.putDefense(CoreDamageType.LIGHTNING, 0.40F)
			.putPoiseDamage(AttackType.LIGHT, 20, 30)
			.putPoiseDamage(AttackType.HEAVY, 24, 30)
			.putPoiseDamage(AttackType.DASH, 20, 30)
			.putPoiseDamage(AttackType.BACKSTAB, 22, 32)
			.putStaminaUsage(AttackType.LIGHT, 25, 38)
			.putStaminaUsage(AttackType.HEAVY, 50, 68)
			.putStaminaUsage(AttackType.DASH, 30, 42)
			.putStaminaUsage(AttackType.BACKSTAB, 25, 38)
			.putStatInfo(Stats.STRENGTH, 10, Scaling.C)
			.putStatInfo(Stats.DEXTERITY, 15, Scaling.D)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setWeaponMaterial(WeaponMaterial.WOODEN_WEAPON),
			
			MeleeWeaponCap.builder(ModItems.BATTLE_AXE.get(), WeaponMovesets.AXE, Colliders.TOOL.getId(), 50, 4.0F)
			.putDefense(CoreDamageType.PHYSICAL, 0.55F)
			.putDefense(CoreDamageType.MAGIC, 0.10F)
			.putDefense(CoreDamageType.FIRE, 0.40F)
			.putDefense(CoreDamageType.LIGHTNING, 0.40F)
			.putPoiseDamage(AttackType.LIGHT, 35, 53)
			.putPoiseDamage(AttackType.HEAVY, 42, 52)
			.putPoiseDamage(AttackType.DASH, 35, 53)
			.putPoiseDamage(AttackType.BACKSTAB, 39, 56)
			.putStaminaUsage(AttackType.LIGHT, 25, 38)
			.putStaminaUsage(AttackType.HEAVY, 50, 68)
			.putStaminaUsage(AttackType.DASH, 30, 42)
			.putStaminaUsage(AttackType.BACKSTAB, 25, 38)
			.putStatInfo(Stats.STRENGTH, 12, Scaling.C)
			.putStatInfo(Stats.DEXTERITY, 8, Scaling.D)
			.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
			.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
			.setWeaponMaterial(WeaponMaterial.METAL_WEAPON)
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
