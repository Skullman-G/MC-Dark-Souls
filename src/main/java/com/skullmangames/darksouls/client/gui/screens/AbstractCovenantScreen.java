package com.skullmangames.darksouls.client.gui.screens;

import java.util.StringTokenizer;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.common.capability.entity.LocalPlayerCap;
import com.skullmangames.darksouls.common.entity.Covenant;
import com.skullmangames.darksouls.common.entity.Covenant.Reward;
import com.skullmangames.darksouls.config.ConfigManager;

import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractCovenantScreen extends Screen
{
	public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/join_covenant.png");
	public static final ResourceLocation DS_TEXTURE_LOCATION = new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/ds_join_covenant.png");
	protected final int imageWidth = 189;
	protected final int imageHeight = 234;
	protected final int buttonWidth = 50;
	protected final int buttonHeight = 20;
	protected final int color;
	protected boolean showProgress;
	
	protected final Covenant covenant;
	private final String title;
	private final String[] description;
	
	protected final LocalPlayerCap playerCap = ClientManager.INSTANCE.getPlayerCap();
	
	public AbstractCovenantScreen(Covenant covenant)
	{
		super(NarratorChatListener.NO_TITLE);
		this.color = 16777215;
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
	protected void init()
	{
		super.init();
		int j = this.imageWidth / 2 - 10;
		this.addRenderableWidget(new Button((this.width / 2) - 5 - j, this.height / 2 + 85, 10, this.buttonHeight, new TextComponent("<"), (b) ->
		{
			this.toggleShowProgress();
		}));
		this.addRenderableWidget(new Button((this.width / 2) - 5 + j, this.height / 2 + 85, 10, this.buttonHeight, new TextComponent(">"), (b) ->
		{
			this.toggleShowProgress();
		}));
	}
	
	protected void toggleShowProgress()
	{
		this.showProgress = !this.showProgress;
	}
	
	@Override
	public boolean isPauseScreen()
	{
		return false;
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
		
		if (this.showProgress)
		{
			this.font.draw(poseStack, "Next Reward", (float) (this.width / 2 - this.font.width("Next Reward") / 2), y + 185, this.color);
			Reward reward = this.covenant.getNextReward(this.playerCap);
			if (reward != null)
			{
				if (ConfigManager.INGAME_CONFIG.darkSoulsUI.getValue()) RenderSystem.setShaderTexture(0, DS_TEXTURE_LOCATION);
				else RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
				
				this.blit(poseStack, this.width / 2 - 9, this.height / 2 + 85, 234, 0, 22, 15);
				ItemStack reqItem = new ItemStack(reward.getReqItem(), this.covenant.getProgressTillNextReward(this.playerCap));
				int itemX = this.width / 2 - 8;
				int itemY = this.height / 2 + 86;
				this.itemRenderer.renderAndDecorateItem(this.minecraft.player, reqItem, itemX - 25, itemY, 18);
				this.itemRenderer.renderGuiItemDecorations(this.font, reqItem, itemX - 25, itemY);
				ItemStack rewardItem = reward.getRewardItem();
				this.itemRenderer.renderAndDecorateItem(this.minecraft.player, rewardItem, itemX + 25, itemY, 18);
				this.itemRenderer.renderGuiItemDecorations(this.font, rewardItem, itemX + 25, itemY);
				
				if (mouseY >= itemY && mouseY <= itemY + 18)
				{
					if (mouseX >= itemX - 25 && mouseX <= itemX - 25 + 18)
					{
						this.renderTooltip(poseStack, reqItem, mouseX, mouseY);
					}
					else if (mouseX >= itemX + 25 && mouseX <= itemX + 25 + 18)
					{
						this.renderTooltip(poseStack, rewardItem, mouseX, mouseY);
					}
				}
			}
			else
			{
				this.font.draw(poseStack, "None", (float) (this.width / 2 - this.font.width("None") / 2), y + 200, this.color);
			}
		}

		super.render(poseStack, mouseX, mouseY, partialticks);
	}

	private void renderBg(PoseStack poseStack, float partialticks, int x, int y)
	{
		if (ConfigManager.INGAME_CONFIG.darkSoulsUI.getValue())
			RenderSystem.setShaderTexture(0, DS_TEXTURE_LOCATION);
		else
			RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
		this.blit(poseStack, x, y, 0, 0, this.imageWidth, this.imageHeight);
	}
	
	@Override
	public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_)
	{
		InputConstants.Key mouseKey = InputConstants.getKey(p_231046_1_, p_231046_2_);
		if (super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_))
			return true;
		else if (this.minecraft.options.keyInventory.isActiveAndMatches(mouseKey))
		{
			this.onClose();
			return true;
		} else
			return false;
	}
}
