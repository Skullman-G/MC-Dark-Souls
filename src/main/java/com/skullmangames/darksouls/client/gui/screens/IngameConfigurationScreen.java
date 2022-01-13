package com.skullmangames.darksouls.client.gui.screens;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.gui.widget.BooleanButton;
import com.skullmangames.darksouls.client.gui.widget.OptionButton;
import com.skullmangames.darksouls.client.gui.widget.RewindableButton;
import com.skullmangames.darksouls.config.Option;
import com.skullmangames.darksouls.config.Option.BooleanOption;
import com.skullmangames.darksouls.config.Option.IntegerOption;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class IngameConfigurationScreen extends Screen
{
	protected final Screen parentScreen;
	
	public IngameConfigurationScreen(Minecraft mc, Screen screen)
	{
		super(new TextComponent(DarkSouls.MOD_ID + ".gui.configuration"));
		this.parentScreen = screen;
	}
	
	@Override
	protected void init()
	{
		List<OptionButton<?>> buttons = new ArrayList<OptionButton<?>>();
		
		int yDistance = -24;
		
		for (Option<?> option : DarkSouls.CLIENT_INGAME_CONFIG.OPTIONS)
		{
			if (option instanceof BooleanOption)
			{
				BooleanOption booleanOption = (BooleanOption)option;
				buttons.add(this.addRenderableWidget(new BooleanButton(this.width / 2 - 100, this.height / 4 + yDistance, 200, 20, booleanOption)));
			}
			else if (option instanceof IntegerOption)
			{
				IntegerOption intOption = (IntegerOption)option;
				buttons.add(this.addRenderableWidget(new RewindableButton(this.width / 2 - 100, this.height / 4 + yDistance, 200, 20,	intOption,
						(button) ->
						{
							intOption.setValue(intOption.getValue() + 1);
							((RewindableButton)button).refreshMessage();
						},
						(button) ->
						{
							intOption.setValue(intOption.getValue() - 1);
							((RewindableButton)button).refreshMessage();
						}
					)));
			}
			
			yDistance += 24;
		}
		
		this.addRenderableWidget(new Button(this.width / 2 - 100, this.height / 4 + 150, 96, 20, new TranslatableComponent("gui."+DarkSouls.MOD_ID+".done"), (button) ->
		{
			this.onClose();
		}));
		
		this.addRenderableWidget(new Button(this.width / 2 + 4, this.height / 4 + 150, 96, 20, new TranslatableComponent("gui."+DarkSouls.MOD_ID+".reset"), (button) ->
		{
			DarkSouls.CLIENT_INGAME_CONFIG.resetSettings();
			for (OptionButton<?> b : buttons)
			{
				b.refreshMessage();
			}
		}));
	}
	
	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		this.renderDirtBackground(0);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void onClose()
	{
		DarkSouls.CLIENT_INGAME_CONFIG.save();
		this.minecraft.setScreen(this.parentScreen);
	}
}