package com.skullmangames.darksouls.core.util.parser.xml.collada;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.animation.Joint;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;
import com.skullmangames.darksouls.core.util.parser.xml.XmlNode;
import com.skullmangames.darksouls.core.util.parser.xml.XmlParser;
import com.skullmangames.darksouls.client.renderer.entity.model.Armature;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ColladaParser
{
	public static Armature getArmature(ResourceLocation path) throws IOException
	{
		BufferedReader bufreader = null;

		try
		{
			bufreader = new BufferedReader(new InputStreamReader(getInputStream(path)));
		}
		catch (FileNotFoundException e)
		{
			DarkSouls.LOGGER.error(e);
		}

		XmlNode rootNode = XmlParser.loadXmlFile(bufreader);
		if (rootNode == null)
		{
			DarkSouls.LOGGER.error("Could not find root node of "+path);
		}
		SkinDataExtractor skin = new SkinDataExtractor(rootNode.getChild("library_controllers").getChild("controller").getChild("skin"));
		JointDataExtractor skeleton = new JointDataExtractor(rootNode.getChild("library_visual_scenes").getChild("visual_scene").getChildWithAttributeValue("node", "id", "Armature"), skin.getRawJoints());
		Joint joint = skeleton.extractSkeletonData();
		joint.setInversedModelTransform(new PublicMatrix4f());
		
		Armature armature = new Armature(skeleton.getJointNumber(), joint, skeleton.getJointTable());
		return armature;
	}
	
	@OnlyIn(Dist.CLIENT)
	public static Mesh getMeshData(ResourceLocation path) throws IOException
	{
		BufferedReader bufferedreader = null;
		try
		{
			bufferedreader = new BufferedReader(new InputStreamReader(getInputStream(path)));
		}
		catch (FileNotFoundException e)
		{
			DarkSouls.LOGGER.error(e);
		}

		XmlNode rootNode = XmlParser.loadXmlFile(bufferedreader);
		GeometryDataExtractor geometry = new GeometryDataExtractor(rootNode.getChild("library_geometries").getChild("geometry").getChild("mesh"));
		SkinDataExtractor skin = new SkinDataExtractor(rootNode.getChild("library_controllers").getChild("controller").getChild("skin"));
		List<VertexData> vertices = geometry.extractVertexNumber();
		skin.extractSkinData(vertices);
		geometry.extractGeometryData(vertices);
		
		Mesh meshdata = VertexData.loadVertexInformation(vertices, geometry.getIndices(), true);
		return meshdata;
	}
	
	public static int[] getIndices(List<Integer> indexlist)
	{
		return ArrayUtils.toPrimitive(indexlist.toArray(new Integer[0]));
	}
	
	private static BufferedInputStream getInputStream(ResourceLocation resourceLocation) throws FileNotFoundException
	{
		BufferedInputStream inputStream = new BufferedInputStream(DarkSouls.class.getResourceAsStream("/assets/"+resourceLocation.getNamespace()+"/models/entity/"+resourceLocation.getPath()+".dae"));
		return inputStream;
	}
}
