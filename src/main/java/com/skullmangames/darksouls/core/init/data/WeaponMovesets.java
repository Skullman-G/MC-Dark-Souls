package com.skullmangames.darksouls.core.init.data;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.core.util.AbstractGetter;
import com.skullmangames.darksouls.core.util.WeaponMoveset;
import net.minecraft.resources.ResourceLocation;

public class WeaponMovesets extends DSJsonDataRegister<WeaponMoveset.Builder>
{
	private static final Logger LOGGER = LogUtils.getLogger();
	private Map<ResourceLocation, WeaponMoveset> movesets = ImmutableMap.of();
	
	@Override
	public String getDirectory()
	{
		return "weapon_movesets";
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
	protected void finish(Set<WeaponMoveset.Builder> builders)
	{
		ImmutableMap.Builder<ResourceLocation, WeaponMoveset> mapBuilder = ImmutableMap.builder();
		builders.forEach(builder ->
		{
			mapBuilder.put(builder.getId(), builder.build());
		});
		this.movesets = mapBuilder.build();
	}
	
	@Override
	protected WeaponMoveset.Builder builderFromJson(ResourceLocation location, JsonObject json)
	{
		return WeaponMoveset.Builder.fromJson(location, json);
	}
	
	@Override
	public Logger getLogger()
	{
		return LOGGER;
	}
	
	public static WeaponMoveset getMoveset(ResourceLocation id)
	{
		WeaponMovesets register = DarkSouls.getInstance().weaponMovesets;
		if (register.movesets.containsKey(id)) return register.movesets.get(id);
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
