package com.skullmangames.darksouls.core.init;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.capability.item.WeaponMoveset;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

public class WeaponMovesets extends SimpleJsonResourceReloadListener
{
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
	private Map<ResourceLocation, WeaponMoveset> movesets = ImmutableMap.of();
	
	public WeaponMovesets()
	{
		super(GSON, "weapon_movesets");
	}
	
	/*public static final WeaponMoveset FIST = register("fist")
			.putMove(AttackType.LIGHT, true, Animations.FIST_LIGHT_ATTACK)
			.putMove(AttackType.HEAVY, true, Animations.FIST_HEAVY_ATTACK)
			.putMove(AttackType.DASH, true, Animations.FIST_DASH_ATTACK)
			.putMove(AttackType.BACKSTAB, true, Animations.BACKSTAB_STRIKE)
			.build();
	
	public static final WeaponMoveset STRAIGHT_SWORD = register("straight_sword")
			.putMove(AttackType.LIGHT, true, Animations.STRAIGHT_SWORD_LIGHT_ATTACK)
			.putMove(AttackType.HEAVY, true, Animations.STRAIGHT_SWORD_HEAVY_ATTACK)
			.putMove(AttackType.DASH, true, Animations.STRAIGHT_SWORD_DASH_ATTACK)
			.putMove(AttackType.BACKSTAB, true, Animations.BACKSTAB_THRUST)
			.build();
	
	public static final WeaponMoveset AXE = register("axe")
			.putMove(AttackType.LIGHT, true, Animations.AXE_LIGHT_ATTACK)
			.putMove(AttackType.HEAVY, true, Animations.AXE_HEAVY_ATTACK)
			.putMove(AttackType.DASH, true, Animations.AXE_DASH_ATTACK)
			.putMove(AttackType.BACKSTAB, true, Animations.BACKSTAB_STRIKE)
			.build();
	
	public static final WeaponMoveset ULTRA_GREATSWORD = register("ultra_greatsword")
			.putMove(AttackType.LIGHT, true, Animations.ULTRA_GREATSWORD_LIGHT_ATTACK)
			.putMove(AttackType.HEAVY, true, Animations.ULTRA_GREATSWORD_HEAVY_ATTACK)
			.putMove(AttackType.DASH, true, Animations.ULTRA_GREATSWORD_DASH_ATTACK)
			.putMove(AttackType.BACKSTAB, true, Animations.BACKSTAB_STRIKE)
			.build();
	
	public static final WeaponMoveset SHIELD = register("shield")
			.putMove(AttackType.LIGHT, true, Animations.SHIELD_LIGHT_ATTACK)
			.build();
	
	public static final WeaponMoveset SPEAR = register("spear")
			.putMove(AttackType.LIGHT, true, Animations.SPEAR_LIGHT_ATTACK)
			.putMove(AttackType.HEAVY, true, Animations.SPEAR_HEAVY_ATTACK)
			.putMove(AttackType.DASH, true, Animations.SPEAR_DASH_ATTACK)
			.putMove(AttackType.BACKSTAB, true, Animations.BACKSTAB_THRUST)
			.build();
	
	public static final WeaponMoveset HAMMER = register("hammer")
			.putMove(AttackType.LIGHT, true, Animations.HAMMER_LIGHT_ATTACK)
			.putMove(AttackType.HEAVY, true, Animations.HAMMER_HEAVY_ATTACK)
			.putMove(AttackType.DASH, true, Animations.HAMMER_DASH_ATTACK)
			.putMove(AttackType.BACKSTAB, true, Animations.BACKSTAB_STRIKE)
			.build();
	
	public static final WeaponMoveset DAGGER = register("dagger")
			.putMove(AttackType.LIGHT, true, Animations.DAGGER_LIGHT_ATTACK)
			.putMove(AttackType.HEAVY, true, Animations.DAGGER_HEAVY_ATTACK)
			.putMove(AttackType.DASH, true, Animations.STRAIGHT_SWORD_DASH_ATTACK)
			.putMove(AttackType.BACKSTAB, true, Animations.BACKSTAB_THRUST)
			.build();
	
	public static final WeaponMoveset GREAT_HAMMER = register("great_hammer")
			.putMove(AttackType.LIGHT, true, Animations.GREAT_HAMMER_LIGHT_ATTACK)
			.putMove(AttackType.HEAVY, true, Animations.GREAT_HAMMER_HEAVY_ATTACK)
			.putMove(AttackType.DASH, true, Animations.GREAT_HAMMER_DASH_ATTACK)
			.putMove(AttackType.BACKSTAB, true, Animations.BACKSTAB_STRIKE)
			.build();
	
	
	private static WeaponMoveset register(String name)
	{
		return register(DarkSouls.MOD_ID, name);
	}
	
	private static WeaponMoveset register(String namespace, String name)
	{
		ResourceLocation location = new ResourceLocation(namespace, name);
		WeaponMoveset weaponType = new WeaponMoveset(location);
		WEAPON_MOVESETS.put(location, weaponType);
		return weaponType;
	}*/

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> objects, ResourceManager resourceManager, ProfilerFiller profiler)
	{
		ImmutableMap.Builder<ResourceLocation, WeaponMoveset> builder = ImmutableMap.builder();
		objects.forEach((location, json) ->
		{
			try
			{
				WeaponMoveset moveset = WeaponMoveset.Builder.fromJson(location, json.getAsJsonObject()).build();
				builder.put(location, moveset);
			}
			catch (IllegalArgumentException | JsonParseException jsonparseexception)
			{
				LOGGER.error("Parsing error loading weapon moveset {}", location, jsonparseexception);
			}
		});
		this.movesets = builder.build();
	}
	
	public static Optional<WeaponMoveset> getByLocation(ResourceLocation location)
	{
		WeaponMoveset moveset = DarkSouls.getInstance().weaponMovesets.movesets.get(location);
		return Optional.ofNullable(moveset);
	}
	
	public static Optional<WeaponMoveset> getByName(String name)
	{
		ResourceLocation location = new ResourceLocation(name);
		return getByLocation(location);
	}
}
