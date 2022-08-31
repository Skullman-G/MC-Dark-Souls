package com.skullmangames.darksouls.client.gui.screens;

import java.util.StringTokenizer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.entity.Covenant;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.client.CTSCovenant;

import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class JoinCovenantScreen extends Screen
{
	public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/join_covenant.png");
	public static final ResourceLocation DS_TEXTURE_LOCATION = new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/ds_join_covenant.png");
	private int imageWidth = 189;
	private int imageHeight = 234;
	private int buttonWidth = 50;
	private int buttonHeight = 20;
	private int color;
	
	private final Covenant covenant;
	private final String title;
	private final String[] description;
	
	public JoinCovenantScreen(Covenant covenant)
	{
		super(NarratorChatListener.NO_TITLE);
		this.color = DarkSouls.CLIENT_INGAME_CONFIG.darkSoulsUI.getValue() ? 16777215 : 4210752;
		this.covenant = covenant;
		this.title = this.covenant.getRegistryName();
		this.description = this.addLinebreaks(this.covenant.getDescription(), 40);
	}
	
	private String[] addLinebreaks(String input, int maxCharInLine)
	{
		StringTokenizer tok = new StringTokenizer(input, " ");
		StringBuilder output = new StringBuilder(input.length());
		int lineLen = 0;
		while (tok.hasMoreTokens())
		{
			String word = tok.nextToken();

			while (word.length() > maxCharInLine)
			{
				output.append(word.substring(0, maxCharInLine - lineLen) + "\n");
				word = word.substring(maxCharInLine - lineLen);
				lineLen = 0;
			}

			if (lineLen + word.length() > maxCharInLine)
			{
				output.append("\n");
				lineLen = 0;
			}
			output.append(word + " ");

			lineLen += word.length() + 1;
		}
		return output.toString().split("\n");
	}
	
	@Override
	public boolean isPauseScreen()
	{
		return false;
	}
	
	@Override
	protected void init()
	{
		this.addRenderableWidget(new Button((this.width / 2) - (this.buttonWidth / 2) - 30, this.height / 2 + 85, this.buttonWidth, this.buttonHeight, new TranslatableComponent("gui.darksouls.yes"), (button) ->
		{
			this.changeCovenant();
		}));
		this.addRenderableWidget(new Button((this.width / 2) - (this.buttonWidth / 2) + 30, this.height / 2 + 85, this.buttonWidth, this.buttonHeight, new TranslatableComponent("gui.darksouls.no"), (button) ->
		{
			this.onClose();
		}));
	}
	
	private void changeCovenant()
	{
		ModNetworkManager.sendToServer(new CTSCovenant(this.covenant));
		this.onClose();
	}
	
	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialticks)
	{
		super.renderBackground(poseStack);

		int x = (this.width - this.imageWidth) / 2;
		int y = (this.height - this.imageHeight) / 2;
		this.renderBg(poseStack, partialticks, x, y);

		this.font.draw(poseStack, this.title, (float) (this.width / 2 - this.font.width(this.title) / 2),
				y + 10, this.color);
		
		poseStack.pushPose();
		float scale = 0.8F;
		poseStack.scale(scale, scale, 1.0F);
		for (int i = 0; i < this.description.length; i++)
		{
			drawString(poseStack, this.minecraft.font, this.description[i], (int) ((x + 10) / scale), (int) ((y + 35 + 13 * i) / scale), 16777215);
		}
		poseStack.popPose();
		
		this.font.draw(poseStack, "Join Covenant?", (float) (this.width / 2 - this.font.width("Join Covenant?") / 2),
				y + 185, this.color);

		super.render(poseStack, mouseX, mouseY, partialticks);
	}

	private void renderBg(PoseStack matrixstack, float partialticks, int x, int y)
	{
		if (DarkSouls.CLIENT_INGAME_CONFIG.darkSoulsUI.getValue())
			RenderSystem.setShaderTexture(0, DS_TEXTURE_LOCATION);
		else
			RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
		this.blit(matrixstack, x, y, 0, 0, this.imageWidth, this.imageHeight);
	}
}
