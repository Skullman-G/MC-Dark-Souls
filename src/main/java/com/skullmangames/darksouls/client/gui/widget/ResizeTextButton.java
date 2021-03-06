package com.skullmangames.darksouls.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ResizeTextButton extends Button
{
	public ResizeTextButton(int p_i232255_1_, int p_i232255_2_, int p_i232255_3_, int p_i232255_4_,
			Component p_i232255_5_, OnPress p_i232255_6_)
	{
		super(p_i232255_1_, p_i232255_2_, p_i232255_3_, p_i232255_4_, p_i232255_5_, p_i232255_6_);
	}

	public ResizeTextButton(int p_i232256_1_, int p_i232256_2_, int p_i232256_3_, int p_i232256_4_,
			Component p_i232256_5_, Button.OnPress p_i232256_6_, Button.OnTooltip p_i232256_7_)
	{
		super(p_i232256_1_, p_i232256_2_, p_i232256_3_, p_i232256_4_, p_i232256_5_, p_i232256_6_, p_i232256_7_);
	}

	@Override
	public void renderButton(PoseStack matrixstack, int p_230431_2_, int p_230431_3_, float p_230431_4_)
	{
		Minecraft minecraft = Minecraft.getInstance();
		Font fontrenderer = minecraft.font;
		RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
		int i = this.getYImage(this.isHovered);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		this.blit(matrixstack, this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
		this.blit(matrixstack, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2,
				this.height);
		this.renderBg(matrixstack, minecraft, p_230431_2_, p_230431_3_);
		int j = getFGColor();

		matrixstack.pushPose();
		float scale = 0.8F;
		matrixstack.scale(scale, scale, scale);
		drawCenteredString(matrixstack, fontrenderer, this.getMessage(), Math.round((this.x + this.width / 2) / scale),
				Math.round((this.y + (this.height - 8) / 2) / scale), j | (int)Math.ceil(this.alpha * 255.0F) << 24);
		matrixstack.popPose();

		if (this.isHovered)
		{
			this.renderToolTip(matrixstack, p_230431_2_, p_230431_3_);
		}
	}
}
