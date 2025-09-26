package com.skullmangames.darksouls.core.init.data;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.core.util.AbstractGetter;
import com.skullmangames.darksouls.core.util.collider.Collider;
import com.skullmangames.darksouls.core.util.collider.ColliderType;
import com.skullmangames.darksouls.core.util.json.JsonBuilder;

import net.minecraft.resources.ResourceLocation;

public class Colliders extends AbstractDSDataRegister<JsonBuilder<ColliderType<?>>>
{
	private static final Logger LOGGER = LogUtils.getLogger();
	private Map<ResourceLocation, ColliderType<?>> colliderTypes = ImmutableMap.of();
	
	public Colliders()
	{
		super("colliders");
	}
	
	public static final Getter FIST = new Getter(DarkSouls.rl("fist"));
	public static final Getter SHORTSWORD = new Getter(DarkSouls.rl("shortsword"));
	public static final Getter LONGSWORD = new Getter(DarkSouls.rl("longsword"));
	public static final Getter BROKEN_SWORD = new Getter(DarkSouls.rl("broken_sword"));
	public static final Getter GREAT_HAMMER = new Getter(DarkSouls.rl("great_hammer"));
	public static final Getter DAGGER = new Getter(DarkSouls.rl("dagger"));
	public static final Getter SPEAR = new Getter(DarkSouls.rl("spear"));
	public static final Getter WINGED_SPEAR = new Getter(DarkSouls.rl("winged_spear"));
	public static final Getter HALBERD = new Getter(DarkSouls.rl("halberd"));
	public static final Getter ULTRA_GREATSWORD = new Getter(DarkSouls.rl("ultra_greatsword"));
	public static final Getter GREATSWORD = new Getter(DarkSouls.rl("greatsword"));
	public static final Getter DEMONS_GREATAXE = new Getter(DarkSouls.rl("demons_greataxe"));
	public static final Getter GREATAXE = new Getter(DarkSouls.rl("greataxe"));
	public static final Getter SHIELD = new Getter(DarkSouls.rl("shield"));
	public static final Getter AXE = new Getter(DarkSouls.rl("axe"));
	public static final Getter BATTLE_AXE = new Getter(DarkSouls.rl("battle_axe"));
	public static final Getter PICKAXE = new Getter(DarkSouls.rl("pickaxe"));
	public static final Getter MACE = new Getter(DarkSouls.rl("mace"));
	
	//Stray Demon
	public static final Getter STRAY_DEMON_GREAT_HAMMER = new Getter(DarkSouls.rl("stray_demon_great_hammer"));
	public static final Getter STRAY_DEMON_BODY = new Getter(DarkSouls.rl("stray_demon_body"));
	
	//Taurus Demon
	public static final Getter TAURUS_DEMON_GREATAXE = new Getter(DarkSouls.rl("taurus_demon_greataxe"));
	
	//Berenike Knight
	public static final Getter BERENIKE_KNIGHT_ULTRA_GREATSWORD = new Getter(DarkSouls.rl("berenike_knight_ultra_greatsword"));
	public static final Getter BERENIKE_KNIGHT_MACE = new Getter(DarkSouls.rl("berenike_knight_mace"));
	
	//Bell Gargoyle
	public static final Getter BELL_GARGOYLE_HALBERD = new Getter(DarkSouls.rl("bell_gargoyle_halberd"));
	
	@Override
	protected void finish(Set<JsonBuilder<ColliderType<?>>> builders)
	{
		ImmutableMap.Builder<ResourceLocation, ColliderType<?>> mapBuilder = ImmutableMap.builder();
		builders.forEach(builder ->
		{
			mapBuilder.put(builder.getId(), builder.build());
		});
		this.colliderTypes = mapBuilder.build();
	}
	
	@Override
	protected JsonBuilder<ColliderType<?>> builderFromJson(ResourceLocation location, JsonObject json)
	{
		return Collider.CoreBuilder.fromJson(location, json);
	}
	
	@Override
	protected Logger getLogger()
	{
		return LOGGER;
	}
	
	public static ColliderType<?> getCollider(ResourceLocation id)
	{
		Colliders register = DarkSouls.getInstance().colliders;
		if (register.colliderTypes.containsKey(id)) return register.colliderTypes.get(id);
		throw new IllegalArgumentException("Unable to find collider with path: " + id);
	}
	
	public static class Getter extends AbstractGetter<ColliderType<?>>
	{
		private Getter(ResourceLocation id)
		{
			super(id);
		}
		
		public ColliderType<?> get()
		{
			return Colliders.getCollider(this.getId());
		}
	}
}