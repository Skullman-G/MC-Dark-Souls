package com.skullmangames.darksouls.client.renderer.entity.model;

import java.io.IOException;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;
import com.skullmangames.darksouls.core.util.math.vector.Vector4fHelper;
import com.skullmangames.darksouls.core.util.parser.xml.collada.ColladaParser;
import com.skullmangames.darksouls.core.util.parser.xml.collada.Mesh;

import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientModel extends Model
{
	private Mesh mesh;

	public ClientModel(ResourceLocation location)
	{
		super(location);
	}
	
	public ClientModel(Mesh mesh)
	{
		super(null);
		this.mesh = mesh;
	}
	
	public void loadMeshData()
	{
		try
		{
			this.mesh = ColladaParser.getMeshData(this.location);
		}
		catch (IOException e)
		{
			DarkSouls.LOGGER.error(this.location.getNamespace() + " failed to load!");
		}
	}

	public void draw(PoseStack posestack, VertexConsumer builderIn, int packedLight, float r, float g, float b, float a, PublicMatrix4f[] poses)
	{
		float[] animatedPosition = this.mesh.positionList.clone();
		float[] animatedNormal = this.mesh.normalList.clone();
		int weightIndex = 0;
		
		for(int i = 0; i < this.mesh.vertexCount; i++)
		{
			int k = i * 3;
			Vector4f totalPos = new Vector4f(0.0F, 0.0F, 0.0F, 0.0F);
			Vector4f totalNormal = new Vector4f(0.0F, 0.0F, 0.0F, 0.0F);
			Vector4f pos = new Vector4f(animatedPosition[k], animatedPosition[k + 1], animatedPosition[k + 2], 1.0F);
			Vector4f normal = new Vector4f(animatedNormal[k], animatedNormal[k + 1], animatedNormal[k + 2], 1.0F);
			
			for(int j = 0; j < this.mesh.weightCountList[i]; j++)
			{
				if(weightIndex < this.mesh.weightList.length)
				{
					float weight = this.mesh.weightList[weightIndex];
					PublicMatrix4f pose = poses[this.mesh.jointIdList[weightIndex++]];
					Vector4fHelper.add(Vector4fHelper.scale(PublicMatrix4f.transform(pose, pos, null), weight), totalPos, totalPos);
					Vector4fHelper.add(Vector4fHelper.scale(PublicMatrix4f.transform(pose, normal, null), weight), totalNormal, totalNormal);
				}
			}
			
			animatedPosition[k] = totalPos.x();
			animatedPosition[k + 1] = totalPos.y();
			animatedPosition[k + 2] = totalPos.z();
			animatedNormal[k] = totalNormal.x();
			animatedNormal[k + 1] = totalNormal.y();
			animatedNormal[k + 2] = totalNormal.z();
		}
		
		Matrix4f matrix4f = posestack.last().pose();
		Matrix3f matrix3f = posestack.last().normal();
		
		for(int i = 0; i < this.mesh.indexCount; i++)
		{
			int index = this.mesh.indexList[i];
			int im2 = index * 2;
			int im3 = index * 3;
			Vector4f position = new Vector4f(animatedPosition[im3], animatedPosition[im3 + 1], animatedPosition[im3 + 2], 1.0F);
			Vector3f normal = new Vector3f(animatedNormal[im3], animatedNormal[im3 + 1], animatedNormal[im3 + 2]);
			position.transform(matrix4f);
			normal.transform(matrix3f);
			
			builderIn.vertex(position.x(), position.y(), position.z(), r, g, b, a, this.mesh.textureList[im2], this.mesh.textureList[im2 + 1], OverlayTexture.NO_OVERLAY, packedLight, normal.x(), normal.y(), normal.z());
		}
	}
}