package com.skullmangames.darksouls.core.data_provider;

import java.util.List;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.common.capability.projectile.ProjectileCapability;
import com.skullmangames.darksouls.core.init.ModEntities;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.CoreDamageType;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.StunType;
import net.minecraft.data.DataGenerator;

public class ProjectileConfigProvider extends ConfigDataProvider<ProjectileCapability.Builder>
{
	private static final Logger LOGGER = LogUtils.getLogger();
	
	public ProjectileConfigProvider(DataGenerator generator)
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
		return "projectiles";
	}

	@Override
	public String getName()
	{
		return "ProjectileConfig";
	}
	
	@Override
	protected List<ProjectileCapability.Builder> data()
	{
		return ImmutableList.of
		(
				ProjectileCapability.builder(ModEntities.LIGHTNING_SPEAR.get(), 2F, 1F, StunType.HEAVY, true)
				.putDamage(CoreDamageType.PHYSICAL, 80F)
				.putDamage(CoreDamageType.LIGHTNING, 100F),
				ProjectileCapability.builder(ModEntities.GREAT_LIGHTNING_SPEAR.get(), 4F, 1F, StunType.HEAVY, true)
				.putDamage(CoreDamageType.PHYSICAL, 80F)
				.putDamage(CoreDamageType.LIGHTNING, 150F),
				
				ProjectileCapability.builder(ModEntities.FIREBOMB.get(), 2F, 1F, StunType.HEAVY, false)
				.putDamage(CoreDamageType.PHYSICAL, 80F)
				.putDamage(CoreDamageType.FIRE, 40F),
				ProjectileCapability.builder(ModEntities.BLACK_FIREBOMB.get(), 2F, 1F, StunType.HEAVY, false)
				.putDamage(CoreDamageType.PHYSICAL, 90F)
				.putDamage(CoreDamageType.FIRE, 70F)
		);
	}
}
