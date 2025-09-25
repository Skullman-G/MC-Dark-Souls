package com.skullmangames.darksouls.core.data_provider;

import java.util.List;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.data.WeaponSkills;
import com.skullmangames.darksouls.core.util.WeaponSkill;
import net.minecraft.data.DataGenerator;

public class WeaponSkillProvider extends ConfigDataProvider<WeaponSkill.Builder>
{
	private static final Logger LOGGER = LogUtils.getLogger();
	
	public WeaponSkillProvider(DataGenerator generator)
	{
		super(generator);
	}
	
	@Override
	protected Logger getLogger()
	{
		return LOGGER;
	}
	
	@Override
	protected String getSubFolder()
	{
		return "weapon_skills";
	}

	@Override
	public String getName()
	{
		return "WeaponSkills";
	}
	
	@Override
	protected List<WeaponSkill.Builder> data()
	{
		return ImmutableList.of
		(
			WeaponSkill.mirrorBuilder(WeaponSkills.PARRY, Animations.SHIELD_PARRY_RIGHT.getId(), Animations.SHIELD_PARRY_LEFT.getId()),
			WeaponSkill.mirrorBuilder(WeaponSkills.FAST_PARRY, Animations.BUCKLER_PARRY_RIGHT.getId(), Animations.BUCKLER_PARRY_LEFT.getId()),
			WeaponSkill.mirrorBuilder(WeaponSkills.GREATSHIELD_BASH, Animations.GREATSHIELD_LIGHT_ATTACK.getId(), Animations.GREATSHIELD_BASH.getId())
		);
	}
}
