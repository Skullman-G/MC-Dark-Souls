package com.skullmangames.darksouls.client.renderer.entity.model;

import java.io.IOException;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.parser.xml.collada.ColladaParser;

import net.minecraft.util.ResourceLocation;

public class Model
{
	protected Armature armature;
	protected ResourceLocation location;

	public Model(ResourceLocation location)
	{
		this.location = location;
	}

	public void loadArmatureData()
	{
		try
		{
			this.armature = ColladaParser.getArmature(location);
		}
		catch (IOException e)
		{
			DarkSouls.LOGGER.error(location.getNamespace() + " failed to load!");
		}
	}

	public void loadArmatureData(Armature armature)
	{
		this.armature = armature;
	}

	public Armature getArmature()
	{
		return armature;
	}
}