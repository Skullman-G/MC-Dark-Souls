package com.skullmangames.darksouls.client.gui.screens;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.gui.widget.ResizeTextButton;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.client.CTSOpenFireKeeperContainer;

import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FireKeeperScreen extends Screen
{
	private final int fireKeeperId;

	private final int color;

	public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(DarkSouls.MOD_ID,
			"textures/guis/fire_keeper_main.png");
	public static final ResourceLocation DS_TEXTURE_LOCATION = new ResourceLocation(DarkSouls.MOD_ID,
			"textures/guis/ds_fire_keeper_main.png");
	private int imageWidth = 129;
	private int imageHeight = 166;
	private int buttonWidth = 100;
	private int buttonHeight = 20;

	public FireKeeperScreen(int firekeeperid)
	{
		super(NarratorChatListener.NO_TITLE);
		this.fireKeeperId = firekeeperid;
		this.color = DarkSouls.CLIENT_INGAME_CONFIG.darkSoulsUI.getValue() ? 16777215 : 4210752;
	}

	@Override
	public boolean isPauseScreen()
	{
		return false;
	}

	@Override
	protected void init()
	{
		super.init();

		this.addRenderableWidget(new Button(this.width / 2 - (this.buttonWidth / 2), this.height / 2, this.buttonWidth,
				this.buttonHeight, new TranslatableComponent("gui.darksouls.level_up_button"), (button) ->
				{
					this.openLevelUpScreen();
				}));
		this.addRenderableWidget(new ResizeTextButton(this.width / 2 - (this.buttonWidth / 2),
				this.height / 2 + (1 * (this.buttonHeight + 5)), this.buttonWidth, this.buttonHeight,
				new TranslatableComponent("gui.darksouls.reinforce_estus_flask_button"), (button) ->
				{
					this.openReinforceEstusFlaskScreen();
				}));
		this.addRenderableWidget(new Button(this.width / 2 - (this.buttonWidth / 2),
				this.height / 2 + (2 * (this.buttonHeight + 5)), this.buttonWidth, this.buttonHeight,
				new TranslatableComponent("gui.darksouls.leave_button"), (button) ->
				{
					super.onClose();
				}));
	}

	private void openReinforceEstusFlaskScreen()
	{
		ModNetworkManager.sendToServer(new CTSOpenFireKeeperContainer(this.fireKeeperId));
	}

	private void openLevelUpScreen()
	{
		this.minecraft.setScreen(new LevelUpScreen());
	}

	@Override
	public void render(PoseStack matrixstack, int mouseX, int mouseY, float partialticks)
	{
		super.renderBackground(matrixstack);

		int x = (this.width - this.imageWidth) / 2;
		int y = (this.height - this.imageHeight) / 2;
		this.renderBg(matrixstack, partialticks, x, y);

		this.font.draw(matrixstack, "Fire Keeper", (float) (this.width / 2 - this.font.width("Fire Keeper") / 2),
				y + 10, this.color);

		super.render(matrixstack, mouseX, mouseY, partialticks);
	}

	private void renderBg(PoseStack matrixstack, float partialticks, int x, int y)
	{
		if (DarkSouls.CLIENT_INGAME_CONFIG.darkSoulsUI.getValue())
			RenderSystem.setShaderTexture(0, DS_TEXTURE_LOCATION);
		else
			RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
		this.blit(matrixstack, x, y, 0, 0, this.imageWidth, this.imageHeight);
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
