package com.skullmangames.darksouls.client.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.gui.widget.RewindableButton;
import com.skullmangames.darksouls.config.Option;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class IngameConfigurationScreen extends Screen
{
	protected final Screen parentScreen;
	
	public IngameConfigurationScreen(Minecraft mc, Screen screen)
	{
		super(new StringTextComponent(DarkSouls.MOD_ID + ".gui.configuration"));
		this.parentScreen = screen;
	}
	
	private String onOrOff(boolean value)
	{
		return ": " + new TranslationTextComponent(value ? "gui." + DarkSouls.MOD_ID + ".on" : "gui." + DarkSouls.MOD_ID + ".off").getString();
	}
	
	@Override
	protected void init()
	{
		Option<Boolean> showHealthIndicator = DarkSouls.CLIENT_INGAME_CONFIG.showHealthIndicator;
		Option<Boolean> showTargetIndicator = DarkSouls.CLIENT_INGAME_CONFIG.showTargetIndicator;
		Option<Boolean> filterAnimation = DarkSouls.CLIENT_INGAME_CONFIG.filterAnimation;
		Option<Integer> longPressCounter = DarkSouls.CLIENT_INGAME_CONFIG.longPressCount;
		
		Button longPressCounterButton = this.addButton(new RewindableButton(this.width / 2 - 100, this.height / 4 - 24, 200, 20,
			new StringTextComponent(new TranslationTextComponent("gui." + DarkSouls.MOD_ID + ".long_press_counter").getString() + ": " + longPressCounter.getValue()),
			(button) ->
			{
				longPressCounter.setValue(longPressCounter.getValue() + 1);
				button.setMessage(new StringTextComponent(new TranslationTextComponent("gui." + DarkSouls.MOD_ID + ".long_press_counter").getString() + ": " + longPressCounter.getValue()));
			},
			(button) ->
			{
				longPressCounter.setValue(longPressCounter.getValue() - 1);
				button.setMessage(new TranslationTextComponent("gui." + DarkSouls.MOD_ID + ".long_press_counter", (ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(longPressCounter.getValue()))));
			}, (button, matrixStack, mouseX, mouseY) ->
			{
		        this.renderTooltip(matrixStack, this.minecraft.font.split(new TranslationTextComponent("tooltip." + DarkSouls.MOD_ID + ".long_press_counter"), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
			}
		));
		
		Button filterAnimationButton = this.addButton(new Button(this.width / 2 - 100, this.height / 4, 200, 20,
			new StringTextComponent(new TranslationTextComponent("gui." + DarkSouls.MOD_ID + ".filter_animation").getString() + this.onOrOff(filterAnimation.getValue())), (button) ->
			{
				filterAnimation.setValue(!filterAnimation.getValue());
				button.setMessage(new StringTextComponent(new TranslationTextComponent("gui." + DarkSouls.MOD_ID + ".filter_animation").getString() + this.onOrOff(filterAnimation.getValue())));
			}, (button, matrixStack, mouseX, mouseY) ->
			{
		        this.renderTooltip(matrixStack, this.minecraft.font.split(new TranslationTextComponent("tooltip." + DarkSouls.MOD_ID + ".filter_animation"), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
			}
		));
		
		Button showHealthIndicatorButton = this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 24, 200, 20,
			new StringTextComponent(new TranslationTextComponent("gui." + DarkSouls.MOD_ID + ".health_indicator").getString() + this.onOrOff(showHealthIndicator.getValue())), (button) ->
			{
				showHealthIndicator.setValue(!showHealthIndicator.getValue());
				button.setMessage(new StringTextComponent(new TranslationTextComponent("gui." + DarkSouls.MOD_ID + ".health_indicator").getString() + this.onOrOff(showHealthIndicator.getValue())));
			}
		));
		
		Button showTargetIndicatorButton = this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 48, 200, 20,
				new StringTextComponent(new TranslationTextComponent("gui." + DarkSouls.MOD_ID + ".target_indicator").getString() + this.onOrOff(showTargetIndicator.getValue())), (button) ->
				{
					showTargetIndicator.setValue(!showTargetIndicator.getValue());
					button.setMessage(new StringTextComponent(new TranslationTextComponent("gui." + DarkSouls.MOD_ID + ".target_indicator").getString() + this.onOrOff(showTargetIndicator.getValue())));
				}
			));
		
		this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 150, 96, 20, new TranslationTextComponent("gui." + DarkSouls.MOD_ID + ".done"), (button) ->
		{
			this.onClose();
		}));
		
		this.addButton(new Button(this.width / 2 + 4, this.height / 4 + 150, 96, 20, new TranslationTextComponent("gui." + DarkSouls.MOD_ID + ".reset"), (button) ->
		{
			DarkSouls.CLIENT_INGAME_CONFIG.resetSettings();
			filterAnimationButton.setMessage(new StringTextComponent(new TranslationTextComponent("gui." + DarkSouls.MOD_ID + ".filter_animation").getString() + this.onOrOff(filterAnimation.getValue())));
			longPressCounterButton.setMessage(new StringTextComponent(new TranslationTextComponent("gui." + DarkSouls.MOD_ID + ".long_press_counter").getString() + ": " + longPressCounter.getValue()));
			showHealthIndicatorButton.setMessage(new StringTextComponent(new TranslationTextComponent("gui." + DarkSouls.MOD_ID + ".health_indicator").getString() + this.onOrOff(showHealthIndicator.getValue())));
			showTargetIndicatorButton.setMessage(new StringTextComponent(new TranslationTextComponent("gui." + DarkSouls.MOD_ID + ".target_indicator").getString() + this.onOrOff(showTargetIndicator.getValue())));
		}));
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
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