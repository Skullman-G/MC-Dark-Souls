package com.skullmangames.darksouls.core.data_provider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.skullmangames.darksouls.core.util.data.pack_resources.DSDefaultPackResources;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;

public class McAssetRootFileCreator implements DataProvider
{
	private final DataGenerator generator;
	
	public McAssetRootFileCreator(DataGenerator generator)
	{
		this.generator = generator;
	}
	
	@Override
	public void run(HashCache cache) throws IOException
	{
		Path path = this.generator.getOutputFolder().resolve(DSDefaultPackResources.ROOT_DIR_NAME+"/.mcassetsroot");
		Files.createFile(path);
	}

	@Override
	public String getName()
	{
		return "McAssetRootFileCreator";
	}
}
