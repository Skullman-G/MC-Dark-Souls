package com.skullmangames.darksouls.core.data_provider;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.capability.item.RangedWeaponCap;
import com.skullmangames.darksouls.common.capability.item.WeaponCap.HandProperty;
import com.skullmangames.darksouls.common.capability.item.WeaponCap.Scaling;
import com.skullmangames.darksouls.common.entity.stats.Stats;
import com.skullmangames.darksouls.core.util.WeaponCategory;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.CoreDamageType;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

public class RangedWeaponConfigProvider implements DataProvider
{
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
	private final DataGenerator generator;
	
	public RangedWeaponConfigProvider(DataGenerator generator)
	{
		this.generator = generator;
	}
	
	@Override
	public void run(HashCache cache) throws IOException
	{
		Path path = this.generator.getOutputFolder();
		
		for (RangedWeaponCap.Builder builder : defaultConfigs())
		{
			Path path1 = createPath(path, builder.getId());
			try
			{
				DataProvider.save(GSON, cache, builder.toJson(), path1);
			}
			catch (IOException ioexception)
			{
				LOGGER.error("Couldn't save ranged weapon config {}", path1, ioexception);
			}
		}
	}
	
	private static List<RangedWeaponCap.Builder> defaultConfigs()
	{
		return ImmutableList.of
		(
				//Bows
				RangedWeaponCap.builder(Items.BOW, WeaponCategory.BOW, 1.00F, 2.0F, HandProperty.TWO_HANDED)
						.putDamage(CoreDamageType.PHYSICAL, 77)
						.putStatInfo(Stats.STRENGTH, 7, Scaling.E)
						.putStatInfo(Stats.DEXTERITY, 12, Scaling.D)
						.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
						.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
						.putAnimOverride(LivingMotion.AIMING, Animations.BIPED_BOW_AIM.get())
						.putAnimOverride(LivingMotion.SHOOTING, Animations.BIPED_BOW_REBOUND.get()),
				
				//Crossbows
				RangedWeaponCap.builder(Items.CROSSBOW, WeaponCategory.CROSSBOW, 1.00F, 3.0F, HandProperty.TWO_HANDED)
						.putDamage(CoreDamageType.PHYSICAL, 64)
						.putStatInfo(Stats.STRENGTH, 10, Scaling.NONE)
						.putStatInfo(Stats.DEXTERITY, 8, Scaling.NONE)
						.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
						.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
						.putAnimOverride(LivingMotion.RELOADING, Animations.BIPED_CROSSBOW_RELOAD.get())
						.putAnimOverride(LivingMotion.AIMING, Animations.BIPED_CROSSBOW_AIM.get())
						.putAnimOverride(LivingMotion.SHOOTING, Animations.BIPED_CROSSBOW_SHOT.get())
						.putAnimOverride(LivingMotion.IDLE, Animations.BIPED_IDLE_CROSSBOW.get())
						.putAnimOverride(LivingMotion.WALKING, Animations.BIPED_WALK_CROSSBOW.get())
						.putAnimOverride(LivingMotion.RUNNING, Animations.BIPED_WALK_CROSSBOW.get()),
				
				//Tridents
				RangedWeaponCap.builder(Items.TRIDENT, WeaponCategory.NONE_WEAON, 1.00F, 3.0F, HandProperty.MAINHAND_ONLY)
						.putDamage(CoreDamageType.PHYSICAL, 64)
						.putStatInfo(Stats.STRENGTH, 15, Scaling.NONE)
						.putStatInfo(Stats.DEXTERITY, 9, Scaling.NONE)
						.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
						.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
						.putAnimOverride(LivingMotion.AIMING, Animations.BIPED_SPEER_AIM.get())
						.putAnimOverride(LivingMotion.SHOOTING, Animations.BIPED_SPEER_REBOUND.get())
		);
	}

	private static Path createPath(Path path, ResourceLocation location)
	{
		return path.resolve("data/" + location.getNamespace() + "/weapon_configs/ranged/" + location.getPath() + ".json");
	}

	@Override
	public String getName()
	{
		return "RangedWeaponConfigs";
	}
}
