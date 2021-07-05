package com.skullmangames.darksouls.client.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.skullmangames.darksouls.common.blocks.BonfireBlock;
import com.skullmangames.darksouls.common.entities.ModEntityDataManager;
import com.skullmangames.darksouls.common.tiles.BonfireTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;

public class BonfireScreen extends Screen
{
	protected Button reverseHollowingButton;
	protected Button kindleButton;
	protected Button leaveButton;
	private BonfireTileEntity bonfiretileentity;
	private PlayerEntity playerEntity;
	
	public BonfireScreen(BonfireTileEntity tileentity, PlayerEntity playerentity)
	{
		super(NarratorChatListener.NO_TITLE);
		this.bonfiretileentity = tileentity;
		this.playerEntity = playerentity;
	}
	
	@Override
	protected void init()
	{
		super.init();
		this.reverseHollowingButton = this.addButton(new Button(this.width / 2 - 75, this.height / 2 + 20, 150, 20, new TranslationTextComponent("gui.darksouls.reverse_hollowing_button"), (p_214187_1_) ->
		{
	         this.reverseHollowing();
	    }));
		this.reverseHollowingButton.active = !ModEntityDataManager.isHuman(this.playerEntity) ? true : false;
		this.kindleButton = this.addButton(new Button(this.width / 2 - 75, this.height / 2 + 40, 150, 20, new TranslationTextComponent("gui.darksouls.kindle"), (p_214187_1_) ->
		{
	         this.kindle();
	    }));
		this.kindleButton.active = this.bonfiretileentity.getBlockState().getValue(BonfireBlock.FIRE_LEVEL) < 2;
		this.leaveButton = this.addButton(new Button(this.width / 2 - 75, this.height / 2 + 60, 150, 20, new TranslationTextComponent("gui.darksouls.leave_button"), (p_214187_1_) ->
		{
	         this.onLeave();
	    }));
	}
	
	@Override
	public void resize(Minecraft minecraft, int p_231152_2_, int p_231152_3_)
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
	
	protected void reverseHollowing()
	{
		if (ModEntityDataManager.getHumanity(this.playerEntity) > 0)
		{
			ModEntityDataManager.shrinkHumanity(this.playerEntity, 1);
			ModEntityDataManager.setHuman(this.playerEntity, true);
		}
		else
		{
			this.playerEntity.sendMessage(new TranslationTextComponent("gui.darksouls.restore_humanity_fail"), Util.NIL_UUID);
		}
		this.onLeave();
	}
	
	protected void kindle()
	{
		if (ModEntityDataManager.getHumanity(this.playerEntity) > 0 && ModEntityDataManager.isHuman(this.playerEntity))
		{
			ModEntityDataManager.shrinkHumanity(this.playerEntity, 1);
			this.bonfiretileentity.kindle();
		}
		else if (ModEntityDataManager.isHuman(this.playerEntity))
		{
			this.playerEntity.sendMessage(new TranslationTextComponent("gui.darksouls.kindle_no_humanity"), Util.NIL_UUID);
		}
		else
		{
			this.playerEntity.sendMessage(new TranslationTextComponent("gui.darksouls.kindle_undead"), Util.NIL_UUID);
		}
		this.onLeave();
	}
	
	protected void onLeave()
	{
		this.minecraft.setScreen((Screen)null);
	}
}
