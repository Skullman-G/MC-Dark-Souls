package com.skullmangames.darksouls.client.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.blocks.BonfireBlock;
import com.skullmangames.darksouls.common.entities.ModEntityDataManager;
import com.skullmangames.darksouls.common.tiles.BonfireTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BonfireScreen extends Screen
{
	public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/bonfire_main.png");
	protected int imageWidth = 129;
	protected int imageHeight = 166;
	protected Button reverseHollowingButton;
	protected Button kindleButton;
	protected Button leaveButton;
	private BonfireTileEntity bonfiretileentity;
	private PlayerEntity playerEntity;
	private int buttonWidth = 100;
	private int buttonHeight = 20;
	
	public BonfireScreen(BonfireTileEntity tileentity, PlayerEntity playerentity)
	{
		super(NarratorChatListener.NO_TITLE);
		this.bonfiretileentity = tileentity;
		this.playerEntity = playerentity;
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
		
		Button.ITooltip tooltip = (button, p_238659_2_, p_238659_3_, p_238659_4_) ->
		{
			String description = new TranslationTextComponent("gui.darksouls.reverse_hollowing_tooltip").getString();
			String warning = "";
			if (!(ModEntityDataManager.getHumanity(this.playerEntity) > 0)) warning = new TranslationTextComponent("gui.darksouls.not_enough_humanity").getString();
			if (ModEntityDataManager.isHuman(playerEntity)) warning = new TranslationTextComponent("gui.darksouls.already_human").getString();
			StringTextComponent textcomponent = warning == "" ? new StringTextComponent(description) : new StringTextComponent(description + "\n\n" + "\u00A74" + warning);
			
			this.renderTooltip(p_238659_2_, this.minecraft.font.split(textcomponent, Math.max(this.width / 2 - 43, 170)), p_238659_3_, p_238659_4_);
	    };
		this.reverseHollowingButton = this.addButton(new Button(this.width / 2 - (this.buttonWidth / 2), this.height / 2, this.buttonWidth, this.buttonHeight, new TranslationTextComponent("gui.darksouls.reverse_hollowing_button"), (p_214187_1_) ->
		{
	         this.reverseHollowing();
	    }, tooltip));
		this.reverseHollowingButton.active = !ModEntityDataManager.isHuman(this.playerEntity) && ModEntityDataManager.getHumanity(this.playerEntity) > 0 ? true : false;
		
		tooltip = (button, p_238659_2_, p_238659_3_, p_238659_4_) ->
		{
			String description = new TranslationTextComponent("gui.darksouls.kindle_tooltip").getString();
			String warning = "";
			if (!(ModEntityDataManager.getHumanity(this.playerEntity) > 0)) warning = new TranslationTextComponent("gui.darksouls.not_enough_humanity").getString();
			if (!ModEntityDataManager.isHuman(this.playerEntity)) warning = new TranslationTextComponent("gui.darksouls.not_human").getString();
			StringTextComponent textcomponent = warning == "" ? new StringTextComponent(description) : new StringTextComponent(description + "\n\n" + "\u00A74" + warning);
			
			this.renderTooltip(p_238659_2_, this.minecraft.font.split(textcomponent, Math.max(this.width / 2 - 43, 170)), p_238659_3_, p_238659_4_);
	    };
		this.kindleButton = this.addButton(new Button(this.width / 2 - (this.buttonWidth / 2), this.height / 2 + (1 * (this.buttonHeight + 5)), this.buttonWidth, this.buttonHeight, new TranslationTextComponent("gui.darksouls.kindle"), (p_214187_1_) ->
		{
	         this.kindle();
	    }, tooltip));
		this.kindleButton.active = ModEntityDataManager.isHuman(this.playerEntity) && ModEntityDataManager.getHumanity(this.playerEntity) > 0 && this.bonfiretileentity.getBlockState().getValue(BonfireBlock.FIRE_LEVEL) < 2;
		this.leaveButton = this.addButton(new Button(this.width / 2 - (this.buttonWidth / 2), this.height / 2 + (2 * (this.buttonHeight + 5)), this.buttonWidth, this.buttonHeight, new TranslationTextComponent("gui.darksouls.leave_button"), (p_214187_1_) ->
		{
	         super.onClose();
	    }));
	}
	
	@Override
	public void resize(Minecraft minecraft, int p_231152_2_, int p_231152_3_)
	{
		this.init(minecraft, p_231152_2_, p_231152_3_);
	}
	
	@Override
	public void render(MatrixStack matrixstack, int mouseX, int mouseY, float partialticks)
	{
		super.renderBackground(matrixstack);
		this.renderBg(matrixstack, partialticks, mouseX, mouseY);
	    drawCenteredString(matrixstack, this.font, this.bonfiretileentity.getName(), this.width / 2, this.height / 2 - 30, 16777215);
	    super.render(matrixstack, mouseX, mouseY, partialticks);
	}
	
	private void renderBg(MatrixStack matrixstack, float partialticks, int mouseX, int mouseY)
	{
		this.minecraft.getTextureManager().bind(TEXTURE_LOCATION);
		int x = (this.width - this.imageWidth) / 2;
	    int y = (this.height - this.imageHeight) / 2;
	    this.blit(matrixstack, x, y, 0, 0, this.imageWidth, this.imageHeight);
	}
	
	protected void reverseHollowing()
	{
		ModEntityDataManager.shrinkHumanity(this.playerEntity, 1);
		ModEntityDataManager.setHuman(this.playerEntity, true);
		super.onClose();
	}
	
	protected void kindle()
	{
		ModEntityDataManager.shrinkHumanity(this.playerEntity, 1);
		this.bonfiretileentity.kindle();
		super.onClose();
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
