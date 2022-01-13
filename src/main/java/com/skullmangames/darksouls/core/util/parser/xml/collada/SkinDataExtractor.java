package com.skullmangames.darksouls.core.util.parser.xml.collada;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.core.util.math.vector.Vector3fHelper;
import com.skullmangames.darksouls.core.util.parser.xml.XmlNode;

public class SkinDataExtractor
{
	private static final int MAX_JOINT_LIMIT = 3;
	private XmlNode skin;

	public SkinDataExtractor(XmlNode skin)
	{
		this.skin = skin;
	}

	public Map<String, Integer> getRawJoints()
	{
		Map<String, Integer> map = Maps.<String, Integer>newHashMap();
		String[] weightData = getJointLists(skin);
		for (int i = 0; i < weightData.length; i++) map.put(weightData[i], i);
		return map;
	}
	
	public void extractSkinData(List<VertexData> vertices) {
		String[] weightData = getWeights(skin);
		String[] effectiveJointNumber = getEffectiveJointNumber(skin);
		String[] indices = getIndices(skin);

		int currentIndice = 0;
		int currentVertex = 0;

		for (int i = 0; i < effectiveJointNumber.length; i++) {
			VertexData vertexData = vertices.get(currentVertex);
			Vector3f jointIndices = new Vector3f();
			Vector3f jointWeights = new Vector3f();
			int jointNumber = Integer.parseInt(effectiveJointNumber[i]);

			for (int j = 0; j < jointNumber; j++) {
				if (j < MAX_JOINT_LIMIT) {
					float index = Integer.parseInt(indices[currentIndice]);
					int weightIndex = Integer.parseInt(indices[currentIndice + 1]);
					float weight = Float.parseFloat(weightData[weightIndex]);

					switch (j) {
					case 0:
						jointIndices.setX(index);
						jointWeights.setX(weight);
						break;
					case 1:
						jointIndices.setY(index);
						jointWeights.setY(weight);
						break;
					case 2:
						jointIndices.setZ(index);
						jointWeights.setZ(weight);
						break;
					default:
						break;
					}
				}

				currentIndice += 2;
			}

			float total = jointWeights.x() + jointWeights.y() + jointWeights.z();
			float expandRatio = 1.0f / total;
			Vector3fHelper.scale(jointWeights, expandRatio);

			vertexData.setEffectiveJointIDs(jointIndices);
			vertexData.setEffectiveJointWeights(jointWeights);
			vertexData.setEffectiveJointNumber(jointNumber);
			currentVertex++;
		}
	}

	private static String[] getWeights(XmlNode node) {
		String weightID = node.getChild("vertex_weights").getChildWithAttributeValue("input", "semantic", "WEIGHT").getAttributeValue("source").substring(1);
		XmlNode weightData = node.getChildWithAttributeValue("source", "id", weightID).getChild("float_array");

		return weightData.getData().split(" ");
	}

	private static String[] getEffectiveJointNumber(XmlNode node) {
		XmlNode vertexNumberData = node.getChild("vertex_weights").getChild("vcount");

		return vertexNumberData.getData().split(" ");
	}

	private static String[] getIndices(XmlNode node) {
		XmlNode vertexNumberData = node.getChild("vertex_weights").getChild("v");

		return vertexNumberData.getData().split(" ");
	}

	private static String[] getJointLists(XmlNode node)
	{
		XmlNode jointData = node.getChildWithAttributeValue("source", "id", "Armature_Cube-skin-joints").getChild("Name_array");
		return jointData.getData().split(" ");
	}
}
