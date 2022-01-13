package com.skullmangames.darksouls.client.gui;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;

import net.minecraft.client.gui.GuiComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModIngameGui extends GuiComponent
{
	public void drawTexturedModalRectFixCoord(Matrix4f matrix, float xCoord, float yCoord, int minU, int minV, int maxU, int maxV)
    {
		drawTexturedModalRectFixCoord(matrix, xCoord, yCoord, maxU, maxV, (float)this.getBlitOffset(), minU, minV, maxU, maxV);
    }
	
	public static void drawTexturedModalRectFixCoord(Matrix4f matrix, float minX, float minY, float maxX, float maxY, float z, float minU, float minV, float maxU, float maxV)
	{
		float cor = 0.00390625F;
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(matrix, minX, minY + maxX, z).uv(minU * cor, (minV + maxV) * cor).endVertex();
        bufferbuilder.vertex(matrix, minX + maxY, minY + maxX, z).uv((minU + maxU) * cor, (minV + maxV) * cor).endVertex();
        bufferbuilder.vertex(matrix, minX + maxY, minY, z).uv((minU + maxU) * cor, (minV * cor)).endVertex();
        bufferbuilder.vertex(matrix, minX, minY, z).uv(minU * cor, minV * cor).endVertex();
        bufferbuilder.end();
		BufferUploader.end(bufferbuilder);
	}
}