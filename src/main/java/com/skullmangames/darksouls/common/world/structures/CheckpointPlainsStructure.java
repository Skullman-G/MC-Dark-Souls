package com.skullmangames.darksouls.common.world.structures;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.JigsawFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;

public class CheckpointPlainsStructure extends JigsawFeature
{
	public CheckpointPlainsStructure(Codec<JigsawConfiguration> config)
	{
		super(config, 0, true, true, (context) -> true);
	}
}
