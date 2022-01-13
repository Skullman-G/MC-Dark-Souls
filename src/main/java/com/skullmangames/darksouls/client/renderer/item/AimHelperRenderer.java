package com.skullmangames.darksouls.client.renderer.item;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.skullmangames.darksouls.client.renderer.ModRenderTypes;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AimHelperRenderer
{
	public void doRender(PoseStack matStackIn, float partialTicks)
	{
		Minecraft minecraft = Minecraft.getInstance();
		Entity entity = minecraft.player;
		HitResult ray = entity.pick(200.D, partialTicks, false);
		Vec3 vec3 = ray.getLocation();
		Vec3 pos1 = new Vec3((float) Mth.lerp((double)partialTicks, entity.xOld, entity.getX()),
							   (float) Mth.lerp((double)partialTicks, entity.yOld, entity.getY()) + entity.getEyeHeight() - 0.15F,
							   (float) Mth.lerp((double)partialTicks, entity.zOld, entity.getZ()));
		Vec3 pos2 = new Vec3((float) vec3.x, (float) vec3.y, (float) vec3.z);
		RenderType renderType = ModRenderTypes.getAimHelper();
		
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glEnable(GL11.GL_POINT_SMOOTH);
		GL11.glLineWidth(3.0F);
		
		Camera renderInfo = minecraft.gameRenderer.getMainCamera();
		Vec3 projectedView = renderInfo.getPosition();
		matStackIn.pushPose();
		matStackIn.translate(-projectedView.x, -projectedView.y, -projectedView.z);
		
		BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
		bufferBuilder.begin(renderType.mode(), renderType.format());
		bufferBuilder.vertex(pos1.x(), pos1.y(), pos1.z()).color(0.0F, 1.0F, 0.0F, 0.5F).endVertex();
		bufferBuilder.vertex(pos2.x(), pos2.y(), pos2.z()).color(0.0F, 1.0F, 0.0F, 0.5F).endVertex();
		renderType.end(bufferBuilder, 0, 0, 0);
		
		float length = (float)pos2.subtract(pos1).length();
		float ratio = Math.min(50.0F, length);
		ratio = (51.0F - ratio) / 50.0F;
		GL11.glPointSize(ratio * 10.0F);
		bufferBuilder.begin(VertexFormat.Mode.LINES, renderType.format());
		bufferBuilder.vertex(pos2.x(), pos2.y(), pos2.z()).color(0.0F, 1.0F, 0.0F, 0.5F).endVertex();
		renderType.end(bufferBuilder, 0, 0, 0);
		
		matStackIn.popPose();
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GL11.glDisable(GL11.GL_POINT_SMOOTH);
	}
}
