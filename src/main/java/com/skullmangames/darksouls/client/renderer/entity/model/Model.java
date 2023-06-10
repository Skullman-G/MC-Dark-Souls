package com.skullmangames.darksouls.client.renderer.entity.model;

import java.io.IOException;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.core.util.parser.xml.collada.ColladaParser;

import net.minecraft.resources.ResourceLocation;

public class Model extends AbstractModel
{
	protected Armature armature;
	protected ResourceLocation armatureLocation;

	public Model(ResourceLocation location)
	{
		super(location);
	}
	
	public String getName()
	{
		return location.getPath();
	}

	public void loadArmatureData()
	{
		try
		{
			ResourceLocation loc = this.armatureLocation != null ? this.armatureLocation : this.location;
			this.armature = ColladaParser.getArmature(loc);
		}
		catch (IOException e)
		{
			DarkSouls.LOGGER.error(location.getNamespace() + " failed to load!");
		}
	}

	public void setArmatureLocation(ResourceLocation location)
	{
		this.armatureLocation = location;
	}

	public Armature getArmature()
	{
		return armature;
	}
}