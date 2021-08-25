package com.skullmangames.darksouls.client.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.block.BonfireBlock;
import com.skullmangames.darksouls.common.entity.nbt.MobNBTManager;
import com.skullmangames.darksouls.common.tileentity.BonfireTileEntity;
import com.skullmangames.darksouls.core.util.StringHelper;

import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
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
	private int imageWidth = 129;
	private int imageHeight = 166;
	private Button reverseHollowingButton;
	private Button kindleButton;
	private BonfireTileEntity bonfiretileentity;
	private PlayerEntity playerEntity;
	private int buttonWidth = 100;
	private int buttonHeight = 20;
	private String estusVolumeLevel;
	private String estusHealLevel;
	private ImageButton estusHealIcon;
	private ImageButton estusVolumeIcon;
	
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
		
		estusVolumeLevel = String.valueOf(this.bonfiretileentity.getBlockState().getValue(BonfireBlock.ESTUS_VOLUME_LEVEL));
		estusHealLevel = String.valueOf(this.bonfiretileentity.getBlockState().getValue(BonfireBlock.ESTUS_HEAL_LEVEL));
		
		Button.ITooltip tooltip = (button, p_238659_2_, p_238659_3_, p_238659_4_) ->
		{
			String description = new TranslationTextComponent("gui.darksouls.estus_heal_level_tooltip").getString();
			StringTextComponent textcomponent = new StringTextComponent(description);
			
			this.renderTooltip(p_238659_2_, this.minecraft.font.split(textcomponent, Math.max(this.width / 2 - 43, 170)), p_238659_3_, p_238659_4_);
	    };
		this.estusHealIcon = this.addButton(new ImageButton(this.width / 2 - 20 - (16 / 2), this.height / 2 - 69 - (16 / 2), 16, 16, 0, 0, 0, new ResourceLocation(DarkSouls.MOD_ID, "textures/items/undead_bone_shard.png"), 16, 16, null, tooltip, null));
		this.estusHealIcon.active = false;
		
		tooltip = (button, p_238659_2_, p_238659_3_, p_238659_4_) ->
		{
			String description = new TranslationTextComponent("gui.darksouls.estus_volume_level_tooltip").getString();
			StringTextComponent textcomponent = new StringTextComponent(description);
			
			this.renderTooltip(p_238659_2_, this.minecraft.font.split(textcomponent, Math.max(this.width / 2 - 43, 170)), p_238659_3_, p_238659_4_);
	    };
		this.estusVolumeIcon = this.addButton(new ImageButton(this.width / 2 + 10 - (16 / 2), this.height / 2 - 68 - (16 / 2), 16, 16, 0, 0, 0, new ResourceLocation(DarkSouls.MOD_ID, "textures/items/estus_flask_full.png"), 16, 16, null, tooltip, null));
		this.estusVolumeIcon.active = false;
		
		tooltip = (button, p_238659_2_, p_238659_3_, p_238659_4_) ->
		{
			String description = new TranslationTextComponent("gui.darksouls.reverse_hollowing_tooltip").getString();
			String warning = "";
			if (!(MobNBTManager.getHumanity(this.playerEntity) > 0)) warning = new TranslationTextComponent("gui.darksouls.not_enough_humanity").getString();
			if (MobNBTManager.isHuman(playerEntity)) warning = new TranslationTextComponent("gui.darksouls.already_human").getString();
			StringTextComponent textcomponent = warning == "" ? new StringTextComponent(description) : new StringTextComponent(description + "\n\n" + "\u00A74" + warning);
			
			this.renderTooltip(p_238659_2_, this.minecraft.font.split(textcomponent, Math.max(this.width / 2 - 43, 170)), p_238659_3_, p_238659_4_);
	    };
		this.reverseHollowingButton = this.addButton(new Button(this.width / 2 - (this.buttonWidth / 2), this.height / 2, this.buttonWidth, this.buttonHeight, new TranslationTextComponent("gui.darksouls.reverse_hollowing_button"), (p_214187_1_) ->
		{
	         this.reverseHollowing();
	    }, tooltip));
		this.reverseHollowingButton.active = !MobNBTManager.isHuman(this.playerEntity) && MobNBTManager.getHumanity(this.playerEntity) > 0 ? true : false;
		
		tooltip = (button, p_238659_2_, p_238659_3_, p_238659_4_) ->
		{
			String description = new TranslationTextComponent("gui.darksouls.kindle_tooltip").getString();
			String warning = "";
			if (!(MobNBTManager.getHumanity(this.playerEntity) > 0)) warning = new TranslationTextComponent("gui.darksouls.not_enough_humanity").getString();
			if (!MobNBTManager.isHuman(this.playerEntity)) warning = new TranslationTextComponent("gui.darksouls.not_human").getString();
			if (this.bonfiretileentity.getBlockState().getValue(BonfireBlock.ESTUS_VOLUME_LEVEL) >= 2) warning = new TranslationTextComponent("gui.darksouls.cannot_kindle_further").getString();
			StringTextComponent textcomponent = warning == "" ? new StringTextComponent(description) : new StringTextComponent(description + "\n\n" + "\u00A74" + warning);
			
			this.renderTooltip(p_238659_2_, this.minecraft.font.split(textcomponent, Math.max(this.width / 2 - 43, 170)), p_238659_3_, p_238659_4_);
	    };
		this.kindleButton = this.addButton(new Button(this.width / 2 - (this.buttonWidth / 2), this.height / 2 + (1 * (this.buttonHeight + 5)), this.buttonWidth, this.buttonHeight, new TranslationTextComponent("gui.darksouls.kindle"), (p_214187_1_) ->
		{
	         this.kindle();
	    }, tooltip));
		this.kindleButton.active = MobNBTManager.isHuman(this.playerEntity) && MobNBTManager.getHumanity(this.playerEntity) > 0 && this.bonfiretileentity.getBlockState().getValue(BonfireBlock.ESTUS_VOLUME_LEVEL) < 2;
		this.addButton(new Button(this.width / 2 - (this.buttonWidth / 2), this.height / 2 + (2 * (this.buttonHeight + 5)), this.buttonWidth, this.buttonHeight, new TranslationTextComponent("gui.darksouls.leave_button"), (p_214187_1_) ->
		{
	         super.onClose();
	    }));
	}
	
	@Override
	public void render(MatrixStack matrixstack, int mouseX, int mouseY, float partialticks)
	{
		super.renderBackground(matrixstack);
		this.renderBg(matrixstack, partialticks, mouseX, mouseY);
		
		String name = this.bonfiretileentity.getName();
		String namepart1 = StringHelper.trySubstring(name, 0, 12);
		String namepart2 = name.length() >= 12 ? StringHelper.trySubstring(name, 12, 24) : "";
		String namepart3 = name.length() >= 24 ? StringHelper.trySubstring(name, 24, 36) : "";
	    drawCenteredString(matrixstack, this.font, namepart1, this.width / 2, this.height / 2 - 55, 16777215);
	    if (namepart2 != "") drawCenteredString(matrixstack, this.font, namepart2, this.width / 2, this.height / 2 - 45, 16777215);
	    if (namepart3 != "") drawCenteredString(matrixstack, this.font, namepart3, this.width / 2, this.height / 2 - 35, 16777215);
	    
	    drawCenteredString(matrixstack, this.font, estusVolumeLevel, this.width / 2 + 25, this.height / 2 - 70, 16777215);
	    drawCenteredString(matrixstack, this.font, estusHealLevel, this.width / 2 - 5, this.height / 2 - 70, 16777215);
	    
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
		MobNBTManager.shrinkHumanity(this.playerEntity, 1);
		MobNBTManager.setHuman(this.playerEntity, true);
		super.onClose();
	}
	
	protected void kindle()
	{
		MobNBTManager.shrinkHumanity(this.playerEntity, 1);
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
