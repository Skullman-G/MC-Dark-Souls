package com.skullmangames.darksouls.core.util.parser.xml.collada;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;
import com.skullmangames.darksouls.core.util.parser.xml.XmlNode;

import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;

public class GeometryDataExtractor
{
	private static final PublicMatrix4f CORRECTION = new PublicMatrix4f().rotate((float) Math.toRadians(-90), new Vector3f(1, 0, 0));
	
	private XmlNode geometryNode;
	private List<Integer> indexList = new ArrayList<Integer> ();
	
	public GeometryDataExtractor(XmlNode geometry)
	{
		this.geometryNode = geometry;
	}
	
	public List<VertexData> extractVertexNumber()
	{
		int vertexCount = Integer.parseInt(getVertexNumber(geometryNode));
		List<VertexData> vertices = new ArrayList<VertexData> ();
		
		for(int i = 0; i < vertexCount; i++)
		{
			vertices.add(new VertexData());
		}
		
		return vertices;
	}
	
	public void extractGeometryData(List<VertexData> vertices)
	{
		String[] rawPositionData = getPositions(geometryNode);
		String[] rawNormalData = getNormals(geometryNode);
		String[] rawTextureCoordData = getTextureCoords(geometryNode);
		String[] polyList = getPolyList(geometryNode);
		for(int i = 0; i < rawPositionData.length; i+=3)
		{
			Vector4f original = new Vector4f(Float.parseFloat(rawPositionData[i]), Float.parseFloat(rawPositionData[i+1]), Float.parseFloat(rawPositionData[i+2]), 1);
			PublicMatrix4f.transform(CORRECTION, original, original);
			Vector3f corrected = new Vector3f(original.x(), original.y(), original.z());
			vertices.get(i/3).setPosition(corrected);
		}
		
		for(int i = 0; i < polyList.length; i+=3)
		{
			int positionIndex = Integer.parseInt(polyList[i]);
			int normalIndex = Integer.parseInt(polyList[i+1]);
			int textureIndex = Integer.parseInt(polyList[i+2]);
			
			float normX = Float.parseFloat(rawNormalData[normalIndex*3]);
			float normY = Float.parseFloat(rawNormalData[normalIndex*3 + 1]);
			float normZ = Float.parseFloat(rawNormalData[normalIndex*3 + 2]);
			
			float coordX = Float.parseFloat(rawTextureCoordData[textureIndex*2]);
			float coordY = Float.parseFloat(rawTextureCoordData[textureIndex*2 + 1]);
			
			Vector2f textureCoord = new Vector2f(coordX, (1-coordY));
			Vector4f normal = new Vector4f(normX, normY, normZ, 1.0f);
			PublicMatrix4f.transform(CORRECTION, normal, normal);
			Vector3f normalCorrected = new Vector3f(normal.x(), normal.y(), normal.z());
			VertexData vertex = vertices.get(positionIndex);
			
			switch(vertex.compareTextureCoordinateAndNormal(normalCorrected, textureCoord))
			{
			case EMPTY:
				vertex.setTextureCoordinate(textureCoord);
				vertex.setNormal(normalCorrected);
				indexList.add(positionIndex);
				break;
			case EQUAL:
				indexList.add(positionIndex);
				break;
			case DIFFERENT:
				VertexData newVertex = new VertexData(vertex);
				newVertex.setNormal(normalCorrected);
				newVertex.setTextureCoordinate(textureCoord);
				indexList.add(vertices.size());
				vertices.add(newVertex);
				break;
			}
		}
	}
	
	public int[] getIndices()
	{
		return ArrayUtils.toPrimitive(indexList.toArray(new Integer[0]));
	}
	
	private String getVertexNumber(XmlNode node)
	{
		String positionsId = node.getChild("vertices").getChild("input").getAttributeValue("source").substring(1);
		XmlNode vertexData = node.getChildWithAttributeValue("source", "id", positionsId).getChild("technique_common").getChild("accessor");
		
		return vertexData.getAttributeValue("count");
	}
	
	private String[] getPositions(XmlNode node)
	{
		String positionsId = node.getChild("vertices").getChild("input").getAttributeValue("source").substring(1);
		XmlNode positionsData = node.getChildWithAttributeValue("source", "id", positionsId).getChild("float_array");
		
		return positionsData.getData().split(" ");
	}
	
	private String[] getNormals(XmlNode node)
	{
		String noramlId = node.getChild("triangles").getChildWithAttributeValue("input", "semantic", "NORMAL")
				.getAttributeValue("source").substring(1);
		XmlNode noramlData = node.getChildWithAttributeValue("source", "id", noramlId).getChild("float_array");
		
		return noramlData.getData().split(" ");
	}
	
	private String[] getTextureCoords(XmlNode node)
	{
		String textureCoordId = node.getChild("triangles").getChildWithAttributeValue("input", "semantic", "TEXCOORD")
				.getAttributeValue("source").substring(1);
		XmlNode textureCoordData = node.getChildWithAttributeValue("source", "id", textureCoordId).getChild("float_array");
		
		return textureCoordData.getData().split(" ");
	}
	
	private String[] getPolyList(XmlNode node)
	{
		return node.getChild("triangles").getChild("p").getData().split(" ");
	}
}
