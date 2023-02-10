package com.skullmangames.darksouls.common.structures;

import java.util.ArrayList;
import java.util.List;

import com.skullmangames.darksouls.core.init.ModEntities;

import net.minecraft.world.biome.MobSpawnInfo.Spawners;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class FalconerCamp extends AbstractSurfaceStructure
{
	public FalconerCamp()
	{
		super(NoFeatureConfig.CODEC, "falconer_camp/falconer_tent");
	}
	
	@Override
	public List<Spawners> getDefaultSpawnList()
	{
		List<Spawners> spawns = new ArrayList<>();
		spawns.add(new Spawners(ModEntities.FALCONER.get(), 2, 1, 1));
		return spawns;
	}
}
