package com.skullmangames.darksouls.core.util.parser.xml.collada;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.Lists;
import net.minecraft.util.math.vector.Vector3f;
import com.skullmangames.darksouls.core.util.math.vector.Vector2f;

public class VertexData
{
	public enum State
	{
		EMPTY, EQUAL, DIFFERENT;
	}
	
	private Vector3f position;
	private Vector3f normal;
	private Vector2f textureCoordinate;
	private Vector3f effectiveJointIds;
	private Vector3f effectiveJointWeights;
	private int effectiveJointNumber;
	
	public VertexData()
	{
		this.position = null;
		this.normal = null;
		this.textureCoordinate = null;
	}
	
	public VertexData(VertexData vertex)
	{
		this.position = vertex.position;
		this.effectiveJointIds = vertex.effectiveJointIds;
		this.effectiveJointWeights = vertex.effectiveJointWeights;
		this.effectiveJointNumber = vertex.effectiveJointNumber;
	}
	
	public VertexData setEffectiveJointIds(Vector3f effectivejointids)
	{
		this.effectiveJointIds = effectivejointids;
		return this;
	}

	public VertexData setEffectiveJointWeights(Vector3f effectivejointweights)
	{
		this.effectiveJointWeights = effectivejointweights;
		return this;
	}
	
	public VertexData setEffectiveJointNumber(int count)
	{
		this.effectiveJointNumber = count;
		return this;
	}
	
	public VertexData setPosition(Vector3f position)
	{
		this.position = position;
		return this;
	}
	
	public VertexData setNormal(Vector3f vector)
	{
		this.normal = vector;
		return this;
	}

	public VertexData setTextureCoordinate(Vector2f vector)
	{
		this.textureCoordinate = vector;
		return this;
	}

	public VertexData setEffectiveJointIDs(Vector3f effectiveJointIDs)
	{
		this.effectiveJointIds = effectiveJointIDs;
		return this;
	}
	
	public static Mesh loadVertexInformation(List<VertexData> vertices, int[] indices, boolean animated)
	{
		List<Float> positions = Lists.<Float>newArrayList();
		List<Float> normals = Lists.<Float>newArrayList();
		List<Float> texCoords = Lists.<Float>newArrayList();
		List<Integer> jointIndices = Lists.<Integer>newArrayList();
		List<Float> jointWeights = Lists.<Float>newArrayList();
		List<Integer> effectJointCount = Lists.<Integer>newArrayList();
		
		for (int i = 0; i < vertices.size(); i++)
		{
			VertexData vertex = vertices.get(i);
			Vector3f position = vertex.position;
			Vector3f normal = vertex.normal;
			Vector2f texCoord = vertex.textureCoordinate;
			positions.add(position.x());
			positions.add(position.y());
			positions.add(position.z());
			normals.add(normal.x());
			normals.add(normal.y());
			normals.add(normal.z());
			texCoords.add(texCoord.x);
			texCoords.add(texCoord.y);

			if (animated)
			{
				Vector3f effectIDs = vertex.effectiveJointIds;
				Vector3f weights = vertex.effectiveJointWeights;
				int count = Math.min(vertex.effectiveJointNumber, 3);
				effectJointCount.add(count);
				for(int j = 0; j < count; j++)
				{
					switch(j)
					{
						case 0:
							jointIndices.add((int) effectIDs.x());
							jointWeights.add(weights.x());
							break;
							
						case 1:
							jointIndices.add((int) effectIDs.y());
							jointWeights.add(weights.y());
							break;
							
						case 2:
							jointIndices.add((int) effectIDs.z());
							jointWeights.add(weights.z());
							break;
							
						default:
					}
				}
			}
		}
		
		float[] positionList = ArrayUtils.toPrimitive(positions.toArray(new Float[0]));
		float[] normalList = ArrayUtils.toPrimitive(normals.toArray(new Float[0]));
		float[] texCoordList = ArrayUtils.toPrimitive(texCoords.toArray(new Float[0]));
		int[] jointIndexList = ArrayUtils.toPrimitive(jointIndices.toArray(new Integer[0]));
		float[] jointWeightList = ArrayUtils.toPrimitive(jointWeights.toArray(new Float[0]));
		int[] jointCountList = ArrayUtils.toPrimitive(effectJointCount.toArray(new Integer[0]));
		
		return new Mesh(positionList, normalList, texCoordList, jointIndexList, jointWeightList, indices, jointCountList, positionList.length / 3, indices.length);
	}
	
	public State compareTextureCoordinateAndNormal(Vector3f normal, Vector2f textureCoord)
	{
		if (textureCoordinate == null)
		{
			return State.EMPTY;
		}
		else if (textureCoordinate.equals(textureCoord) && this.normal.equals(normal))
		{
			return State.EQUAL;
		}
		else
		{
			return State.DIFFERENT;
		}
	}
}
