package com.skullmangames.darksouls.client.gui.widget;

import com.skullmangames.darksouls.config.Option;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RewindableButton extends OptionButton<Integer>
{
	protected final Button.OnPress onRewindPress;
	
	public RewindableButton(int x, int y, int width, int height, Option<Integer> option, OnPress pressedAction, OnPress rewindPressedAction)
	{
		super(x, y, width, height, option, pressedAction);
		this.onRewindPress = rewindPressedAction;
	}
	
	@Override
	protected Component getRefreshedMessage()
	{
		return new TextComponent(new TranslatableComponent(this.option.getName()).getString()+": "+this.option.getValue());
	}
	
	@Override
	protected boolean isValidClickButton(int button)
	{
		return button == 0 || button == 1;
	}
	
	public void onClick(double mouseX, double mouseY, int button)
	{
		if (button == 0)
		{
			super.onClick(mouseX, mouseY);
		}
		else
		{
			this.onRewindPress.onPress(this);
		}
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		if (this.active && this.visible)
		{
			if (this.isValidClickButton(button))
			{
				boolean flag = this.clicked(mouseX, mouseY);
				if (flag)
				{
					this.playDownSound(Minecraft.getInstance().getSoundManager());
					this.onClick(mouseX, mouseY, button);
					return true;
				}
			}
			return false;
		}
		else
		{
			return false;
		}
	}
}