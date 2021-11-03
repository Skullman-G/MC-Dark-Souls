package com.skullmangames.darksouls.client.gui.widget;

import com.skullmangames.darksouls.config.Option;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RewindableButton extends OptionButton<Integer>
{
	protected final Button.IPressable onRewindPress;
	
	public RewindableButton(int x, int y, int width, int height, Option<Integer> option, IPressable pressedAction, IPressable rewindPressedAction)
	{
		super(x, y, width, height, option, pressedAction);
		this.onRewindPress = rewindPressedAction;
	}
	
	@Override
	protected ITextComponent getRefreshedMessage()
	{
		return new StringTextComponent(new TranslationTextComponent(this.option.getName()).getString()+": "+this.option.getValue());
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