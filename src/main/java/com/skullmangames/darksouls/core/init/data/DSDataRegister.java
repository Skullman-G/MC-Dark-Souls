package com.skullmangames.darksouls.core.init.data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;

import com.skullmangames.darksouls.core.util.ResourceBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public abstract class DSDataRegister<T extends ResourceBuilder<?>>
{
	public abstract String getDirectory();
	
	public abstract String getPathSuffix();
	
	public abstract Logger getLogger();
	
	protected abstract T parseFromFile(ResourceLocation fileID, BufferedReader reader) throws Exception;
	
	protected abstract void finish(Set<T> objects);
	
	public final void load(ResourceManager resourceManager)
	{
		Set<T> objects = new HashSet<>();
		for (ResourceLocation fileLocation : resourceManager.listResources(this.getDirectory(),
				(path) -> path.endsWith(this.getPathSuffix())))
		{
			ResourceLocation fileID = this.getFileID(fileLocation);
			
			try (Resource resource = resourceManager.getResource(fileLocation);
					InputStream inputstream = resource.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8)))
			{
				T object = this.parseFromFile(fileID, reader);
				if (object != null)
				{
					objects.add(object);
				}
				else
				{
					this.getLogger().error("Couldn't load config file {} from {} as it's null or empty", fileID, fileLocation);
				}
			}
			catch (Exception e)
			{
				this.getLogger().error("Couldn't parse file {}", fileLocation, e);
			}
		}
		this.finish(objects);
	}
	
	private ResourceLocation getFileID(ResourceLocation fileLocation)
	{
		String path = fileLocation.getPath();
		String id = path.substring(this.getDirectory().length() + 1, path.length() - this.getPathSuffix().length());
		return new ResourceLocation(fileLocation.getNamespace(), id);
	}
}
