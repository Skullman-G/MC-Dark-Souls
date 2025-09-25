package com.skullmangames.darksouls.core.data_provider;

import java.util.List;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.common.capability.item.SpellcastingWeaponCap;
import com.skullmangames.darksouls.common.capability.item.WeaponCap.Scaling;
import com.skullmangames.darksouls.common.entity.stats.Stats;
import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.core.util.WeaponCategory;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.CoreDamageType;
import net.minecraft.data.DataGenerator;

public class SpellcastingWeaponConfigProvider extends ConfigDataProvider<SpellcastingWeaponCap.Builder>
{
	private static final Logger LOGGER = LogUtils.getLogger();
	
	public SpellcastingWeaponConfigProvider(DataGenerator generator)
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
		return "weapon_configs/spellcasting";
	}

	@Override
	public String getName()
	{
		return "SpellcastingWeaponConfigs";
	}
	
	@Override
	protected List<SpellcastingWeaponCap.Builder> data()
	{
		return ImmutableList.of
		(
				//Talismans
				SpellcastingWeaponCap.builder(ModItems.TALISMAN.get(), WeaponCategory.TALISMAN, 1.00F, 1.00F, 0.5F)
						.putDamage(CoreDamageType.PHYSICAL, 52)
						.putStatInfo(Stats.STRENGTH, 4, Scaling.E)
						.putStatInfo(Stats.DEXTERITY, 0, Scaling.NONE)
						.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
						.putStatInfo(Stats.FAITH, 10, Scaling.B),
				SpellcastingWeaponCap.builder(ModItems.THOROLUND_TALISMAN.get(), WeaponCategory.TALISMAN, 1.00F, 1.15F, 0.3F)
						.putDamage(CoreDamageType.PHYSICAL, 52)
						.putStatInfo(Stats.STRENGTH, 4, Scaling.E)
						.putStatInfo(Stats.DEXTERITY, 0, Scaling.NONE)
						.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
						.putStatInfo(Stats.FAITH, 10, Scaling.B)
		);
	}
}
