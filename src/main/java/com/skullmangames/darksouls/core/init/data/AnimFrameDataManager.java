package com.skullmangames.darksouls.core.init.data;

import java.io.BufferedReader;
import java.util.Set;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.animation.AnimFrameData;
import com.skullmangames.darksouls.core.util.parser.xml.XmlNode;
import com.skullmangames.darksouls.core.util.parser.xml.XmlParser;
import net.minecraft.resources.ResourceLocation;

public class AnimFrameDataManager extends DSDataRegister<AnimFrameData.Builder>
{
	private static final Logger LOGGER = LogUtils.getLogger();
	
	private ImmutableMap<ResourceLocation, AnimFrameData> frameData = ImmutableMap.of();
	
	public static AnimFrameData getByID(ResourceLocation id)
	{
		AnimFrameDataManager register = DarkSouls.getInstance().frameDataManager;
		return register.frameData.get(id);
	}
	
	@Override
	public String getPathSuffix()
	{
		return ".dae";
	}

	@Override
	public String getDirectory()
	{
		return "animations";
	}
	
	@Override
	public Logger getLogger()
	{
		return LOGGER;
	}
	
	@Override
	protected AnimFrameData.Builder parseFromFile(ResourceLocation fileID, BufferedReader reader) throws Exception
	{
		XmlNode collada = XmlParser.loadXmlFile(reader);
		return AnimFrameData.read(fileID, collada);
	}

	@Override
	protected void finish(Set<AnimFrameData.Builder> objects)
	{
		ImmutableMap.Builder<ResourceLocation, AnimFrameData> mapBuilder = ImmutableMap.builder();
		objects.forEach(builder ->
		{
			mapBuilder.put(builder.getId(), builder.build());
		});
		this.frameData = mapBuilder.build();
	}
}
