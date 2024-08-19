package com.skullmangames.darksouls.core.init.data;

import java.util.Map;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.core.util.AbstractGetter;
import com.skullmangames.darksouls.core.util.collider.Collider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class Colliders extends AbstractDSDataRegister
{
	private static final Logger LOGGER = LogUtils.getLogger();
	private Map<ResourceLocation, Collider> colliders = ImmutableMap.of();
	
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
	protected void apply(Map<ResourceLocation, JsonElement> objects, ResourceManager resourceManager)
	{
		ImmutableMap.Builder<ResourceLocation, Collider> builder = ImmutableMap.builder();
		objects.forEach((location, json) ->
		{
			try
			{
				Collider collider = Collider.CoreBuilder.fromJson(location, json.getAsJsonObject()).build();
				builder.put(location, collider);
			}
			catch (IllegalArgumentException | JsonParseException jsonparseexception)
			{
				LOGGER.error("Parsing error loading collider {}", location, jsonparseexception);
			}
		});
		this.colliders = builder.build();
		
		LOGGER.info("Loaded "+this.colliders.size()+" colliders");
	}
	
	public static Collider getCollider(ResourceLocation id)
	{
		Colliders register = DarkSouls.getInstance().colliders;
		if (register.colliders.containsKey(id)) return register.colliders.get(id);
		throw new IllegalArgumentException("Unable to find collider with path: " + id);
	}
	
	public static class Getter extends AbstractGetter<Collider>
	{
		private Getter(ResourceLocation id)
		{
			super(id);
		}
		
		public Collider get()
		{
			return Colliders.getCollider(this.getId());
		}
	}
}