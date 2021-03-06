package com.skullmangames.darksouls.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ScalableButton extends Button
{
	public ScalableButton(int p_i232256_1_, int p_i232256_2_, int p_i232256_3_, int p_i232256_4_,
			Component p_i232256_5_, OnPress p_i232256_6_, OnTooltip p_i232256_7_)
	{
		super(p_i232256_1_, p_i232256_2_, p_i232256_3_, p_i232256_4_, p_i232256_5_, p_i232256_6_, p_i232256_7_);
	}

	public ScalableButton(int p_i232256_1_, int p_i232256_2_, int p_i232256_3_, int p_i232256_4_,
			Component p_i232256_5_, OnPress p_i232256_6_)
	{
		super(p_i232256_1_, p_i232256_2_, p_i232256_3_, p_i232256_4_, p_i232256_5_, p_i232256_6_);
	}

	@Override
	public void renderButton(PoseStack matStack, int p_230431_2_, int p_230431_3_, float p_230431_4_)
	{
		Minecraft minecraft = Minecraft.getInstance();
		Font fontrenderer = minecraft.font;
		RenderSystem.setShaderTexture(0, AbstractWidget.WIDGETS_LOCATION);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
		int i = this.getYImage(this.isHovered);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();

		this.blit(matStack, this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height / 2);
		this.blit(matStack, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2,
				this.height / 2);

		this.blit(matStack, this.x, this.y + this.height / 2, 0, (66 + i * 20) - this.height / 2, this.width / 2,
				this.height / 2);
		this.blit(matStack, this.x + this.width / 2, this.y + this.height / 2, 200 - this.width / 2,
				(66 + i * 20) - this.height / 2, this.width / 2, this.height / 2);

		this.renderBg(matStack, minecraft, p_230431_2_, p_230431_3_);
		int j = getFGColor();

		if (this.height <= 10)
		{
			matStack.pushPose();
			float scale = 0.8F;
			matStack.scale(scale, scale, 1.0F);
			drawCenteredString(matStack, fontrenderer, this.getMessage(), (int) ((this.x + this.width / 2) / scale),
					(int) ((this.y + (this.height - 8) / 2) / scale), j | (int)Math.ceil(this.alpha * 255.0F) << 24);
			matStack.popPose();
		} else
		{
			drawCenteredString(matStack, fontrenderer, this.getMessage(), this.x + this.width / 2,
					this.y + (this.height - 8) / 2, j | (int)Math.ceil(this.alpha * 255.0F) << 24);
		}
	}
}
