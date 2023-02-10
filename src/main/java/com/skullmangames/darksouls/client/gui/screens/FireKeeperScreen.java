package com.skullmangames.darksouls.client.gui.screens;

import net.minecraft.client.util.InputMappings;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.gui.widget.ResizeTextButton;
import com.skullmangames.darksouls.config.ConfigManager;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.client.CTSOpenFireKeeperContainer;

import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FireKeeperScreen extends Screen
{
	private final int fireKeeperId;

	private final int color;

	public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/fire_keeper_main.png");
	public static final ResourceLocation DS_TEXTURE_LOCATION = new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/ds_fire_keeper_main.png");
	private int imageWidth = 129;
	private int imageHeight = 166;
	private int buttonWidth = 100;
	private int buttonHeight = 20;

	public FireKeeperScreen(int firekeeperid)
	{
		super(NarratorChatListener.NO_TITLE);
		this.fireKeeperId = firekeeperid;
		this.color = ConfigManager.INGAME_CONFIG.darkSoulsUI.getValue() ? 16777215 : 4210752;
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

		this.addButton(new Button(this.width / 2 - (this.buttonWidth / 2), this.height / 2, this.buttonWidth,
				this.buttonHeight, new TranslationTextComponent("gui.darksouls.level_up_button"), (button) ->
				{
					this.openLevelUpScreen();
				}));
		this.addButton(new ResizeTextButton(this.width / 2 - (this.buttonWidth / 2),
				this.height / 2 + (1 * (this.buttonHeight + 5)), this.buttonWidth, this.buttonHeight,
				new TranslationTextComponent("gui.darksouls.reinforce_estus_flask_button"), (button) ->
				{
					this.openReinforceEstusFlaskScreen();
				}));
		this.addButton(new Button(this.width / 2 - (this.buttonWidth / 2),
				this.height / 2 + (2 * (this.buttonHeight + 5)), this.buttonWidth, this.buttonHeight,
				new TranslationTextComponent("gui.darksouls.leave_button"), (button) ->
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
	public void render(MatrixStack matrixstack, int mouseX, int mouseY, float partialticks)
	{
		super.renderBackground(matrixstack);

		int x = (this.width - this.imageWidth) / 2;
		int y = (this.height - this.imageHeight) / 2;
		this.renderBg(matrixstack, partialticks, x, y);

		this.font.draw(matrixstack, "Fire Keeper", (float) (this.width / 2 - this.font.width("Fire Keeper") / 2),
				y + 10, this.color);

		super.render(matrixstack, mouseX, mouseY, partialticks);
	}

	private void renderBg(MatrixStack matrixstack, float partialticks, int x, int y)
	{
		if (ConfigManager.INGAME_CONFIG.darkSoulsUI.getValue())
			minecraft.getTextureManager().bind(DS_TEXTURE_LOCATION);
		else
			minecraft.getTextureManager().bind(TEXTURE_LOCATION);
		this.blit(matrixstack, x, y, 0, 0, this.imageWidth, this.imageHeight);
	}

	@Override
	public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_)
	{
		InputMappings.Input mouseKey = InputMappings.getKey(p_231046_1_, p_231046_2_);
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
