package com.skullmangames.darksouls.core.data_provider;

import java.util.List;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.common.capability.item.ThrowableCap;
import com.skullmangames.darksouls.core.init.ModEntities;
import com.skullmangames.darksouls.core.init.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;

public class ThrowableConfigProvider extends ConfigDataProvider<ThrowableCap.Builder>
{
	private static final Logger LOGGER = LogUtils.getLogger();
	
	public ThrowableConfigProvider(DataGenerator generator)
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
		return "throwables";
	}

	@Override
	public String getName()
	{
		return "ThrowableConfig";
	}
	
	@Override
	protected List<ThrowableCap.Builder> data()
	{
		return ImmutableList.of
		(
				ThrowableCap.builder(Items.SNOWBALL, EntityType.SNOWBALL, () -> SoundEvents.SNOWBALL_THROW),
				ThrowableCap.builder(Items.EGG, EntityType.EGG, () -> SoundEvents.EGG_THROW),
				
				ThrowableCap.builder(ModItems.FIREBOMB.get(), ModEntities.FIREBOMB.get(), () -> SoundEvents.SNOWBALL_THROW),
				ThrowableCap.builder(ModItems.BLACK_FIREBOMB.get(), ModEntities.BLACK_FIREBOMB.get(), () -> SoundEvents.SNOWBALL_THROW)
		);
	}
}
