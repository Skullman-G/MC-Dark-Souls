package com.skullmangames.darksouls.core.data_provider;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.skullmangames.darksouls.core.util.data.pack_resources.DSDefaultPackResources;
import com.skullmangames.darksouls.core.util.json.JsonBuilder;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;

public abstract class ConfigDataProvider<T extends JsonBuilder<?>> implements DataProvider
{
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
	
	private final DataGenerator generator;
	private final Path outputDir;
	
	public ConfigDataProvider(DataGenerator generator)
	{
		this.generator = generator;
		this.outputDir = this.generator.getOutputFolder().resolve(DSDefaultPackResources.ROOT_DIR_NAME);
	}
	
	@Override
	public void run(HashCache cache) throws IOException
	{
		for (T builder : this.data())
		{
			Path filePath = this.createPath(builder.getId());
			try
			{
				DataProvider.save(GSON, cache, builder.toJson(), filePath);
			}
			catch (IOException ioexception)
			{
				this.getLogger().error("Couldn't save json file {}", filePath, ioexception);
			}
		}
	}
	
	protected abstract List<T> data();
	
	protected abstract String getSubFolder();
	
	protected abstract Logger getLogger();

	private Path createPath(ResourceLocation location)
	{
		return this.outputDir.resolve(location.getNamespace()+"/"+this.getSubFolder()+"/"+location.getPath()+".json");
	}
}
