package com.skullmangames.darksouls.client.renderer.entity.model;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.core.util.math.vector.ModMatrix4f;
import com.skullmangames.darksouls.core.util.math.vector.Vec4f;
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

	public void draw(PoseStack posestack, VertexConsumer builder, int packedLight,
			float r, float g, float b, float a, ModMatrix4f[] poses, @Nullable Set<Integer> jointMask)
	{
		float[] animatedPosition = this.mesh.positionList.clone();
		float[] animatedNormal = this.mesh.normalList.clone();
		int weightIndex = 0;
		
		Set<Integer> blacklist = new HashSet<>();
		
		for(int i = 0; i < this.mesh.vertexCount; i++)
		{
			int k = i * 3;
			Vec4f totalPos = new Vec4f(0.0F, 0.0F, 0.0F, 0.0F);
			Vec4f totalNormal = new Vec4f(0.0F, 0.0F, 0.0F, 0.0F);
			Vec4f pos = new Vec4f(animatedPosition[k], animatedPosition[k + 1], animatedPosition[k + 2], 1.0F);
			Vec4f normal = new Vec4f(animatedNormal[k], animatedNormal[k + 1], animatedNormal[k + 2], 1.0F);
			
			for(int j = 0; j < this.mesh.weightCountList[i]; j++)
			{
				if(weightIndex < this.mesh.weightList.length)
				{
					float weight = this.mesh.weightList[weightIndex];
					int jointId = this.mesh.jointIdList[weightIndex++];
					
					if (jointMask != null && jointMask.contains(jointId)) blacklist.add(i);
					
					ModMatrix4f pose = poses[jointId];
					totalPos = ModMatrix4f.transform(pose, pos).scale(weight).add(totalPos);
					totalNormal = ModMatrix4f.transform(pose, normal).scale(weight).add(totalNormal);
				}
			}
			
			animatedPosition[k] = totalPos.x;
			animatedPosition[k + 1] = totalPos.y;
			animatedPosition[k + 2] = totalPos.z;
			animatedNormal[k] = totalNormal.x;
			animatedNormal[k + 1] = totalNormal.y;
			animatedNormal[k + 2] = totalNormal.z;
		}
		
		Matrix4f matrix4f = posestack.last().pose();
		Matrix3f matrix3f = posestack.last().normal();
		
		for(int i = 0; i < this.mesh.indexCount; i++)
		{
			int index = this.mesh.indexList[i];
			int im2 = index * 2;
			int im3 = index * 3;
			
			boolean cancel = blacklist.contains(index);
			
			Vector4f position = new Vector4f(animatedPosition[im3], animatedPosition[im3 + 1], animatedPosition[im3 + 2], 1.0F);
			Vector3f normal = new Vector3f(animatedNormal[im3], animatedNormal[im3 + 1], animatedNormal[im3 + 2]);
			position.transform(matrix4f);
			normal.transform(matrix3f);
			
			builder.vertex(position.x(), position.y(), position.z(), r, g, b, cancel ? 0 : a,
					cancel ? 0 : this.mesh.textureList[im2], cancel ? 0 : this.mesh.textureList[im2 + 1], OverlayTexture.NO_OVERLAY,
							packedLight, normal.x(), normal.y(), normal.z());
		}
	}
}