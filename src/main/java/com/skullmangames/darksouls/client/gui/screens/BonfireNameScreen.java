package com.skullmangames.darksouls.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.skullmangames.darksouls.common.block.BonfireBlock;
import com.skullmangames.darksouls.common.tileentity.BonfireBlockEntity;
import com.skullmangames.darksouls.core.init.ModSoundEvents;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.client.CTSUpdateBonfireBlock;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BonfireNameScreen extends Screen
{
	protected EditBox titleEdit;
	protected Button doneButton;
	private final BonfireBlockEntity tileentity;
	
	public BonfireNameScreen(BonfireBlockEntity tileentity)
	{
		super(NarratorChatListener.NO_TITLE);
		this.tileentity = tileentity;
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
		this.doneButton = this.addRenderableWidget(new Button(this.width / 2 - 75, this.height / 2 + 20, 150, 20, CommonComponents.GUI_DONE, (p_214187_1_) ->
		{
	         this.onDone();
	    }));
		this.doneButton.active = false;
		this.titleEdit = new EditBox(this.font, this.width / 2 - 75, this.height / 2 - 20, 150, 20, new TranslatableComponent("advMode.command"))
		{};
		this.titleEdit.setMaxLength(37);
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
	public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks)
	{
		this.renderBackground(stack);
	    drawCenteredString(stack, this.font, new TranslatableComponent("gui.darksouls.name_bonfire"), this.width / 2, this.height / 2 - 40, 16777215);
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
	    BlockState state = this.tileentity.getBlockState();
	    if (!state.getValue(BonfireBlock.LIT))
	    {
	    	ModNetworkManager.connection.handleSetTitles(new TranslatableComponent("gui.darksouls.bonfire_lit_message"), 10, 50, 10);
	    	BlockPos pos = this.tileentity.getBlockPos();
	    	this.minecraft.player.level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), ModSoundEvents.GENERIC_HUMAN_FORM.get(), SoundSource.AMBIENT, 1.0F, 0.8F, false);
	    }
	    ModNetworkManager.sendToServer(new CTSUpdateBonfireBlock(this.titleEdit.getValue(), true, false, this.tileentity.getBlockPos()));
	    
	    super.onClose();
	}
}
