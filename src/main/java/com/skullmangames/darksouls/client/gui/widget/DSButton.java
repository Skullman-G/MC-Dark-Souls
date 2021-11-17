package com.skullmangames.darksouls.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.skullmangames.darksouls.DarkSouls;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

public class DSButton extends Button
{
	public static final ResourceLocation DS_BUTTON_LOCATION = new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/widgets.png");
	
	public DSButton(int p_i232256_1_, int p_i232256_2_, int p_i232256_3_, int p_i232256_4_, ITextComponent p_i232256_5_,
			IPressable p_i232256_6_)
	{
		super(p_i232256_1_, p_i232256_2_, p_i232256_3_, p_i232256_4_, p_i232256_5_, p_i232256_6_);
	}
	
	public DSButton(int p_i232256_1_, int p_i232256_2_, int p_i232256_3_, int p_i232256_4_, ITextComponent p_i232256_5_,
			IPressable p_i232256_6_, ITooltip p_i232256_7_)
	{
		super(p_i232256_1_, p_i232256_2_, p_i232256_3_, p_i232256_4_, p_i232256_5_, p_i232256_6_, p_i232256_7_);
	}
	
	@Override
	public void renderButton(MatrixStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_)
	{
	    Minecraft minecraft = Minecraft.getInstance();
	    FontRenderer fontrenderer = minecraft.font;
	    minecraft.getTextureManager().bind(DS_BUTTON_LOCATION);
	    RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
	    int i = this.getYImage(this.isHovered());
	    RenderSystem.enableBlend();
	    RenderSystem.defaultBlendFunc();
	    RenderSystem.enableDepthTest();
	    
	    this.blit(p_230431_1_, this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height / 2);
	    this.blit(p_230431_1_, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height / 2);
	    
	    this.blit(p_230431_1_, this.x, this.y + this.height / 2, 0, (66 + i * 20) - this.height / 2, this.width / 2, this.height / 2);
	    this.blit(p_230431_1_, this.x + this.width / 2, this.y + this.height / 2, 200 - this.width / 2, (66 + i * 20) - this.height / 2, this.width / 2, this.height / 2);
	    
	    this.renderBg(p_230431_1_, minecraft, p_230431_2_, p_230431_3_);
	    int j = getFGColor();
	    drawCenteredString(p_230431_1_, fontrenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
	}
}
