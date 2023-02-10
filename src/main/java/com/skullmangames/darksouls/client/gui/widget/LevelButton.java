package com.skullmangames.darksouls.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.skullmangames.darksouls.common.entity.stats.Stat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;

public class LevelButton extends Button
{
	private final Stat stat;
	
	public LevelButton(int p_i232255_1_, int p_i232255_2_, int p_i232255_3_, int p_i232255_4_, ITextComponent p_i232255_5_, IPressable p_i232255_6_, Stat stat)
	{
		super(p_i232255_1_, p_i232255_2_, p_i232255_3_, p_i232255_4_, p_i232255_5_, p_i232255_6_);
		this.stat = stat;
	}
	
	public Stat getStat()
	{
		return this.stat;
	}
	
	@Override
	public void renderButton(MatrixStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_)
	{
		Minecraft minecraft = Minecraft.getInstance();
	    FontRenderer fontrenderer = minecraft.font;
	    int j = getFGColor();
		drawCenteredString(p_230431_1_, fontrenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | (int)Math.ceil(this.alpha * 255.0F) << 24);
	}
}
