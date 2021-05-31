package com.skullmangames.darksouls.client.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.skullmangames.darksouls.common.tiles.BonfireTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;

public class BonfireScreen extends Screen
{
	protected Button warpButton;
	protected Button leaveButton;
	private BonfireTileEntity bonfiretileentity;
	
	public BonfireScreen(BonfireTileEntity tileentity)
	{
		super(NarratorChatListener.NO_TITLE);
		this.bonfiretileentity = tileentity;
	}
	
	@Override
	protected void init()
	{
		super.init();
		this.warpButton = this.addButton(new Button(this.width / 2 - 75, this.height / 2 + 20, 150, 20, new TranslationTextComponent("gui.darksouls.warp_button"), (p_214187_1_) ->
		{
	         this.openWarpMenu();
	    }));
		this.leaveButton = this.addButton(new Button(this.width / 2 - 75, this.height / 2 + 40, 150, 20, new TranslationTextComponent("gui.darksouls.leave_button"), (p_214187_1_) ->
		{
	         this.onLeave();
	    }));
	}
	
	@Override
	public void resize(Minecraft p_231152_1_, int p_231152_2_, int p_231152_3_)
	{
		this.init(minecraft, p_231152_2_, p_231152_3_);
	}
	
	@Override
	public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks)
	{
	    this.renderBackground(stack);
	    drawCenteredString(stack, this.font, this.bonfiretileentity.getName(), this.width / 2, this.height / 2 - 40, 16777215);
	    super.render(stack, mouseX, mouseY, partialTicks);
	}
	
	protected void openWarpMenu()
	{
		//open warp menu
	}
	
	protected void onLeave()
	{
		this.minecraft.setScreen((Screen)null);
	}
}
