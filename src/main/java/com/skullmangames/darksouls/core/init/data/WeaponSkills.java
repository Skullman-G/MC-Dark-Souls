package com.skullmangames.darksouls.core.init.data;

import java.util.Map;
import org.slf4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.core.util.WeaponSkill;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class WeaponSkills extends AbstractDSDataRegister
{
	private static final Logger LOGGER = LogUtils.getLogger();
	private Map<ResourceLocation, WeaponSkill> skills = ImmutableMap.of();
	
	public WeaponSkills()
	{
		super("weapon_skills");
	}
	
	
	public static final ResourceLocation PARRY = DarkSouls.rl("parry");
	public static final ResourceLocation FAST_PARRY = DarkSouls.rl("fast_parry");
	public static final ResourceLocation GREATSHIELD_BASH = DarkSouls.rl("greatshield_bash");
	
	
	@Override
	protected void apply(Map<ResourceLocation, JsonElement> objects, ResourceManager resourceManager)
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
	
	public static WeaponSkill getSkill(ResourceLocation id)
	{
		WeaponSkills register = DarkSouls.getInstance().weaponSkills;
		if (register.skills.containsKey(id)) return register.skills.get(id);
		throw new IllegalArgumentException("Unable to find weapon skill with path: " + id);
	}
}
