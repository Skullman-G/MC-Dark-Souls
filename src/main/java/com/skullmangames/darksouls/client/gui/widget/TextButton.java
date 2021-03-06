package com.skullmangames.darksouls.client.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class TextButton extends Button
{
	public TextButton(int p_93721_, int p_93722_, int p_93723_, int p_93724_, Component p_93725_, OnPress p_93726_)
	{
		super(p_93721_, p_93722_, p_93723_, p_93724_, p_93725_, p_93726_);
	}

	public TextButton(int p_i232256_1_, int p_i232256_2_, int p_i232256_3_, int p_i232256_4_, Component p_i232256_5_,
			OnPress p_i232256_6_, OnTooltip p_i232256_7_)
	{
		super(p_i232256_1_, p_i232256_2_, p_i232256_3_, p_i232256_4_, p_i232256_5_, p_i232256_6_, p_i232256_7_);
	}

	@Override
	public void renderButton(PoseStack poseStack, int p_93747_, int p_93748_, float p_93749_)
	{
		Minecraft minecraft = Minecraft.getInstance();
		Font font = minecraft.font;
		int color = this.isHovered ? 16777215 : 11184810;
		drawCenteredString(poseStack, font, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2,
				color | Mth.ceil(this.alpha * 255.0F) << 24);

		if (this.isHoveredOrFocused())
		{
			this.renderToolTip(poseStack, p_93747_, p_93748_);
		}
	}
}
