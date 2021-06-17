package com.skullmangames.darksouls.client.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.skullmangames.darksouls.common.tiles.BonfireTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

public class BonfireNameScreen extends Screen
{
	protected TextFieldWidget titleEdit;
	protected Button doneButton;
	private final BonfireTileEntity bonfiretileentity;
	private final PlayerEntity player;
	
	public BonfireNameScreen(PlayerEntity player, BonfireTileEntity tileentity)
	{
		super(NarratorChatListener.NO_TITLE);
		this.bonfiretileentity = tileentity;
		this.player = player;
	}
	
	@Override
	protected void init()
	{
		super.init();
		this.doneButton = this.addButton(new Button(this.width / 2 - 75, this.height / 2 + 20, 150, 20, DialogTexts.GUI_DONE, (p_214187_1_) ->
		{
	         this.onDone();
	    }));
		this.doneButton.active = false;
		this.titleEdit = new TextFieldWidget(this.font, this.width / 2 - 75, this.height / 2 - 20, 150, 20, new TranslationTextComponent("advMode.command"))
		{};
		this.titleEdit.setMaxLength(100);
		this.setInitialFocus(this.titleEdit);
	    this.titleEdit.setFocus(true);
	}
	
	@Override
	public void resize(Minecraft minecraft, int p_231152_2_, int p_231152_3_)
	{
		String s = this.titleEdit.getValue();
		this.init(minecraft, p_231152_2_, p_231152_3_);
		this.titleEdit.setValue(s);
	}
	
	@Override
	public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks)
	{
	    this.renderBackground(stack);
	    drawCenteredString(stack, this.font, new TranslationTextComponent("gui.darksouls.name_bonfire"), this.width / 2, this.height / 2 - 40, 16777215);
	    this.titleEdit.render(stack, mouseX, mouseY, partialTicks);
	    super.render(stack, mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void tick()
	{
		this.titleEdit.tick();
		
		if (this.titleEdit.getValue().isEmpty() && this.doneButton.active)
		{
			this.doneButton.active = false;
		}
		else if (!this.titleEdit.getValue().isEmpty() && !this.doneButton.active)
		{
			this.doneButton.active = true;
		}
	}
	
	protected void onDone()
	{
	    this.minecraft.setScreen((Screen)null);
	    this.bonfiretileentity.setName(this.titleEdit.getValue());
	    this.bonfiretileentity.setLit(this.player, true);
	}
}
