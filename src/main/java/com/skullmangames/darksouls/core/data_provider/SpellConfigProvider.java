package com.skullmangames.darksouls.core.data_provider;

import java.util.List;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.common.capability.item.SpellCap;
import com.skullmangames.darksouls.common.entity.stats.Stats;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.core.util.SpellType;
import net.minecraft.data.DataGenerator;

public class SpellConfigProvider extends ConfigDataProvider<SpellCap.Builder>
{
	private static final Logger LOGGER = LogUtils.getLogger();
	
	public SpellConfigProvider(DataGenerator generator)
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
		return "spell_configs";
	}

	@Override
	public String getName()
	{
		return "SpellConfigs";
	}
	
	@Override
	protected List<SpellCap.Builder> data()
	{
		return ImmutableList.of
		(
				//Miracles
				SpellCap.builder(ModItems.MIRACLE_HEAL.get(), SpellType.MIRACLE, 45F, Animations.BIPED_CAST_MIRACLE_HEAL.getId())
				.putStatReq(Stats.INTELLIGENCE, 0)
				.putStatReq(Stats.FAITH, 12),
				
				SpellCap.builder(ModItems.MIRACLE_HEAL_AID.get(), SpellType.MIRACLE, 27F, Animations.BIPED_CAST_MIRACLE_HEAL_AID.getId())
				.putStatReq(Stats.INTELLIGENCE, 0)
				.putStatReq(Stats.FAITH, 8),
				
				SpellCap.builder(ModItems.MIRACLE_HOMEWARD.get(), SpellType.MIRACLE, 30F, Animations.BIPED_CAST_MIRACLE_HOMEWARD.getId())
				.putStatReq(Stats.INTELLIGENCE, 0)
				.putStatReq(Stats.FAITH, 18),
				
				SpellCap.builder(ModItems.MIRACLE_FORCE.get(), SpellType.MIRACLE, 26F, Animations.BIPED_CAST_MIRACLE_FORCE.getId())
				.putStatReq(Stats.INTELLIGENCE, 0)
				.putStatReq(Stats.FAITH, 12),
				
				SpellCap.builder(ModItems.MIRACLE_LIGHTNING_SPEAR.get(), SpellType.MIRACLE, 23F, Animations.BIPED_CAST_MIRACLE_LIGHTNING_SPEAR.getId(),
						Animations.HORSEBACK_CAST_MIRACLE_LIGHTNING_SPEAR.getId())
				.putStatReq(Stats.INTELLIGENCE, 0)
				.putStatReq(Stats.FAITH, 20),
				
				SpellCap.builder(ModItems.MIRACLE_GREAT_LIGHTNING_SPEAR.get(), SpellType.MIRACLE, 32F, Animations.BIPED_CAST_MIRACLE_GREAT_LIGHTNING_SPEAR.getId(),
						Animations.HORSEBACK_CAST_MIRACLE_GREAT_LIGHTNING_SPEAR.getId())
				.putStatReq(Stats.INTELLIGENCE, 0)
				.putStatReq(Stats.FAITH, 30)
		);
	}
}
