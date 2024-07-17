package com.skullmangames.darksouls.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.core.util.math.vector.ModMatrix4f;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

public class Gizmos
{
	private static Matrix4f getMatrix4f(Vec3 min, Vec3 max)
	{
		Camera cam = ClientManager.INSTANCE.mainCamera;
		Vec3 camPos = cam.getPosition();
		float camXRot = cam.getXRot();
		float camYRot = cam.getYRot();
		
		PoseStack poseStack = new PoseStack();
		
		Vec3 start2 = camPos.subtract(min);
		Vec3 end2 = camPos.subtract(max);
		Vec3 projCenter = new Vec3((start2.x + end2.x) / 2, -(start2.y + end2.y) / 2, (start2.z + end2.z) / 2);
		
		poseStack.mulPoseMatrix(ModMatrix4f.createRotatorDeg(camXRot, Vector3f.XP).export());
		poseStack.mulPoseMatrix(ModMatrix4f.createRotatorDeg(camYRot, Vector3f.YP).export());
		poseStack.translate(projCenter.x, projCenter.y, projCenter.z);
		
		return poseStack.last().pose();
	}
	
	private static Vec3[] convertVertices(Vec3 min, Vec3 max, Vec3[] vertices)
	{
		Vec3 center = max.add(min).scale(0.5D);
		Vec3[] normalVerts = new Vec3[vertices.length];
		for (int i = 0; i < normalVerts.length; i++)
		{
			normalVerts[i] = vertices[i].subtract(center);
			normalVerts[i] = new Vec3(normalVerts[i].x, -normalVerts[i].y, normalVerts[i].z);
		}
		return normalVerts;
	}
	
	public static void drawLine(Vec3 start, Vec3 end)
	{
		VertexConsumer vertexBuilder = Minecraft.getInstance().renderBuffers().outlineBufferSource().getBuffer(ModRenderTypes.debugCollider());
		Matrix4f mat = getMatrix4f(start, end);
		
		float minX = (float) (start.x - end.x) / 2;
		float minY = (float) (end.y - start.y) / 2;
		float minZ = (float) (start.z - end.z) / 2;
		float maxX = (float) (end.x - start.x) / 2;
		float maxY = (float) (start.y - end.y) / 2;
		float maxZ = (float) (end.z - start.z) / 2;
		
		vertexBuilder.vertex(mat, minX, minY, minZ).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		vertexBuilder.vertex(mat, maxX, maxY, maxZ).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		
		Minecraft.getInstance().renderBuffers().bufferSource().endBatch(ModRenderTypes.debugCollider());
	}
	
	public static void drawBox(Vec3 start, Vec3 end)
	{
		VertexConsumer vertexBuilder = Minecraft.getInstance().renderBuffers().outlineBufferSource().getBuffer(ModRenderTypes.debugCollider());
		Matrix4f mat = getMatrix4f(start, end);
		
		float minX = (float) (start.x - end.x) / 2;
		float minY = (float) (end.y - start.y) / 2;
		float minZ = (float) (start.z - end.z) / 2;
		float maxX = (float) (end.x - start.x) / 2;
		float maxY = (float) (start.y - end.y) / 2;
		float maxZ = (float) (end.z - start.z) / 2;
		
		vertexBuilder.vertex(mat, minX, minY, minZ).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		vertexBuilder.vertex(mat, maxX, minY, minZ).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		vertexBuilder.vertex(mat, maxX, maxY, minZ).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		vertexBuilder.vertex(mat, minX, maxY, minZ).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		vertexBuilder.vertex(mat, minX, minY, minZ).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		vertexBuilder.vertex(mat, minX, minY, maxZ).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		vertexBuilder.vertex(mat, maxX, minY, maxZ).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		vertexBuilder.vertex(mat, maxX, minY, minZ).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		vertexBuilder.vertex(mat, maxX, minY, maxZ).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		vertexBuilder.vertex(mat, maxX, maxY, maxZ).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		vertexBuilder.vertex(mat, maxX, maxY, minZ).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		vertexBuilder.vertex(mat, maxX, maxY, maxZ).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		vertexBuilder.vertex(mat, minX, maxY, maxZ).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		vertexBuilder.vertex(mat, minX, maxY, minZ).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		vertexBuilder.vertex(mat, minX, maxY, maxZ).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		vertexBuilder.vertex(mat, minX, minY, maxZ).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		
		Minecraft.getInstance().renderBuffers().bufferSource().endBatch(ModRenderTypes.debugCollider());
	}
	
	public static void drawBox(Vec3[] vertices, int color)
	{
		VertexConsumer vertexBuilder = Minecraft.getInstance().renderBuffers().outlineBufferSource().getBuffer(ModRenderTypes.debugCollider());
		
		Vec3 min = vertices[0];
		Vec3 max = vertices[6];
		Matrix4f mat = getMatrix4f(min, max);
		vertices = convertVertices(min, max, vertices);
		
		vertexBuilder.vertex(mat, (float)vertices[0].x, (float)vertices[0].y, (float)vertices[0].z).color(color).endVertex();
		vertexBuilder.vertex(mat, (float)vertices[1].x, (float)vertices[1].y, (float)vertices[1].z).color(color).endVertex();
		vertexBuilder.vertex(mat, (float)vertices[2].x, (float)vertices[2].y, (float)vertices[2].z).color(color).endVertex();
		vertexBuilder.vertex(mat, (float)vertices[3].x, (float)vertices[3].y, (float)vertices[3].z).color(color).endVertex();
		vertexBuilder.vertex(mat, (float)vertices[0].x, (float)vertices[0].y, (float)vertices[0].z).color(color).endVertex();
		vertexBuilder.vertex(mat, (float)vertices[4].x, (float)vertices[4].y, (float)vertices[4].z).color(color).endVertex();
		vertexBuilder.vertex(mat, (float)vertices[5].x, (float)vertices[5].y, (float)vertices[5].z).color(color).endVertex();
		vertexBuilder.vertex(mat, (float)vertices[1].x, (float)vertices[1].y, (float)vertices[1].z).color(color).endVertex();
		vertexBuilder.vertex(mat, (float)vertices[5].x, (float)vertices[5].y, (float)vertices[5].z).color(color).endVertex();
		vertexBuilder.vertex(mat, (float)vertices[6].x, (float)vertices[6].y, (float)vertices[6].z).color(color).endVertex();
		vertexBuilder.vertex(mat, (float)vertices[2].x, (float)vertices[2].y, (float)vertices[2].z).color(color).endVertex();
		vertexBuilder.vertex(mat, (float)vertices[6].x, (float)vertices[6].y, (float)vertices[6].z).color(color).endVertex();
		vertexBuilder.vertex(mat, (float)vertices[7].x, (float)vertices[7].y, (float)vertices[7].z).color(color).endVertex();
		vertexBuilder.vertex(mat, (float)vertices[3].x, (float)vertices[3].y, (float)vertices[3].z).color(color).endVertex();
		vertexBuilder.vertex(mat, (float)vertices[7].x, (float)vertices[7].y, (float)vertices[7].z).color(color).endVertex();
		vertexBuilder.vertex(mat, (float)vertices[4].x, (float)vertices[4].y, (float)vertices[4].z).color(color).endVertex();
		
		Minecraft.getInstance().renderBuffers().bufferSource().endBatch(ModRenderTypes.debugCollider());
	}
	
	public static void drawCapsule(Vec3[] vertices, int color)
	{
		VertexConsumer vertexBuilder = Minecraft.getInstance().renderBuffers().outlineBufferSource().getBuffer(ModRenderTypes.debugCollider());
		
		Vec3 min = vertices[0];
		Vec3 max = vertices[145];
		Matrix4f mat = getMatrix4f(min, max);
		vertices = convertVertices(min, max, vertices);
		
		for (int i = 0; i < 13; i++)
		{
			int i2 = (i % 12) + 1;
			vertexBuilder.vertex(mat, (float)vertices[i2].x, (float)vertices[i2].y, (float)vertices[i2].z).color(color).endVertex();
			vertexBuilder.vertex(mat, (float)vertices[0].x, (float)vertices[0].y, (float)vertices[0].z).color(color).endVertex();
			vertexBuilder.vertex(mat, (float)vertices[i2].x, (float)vertices[i2].y, (float)vertices[i2].z).color(color).endVertex();
		}
		for (int i = 1; i < 12; i++)
		{
			for (int s = 0; s < 13; s++)
			{
				int s2 = i * 12 + (s % 12) + 1;
				vertexBuilder.vertex(mat, (float)vertices[s2].x, (float)vertices[s2].y, (float)vertices[s2].z).color(color).endVertex();
				int s3 = (i - 1) * 12 + (s % 12) + 1;
				vertexBuilder.vertex(mat, (float)vertices[s3].x, (float)vertices[s3].y, (float)vertices[s3].z).color(color).endVertex();
				vertexBuilder.vertex(mat, (float)vertices[s2].x, (float)vertices[s2].y, (float)vertices[s2].z).color(color).endVertex();
			}
		}
		for (int i = 0; i < 11; i++)
		{
			vertexBuilder.vertex(mat, (float)vertices[145].x, (float)vertices[145].y, (float)vertices[145].z).color(color).endVertex();
			int i2 = 144 - i;
			vertexBuilder.vertex(mat, (float)vertices[i2].x, (float)vertices[i2].y, (float)vertices[i2].z).color(color).endVertex();
		}
		
		Minecraft.getInstance().renderBuffers().bufferSource().endBatch(ModRenderTypes.debugCollider());
	}
}
