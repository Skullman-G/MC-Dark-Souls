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
import com.skullmangames.darksouls.core.util.WeaponSkill;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

public class WeaponSkills extends SimpleJsonResourceReloadListener
{
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
	private Map<ResourceLocation, WeaponSkill> skills = ImmutableMap.of();
	
	public WeaponSkills()
	{
		super(GSON, "weapon_skills");
	}
	
	
	public static final ResourceLocation PARRY = DarkSouls.rl("parry");
	public static final ResourceLocation FAST_PARRY = DarkSouls.rl("fast_parry");
	public static final ResourceLocation GREATSHIELD_BASH = DarkSouls.rl("greatshield_bash");
	
	
	@Override
	protected void apply(Map<ResourceLocation, JsonElement> objects, ResourceManager resourceManager, ProfilerFiller profiler)
	{
		ImmutableMap.Builder<ResourceLocation, WeaponSkill> builder = ImmutableMap.builder();
		objects.forEach((location, json) ->
		{
			try
			{
				WeaponSkill skill = WeaponSkill.Builder.fromJson(location, json.getAsJsonObject()).build();
				builder.put(location, skill);
			}
			catch (IllegalArgumentException | JsonParseException jsonparseexception)
			{
				LOGGER.error("Parsing error loading weapon skill {}", location, jsonparseexception);
			}
		});
		this.skills = builder.build();
		
		LOGGER.info("Loaded "+this.skills.size()+" weapon skills");
	}
	
	public static WeaponSkill getFromLocation(ResourceLocation location)
	{
		return DarkSouls.getInstance().weaponSkills.skills.get(location);
	}
}
