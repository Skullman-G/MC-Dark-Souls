package com.skullmangames.darksouls.parser.xml.collada;

public class Mesh
{
	public final float[] positionList;
	public final float[] normalList;
	public final float[] textureList;
	public final int[] jointIdList;
	public final float[] weightList;
	public final int[] indexList;
	public final int[] weightCountList;
	public final int vertexCount;
	public final int indexCount;
	
	public Mesh(float[] positionList, float[] normalList, float[] textureList, int[] jointIdList, float[] weightList, int[] indexList, int[] weightCountList, int vertexCount, int drawCount)
	{
		this.positionList = positionList;
		this.normalList = normalList;
		this.textureList = textureList;
		this.jointIdList = jointIdList;
		this.weightList = weightList;
		this.indexList = indexList;
		this.weightCountList = weightCountList;
		this.vertexCount = vertexCount;
		this.indexCount = drawCount;
	}
}
