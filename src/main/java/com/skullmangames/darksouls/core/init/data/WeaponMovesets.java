package com.skullmangames.darksouls.core.init.data;

import java.util.Map;
import org.slf4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.core.util.AbstractGetter;
import com.skullmangames.darksouls.core.util.WeaponMoveset;

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
	
	public static final Getter FIST = new Getter(DarkSouls.rl("fist"));
	
	public static final Getter STRAIGHT_SWORD = new Getter(DarkSouls.rl("straight_sword"));
	
	public static final Getter AXE = new Getter(DarkSouls.rl("axe"));
	
	public static final Getter BLACK_KNIGHT_SWORD = new Getter(DarkSouls.rl("black_knight_sword"));
	
	public static final Getter CLAYMORE = new Getter(DarkSouls.rl("claymore"));
	
	public static final Getter ULTRA_GREATSWORD = new Getter(DarkSouls.rl("ultra_greatsword"));
	
	public static final Getter SHIELD = new Getter(DarkSouls.rl("shield"));
	
	public static final Getter GREATSHIELD = new Getter(DarkSouls.rl("greatshield"));
	
	public static final Getter SPEAR = new Getter(DarkSouls.rl("spear"));
	
	public static final Getter HAMMER = new Getter(DarkSouls.rl("hammer"));
	
	public static final Getter DAGGER = new Getter(DarkSouls.rl("dagger"));
	
	public static final Getter GREAT_HAMMER = new Getter(DarkSouls.rl("great_hammer"));
	
	public static final Getter THRUSTING_SWORD = new Getter(DarkSouls.rl("thrusting_sword"));
	
	public static final Getter GREATAXE = new Getter(DarkSouls.rl("greataxe"));

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
		
		LOGGER.info("Loaded "+this.movesets.size()+" weapon movesets");
	}
	
	public static WeaponMoveset getMoveset(ResourceLocation id)
	{
		WeaponMovesets manager = DarkSouls.getInstance().weaponMovesets;
		if (manager.movesets.containsKey(id)) return manager.movesets.get(id);
		throw new IllegalArgumentException("Unable to find weapon moveset with path: " + id);
	}
	
	public static class Getter extends AbstractGetter<WeaponMoveset>
	{
		private Getter(ResourceLocation id)
		{
			super(id);
		}
		
		public WeaponMoveset get()
		{
			return WeaponMovesets.getMoveset(this.getId());
		}
	}
}
