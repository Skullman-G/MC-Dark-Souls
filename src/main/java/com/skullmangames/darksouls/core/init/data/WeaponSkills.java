package com.skullmangames.darksouls.core.init.data;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.core.util.WeaponSkill;
import net.minecraft.resources.ResourceLocation;

public class WeaponSkills extends AbstractDSDataRegister<WeaponSkill.Builder>
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
	protected void finish(Set<WeaponSkill.Builder> builders)
	{
		ImmutableMap.Builder<ResourceLocation, WeaponSkill> mapBuilder = ImmutableMap.builder();
		builders.forEach(builder ->
		{
			mapBuilder.put(builder.getId(), builder.build());
		});
		this.skills = mapBuilder.build();
	}
	
	@Override
	protected WeaponSkill.Builder builderFromJson(ResourceLocation location, JsonObject json)
	{
		return WeaponSkill.Builder.fromJson(location, json);
	}
	
	@Override
	protected Logger getLogger()
	{
		return LOGGER;
	}
	
	public static WeaponSkill getSkill(ResourceLocation id)
	{
		WeaponSkills register = DarkSouls.getInstance().weaponSkills;
		if (register.skills.containsKey(id)) return register.skills.get(id);
		throw new IllegalArgumentException("Unable to find weapon skill with path: " + id);
	}
}
