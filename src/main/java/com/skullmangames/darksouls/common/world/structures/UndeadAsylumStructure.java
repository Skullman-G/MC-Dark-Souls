package com.skullmangames.darksouls.common.world.structures;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.JigsawFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;

public class UndeadAsylumStructure extends JigsawFeature
{
	public UndeadAsylumStructure(Codec<JigsawConfiguration> codec)
	{
		super(codec, 0, true, true, (context) -> true);
	}
}
