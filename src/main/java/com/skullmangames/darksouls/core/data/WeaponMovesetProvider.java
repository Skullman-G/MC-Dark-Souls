package com.skullmangames.darksouls.core.data;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.slf4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap.AttackType;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.WeaponMovesets;
import com.skullmangames.darksouls.core.util.WeaponMoveset;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;

public class WeaponMovesetProvider implements DataProvider
{
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
	private final DataGenerator generator;
	
	public WeaponMovesetProvider(DataGenerator generator)
	{
		this.generator = generator;
	}
	
	@Override
	public void run(HashCache cache) throws IOException
	{
		Animations.init();
		Path path = this.generator.getOutputFolder();
		
		for (WeaponMoveset.Builder builder : defaultMovesets())
		{
			Path path1 = createPath(path, builder.getLocation());
			try
			{
				DataProvider.save(GSON, cache, builder.toJson(), path1);
			}
			catch (IOException ioexception)
			{
				LOGGER.error("Couldn't save weapon moveset {}", path1, ioexception);
			}
		}
	}
	
	private static List<WeaponMoveset.Builder> defaultMovesets()
	{
		return ImmutableList.of
		(
			new WeaponMoveset.Builder(WeaponMovesets.FIST)
			.putMove(AttackType.LIGHT, true, Animations.FIST_LIGHT_ATTACK)
			.putMove(AttackType.HEAVY, true, Animations.FIST_HEAVY_ATTACK)
			.putMove(AttackType.DASH, true, Animations.FIST_DASH_ATTACK)
			.putMove(AttackType.BACKSTAB, true, Animations.BACKSTAB_STRIKE)
			.putMove(AttackType.PUNISH, true, Animations.PUNISH_STRIKE),
			new WeaponMoveset.Builder(WeaponMovesets.STRAIGHT_SWORD)
			.putMove(AttackType.LIGHT, true, Animations.STRAIGHT_SWORD_LIGHT_ATTACK)
			.putMove(AttackType.HEAVY, true, Animations.STRAIGHT_SWORD_HEAVY_ATTACK)
			.putMove(AttackType.DASH, true, Animations.STRAIGHT_SWORD_DASH_ATTACK)
			.putMove(AttackType.BACKSTAB, true, Animations.BACKSTAB_THRUST)
			.putMove(AttackType.PUNISH, true, Animations.PUNISH_THRUST)
			.putMove(AttackType.TWO_HANDED_LIGHT, true, Animations.STRAIGHT_SWORD_TH_LIGHT_ATTACK)
			.putMove(AttackType.TWO_HANDED_HEAVY, true, Animations.STRAIGHT_SWORD_TH_HEAVY_ATTACK)
			.putMove(AttackType.TWO_HANDED_DASH, true, Animations.STRAIGHT_SWORD_TH_DASH_ATTACK),
			new WeaponMoveset.Builder(WeaponMovesets.AXE)
			.putMove(AttackType.LIGHT, true, Animations.AXE_LIGHT_ATTACK)
			.putMove(AttackType.HEAVY, true, Animations.AXE_HEAVY_ATTACK)
			.putMove(AttackType.DASH, true, Animations.AXE_DASH_ATTACK)
			.putMove(AttackType.BACKSTAB, true, Animations.BACKSTAB_STRIKE)
			.putMove(AttackType.PUNISH, true, Animations.PUNISH_STRIKE)
			.putMove(AttackType.TWO_HANDED_LIGHT, true, Animations.AXE_TH_LIGHT_ATTACK)
			.putMove(AttackType.TWO_HANDED_HEAVY, true, Animations.AXE_TH_HEAVY_ATTACK)
			.putMove(AttackType.TWO_HANDED_DASH, true, Animations.AXE_TH_DASH_ATTACK),
			new WeaponMoveset.Builder(WeaponMovesets.BLACK_KNIGHT_SWORD)
			.putMove(AttackType.LIGHT, true, Animations.GREATSWORD_LIGHT_ATTACK)
			.putMove(AttackType.HEAVY, true, Animations.GREATSWORD_STYLISH_THRUST)
			.putMove(AttackType.DASH, true, Animations.GREATSWORD_DASH_ATTACK)
			.putMove(AttackType.BACKSTAB, true, Animations.BACKSTAB_THRUST)
			.putMove(AttackType.PUNISH, true, Animations.PUNISH_THRUST)
			.putMove(AttackType.TWO_HANDED_LIGHT, true, Animations.GREATSWORD_TH_LIGHT_ATTACK)
			.putMove(AttackType.TWO_HANDED_HEAVY, true, Animations.GREATSWORD_TH_THRUST_ATTACK)
			.putMove(AttackType.TWO_HANDED_DASH, true, Animations.GREATSWORD_TH_DASH_ATTACK),
			new WeaponMoveset.Builder(WeaponMovesets.CLAYMORE)
			.putMove(AttackType.LIGHT, true, Animations.GREATSWORD_LIGHT_ATTACK)
			.putMove(AttackType.HEAVY, true, new AttackAnimation[] {Animations.GREATSWORD_THRUST, Animations.GREATSWORD_UPWARD_SLASH})
			.putMove(AttackType.DASH, true, Animations.GREATSWORD_DASH_ATTACK)
			.putMove(AttackType.BACKSTAB, true, Animations.BACKSTAB_THRUST)
			.putMove(AttackType.PUNISH, true, Animations.PUNISH_THRUST)
			.putMove(AttackType.TWO_HANDED_LIGHT, true, Animations.GREATSWORD_TH_LIGHT_ATTACK)
			.putMove(AttackType.TWO_HANDED_HEAVY, true, Animations.GREATSWORD_TH_THRUST_ATTACK)
			.putMove(AttackType.TWO_HANDED_DASH, true, Animations.GREATSWORD_TH_DASH_ATTACK),
			new WeaponMoveset.Builder(WeaponMovesets.ULTRA_GREATSWORD)
			.putMove(AttackType.LIGHT, true, Animations.ULTRA_GREATSWORD_LIGHT_ATTACK)
			.putMove(AttackType.HEAVY, false, Animations.ULTRA_GREATSWORD_HEAVY_ATTACK)
			.putMove(AttackType.DASH, true, Animations.ULTRA_GREATSWORD_DASH_ATTACK)
			.putMove(AttackType.BACKSTAB, true, Animations.BACKSTAB_STRIKE)
			.putMove(AttackType.PUNISH, true, Animations.PUNISH_STRIKE)
			.putMove(AttackType.TWO_HANDED_LIGHT, true, Animations.ULTRA_GREATSWORD_TH_LIGHT_ATTACK)
			.putMove(AttackType.TWO_HANDED_HEAVY, false, Animations.ULTRA_GREATSWORD_TH_HEAVY_ATTACK)
			.putMove(AttackType.TWO_HANDED_DASH, true, Animations.ULTRA_GREATSWORD_TH_DASH_ATTACK),
			new WeaponMoveset.Builder(WeaponMovesets.SHIELD)
			.putMove(AttackType.LIGHT, true, Animations.SHIELD_LIGHT_ATTACK)
			.putMove(AttackType.HEAVY, true, Animations.SHIELD_HEAVY_ATTACK)
			.putMove(AttackType.DASH, true, Animations.SHIELD_DASH_ATTACK)
			.putMove(AttackType.TWO_HANDED_LIGHT, true, Animations.SHIELD_TH_LIGHT_ATTACK)
			.putMove(AttackType.TWO_HANDED_HEAVY, true, Animations.SHIELD_TH_HEAVY_ATTACK)
			.putMove(AttackType.TWO_HANDED_DASH, true, Animations.SHIELD_TH_DASH_ATTACK),
			new WeaponMoveset.Builder(WeaponMovesets.SPEAR)
			.putMove(AttackType.LIGHT, true, Animations.SPEAR_LIGHT_ATTACK)
			.putMove(AttackType.HEAVY, true, Animations.SPEAR_HEAVY_ATTACK)
			.putMove(AttackType.DASH, true, Animations.SPEAR_DASH_ATTACK)
			.putMove(AttackType.BACKSTAB, true, Animations.BACKSTAB_THRUST)
			.putMove(AttackType.PUNISH, true, Animations.PUNISH_THRUST)
			.putMove(AttackType.TWO_HANDED_LIGHT, true, Animations.SPEAR_TH_LIGHT_ATTACK)
			.putMove(AttackType.TWO_HANDED_HEAVY, true, Animations.SPEAR_TH_HEAVY_ATTACK)
			.putMove(AttackType.TWO_HANDED_DASH, true, Animations.SPEAR_TH_DASH_ATTACK),
			new WeaponMoveset.Builder(WeaponMovesets.HAMMER)
			.putMove(AttackType.LIGHT, true, Animations.HAMMER_LIGHT_ATTACK)
			.putMove(AttackType.HEAVY, true, Animations.HAMMER_HEAVY_ATTACK)
			.putMove(AttackType.DASH, true, Animations.HAMMER_DASH_ATTACK)
			.putMove(AttackType.BACKSTAB, true, Animations.BACKSTAB_STRIKE)
			.putMove(AttackType.PUNISH, true, Animations.PUNISH_STRIKE)
			.putMove(AttackType.TWO_HANDED_LIGHT, true, Animations.HAMMER_TH_LIGHT_ATTACK)
			.putMove(AttackType.TWO_HANDED_HEAVY, true, Animations.HAMMER_TH_HEAVY_ATTACK)
			.putMove(AttackType.TWO_HANDED_DASH, true, Animations.HAMMER_TH_DASH_ATTACK),
			new WeaponMoveset.Builder(WeaponMovesets.DAGGER)
			.putMove(AttackType.LIGHT, true, Animations.DAGGER_LIGHT_ATTACK)
			.putMove(AttackType.HEAVY, true, Animations.DAGGER_HEAVY_ATTACK)
			.putMove(AttackType.DASH, true, Animations.STRAIGHT_SWORD_DASH_ATTACK)
			.putMove(AttackType.BACKSTAB, true, Animations.BACKSTAB_THRUST),
			new WeaponMoveset.Builder(WeaponMovesets.GREAT_HAMMER)
			.putMove(AttackType.LIGHT, true, Animations.GREAT_HAMMER_LIGHT_ATTACK)
			.putMove(AttackType.HEAVY, false, Animations.GREAT_HAMMER_HEAVY_ATTACK)
			.putMove(AttackType.DASH, true, Animations.GREAT_HAMMER_DASH_ATTACK)
			.putMove(AttackType.BACKSTAB, true, Animations.BACKSTAB_STRIKE)
			.putMove(AttackType.PUNISH, true, Animations.PUNISH_STRIKE)
			.putMove(AttackType.TWO_HANDED_LIGHT, true, Animations.GREAT_HAMMER_TH_LIGHT_ATTACK)
			.putMove(AttackType.TWO_HANDED_HEAVY, true, Animations.GREAT_HAMMER_TH_HEAVY_ATTACK)
			.putMove(AttackType.TWO_HANDED_DASH, true, Animations.GREAT_HAMMER_TH_DASH_ATTACK)
		);
	}
	
	private static Path createPath(Path path, ResourceLocation location)
	{
		return path.resolve("data/" + location.getNamespace() + "/weapon_movesets/" + location.getPath() + ".json");
	}

	@Override
	public String getName()
	{
		return "WeaponMovesets";
	}
}
