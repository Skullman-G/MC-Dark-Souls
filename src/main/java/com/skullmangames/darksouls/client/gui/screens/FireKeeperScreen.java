package com.skullmangames.darksouls.client.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.gui.widget.ResizeTextButton;
import com.skullmangames.darksouls.common.entity.FireKeeperEntity;

import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FireKeeperScreen extends Screen
{
	private FireKeeperEntity fireKeeper;
	private ServerPlayerEntity serverPlayer;
	
	
	public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/ds_fire_keeper_main.png");
	private int imageWidth = 129;
	private int imageHeight = 166;
	private int buttonWidth = 100;
	private int buttonHeight = 20;
	
	public FireKeeperScreen(FireKeeperEntity firekeeper, ServerPlayerEntity serverplayer)
	{
		super(NarratorChatListener.NO_TITLE);
		
		this.fireKeeper = firekeeper;
		this.serverPlayer = serverplayer;
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
		
		this.addButton(new Button(this.width / 2 - (this.buttonWidth / 2), this.height / 2, this.buttonWidth, this.buttonHeight, new TranslationTextComponent("gui.darksouls.level_up_button"), (p_214187_1_) ->
		{
	         this.openLevelUpScreen();
	    }));
		this.addButton(new ResizeTextButton(this.width / 2 - (this.buttonWidth / 2), this.height / 2 + (1 * (this.buttonHeight + 5)), this.buttonWidth, this.buttonHeight, new TranslationTextComponent("gui.darksouls.reinforce_estus_flask_button"), (p_214187_1_) ->
		{
	         this.openReinforceEstusFlaskScreen();
	    }));
		this.addButton(new Button(this.width / 2 - (this.buttonWidth / 2), this.height / 2 + (2 * (this.buttonHeight + 5)), this.buttonWidth, this.buttonHeight, new TranslationTextComponent("gui.darksouls.leave_button"), (p_214187_1_) ->
		{
	         super.onClose();
	    }));
	}
	
	private void openReinforceEstusFlaskScreen()
	{
		this.fireKeeper.openContainer(this.serverPlayer);
	}
	
	private void openLevelUpScreen()
	{
		this.minecraft.setScreen(new LevelUpScreen(this.serverPlayer));
	}
	
	@Override
	public void render(MatrixStack matrixstack, int mouseX, int mouseY, float partialticks)
	{
		super.renderBackground(matrixstack);
		
		int x = (this.width - this.imageWidth) / 2;
	    int y = (this.height - this.imageHeight) / 2;
		this.renderBg(matrixstack, partialticks, x, y);
		
		drawCenteredString(matrixstack, this.font, "Fire Keeper", this.width / 2, y + 10, 16777215);
		
		super.render(matrixstack, mouseX, mouseY, partialticks);
	}
	
	private void renderBg(MatrixStack matrixstack, float partialticks, int x, int y)
	{
		this.minecraft.getTextureManager().bind(TEXTURE_LOCATION);
	    this.blit(matrixstack, x, y, 0, 0, this.imageWidth, this.imageHeight);
	}
	
	@Override
	public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_)
	{
		InputMappings.Input mouseKey = InputMappings.getKey(p_231046_1_, p_231046_2_);
		if (super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_)) return true;
		else if (this.minecraft.options.keyInventory.isActiveAndMatches(mouseKey)) 
		{
	         this.onClose();
	         return true;
		}
		else return false;
	}
}
