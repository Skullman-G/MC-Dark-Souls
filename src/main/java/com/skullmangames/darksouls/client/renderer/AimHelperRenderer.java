package com.skullmangames.darksouls.client.renderer;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.skullmangames.darksouls.util.math.vector.Vector3fHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AimHelperRenderer
{
	@SuppressWarnings("resource")
	public void doRender(MatrixStack matStackIn, float partialTicks)
	{
		Entity entity = Minecraft.getInstance().player;
		RayTraceResult ray = entity.pick(200.D, partialTicks, false);
		Vector3d vec3 = ray.getLocation();
		Vector3f pos1 = new Vector3f((float) MathHelper.lerp((double)partialTicks, entity.xOld, entity.getX()),
							   (float) MathHelper.lerp((double)partialTicks, entity.yOld, entity.getY()) + entity.getEyeHeight() - 0.15F,
							   (float) MathHelper.lerp((double)partialTicks, entity.zOld, entity.getZ()));
		Vector3f pos2 = new Vector3f((float) vec3.x, (float) vec3.y, (float) vec3.z);
		RenderType renderType = ModRenderTypes.getAimHelper();
		
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glEnable(GL11.GL_POINT_SMOOTH);
		GL11.glLineWidth(3.0F);
		
		ActiveRenderInfo renderInfo = Minecraft.getInstance().gameRenderer.getMainCamera();
		Vector3d projectedView = renderInfo.getPosition();
		matStackIn.pushPose();
		matStackIn.translate(-projectedView.x, -projectedView.y, -projectedView.z);
		Matrix4f matrix = matStackIn.last().pose();
		
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuilder();
		bufferBuilder.begin(renderType.mode(), renderType.format());
		bufferBuilder.vertex(matrix, pos1.x(), pos1.y(), pos1.z()).color(0.0F, 1.0F, 0.0F, 0.5F).endVertex();
		bufferBuilder.vertex(matrix, pos2.x(), pos2.y(), pos2.z()).color(0.0F, 1.0F, 0.0F, 0.5F).endVertex();
		renderType.end(bufferBuilder, 0, 0, 0);
		
		float length = Vector3fHelper.length(Vector3fHelper.sub(pos2, pos1));
		float ratio = Math.min(50.0F, length);
		ratio = (51.0F - ratio) / 50.0F;
		GL11.glPointSize(ratio * 10.0F);
		bufferBuilder.begin(GL11.GL_POINTS, renderType.format());
		bufferBuilder.vertex(matrix, pos2.x(), pos2.y(), pos2.z()).color(0.0F, 1.0F, 0.0F, 0.5F).endVertex();
		renderType.end(bufferBuilder, 0, 0, 0);
		
		matStackIn.popPose();
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GL11.glDisable(GL11.GL_POINT_SMOOTH);
	}
}
