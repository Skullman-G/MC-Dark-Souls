package com.skullmangames.darksouls.client.gui;

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

public class IngameConfigurationGui extends Screen {
	protected final Screen parentScreen;
	
	public IngameConfigurationGui(Minecraft mc, Screen screen) {
		super(new StringTextComponent(DarkSouls.MOD_ID + ".gui.configuration"));
		this.parentScreen = screen;
	}
	
	@Override
	protected void init() {
		Option<Boolean> showHealthIndicator = DarkSouls.CLIENT_INGAME_CONFIG.showHealthIndicator;
		Option<Boolean> showTargetIndicator = DarkSouls.CLIENT_INGAME_CONFIG.showTargetIndicator;
		Option<Boolean> filterAnimation = DarkSouls.CLIENT_INGAME_CONFIG.filterAnimation;
		Option<Integer> longPressCounter = DarkSouls.CLIENT_INGAME_CONFIG.longPressCount;
		
		Button longPressCounterButton = this.addButton(new RewindableButton(this.width / 2 - 100, this.height / 4 - 24, 200, 20,
			new TranslationTextComponent("gui."+DarkSouls.MOD_ID+".long_press_counter", (ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(longPressCounter.getValue()))),
			(button) -> {
				longPressCounter.setValue(longPressCounter.getValue() + 1);
				button.setMessage(new TranslationTextComponent("gui."+DarkSouls.MOD_ID+".long_press_counter", (ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(longPressCounter.getValue()))));
			},
			(button) -> {
				longPressCounter.setValue(longPressCounter.getValue() - 1);
				button.setMessage(new TranslationTextComponent("gui."+DarkSouls.MOD_ID+".long_press_counter", (ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(longPressCounter.getValue()))));
			}, (button, matrixStack, mouseX, mouseY) -> {
		        this.renderTooltip(matrixStack, this.minecraft.font.split(new TranslationTextComponent("gui.epicfight.long_press_counter.tooltip"), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
			}
		));
		
		Button filterAnimationButton = this.addButton(new Button(this.width / 2 - 100, this.height / 4, 200, 20,
			new TranslationTextComponent("gui."+DarkSouls.MOD_ID+".filter_animation." + (filterAnimation.getValue() ? "on" : "off")), (button) -> {
				filterAnimation.setValue(!filterAnimation.getValue());
				button.setMessage(new TranslationTextComponent("gui."+DarkSouls.MOD_ID+".filter_animation." + (filterAnimation.getValue() ? "on" : "off")));
			}, (button, matrixStack, mouseX, mouseY) -> {
		        this.renderTooltip(matrixStack, this.minecraft.font.split(new TranslationTextComponent("gui.epicfight.filter_animation.tooltip"), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
			}
		));
		
		Button showHealthIndicatorButton = this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 24, 200, 20,
			new TranslationTextComponent("gui."+DarkSouls.MOD_ID+".health_indicator." + (showHealthIndicator.getValue() ? "on" : "off")), (button) -> {
				showHealthIndicator.setValue(!showHealthIndicator.getValue());
				button.setMessage(new TranslationTextComponent("gui."+DarkSouls.MOD_ID+".health_indicator." + (showHealthIndicator.getValue() ? "on" : "off")));
			}, (button, matrixStack, mouseX, mouseY) -> {
		        this.renderTooltip(matrixStack, this.minecraft.font.split(new TranslationTextComponent("gui.epicfight.health_indicator.tooltip"), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
			}
		));
		
		Button showTargetIndicatorButton = this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 48, 200, 20,
				new TranslationTextComponent("gui."+DarkSouls.MOD_ID+".target_indicator." + (showTargetIndicator.getValue() ? "on" : "off")), (button) -> {
					showTargetIndicator.setValue(!showTargetIndicator.getValue());
					button.setMessage(new TranslationTextComponent("gui."+DarkSouls.MOD_ID+".target_indicator." + (showTargetIndicator.getValue() ? "on" : "off")));
				}, (button, matrixStack, mouseX, mouseY) -> {
			        this.renderTooltip(matrixStack, this.minecraft.font.split(new TranslationTextComponent("gui.epicfight.target_indicator.tooltip"), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
				}
			));
		
		this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 150, 96, 20, new TranslationTextComponent("gui.done"), (button) -> {
			this.onClose();
		}));
		
		this.addButton(new Button(this.width / 2 + 4, this.height / 4 + 150, 96, 20, new TranslationTextComponent("controls.reset"), (button) -> {
			DarkSouls.CLIENT_INGAME_CONFIG.resetSettings();
			filterAnimationButton.setMessage(new TranslationTextComponent("gui."+DarkSouls.MOD_ID+".filter_animation." + (filterAnimation.getValue() ? "on" : "off")));
			longPressCounterButton.setMessage(new TranslationTextComponent("gui."+DarkSouls.MOD_ID+".long_press_counter", (ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(longPressCounter.getValue()))));
			showHealthIndicatorButton.setMessage(new TranslationTextComponent("gui."+DarkSouls.MOD_ID+".health_indicator." + (showHealthIndicator.getValue() ? "on" : "off")));
			showTargetIndicatorButton.setMessage(new TranslationTextComponent("gui."+DarkSouls.MOD_ID+".target_indicator." + (showTargetIndicator.getValue() ? "on" : "off")));
		}));
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
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