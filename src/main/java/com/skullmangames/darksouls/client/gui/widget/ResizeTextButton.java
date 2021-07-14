package com.skullmangames.darksouls.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

public class ResizeTextButton extends Button
{
	public ResizeTextButton(int p_i232255_1_, int p_i232255_2_, int p_i232255_3_, int p_i232255_4_,	ITextComponent p_i232255_5_, IPressable p_i232255_6_)
	{
		super(p_i232255_1_, p_i232255_2_, p_i232255_3_, p_i232255_4_, p_i232255_5_, p_i232255_6_);
	}
	
	public ResizeTextButton(int p_i232256_1_, int p_i232256_2_, int p_i232256_3_, int p_i232256_4_, ITextComponent p_i232256_5_, Button.IPressable p_i232256_6_, Button.ITooltip p_i232256_7_)
	{
	      super(p_i232256_1_, p_i232256_2_, p_i232256_3_, p_i232256_4_, p_i232256_5_, p_i232256_6_, p_i232256_7_);
	}
	
	@Override
	public void renderButton(MatrixStack matrixstack, int p_230431_2_, int p_230431_3_, float p_230431_4_)
	{
		Minecraft minecraft = Minecraft.getInstance();
	    FontRenderer fontrenderer = minecraft.font;
	    minecraft.getTextureManager().bind(WIDGETS_LOCATION);
	    int i = this.getYImage(this.isHovered());
	    RenderSystem.enableBlend();
	    RenderSystem.defaultBlendFunc();
	    RenderSystem.enableDepthTest();
	    this.blit(matrixstack, this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
	    this.blit(matrixstack, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
	    this.renderBg(matrixstack, minecraft, p_230431_2_, p_230431_3_);
	    int j = getFGColor();
	    
	    matrixstack.pushPose();
	    double xscale = 0.8D;
	    double yscale = 0.8D;
	    matrixstack.scale((float)xscale, (float)yscale, 0.0F);
	    matrixstack.translate((1.0D - xscale) * 100 * 3, (1.0D - yscale) * 100 + 15, 0.0D);
	    drawCenteredString(matrixstack, fontrenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
		matrixstack.popPose();
	    
		if (this.isHovered())
		{
	         this.renderToolTip(matrixstack, p_230431_2_, p_230431_3_);
	    }
	}
}
