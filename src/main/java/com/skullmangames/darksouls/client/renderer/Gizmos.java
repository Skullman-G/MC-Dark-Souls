package com.skullmangames.darksouls.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.client.ClientManager;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

public class Gizmos
{
	public static void drawLine(Vec3 start, Vec3 end)
	{
		VertexConsumer vertexBuilder = Minecraft.getInstance().renderBuffers().outlineBufferSource().getBuffer(ModRenderTypes.debugCollider());
		
		Camera cam = ClientManager.INSTANCE.mainCamera;
		Vec3 camPos = cam.getPosition();
		float camXRot = cam.getXRot();
		float camYRot = cam.getYRot();
		double camYRotRad = Math.toRadians(camYRot);
		
		PoseStack poseStack = new PoseStack();
		
		Vec3 start2 = camPos.subtract(start);
		Vec3 end2 = camPos.subtract(end);
		Vec3 projCenter = new Vec3((start2.x + end2.x) / 2, -(start2.y + end2.y) / 2, (start2.z + end2.z) / 2);
		
		poseStack.mulPose(Vector3f.YP.rotationDegrees(camYRot));
		poseStack.mulPose(new Vector3f((float)Math.cos(camYRotRad), 0, (float)Math.sin(camYRotRad)).rotationDegrees(camXRot));
		poseStack.translate(projCenter.x, projCenter.y, projCenter.z);
		
		Matrix4f mat = poseStack.last().pose();
		
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
		
		Camera cam = ClientManager.INSTANCE.mainCamera;
		Vec3 camPos = cam.getPosition();
		float camXRot = cam.getXRot();
		float camYRot = cam.getYRot();
		double camYRotRad = Math.toRadians(camYRot);
		
		PoseStack poseStack = new PoseStack();
		
		Vec3 start2 = camPos.subtract(start);
		Vec3 end2 = camPos.subtract(end);
		Vec3 projCenter = new Vec3((start2.x + end2.x) / 2, -(start2.y + end2.y) / 2, (start2.z + end2.z) / 2);
		
		poseStack.mulPose(Vector3f.YP.rotationDegrees(camYRot));
		poseStack.mulPose(new Vector3f((float)Math.cos(camYRotRad), 0, (float)Math.sin(camYRotRad)).rotationDegrees(camXRot));
		poseStack.translate(projCenter.x, projCenter.y, projCenter.z);
		
		Matrix4f mat = poseStack.last().pose();
		
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
		
		Camera cam = ClientManager.INSTANCE.mainCamera;
		Vec3 camPos = cam.getPosition();
		float camXRot = cam.getXRot();
		float camYRot = cam.getYRot();
		double camYRotRad = Math.toRadians(camYRot);
		
		Vec3 min = vertices[0];
		Vec3 max = vertices[6];
		
		PoseStack poseStack = new PoseStack();
		
		Vec3 start2 = camPos.subtract(min);
		Vec3 end2 = camPos.subtract(max);
		Vec3 projCenter = new Vec3((start2.x + end2.x) / 2, -(start2.y + end2.y) / 2, (start2.z + end2.z) / 2);
		
		poseStack.mulPose(Vector3f.YP.rotationDegrees(camYRot));
		poseStack.mulPose(new Vector3f((float)Math.cos(camYRotRad), 0, (float)Math.sin(camYRotRad)).rotationDegrees(camXRot));
		poseStack.translate(projCenter.x, projCenter.y, projCenter.z);
		
		Matrix4f mat = poseStack.last().pose();
		
		Vec3 center = max.add(min).scale(0.5D);
		Vec3[] normalVerts = new Vec3[vertices.length];
		for (int i = 0; i < normalVerts.length; i++)
		{
			normalVerts[i] = vertices[i].subtract(center);
			normalVerts[i] = new Vec3(normalVerts[i].x, -normalVerts[i].y, normalVerts[i].z);
		}
		
		vertexBuilder.vertex(mat, (float)normalVerts[0].x, (float)normalVerts[0].y, (float)normalVerts[0].z).color(color).endVertex();
		vertexBuilder.vertex(mat, (float)normalVerts[1].x, (float)normalVerts[1].y, (float)normalVerts[1].z).color(color).endVertex();
		vertexBuilder.vertex(mat, (float)normalVerts[2].x, (float)normalVerts[2].y, (float)normalVerts[2].z).color(color).endVertex();
		vertexBuilder.vertex(mat, (float)normalVerts[3].x, (float)normalVerts[3].y, (float)normalVerts[3].z).color(color).endVertex();
		vertexBuilder.vertex(mat, (float)normalVerts[0].x, (float)normalVerts[0].y, (float)normalVerts[0].z).color(color).endVertex();
		vertexBuilder.vertex(mat, (float)normalVerts[4].x, (float)normalVerts[4].y, (float)normalVerts[4].z).color(color).endVertex();
		vertexBuilder.vertex(mat, (float)normalVerts[5].x, (float)normalVerts[5].y, (float)normalVerts[5].z).color(color).endVertex();
		vertexBuilder.vertex(mat, (float)normalVerts[1].x, (float)normalVerts[1].y, (float)normalVerts[1].z).color(color).endVertex();
		vertexBuilder.vertex(mat, (float)normalVerts[5].x, (float)normalVerts[5].y, (float)normalVerts[5].z).color(color).endVertex();
		vertexBuilder.vertex(mat, (float)normalVerts[6].x, (float)normalVerts[6].y, (float)normalVerts[6].z).color(color).endVertex();
		vertexBuilder.vertex(mat, (float)normalVerts[2].x, (float)normalVerts[2].y, (float)normalVerts[2].z).color(color).endVertex();
		vertexBuilder.vertex(mat, (float)normalVerts[6].x, (float)normalVerts[6].y, (float)normalVerts[6].z).color(color).endVertex();
		vertexBuilder.vertex(mat, (float)normalVerts[7].x, (float)normalVerts[7].y, (float)normalVerts[7].z).color(color).endVertex();
		vertexBuilder.vertex(mat, (float)normalVerts[3].x, (float)normalVerts[3].y, (float)normalVerts[3].z).color(color).endVertex();
		vertexBuilder.vertex(mat, (float)normalVerts[7].x, (float)normalVerts[7].y, (float)normalVerts[7].z).color(color).endVertex();
		vertexBuilder.vertex(mat, (float)normalVerts[4].x, (float)normalVerts[4].y, (float)normalVerts[4].z).color(color).endVertex();
		
		Minecraft.getInstance().renderBuffers().bufferSource().endBatch(ModRenderTypes.debugCollider());
	}
}
