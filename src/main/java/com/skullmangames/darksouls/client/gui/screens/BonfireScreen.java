package com.skullmangames.darksouls.client.gui.screens;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.common.block.BonfireBlock;
import com.skullmangames.darksouls.common.blockentity.BonfireBlockEntity;
import com.skullmangames.darksouls.common.capability.entity.LocalPlayerCap;
import com.skullmangames.darksouls.core.util.StringHelper;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.client.CTSBonfireTask;
import com.skullmangames.darksouls.network.client.CTSOpenAttunementScreen;

import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BonfireScreen extends Screen
{
	public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/bonfire_main.png");
	public static final ResourceLocation DS_TEXTURE_LOCATION = new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/ds_bonfire_main.png");
	
	private int imageWidth = 129;
	private int imageHeight = 186;
	private Button reverseHollowingButton;
	private Button kindleButton;
	private BonfireBlockEntity bonfiretileentity;
	private LocalPlayerCap playerCap;
	private int buttonWidth = 100;
	private int buttonHeight = 20;
	private String estusVolumeLevel;
	private String estusHealLevel;
	private ImageButton estusHealIcon;
	private ImageButton estusVolumeIcon;
	private String[] nameparts = new String[3];
	
	public BonfireScreen(BonfireBlockEntity tileentity)
	{
		super(NarratorChatListener.NO_TITLE);
		this.bonfiretileentity = tileentity;
		this.playerCap = ClientManager.INSTANCE.getPlayerCap();
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
		
		int yPos = this.height / 2 - 10;
		String name = this.bonfiretileentity.getName();
		nameparts[0] = StringHelper.trySubstring(name, 0, 12);
		nameparts[1] = name.length() >= 12 ? StringHelper.trySubstring(name, 12, 24) : "";
		nameparts[2] = name.length() >= 24 ? StringHelper.trySubstring(name, 24, 36) : "";
		if (nameparts[1].isBlank())
		{
			nameparts[1] = nameparts[0];
			nameparts[0] = "";
		}
		
		estusVolumeLevel = String.valueOf(this.bonfiretileentity.getBlockState().getValue(BonfireBlock.ESTUS_VOLUME_LEVEL));
		estusHealLevel = String.valueOf(this.bonfiretileentity.getBlockState().getValue(BonfireBlock.ESTUS_HEAL_LEVEL));
		
		Button.OnTooltip tooltip = (button, p_238659_2_, p_238659_3_, p_238659_4_) ->
		{
			String description = new TranslatableComponent("gui.darksouls.estus_heal_level_tooltip").getString();
			TextComponent textcomponent = new TextComponent(description);
			
			this.renderTooltip(p_238659_2_, this.minecraft.font.split(textcomponent, Math.max(this.width / 2 - 43, 170)), p_238659_3_, p_238659_4_);
	    };
		this.estusHealIcon = this.addRenderableWidget(new ImageButton(this.width / 2 - 20 - (16 / 2), this.height / 2 - 79 - (16 / 2), 16, 16, 0, 0, 0, new ResourceLocation(DarkSouls.MOD_ID, "textures/items/undead_bone_shard.png"), 16, 16, null, tooltip, null));
		this.estusHealIcon.active = false;
		
		tooltip = (button, p_238659_2_, p_238659_3_, p_238659_4_) ->
		{
			String description = new TranslatableComponent("gui.darksouls.estus_volume_level_tooltip").getString();
			TextComponent textcomponent = new TextComponent(description);
			
			this.renderTooltip(p_238659_2_, this.minecraft.font.split(textcomponent, Math.max(this.width / 2 - 43, 170)), p_238659_3_, p_238659_4_);
	    };
		this.estusVolumeIcon = this.addRenderableWidget(new ImageButton(this.width / 2 + 10 - (16 / 2), this.height / 2 - 78 - (16 / 2), 16, 16, 0, 0, 0, new ResourceLocation(DarkSouls.MOD_ID, "textures/items/estus_flask_full.png"), 16, 16, null, tooltip, null));
		this.estusVolumeIcon.active = false;
		
		tooltip = (button, p_238659_2_, p_238659_3_, p_238659_4_) ->
		{
			String description = new TranslatableComponent("gui.darksouls.reverse_hollowing_tooltip").getString();
			String warning = "";
			if (!this.playerCap.hasEnoughHumanity(1)) warning = new TranslatableComponent("gui.darksouls.not_enough_humanity").getString();
			if (this.playerCap.isHuman()) warning = new TranslatableComponent("gui.darksouls.already_human").getString();
			TextComponent textcomponent = warning == "" ? new TextComponent(description) : new TextComponent(description + "\n\n" + "\u00A74" + warning);
			
			this.renderTooltip(p_238659_2_, this.minecraft.font.split(textcomponent, Math.max(this.width / 2 - 43, 170)), p_238659_3_, p_238659_4_);
	    };
		this.reverseHollowingButton = this.addRenderableWidget(new Button(this.width / 2 - (this.buttonWidth / 2), yPos, this.buttonWidth, this.buttonHeight, new TranslatableComponent("gui.darksouls.reverse_hollowing_button"), (p_214187_1_) ->
		{
	         this.reverseHollowing();
	    }, tooltip));
		this.reverseHollowingButton.active = !this.playerCap.isHuman() && this.playerCap.hasEnoughHumanity(1) ? true : false;
		
		tooltip = (button, p_238659_2_, p_238659_3_, p_238659_4_) ->
		{
			String description = new TranslatableComponent("gui.darksouls.kindle_tooltip").getString();
			String warning = "";
			if (!this.playerCap.hasEnoughHumanity(1)) warning = new TranslatableComponent("gui.darksouls.not_enough_humanity").getString();
			if (!this.playerCap.isHuman()) warning = new TranslatableComponent("gui.darksouls.not_human").getString();
			if (!this.bonfiretileentity.canKindle()) warning = new TranslatableComponent("gui.darksouls.cannot_kindle_further").getString();
			TextComponent textcomponent = warning == "" ? new TextComponent(description) : new TextComponent(description + "\n\n" + "\u00A74" + warning);
			
			this.renderTooltip(p_238659_2_, this.minecraft.font.split(textcomponent, Math.max(this.width / 2 - 43, 170)), p_238659_3_, p_238659_4_);
	    };
		this.kindleButton = this.addRenderableWidget(new Button(this.width / 2 - (this.buttonWidth / 2), yPos + (1 * (this.buttonHeight + 5)), this.buttonWidth, this.buttonHeight, new TranslatableComponent("gui.darksouls.kindle"), (button) ->
		{
	         this.kindle();
	    }, tooltip));
		this.kindleButton.active = this.playerCap.isHuman() && this.playerCap.hasEnoughHumanity(1) && this.bonfiretileentity.canKindle();
		this.addRenderableWidget(new Button(this.width / 2 - (this.buttonWidth / 2), yPos + (2 * (this.buttonHeight + 5)), this.buttonWidth, this.buttonHeight, new TranslatableComponent("gui.darksouls.attunements"), (button) ->
		{
	         ModNetworkManager.sendToServer(new CTSOpenAttunementScreen());
	    }));
		this.addRenderableWidget(new Button(this.width / 2 - (this.buttonWidth / 2), yPos + (3 * (this.buttonHeight + 5)), this.buttonWidth, this.buttonHeight, new TranslatableComponent("gui.darksouls.leave_button"), (button) ->
		{
	         super.onClose();
	    }));
	}
	
	@Override
	public void render(PoseStack matrixstack, int mouseX, int mouseY, float partialticks)
	{
		super.renderBackground(matrixstack);
		this.renderBg(matrixstack, partialticks, mouseX, mouseY);
		
		
	    drawCenteredString(matrixstack, this.font, this.nameparts[0], this.width / 2, this.height / 2 - 65, 16777215);
	    drawCenteredString(matrixstack, this.font, this.nameparts[1], this.width / 2, this.height / 2 - 55, 16777215);
	    drawCenteredString(matrixstack, this.font, this.nameparts[2], this.width / 2, this.height / 2 - 45, 16777215);
	    
	    drawCenteredString(matrixstack, this.font, estusVolumeLevel, this.width / 2 + 25, this.height / 2 - 80, 16777215);
	    drawCenteredString(matrixstack, this.font, estusHealLevel, this.width / 2 - 5, this.height / 2 - 80, 16777215);
	    
	    super.render(matrixstack, mouseX, mouseY, partialticks);
	}
	
	private void renderBg(PoseStack matrixstack, float partialticks, int mouseX, int mouseY)
	{
		if (DarkSouls.CLIENT_INGAME_CONFIG.darkSoulsUI.getValue()) RenderSystem.setShaderTexture(0, DS_TEXTURE_LOCATION);
		else RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
		int x = (this.width - this.imageWidth) / 2;
	    int y = (this.height - this.imageHeight) / 2;
	    this.blit(matrixstack, x, y, 0, 0, this.imageWidth, this.imageHeight);
	}
	
	protected void reverseHollowing()
	{
		ModNetworkManager.sendToServer(new CTSBonfireTask(CTSBonfireTask.Task.REVERSE_HOLLOWING, this.bonfiretileentity.getBlockPos(), ""));
		super.onClose();
	}
	
	protected void kindle()
	{
		ModNetworkManager.sendToServer(new CTSBonfireTask(CTSBonfireTask.Task.KINDLE, this.bonfiretileentity.getBlockPos(), ""));
		super.onClose();
	}
	
	@Override
	public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_)
	{
		InputConstants.Key mouseKey = InputConstants.getKey(p_231046_1_, p_231046_2_);
		if (super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_)) return true;
		else if (this.minecraft.options.keyInventory.isActiveAndMatches(mouseKey)) 
		{
	         this.onClose();
	         return true;
		}
		else return false;
	}
}
