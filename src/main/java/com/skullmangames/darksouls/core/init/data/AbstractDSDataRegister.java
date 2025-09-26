package com.skullmangames.darksouls.core.init.data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.skullmangames.darksouls.core.util.json.JsonBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;

public abstract class AbstractDSDataRegister<B extends JsonBuilder<?>>
{
	private static final String PATH_SUFFIX = ".json";
	private static final int PATH_SUFFIX_LENGTH = PATH_SUFFIX.length();
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
	private final String directory;

	public AbstractDSDataRegister(String directory)
	{
		this.directory = directory;
	}
	
	protected abstract Logger getLogger();
	
	public void load(ResourceManager resourceManager)
	{
		this.finish(this.buildersFromJson(this.loadJsonFiles(resourceManager)));
	}
	
	private Set<B> buildersFromJson(Map<ResourceLocation, JsonElement> jsonFiles)
	{
		Set<B> builders = new HashSet<>();
		jsonFiles.forEach((location, json) ->
		{
			try
			{
				builders.add(this.builderFromJson(location, json.getAsJsonObject()));
			}
			catch (IllegalArgumentException | JsonParseException jsonparseexception)
			{
				this.getLogger().error("Parsing error loading config {}", location, jsonparseexception);
			}
		});
		
		this.getLogger().info("Loaded "+builders.size()+" configs");
		
		return builders;
	}
	
	protected abstract void finish(Set<B> builders);
	
	protected abstract B builderFromJson(ResourceLocation location, JsonObject json);

	private Map<ResourceLocation, JsonElement> loadJsonFiles(ResourceManager resourceManager)
	{
		Map<ResourceLocation, JsonElement> jsonFiles = new HashMap<>();
		int i = this.directory.length() + 1;

		for (ResourceLocation fileLocation : resourceManager.listResources(this.directory,
				(path) -> path.endsWith(PATH_SUFFIX)))
		{
			String s = fileLocation.getPath();
			ResourceLocation fileId = new ResourceLocation(fileLocation.getNamespace(),
					s.substring(i, s.length() - PATH_SUFFIX_LENGTH));

			try
			{
				JsonElement jsonelement = this.parseJsonFile(resourceManager, fileLocation);
				if (jsonelement != null)
				{
					JsonElement jsonelement1 = jsonFiles.put(fileId, jsonelement);
					if (jsonelement1 != null)
					{
						throw new IllegalStateException(
								"Duplicate data file ignored with ID " + fileId);
					}
				}
				else
				{
					this.getLogger().error("Couldn't load data file {} from {} as it's null or empty",
							fileId, fileLocation);
				}
			}
			catch (Exception jsonparseexception)
			{
				this.getLogger().error("Couldn't parse data file {} from {}", fileId, fileLocation,
						jsonparseexception);
			}
		}

		return jsonFiles;
	}
	
	private JsonElement parseJsonFile(ResourceManager resourceManager, ResourceLocation fileLocation) throws Exception
	{
		try (Resource resource = resourceManager.getResource(fileLocation);
				InputStream inputstream = resource.getInputStream();
				Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8)))
		{
			return GsonHelper.fromJson(GSON, reader, JsonElement.class);
		}
	}
}
