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
import com.skullmangames.darksouls.common.capability.item.ItemCapability;
import com.skullmangames.darksouls.common.capability.item.ReloadableCap;
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
	private static WeaponMoveset fistMoveset = WeaponMoveset.EMPTY;
	
	public WeaponMovesets()
	{
		super(GSON, "weapon_movesets");
	}
	
	public static final ResourceLocation FIST = DarkSouls.rl("fist");
	
	public static final ResourceLocation STRAIGHT_SWORD = DarkSouls.rl("straight_sword");
	
	public static final ResourceLocation AXE = DarkSouls.rl("axe");
	
	public static final ResourceLocation BLACK_KNIGHT_SWORD = DarkSouls.rl("black_knight_sword");
	
	public static final ResourceLocation ULTRA_GREATSWORD = DarkSouls.rl("ultra_greatsword");
	
	public static final ResourceLocation SHIELD = DarkSouls.rl("shield");
	
	public static final ResourceLocation SPEAR = DarkSouls.rl("spear");
	
	public static final ResourceLocation HAMMER = DarkSouls.rl("hammer");
	
	public static final ResourceLocation DAGGER = DarkSouls.rl("dagger");
	
	public static final ResourceLocation GREAT_HAMMER = DarkSouls.rl("great_hammer");

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
		
		fistMoveset = getByLocation(FIST).orElse(WeaponMoveset.EMPTY);
		for (ItemCapability cap : ProviderItem.CAPABILITIES.values())
		{
			if (cap instanceof ReloadableCap) ((ReloadableCap)cap).reload();
		}
		
		LOGGER.info("Loaded "+this.movesets.size()+" weapon movesets");
	}
	
	public static WeaponMoveset getFistMoveset()
	{
		return fistMoveset;
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
